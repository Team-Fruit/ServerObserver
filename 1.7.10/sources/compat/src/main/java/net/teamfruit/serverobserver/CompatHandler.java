package net.teamfruit.serverobserver;

import java.io.File;

import javax.annotation.Nonnull;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent;
import net.minecraftforge.client.event.GuiScreenEvent.DrawScreenEvent;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.common.MinecraftForge;

public abstract class CompatHandler {
	public abstract void preInit(final File root);

	public void init() {
		// FMLCommonHandler.instance().bus().register(this);
		MinecraftForge.EVENT_BUS.register(this);
	}

	public static class CompatEvent<T extends Event> {
		protected final T event;

		public CompatEvent(final T event) {
			this.event = event;
		}

		public void setCanceled(final boolean cancel) {
			this.event.setCanceled(cancel);
		}
	}

	public static class CompatOnConfigChangedEvent extends CompatEvent<ConfigChangedEvent.OnConfigChangedEvent> {
		public CompatOnConfigChangedEvent(final ConfigChangedEvent.OnConfigChangedEvent event) {
			super(event);
		}

		public String getModID() {
			return this.event.modID;
		}
	}

	public static class CompatClientTickEvent extends CompatEvent<ClientTickEvent> {
		public CompatClientTickEvent(final ClientTickEvent event) {
			super(event);
		}
	}

	@SubscribeEvent
	public void onConfigChanged(final @Nonnull ConfigChangedEvent.OnConfigChangedEvent eventArgs) {
		onConfigChangedCompat(new CompatOnConfigChangedEvent(eventArgs));
	}

	public abstract void onConfigChangedCompat(final @Nonnull CompatOnConfigChangedEvent eventArgs);

	@SubscribeEvent
	public void open(final InitGuiEvent.Post e) {
		openCompat(e);
	}

	public abstract void openCompat(final InitGuiEvent.Post e);

	@SubscribeEvent
	public void draw(final DrawScreenEvent.Post e) {
		drawCompat(e);
	}

	public abstract void drawCompat(final DrawScreenEvent.Post e);

	@SubscribeEvent
	public void action(final ActionPerformedEvent.Pre e) {
		actionCompat(e);
	}

	public abstract void actionCompat(final ActionPerformedEvent.Pre e);

	@SubscribeEvent
	public void action(final ActionPerformedEvent.Post e) {
		actionCompat(e);
	}

	public abstract void actionCompat(final ActionPerformedEvent.Post e);

	@SubscribeEvent
	public void tickclient(final @Nonnull ClientTickEvent e) {
		tickclientCompat(new CompatClientTickEvent(e));
	}

	public abstract void tickclientCompat(final @Nonnull CompatClientTickEvent e);
}