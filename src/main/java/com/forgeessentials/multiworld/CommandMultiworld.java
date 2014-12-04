package com.forgeessentials.multiworld;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Queue;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.WorldType;
import net.minecraftforge.permissions.PermissionsManager;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.util.OutputHandler;

public class CommandMultiworld extends ForgeEssentialsCommandBase {

    private ICommandSender sender;
    private EntityPlayerMP senderPlayer;
    private Queue<String> args;
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

    private void warn(String message)
    {
        if (!tabCompleteMode)
            OutputHandler.chatWarning(sender, message);
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args)
    {
        this.args = new LinkedList<String>(Arrays.asList(args));
        this.sender = sender;
        this.senderPlayer = (sender instanceof EntityPlayerMP) ? (EntityPlayerMP) sender : null;
        this.tabCompleteMode = false;
        parseMain();
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        if (args.length == 1)
        {
            return getListOfStringsMatchingLastWord(args, parseMainArgs);
        }
        else
        {
            return null;
        }
    }

    private static String[] parseMainArgs = new String[] { "create", "delete", "list" };

    /**
     * Parse subcommands
     */
    private void parseMain()
    {
        if (args.isEmpty())
        {
            info("subcommands: " + StringUtils.join(parseMainArgs, ", "));
            return;
        }
        if (tabCompleteMode)
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
            if (!PermissionsManager.checkPermission(senderPlayer, ModuleMultiworld.PERM_CREATE))
                return;
        }
        if (args.isEmpty())
            throw new CommandException("Missing name argument");
        String name = args.remove().toLowerCase();

        String provider = MultiworldManager.PROVIDER_NORMAL;
        if (tabCompleteMode && args.size() == 1)
        {
            tabComplete = ForgeEssentialsCommandBase.getListOfStringsMatchingLastWord(args.peek(), ModuleMultiworld.getMultiworldManager().getWorldProviders().keySet());
            return;
        }
        if (!args.isEmpty())
            provider = args.remove();

        String worldType = WorldType.DEFAULT.getWorldTypeName();
        if (tabCompleteMode && args.size() == 1)
        {
            tabComplete = ForgeEssentialsCommandBase.getListOfStringsMatchingLastWord(args.peek(), ModuleMultiworld.getMultiworldManager().getWorldTypes().keySet());
            return;
        }
        if (!args.isEmpty())
            worldType = args.remove();

        if (!args.isEmpty())
            throw new CommandException("Too many arguments");
        if (tabCompleteMode)
            return;

        Multiworld world = new Multiworld(name, provider, worldType);
        try
        {
            ModuleMultiworld.getMultiworldManager().addWorld(world);
            if (senderPlayer != null)
            {
                new MultiworldTeleporter(world.getWorldServer()).teleport(senderPlayer);
            }
        }
        catch (MultiworldException e)
        {
            throw new CommandException(e.type.error);
        }
    }

    private void parseDelete()
    {
        if (senderPlayer != null)
        {
            if (!PermissionsManager.checkPermission(senderPlayer, ModuleMultiworld.PERM_DELETE))
                return;
        }
        if (args.isEmpty())
        {
            throw new CommandException("Too few arguments!");
        }
        Multiworld world = ModuleMultiworld.getMultiworldManager().getWorld(args.peek());
        if (world != null)
        {
            ModuleMultiworld.getMultiworldManager().deleteWorld(world);
            sender.addChatMessage(new ChatComponentText("Deleted Multiworld #" + args.peek()));
        }
        else
        {
            sender.addChatMessage(new ChatComponentText("Dimension #" + args.peek() + " does not exist!"));
        }
    }

    private void parseList()
    {
        if (senderPlayer != null)
        {
            if (!PermissionsManager.checkPermission(senderPlayer, ModuleMultiworld.PERM_LIST))
                return;
        }
        String subArg = args.remove().toLowerCase();
        switch (subArg)
        {
        case "providers":
            OutputHandler.chatNotification(sender, "Available world providers:");
            for (String provider : MultiworldManager.PROVIDERS)
            {
                OutputHandler.chatNotification(sender, "  " + provider);
            }
            for (Entry<String, Integer> provider : ModuleMultiworld.getMultiworldManager().getWorldProviders().entrySet())
            {
                OutputHandler.chatNotification(sender, "  " + provider.getKey());
            }
            break;
        case "worldtypes":
            OutputHandler.chatNotification(sender, "Available world types:");
            for (String worldType : ModuleMultiworld.getMultiworldManager().getWorldTypes().keySet())
            {
                OutputHandler.chatNotification(sender, "  " + worldType);
            }
            break;
        default:
            sender.addChatMessage(new ChatComponentText("Available worlds:"));
            for (Multiworld world : ModuleMultiworld.getMultiworldManager().getWorlds())
            {
                sender.addChatMessage(new ChatComponentText("#" + world.getDimensionId() + " " + world.getName() + ": " + world.getProvider()));
            }
            break;
        }
    }
}
