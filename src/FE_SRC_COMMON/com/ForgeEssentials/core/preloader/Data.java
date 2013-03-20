package com.ForgeEssentials.core.preloader;

import java.util.HashMap;

/**
 * Kindly do not reference any FE classes outside the coremod package in this
 * class. This is a store room for all String[]s used by the coremod, 99% of
 * stuff is edited here and not in the actual coremod classes.
 */

public class Data
{

	protected static String[]	libraries		=
												{ "mysql-connector-java-bin.jar", "H2DB.jar", "metrics-R7-FEmod.jar" };
	protected static String[]	checksums		=
												{ "3ae0cff91d7f40d5b4c7cefbbd1eab34025bdc15", "32f12e53b4dab80b721525c01d766b95d22129bb", "bfffcaf975982b45d568f95526eba337652eecfb" };
	protected static String[]	transformers	=
												{ "com.ForgeEssentials.core.preloader.asm.FEAccessTransformer", "com.ForgeEssentials.core.preloader.asm.FEeventAdder", "com.ForgeEssentials.core.preloader.asm.FEBrandingTransformer" };
	public static HashMap<String, String>	iiwmHMob	= makeiiwmHMob();
	public static HashMap<String, String>	iiwmHMdev	= makeiiwmHMdev();

	public static HashMap<String, String>	isHMob		= makeisHMob();
	public static HashMap<String, String>	isHMdev		= makeisHMdev();
	
	public static HashMap<String, String> makeiiwmHMob()
	{
		HashMap<String, String> iiwmHMob = new HashMap<String, String>();

		iiwmHMob.put("className", "ir");
		iiwmHMob.put("javaClassName", "ir");
		iiwmHMob.put("targetMethodName", "d");
		iiwmHMob.put("worldFieldName", "a");
		iiwmHMob.put("entityPlayerFieldName", "b");
		iiwmHMob.put("worldJavaClassName", "yc");
		iiwmHMob.put("getBlockMetadataMethodName", "h");
		iiwmHMob.put("blockJavaClassName", "amq");
		iiwmHMob.put("blocksListFieldName", "p");
		iiwmHMob.put("entityPlayerJavaClassName", "qx");
		iiwmHMob.put("entityPlayerMPJavaClassName", "iq");

		return iiwmHMob;
	}

	public static HashMap<String, String> makeiiwmHMdev()
	{
		HashMap<String, String> iiwmHMdev = new HashMap<String, String>();

		iiwmHMdev.put("className", "net.minecraft.item.ItemInWorldManager");
		iiwmHMdev.put("javaClassName", "net/minecraft/item/ItemInWorldManager");
		iiwmHMdev.put("targetMethodName", "removeBlock");
		iiwmHMdev.put("worldFieldName", "theWorld");
		iiwmHMdev.put("entityPlayerFieldName", "thisPlayerMP");
		iiwmHMdev.put("worldJavaClassName", "net/minecraft/world/World");
		iiwmHMdev.put("getBlockMetaiiwmHMdevMethodName", "getBlockMetaiiwmHMdev");
		iiwmHMdev.put("blockJavaClassName", "net/minecraft/block/Block");
		iiwmHMdev.put("blocksListFieldName", "blocksList");
		iiwmHMdev.put("entityPlayerJavaClassName", "net/minecraft/entity/player/EntityPlayer");
		iiwmHMdev.put("entityPlayerMPJavaClassName", "net/minecraft/entity/player/EntityPlayerMP");

		return iiwmHMdev;
	}

	public static HashMap<String, String> makeisHMob()
	{
		HashMap<String, String> isHMob = new HashMap<String, String>();

		isHMob.put("className", "ur");
		isHMob.put("javaClassName", "ur");
		isHMob.put("targetMethodName", "a");
		isHMob.put("itemstackJavaClassName", "ur");
		isHMob.put("entityPlayerJavaClassName", "qx");
		isHMob.put("worldJavaClassName", "yc");

		return isHMob;
	}

	public static HashMap<String, String> makeisHMdev()
	{
		HashMap<String, String> isHMdev = new HashMap<String, String>();

		isHMdev.put("className", "net.minecraft.item.ItemStack");
		isHMdev.put("javaClassName", "net/minecraft/item/ItemStack");
		isHMdev.put("targetMethodName", "tryPlaceItemIntoWorld");

		isHMdev.put("itemstackJavaClassName", "net/minecraft/item/ItemStack");
		isHMdev.put("entityPlayerJavaClassName", "net/minecraft/entity/player/EntityPlayer");
		isHMdev.put("worldJavaClassName", "net/minecraft/world/World");

		return isHMdev;
	}
}
