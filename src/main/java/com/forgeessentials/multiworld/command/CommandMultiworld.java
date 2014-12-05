package com.forgeessentials.multiworld.command;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldType;
import net.minecraftforge.permissions.PermissionContext;
import net.minecraftforge.permissions.PermissionsManager;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.multiworld.ModuleMultiworld;
import com.forgeessentials.multiworld.Multiworld;
import com.forgeessentials.multiworld.MultiworldException;
import com.forgeessentials.multiworld.MultiworldManager;
import com.forgeessentials.util.OutputHandler;

public class CommandMultiworld extends ForgeEssentialsCommandBase {

    private ICommandSender sender;
    private EntityPlayerMP senderPlayer;
    private Queue<String> args;
    private PermissionContext permissionContext;
    private boolean tabCompleteMode;
    private List<String> tabComplete;

    @Override
    public String getCommandName()
    {
        return "mw";
    }

    @Override
    public String getCommandUsage(ICommandSender commandSender)
    {
        return "/mw Multiworld management command";
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public String getPermissionNode()
    {
        return ModuleMultiworld.PERM_BASE;
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {
        return RegisteredPermValue.OP;
    }

    private void info(String message)
    {
        if (!tabCompleteMode)
            OutputHandler.chatConfirmation(sender, message);
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args)
    {
        this.args = new LinkedList<String>(Arrays.asList(args));
        this.sender = sender;
        this.senderPlayer = (sender instanceof EntityPlayerMP) ? (EntityPlayerMP) sender : null;
        this.tabCompleteMode = false;
        this.permissionContext = new PermissionContext().setCommandSender(sender).setCommand(this);
        parseMain();
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        this.args = new LinkedList<String>(Arrays.asList(args));
        this.sender = sender;
        this.senderPlayer = (sender instanceof EntityPlayerMP) ? (EntityPlayerMP) sender : null;
        this.tabCompleteMode = true;
        this.tabComplete = null;
        this.permissionContext = new PermissionContext().setCommandSender(sender).setCommand(this);
        parseMain();
        return tabComplete;
    }

    private static String[] parseMainArgs = new String[] { "create", "delete", "list" };

    private static String[] parseListArgs = new String[] { "worlds", "providers", "worldtypes" };

    /**
     * Parse subcommands
     */
    private void parseMain()
    {
        if (args.isEmpty())
        {
            info("Multiworld Usage:");
            info("/mw create (world) : Create a new world (? for usage)");
            info("/mw delete (world) : Delete a world");
            info("/mw list [worlds|providers|worldtypes]");
            return;
        }
        if (tabCompleteMode && args.size() == 1)
        {
            tabComplete = ForgeEssentialsCommandBase.getListOfStringsMatchingLastWord(args.peek(), parseMainArgs);
            return;
        }
        String mainArg = args.remove().toLowerCase();
        switch (mainArg)
        {
        case "create":
            parseCreate();
            break;

        case "delete":
            parseDelete();
            break;

        case "list":
            parseList();
            break;

        default:
            throw new CommandException("Unknown subcommand: " + mainArg);
        }
    }

    /**
     * Create a new multiworld
     */
    private void parseCreate()
    {
        if (senderPlayer != null)
        {
            if (!PermissionsManager.checkPermission(permissionContext, ModuleMultiworld.PERM_CREATE))
                return;
        }

        // Get the world name
        if (args.isEmpty())
            throw new CommandException("Missing name argument");
        String name = args.remove().toLowerCase();

        // Display usage if requested
        if (name.equals("?"))
        {
            info("Usage: /mw create (name) [provider] [worldType] [seed]");
            return;
        }

        // Get the provider
        String provider = MultiworldManager.PROVIDER_NORMAL;
        if (tabCompleteMode && args.size() == 1)
        {
            tabComplete = ForgeEssentialsCommandBase.getListOfStringsMatchingLastWord(args.peek(), ModuleMultiworld.getMultiworldManager().getWorldProviders()
                    .keySet());
            return;
        }
        if (!args.isEmpty())
            provider = args.remove();

        // Get the World Type
        String worldType = WorldType.DEFAULT.getWorldTypeName();
        if (tabCompleteMode && args.size() == 1)
        {
            tabComplete = ForgeEssentialsCommandBase.getListOfStringsMatchingLastWord(args.peek(), ModuleMultiworld.getMultiworldManager().getWorldTypes()
                    .keySet());
            return;
        }
        if (!args.isEmpty())
            worldType = args.remove();

        // Get the World Seed
        long seed = new Random().nextLong();
        if (!args.isEmpty())
        {
            String arg = args.remove();
            try
            {
                seed = Long.parseLong(arg, 10);
            }
            catch (NumberFormatException e)
            {
                seed = arg.hashCode();
            }
        }

        if (!args.isEmpty())
            throw new CommandException("Too many arguments");

        if (tabCompleteMode)
            return;

        Multiworld world = new Multiworld(name, provider, worldType, seed);
        try
        {
            ModuleMultiworld.getMultiworldManager().addWorld(world);
            if (senderPlayer != null)
            {
                world.teleport(senderPlayer, true);
            }
        }
        catch (MultiworldException e)
        {
            throw new CommandException(e.type.error);
        }
    }

    /**
     * Delete a multiworld
     */
    private void parseDelete()
    {
        if (tabCompleteMode)
        {
            if (args.size() > 1)
                return;
            tabComplete = ForgeEssentialsCommandBase.getListOfStringsMatchingLastWord(args.peek(), ModuleMultiworld.getMultiworldManager().getWorldMap().keySet());
            return;
        }
        if (args.isEmpty())
            throw new CommandException("Too few arguments!");

        if (!tabCompleteMode && !PermissionsManager.checkPermission(permissionContext, ModuleMultiworld.PERM_DELETE))
        {
            OutputHandler.chatError(sender, FEPermissions.MSG_NO_COMMAND_PERM);
            return;
        }

        Multiworld world = ModuleMultiworld.getMultiworldManager().getWorld(args.peek());
        if (world == null)
            throw new CommandException("Multiworld " + args.peek() + " does not exist!");

        ModuleMultiworld.getMultiworldManager().deleteWorld(world);
        info("Deleted Multiworld #" + args.peek());
    }

    /**
     * Print lists of multiworlds, available providers and available world-types
     */
    private void parseList()
    {
        if (tabCompleteMode)
        {
            if (args.size() > 1)
                return;
            tabComplete = ForgeEssentialsCommandBase.getListOfStringsMatchingLastWord(args.peek(), parseListArgs);
            return;
        }

        if (!tabCompleteMode && !PermissionsManager.checkPermission(permissionContext, ModuleMultiworld.PERM_LIST))
        {
            OutputHandler.chatError(sender, FEPermissions.MSG_NO_COMMAND_PERM);
            return;
        }

        String subArg = args.isEmpty() ? "worlds" : args.remove().toLowerCase();
        switch (subArg)
        {
        case "providers":
            info("Available world providers:");
            for (String provider : ModuleMultiworld.getMultiworldManager().getWorldProviders().keySet())
            {
                info("  " + provider);
            }
            break;
        case "worldtypes":
            info("Available world types:");
            for (String worldType : ModuleMultiworld.getMultiworldManager().getWorldTypes().keySet())
            {
                info("  " + worldType);
            }
            break;
        case "worlds":
        default:
            info("Available worlds:");
            for (Multiworld world : ModuleMultiworld.getMultiworldManager().getWorlds())
            {
                info("#" + world.getDimensionId() + " " + world.getName() + ": " + world.getProvider());
            }
            break;
        }
    }
}