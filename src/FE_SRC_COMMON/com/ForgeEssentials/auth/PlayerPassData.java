package com.ForgeEssentials.auth;

import java.util.Map.Entry;
import java.util.Map;
import java.util.Set;

import com.ForgeEssentials.util.CaseStringMap;

public class PlayerPassData
{
	public final String				username;
	public String					password;
	private CaseStringMap<String>	vals;

	public PlayerPassData(String username, String password)
	{
		this.username = username;
		this.password = password;
		vals = new CaseStringMap();
	}
	
	public String getValue(String key)
	{
		return vals.get(key);
	}
	
	public void SetValue(String key, String value)
	{
		vals.put(key, value);
	}
	
	public Set<Entry<String, String>> getEntrySet()
	{
		return vals.entrySet();
	}

}
