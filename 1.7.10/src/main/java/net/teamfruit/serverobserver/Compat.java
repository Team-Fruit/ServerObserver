package net.teamfruit.serverobserver;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import javax.annotation.Nonnull;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ServerListEntryNormal;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.teamfruit.serverobserver.GuiHandler.SkeletonButtonDrawInside;

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

	@Override
	public void ping(final GuiMultiplayer mpgui, final ServerData serverData) throws Exception {
		mpgui.func_146789_i().func_147224_a(serverData);
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
	public ServerList getServerList(final GuiMultiplayer mpgui) {
		return mpgui.func_146795_p();
	}

	@Override
	public ServerData getServerData(final GuiMultiplayer mpgui, final int index) {
		return mpgui.func_146795_p().getServerData(index);
	}

	@Override
	public ThreadPoolExecutor getThreadPool() {
		return ServerListEntryNormal.field_148302_b;
	}

	@Override
	public GuiButton createSkeletonButton(final int buttonId, final int x, final int y, final int widthIn, final int heightIn, final String buttonText, final SkeletonButtonDrawInside inside) {
		return new SkeletonButton(this, buttonId, x, y, widthIn, heightIn, buttonText, inside);
	}

	public static class SkeletonButton extends GuiButton {
		private final SkeletonButtonDrawInside inside;
		private final ICompat compat;

		public SkeletonButton(final ICompat compat, final int buttonId, final int x, final int y, final int widthIn, final int heightIn, final String buttonText, final SkeletonButtonDrawInside inside) {
			super(buttonId, x, y, widthIn, heightIn, buttonText);
			this.inside = inside;
			this.compat = compat;
		}

		protected boolean isHovered;

		@Override
		public void drawButton(final Minecraft mc, final int mouseX, final int mouseY) {
			if (this.visible) {
				this.compat.color(1.0F, 1.0F, 1.0F, 1.0F);
				final int x = this.xPosition;
				final int y = this.yPosition;
				this.isHovered = mouseX>=x&&mouseY>=y&&mouseX<x+this.width&&mouseY<y+this.height;
				mouseDragged(mc, mouseX, mouseY);
				drawRect(x, y, x+this.width, y+this.height, 0xcc000000);
				this.inside.drawInside(this, mc, mouseX, mouseY, x, y);
			}
		}
	}
}
