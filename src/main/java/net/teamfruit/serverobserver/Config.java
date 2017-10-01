package net.teamfruit.serverobserver;

import java.io.File;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Config extends ConfigBase {
	private static @Nullable Config instance;

	public static @Nonnull Config getConfig() {
		if (instance!=null)
			return instance;
		throw new IllegalStateException("config not initialized");
	}

	public static void init(final @Nonnull File cfgFile, final @Nonnull String version) {
		instance = new Config(cfgFile, version);
	}

	private Config(final @Nonnull File configFile, final @Nonnull String version) {
		super(configFile, version);
	}

	{
		getCategory("Duration").setLanguageKey("serverobserver.config.duration").setComment("Set the time such as ping interval");
	}

	public final ConfigProperty<Integer> durationMainMenu = propertyInteger(get("Duration", "MainMenu", 10, "Time until the multiplay screen is displayed after starting Minecraft (invalid with 0)").setMinValue(0).setLanguageKey("serverobserver.config.duration.mainmenu"));
	public final ConfigProperty<Integer> durationPing = propertyInteger(get("Duration", "Ping", 10, "Ping interval (minimum: 10 seconds)").setMinValue(10).setLanguageKey("serverobserver.config.duration.ping"));
	public final ConfigProperty<Integer> durationAutoLogin = propertyInteger(get("Duration", "AutoLogin", 10, "Time from ping completion to automatic login (minimum: 10 seconds)").setMinValue(10).setLanguageKey("serverobserver.config.duration.autologin"));
	public final ConfigProperty<Integer> durationDisconnected = propertyInteger(get("Duration", "Disconnected", 10, "The time from the disconnection screen to the display of the multiplay screen (minimum: 10 seconds)").setMinValue(10).setLanguageKey("serverobserver.config.duration.disconnected"));

	{
		getCategory("ObserveTarget").setLanguageKey("serverobserver.config.observetarget").setComment("Remember the server being monitored");
	}

	public final ConfigProperty<String> targetServerIP = propertyString(get("ObserveTarget", "ServerIP", "", "IP address of the server being monitored").setLanguageKey("serverobserver.config.observetarget.serverip"));
	public final ConfigProperty<Boolean> targetAutoLogin = propertyBoolean(get("ObserveTarget", "AutoLogin", false, "Auto Login Mode").setLanguageKey("serverobserver.config.observetarget.autologin"));
}
