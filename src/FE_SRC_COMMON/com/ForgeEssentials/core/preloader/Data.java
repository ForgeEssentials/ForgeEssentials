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
												{ "com.ForgeEssentials.core.preloader.asm.FEAccessTransformer", "com.ForgeEssentials.core.preloader.asm.FEeventAdder", /*"com.ForgeEssentials.core.preloader.asm.FEBrandingTransformer" ,*/ "com.ForgeEssentials.core.preloader.asm.FEPacketAnalyzer"};
	
	public static HashMap<String, String>	IIWMob     = new HashMap<String, String>();
	public static HashMap<String, String>	IIWMdev    = new HashMap<String, String>();

	public static HashMap<String, String>	ISob       = new HashMap<String, String>();
	public static HashMap<String, String>	ISdev      = new HashMap<String, String>();
	
	public static HashMap<String, String>   MCob         = new HashMap<String, String>();
	public static HashMap<String, String>   MCdev        = new HashMap<String, String>();
	
	public static HashMap<String, String>   TCob         = new HashMap<String, String>();
    public static HashMap<String, String>   TCdev        = new HashMap<String, String>();
	
	static
	{
		IIWMob.put("className", "jd");
		IIWMob.put("javaClassName", "jd");
		IIWMob.put("targetMethodName", "d");// searge name func_73079_d
		IIWMob.put("worldFieldName", "a"); // searge name field_73092_a
		IIWMob.put("entityPlayerFieldName", "b");// searge name field_73090_b
		IIWMob.put("worldJavaClassName", "aab");
		IIWMob.put("getBlockMetadataMethodName", "h");// searge name func_72805_g
		IIWMob.put("blockJavaClassName", "apa");
		IIWMob.put("blocksListFieldName", "p");// searge name field_71973_m
		IIWMob.put("entityPlayerJavaClassName", "sq");
		IIWMob.put("entityPlayerMPJavaClassName", "jc");

	    IIWMdev.put("className", "net.minecraft.item.ItemInWorldManager");
        IIWMdev.put("javaClassName", "net/minecraft/item/ItemInWorldManager");
        IIWMdev.put("targetMethodName", "removeBlock");
        IIWMdev.put("worldFieldName", "theWorld");
        IIWMdev.put("entityPlayerFieldName", "thisPlayerMP");
        IIWMdev.put("worldJavaClassName", "net/minecraft/world/World");
        IIWMdev.put("getBlockMetaiiwmHMdevMethodName", "getBlockMetadata");
        IIWMdev.put("blockJavaClassName", "net/minecraft/block/Block");
        IIWMdev.put("blocksListFieldName", "blocksList");
        IIWMdev.put("entityPlayerJavaClassName", "net/minecraft/entity/player/EntityPlayer");
        IIWMdev.put("entityPlayerMPJavaClassName", "net/minecraft/entity/player/EntityPlayerMP");

        ISob.put("className", "wm");
        ISob.put("javaClassName", "wm");
        ISob.put("targetMethodName", "a");// searge name func_77943_a
        ISob.put("itemstackJavaClassName", "wm");
        ISob.put("entityPlayerJavaClassName", "sq");
        ISob.put("worldJavaClassName", "aab");

        ISdev.put("className", "net.minecraft.item.ItemStack");
        ISdev.put("javaClassName", "net/minecraft/item/ItemStack");
        ISdev.put("targetMethodName", "tryPlaceItemIntoWorld");
        ISdev.put("itemstackJavaClassName", "net/minecraft/item/ItemStack");
        ISdev.put("entityPlayerJavaClassName", "net/minecraft/entity/player/EntityPlayer");
        ISdev.put("worldJavaClassName", "net/minecraft/world/World");
        
        MCob.put("className", "ch");
        MCob.put("targetMethod1", "a");// searge name func_74429_a
        MCob.put("targetMethod2", "b");// searge name func_74436_b
        MCob.put("packetName", "ei");
        
        MCdev.put("className", "net.minecraft.network.MemoryConnection");
        MCdev.put("targetMethod1", "addToSendQueue");
        MCdev.put("targetMethod2", "processOrCachePacket");
        MCdev.put("packetName", "net/minecraft/network/packet/Packet");
        
        TCob.put("className", "ci");
        TCob.put("targetMethod1", "a");// searge namefunc_74429_a
        TCob.put("targetMethod2", "i");// searge name func_74447_i
        TCob.put("packetName", "ei");
        
        TCdev.put("className", "net.minecraft.network.TcpConnection");
        TCdev.put("targetMethod1", "addToSendQueue");
        TCdev.put("targetMethod2", "readPacket");
        TCdev.put("packetName", "net/minecraft/network/packet/Packet");
	}
}