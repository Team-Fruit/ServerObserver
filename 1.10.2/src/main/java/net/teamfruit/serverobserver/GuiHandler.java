package net.teamfruit.serverobserver;

import java.awt.Color;

import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.lwjgl.util.Timer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiListExtended.IGuiListEntry;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ServerListEntryNormal;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.SoundEvents;
import net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent;
import net.minecraftforge.client.event.GuiScreenEvent.DrawScreenEvent;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
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

		@Override
		public void drawButton(final Minecraft mc, final int mouseX, final int mouseY) {
			if (this.visible) {
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				this.hovered = mouseX>=this.xPosition&&mouseY>=this.yPosition&&mouseX<this.xPosition+this.width&&mouseY<this.yPosition+this.height;
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
		final GuiScreen screen = e.getGui();
		this.mainMenuMultiPlayer = null;
		if (screen instanceof GuiMultiplayer) {
			final GuiMultiplayer mpgui = (GuiMultiplayer) screen;
			e.getButtonList().add(new SkeletonButton(BUTTON_ID, mpgui.width-(5+180), 5, 180, 23, I18n.format("serverobserver.gui.mode")) {
				@Override
				protected void drawInside(final Minecraft mc, final int mouseX, final int mouseY) {
					final ServerData serverData = GuiHandler.this.target.get(mpgui);
					final FontRenderer font = mc.fontRendererObj;
					GuiHandler.this.displayText = serverData!=null ? GuiHandler.this.autologin ? "serverobserver.gui.mode.1" : "serverobserver.gui.mode.2" : "serverobserver.gui.mode.3";
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
			final FontRenderer font = dcgui.mc.fontRendererObj;
			e.getButtonList().add(
					this.disableBackButton = new GuiButton(DISABLE_BACK_BUTTON_ID,
							dcgui.width/2-100, dcgui.height/2+dcgui.textHeight/2+font.FONT_HEIGHT+25,
							I18n.format("serverobserver.gui.backandstop", 0f)));
			reset(Config.getConfig().durationDisconnected);
		} else if (screen instanceof GuiMainMenu) {
			for (final GuiButton button : e.getButtonList())
				if (button.id==2)
					this.mainMenuMultiPlayer = button;
			if (Config.getConfig().durationMainMenu.get()>0)
				reset(Config.getConfig().durationMainMenu);
			// Log.log.info("ready");
		}
	}

	@CoreEvent
	public void draw(final DrawScreenEvent.Post e) {
		final GuiScreen gui = e.getGui();
		if (gui instanceof GuiMultiplayer) {
		} else if (gui instanceof GuiDisconnected) {
			if (this.disableBackButton!=null)
				this.disableBackButton.displayString = I18n.format("serverobserver.gui.backandstop", timeremain());
		} else if (gui instanceof GuiMainMenu)
			if (this.mainMenuMultiPlayer!=null&&this.target.getIP()!=null&&!this.hasOpened)
				this.mainMenuMultiPlayer.displayString = I18n.format("serverobserver.gui.maintomulti", I18n.format("menu.multiplayer"), timeremain());
	}

	private String displayText = "Disabled";
	private String displayTime = "";
	private boolean autologin;
	private Boolean targetServerStatus;
	private TargetServer target = new TargetServer();

	@CoreEvent
	public void action(final ActionPerformedEvent.Post e) {
		this.targetServerStatus = null;
		final GuiScreen screen = e.getGui();
		if (screen instanceof GuiMultiplayer&&e.getButton().id==BUTTON_ID) {
			final GuiMultiplayer mpgui = (GuiMultiplayer) screen;
			final ServerData server = getServerData(mpgui, mpgui.serverListSelector.getSelected());
			if (server!=null) {
				if (!StringUtils.equals(this.target.getIP(), server.serverIP)) {
					this.autologin = false;
					this.target.set(server);
				} else if (!this.autologin) {
					this.autologin = true;
					this.target.set(server);
				} else {
					this.autologin = false;
					this.target.set(null);
				}
			} else
				selectTarget(mpgui, this.target.getIP());
		} else if (screen instanceof GuiDisconnected&&e.getButton().id==DISABLE_BACK_BUTTON_ID) {
			final GuiDisconnected dcgui = (GuiDisconnected) screen;
			this.autologin = false;
			this.target.set(null);
			dcgui.mc.displayGuiScreen(dcgui.parentScreen);
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

	private final Minecraft mc = FMLClientHandler.instance().getClient();
	private Timer timer = new Timer();
	private boolean hasOpened;

	@CoreEvent
	public void tickclient(final ClientTickEvent e) {
		Timer.tick();
		if (this.mc.currentScreen instanceof GuiMultiplayer) {
			final GuiMultiplayer mpgui = (GuiMultiplayer) this.mc.currentScreen;
			this.hasOpened = true;
			final ServerData serverData = this.target.get(mpgui);
			if (serverData!=null) {
				final Boolean before = this.targetServerStatus;
				this.targetServerStatus = serverData.pinged&&serverData.pingToServer>=0;
				// Log.log.info("pinged: {}, pingms: {}", serverData.pinged, serverData.pingToServer);
				if (this.targetServerStatus)
					if (before!=null&&!before)
						this.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.ENTITY_EXPERIENCE_ORB_TOUCH, 1.0F));

				if (!this.targetServerStatus)
					this.displayTime = I18n.format("serverobserver.gui.nextping", timeremain());
				else if (this.autologin)
					this.displayTime = I18n.format("serverobserver.gui.nextconnect", timeremain());
				else
					this.displayTime = "";
				if (this.timer.getTime()>0) {
					if (this.targetServerStatus) {
						if (this.autologin)
							mpgui.connectToServer(serverData);
					} else
						serverData.pinged = false;
					reset(Config.getConfig().durationPing);
				}
			}
		} else if (this.mc.currentScreen instanceof GuiDisconnected) {
			final GuiDisconnected dcgui = (GuiDisconnected) this.mc.currentScreen;
			if (this.timer.getTime()>0)
				dcgui.mc.displayGuiScreen(dcgui.parentScreen);
		} else if (this.mc.currentScreen instanceof GuiMainMenu) {
			final GuiMainMenu mmgui = (GuiMainMenu) this.mc.currentScreen;
			if (this.target.getIP()!=null&&!this.hasOpened)
				if (this.timer.getTime()>0)
					this.mc.displayGuiScreen(new GuiMultiplayer(mmgui));
		}
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
			mpgui.selectServer(target);
	}

	private int getTarget(final GuiMultiplayer mpgui, final String serverIP) {
		final int count = mpgui.savedServerList.countServers();
		for (int i = 0; i<count; i++) {
			final ServerData serverData = mpgui.savedServerList.getServerData(i);
			if (StringUtils.equals(serverIP, serverData.serverIP))
				return i;
		}
		return -1;
	}

	private ServerData getServerData(final GuiMultiplayer mpgui, final int index) {
		if (index<0)
			return null;
		final IGuiListEntry guilistextended$iguilistentry = mpgui.serverListSelector.getListEntry(index);
		if (guilistextended$iguilistentry instanceof ServerListEntryNormal)
			return ((ServerListEntryNormal) guilistextended$iguilistentry).getServerData();
		return null;
	}
}