package net.teamfruit.serverobserver;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

public interface SkeletonButtonDrawInside {
	void drawInside(GuiButton button, final Minecraft mc, final int mouseX, final int mouseY, final int x, final int y);
}
