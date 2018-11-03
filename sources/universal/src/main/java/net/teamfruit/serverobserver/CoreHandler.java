package net.teamfruit.serverobserver;

import java.io.File;

import javax.annotation.Nonnull;

import net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent;
import net.minecraftforge.client.event.GuiScreenEvent.DrawScreenEvent;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;

public class CoreHandler extends CompatHandler {
	public static final @Nonnull CoreHandler instance = new CoreHandler();

	public final @Nonnull Config configHandler = Config.getConfig();
	public final @Nonnull GuiHandler guiHandler = new GuiHandler(Compat.compat);

	@Override
	public void preInit(final File root) {
		this.guiHandler.init(root);
	}

	@Override
	public void onConfigChangedCompat(final @Nonnull CompatOnConfigChangedEvent eventArgs) {
		for (final ConfigBase base : ConfigBase.configChangeHandlers)
			base.onConfigChanged(eventArgs.getModID());
	}

	@Override
	public void openCompat(final InitGuiEvent.Post e) {
		this.guiHandler.open(e);
	}

	@Override
	public void drawCompat(final DrawScreenEvent.Post e) {
		this.guiHandler.draw(e);
	}

	@Override
	public void actionCompat(final ActionPerformedEvent.Pre e) {
		this.guiHandler.action(e);
	}

	@Override
	public void actionCompat(final ActionPerformedEvent.Post e) {
		this.guiHandler.action(e);
	}

	@Override
	public void tickclientCompat(final @Nonnull CompatClientTickEvent e) {
		this.guiHandler.tickclient();
	}
}