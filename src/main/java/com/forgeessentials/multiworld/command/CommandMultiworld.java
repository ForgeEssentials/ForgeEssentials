package com.forgeessentials.multiworld.command;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldType;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.multiworld.ModuleMultiworld;
import com.forgeessentials.multiworld.core.Multiworld;
import com.forgeessentials.multiworld.core.MultiworldManager;
import com.forgeessentials.multiworld.core.MultiworldTeleporter;
import com.forgeessentials.multiworld.core.exception.MultiworldAlreadyExistsException;
import com.forgeessentials.multiworld.core.exception.ProviderNotFoundException;
import com.forgeessentials.multiworld.core.exception.WorldTypeNotFoundException;
import com.forgeessentials.util.OutputHandler;

/**
 * 
 * @author Olee
 */
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
        return "Multiworld management command";
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public String getPermissionNode()
    {
        return ModuleMultiworld.PERM_CREATE;
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
        this.args = new LinkedList<String>(Arrays.asList(args));
        this.sender = sender;
        this.senderPlayer = (sender instanceof EntityPlayerMP) ? (EntityPlayerMP) sender : null;
        this.tabCompleteMode = true;
        this.tabComplete = null;
        parseMain();
        return tabComplete;
    }

    private static String[] parseMainArgs = new String[] { "create", "delete", "regen", "providers", "types" };

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
        default:
            throw new CommandException("Unknown subcommand: " + mainArg);
        }
    }

    /**
     * Create a new multiworld
     */
    private void parseCreate()
    {
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
        catch (ProviderNotFoundException e)
        {
            throw new CommandException("World-provider not found!");
        }
        catch (WorldTypeNotFoundException e)
        {
            throw new CommandException("World-type not found!");
        }
        catch (MultiworldAlreadyExistsException e)
        {
            throw new CommandException("A world with that name already exists!");
        }
    }

}
