package net.teamfruit.serverobserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;

import com.google.common.base.Charsets;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

public class DefaultServerList {
	public final Gson gson = new Gson();

	public ServerListModel loadModel(final File file) {
		try (JsonReader reader = new JsonReader(new InputStreamReader(new FileInputStream(file), Charsets.UTF_8))) {
			return this.gson.fromJson(reader, ServerListModel.class);
		} catch (final Exception e) {
			Log.log.warn("Could not load "+file.getName(), e);
			return null;
		}
	}

	public static class ServerListModel {
		public List<ServerModel> servers;
	}

	public static class ServerModel {
		public String serverName;
		public String serverIP;
	}
}
