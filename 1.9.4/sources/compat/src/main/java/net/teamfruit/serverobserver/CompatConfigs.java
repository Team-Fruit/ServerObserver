package net.teamfruit.serverobserver;

import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

public class CompatConfigs {
	public static class CompatGuiConfig extends GuiConfig {
		public CompatGuiConfig(final GuiScreen parentScreen, final List<CompatConfigElement> configElements, final String modID, final boolean allRequireWorldRestart, final boolean allRequireMcRestart, final String title) {
			super(parentScreen, CompatConfigElement.getConfigElements(configElements), modID, allRequireWorldRestart, allRequireMcRestart, GuiConfig.getAbridgedConfigPath(title));
		}
	}

	public static class CompatConfigElement {
		public final IConfigElement element;

		public CompatConfigElement(final IConfigElement element) {
			this.element = element;
		}

		public static List<IConfigElement> getConfigElements(final List<CompatConfigElement> elements) {
			return Lists.transform(elements, t -> t==null ? null : t.element);
		}

		public static CompatConfigElement fromCategory(final ConfigCategory category) {
			return new CompatConfigElement(new ConfigElement(category));
		}

		public static CompatConfigElement fromProperty(final Property prop) {
			return new CompatConfigElement(new ConfigElement(prop));
		}
	}

	public static abstract class CompatModGuiFactory implements IModGuiFactory {
		@Override
		public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
			return null;
		}

		@Override
		public RuntimeOptionGuiHandler getHandlerFor(final RuntimeOptionCategoryElement element) {
			return null;
		}

		@Override
		public @Nullable Class<? extends GuiScreen> mainConfigGuiClass() {
			return mainConfigGuiClassCompat();
		}

		public abstract @Nullable Class<? extends GuiScreen> mainConfigGuiClassCompat();

		public abstract GuiScreen createConfigGuiCompat(GuiScreen parentScreen);
	}
}
