package com.forgeessentials.auth;

import java.util.HashMap;

import com.forgeessentials.data.api.ClassContainer;
import com.forgeessentials.data.api.DataStorageManager;
import com.forgeessentials.data.api.IReconstructData;
import com.forgeessentials.data.api.SaveableObject;
import com.forgeessentials.data.api.SaveableObject.Reconstructor;
import com.forgeessentials.data.api.SaveableObject.SaveableField;
import com.forgeessentials.data.api.SaveableObject.UniqueLoadingKey;

@SaveableObject
public class PlayerPassData
{
	private static HashMap<String, PlayerPassData>	datas	= new HashMap<String, PlayerPassData>();

	/**
	 * Returns the PlayerPassData if it exists.
	 * @param username
	 * @return
	 */
	public static PlayerPassData getData(String username)
	{
		PlayerPassData data = datas.get(username);

		if (data == null)
		{
			data = (PlayerPassData) DataStorageManager.getReccomendedDriver().loadObject(container, username);
		}

		return data;
	}

	/**
	 * Creates a PlayerPassData
	 * @param username
	 * @return
	 */
	public static void registerData(String username, String pass)
	{
		PlayerPassData data = new PlayerPassData(username, pass);
		data.save();
		if (datas.get(data.username) != null)
		{
			datas.put(data.username, data);
		}
	}

	/**
	 * Discards it.
	 * Usually onPlayerLogout
	 * @param username
	 * @return
	 */
	public static void discardData(String username)
	{
		PlayerPassData data = datas.remove(username);
		if (data != null)
			data.save();
	}

	/**
	 * Completely removes the data.
	 * @param username
	 * @return
	 */
	public static void deleteData(String username)
	{
		PlayerPassData data = datas.remove(username);
		DataStorageManager.getReccomendedDriver().deleteObject(container, username);
		if (data != null)
			ModuleAuth.unRegistered.add(username);
	}

	public static final ClassContainer	container	= new ClassContainer(PlayerPassData.class);

	@UniqueLoadingKey
	@SaveableField
	public final String					username;
	@SaveableField
	public String						password;

	public PlayerPassData(String username, String password)
	{
		this.username = username;
		this.password = password;
	}

	@Reconstructor
	private static PlayerPassData reconstruct(IReconstructData data)
	{
		String username = data.getUniqueKey();
		String pass = (String) data.getFieldValue("password");

		return new PlayerPassData(username, pass);
	}

	public void save()
	{
		DataStorageManager.getReccomendedDriver().saveObject(container, this);
	}

}
