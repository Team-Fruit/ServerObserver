package net.teamfruit.serverobserver;

import java.awt.Color;
import java.io.File;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.math.NumberUtils;
import org.lwjgl.util.Timer;

import com.google.common.collect.Sets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent;
import net.minecraftforge.client.event.GuiScreenEvent.DrawScreenEvent;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.teamfruit.serverobserver.ConfigBase.ConfigProperty;
import net.teamfruit.serverobserver.DefaultServerList.ServerListModel;
import net.teamfruit.serverobserver.DefaultServerList.ServerModel;

public class GuiHandler {
	private static final int BUTTON_ID = 31058;
	private static final int DISABLE_BACK_BUTTON_ID = 31059;

	private final @Nonnull ICompat compat;

	private GuiButton disableBackButton;
	private @Nullable GuiButton mainMenuButtonMulti;

	public GuiHandler(final ICompat compat) {
		this.compat = compat;
	}

	public interface SkeletonButtonDrawInside {
		void drawInside(GuiButton button, final Minecraft mc, final int mouseX, final int mouseY, final int x, final int y);
	}

	private boolean isFirstOpen = true;

	@CoreEvent
	public void open(final InitGuiEvent.Post e) {
		this.manualOpen = this.manual;
		Timer.tick();
		this.targetServerStatus = null;
		final GuiScreen screen = this.mc.currentScreen;
		final List<GuiButton> buttons = this.compat.getButtonList(e);
		// Log.log.info(String.format("opened: %s, buttons: %s", screen, buttons));
		if (screen instanceof GuiMultiplayer) {
			final GuiMultiplayer mpgui = (GuiMultiplayer) screen;
			buttons.add(this.compat.createSkeletonButton(BUTTON_ID, mpgui.width-(5+180), 5, 180, 23, I18n.format("serverobserver.gui.mode"),
					(
							button, mc, mouseX, mouseY, x, y
					) -> {
						final ServerData serverData = GuiHandler.this.target.get(mpgui);
						final FontRenderer font = GuiHandler.this.compat.font(mc);
						GuiHandler.this.displayText = serverData!=null ? GuiHandler.this.autologin.is() ? "serverobserver.gui.mode.1" : "serverobserver.gui.mode.2" : "serverobserver.gui.mode.3";
						mpgui.drawString(font, I18n.format(GuiHandler.this.displayText, GuiHandler.this.displayTime), x+4, y+3, Color.WHITE.getRGB());
						if (serverData!=null) {
							final String text = font.trimStringToWidth(serverData.serverName, button.width-(4*2+font.getStringWidth("...")));
							mpgui.drawString(font, text, x+4, y+12, Color.GRAY.getRGB());
							if (!StringUtils.equals(serverData.serverName, text))
								mpgui.drawString(font, "...", x+4+font.getStringWidth(text), y+12, Color.GRAY.getRGB());
						}
					}));
			selectTarget(mpgui, this.target.getServerIP());
			reset(Config.getConfig().durationPing);
		} else if (screen instanceof GuiDisconnected) {
			final GuiDisconnected dcgui = (GuiDisconnected) screen;
			if (this.target.getServerIP()!=null)
				buttons.add(
						this.disableBackButton = new GuiButton(DISABLE_BACK_BUTTON_ID,
								dcgui.width/2-100, this.compat.getHeight(dcgui),
								I18n.format("serverobserver.gui.backandstop", 0f)));
			reset(Config.getConfig().durationDisconnected);
		} else if (screen instanceof GuiMainMenu) {
			if (this.isFirstOpen)
				if (Config.getConfig().startAndConnect.get()) {
					final ServerData server = this.target.get(null);
					if (server!=null)
						this.compat.connectToServer(new GuiMultiplayer(screen), server);
				} else if (Config.getConfig().startWithMultiplayerMenu.get())
					this.mc.displayGuiScreen(new GuiMultiplayer(screen));
			for (final GuiButton button : buttons)
				if (button.id==2)
					this.mainMenuButtonMulti = button;
			if (Config.getConfig().durationMainMenu.get()>0)
				reset(Config.getConfig().durationMainMenu);
		}
		this.manual = false;
		this.isFirstOpen = false;
	}

	@CoreEvent
	public void draw(final DrawScreenEvent.Post e) {
		final GuiScreen gui = this.mc.currentScreen;
		if (gui instanceof GuiMultiplayer) {
		} else if (gui instanceof GuiDisconnected) {
			if (this.disableBackButton!=null&&Config.getConfig().durationDisconnected.get()>=10)
				this.disableBackButton.displayString = I18n.format("serverobserver.gui.backandstop.time", I18n.format("serverobserver.gui.backandstop"), timeremain());
		} else if (gui instanceof GuiMainMenu) {
			final GuiButton button = this.mainMenuButtonMulti;
			if (button!=null&&this.target.getServerIP()!=null&&!this.manualOpen)
				button.displayString = I18n.format("serverobserver.gui.maintomulti.time", I18n.format("menu.multiplayer"), timeremain());
		}
	}

	private String displayText = "Disabled";
	private String displayTime = "";
	private Boolean targetServerStatus;
	private TargetServer target = new TargetServer();
	private AutoLoginMode autologin = new AutoLoginMode();

	@CoreEvent
	public void init(final File root) {
		final ConfigProperty<Boolean> initServer = Config.getConfig().miscInitServer;
		if (initServer.get()) {
			initServer.set(false);

			final File serversFile = new File(root, "servers.json");
			final ServerListModel model = new DefaultServerList().loadModel(serversFile);
			if (model!=null) {
				final ServerList list = new ServerList(this.mc);
				list.loadServerList();
				final List<ServerModel> servers = model.servers;
				if (servers!=null) {
					final Set<String> ipexists = Sets.newHashSet();
					final int countserver = list.countServers();
					for (int i = 0; i<countserver; i++) {
						final ServerData data = list.getServerData(i);
						if (data!=null)
							ipexists.add(data.serverIP);
					}
					boolean dirty = false;
					for (final ServerModel server : servers) {
						final String serverIP = server.serverIP;
						if (!ipexists.contains(serverIP)) {
							list.addServerData(new ServerData(server.serverName, serverIP, false));
							dirty = true;
						}
					}
					if (dirty)
						list.saveServerList();
				}
			}
		}
	}

	@CoreEvent
	public void action(final ActionPerformedEvent.Pre e) {
		this.manual = true;
	}

	@CoreEvent
	public void action(final ActionPerformedEvent.Post e) {
		this.targetServerStatus = null;
		final GuiScreen screen = this.mc.currentScreen;
		final int id = this.compat.getButton(e).id;
		if (screen instanceof GuiMultiplayer&&id==BUTTON_ID) {
			final GuiMultiplayer mpgui = (GuiMultiplayer) screen;
			final ServerList serverList = this.compat.getServerList(mpgui);
			final int serverindex = this.compat.getSelected(mpgui);
			ServerData server = null;
			if (0<=serverindex&&serverindex<serverList.countServers())
				server = serverList.getServerData(serverindex);
			if (server!=null) {
				if (!StringUtils.equals(this.target.getServerIP(), server.serverIP)) {
					this.autologin.set(false);
					this.target.set(server);
				} else if (!this.autologin.is()) {
					this.autologin.set(true);
					this.target.set(server);
				} else {
					this.autologin.set(false);
					this.target.set(null);
				}
			} else
				selectTarget(mpgui, this.target.getServerIP());
		} else if (screen instanceof GuiDisconnected&&id==DISABLE_BACK_BUTTON_ID) {
			final GuiDisconnected dcgui = (GuiDisconnected) screen;
			this.autologin.set(false);
			this.target.set(null);
			this.mc.displayGuiScreen(this.compat.getParentScreen(dcgui));
		}
		this.displayTime = "";
		reset(Config.getConfig().durationPing);
		this.manual = false;
	}

	public class TargetServer {
		private ServerData target;

		public void set(final ServerData serverData) {
			if (serverData==null) {
				setServer(null, null);
				this.target = null;
			} else {
				setServer(serverData.serverName, serverData.serverIP);
				this.target = serverData;
			}
		}

		public @Nullable ServerData get(final GuiMultiplayer mpgui) {
			final String targetServerIP = getServerIP();
			if (targetServerIP==null)
				return null;
			if (this.target==null||!StringUtils.equals(this.target.serverIP, targetServerIP))
				this.target = getServerData(mpgui, getServerName(), targetServerIP, true);
			return this.target;
		}

		public void setServer(final String name, final String ip) {
			Config.getConfig().targetServerName.set(StringUtils.defaultString(name));
			Config.getConfig().targetServerIP.set(StringUtils.defaultString(ip));
		}

		public String getServerName() {
			final String name = Config.getConfig().targetServerName.get();
			if (!StringUtils.isEmpty(name))
				return name;
			return null;
		}

		public String getServerIP() {
			final String ip = Config.getConfig().targetServerIP.get();
			if (!StringUtils.isEmpty(ip))
				return ip;
			return null;
		}
	}

	public class AutoLoginMode {
		public void set(final boolean enabled) {
			Config.getConfig().targetAutoLogin.set(enabled);
			Config.getConfig().getBase().save();
		}

		public boolean is() {
			return Config.getConfig().targetAutoLogin.get();
		}
	}

	private final Minecraft mc = Minecraft.getMinecraft();
	private Timer timer = new Timer();
	private boolean manual;
	private boolean manualOpen;

	@CoreEvent
	public void tickclient() {
		Timer.tick();
		final GuiScreen screen = this.mc.currentScreen;

		if (screen instanceof GuiMultiplayer) {
			final GuiMultiplayer mpgui = (GuiMultiplayer) screen;
			final ServerData serverData = this.target.get(mpgui);
			if (serverData!=null) {
				final Boolean before = this.targetServerStatus;
				this.targetServerStatus = this.compat.getPinged(serverData)&&serverData.pingToServer>=0;
				// Log.log.info("pinged: {}, pingms: {}", serverData.pinged, serverData.pingToServer);
				if (this.targetServerStatus)
					if (before!=null&&!before) {
						this.compat.playSound(this.mc, new ResourceLocation(Config.getConfig().notificationSound.get()), (float) (double) Config.getConfig().notificationPitch.get());
						reset(Config.getConfig().durationAutoLogin);
					}

				if (!this.targetServerStatus)
					this.displayTime = I18n.format("serverobserver.gui.nextping", timeremain());
				else if (this.autologin.is())
					this.displayTime = I18n.format("serverobserver.gui.nextconnect", timeremain());
				else
					this.displayTime = "";
				if (this.timer.getTime()>0) {
					if (this.targetServerStatus) {
						if (this.autologin.is())
							this.compat.connectToServer(mpgui, serverData);
					} else
						this.compat.setPinged(serverData, false);
					reset(Config.getConfig().durationPing);
				}
			}
		} else if (screen instanceof GuiDisconnected) {
			final GuiDisconnected dcgui = (GuiDisconnected) screen;
			if (this.timer.getTime()>0) {
				GuiScreen screen2 = this.compat.getParentScreen(dcgui);
				if (!(screen2 instanceof GuiMultiplayer))
					screen2 = new GuiMultiplayer(screen2);
				this.mc.displayGuiScreen(screen2);
				final GuiMultiplayer mpgui = (GuiMultiplayer) screen2;
				final ServerData serverData = this.target.get(mpgui);
				if (serverData!=null)
					this.compat.setPinged(serverData, false);
			}
		} else if (screen instanceof GuiMainMenu)
			if (this.target.getServerIP()!=null&&!this.manualOpen&&Config.getConfig().durationDisconnected.get()>=10)
				if (this.timer.getTime()>0)
					this.mc.displayGuiScreen(new GuiMultiplayer(screen));
	}

	private void reset(final ConfigProperty<Integer> time) {
		int duration = time.get();
		final String minstr = time.getProperty().getMinValue();
		if (minstr!=null)
			duration = Math.max(duration, NumberUtils.toInt(minstr));
		// Log.log.info("{}: {}", time.property.getName(), duration);
		this.timer.set(-duration);
	}

	private int timeremain() {
		return (int) Math.ceil(-this.timer.getTime());
	}

	private void selectTarget(final GuiMultiplayer mpgui, final String serverIP) {
		final int target = getTarget(mpgui, serverIP);
		if (target>=0)
			this.compat.selectServer(mpgui, target);
	}

	private int getTarget(final GuiMultiplayer mpgui, final String serverIP) {
		final ServerList serverList = this.compat.getServerList(mpgui);
		final int countserver = serverList.countServers();
		for (int i = 0; i<countserver; i++) {
			final ServerData serverData = serverList.getServerData(i);
			if (StringUtils.equals(serverIP, serverData.serverIP))
				return i;
		}
		return -1;
	}

	private ServerData getServerData(final GuiMultiplayer mpgui, String name, final @Nonnull String ip, final boolean doPing) {
		Validate.notNull(ip, "Internal Error: serverIP is null");
		ServerList serverList = null;
		if (mpgui!=null)
			serverList = this.compat.getServerList(mpgui);
		if (serverList!=null) {
			ServerData server = null;
			final int serverIndex = getTarget(mpgui, ip);
			if (serverIndex>=0)
				server = serverList.getServerData(serverIndex);
			if (server!=null)
				return server;
		}
		if (name==null)
			name = ip;
		final ServerData server = new ServerData(name, ip, false);
		if (doPing)
			ping(mpgui, server);
		return server;
	}

	private void ping(final GuiMultiplayer mpgui, final ServerData serverData) {
		if (!this.compat.getPinged(serverData)) {
			this.compat.setPinged(serverData, true);
			serverData.pingToServer = -2L;
			serverData.serverMOTD = "";
			serverData.populationInfo = "";
			this.compat.getThreadPool().submit(() -> {
				try {
					this.compat.ping(mpgui, serverData);
				} catch (final UnknownHostException unknownhostexception) {
					serverData.pingToServer = -1L;
					serverData.serverMOTD = "\u00a74Can\'t resolve hostname";
				} catch (final Exception exception) {
					serverData.pingToServer = -1L;
					serverData.serverMOTD = "\u00a74Can\'t connect to server.";
				}
			});
		}
	}
}