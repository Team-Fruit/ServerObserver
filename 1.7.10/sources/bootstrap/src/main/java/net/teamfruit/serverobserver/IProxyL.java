package net.teamfruit.serverobserver;

import javax.annotation.Nonnull;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public interface IProxyL {

	void preInit(@Nonnull FMLPreInitializationEvent event);

	void init(@Nonnull FMLInitializationEvent event);

	void postInit(@Nonnull FMLPostInitializationEvent event);

}