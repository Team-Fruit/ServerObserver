package net.teamfruit.serverobserver;

import java.util.List;

import net.minecraft.client.Minecraft;
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

public interface ICompat {

	void color(float colorRed, float colorGreen, float colorBlue, float colorAlpha);

	List<GuiButton> getButtonList(InitGuiEvent e);

	FontRenderer font(Minecraft mc);

	int getHeight(GuiDisconnected dcgui);

	GuiScreen getParentScreen(GuiDisconnected dcgui);

	GuiButton getButton(ActionPerformedEvent e);

	int getSelected(GuiMultiplayer mpgui);

	void connectToServer(GuiMultiplayer mpgui, ServerData serverData);

	boolean getPinged(ServerData serverData);

	void setPinged(ServerData serverData, boolean pinged);

	String getDefaultSound();

	void playSound(Minecraft mc, ResourceLocation sound, float pitch);

	void selectServer(GuiMultiplayer mpgui, int index);

	int countServers(GuiMultiplayer mpgui);

	ServerData getServerData(GuiMultiplayer mpgui, int index);

	IGuiListEntry getListEntry(GuiMultiplayer mpgui, int index);

	ServerData getServerData(ServerListEntryNormal entry);

	int getPositionX(GuiButton button);

	int getPositionY(GuiButton button);

}