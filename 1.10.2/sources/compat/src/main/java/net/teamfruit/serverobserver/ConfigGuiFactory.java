package net.teamfruit.serverobserver;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

/**
 * コンフィグGUI
 *
 * @author Kamesuta
 */
public class ConfigGuiFactory implements IModGuiFactory {

	public static class ConfigGui extends GuiConfig {

		public ConfigGui(final @Nullable GuiScreen parent) {
			super(parent, getConfigElements(), Reference.MODID, false, false, I18n.format("serverobserver.config"));
		}

		private static @Nonnull List<IConfigElement> getConfigElements() {
			final List<IConfigElement> list = new ArrayList<IConfigElement>();
			getConfigElements(list, Config.getConfig().getBase());
			getConfigElements(list, Config.getConfig().getBaseDynamic());
			return list;
		}

		private static @Nonnull List<IConfigElement> getConfigElements(final List<IConfigElement> list, final ConfigBase cb) {
			ConfigCategory general = null;

			for (final String cat : cb.getCategoryNames()) {
				final ConfigCategory cc = cb.getCategory(cat);

				if (StringUtils.equals(cc.getName(), Configuration.CATEGORY_GENERAL)) {
					general = cc;
					continue;
				}

				if (cc.isChild())
					continue;

				list.add(new ConfigElement(cc));
			}

			// General項目をトップに表示します
			if (general!=null) {
				for (final ConfigCategory cc : general.getChildren())
					list.add(new ConfigElement(cc));
				for (final Property prop : general.values())
					list.add(new ConfigElement(prop));
			}

			return list;
		}
	}

	@Override
	public void initialize(final @Nullable Minecraft minecraftInstance) {

	}

	@Override
	public @Nullable Class<? extends GuiScreen> mainConfigGuiClass() {
		return ConfigGui.class;
	}

	@Override
	public @Nullable Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
		return null;
	}

	@SuppressWarnings("deprecation")
	@Override
	public @Nullable RuntimeOptionGuiHandler getHandlerFor(final @Nullable RuntimeOptionCategoryElement element) {
		return null;
	}
}