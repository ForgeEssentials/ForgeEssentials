package com.forgeessentials.protection;

import static cpw.mods.fml.common.eventhandler.Event.Result.ALLOW;
import static cpw.mods.fml.common.eventhandler.Event.Result.DENY;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.CheckSpawn;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.SpecialSpawn;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.permissions.PermissionsManager;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.events.PlayerBlockPlace;
import com.forgeessentials.util.selections.WorldPoint;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class ProtectionEventHandler {
	@SubscribeEvent(priority = EventPriority.LOW)
	public void playerAttack(AttackEntityEvent e)
	{
		if (e.target == null)
		{
			return;
		}

		if (e.target instanceof EntityPlayer)
		{
			// Stops players from hitting each other.

			boolean sourceB = !PermissionsManager.checkPerm(e.entityPlayer, ModuleProtection.PERM_PVP);

			if (sourceB)
			{
				e.setCanceled(true);
				return;
			}

			boolean receiverB = !PermissionsManager.checkPerm((EntityPlayer) e.target, ModuleProtection.PERM_PVP);

			if (sourceB || receiverB)
			{
				e.setCanceled(true);
			}

		}
		else
		{
			// Stops players from hitting entities.

			boolean result = PermissionsManager.checkPerm(e.entityPlayer, ModuleProtection.PERM_OVERRIDE);
			if (!result)
			{
				result = PermissionsManager.checkPerm(e.entityPlayer, ModuleProtection.PERM_INTERACT_ENTITY);
			}

			e.setCanceled(!result);
		}
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public void damage(LivingHurtEvent e)
	{
		// do nothing if the source isnt a living thing.
		// actually... if its ANY entity.
		if (e.source.getEntity() == null || !(e.source.getEntity() instanceof EntityLiving))
		{
			return;
		}

		EntityLivingBase source = (EntityLivingBase) e.source.getEntity();

		boolean sourcePlayer = e.source.getEntity() instanceof EntityPlayer;
		boolean targetPlayer = e.entityLiving instanceof EntityPlayer;

		if (e.entityLiving == null)
		{
			return;
		}

		if (sourcePlayer && targetPlayer)
		{
			// PVP checks
			boolean sourceB = !APIRegistry.perms.checkPermission((EntityPlayer) e.entityLiving, new WorldPoint(e.source.getEntity()),
					ModuleProtection.PERM_PVP);
			if (sourceB)
			{
				e.setCanceled(true);
				return;
			}

			boolean receiverB = !PermissionsManager.checkPerm((EntityPlayer) e.source.getEntity(), ModuleProtection.PERM_PVP);
			if (sourceB || receiverB)
			{
				e.setCanceled(true);
			}
		}
		else if (sourcePlayer)
		{
			// stop players hitting animals.
			boolean result = APIRegistry.perms.checkPermission((EntityPlayer) source, new WorldPoint(e.entityLiving),
					ModuleProtection.PERM_OVERRIDE);
			if (!result)
			{
				result = APIRegistry.perms.checkPermission((EntityPlayer) source, new WorldPoint(e.entityLiving),
						ModuleProtection.PERM_INTERACT_ENTITY);
			}
			e.setCanceled(!result);
		}
		else if (targetPlayer)
		{
			// stop people from hitting entites.
			boolean result = APIRegistry.perms.checkPermission((EntityPlayer) e.entityLiving, new WorldPoint(e.entityLiving),
					ModuleProtection.PERM_OVERRIDE);
			if (!result)
			{
				result = APIRegistry.perms.checkPermission((EntityPlayer) e.entityLiving, new WorldPoint(e.entityLiving),
						ModuleProtection.PERM_INTERACT_ENTITY);
			}
			e.setCanceled(!result);
		}
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public void breakEvent(BreakEvent e)
	{
		if (FMLCommonHandler.instance().getEffectiveSide().isClient())
		{
			return;
		}

		WorldPoint point = new WorldPoint(e.getPlayer().dimension, e.x, e.y, e.z);
		boolean overall = APIRegistry.perms.checkPermission(e.getPlayer(), point, ModuleProtection.PERM_OVERRIDE);
		boolean breaks = APIRegistry.perms.checkPermission(e.getPlayer(), point, ModuleProtection.PERM_EDITS);
		if (!overall)
		{
			if (!breaks)
				e.setCanceled(true);
		}

	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public void placeEvent(PlayerBlockPlace e)
	{
		if (FMLCommonHandler.instance().getEffectiveSide().isClient())
		{
			return;
		}

		WorldPoint point = new WorldPoint(e.getPlayer().dimension, e.getBlockX(), e.getBlockY(), e.getBlockZ());
		boolean overall = APIRegistry.perms.checkPermission(e.getPlayer(), point, ModuleProtection.PERM_OVERRIDE);
		boolean breaks = APIRegistry.perms.checkPermission(e.getPlayer(), point, ModuleProtection.PERM_EDITS);

		if (!overall)
		{
			if (!breaks)
				e.setCanceled(true);
		}
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public void playerInteractEventItemUse(PlayerInteractEvent e)
	{
		if (e.action.equals(PlayerInteractEvent.Action.LEFT_CLICK_BLOCK))
		{
			return;
		}

		// item check
		ItemStack stack = e.entityPlayer.getCurrentEquippedItem();
		if (stack == null)
		{
			return;
		}

		WorldPoint point = new WorldPoint(e.entityPlayer);
		if (e.action.equals(PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK))
		{
			if (stack.getItem() instanceof ItemBlock)
			{
				// calculate offsets.
				ForgeDirection dir = ForgeDirection.getOrientation(e.face);
				int x = e.x + dir.offsetX;
				int y = e.y + dir.offsetY;
				int z = e.z + dir.offsetZ;

				point = new WorldPoint(e.entityPlayer.dimension, x, y, z);
			}
			else
			{
				point = new WorldPoint(e.entityPlayer.dimension, e.x, e.y, e.z);
			}
		}

		boolean result = APIRegistry.perms.checkPermission(e.entityPlayer, point, ModuleProtection.PERM_OVERRIDE);
		if (!result)
		{
			String name = stack.getUnlocalizedName();
			name = ModuleProtection.PERM_ITEM_USE + "." + name;
			name = name + "." + stack.getItemDamage();

			result = APIRegistry.perms.checkPermission(e.entityPlayer, point, name);
		}

		if (result)
		{
			e.useItem = ALLOW;
		}
		else
		{
			e.useItem = DENY;
		}
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public void playerInteractEventBlockUse(PlayerInteractEvent e)
	{
		if (FMLCommonHandler.instance().getEffectiveSide().isClient())
		{
			return;
		}

		if (e.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK)
		{
			WorldPoint point = new WorldPoint(e.entityPlayer.dimension, e.x, e.y, e.z);

			boolean result = APIRegistry.perms.checkPermission(e.entityPlayer, point, ModuleProtection.PERM_OVERRIDE);
			if (!result)
			{
				// check block usage perm
				result = APIRegistry.perms.checkPermission(e.entityPlayer, point, ModuleProtection.PERM_INTERACT_BLOCK);
			}

			if (result)
			{
				e.useBlock = ALLOW;
			}
			else
			{
				e.useBlock = DENY;
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public void entityInteractEvent(EntityInteractEvent e)
	{
		if (FMLCommonHandler.instance().getEffectiveSide().isClient())
		{
			return;
		}

		WorldPoint point = new WorldPoint(e.entityPlayer.dimension, (int) e.target.posX, (int) e.target.posY, (int) e.target.posZ);

		boolean result = APIRegistry.perms.checkPermission(e.entityPlayer, point, ModuleProtection.PERM_OVERRIDE);
		if (!result)
		{
			result = APIRegistry.perms.checkPermission(e.entityPlayer, point, ModuleProtection.PERM_INTERACT_ENTITY);
		}

		e.setCanceled(!result);
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public void handleSpawn(CheckSpawn e)
	{
		// ignore players
		if (!ModuleProtection.enableMobSpawns || e.entityLiving instanceof EntityPlayer)
		{
			return;
		}

		WorldPoint point = new WorldPoint(e.entityLiving);
		String mobID = EntityList.getEntityString(e.entity);

		if (!APIRegistry.perms.checkPermission(null, point, ModuleProtection.PERM_MOB_SPAWN_NATURAL + "." + mobID))
		{
			e.setResult(Result.DENY);
			OutputHandler.debug(mobID + " : DENIED");
		}
		else
		{
			OutputHandler.debug(mobID + " : ALLOWED");
		}
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public void handleSpawn(SpecialSpawn e)
	{
		// ignore players
		if (!ModuleProtection.enableMobSpawns || e.entityLiving instanceof EntityPlayer)
		{
			return;
		}

		WorldPoint point = new WorldPoint(e.entityLiving);
		String mobID = EntityList.getEntityString(e.entity);

		if (!APIRegistry.perms.checkPermission(null, point, ModuleProtection.PERM_MOB_SPAWN_FORCED + "." + mobID))
		{
			e.setResult(Result.DENY);
		}
	}

	/*
	 * @SubscribeEvent public void manageZoneBannedItems(PlayerChangedZone e) { for (ItemStack returned :
	 * PlayerInfo.getPlayerInfo(e.entityPlayer).getHiddenItems()) { e.entityPlayer.inventory.addItemStackToInventory(returned); }
	 * 
	 * AdditionalZoneData bi = ModuleProtection.itemsList.get(e.afterZone); List<String> biList = bi.getBannedItems(); for (ListIterator<String> iter =
	 * biList.listIterator(); iter.hasNext(); ) { String element = iter.next(); String[] split = element.split(":"); int id = Integer.parseInt(split[0]); int
	 * meta = Integer.parseInt(split[1]); ItemStack is = new ItemStack(element, 0, meta);
	 * 
	 * if (e.entityPlayer.inventory.hasItemStack(is)) { PlayerInfo.getPlayerInfo(e.entityPlayer).getHiddenItems().add(is); } } }
	 * 
	 * @SubscribeEvent public void manageCrafting(PlayerEvent.ItemCraftedEvent e) { }
	 * 
	 * @SubscribeEvent public void manageZoneGameModes(PlayerChangedZone e) { String val = APIRegistry.perms.getPermissionPropForPlayer(e.entityPlayer.username,
	 * e.afterZone.getName(), ModuleProtection.PERMPROP_ZONE_GAMEMODE); e.entityPlayer.setGameType(EnumGameType.getByID(Integer.parseInt(val)));
	 * System.out.println("yoohoo");
	 * 
	 * }
	 */

}
