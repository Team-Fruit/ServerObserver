package net.teamfruit.serverobserver;

import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiListExtended.IGuiListEntry;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ServerListEntryNormal;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.init.SoundEvents;
import net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;

public class Compat {
	public static void color(final float colorRed, final float colorGreen, final float colorBlue, final float colorAlpha) {
		GL11.glColor4f(colorRed, colorGreen, colorBlue, colorAlpha);
	}

	public static List<GuiButton> getButtonList(final InitGuiEvent e) {
		return e.getButtonList();
	}

	public static FontRenderer font(final Minecraft mc) {
		return mc.fontRendererObj;
	}

	public static int getHeight(final GuiDisconnected dcgui) {
		return dcgui.height/2+dcgui.textHeight/2+font(dcgui.mc).FONT_HEIGHT+25;
	}

	public static GuiScreen getParentScreen(final GuiDisconnected dcgui) {
		return dcgui.parentScreen;
	}

	public static GuiButton getButton(final ActionPerformedEvent e) {
		return e.getButton();
	}

	public static int getSelected(final GuiMultiplayer mpgui) {
		return mpgui.serverListSelector.getSelected();
	}

	public static void connectToServer(final GuiMultiplayer mpgui, final ServerData serverData) {
		mpgui.connectToServer(serverData);
	}

	public static boolean getPinged(final ServerData serverData) {
		return serverData.pinged;
	}

	public static void setPinged(final ServerData serverData, final boolean pinged) {
		serverData.pinged = pinged;
	}

	public static void playExpSound(final Minecraft mc) {
		mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.ENTITY_EXPERIENCE_ORB_TOUCH, 1.0F));
	}

	public static void selectServer(final GuiMultiplayer mpgui, final int index) {
		mpgui.selectServer(index);
	}

	public static int countServers(final GuiMultiplayer mpgui) {
		return mpgui.getServerList().countServers();
	}

	public static ServerData getServerData(final GuiMultiplayer mpgui, final int index) {
		return mpgui.getServerList().getServerData(index);
	}

	public static IGuiListEntry getListEntry(final GuiMultiplayer mpgui, final int index) {
		return mpgui.serverListSelector.getListEntry(index);
	}

	public static ServerData getServerData(final ServerListEntryNormal entry) {
		return entry.getServerData();
	}
}
