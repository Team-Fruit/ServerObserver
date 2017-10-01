package net.teamfruit.serverobserver;

import javax.annotation.Nonnull;

public class Reference {
	public static final @Nonnull String MODID = "serverobserver";
	public static final @Nonnull String NAME = "ServerObserver";
	public static final @Nonnull String VERSION = "${version}";
	public static final @Nonnull String FORGE = "${forgeversion}";
	public static final @Nonnull String MINECRAFT = "${mcversion}";
	public static final @Nonnull String PROXY_SERVER = "net.teamfruit.serverobserver.CommonProxy";
	public static final @Nonnull String PROXY_CLIENT = "net.teamfruit.serverobserver.ClientProxy";
	public static final @Nonnull String GUI_FACTORY = "net.teamfruit.serverobserver.ConfigGuiFactory";
}
