package com.ForgeEssentials.core;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.NBTTagCompound;

public class Util {
	
	//no use for it yet
	
	/**
	 * Mainly useful in permissions, but is here because commands needs it as well.
	
	public static void setPerPlayerSetting(EntityPlayer player, NBTTagCompound setting){
		String user = player.username;
		if(FMLCommonHandler.instance().getMinecraftServerInstance().isDedicatedServer()) user = "player";
		NBTTagCompound data = playerData.getCompoundTag(user);
		data.setCompoundTag("Setting", setting);
		playerData.setCompoundTag(user, data);
	}
	
	public static NBTTagCompound getPerPlayerSetting(EntityPlayer player){
		String user = player.username;
		if(FMLCommonHandler.instance().getMinecraftServerInstance().isDedicatedServer()) user = "player";
		return playerData.getCompoundTag(player.username).getCompoundTag("Settings");
	}
	*/

}
