package net.teamfruit.serverobserver;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.zip.ZipFile;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderException;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkCheckHandler;
import net.minecraftforge.fml.relauncher.FMLInjectionData;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = Reference.MODID, name = Reference.NAME, version = Reference.VERSION, guiFactory = Reference.GUI_FACTORY)
public class ServerObserver {
	@Instance(Reference.MODID)
	public static @Nullable ServerObserver instance;

	static {
		final ModContainer container = Loader.instance().activeModContainer();
		Log.log.info(container);
		final Object[] data = FMLInjectionData.data();
		final String mccversion = (String) data[4];
		final File minecraftDir = (File) data[6];
		final File modsDir = new File(minecraftDir, "mods");
		if (container!=null) {
			final File modFile = container.getSource();
			if (modFile!=null) {
				Log.log.info(container.getSource());
				ZipFile file = null;
				InputStream stream = null;
				try {
					final File canonicalModsDir = modsDir.getCanonicalFile();
					final File versionSpecificModsDir = new File(canonicalModsDir, mccversion);

					final String jarname = String.format("%s.jar", mccversion);
					final File destMod = new File(versionSpecificModsDir, jarname);

					file = new ZipFile(modFile);
					stream = file.getInputStream(file.getEntry(jarname));

					FileUtils.copyInputStreamToFile(stream, destMod);
					Launch.classLoader.addURL(destMod.toURI().toURL());
				} catch (final IOException e) {
					new LoaderException("Could not load version-specific file.", e);
				} finally {
					IOUtils.closeQuietly(file);
					IOUtils.closeQuietly(stream);
				}

			}
		}
		Log.log.info("init");
	}

	@SidedProxy(serverSide = Reference.PROXY_SERVER, clientSide = Reference.PROXY_CLIENT)
	public static @Nullable IProxy proxy;

	// public static final @Nonnull ICompat compat = new Compat();

	@NetworkCheckHandler
	public boolean checkModList(final @Nonnull Map<String, String> versions, final @Nonnull Side side) {
		return true;
	}

	@EventHandler
	public void preInit(final @Nonnull FMLPreInitializationEvent event) {
		if (proxy!=null)
			proxy.preInit(event);
	}

	@EventHandler
	public void init(final @Nonnull FMLInitializationEvent event) {
		if (proxy!=null)
			proxy.init(event);
	}

	@EventHandler
	public void postInit(final @Nonnull FMLPostInitializationEvent event) {
		if (proxy!=null)
			proxy.postInit(event);
	}
}
