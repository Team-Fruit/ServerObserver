package net.teamfruit.serverobserver;

import java.util.List;

import javax.annotation.Nonnull;

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

public class Compat implements ICompat {
	public static final @Nonnull ICompat compat = new Compat();

	@Override
	public void color(final float colorRed, final float colorGreen, final float colorBlue, final float colorAlpha) {
		GL11.glColor4f(colorRed, colorGreen, colorBlue, colorAlpha);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<GuiButton> getButtonList(final InitGuiEvent e) {
		return e.buttonList;
	}

	@Override
	public FontRenderer font(final Minecraft mc) {
		return mc.fontRenderer;
	}

	@Override
	public int getHeight(final GuiDisconnected dcgui) {
		return dcgui.height/4+120+12+25;
	}

	@Override
	public GuiScreen getParentScreen(final GuiDisconnected dcgui) {
		return dcgui.field_146307_h;
	}

	@Override
	public GuiButton getButton(final ActionPerformedEvent e) {
		return e.button;
	}

	@Override
	public int getSelected(final GuiMultiplayer mpgui) {
		return mpgui.field_146803_h.func_148193_k();
	}

	@Override
	public void connectToServer(final GuiMultiplayer mpgui, final ServerData serverData) {
		mpgui.func_146791_a(serverData);
	}

	@Override
	public boolean getPinged(final ServerData serverData) {
		return serverData.field_78841_f;
	}

	@Override
	public void setPinged(final ServerData serverData, final boolean pinged) {
		serverData.field_78841_f = pinged;
	}

	private final String defaultSound = "minecraft:random.orb";

	@Override
	public String getDefaultSound() {
		return this.defaultSound;
	}

	@Override
	public void playSound(final Minecraft mc, final ResourceLocation sound, final float pitch) {
		mc.getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(sound, pitch));
	}

	@Override
	public void selectServer(final GuiMultiplayer mpgui, final int index) {
		mpgui.func_146790_a(index);
	}

	@Override
	public int countServers(final GuiMultiplayer mpgui) {
		return mpgui.func_146795_p().countServers();
	}

	@Override
	public ServerData getServerData(final GuiMultiplayer mpgui, final int index) {
		return mpgui.func_146795_p().getServerData(index);
	}

	@Override
	public IGuiListEntry getListEntry(final GuiMultiplayer mpgui, final int index) {
		return mpgui.field_146803_h.getListEntry(index);
	}

	@Override
	public ServerData getServerData(final ServerListEntryNormal entry) {
		return entry.func_148296_a();
	}

	@Override
	public int getPositionX(final GuiButton button) {
		return button.xPosition;
	}

	@Override
	public int getPositionY(final GuiButton button) {
		return button.yPosition;
	}
}
