package com.forgeessentials.client.auth;

import java.io.File;
import java.io.IOException;

import com.forgeessentials.client.ForgeEssentialsClient;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.StringNBT;

public class AuthAutoLogin
{
    private final File KEYSTORE_FILE;

    public CompoundNBT KEYSTORE;

    public AuthAutoLogin()
    {
        Minecraft mc = Minecraft.getInstance();
        File path = new File(mc.gameDirectory, "FEAuthStore");
        if (!path.exists())
            path.mkdirs();
        this.KEYSTORE_FILE = new File(path, Minecraft.getInstance().getUser().getUuid() + ".dat");
        load();
    }

    /**
     * Load the keystore from its NBT save file.
     */
    public void load()
    {
        try
        {
            if (!KEYSTORE_FILE.exists())
            {
                KEYSTORE_FILE.createNewFile();
                KEYSTORE = new CompoundNBT();
            }
            KEYSTORE = CompressedStreamTools.read(KEYSTORE_FILE);
        }
        catch (IOException ex)
        {
            ForgeEssentialsClient.feclientlog.error("Unable to load AuthLogin keystore file - will ignore keystore.");
            KEYSTORE = new CompoundNBT();
        }
    }

    /**
     * Set the key for the current player on a server.
     * 
     * @param serverIP
     *            IP of the server that we received the key from
     * @param key
     *            The key to persist
     */
    public void setKey(String serverIP, String key)
    {
        KEYSTORE.put(serverIP, StringNBT.valueOf(key));
        try
        {
            CompressedStreamTools.write(KEYSTORE, KEYSTORE_FILE);
        }
        catch (IOException e)
        {
            ForgeEssentialsClient.feclientlog.error(
                    "Unable to save AuthLogin keystore file - any keys received in this session will be discarded..");
        }
    }

    /**
     * Get the key for the current player on a server.
     * 
     * @param serverIP
     *            IP of the server requesting the key
     * @return
     */
    public String getKey(String serverIP)
    {
        String key = KEYSTORE.getString(serverIP);
        if (key == null)
            return "";
        else
            return key;
    }
}