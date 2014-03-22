package com.forgeessentials.core.commands.selections;

//Depreciated
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.event.EventPriority;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.query.PermQueryPlayerArea;
import com.forgeessentials.core.PlayerInfo;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.AreaSelector.Point;

import cpw.mods.fml.common.FMLCommonHandler;

public class WandController
{
	@ForgeSubscribe(priority = EventPriority.HIGHEST)
	public void playerInteractEvent(PlayerInteractEvent event)
	{
		// only server events please.
		if (FMLCommonHandler.instance().getEffectiveSide().isClient())
			return;

		// get info now rather than later
		EntityPlayer player = event.entityPlayer;
		PlayerInfo info = PlayerInfo.getPlayerInfo(player.username);

		int id = player.getCurrentEquippedItem() == null ? 0 : player.getCurrentEquippedItem().itemID;
		int damage = 0;
		if (id != 0 && player.getCurrentEquippedItem().getHasSubtypes())
		{
			damage = player.getCurrentEquippedItem().getItemDamage();
		}

		if (id != info.wandID || !info.wandEnabled || damage != info.wandDmg)
			return; // wand does not activate

		Point point = new Point(event.x, event.y, event.z);

		if (!APIRegistry.perms.checkPermAllowed(new PermQueryPlayerArea(player, "ForgeEssentials.CoreCommands.select.pos", point)))
		{
			OutputHandler.chatError(player, "You have insufficient permission to do that. If you believe you received this message in error, please talk to a server admin.");
			return;
		}

		// left Click
		if (event.action.equals(PlayerInteractEvent.Action.LEFT_CLICK_BLOCK))
		{
			info.setPoint1(point);
			player.addChatMessage(EnumChatFormatting.DARK_PURPLE + "Pos1 set to " + event.x + ", " + event.y + ", " + event.z);
			event.setCanceled(true);
		}
		// right Click
		else if (event.action.equals(PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK))
		{
			info.setPoint2(point);
			player.addChatMessage(EnumChatFormatting.DARK_PURPLE + "Pos2 set to " + event.x + ", " + event.y + ", " + event.z);
			event.setCanceled(true);
		}
	}
}
