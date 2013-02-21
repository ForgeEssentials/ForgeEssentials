package com.ForgeEssentials.WorldControl;

//Depreciated
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.event.EventPriority;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import com.ForgeEssentials.api.permissions.PermissionsAPI;
import com.ForgeEssentials.api.permissions.query.PermQueryPlayerArea;
import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.util.FEChatFormatCodes;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.AreaSelector.Point;

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
		PlayerInfo info = PlayerInfo.getPlayerInfo(player);

		int id = player.getCurrentEquippedItem() == null ? 0 : player.getCurrentEquippedItem().itemID;
		int damage = 0;
		if (id != 0 && player.getCurrentEquippedItem().getHasSubtypes())
		{
			damage = player.getCurrentEquippedItem().getItemDamage();
		}

		if (id != info.wandID || !info.wandEnabled || (info.wandDmg>-1?damage != info.wandDmg:false))
		{
			return; // wand does not activate
		}

		Point point = new Point(event.x, event.y, event.z);

		if (!PermissionsAPI.checkPermAllowed(new PermQueryPlayerArea(player, "WorldControl.commands.pos", point)))
		{
			OutputHandler.chatError(player, Localization.get(Localization.ERROR_PERMDENIED));
			return;
		}
		
		int x = event.x;
		int y = event.y;
		int z = event.z;
		
		MovingObjectPosition mouseOverBlock = FunctionHelper.rayTrace(512F, player); // Gets moused over block up to 512 blocks away.
		if(mouseOverBlock != null) { // No block is found within the distance specified, 512 is chosen to eleiminate lag.
			x = mouseOverBlock.blockX;
			y = mouseOverBlock.blockY;
			z = mouseOverBlock.blockZ;
		}

		// left Click
		if (event.action.equals(PlayerInteractEvent.Action.LEFT_CLICK_BLOCK))
		{
			info.setPoint1(point);
			player.addChatMessage(FEChatFormatCodes.PURPLE + "Pos1 set to " + x + ", " + y + ", " + z);
			event.setCanceled(true);
		}
		// right Click
		else if (event.action.equals(PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK))
		{
			info.setPoint2(point);
			player.addChatMessage(FEChatFormatCodes.PURPLE + "Pos2 set to " + x + ", " + y + ", " + z);
			event.setCanceled(true);
		}
	}
}
