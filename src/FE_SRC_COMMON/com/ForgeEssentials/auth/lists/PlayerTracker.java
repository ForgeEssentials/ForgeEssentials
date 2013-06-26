package com.ForgeEssentials.auth.lists;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import com.ForgeEssentials.api.APIRegistry;
import com.ForgeEssentials.util.OutputHandler;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.IPlayerTracker;

public class PlayerTracker implements IPlayerTracker {

	public static String banned;
	public int counter;
	public int maxcounter;
	public static String notvip;
	public static String notwhitelisted;
	public static boolean whitelist;
	public static int vipslots;
	public static int offset;
	
	@Override
	public void onPlayerLogin(EntityPlayer player) {
		maxcounter = FMLCommonHandler.instance().getMinecraftServerInstance().getMaxPlayers() - vipslots - offset;
		if (APIRegistry.perms.checkPermAllowed(player, "ForgeEssentials.Auth.isBanned")){
			((EntityPlayerMP) player).playerNetServerHandler.kickPlayerFromServer(banned);
		}else if (whitelist){
			if(!APIRegistry.perms.checkPermAllowed(player, "ForgeEssentials.Auth.isWhiteListed")){
				((EntityPlayerMP) player).playerNetServerHandler.kickPlayerFromServer(notwhitelisted);
			}
		}
		if (APIRegistry.perms.checkPermAllowed(player, "ForgeEssentials.Auth.isVIP")){
			return;
		}else if (counter == maxcounter){
			((EntityPlayerMP) player).playerNetServerHandler.kickPlayerFromServer(notvip);
		}else counter = counter + 1;

	}

	@Override
	public void onPlayerLogout(EntityPlayer player) {
		counter = counter - 1;
	}

	@Override
	public void onPlayerChangedDimension(EntityPlayer player) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPlayerRespawn(EntityPlayer player) {
		// TODO Auto-generated method stub

	}

}
