package net.teamfruit.serverobserver;

import javax.annotation.Nonnull;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {
	@Override
	public void preInit(final @Nonnull FMLPreInitializationEvent event) {
		super.preInit(event);
		Config.init(event.getSuggestedConfigurationFile(), "1.0.0", ServerObserver.compat);
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