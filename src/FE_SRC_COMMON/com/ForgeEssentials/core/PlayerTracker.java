package com.ForgeEssentials.core;

import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import com.ForgeEssentials.api.APIRegistry;
import com.ForgeEssentials.api.permissions.query.PermQueryPlayer;
import com.ForgeEssentials.core.misc.LoginMessage;
import com.ForgeEssentials.util.FunctionHelper;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.IPlayerTracker;

public class PlayerTracker implements IPlayerTracker{

	@Override
	public void onPlayerLogin(EntityPlayer player)
	{
		PlayerInfo.getPlayerInfo(player.username);
		LoginMessage.sendLoginMessage(player);
	}

	@Override
	public void onPlayerLogout(EntityPlayer player)
	{
		PlayerInfo.discardInfo(player.username);
	}

	@Override
	public void onPlayerChangedDimension(EntityPlayer player)
	{
		// Not sure if we need to do anything here.
	}

	@Override
	public void onPlayerRespawn(EntityPlayer player)
	{
		// Not sure if we need to do anything here.
	}
}
