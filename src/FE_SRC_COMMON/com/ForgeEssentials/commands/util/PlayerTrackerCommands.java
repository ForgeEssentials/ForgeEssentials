package com.ForgeEssentials.commands.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.packet.Packet34EntityTeleport;

import com.ForgeEssentials.commands.CommandSetSpawn;
import com.ForgeEssentials.commands.CommandVanish;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.AreaSelector.WarpPoint;

import cpw.mods.fml.common.IPlayerTracker;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

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
