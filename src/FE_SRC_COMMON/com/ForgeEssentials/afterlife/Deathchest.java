package com.ForgeEssentials.afterlife;

import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet100OpenWindow;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import com.ForgeEssentials.api.data.ClassContainer;
import com.ForgeEssentials.api.data.DataStorageManager;
import com.ForgeEssentials.api.permissions.PermissionsAPI;
import com.ForgeEssentials.api.permissions.query.PermQueryPlayer;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.AreaSelector.WorldPoint;
import com.ForgeEssentials.util.events.PlayerBlockBreak;

import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

public class Deathchest
{
	/**
	 * This permission is needed to get the skull, Default = members.
	 */
	public static final String		PERMISSION_MAKE		= ModuleAfterlife.BASEPERM + ".deathchest.make";

	/**
	 * This is the permission that allows you to bypass the protection timer.
	 */
	public static final String		PERMISSION_BYPASS	= ModuleAfterlife.BASEPERM + ".deathchest.protectionBypass";

	public static boolean			enable;
	public static boolean			enableXP;
	public static boolean			enableFencePost;
	public static int				protectionTime;

	public HashMap<String, Grave>	gravemap			= new HashMap<String, Grave>();
	private ClassContainer			graveType			= new ClassContainer(Grave.class);

	public Deathchest()
	{
		TileEntity.addMapping(FEskullTe.class, "FESkull");
		MinecraftForge.EVENT_BUS.register(this);
		TickRegistry.registerScheduledTickHandler(new GraveProtectionTicker(this), Side.SERVER);
	}

	public void load()
	{
		for (Object obj : DataStorageManager.getReccomendedDriver().loadAllObjects(graveType))
		{
			Grave grave = (Grave) obj;
			gravemap.put(grave.point.toString(), grave);
		}
	}

	public void save()
	{
		for (Grave grave : gravemap.values())
		{
			DataStorageManager.getReccomendedDriver().saveObject(graveType, grave);
		}
	}

	@ForgeSubscribe
	public void handleDeath(PlayerDropsEvent e)
	{
		if (!enable)
			return;
		if (!PermissionsAPI.checkPermAllowed(new PermQueryPlayer(e.entityPlayer, PERMISSION_MAKE)))
			return;
		WorldPoint point = new WorldPoint(e.entityPlayer);
		World world = e.entityPlayer.worldObj;
		if (world.isRemote)
			return;

		if (world.getBlockMaterial(point.x, point.y, point.z).isReplaceable())
		{
			e.setCanceled(true);

			if (enableFencePost)
			{
				world.func_94575_c(point.x, point.y, point.z, Block.fence.blockID);
				point.y++;
			}
			new Grave(point, e.entityPlayer, e.drops, this);

			world.setBlockAndMetadataWithNotify(point.x, point.y, point.z, Block.skull.blockID, 1, 1);
			FEskullTe te = new FEskullTe();
			te.setSkullType(3, e.entityPlayer.username);
			world.setBlockTileEntity(point.x, point.y, point.z, te);
		}
	}

	@ForgeSubscribe
	public void handleClick(PlayerInteractEvent e)
	{
		if (e.entity.worldObj.isRemote)
			return;

		if (e.action != PlayerInteractEvent.Action.RIGHT_CLICK_AIR)
		{
			WorldPoint point = new WorldPoint(e.entity.worldObj, e.x, e.y, e.z);
			if (gravemap.containsKey(point.toString()))
			{
				Grave grave = gravemap.get(point.toString());
				if (e.entity.worldObj.getBlockId(e.x, e.y, e.z) == Block.skull.blockID)
				{
					if (!grave.canOpen(e.entityPlayer))
					{
						OutputHandler.chatWarning(e.entityPlayer, Localization.get("message.afterlife.protectionEnabled"));
						e.setCanceled(true);
					}
					else
					{
						EntityPlayerMP player = (EntityPlayerMP) e.entityPlayer;

						if (player.openContainer != player.inventoryContainer)
						{
							player.closeScreen();
						}
						player.incrementWindowID();

						InventoryGrave invGrave = new InventoryGrave(grave);
						player.playerNetServerHandler.sendPacketToPlayer(new Packet100OpenWindow(player.currentWindowId, 0, invGrave.getInvName(), invGrave.getSizeInventory(), true));
						player.openContainer = new ContainerChest(player.inventory, invGrave);
						player.openContainer.windowId = player.currentWindowId;
						player.openContainer.addCraftingToCrafters(player);
						e.setCanceled(true);
					}
				}
				else
				{
					removeGrave(grave, true);
				}
			}
		}
	}

	@ForgeSubscribe
	public void mineGrave(PlayerBlockBreak e)
	{
		if (e.world.isRemote)
			return;
		WorldPoint point = new WorldPoint(e.world, e.blockX, e.blockY, e.blockZ);
		if (gravemap.containsKey(point.toString()))
		{
			Grave grave = gravemap.get(point.toString());
			removeGrave(grave, true);
		}
	}

	public void removeGrave(Grave grave, boolean mined)
	{
		if (grave == null)
			return;
		DataStorageManager.getReccomendedDriver().deleteObject(graveType, grave.point.toString());
		gravemap.remove(grave.point.toString());
		if (mined)
		{
			for (ItemStack is : grave.inv)
			{
				try
				{
					EntityItem entity = new EntityItem(DimensionManager.getWorld(grave.point.dim), grave.point.x, grave.point.y, grave.point.z, is);
					DimensionManager.getWorld(grave.point.dim).spawnEntityInWorld(entity);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}

			for (int i = 0; i < 10; i++)
			{
				try
				{
					int xp = grave.xp / 10;
					if (xp == 0)
					{
						break;
					}
					EntityXPOrb entity = new EntityXPOrb(DimensionManager.getWorld(grave.point.dim), grave.point.x, grave.point.y, grave.point.z, xp);
					DimensionManager.getWorld(grave.point.dim).spawnEntityInWorld(entity);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}
}
