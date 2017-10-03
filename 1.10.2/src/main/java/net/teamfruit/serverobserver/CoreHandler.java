package net.teamfruit.serverobserver;

import javax.annotation.Nonnull;

import net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent;
import net.minecraftforge.client.event.GuiScreenEvent.DrawScreenEvent;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

public class CoreHandler {
	public static final @Nonnull CoreHandler instance = new CoreHandler();

	public final @Nonnull Config configHandler = Config.getConfig();
	public final @Nonnull GuiHandler guiHandler = new GuiHandler(Compat.compat);

	public void init() {
		// FMLCommonHandler.instance().bus().register(this);
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void onConfigChanged(final @Nonnull ConfigChangedEvent.OnConfigChangedEvent eventArgs) {
		for (final ConfigBase base : ConfigBase.configChangeHandlers)
			base.onConfigChanged(eventArgs.getModID());
	}

	@SubscribeEvent
	public void open(final InitGuiEvent.Post e) {
		this.guiHandler.open(e);
	}

	@SubscribeEvent
	public void draw(final DrawScreenEvent.Post e) {
		this.guiHandler.draw(e);
	}

	@SubscribeEvent
	public void action(final ActionPerformedEvent.Post e) {
		this.guiHandler.action(e);
	}

	@SubscribeEvent
	public void tickclient(final @Nonnull ClientTickEvent e) {
		this.guiHandler.tickclient();
	}
}