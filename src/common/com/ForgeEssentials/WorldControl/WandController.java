package com.ForgeEssentials.WorldControl;

import com.ForgeEssentials.PlayerInfo;
import com.ForgeEssentials.AreaSelector.Point;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Side;
import net.minecraft.src.EntityPlayer;
import net.minecraftforge.event.Event.Result;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public class WandController
{
	@ForgeSubscribe
	public void interact(PlayerInteractEvent event)
	{
		if (event.entityPlayer.getCurrentEquippedItem().itemID != WorldControlMain.wandID)
			return;

		if (event.action.equals(PlayerInteractEvent.Action.LEFT_CLICK_BLOCK))
			onLeftClick(event.entityPlayer, event.x, event.y, event.z, event.face);
		else if (event.action.equals(PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK))
			onRightClick(event.entityPlayer, event.x, event.y, event.z, event.face);
		else
			return;

		event.setResult(Result.DENY);
	}

	private void onLeftClick(EntityPlayer player, int x, int y, int z, int side)
	{
		PlayerInfo.getPlayerInfo(player.username).setPoint1(new Point(x, y, z));

		if (FMLCommonHandler.instance().getSide().equals(Side.CLIENT))
			player.addChatMessage("Pos1 set to: " + x + ", " + y + ", " + z);
	}

	private void onRightClick(EntityPlayer player, int x, int y, int z, int side)
	{
		PlayerInfo.getPlayerInfo(player.username).setPoint2(new Point(x, y, z));

		if (FMLCommonHandler.instance().getEffectiveSide().equals(Side.CLIENT))
			player.addChatMessage("Pos2 set to: " + x + ", " + y + ", " + z);
	}
}
