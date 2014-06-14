package com.forgeessentials.commands.shortcut;

import com.forgeessentials.api.permissions.RegGroup;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.util.OutputHandler;
import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

import java.util.List;

public class cmdWrapper extends ForgeEssentialsCommandBase {
    public ForgeEssentialsCommandBase cmd;
    public String cmdName;
    public String[] args;
    public String name;
    public String syntax = "";

    public cmdWrapper(String commandName, String name, String[] args, String syntax)
    {
        this.cmdName = commandName;
        this.name = name;
        this.args = args;
        this.syntax = (syntax.startsWith("\"") && syntax.endsWith("\"") ? syntax.substring(1, syntax.length() - 1) : syntax);
    }

    @Override
    public String getCommandName()
    {
        return this.name;
    }

    /*
     * We override this so we can parse our own commands
     */
    @Override
    public void processCommand(ICommandSender var1, String[] var2)
    {
        check();

        try
        {
            super.processCommand(var1, ShortcutCommands.parseArgs(var1, this.args, var2));
        }
        catch (Exception e)
        {
            OutputHandler.chatError(var1, this.getCommandUsage(var1));
        }
    }

    @Override
    public void processCommandPlayer(EntityPlayer sender, String[] args)
    {
        cmd.processCommandPlayer(sender, args);
    }

    @Override
    public void processCommandConsole(ICommandSender sender, String[] args)
    {
        cmd.processCommandConsole(sender, args);
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        return cmd.addTabCompletionOptions(sender, args);
    }

    @Override
    public String getCommandPerm()
    {
        check();
        return cmd.getCommandPerm();
    }

    private void check()
    {
        if (cmd == null)
        {
            Object fakeCmd = ((CommandHandler) MinecraftServer.getServer().getCommandManager()).getCommands().get(cmdName);
            if (fakeCmd instanceof ForgeEssentialsCommandBase)
            {
                cmd = (ForgeEssentialsCommandBase) fakeCmd;
            }
            else
            {
                OutputHandler.felog.severe("Error in this shortcut (" + this.name + "). The command sepcified in the config is no FE command.");
                return;
            }
        }
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/" + this.getCommandName() + " " + this.syntax;
    }

    @Override
    public int compareTo(Object o)
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public RegGroup getReggroup()
    {
        // TODO Auto-generated method stub
        return cmd.getReggroup();
    }
}