package com.forgeessentials.core.preloader;

import java.util.HashMap;

/**
 * Kindly do not reference any FE classes outside the coremod package in this
 * class. This is a store room for all String[]s used by the coremod, 99% of
 * stuff is edited here and not in the actual coremod classes.
 */

// Change me if this class is updated for future minecraft versions
// MC version: 1.6.4
public class Data
{

	protected static String[]	libraries		=
												{ "mysql-connector-java-bin.jar", "H2DB.jar", "metrics-R7-FEmod.jar", "pircbotx-1.9.jar"};
	protected static String[]	checksums		=
												{ "3ae0cff91d7f40d5b4c7cefbbd1eab34025bdc15", "32f12e53b4dab80b721525c01d766b95d22129bb", "bfffcaf975982b45d568f95526eba337652eecfb", "c84e54d65043af5ad7cf45c54820225d14665769" };
	protected static String[]	transformers	=
												{ "com.forgeessentials.core.preloader.asm.FEAccessTransformer", "com.forgeessentials.core.preloader.asm.FEeventAdder", "com.forgeessentials.core.preloader.asm.FEPacketAnalyzer"};
	
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
		
		IIWMob.put("className", "jv");
		IIWMob.put("javaClassName", "jv");
		IIWMob.put("targetMethodName", "d");// searge name func_73079_d
		IIWMob.put("worldFieldName", "a"); // searge name field_73092_a
		IIWMob.put("entityPlayerFieldName", "b");// searge name field_73090_b
		IIWMob.put("worldJavaClassName", "ace");
		IIWMob.put("getBlockMetadataMethodName", "h");// searge name func_72805_g
		IIWMob.put("blockJavaClassName", "aqw");
		IIWMob.put("blocksListFieldName", "s");// searge name field_71973_m
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
        
        MCob.put("className", "cm");
        MCob.put("targetMethod1", "a");// searge name func_74429_a
        MCob.put("targetMethod2", "b");// searge name func_74436_b
        MCob.put("packetName", "ex");
        
        MCdev.put("className", "net.minecraft.network.MemoryConnection");
        MCdev.put("targetMethod1", "addToSendQueue");
        MCdev.put("targetMethod2", "processOrCachePacket");
        MCdev.put("packetName", "net/minecraft/network/packet/Packet");
        
        TCob.put("className", "cn");
        TCob.put("targetMethod1", "a");// searge namefunc_74429_a
        TCob.put("targetMethod2", "i");// searge name func_74447_i
        TCob.put("packetName", "ex");
        
        TCdev.put("className", "net.minecraft.network.TcpConnection");
        TCdev.put("targetMethod1", "addToSendQueue");
        TCdev.put("targetMethod2", "readPacket");
        TCdev.put("packetName", "net/minecraft/network/packet/Packet");
	}
}
