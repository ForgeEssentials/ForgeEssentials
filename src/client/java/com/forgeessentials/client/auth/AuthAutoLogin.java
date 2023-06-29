package com.forgeessentials.client.auth;

import java.io.File;
import java.io.IOException;

import com.forgeessentials.client.ForgeEssentialsClient;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.StringNBT;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class AuthAutoLogin {
	private static File KEYSTORE_DIR = new File(ServerLifecycleHooks.getCurrentServer().getServerDirectory(),
			"FEAuthStore/");

	private static File KEYSTORE_FILE;

	public static CompoundNBT KEYSTORE;

	/**
	 * Load the keystore from its NBT save file.
	 */
	public static CompoundNBT load() {
		if (!KEYSTORE_DIR.exists())
			KEYSTORE_DIR.mkdirs();

		try {
			KEYSTORE_FILE = new File(KEYSTORE_DIR,
					ServerLifecycleHooks.getCurrentServer().getSingleplayerName() + ".dat");
			if (!KEYSTORE_FILE.exists()) {
				KEYSTORE_FILE.createNewFile();
				return new CompoundNBT();
			}
			return CompressedStreamTools.read(KEYSTORE_FILE);
		} catch (IOException ex) {
			ForgeEssentialsClient.feclientlog.error("Unable to load AuthLogin keystore file - will ignore keystore.");
			return new CompoundNBT();
		}
	}

	/**
	 * Set the key for the current player on a server.
	 * 
	 * @param serverIP IP of the server that we received the key from
	 * @param key      The key to persist
	 */
	public static void setKey(String serverIP, String key) {
		KEYSTORE.put(serverIP, StringNBT.valueOf(key));
		try {
			KEYSTORE_FILE = new File(KEYSTORE_DIR,
					ServerLifecycleHooks.getCurrentServer().getSingleplayerName() + ".dat");
			CompressedStreamTools.write(KEYSTORE, KEYSTORE_FILE);
		} catch (IOException e) {
			ForgeEssentialsClient.feclientlog.error(
					"Unable to save AuthLogin keystore file - any keys received in this session will be discarded..");
		}
	}

	/**
	 * Get the key for the current player on a server.
	 * 
	 * @param serverIP IP of the server requesting the key
	 * @return
	 */
	public static String getKey(String serverIP) {
		String key = KEYSTORE.getString(serverIP);
		if (key == null)
			return "";
		else
			return key;
	}
}