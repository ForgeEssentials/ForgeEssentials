package com.ForgeEssentials.WorldControl;

import com.ForgeEssentials.AreaSelector.Point;
import com.ForgeEssentials.core.OutputHandler;
import com.ForgeEssentials.core.PlayerInfo;

import cpw.mods.fml.common.FMLCommonHandler;

import net.minecraft.src.EntityPlayer;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public class WandController
{
	@ForgeSubscribe
	public void playerInteractEvent(PlayerInteractEvent event)
	{
		// only server events please.
		if (FMLCommonHandler.instance().getEffectiveSide().isClient())
			return;
		
		// get info now rather than later
		EntityPlayer player = event.entityPlayer;
		PlayerInfo info = PlayerInfo.getPlayerInfo(player.username);
		
		if (player.getCurrentEquippedItem().itemID != info.wandID || !info.wandEnabled)
			return;  // wand does not activate
		
		// left Click
		if (event.action.equals(PlayerInteractEvent.Action.LEFT_CLICK_BLOCK))
		{
			info.setPoint1(new Point(event.x, event.y, event.z));
			player.addChatMessage(OutputHandler.PURPLE+"Pos1 set to "+event.x+", "+event.y+", "+event.z);
			event.setCanceled(true);
		}
		// right Click
		else if(event.action.equals(PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK))
		{
			info.setPoint2(new Point(event.x, event.y, event.z));
			player.addChatMessage(OutputHandler.PURPLE+"Pos2 set to "+event.x+", "+event.y+", "+event.z);
			event.setCanceled(true);
		}
	}
}
