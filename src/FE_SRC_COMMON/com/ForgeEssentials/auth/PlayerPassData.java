package com.ForgeEssentials.auth;

import java.util.HashMap;

import com.ForgeEssentials.api.data.ClassContainer;
import com.ForgeEssentials.api.data.DataStorageManager;
import com.ForgeEssentials.api.data.IReconstructData;
import com.ForgeEssentials.api.data.SaveableObject;
import com.ForgeEssentials.api.data.SaveableObject.Reconstructor;
import com.ForgeEssentials.api.data.SaveableObject.SaveableField;
import com.ForgeEssentials.api.data.SaveableObject.UniqueLoadingKey;

@SaveableObject
public class PlayerPassData
{
	private static HashMap<String, PlayerPassData>	datas	= new HashMap<String, PlayerPassData>();

	public static PlayerPassData getData(String username)
	{
		PlayerPassData data = datas.get(username);
		
		if (data == null)
		{
			data = (PlayerPassData) DataStorageManager.getReccomendedDriver().loadObject(container, username);
		}

		return data;
	}
	
	public static void registerData(PlayerPassData data)
	{
		DataStorageManager.getReccomendedDriver().saveObject(container, data);
	}
	
	public static void discardData(String username)
	{
		PlayerPassData data = datas.remove(username);
		
		if (data != null)
			DataStorageManager.getReccomendedDriver().saveObject(container, data);
	}
	
	public static final ClassContainer container = new ClassContainer(PlayerPassData.class);

	@UniqueLoadingKey
	public final String	username;
	@SaveableField
	public String		password;

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

}
