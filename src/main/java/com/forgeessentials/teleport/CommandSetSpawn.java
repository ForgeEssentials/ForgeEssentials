package com.forgeessentials.teleport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.Group;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.selections.WarpPoint;
import com.forgeessentials.util.selections.WorldPoint;

import cpw.mods.fml.common.FMLCommonHandler;

public class CommandSetSpawn extends ForgeEssentialsCommandBase {
    public static final String SPAWN_PROP = "fe.teleport.spawnPoint";
    public static final String SPAWN_TYPE_PROP = "fe.teleport.spawnType";
    public static HashMap<UUID, WarpPoint> spawns = new HashMap<UUID, WarpPoint>();
    public static HashSet<Integer> dimsWithProp = new HashSet<Integer>();

    public static void setSpawnPoint(WorldPoint p, Zone zone)
    {
        String val = p.dim + ";" + p.x + ";" + p.y + ";" + p.z;
        APIRegistry.permissionManager.setGroupPermissionProp(APIRegistry.permissionManager.getDefaultGroup().name, SPAWN_PROP, val, zone);
    }

    @Override
    public String getCommandName()
    {
        return "setspawn";
    }

    @Override
    public void processCommandPlayer(EntityPlayer sender, String[] args)
    {
        if (args.length <= 0)
        {
            error(sender);
            return;
        }

        if (args.length == 1)
        {
            if (args[0].equalsIgnoreCase("help"))
            {
                OutputHandler.chatConfirmation(sender, " - /setspawn type <player|group|zone> <name> <none|bed|point> [zone]");
                OutputHandler.chatConfirmation(sender, " - - sets the spawn type of the player, group, or zone");
                OutputHandler.chatConfirmation(sender, " - /setspawn point <player|group|zone> <name> <here|revert|<x> <y> <z>> [zone]");
                OutputHandler.chatConfirmation(sender, " - - sets the spawn point of the player, group, or zone");
                OutputHandler.chatConfirmation(sender, " - the final [zone] argument is not necessary if the 2nd argument is zone");
                return;
            }
            else
            {
                error(sender);
            }
        }
        else if (args.length < 4 || args.length > 7)
        {
            error(sender);
            return;
        }

        // check point or type.
        String permProp = null;
        String prop = null;
        String output = "";
        if (args[0].equalsIgnoreCase("point"))
        {
            permProp = SPAWN_PROP;

            int dim = 0, x = 0, y = 0, z = 0;
            if (args.length >= 6)
            {
                dim = sender.worldObj.provider.dimensionId;
                x = parseInt(sender, args[3], (int) sender.posX);
                y = parseInt(sender, args[4], (int) sender.posY);
                z = parseInt(sender, args[5], (int) sender.posZ);
            }
            else if (args.length >= 4)
            {
                if (args[3].equalsIgnoreCase("here"))
                {
                    WorldPoint p = new WorldPoint(sender);
                    x = p.x;
                    y = p.y;
                    z = p.z;
                    dim = p.dim;
                }
                else if (args[3].equalsIgnoreCase("revert"))
                {
                    World world = FunctionHelper.getDimension(0);
                    WorldPoint p = new WorldPoint(world, world.getSpawnPoint().posX, world.getSpawnPoint().posY, world.getSpawnPoint().posZ);
                    x = p.x;
                    y = p.y;
                    z = p.z;
                    dim = p.dim;
                }
                else
                {
                    error(sender);
                }
            }
            else
            {
                error(sender);
            }

            prop = dim + ";" + x + ";" + y + ";" + z;
            output = String.format("Spawn point set to %1$d, %2$d, %3$d", x, y, z);
        }
        else if (args[0].equalsIgnoreCase("type"))
        {
            permProp = SPAWN_TYPE_PROP;

            if (args[3].equalsIgnoreCase("none"))
            {
                prop = "none";
            }
            else if (args[3].equalsIgnoreCase("bed"))
            {
                prop = "bed";
            }
            else if (args[3].equalsIgnoreCase("point"))
            {
                prop = "point";
            }
            else
            {
                error(sender);
            }

            output = String.format("Spawn type set to %1$s", prop);
        }
        else
        {
            error(sender);
        }

        // calc zone.
        Zone zone = APIRegistry.permissionManager.getGlobalZone();
        if (args.length == 5)
        {
            if (APIRegistry.permissionManager.doesZoneExist(args[4]))
            {
                zone = APIRegistry.permissionManager.getZone(args[4]);
            }
            else if (args[4].equalsIgnoreCase("here"))
            {
                zone = APIRegistry.permissionManager.getWhichZoneIn(new WorldPoint(sender));
            }
            else if (args[4].equalsIgnoreCase("revert"))
            {
                World world = FunctionHelper.getDimension(0);
                // zone = APIRegistry.permissionManager.getWhichZoneIn(new WorldPoint(world, world.getSpawnPoint().posX, world.getSpawnPoint().posY, world.getSpawnPoint().posZ));
                zone = APIRegistry.permissionManager.getWhichZoneIn(new WorldPoint(0, 10, 10, 10));
            }
            else
            {
                OutputHandler.chatError(sender, String.format("No zone by the name %s exists!", args[5]));
                return;
            }
        }
        else if (args.length == 7)
        {
            if (APIRegistry.permissionManager.doesZoneExist(args[6]))
            {
                zone = APIRegistry.permissionManager.getZone(args[6]);
            }
            else if (args[6].equalsIgnoreCase("here"))
            {
                zone = APIRegistry.permissionManager.getWhichZoneIn(new WorldPoint(sender));
            }
            else if (args[4].equalsIgnoreCase("revert"))
            {
                World world = FunctionHelper.getDimension(0);
                // zone = APIRegistry.permissionManager.getWhichZoneIn(new WorldPoint(world, world.getSpawnPoint().posX, world.getSpawnPoint().posY, world.getSpawnPoint().posZ));
                zone = APIRegistry.permissionManager.getWhichZoneIn(new WorldPoint(0, 10, 10, 10));
            }
            else
            {
                OutputHandler.chatError(sender, String.format("No zone by the name %s exists!", args[7]));
                return;
            }
        }

        if (args[1].equalsIgnoreCase("player"))
        {
            String name = args[2];
            UUID id = null;
            if (args[2].equalsIgnoreCase("_ME_"))
            {
                name = sender.getCommandSenderName();
                id = FunctionHelper.getPlayerID(name);
            }
            else
            {
                EntityPlayerMP player = FunctionHelper.getPlayerForName(sender, name);
                if (player == null)
                {
                    OutputHandler.chatError(sender, String.format("Player %s does not exist, or is not online.", name));
                    OutputHandler.chatConfirmation(sender, name + " will be used, but may be inaccurate.");
                }
                else
                {
                    name = player.getCommandSenderName();
                    id = player.getPersistentID();
                }
            }

            APIRegistry.permissionManager.setPlayerPermissionProp(id, permProp, prop, zone.getName());
            OutputHandler.chatConfirmation(sender, output);
        }
        else if (args[1].equalsIgnoreCase("group"))
        {
            if (APIRegistry.getAsFEGroup(args[2]) == null)
            {
                OutputHandler.chatError(sender, args[2] + " does not exist as a group!");
                return;
            }

            APIRegistry.permissionManager.setGroupPermissionProp(args[2], permProp, prop, zone.getName());
            OutputHandler.chatConfirmation(sender, output);
        }
        else if (args[1].equalsIgnoreCase("zone"))
        {
            if (APIRegistry.permissionManager.doesZoneExist(args[2]))
            {
                zone = APIRegistry.permissionManager.getZone(args[2]);
            }
            else if (args[5].equalsIgnoreCase("here"))
            {
                zone = APIRegistry.permissionManager.getWhichZoneIn(new WorldPoint(sender));
            }
            else
            {
                OutputHandler.chatError(sender, String.format("No zone by the name %s exists!", args[2]));
                return;
            }

            APIRegistry.permissionManager.setGroupPermissionProp(APIRegistry.permissionManager.getDefaultGroup().name, permProp, prop, zone.getName());
            OutputHandler.chatConfirmation(sender, output);
        }
        else
        {
            error(sender);
        }
    }

    @Override
    public void processCommandConsole(ICommandSender sender, String[] args)
    {
        if (args.length <= 1)
        {
            error(sender);
            return;
        }

        if (args.length == 1)
        {
            if (args[0].equalsIgnoreCase("help"))
            {
                OutputHandler.chatConfirmation(sender, " - /setspawn type <player|group|zone> <name> <none|bed|point> [zone]");
                OutputHandler.chatConfirmation(sender, " - - sets the spawn type of the player, group, or zone");
                OutputHandler.chatConfirmation(sender, " - /setspawn point <player|group|zone> <name> <here|revert|<x> <y> <z>> [zone]");
                OutputHandler.chatConfirmation(sender, " - - sets the spawn type of the player, group, or zone");
                OutputHandler.chatConfirmation(sender, " - the final [zone] argument is not necessary if the 2nd argument is zone");
                return;
            }
            else
            {
                error(sender);
            }
        }
        else if (args.length < 4 || args.length > 7)
        {
            error(sender);
            return;
        }

        // check point or type.
        String permProp = null;
        String prop = null;
        String output = "";
        if (args[0].equalsIgnoreCase("point"))
        {
            permProp = SPAWN_PROP;

            int dim = 0, x = 0, y = 0, z = 0;
            if (args.length >= 7)
            {
                dim = parseInt(sender, args[6]);
                x = parseInt(sender, args[3]);
                y = parseInt(sender, args[4]);
                z = parseInt(sender, args[5]);
            }

            prop = dim + ";" + x + ";" + y + ";" + z;
            output = String.format("Spawn point set to %1$d, %2$d, %3$d", x, y, z);
        }
        else if (args[0].equalsIgnoreCase("type"))
        {
            permProp = SPAWN_TYPE_PROP;

            if (args[3].equalsIgnoreCase("none"))
            {
                prop = "none";
            }
            else if (args[3].equalsIgnoreCase("bed"))
            {
                prop = "bed";
            }
            else if (args[3].equalsIgnoreCase("point"))
            {
                prop = "point";
            }
            else
            {
                error(sender);
            }

            output = String.format("Spawn type set to %1$s", prop);
        }
        else
        {
            error(sender);
        }

        // calc zone.
        Zone zone = APIRegistry.permissionManager.getGlobalZone();
        if (args.length == 6)
        {
            if (APIRegistry.permissionManager.doesZoneExist(args[5]))
            {
                zone = APIRegistry.permissionManager.getZone(args[5]);
            }
            else
            {
                OutputHandler.chatError(sender, String.format("No zone by the name %s exists!", args[5]));
                return;
            }
        }
        else if (args.length == 8)
        {
            if (APIRegistry.permissionManager.doesZoneExist(args[7]))
            {
                zone = APIRegistry.permissionManager.getZone(args[7]);
            }
            else
            {
                OutputHandler.chatError(sender, String.format("No zone by the name %s exists!", args[7]));
                return;
            }
        }

        if (args[1].equalsIgnoreCase("player"))
        {
            String name = args[2];
            UUID id = null;
            EntityPlayerMP player = FunctionHelper.getPlayerForName(sender, name);
            if (player == null)
            {
                OutputHandler.chatError(sender, String.format("Player %s does not exist, or is not online.", args[0]));
                OutputHandler.chatConfirmation(sender, name + " will be used, but may be inaccurate.");
            }
            else
            {
                name = player.getCommandSenderName();
                id = player.getPersistentID();
            }

            APIRegistry.permissionManager.setPlayerPermissionProp(id, permProp, prop, zone.getName());
            OutputHandler.chatConfirmation(sender, output);
        }
        else if (args[1].equalsIgnoreCase("group"))
        {
            if (APIRegistry.getAsFEGroup(args[2]) == null)
            {
                OutputHandler.chatError(sender, args[2] + " does not exist as a group!");
                return;
            }

            APIRegistry.permissionManager.setGroupPermissionProp(args[2], permProp, prop, zone.getName());
            OutputHandler.chatConfirmation(sender, output);
        }
        else if (args[1].equalsIgnoreCase("zone"))
        {
            if (APIRegistry.permissionManager.doesZoneExist(args[2]))
            {
                zone = APIRegistry.permissionManager.getZone(args[2]);
            }
            else
            {
                OutputHandler.chatError(sender, String.format("No zone by the name %s exists!", args[2]));
                return;
            }

            APIRegistry.permissionManager.setGroupPermissionProp(APIRegistry.permissionManager.getDefaultGroup().name, permProp, prop, zone.getName());
            OutputHandler.chatConfirmation(sender, output);
        }
        else
        {
            error(sender);
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
        return "fe.teleport." + getCommandName();
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        ArrayList<String> completes = new ArrayList<String>();

        // type
        if (args.length == 1)
        {
            completes.add("type");
            completes.add("point");
            completes.add("help");
        }
        // target type
        else if (args.length == 2)
        {
            completes.add("player");
            completes.add("group");
            completes.add("zone");
        }
        // target
        else if (args.length == 3)
        {
            if (args[1].equalsIgnoreCase("player"))
            {
                completes.add("_ME_");
                for (String name : FMLCommonHandler.instance().getMinecraftServerInstance().getAllUsernames())
                {
                    completes.add(name);
                }

            }
            else if (args[1].equalsIgnoreCase("group"))
            {
                List<Group> groups = APIRegistry.permissionManager.getGroupsInZone(APIRegistry.permissionManager.getGlobalZone().getName());
                for (Group g : groups)
                {
                    completes.add(g.name);
                }
            }
            else if (args[1].equalsIgnoreCase("zone"))
            {
                for (Zone z : APIRegistry.permissionManager.getZoneList())
                {
                    completes.add(z.getName());
                }
            }
        }
        // value
        else if (args.length == 4)
        {
            if (args[0].equalsIgnoreCase("type"))
            {
                completes.add("none");
                completes.add("bed");
                completes.add("point");
            }
            else if (args[0].equalsIgnoreCase("point"))
            {
                completes.add("here");
            }
        }
        // zone after 1 arg of vals
        else if (args.length == 5)
        {
            if (args[0].equalsIgnoreCase("type") || (args[0].equalsIgnoreCase("point") && args[4].equalsIgnoreCase("here")))
            {
                for (Zone z : APIRegistry.permissionManager.getZoneList())
                {
                    completes.add(z.getName());
                }
            }
        }
        // zone after coords
        else if (args.length == 7)
        {
            if (args[0].equalsIgnoreCase("point"))
            {
                for (Zone z : APIRegistry.permissionManager.getZoneList())
                {
                    completes.add(z.getName());
                }
            }
        }

        return getListOfStringsMatchingLastWord(args, completes.toArray(new String[completes.size()]));

    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {
        return RegisteredPermValue.OP;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {

        return "/setspawn help Set the spawn point.";
    }
}
