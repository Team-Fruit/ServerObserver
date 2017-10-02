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
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;

public class Compat {
	public static void color(final float colorRed, final float colorGreen, final float colorBlue, final float colorAlpha) {
		GL11.glColor4f(colorRed, colorGreen, colorBlue, colorAlpha);
	}

	@SuppressWarnings("unchecked")
	public static List<GuiButton> getButtonList(final InitGuiEvent e) {
		return e.buttonList;
	}

	public static FontRenderer font(final Minecraft mc) {
		return mc.fontRenderer;
	}

	public static int getHeight(final GuiDisconnected dcgui) {
		return dcgui.height/4+120+12+25;
	}

	public static GuiScreen getParentScreen(final GuiDisconnected dcgui) {
		return dcgui.field_146307_h;
	}

	public static GuiButton getButton(final ActionPerformedEvent e) {
		return e.button;
	}

	public static int getSelected(final GuiMultiplayer mpgui) {
		return mpgui.field_146803_h.func_148193_k();
	}

	public static void connectToServer(final GuiMultiplayer mpgui, final ServerData serverData) {
		mpgui.func_146791_a(serverData);
	}

	public static boolean getPinged(final ServerData serverData) {
		return serverData.field_78841_f;
	}

	public static void setPinged(final ServerData serverData, final boolean pinged) {
		serverData.field_78841_f = pinged;
	}

	public static String defaultSound = "minecraft:random.orb";

	public static void playSound(final Minecraft mc, final ResourceLocation sound, final float pitch) {
		mc.getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(sound, pitch));
	}

	public static void selectServer(final GuiMultiplayer mpgui, final int index) {
		mpgui.func_146790_a(index);
	}

	public static int countServers(final GuiMultiplayer mpgui) {
		return mpgui.func_146795_p().countServers();
	}

	public static ServerData getServerData(final GuiMultiplayer mpgui, final int index) {
		return mpgui.func_146795_p().getServerData(index);
	}

	public static IGuiListEntry getListEntry(final GuiMultiplayer mpgui, final int index) {
		return mpgui.field_146803_h.getListEntry(index);
	}

	public static ServerData getServerData(final ServerListEntryNormal entry) {
		return entry.func_148296_a();
	}
}
