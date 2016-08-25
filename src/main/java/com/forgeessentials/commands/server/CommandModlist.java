package com.forgeessentials.commands.server;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.permission.PermissionLevel;

import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.util.output.ChatOutputHandler;

public class CommandModlist extends ForgeEssentialsCommandBase
{

    @Override
    public String getCommandName()
    {
        return "femodlist";
    }

    @Override
    public String[] getDefaultAliases()
    {
        return new String[] { "modlist" };
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public PermissionLevel getPermissionLevel()
    {
        return PermissionLevel.TRUE;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/modlist: Get a list of all mods running on this server.";
    }

    @Override
    public String getPermissionNode()
    {
        return ModuleCommands.PERM + ".modlist";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException
    {
        int size = Loader.instance().getModList().size();
        int perPage = 7;
        int pages = (int) Math.ceil(size / (float) perPage);

        int page = args.length == 0 ? 0 : parseInt(args[0], 1, pages) - 1;
        int min = Math.min(page * perPage, size);

        ChatOutputHandler.chatNotification(sender, String.format("--- Showing modlist page %1$d of %2$d ---", page + 1, pages));
        for (int i = page * perPage; i < min + perPage; i++)
        {
            if (i >= size)
            {
                break;
            }
            ModContainer mod = Loader.instance().getModList().get(i);
            ChatOutputHandler.chatNotification(sender, mod.getName() + " - " + mod.getVersion());
        }
    }

}
