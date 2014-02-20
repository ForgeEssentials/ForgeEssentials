package com.forgeessentials.commands.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import com.forgeessentials.commands.CommandSetSpawn;
import com.forgeessentials.commands.CommandVanish;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.AreaSelector.WarpPoint;

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
		CommandSetSpawn.spawns.remove(player.username);
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
		// send to spawn point
		WarpPoint p = CommandSetSpawn.spawns.get(player.username);
		if (p != null)
		{
			FunctionHelper.setPlayer((EntityPlayerMP) player, p);
			player.posX = p.xd;
			player.posY = p.yd;
			player.posZ = p.zd;
		}
		
		if (player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG).getBoolean(CommandVanish.TAGNAME))
        {
            CommandVanish.vanishedPlayers.add(player.entityId);
        }
	}
}
