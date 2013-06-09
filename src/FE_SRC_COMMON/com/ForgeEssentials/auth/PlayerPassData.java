package com.ForgeEssentials.auth;

import java.util.HashMap;

import com.ForgeEssentials.api.data.IReconstructData;
import com.ForgeEssentials.api.data.SaveableObject;
import com.ForgeEssentials.api.data.SaveableObject.Reconstructor;
import com.ForgeEssentials.api.data.SaveableObject.SaveableField;
import com.ForgeEssentials.api.data.SaveableObject.UniqueLoadingKey;
import com.ForgeEssentials.data.api.ClassContainer;
import com.ForgeEssentials.data.api.DataStorageManager;

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
