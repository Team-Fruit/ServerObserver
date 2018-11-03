package net.teamfruit.serverobserver;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.teamfruit.serverobserver.CompatConfigs.CompatConfigElement;
import net.teamfruit.serverobserver.CompatConfigs.CompatGuiConfig;
import net.teamfruit.serverobserver.CompatConfigs.CompatModGuiFactory;

/**
 * コンフィグGUI
 *
 * @author Kamesuta
 */
public class ConfigGuiFactory extends CompatModGuiFactory {

	public static class ConfigGui extends CompatGuiConfig {

		public ConfigGui(final @Nullable GuiScreen parent) {
			super(parent, getConfigElements(), Reference.MODID, false, false, I18n.format("serverobserver.config"));
		}

		private static @Nonnull List<CompatConfigElement> getConfigElements() {
			final List<CompatConfigElement> list = new ArrayList<CompatConfigElement>();
			getConfigElements(list, Config.getConfig().getBase());
			getConfigElements(list, Config.getConfig().getBaseDynamic());
			return list;
		}

		private static @Nonnull List<CompatConfigElement> getConfigElements(final List<CompatConfigElement> list, final ConfigBase cb) {
			ConfigCategory general = null;

			for (final String cat : cb.getCategoryNames()) {
				final ConfigCategory cc = cb.getCategory(cat);

				if (StringUtils.equals(cc.getName(), Configuration.CATEGORY_GENERAL)) {
					general = cc;
					continue;
				}

				if (cc.isChild())
					continue;

				list.add(CompatConfigElement.fromCategory(cc));
			}

			// General項目をトップに表示します
			if (general!=null) {
				for (final ConfigCategory cc : general.getChildren())
					list.add(CompatConfigElement.fromCategory(cc));
				for (final Property prop : general.values())
					list.add(CompatConfigElement.fromProperty(prop));
			}

			return list;
		}
	}

	@Override
	public void initialize(final @Nullable Minecraft minecraftInstance) {

	}

	@Override
	public Class<? extends GuiScreen> mainConfigGuiClassCompat() {
		return ConfigGui.class;
	}

	@Override
	public GuiScreen createConfigGuiCompat(final GuiScreen parentScreen) {
		return new ConfigGui(parentScreen);
	}
}