package com.forgeessentials.core.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.CommandBlockBaseLogic;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.core.misc.PermissionManager;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.util.output.LoggingHandler;

public abstract class ForgeEssentialsCommandBase extends CommandBase
{

    public List<String> aliases = new ArrayList<>();

    protected final static String PREFIX="fe";

    // ------------------------------------------------------------
    // Command alias

    @Override
    public abstract String getUsage(ICommandSender sender);

    @Override
    public List<String> getAliases()
    {
        return aliases;
    }

    /**
     * @deprecated Use {@link #getPrimaryAlias()} instead for downstream classes     *
     */
    @Override
    public String getName() {
        String name = getPrimaryAlias();
        if (name.startsWith(PREFIX)) {
            return name;
        } else
        {
            if (name.startsWith("/"))
            {
                   String newname = name.substring(1);
                if (newname.startsWith(PREFIX)) {
                    return name;
                } else {
                    return "/" + PREFIX + newname;
                }
            } else
            {
                return PREFIX + name;
            }
        }
    }

    /**
     * @deprecated Use {@link ForgeEssentialsCommandBase#getDefaultSecondaryAliases()} in downstream classes
     * Returns a list of default aliases, that will be added to the configuration on first run
     */
    public String[] getDefaultAliases()
    {
        List<String> list = new ArrayList<>();
        String name = getPrimaryAlias();
        if (!name.startsWith(PREFIX))
        {
            list.add(name);
        }
        list.addAll(Arrays.asList(getDefaultSecondaryAliases()));
        return list.toArray(new String[]{});
    }

    public void setAliases(String[] aliases)
    {
        if (aliases == null)
            setAliases(new ArrayList<String>());
        else
            setAliases(Arrays.asList(aliases));
    }

    public void setAliases(List<String> aliases)
    {
        this.aliases = aliases;
    }

    // ------------------------------------------------------------
    // Command processing

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (sender instanceof EntityPlayerMP)
        {
            processCommandPlayer(server, (EntityPlayerMP) sender, args);
        }
        else if (sender instanceof CommandBlockBaseLogic)
        {
            processCommandBlock(server, (CommandBlockBaseLogic) sender, args);
        }
        else
        {
            processCommandConsole(server, sender, args);
        }
    }

    public void processCommandPlayer(MinecraftServer server, EntityPlayerMP sender, String[] args) throws CommandException
    {
        throw new TranslatedCommandException("This command cannot be used as player");
    }

    public void processCommandConsole(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        throw new TranslatedCommandException(FEPermissions.MSG_NO_CONSOLE_COMMAND);
    }

    public void processCommandBlock(MinecraftServer server, CommandBlockBaseLogic block, String[] args) throws CommandException
    {
        processCommandConsole(server, block, args);
    }

    // ------------------------------------------------------------
    // Command usage

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender)
    {
        if (!canConsoleUseCommand() && !(sender instanceof EntityPlayer))
            return false;
        return true;
    }

    public abstract boolean canConsoleUseCommand();

    // ------------------------------------------------------------
    // Permissions

    /**
     * Registers this command and it's permission node
     */
    public void register()
    {
        if (FMLCommonHandler.instance().getMinecraftServerInstance() == null)
            return;

        Map<String, ICommand> commandMap = ((CommandHandler) FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager()).getCommands();
        if (commandMap.containsKey(getName()))
            LoggingHandler.felog.error(String.format("Command %s registered twice", getName()));

        if (getAliases() != null && !getAliases().isEmpty())
        {
            for (String alias : getAliases())
                if (alias != null && commandMap.containsKey(alias))
                {
                    LoggingHandler.felog.error(String.format("Command alias %s of command %s registered twice", alias, getName()));
                    ICommand old = commandMap.get(alias);
                    LoggingHandler.felog.error(String.format("Old Class: %s has been removed from commandMap!", old.getClass().getCanonicalName()));
                    commandMap.remove(alias);
                }
        }

        ((CommandHandler) FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager()).registerCommand(this);
        PermissionManager.registerCommandPermission(this, this.getPermissionNode(), this.getPermissionLevel());
        registerExtraPermissions();
    }

    @SuppressWarnings("unchecked")
    public void deregister()
    {
        if (FMLCommonHandler.instance().getMinecraftServerInstance() == null)
            return;
        CommandHandler cmdHandler = (CommandHandler) FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager();
        Map<String, ICommand> commandMap = cmdHandler.getCommands();
        Set<ICommand> commandSet = cmdHandler.commandSet;

        String commandName = getName();
        List<String> commandAliases = getAliases();
        commandSet.remove(this);
        if (commandName != null)
            commandMap.remove(commandName);
        if (commandAliases != null && !commandAliases.isEmpty())
        {
            for (String alias : commandAliases)
            {
                commandMap.remove(alias);
            }
        }
    }

    /**
     * Registers additional permissions
     */
    public void registerExtraPermissions()
    {
        /* do nothing */
    }

    /**
     * Check, if the sender has permissions to use this command
     */
    public boolean checkCommandPermission(ICommandSender sender)
    {
        if (getPermissionNode() == null || getPermissionNode().isEmpty())
            return true;
        if (sender instanceof MinecraftServer || sender instanceof CommandBlockBaseLogic)
            return true;
        return PermissionAPI.hasPermission(UserIdent.get(sender.getName()).getPlayer(), getPermissionNode());
    }

    // ------------------------------------------------------------
    // Utilities

    public static List<String> getListOfStringsMatchingLastWord(String arg, Collection<String> possibleMatches)
    {
        List<String> arraylist = new ArrayList<>();
        for (String s2 : possibleMatches)
        {
            if (doesStringStartWith(arg, s2))
            {
                arraylist.add(s2);
            }
        }
        return arraylist;
    }

    /*public static List<String> getListOfStringsMatchingLastWord(String[] args, Collection<?> possibleMatches)
    {
        return getListOfStringsMatchingLastWord(args[args.length - 1], possibleMatches);
    }*/

    public static List<String> getListOfStringsMatchingLastWord(String arg, String... possibleMatches)
    {
        List<String> arraylist = new ArrayList<>();
        int i = possibleMatches.length;
        for (int j = 0; j < i; ++j)
        {
            String s2 = possibleMatches[j];
            if (doesStringStartWith(arg, s2))
            {
                arraylist.add(s2);
            }
        }
        return arraylist;
    }

    public static List<String> completePlayername(String arg)
    {
        List<String> arraylist = new ArrayList<>();
        for (UserIdent s2 : APIRegistry.perms.getServerZone().getKnownPlayers())
        {
            if (doesStringStartWith(arg, s2.getUsernameOrUuid()))
            {
                arraylist.add(s2.getUsernameOrUuid());
            }
        }
        return arraylist;
    }

    /**
     * Parse int with support for relative int.
     *
     * @param string
     * @param relativeStart
     * @return
     * @throws NumberInvalidException
     */
    public static int parseInt(String string, int relativeStart) throws NumberInvalidException
    {
        if (string.startsWith("~"))
        {
            string = string.substring(1);
            return relativeStart + parseInt(string);
        }
        else
        {
            return parseInt(string);
        }
    }

    /**
     * Parse double with support for relative values.
     *
     * @param string
     * @param relativeStart
     * @return
     */
    public static double parseDouble(String string, double relativeStart) throws NumberInvalidException
    {
        if (string.startsWith("~"))
        {
            string = string.substring(1);
            return relativeStart + parseInt(string);
        }
        else
        {
            return parseInt(string);
        }
    }

    /**
     * formerly of PermissionObject
     */
    public abstract String getPermissionNode();

    public abstract DefaultPermissionLevel getPermissionLevel();

    public List<String> matchToPlayers(String[] args)
    {
        return getListOfStringsMatchingLastWord(args, FMLCommonHandler.instance().getMinecraftServerInstance().getOnlinePlayerNames());
    }

    @Nonnull
    protected abstract String getPrimaryAlias();

    @Nonnull
    protected String[] getDefaultSecondaryAliases() {
        return new String[] {};
    }
}
