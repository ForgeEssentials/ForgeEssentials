package com.forgeessentials.teleport;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.RegGroup;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.api.permissions.query.PermQueryPlayer;
import com.forgeessentials.api.permissions.query.PropQueryPlayerZone;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.util.selections.WarpPoint;
import com.forgeessentials.util.*;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChunkCoordinates;

import java.util.List;

public class CommandSpawn extends ForgeEssentialsCommandBase {
    @Override
    public String getCommandName()
    {
        return "spawn";
    }

    @Override
    public void processCommandPlayer(EntityPlayer sender, String[] args)
    {
        Zone zone = APIRegistry.zones.getWorldZone(sender.worldObj);
        if (args.length >= 1)
        {
            if (!APIRegistry.perms.checkPermAllowed(new PermQueryPlayer(sender, getPermissionNode() + ".others")))
            {
                OutputHandler.chatError(sender,
                        "You have insufficient permissions to do that. If you believe you received this message in error, please talk to a server admin.");
                return;
            }
            EntityPlayerMP player = FunctionHelper.getPlayerForName(sender, args[0]);
            if (player != null)
            {
                PlayerInfo.getPlayerInfo(player.getPersistentID()).back = new WarpPoint(player);

                PropQueryPlayerZone query = new PropQueryPlayerZone(player, CommandSetSpawn.SPAWN_PROP, zone, true);
                APIRegistry.perms.getPermissionProp(query);

                String val = query.getStringValue();
                String[] split = val.split("[;_]");

                int dim = Integer.parseInt(split[0]);
                int x = Integer.parseInt(split[1]);
                int y = Integer.parseInt(split[2]);
                int z = Integer.parseInt(split[3]);

                WarpPoint point = new WarpPoint(dim, x + .5, y + 1, z + .5, player.cameraYaw, player.cameraPitch);

                // teleport
                FunctionHelper.setPlayer(player, point);
                ChatUtils.sendMessage(player, "Teleported to spawn.");
                return;
            }
            else
            {
                OutputHandler.chatError(sender, String.format("Player %s does not exist, or is not online.", args[0]));
                return;
            }
        }
        else if (args.length == 0)
        {
            PropQueryPlayerZone query = new PropQueryPlayerZone(sender, CommandSetSpawn.SPAWN_PROP, zone, true);
            APIRegistry.perms.getPermissionProp(query);

            String val = query.getStringValue();
            String[] split = val.split("[;_]");

            int dim = Integer.parseInt(split[0]);
            int x = Integer.parseInt(split[1]);
            int y = Integer.parseInt(split[2]);
            int z = Integer.parseInt(split[3]);

            WarpPoint spawn = new WarpPoint(dim, x + .5, y + 1, z + .5, sender.cameraYaw, sender.cameraPitch);
            PlayerInfo.getPlayerInfo(sender.getPersistentID()).back = new WarpPoint(sender);
            TeleportCenter.addToTpQue(spawn, sender);
            ChatUtils.sendMessage(sender, "Teleported to spawn.");
        }
    }

    @Override
    public void processCommandConsole(ICommandSender sender, String[] args)
    {
        if (args.length >= 1)
        {
            EntityPlayerMP player = FunctionHelper.getPlayerForName(sender, args[0]);
            if (player != null)
            {
                PlayerInfo.getPlayerInfo(player.getPersistentID()).back = new WarpPoint(player);

                WarpPoint spawn;
                ChunkCoordinates point = FMLCommonHandler.instance().getMinecraftServerInstance().worldServers[0].provider.getSpawnPoint();
                spawn = new WarpPoint(0, point.posX, point.posY, point.posZ, player.rotationPitch, player.rotationYaw);
                TeleportCenter.addToTpQue(spawn, player);
                ChatUtils.sendMessage(player, "Teleported to spawn.");
                return;
            }
            else
            {
                OutputHandler.chatError(sender, String.format("Player %s does not exist, or is not online.", args[0]));
                return;
            }
        }
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public String getPermissionNode()
    {
        return "fe.teleport.spawn";
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        if (args.length == 1)
        {
            return getListOfStringsMatchingLastWord(args, FMLCommonHandler.instance().getMinecraftServerInstance().getAllUsernames());
        }
        else
        {
            return null;
        }
    }

    @Override
    public RegGroup getReggroup()
    {
        return RegGroup.MEMBERS;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        if (sender instanceof EntityPlayer)
        {
            return "/spawn [player] [dimension] Teleport you or another player to their spawn point.";
        }
        else
        {
            return "/spawn <player> [dimension] Teleport a player to their spawn point.";
        }
    }
}
