package net.teamfruit.serverobserver;

import javax.annotation.Nonnull;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public interface IProxy {

	void preInit(@Nonnull FMLPreInitializationEvent event);

	void init(@Nonnull FMLInitializationEvent event);

	void postInit(@Nonnull FMLPostInitializationEvent event);

}