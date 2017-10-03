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
	public List<GuiButton> getButtonList(final InitGuiEvent e) {
		return e.buttonList;
	}

	@Override
	public FontRenderer font(final Minecraft mc) {
		return mc.fontRendererObj;
	}

	@Override
	public int getHeight(final GuiDisconnected dcgui) {
		return dcgui.height/2+dcgui.field_175353_i/2+font(dcgui.mc).FONT_HEIGHT+25;
	}

	@Override
	public GuiScreen getParentScreen(final GuiDisconnected dcgui) {
		return dcgui.parentScreen;
	}

	@Override
	public GuiButton getButton(final ActionPerformedEvent e) {
		return e.button;
	}

	@Override
	public int getSelected(final GuiMultiplayer mpgui) {
		return mpgui.serverListSelector.func_148193_k();
	}

	@Override
	public void connectToServer(final GuiMultiplayer mpgui, final ServerData serverData) {
		mpgui.connectToServer(serverData);
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
		mc.getSoundHandler().playSound(new PositionedSoundRecord(sound, 0.25F, pitch, 0.0F, 0.0F, 0.0F));
	}

	@Override
	public void selectServer(final GuiMultiplayer mpgui, final int index) {
		mpgui.selectServer(index);
	}

	@Override
	public int countServers(final GuiMultiplayer mpgui) {
		return mpgui.getServerList().countServers();
	}

	@Override
	public ServerData getServerData(final GuiMultiplayer mpgui, final int index) {
		return mpgui.getServerList().getServerData(index);
	}

	@Override
	public IGuiListEntry getListEntry(final GuiMultiplayer mpgui, final int index) {
		return mpgui.serverListSelector.getListEntry(index);
	}

	@Override
	public ServerData getServerData(final ServerListEntryNormal entry) {
		return entry.getServerData();
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
