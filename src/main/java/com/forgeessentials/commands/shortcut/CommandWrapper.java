package com.forgeessentials.commands.shortcut;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import java.util.List;

public class CommandWrapper extends ForgeEssentialsCommandBase {
	
    private ForgeEssentialsCommandBase command;
	private String cmdName;
    private String[] args;
    private String name;
    private String syntax = "";

    public CommandWrapper(String commandName, String name, String[] args, String syntax)
    {
        this.cmdName = commandName;
        this.name = name;
        this.args = args;
        this.syntax = (syntax.startsWith("\"") && syntax.endsWith("\"") ? syntax.substring(1, syntax.length() - 1) : syntax);
    }
    
    public ForgeEssentialsCommandBase getCommand()
	{
        if (command == null)
        {
            Object cmd = ((CommandHandler) MinecraftServer.getServer().getCommandManager()).getCommands().get(cmdName);
            if (cmd instanceof ForgeEssentialsCommandBase)
            {
                command = (ForgeEssentialsCommandBase) cmd;
            }
            else
            {
            	throw new CommandException("Error in this shortcut (" + this.name + "). The command sepcified in the config is no FE command.");
            }
        }
		return command;
	}

    @Override
    public String getCommandName()
    {
        return this.name;
    }

	public String getSyntax()
	{
		return syntax;
	}

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/" + this.getCommandName() + " " + this.getSyntax();
    }

	/*
	 * We override this so we can parse our own commands
	 */
	@Override
	public void processCommand(ICommandSender sender, String[] args)
	{
		getCommand().processCommand(sender, ShortcutCommands.parseArgs(sender, this.args, args));
	}

//    @Override
//    public void processCommandPlayer(EntityPlayer sender, String[] args)
//    {
//    	getCommand().processCommandPlayer(sender, args);
//    }
//
//    @Override
//    public void processCommandConsole(ICommandSender sender, String[] args)
//    {
//    	getCommand().processCommandConsole(sender, args);
//    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        return getCommand().addTabCompletionOptions(sender, args);
    }

    @Override
    public String getPermissionNode()
    {
        return getCommand().getPermissionNode();
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {
        return getCommand().getDefaultPermission();
    }
}