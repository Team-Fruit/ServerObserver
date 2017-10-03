package net.teamfruit.serverobserver;

import java.util.List;

import javax.annotation.Nonnull;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
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
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
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
	public List<GuiButton> getButtonList(final InitGuiEvent e) {
		return e.getButtonList();
	}

	@Override
	public FontRenderer font(final Minecraft mc) {
		return mc.fontRenderer;
	}

	@Override
	public int getHeight(final GuiDisconnected dcgui) {
		return dcgui.height/2+dcgui.textHeight/2+font(dcgui.mc).FONT_HEIGHT+25;
	}

	@Override
	public GuiScreen getParentScreen(final GuiDisconnected dcgui) {
		return dcgui.parentScreen;
	}

	@Override
	public GuiButton getButton(final ActionPerformedEvent e) {
		return e.getButton();
	}

	@Override
	public int getSelected(final GuiMultiplayer mpgui) {
		return mpgui.serverListSelector.getSelected();
	}

	@Override
	public void connectToServer(final GuiMultiplayer mpgui, final ServerData serverData) {
		mpgui.connectToServer(serverData);
	}

	@Override
	public boolean getPinged(final ServerData serverData) {
		return serverData.pinged;
	}

	@Override
	public void setPinged(final ServerData serverData, final boolean pinged) {
		serverData.pinged = pinged;
	}

	@Override
	public String getDefaultSound() {
		return SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP.getSoundName().toString();
	}

	@Override
	public void playSound(final Minecraft mc, final ResourceLocation sound, final float pitch) {
		mc.getSoundHandler().playSound(new PositionedSoundRecord(sound, SoundCategory.MASTER, 0.25F, pitch, false, 0, ISound.AttenuationType.NONE, 0.0F, 0.0F, 0.0F));
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
		public void drawButton(final Minecraft mc, final int mouseX, final int mouseY, final float partialTicks) {
			if (this.visible) {
				this.compat.color(1.0F, 1.0F, 1.0F, 1.0F);
				final int x = this.x;
				final int y = this.y;
				this.isHovered = mouseX>=x&&mouseY>=y&&mouseX<x+this.width&&mouseY<y+this.height;
				mouseDragged(mc, mouseX, mouseY);
				drawRect(x, y, x+this.width, y+this.height, 0xcc000000);
				this.inside.drawInside(this, mc, mouseX, mouseY, x, y);
			}
		}
	}
}
