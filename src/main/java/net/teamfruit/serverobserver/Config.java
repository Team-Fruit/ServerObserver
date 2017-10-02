package net.teamfruit.serverobserver;

import java.io.File;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Config extends ConfigBase {
	private static @Nullable Config instance;
	private static @Nullable ICompat compat;

	public static @Nonnull Config getConfig() {
		if (instance!=null)
			return instance;
		throw new IllegalStateException("config not initialized");
	}

	public static void init(final @Nonnull File cfgFile, final @Nonnull String version, @Nonnull final ICompat icompat) {
		compat = icompat;
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
	public final ConfigProperty<Integer> durationDisconnected = propertyInteger(get("Duration", "Disconnected", 10, "The time from the disconnection screen to the display of the multiplay screen (Minimum: 10 seconds, less than 10 will not return to multiplay screen)").setMinValue(10).setLanguageKey("serverobserver.config.duration.disconnected"));

	{
		getCategory("ObserveTarget").setLanguageKey("serverobserver.config.observetarget").setComment("Remember the server being monitored");
	}

	public final ConfigProperty<String> targetServerIP = propertyString(get("ObserveTarget", "ServerIP", "", "IP address of the server being monitored").setLanguageKey("serverobserver.config.observetarget.serverip"));
	public final ConfigProperty<Boolean> targetAutoLogin = propertyBoolean(get("ObserveTarget", "AutoLogin", false, "Auto Login Mode").setLanguageKey("serverobserver.config.observetarget.autologin"));

	{
		getCategory("Notification").setLanguageKey("serverobserver.config.notification").setComment("Notify by sound");
	}

	public final ConfigProperty<String> notificationSound = propertyString(get("Notification", "Sound", compat!=null ? compat.getDefaultSound() : "", "Sound resource location").setLanguageKey("serverobserver.config.notification.sound"));
	public final ConfigProperty<Double> notificationPitch = propertyDouble(get("Notification", "Pitch", 1.0, "Sound pitch (minimum: 0.0, maximum: 2.0)").setMinValue(0.0).setMaxValue(2.0).setLanguageKey("serverobserver.config.notification.pitch"));
}
