package net.teamfruit.serverobserver;

import java.io.File;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.teamfruit.serverobserver.ConfigBase.ConfigProperty;

public class Config {
	private static @Nullable Config instance;

	private ConfigBase config;
	private ConfigBase dynamicconfig;

	public final ConfigProperty<Integer> durationMainMenu;
	public final ConfigProperty<Integer> durationPing;
	public final ConfigProperty<Integer> durationAutoLogin;
	public final ConfigProperty<Integer> durationDisconnected;
	private final ConfigProperty<String> targetServerNameDefault;
	private final ConfigProperty<String> targetServerIPDefault;
	private final ConfigProperty<Boolean> targetAutoLoginDefault;
	public final ConfigProperty<String> notificationSound;
	public final ConfigProperty<Double> notificationPitch;
	public final ConfigProperty<Boolean> startWithMultiplayerMenu;
	public final ConfigProperty<Boolean> startAndConnect;

	public final ConfigProperty<String> targetServerName;
	public final ConfigProperty<String> targetServerIP;
	public final ConfigProperty<Boolean> targetAutoLogin;
	public final ConfigProperty<Boolean> miscInitServer;

	private Config(final @Nonnull File staticFile, final @Nonnull File dynamicFile, final @Nonnull String version, @Nonnull final ICompat compat) {
		// init static config
		this.config = new ConfigBase(staticFile, version);

		this.config.getCategory("Duration").setLanguageKey("serverobserver.config.duration").setComment("Set the time such as ping interval");
		this.durationMainMenu = this.config.propertyInteger(this.config.get("Duration", "MainMenu", 15, "Time until the multiplay screen is displayed after starting Minecraft (invalid with 0)").setMinValue(0).setLanguageKey("serverobserver.config.duration.mainmenu"));
		this.durationPing = this.config.propertyInteger(this.config.get("Duration", "Ping", 20, "Ping interval (minimum: 10 seconds)").setMinValue(10).setLanguageKey("serverobserver.config.duration.ping"));
		this.durationAutoLogin = this.config.propertyInteger(this.config.get("Duration", "AutoLogin", 10, "Time from ping completion to automatic login (minimum: 10 seconds)").setMinValue(10).setLanguageKey("serverobserver.config.duration.autologin"));
		this.durationDisconnected = this.config.propertyInteger(this.config.get("Duration", "Disconnected", 30, "The time from the disconnection screen to the display of the multiplay screen (Minimum: 10 seconds, less than 10 will not return to multiplay screen)").setMinValue(10).setLanguageKey("serverobserver.config.duration.disconnected"));

		this.config.getCategory("ObserveTarget").setLanguageKey("serverobserver.config.observetarget").setComment("Remember the server being monitored");
		this.targetServerNameDefault = this.config.propertyString(this.config.get("ObserveTarget", "DefaultServerName", "", "Name of the server being monitored").setLanguageKey("serverobserver.config.observetarget.servername"));
		this.targetServerIPDefault = this.config.propertyString(this.config.get("ObserveTarget", "DefaultServerIP", "", "IP address of the server being monitored").setLanguageKey("serverobserver.config.observetarget.serverip"));
		this.targetAutoLoginDefault = this.config.propertyBoolean(this.config.get("ObserveTarget", "DefaultAutoLogin", false, "Auto Login Mode").setLanguageKey("serverobserver.config.observetarget.autologin"));

		this.config.getCategory("Notification").setLanguageKey("serverobserver.config.notification").setComment("Notify by sound");
		this.notificationSound = this.config.propertyString(this.config.get("Notification", "Sound", compat.getDefaultSound(), "Sound resource location").setLanguageKey("serverobserver.config.notification.sound"));
		this.notificationPitch = this.config.propertyDouble(this.config.get("Notification", "Pitch", 1.0, "Sound pitch (minimum: 0.0, maximum: 2.0)").setMinValue(0.0).setMaxValue(2.0).setLanguageKey("serverobserver.config.notification.pitch"));

		this.config.getCategory("Miscellaneous").setLanguageKey("serverobserver.config.miscellaneous").setComment("Miscellaneous");
		this.startWithMultiplayerMenu = this.config.propertyBoolean(this.config.get("Miscellaneous", "StartWithMultiplayerMenu", false, "After game initialized, show multiplayer menu").setLanguageKey("serverobserver.config.miscellaneous.startwithmultiplayermenu").setRequiresMcRestart(true));
		this.startAndConnect = this.config.propertyBoolean(this.config.get("Miscellaneous", "StartAndConnect", false, "After game initialized, connect to the server").setLanguageKey("serverobserver.config.miscellaneous.startandconnect").setRequiresMcRestart(true));

		// init dynamic config
		this.dynamicconfig = new ConfigBase(dynamicFile, version);

		this.dynamicconfig.getCategory("Status").setLanguageKey("serverobserver.config.status").setComment("Current Status");
		this.targetServerName = this.dynamicconfig.propertyString(this.dynamicconfig.get("Status", "SelectedServerName", this.targetServerNameDefault.get(), "Name of the server being monitored").setLanguageKey("serverobserver.config.status.servername"));
		this.targetServerIP = this.dynamicconfig.propertyString(this.dynamicconfig.get("Status", "SelectedServerIP", this.targetServerIPDefault.get(), "IP address of the server being monitored").setLanguageKey("serverobserver.config.status.serverip"));
		this.targetAutoLogin = this.dynamicconfig.propertyBoolean(this.dynamicconfig.get("Status", "SelectedAutoLogin", this.targetAutoLoginDefault.get(), "Auto Login Mode").setLanguageKey("serverobserver.config.status.autologin"));
		this.miscInitServer = this.dynamicconfig.propertyBoolean(this.dynamicconfig.get("Status", "InitServers", true, "Initialize server list").setLanguageKey("serverobserver.config.status.initservers").setRequiresMcRestart(true));
	}

	public ConfigBase getBase() {
		return this.config;
	}

	public ConfigBase getBaseDynamic() {
		return this.dynamicconfig;
	}

	public void save() {
		this.config.save();
	}

	public static @Nonnull Config getConfig() {
		if (instance!=null)
			return instance;
		throw new IllegalStateException("config not initialized");
	}

	public static void init(final @Nonnull File staticFile, final @Nonnull File dynamicFile, final @Nonnull String version, @Nonnull final ICompat icompat) {
		instance = new Config(staticFile, dynamicFile, version, icompat);
	}
}
