package com.forgeessentials.commands.server;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.permission.PermissionLevel;

import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.util.output.ChatOutputHandler;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

public class CommandModlist extends FEcmdModuleCommands
{

    @Override
    public String getCommandName()
    {
        return "modlist";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args)
    {
        int size = Loader.instance().getModList().size();
        int perPage = 7;
        int pages = (int) Math.ceil(size / (float) perPage);

        int page = args.length == 0 ? 0 : parseIntBounded(sender, args[0], 1, pages) - 1;
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
        return "/modlist Get a list of all mods running on this server.";
    }

}
