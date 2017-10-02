package net.teamfruit.serverobserver;

import java.awt.Color;
import java.util.List;

import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.lwjgl.util.Timer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiListExtended.IGuiListEntry;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ServerListEntryNormal;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent;
import net.minecraftforge.client.event.GuiScreenEvent.DrawScreenEvent;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.teamfruit.serverobserver.ConfigBase.ConfigProperty;

public class GuiHandler {
	private static final int BUTTON_ID = 31058;
	private static final int DISABLE_BACK_BUTTON_ID = 31059;

	private GuiButton disableBackButton;
	private @Nullable GuiButton mainMenuMultiPlayer;

	public static class SkeletonButton extends GuiButton {
		public SkeletonButton(final int buttonId, final int x, final int y, final int widthIn, final int heightIn, final String buttonText) {
			super(buttonId, x, y, widthIn, heightIn, buttonText);
		}

		public SkeletonButton(final int buttonId, final int x, final int y, final String buttonText) {
			super(buttonId, x, y, buttonText);
		}

		protected boolean isHovered;

		@Override
		public void drawButton(final Minecraft mc, final int mouseX, final int mouseY) {
			if (this.visible) {
				Compat.color(1.0F, 1.0F, 1.0F, 1.0F);
				this.isHovered = mouseX>=this.xPosition&&mouseY>=this.yPosition&&mouseX<this.xPosition+this.width&&mouseY<this.yPosition+this.height;
				mouseDragged(mc, mouseX, mouseY);
				drawRect(this.xPosition, this.yPosition, this.xPosition+this.width, this.yPosition+this.height, 0xcc000000);
				drawInside(mc, mouseX, mouseY);
			}
		}

		protected void drawInside(final Minecraft mc, final int mouseX, final int mouseY) {
		}
	}

	@CoreEvent
	public void open(final InitGuiEvent.Post e) {
		Timer.tick();
		this.targetServerStatus = null;
		final GuiScreen screen = this.mc.currentScreen;
		this.mainMenuMultiPlayer = null;
		final List<GuiButton> buttons = Compat.getButtonList(e);
		if (screen instanceof GuiMultiplayer) {
			final GuiMultiplayer mpgui = (GuiMultiplayer) screen;
			buttons.add(new SkeletonButton(BUTTON_ID, mpgui.width-(5+180), 5, 180, 23, I18n.format("serverobserver.gui.mode")) {
				@Override
				protected void drawInside(final Minecraft mc, final int mouseX, final int mouseY) {
					final ServerData serverData = GuiHandler.this.target.get(mpgui);
					final FontRenderer font = Compat.font(mc);
					GuiHandler.this.displayText = serverData!=null ? GuiHandler.this.autologin.is() ? "serverobserver.gui.mode.1" : "serverobserver.gui.mode.2" : "serverobserver.gui.mode.3";
					mpgui.drawString(font, I18n.format(GuiHandler.this.displayText, GuiHandler.this.displayTime), this.xPosition+4, this.yPosition+3, Color.WHITE.getRGB());
					if (serverData!=null) {
						final String text = font.trimStringToWidth(serverData.serverName, this.width-(4*2+font.getStringWidth("...")));
						mpgui.drawString(font, text, this.xPosition+4, this.yPosition+12, Color.GRAY.getRGB());
						if (!StringUtils.equals(serverData.serverName, text))
							mpgui.drawString(font, "...", this.xPosition+4+font.getStringWidth(text), this.yPosition+12, Color.GRAY.getRGB());
					}
				}
			});
			selectTarget(mpgui, this.target.getIP());
			reset(Config.getConfig().durationPing);
		} else if (screen instanceof GuiDisconnected) {
			final GuiDisconnected dcgui = (GuiDisconnected) screen;
			if (this.target.getIP()!=null)
				buttons.add(
						this.disableBackButton = new GuiButton(DISABLE_BACK_BUTTON_ID,
								dcgui.width/2-100, Compat.getHeight(dcgui),
								I18n.format("serverobserver.gui.backandstop", 0f)));
			reset(Config.getConfig().durationDisconnected);
		} else if (screen instanceof GuiMainMenu) {
			for (final GuiButton button : buttons)
				if (button.id==2)
					this.mainMenuMultiPlayer = button;
			if (Config.getConfig().durationMainMenu.get()>0)
				reset(Config.getConfig().durationMainMenu);
			// Log.log.info("ready");
		}
	}

	@CoreEvent
	public void draw(final DrawScreenEvent.Post e) {
		final GuiScreen gui = this.mc.currentScreen;
		if (gui instanceof GuiMultiplayer) {
		} else if (gui instanceof GuiDisconnected) {
			if (this.disableBackButton!=null&&Config.getConfig().durationDisconnected.get()>=10)
				this.disableBackButton.displayString = I18n.format("serverobserver.gui.backandstop.time", I18n.format("serverobserver.gui.backandstop"), timeremain());
		} else if (gui instanceof GuiMainMenu)
			if (this.mainMenuMultiPlayer!=null&&this.target.getIP()!=null&&!this.hasOpened)
				this.mainMenuMultiPlayer.displayString = I18n.format("serverobserver.gui.maintomulti.time", I18n.format("menu.multiplayer"), timeremain());
	}

	private String displayText = "Disabled";
	private String displayTime = "";
	private Boolean targetServerStatus;
	private TargetServer target = new TargetServer();
	private AutoLoginMode autologin = new AutoLoginMode();

	@CoreEvent
	public void action(final ActionPerformedEvent.Post e) {
		this.targetServerStatus = null;
		final GuiScreen screen = this.mc.currentScreen;
		final int id = Compat.getButton(e).id;
		if (screen instanceof GuiMultiplayer&&id==BUTTON_ID) {
			final GuiMultiplayer mpgui = (GuiMultiplayer) screen;
			final ServerData server = getServerData(mpgui, Compat.getSelected(mpgui));
			if (server!=null) {
				if (!StringUtils.equals(this.target.getIP(), server.serverIP)) {
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
				selectTarget(mpgui, this.target.getIP());
		} else if (screen instanceof GuiDisconnected&&id==DISABLE_BACK_BUTTON_ID) {
			final GuiDisconnected dcgui = (GuiDisconnected) screen;
			this.autologin.set(false);
			this.target.set(null);
			dcgui.mc.displayGuiScreen(Compat.getParentScreen(dcgui));
		}
		this.displayTime = "";
		reset(Config.getConfig().durationPing);
	}

	public class TargetServer {
		private GuiMultiplayer mpgui_cache;
		private ServerData target;
		private String targetIP_cache;

		public void set(final ServerData serverData) {
			if (serverData==null) {
				setIP(null);
				this.target = null;
				Config.getConfig().save();
			} else {
				setIP(serverData.serverIP);
				this.target = serverData;
			}
		}

		public ServerData get(final GuiMultiplayer mpgui) {
			final String targetServerIP = Config.getConfig().targetServerIP.get();
			if (mpgui!=this.mpgui_cache||!StringUtils.equals(this.targetIP_cache, targetServerIP)) {
				this.target = getServerData(mpgui, getTarget(mpgui, this.targetIP_cache = targetServerIP));
				this.mpgui_cache = mpgui;
			}
			return this.target;
		}

		public void setIP(final String ip) {
			Config.getConfig().targetServerIP.set(StringUtils.defaultString(ip));
		}

		public String getIP() {
			final String ip = Config.getConfig().targetServerIP.get();
			if (!StringUtils.isEmpty(ip))
				return ip;
			return null;
		}
	}

	public class AutoLoginMode {
		public void set(final boolean enabled) {
			Config.getConfig().targetAutoLogin.set(enabled);
			Config.getConfig().save();
		}

		public boolean is() {
			return Config.getConfig().targetAutoLogin.get();
		}
	}

	private final Minecraft mc = Minecraft.getMinecraft();
	private Timer timer = new Timer();
	private boolean hasOpened;

	@CoreEvent
	public void tickclient() {
		Timer.tick();
		if (this.mc.currentScreen instanceof GuiMultiplayer) {
			final GuiMultiplayer mpgui = (GuiMultiplayer) this.mc.currentScreen;
			this.hasOpened = true;
			final ServerData serverData = this.target.get(mpgui);
			if (serverData!=null) {
				final Boolean before = this.targetServerStatus;
				this.targetServerStatus = Compat.getPinged(serverData)&&serverData.pingToServer>=0;
				// Log.log.info("pinged: {}, pingms: {}", serverData.pinged, serverData.pingToServer);
				if (this.targetServerStatus)
					if (before!=null&&!before) {
						Compat.playSound(this.mc, new ResourceLocation(Config.getConfig().notificationSound.get()), (float) (double) Config.getConfig().notificationPitch.get());
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
							Compat.connectToServer(mpgui, serverData);
					} else
						Compat.setPinged(serverData, false);
					reset(Config.getConfig().durationPing);
				}
			}
		} else if (this.mc.currentScreen instanceof GuiDisconnected) {
			final GuiDisconnected dcgui = (GuiDisconnected) this.mc.currentScreen;
			if (this.timer.getTime()>0)
				dcgui.mc.displayGuiScreen(Compat.getParentScreen(dcgui));
		} else if (this.mc.currentScreen instanceof GuiMainMenu) {
			final GuiMainMenu mmgui = (GuiMainMenu) this.mc.currentScreen;
			if (this.target.getIP()!=null&&!this.hasOpened&&Config.getConfig().durationDisconnected.get()>=10)
				if (this.timer.getTime()>0)
					this.mc.displayGuiScreen(new GuiMultiplayer(mmgui));
		} else
			this.hasOpened = true;
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
			Compat.selectServer(mpgui, target);
	}

	private int getTarget(final GuiMultiplayer mpgui, final String serverIP) {
		final int count = Compat.countServers(mpgui);
		for (int i = 0; i<count; i++) {
			final ServerData serverData = Compat.getServerData(mpgui, i);
			if (StringUtils.equals(serverIP, serverData.serverIP))
				return i;
		}
		return -1;
	}

	private ServerData getServerData(final GuiMultiplayer mpgui, final int index) {
		if (index<0)
			return null;
		final IGuiListEntry guilistextended$iguilistentry = Compat.getListEntry(mpgui, index);
		if (guilistextended$iguilistentry instanceof ServerListEntryNormal)
			return Compat.getServerData((ServerListEntryNormal) guilistextended$iguilistentry);
		return null;
	}
}