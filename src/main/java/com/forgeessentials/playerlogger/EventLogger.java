package com.forgeessentials.playerlogger;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.Group;
import com.forgeessentials.playerlogger.network.PacketPlayerLogger;
import com.forgeessentials.playerlogger.types.blockChangeLog;
import com.forgeessentials.playerlogger.types.blockChangeLog.blockChangeLogCategory;
import com.forgeessentials.playerlogger.types.commandLog;
import com.forgeessentials.playerlogger.types.playerTrackerLog;
import com.forgeessentials.playerlogger.types.playerTrackerLog.playerTrackerLogCategory;
import com.forgeessentials.util.events.PlayerBlockPlace;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CommandEvent;
import cpw.mods.fml.common.eventhandler.EventPriority;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;

import java.util.ArrayList;
import java.util.List;

public class EventLogger {
    public Side side = FMLCommonHandler.instance().getEffectiveSide();

    public EventLogger()
    {
        MinecraftForge.EVENT_BUS.register(this);
        FMLCommonHandler.instance().bus().register(this);
    }

    public static boolean logPlayerChangedDimension = true;
    public static boolean logPlayerRespawn = true;
    public static boolean logItemUsage = true;
    public static boolean logBlockChanges = true;
    public static boolean logPlayerLoginLogout = true;

    public static boolean logCommands_Player = true;
    public static boolean logCommands_Block = true;
    public static boolean logCommands_rest = true;
    public static boolean BlockChange_WhiteList_Use = false;
    public static ArrayList<Integer> BlockChange_WhiteList = new ArrayList<Integer>();
    public static ArrayList<Integer> BlockChange_BlackList = new ArrayList<Integer>();
    public static List<String> exempt_players = new ArrayList<String>();
    public static List<String> exempt_groups = new ArrayList<String>();

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent e)
    {
        PacketDispatcher.sendPacketToPlayer(new PacketPlayerLogger(e.player).getPayload(), (Player) player);
        if (logPlayerLoginLogout && side.isServer())
        {
            if (exempt(e.player))
            {
                return;
            }
            new playerTrackerLog(playerTrackerLogCategory.Login, e.player, "");
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
            new playerTrackerLog(playerTrackerLogCategory.Logout, e.player, "");
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
            new playerTrackerLog(playerTrackerLogCategory.ChangedDim, e.player, "");
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
            new playerTrackerLog(playerTrackerLogCategory.Respawn, e.player, "");
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
            new commandLog(e.sender.getCommandSenderName(), getCommand(e));
            return;
        }
        if (logCommands_Block && !e.isCanceled() && e.sender instanceof TileEntityCommandBlock && side.isServer())
        {
            new commandLog(e.sender.getCommandSenderName(), getCommand(e));
            return;
        }
        if (logCommands_rest && !e.isCanceled() && side.isServer())
        {
            new commandLog(e.sender.getCommandSenderName(), getCommand(e));
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

            new blockChangeLog(blockChangeLogCategory.broke, e.getPlayer(), e.block.blockID + ":" + e.blockMetadata, e.x, e.y, e.z,
                    e.world.getBlockTileEntity(e.x, e.y, e.z));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void playerBlockPlace(PlayerBlockPlace e)
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
                block = e.player.inventory.getCurrentItem().itemID + ":" + e.player.inventory.getCurrentItem().getItemDamage();
            }

            new blockChangeLog(blockChangeLogCategory.placed, e.player, block, e.blockX, e.blockY, e.blockZ, null);
        }
    }

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

            new blockChangeLog(blockChangeLogCategory.interact, e.entityPlayer,
                    e.entity.worldObj.getBlockId(e.x, e.y, e.z) + ":" + e.entity.worldObj.getBlockMetadata(e.x, e.y, e.z), e.x, e.y, e.z,
                    e.entity.worldObj.getBlockTileEntity(e.x, e.y, e.z));
        }
    }

	/*
     * Needed background stuff
	 */

    public String getCommand(CommandEvent e)
    {
        String command = "/" + e.command.getCommandName();
        for (String str : e.parameters)
        {
            command = command + " " + str;
        }
        return command;
    }

    public static boolean exempt(EntityPlayer player)
    {
        for (String un : exempt_players)
        {
            if (un.replaceAll("\"", "").equalsIgnoreCase(player.username))
            {
                return true;
            }
        }
        for (Group group : APIRegistry.perms.getApplicableGroups(player, false))
        {
            if (exempt_groups.contains(group.name))
            {
                return true;
            }
        }
        return false;
    }
}
