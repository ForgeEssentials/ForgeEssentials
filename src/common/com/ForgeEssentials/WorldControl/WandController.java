package com.ForgeEssentials.WorldControl;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Side;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.WorldClient;
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
			onRightClick(event.entityPlayer, event.x, event.y, event.z, event.face);
		else if (event.action.equals(PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK))
			onLeftClick(event.entityPlayer, event.x, event.y, event.z, event.face);
	}
	
	private void onLeftClick(EntityPlayer player, int x, int y, int z, int side)
	{
		if (player.getCurrentEquippedItem().itemID != WorldControlMain.wandID)
			return;
		
		FunctionHandler.instance.point1X.put(player.username, x);
		FunctionHandler.instance.point1Y.put(player.username, y);
		FunctionHandler.instance.point1Z.put(player.username, z);
		
		if (FMLCommonHandler.instance().getEffectiveSide().equals(Side.CLIENT))
			player.addChatMessage("Pos1 set to: "+FunctionHandler.instance.point1X.get(player.username)+", "+FunctionHandler.instance.point1Y.get(player.username)+", "+FunctionHandler.instance.point1Z.get(player.username));
	}
	
	private void onRightClick(EntityPlayer player, int x, int y, int z, int side)
	{
		if (player.getCurrentEquippedItem().itemID != WorldControlMain.wandID)
			return;
		
		FunctionHandler.instance.point2X.put(player.username, x);
		FunctionHandler.instance.point2Y.put(player.username, y);
		FunctionHandler.instance.point2Z.put(player.username, z);
		
		if (FMLCommonHandler.instance().getEffectiveSide().equals(Side.CLIENT))
			player.addChatMessage("Pos2 set to: "+FunctionHandler.instance.point2X.get(player.username)+", "+FunctionHandler.instance.point2Y.get(player.username)+", "+FunctionHandler.instance.point2Z.get(player.username));
	}
}
