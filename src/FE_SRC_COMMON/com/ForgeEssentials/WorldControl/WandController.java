package com.ForgeEssentials.WorldControl;

//Depreciated
import java.util.EnumSet;
import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
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
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class WandController implements ITickHandler
{
	public static WandController instance = new WandController();
	
	@ForgeSubscribe(priority = EventPriority.HIGHEST)
	public void playerInteractEvent(PlayerInteractEvent event)
	{
		// only server events please.
		if (FMLCommonHandler.instance().getEffectiveSide().isClient()) return;
		EntityPlayer player = event.entityPlayer;
		if (event.action.equals(PlayerInteractEvent.Action.LEFT_CLICK_BLOCK))
		{
			event.setCanceled(leftClick(player, event.x, event.y, event.z, event.face, true));
		}
		// right Click
		else if (event.action.equals(PlayerInteractEvent.Action.RIGHT_CLICK_AIR))
		{
			MovingObjectPosition mouseOverBlock = FunctionHelper.getPlayerLookingSpot(player, false);
			if(mouseOverBlock!=null) {
				int x = mouseOverBlock.blockX;
				int y = mouseOverBlock.blockY;
				int z = mouseOverBlock.blockZ;
				int side = mouseOverBlock.sideHit;
				event.setCanceled(rightClick(player, x, y, z, side, player.getDistance(x, y, z)<4.8F?true:false));
			}
		}
		if (event.action.equals(PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK))
		{
			if(!delay.containsKey(player.username))delay.put(player.username, 0);
			delay.put(player.username, 1);
		}
	}
	
	private static HashMap<String, Integer> delay = new HashMap<String, Integer>();
	
	public static void swingItem(EntityPlayer player) {
		if(!delay.containsKey(player.username))delay.put(player.username, 0);
		if(delay.get(player.username)>0)return;
		MovingObjectPosition mouseOverBlock = FunctionHelper.getPlayerLookingSpot(player, false); // Gets moused over block up to 512 blocks away.
		if(mouseOverBlock != null) { // No block is found within the distance specified
			int x = mouseOverBlock.blockX;
			int y = mouseOverBlock.blockY;
			int z = mouseOverBlock.blockZ;
			int side = mouseOverBlock.sideHit;
			if(player.getDistance(x, y, z)>=4.8F)leftClick(player, x, y, z, side, false);
		}

	}
	
	public static boolean rightClick(EntityPlayer player, int x, int y, int z, int side, boolean inReach) {
		PlayerInfo info = PlayerInfo.getPlayerInfo(player);
		Point pt = new Point(x, y, z);
		int ix = side==4?x-1:side==5?x+1:x;
		int iy = side==0?y-1:side==1?y+1:y;
		int iz = side==2?z-1:side==3?z+1:z;
		Point ipt = new Point(ix, iy, iz);
		ItemStack item = player.getCurrentEquippedItem();
		if (!PermissionsAPI.checkPermAllowed(new PermQueryPlayerArea(player, "WorldControl.commands.pos", new Point(x, y, z))))
		{
			//OutputHandler.chatError(player, Localization.get(Localization.ERROR_PERMDENIED));
			return false;
		}
		if(item!=null) {
			if (item.itemID == info.wandID && info.wandEnabled && (info.wandDmg>-1?item.getItemDamage() == info.wandDmg:true))
			{
				info.setPoint2(new Point(x, y, z));
				player.addChatMessage(FEChatFormatCodes.PURPLE + "Pos2 set to " + x + ", " + y + ", " + z);
				return true;
			}else if(item.getItem() instanceof ItemBlock&&!inReach) {
				ItemBlock block = (ItemBlock) item.getItem();
				if(canPlaceItemBlockOnSide(player.worldObj, x, y, z, side, player, item, block) && player.capabilities.isCreativeMode) {
					if(block.placeBlockAt(item, player, player.worldObj, ix, iy, iz, side, x, y, z, item.getItemDamage())){
						if(!player.capabilities.isCreativeMode) {
							player.inventory.consumeInventoryItem(item.itemID);
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	public static boolean leftClick(EntityPlayer player, int x, int y, int z, int side, boolean inReach) {
		PlayerInfo info = PlayerInfo.getPlayerInfo(player);
		Point pt = new Point(x, y, z);
		int ix = side==4?x-1:side==5?x+1:x;
		int iy = side==0?y-1:side==1?y+1:y;
		int iz = side==2?z-1:side==3?z+1:z;
		Point ipt = new Point(ix, iy, iz);
		ItemStack item = player.getCurrentEquippedItem();
		if (!PermissionsAPI.checkPermAllowed(new PermQueryPlayerArea(player, "WorldControl.commands.pos", new Point(x, y, z))))
		{
			//OutputHandler.chatError(player, Localization.get(Localization.ERROR_PERMDENIED));
			return false;
		}
		if(item!=null) {
			if (item.itemID == info.wandID && info.wandEnabled && (info.wandDmg>-1?item.getItemDamage() == info.wandDmg:true))
			{
				info.setPoint1(new Point(x, y, z));
				player.addChatMessage(FEChatFormatCodes.PURPLE + "Pos1 set to " + x + ", " + y + ", " + z);
				return true;
			}else if(player.capabilities.isCreativeMode&&!inReach){
				player.worldObj.setBlockWithNotify(x, y, z, 0);
				return true;
			}
		}
		return false;
	}
	
	public static boolean canPlaceItemBlockOnSide(World par1World, int par2, int par3, int par4, int par5, EntityPlayer par6EntityPlayer, ItemStack par7ItemStack, ItemBlock ib)
    {
        int var8 = par1World.getBlockId(par2, par3, par4);

        if (var8 == Block.snow.blockID)
        {
            par5 = 1;
        }
        else if (var8 != Block.vine.blockID && var8 != Block.tallGrass.blockID && var8 != Block.deadBush.blockID
                && (Block.blocksList[var8] == null || !Block.blocksList[var8].isBlockReplaceable(par1World, par2, par3, par4)))
        {
            if (par5 == 0)
            {
                --par3;
            }

            if (par5 == 1)
            {
                ++par3;
            }

            if (par5 == 2)
            {
                --par4;
            }

            if (par5 == 3)
            {
                ++par4;
            }

            if (par5 == 4)
            {
                --par2;
            }

            if (par5 == 5)
            {
                ++par2;
            }
        }

        return par1World.canPlaceEntityOnSide(ib.getBlockID(), par2, par3, par4, false, par5, (Entity)null);
    }
	
	private static HashMap<String, Boolean> isSwinging = new HashMap<String, Boolean>();

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData)
	{
		if (FMLCommonHandler.instance().getEffectiveSide().isClient()) return;
		EntityPlayer player = (EntityPlayer)tickData[0];
		if(!delay.containsKey(player.username))delay.put(player.username, 0);
		if(delay.get(player.username)>0)delay.put(player.username, delay.get(player.username)-1);
		if(!isSwinging.containsKey(player.username))isSwinging.put(player.username, false);
		if(player.isSwingInProgress&&!isSwinging.get(player.username)) {
			isSwinging.put(player.username, true);
			swingItem(player);
		}else if(!player.isSwingInProgress&&isSwinging.get(player.username)) {
			isSwinging.put(player.username, false);
		}
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData)
	{
		
	}

	@Override
	public EnumSet<TickType> ticks()
	{
		return EnumSet.of(TickType.PLAYER);
	}

	@Override
	public String getLabel()
	{
		return null;
	}
}
