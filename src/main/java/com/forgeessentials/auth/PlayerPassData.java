package com.forgeessentials.auth;

import com.forgeessentials.core.data.DataManager;
import com.forgeessentials.data.api.ClassContainer;
import com.forgeessentials.data.api.DataStorageManager;
import com.forgeessentials.data.api.IReconstructData;
import com.forgeessentials.data.api.SaveableObject;
import com.forgeessentials.data.api.SaveableObject.Reconstructor;
import com.forgeessentials.data.api.SaveableObject.SaveableField;
import com.forgeessentials.data.api.SaveableObject.UniqueLoadingKey;

import java.util.HashMap;
import java.util.UUID;

@SaveableObject
public class PlayerPassData {
    public static final ClassContainer container = new ClassContainer(PlayerPassData.class);
    private static HashMap<UUID, PlayerPassData> datas = new HashMap<UUID, PlayerPassData>();
    @UniqueLoadingKey
    @SaveableField
    public final String username;
    @SaveableField
    public String password;

    public PlayerPassData(UUID username, String password)
    {
        this.username = username.toString();
        this.password = password;
    }

    /**
     * Returns the PlayerPassData if it exists.
     *
     * @param username
     * @return
     */
    public static PlayerPassData getData(UUID username)
    {
        PlayerPassData data = datas.get(username);
        if (data == null)
        {
            data = DataManager.getInstance().load(PlayerPassData.class, username.toString());
            if (data == null)
                data = (PlayerPassData) DataStorageManager.getReccomendedDriver().loadObject(container, username.toString());
        }
        return data;
    }

    /**
     * Creates a PlayerPassData
     *
     * @param username
     * @return
     */
    public static void registerData(UUID username, String pass)
    {
        PlayerPassData data = new PlayerPassData(username, pass);
        data.save();
        if (datas.get(data.username) != null)
        {
            datas.put(UUID.fromString(data.username), data);
        }
    }

    /**
     * Discards it.
     * Usually onPlayerLogout
     *
     * @param username
     * @return
     */
    public static void discardData(UUID username)
    {
        PlayerPassData data = datas.remove(username);
        if (data != null)
        {
            data.save();
        }
    }

    /**
     * Completely removes the data.
     *
     * @param username
     * @return
     */
    public static void deleteData(UUID username)
    {
        PlayerPassData data = datas.remove(username);
        DataManager.getInstance().delete(PlayerPassData.class, username.toString());
        DataStorageManager.getReccomendedDriver().deleteObject(container, username.toString());
    }

    @Reconstructor
    private static PlayerPassData reconstruct(IReconstructData data)
    {
        String username = data.getUniqueKey();
        String pass = (String) data.getFieldValue("password");

        return new PlayerPassData(UUID.fromString(username), pass);
    }

    public void save()
    {
        DataManager.getInstance().save(this, username);
        DataStorageManager.getReccomendedDriver().saveObject(container, this);
    }

}
