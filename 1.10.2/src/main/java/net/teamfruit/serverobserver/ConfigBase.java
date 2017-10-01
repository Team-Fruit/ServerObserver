package net.teamfruit.serverobserver;

import java.io.File;
import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Sets;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;

public class ConfigBase extends Configuration {
	private final @Nonnull Set<IReloadableConfig> configs = Sets.newHashSet();

	public static interface IReloadableConfig {
		void reload();
	}

	public @Nonnull <T extends IReloadableConfig> T registerReload(final @Nonnull T config) {
		this.configs.add(config);
		return config;
	}

	public void reload() {
		for (final IReloadableConfig config : this.configs)
			config.reload();
	}

	public ConfigBase(final @Nonnull File configFile, final @Nonnull String version) {
		super(configFile, version, true);
		configChangeHandlers.add(this);
	}

	@Override
	public void save() {
		if (hasChanged())
			super.save();
	}

	public void onConfigChanged(final @Nonnull ConfigChangedEvent.OnConfigChangedEvent eventArgs) {
		if (StringUtils.equals(eventArgs.getModID(), Reference.MODID)) {
			save();
			reload();
		}
	}

	public static Set<ConfigBase> configChangeHandlers = Collections.newSetFromMap(new WeakHashMap<ConfigBase, Boolean>());

	@CoreEvent
	public static void onConfigChangedAll(final @Nonnull ConfigChangedEvent.OnConfigChangedEvent eventArgs) {
		for (final ConfigBase base : configChangeHandlers)
			base.onConfigChanged(eventArgs);
	}

	public @Nonnull ConfigProperty<String> propertyString(final @Nonnull Property property) {
		return registerReload(ConfigProperty.propertyString(this, property));
	}

	public @Nonnull ConfigProperty<Boolean> propertyBoolean(final @Nonnull Property property) {
		return registerReload(ConfigProperty.propertyBoolean(this, property));
	}

	public @Nonnull ConfigProperty<Double> propertyDouble(final @Nonnull Property property) {
		return registerReload(ConfigProperty.propertyDouble(this, property));
	}

	public @Nonnull ConfigProperty<Integer> propertyInteger(final @Nonnull Property property) {
		return registerReload(ConfigProperty.propertyInteger(this, property));
	}

	public static interface ConfigListener<E> {
		void onChanged(@Nonnull E value);
	}

	public static abstract class ConfigProperty<E> implements IReloadableConfig {
		protected final @Nonnull Configuration config;
		protected final @Nonnull Property property;
		private transient @Nonnull E prop;
		private @Nullable ConfigListener<E> listener;

		protected ConfigProperty(final @Nonnull Configuration config, final @Nonnull Property property, final @Nonnull E prop) {
			this.config = config;
			this.property = property;
			this.prop = prop;
		}

		public @Nonnull Property getProperty() {
			return this.property;
		}

		public @Nonnull ConfigProperty<E> setListener(@Nullable final ConfigListener<E> listener) {
			this.listener = listener;
			return this;
		}

		protected void setProp(final @Nonnull E prop) {
			if (!this.property.requiresMcRestart()) {
				this.prop = prop;
				if (this.listener!=null)
					this.listener.onChanged(prop);
			}
		}

		public @Nonnull E get() {
			return this.prop;
		}

		public abstract @Nonnull ConfigProperty<E> set(@Nonnull E value);

		public abstract @Nonnull ConfigProperty<E> reset();

		public static @Nonnull ConfigProperty<String> propertyString(final @Nonnull ConfigBase config, final @Nonnull Property property) {
			return new StringConfigProperty(config, property);
		}

		public static @Nonnull ConfigProperty<Boolean> propertyBoolean(final @Nonnull ConfigBase config, final @Nonnull Property property) {
			return new BooleanConfigProperty(config, property);
		}

		public static @Nonnull ConfigProperty<Double> propertyDouble(final @Nonnull ConfigBase config, final @Nonnull Property property) {
			return new DoubleConfigProperty(config, property);
		}

		public static @Nonnull ConfigProperty<Integer> propertyInteger(final @Nonnull ConfigBase config, final @Nonnull Property property) {
			return new IntegerConfigProperty(config, property);
		}

		private static class StringConfigProperty extends ConfigProperty<String> {
			protected StringConfigProperty(final @Nonnull Configuration config, final @Nonnull Property property) {
				super(config, property, property.getString());
			}

			@Override
			public @Nonnull StringConfigProperty set(final @Nonnull String value) {
				this.property.set(value);
				setProp(value);
				this.config.save();
				return this;
			}

			@Override
			public @Nonnull StringConfigProperty reset() {
				final String p = this.property.getDefault();
				this.property.set(p);
				setProp(p);
				this.config.save();
				return this;
			}

			@Override
			public void reload() {
				setProp(this.property.getString());
			}
		}

		private static class BooleanConfigProperty extends ConfigProperty<Boolean> {
			protected BooleanConfigProperty(final @Nonnull Configuration config, final @Nonnull Property property) {
				super(config, property, property.getBoolean());
			}

			@Override
			public @Nonnull BooleanConfigProperty set(final @Nonnull Boolean value) {
				this.property.set(value);
				setProp(value);
				this.config.save();
				return this;
			}

			@Override
			public @Nonnull BooleanConfigProperty reset() {
				final String p = this.property.getDefault();
				this.property.set(p);
				setProp(this.property.getBoolean());
				this.config.save();
				return this;
			}

			@Override
			public void reload() {
				setProp(this.property.getBoolean());
			}
		}

		private static class DoubleConfigProperty extends ConfigProperty<Double> {
			protected DoubleConfigProperty(final @Nonnull Configuration config, final @Nonnull Property property) {
				super(config, property, property.getDouble());
			}

			@Override
			public @Nonnull DoubleConfigProperty set(final @Nonnull Double value) {
				this.property.set(value);
				setProp(value);
				this.config.save();
				return this;
			}

			@Override
			public @Nonnull DoubleConfigProperty reset() {
				final String p = this.property.getDefault();
				this.property.set(p);
				setProp(this.property.getDouble());
				this.config.save();
				return this;
			}

			@Override
			public void reload() {
				setProp(this.property.getDouble());
			}
		}

		private static class IntegerConfigProperty extends ConfigProperty<Integer> {
			protected IntegerConfigProperty(final @Nonnull Configuration config, final @Nonnull Property property) {
				super(config, property, property.getInt());
			}

			@Override
			public @Nonnull IntegerConfigProperty set(final @Nonnull Integer value) {
				this.property.set(value);
				setProp(value);
				this.config.save();
				return this;
			}

			@Override
			public @Nonnull IntegerConfigProperty reset() {
				final String p = this.property.getDefault();
				this.property.set(p);
				setProp(this.property.getInt());
				this.config.save();
				return this;
			}

			@Override
			public void reload() {
				setProp(this.property.getInt());
			}
		}
	}
}