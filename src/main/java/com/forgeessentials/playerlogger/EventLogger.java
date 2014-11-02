package com.forgeessentials.playerlogger;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.util.events.ServerEventHandler;
import com.forgeessentials.playerlogger.types.BlockChangeType;
import com.forgeessentials.playerlogger.types.BlockChangeType.blockChangeLogCategory;
import com.forgeessentials.playerlogger.types.CommandType;
import com.forgeessentials.playerlogger.types.PlayerTrackerType;
import com.forgeessentials.util.UserIdent;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.relauncher.Side;

public class EventLogger extends ServerEventHandler {
	public static boolean logPlayerChangedDimension = true;
	public static boolean logPlayerRespawn = true;
	public static boolean logItemUsage = true;
	public static boolean logBlockChanges = true;
	public static boolean logPlayerLoginLogout = true;
	public static boolean logCommands_Player = true;
	public static boolean logCommands_Block = true;
	public static boolean logCommands_rest = true;
    public static boolean logDeathDrops = true;
	public static boolean BlockChange_WhiteList_Use = false;
	public static ArrayList<Integer> BlockChange_WhiteList = new ArrayList<Integer>();
	public static ArrayList<Integer> BlockChange_BlackList = new ArrayList<Integer>();
	public static List<String> exempt_players = new ArrayList<String>();
	public static List<String> exempt_groups = new ArrayList<String>();
	public Side side = FMLCommonHandler.instance().getEffectiveSide();

	public EventLogger()
	{
		MinecraftForge.EVENT_BUS.register(this);
	}

	public static boolean exempt(EntityPlayer player)
	{
		for (String un : exempt_players)
		{
			if (un.replaceAll("\"", "").equalsIgnoreCase(player.getPersistentID().toString()))
			{
				return true;
			}
		}
		for (String group : APIRegistry.perms.getPlayerGroups(new UserIdent(player)))
		{
			if (exempt_groups.contains(group))
			{
				return true;
			}
		}
		return false;
	}

	@SubscribeEvent
	public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent e)
	{
		if (logPlayerLoginLogout && side.isServer())
		{
			if (exempt(e.player))
			{
				return;
			}
			new PlayerTrackerType(PlayerTrackerType.Types.Login, e.player, "");
		}
	}

	@SubscribeEvent
	public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent e)
	{
		if (logPlayerLoginLogout && side.isServer())
		{
			if (exempt(e.player))
			{
				return;
			}
			new PlayerTrackerType(PlayerTrackerType.Types.Logout, e.player, "");
		}
	}

	@SubscribeEvent
	public void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent e)
	{
		if (logPlayerChangedDimension && side.isServer())
		{
			if (exempt(e.player))
			{
				return;
			}
			new PlayerTrackerType(PlayerTrackerType.Types.ChangedDim, e.player, "");
		}
	}

	@SubscribeEvent
	public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent e)
	{
		if (logPlayerRespawn && side.isServer())
		{
			if (exempt(e.player))
			{
				return;
			}
			new PlayerTrackerType(PlayerTrackerType.Types.Respawn, e.player, "");
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void command(CommandEvent e)
	{
		if (logCommands_Player && !e.isCanceled() && e.sender instanceof EntityPlayer && side.isServer())
		{
			if (exempt((EntityPlayer) e.sender))
			{
				return;
			}
			new CommandType(e.sender.getCommandSenderName(), getCommand(e));
			return;
		}
		if (logCommands_Block && !e.isCanceled() && e.sender instanceof TileEntityCommandBlock && side.isServer())
		{
			new CommandType(e.sender.getCommandSenderName(), getCommand(e));
			return;
		}
		if (logCommands_rest && !e.isCanceled() && side.isServer())
		{
			new CommandType(e.sender.getCommandSenderName(), getCommand(e));
			return;
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void playerBlockBreak(BreakEvent e)
	{
		if (logBlockChanges && !e.isCanceled() && side.isServer())
		{
			if (exempt(e.getPlayer()))
			{
				return;
			}

			new BlockChangeType(blockChangeLogCategory.broke, e.getPlayer(), e.block.getUnlocalizedName() + ":" + e.blockMetadata, e.x, e.y, e.z,
					e.world.getTileEntity(e.x, e.y, e.z));
		}
	}

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void playerBlockPlace(BlockEvent.PlaceEvent e)
    {
        if (logBlockChanges && !e.isCanceled() && side.isServer())
        {
            if (exempt(e.player))
            {
                return;
            }
            if (BlockChange_WhiteList_Use && !BlockChange_WhiteList.contains(e.player.dimension))
            {
                return;
            }
            if (BlockChange_BlackList.contains(e.player.dimension) && !BlockChange_WhiteList.contains(e.player.dimension))
            {
                return;
            }

            String block = "";
            if (e.player.inventory.getCurrentItem() != null)
            {
                block = e.player.inventory.getCurrentItem().getUnlocalizedName() + ":" + e.player.inventory.getCurrentItem().getItemDamage();
            }

            new BlockChangeType(blockChangeLogCategory.placed, e.player, block, e.x, e.y, e.z, null);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void playerBlockPlace(BlockEvent.MultiPlaceEvent e)
    {
        if (logBlockChanges && !e.isCanceled() && side.isServer())
        {
            if (exempt(e.player))
            {
                return;
            }
            if (BlockChange_WhiteList_Use && !BlockChange_WhiteList.contains(e.player.dimension))
            {
                return;
            }
            if (BlockChange_BlackList.contains(e.player.dimension) && !BlockChange_WhiteList.contains(e.player.dimension))
            {
                return;
            }

            String block = "";
            if (e.player.inventory.getCurrentItem() != null)
            {
                block = e.player.inventory.getCurrentItem().getUnlocalizedName() + ":" + e.player.inventory.getCurrentItem().getItemDamage();
            }

            for (BlockSnapshot b : e.getReplacedBlockSnapshots())
            {
                new BlockChangeType(blockChangeLogCategory.placed, e.player, block, b.x, b.y, b.z, null);
            }

        }
    }



	/*
	 * Needed background stuff
	 */

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void playerInteractEvent(PlayerInteractEvent e)
	{
		if (e.action == Action.RIGHT_CLICK_BLOCK)
		{
			if (exempt(e.entityPlayer))
			{
				return;
			}
			if (BlockChange_WhiteList_Use && !BlockChange_WhiteList.contains(e.entityPlayer.dimension))
			{
				return;
			}
			if (BlockChange_BlackList.contains(e.entityPlayer.dimension) && !BlockChange_WhiteList.contains(e.entityPlayer.dimension))
			{
				return;
			}

			new BlockChangeType(blockChangeLogCategory.interact, e.entityPlayer, e.entity.worldObj.getBlock(e.x, e.y, e.z).getUnlocalizedName()
					+ ":" + e.entity.worldObj.getBlockMetadata(e.x, e.y, e.z), e.x, e.y, e.z, e.entity.worldObj.getTileEntity(
					e.x, e.y, e.z));
		}
	}

	public String getCommand(CommandEvent e)
	{
		String command = "/" + e.command.getCommandName();
		for (String str : e.parameters)
		{
			command = command + " " + str;
		}
		return command;
	}
}
