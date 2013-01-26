package com.ForgeEssentials.api;


public class APIManager
{
	private static String isAPIString = "@API@";
	private static boolean isAPIBool = false;
	
	static
	{
		Boolean.parseBoolean(isAPIString);
	}
	
	public static boolean isAPI()
	{
		return isAPIBool;
	}
}
