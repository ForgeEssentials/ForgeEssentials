package com.ForgeEssentials.afterlife;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;

import com.ForgeEssentials.api.APIRegistry;

import cpw.mods.fml.common.IPlayerTracker;

public class RespawnDebuff implements IPlayerTracker
{
	public static final String				BYPASSPOTION	= ModuleAfterlife.BASEPERM + ".bypassPotions";
	public static final String				BYPASSSTATS		= ModuleAfterlife.BASEPERM + ".bypassStats";
	public static ArrayList<PotionEffect>	potionEffects;
	public static int						hp;
	public static int						food;

	@Override
	public void onPlayerLogin(EntityPlayer player)
	{
	}

	@Override
	public void onPlayerLogout(EntityPlayer player)
	{
	}

	@Override
	public void onPlayerChangedDimension(EntityPlayer player)
	{
	}

	@Override
	public void onPlayerRespawn(EntityPlayer player1)
	{
		if (player1.worldObj.isRemote)
			return;
		EntityPlayer player = player1;
		if (!APIRegistry.perms.checkPermAllowed(player, BYPASSPOTION))
		{
			for (PotionEffect effect : potionEffects)
			{
				player.addPotionEffect(effect);
			}
		}
		if (!APIRegistry.perms.checkPermAllowed(player, BYPASSSTATS))
		{
			player.getFoodStats().addStats(-1 * (20 - food), 0);
			player.setEntityHealth(hp);
		}
	}
}