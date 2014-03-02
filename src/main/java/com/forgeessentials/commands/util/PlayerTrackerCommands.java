package com.forgeessentials.commands.util;

import net.minecraft.entity.player.EntityPlayer;

import com.forgeessentials.commands.CommandVanish;

import cpw.mods.fml.common.IPlayerTracker;

public class PlayerTrackerCommands implements IPlayerTracker
{
	@Override
	public void onPlayerLogin(EntityPlayer player)
	{
	    if (player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG).getBoolean(CommandVanish.TAGNAME))
	    {
	        CommandVanish.vanishedPlayers.add(player.entityId);
	    }
	}

	@Override
	public void onPlayerLogout(EntityPlayer player)
	{
		CommandVanish.vanishedPlayers.remove(player.entityId);
	}

	@Override
	public void onPlayerChangedDimension(EntityPlayer player)
	{
	    if (player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG).getBoolean(CommandVanish.TAGNAME))
        {
            CommandVanish.vanishedPlayers.add(player.entityId);
        }
	}

	@Override
	public void onPlayerRespawn(EntityPlayer player)
	{
		
		if (player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG).getBoolean(CommandVanish.TAGNAME))
        {
            CommandVanish.vanishedPlayers.add(player.entityId);
        }
	}
}
