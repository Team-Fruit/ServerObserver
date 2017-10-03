package net.teamfruit.serverobserver;

import javax.annotation.Nonnull;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {
	@Override
	public void preInit(final @Nonnull FMLPreInitializationEvent event) {
		super.preInit(event);
		Config.init(event.getSuggestedConfigurationFile(), "1.0.0", Compat.compat);
	}

	@Override
	public void init(final @Nonnull FMLInitializationEvent event) {
		super.init(event);
		// Event Register
		CoreHandler.instance.init();
	}

	@Override
	public void postInit(final @Nonnull FMLPostInitializationEvent event) {
		super.postInit(event);
		Config.getConfig().save();
	}
}