package net.teamfruit.serverobserver;

import java.io.File;
import java.io.IOException;

import javax.annotation.Nonnull;

import org.apache.commons.io.FileUtils;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.client.Minecraft;

public class ClientProxy extends CommonProxy {
	@Override
	public void preInit(final @Nonnull FMLPreInitializationEvent event) {
		super.preInit(event);
		final File cfgDir = event.getModConfigurationDirectory();
		final File modCfgDir = new File(cfgDir, Reference.MODID);
		final File oldCfg = event.getSuggestedConfigurationFile();
		final File cfg = new File(modCfgDir, Reference.MODID+".cfg");
		if (oldCfg.exists())
			try {
				FileUtils.moveFile(oldCfg, cfg);
			} catch (final IOException e) {
			}
		final File dynCfg = new File(Minecraft.getMinecraft().mcDataDir, Reference.MODID+".cfg");
		Config.init(cfg, dynCfg, "1.0.0", Compat.compat);
		CoreHandler.instance.preInit(modCfgDir);
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