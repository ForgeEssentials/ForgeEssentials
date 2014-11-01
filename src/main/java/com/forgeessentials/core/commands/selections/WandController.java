package com.forgeessentials.core.commands.selections;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.ServerEventHandler;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.UserIdent;
import com.forgeessentials.util.selections.WorldPoint;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class WandController extends ServerEventHandler {

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void playerInteractEvent(PlayerInteractEvent event)
	{
		// if worldedit is installed, don't do anything
		// and only handle server events
		if (FMLCommonHandler.instance().getEffectiveSide().isClient() || ForgeEssentials.worldEditCompatilityPresent)
			return;

		// get info now rather than later
		EntityPlayer player = event.entityPlayer;
		PlayerInfo info = PlayerInfo.getPlayerInfo(player.getPersistentID());

		if (!info.isWandEnabled())
			return;

		// Check if wand should activate
		if (player.getCurrentEquippedItem() == null)
		{
			if (info.getWandID() != null)
				return;
		}
		else
		{
			if (!(player.getCurrentEquippedItem().getItem().getUnlocalizedName().equals(info.getWandID())))
				return;
			if (player.getCurrentEquippedItem().getItemDamage() != info.getWandDmg())
				return;
		}

		WorldPoint point = new WorldPoint(player.dimension, event.x, event.y, event.z);
		if (!APIRegistry.perms.checkPermission(new UserIdent(player), point, "fe.core.pos"))
		{
			OutputHandler.chatError(player,
					"You have insufficient permissions to do that. If you believe you received this message in error, please talk to a server admin.");
			return;
		}

		// left Click
		if (event.action.equals(PlayerInteractEvent.Action.LEFT_CLICK_BLOCK))
		{
			info.setPoint1(point);
			IChatComponent format = OutputHandler.createFromText("Pos1 set to " + event.x + ", " + event.y + ", " + event.z);
			player.addChatMessage(OutputHandler.colourize(format, EnumChatFormatting.DARK_PURPLE));
			event.setCanceled(true);
		}
		// right Click
		else if (event.action.equals(PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK))
		{
			info.setPoint2(point);
			IChatComponent format = OutputHandler.createFromText("Pos2 set to " + event.x + ", " + event.y + ", " + event.z);
			player.addChatMessage(OutputHandler.colourize(format, EnumChatFormatting.DARK_PURPLE));
			event.setCanceled(true);
		}
	}

}
