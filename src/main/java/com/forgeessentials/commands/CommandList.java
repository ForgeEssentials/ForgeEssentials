package com.forgeessentials.commands;

import com.forgeessentials.api.permissions.RegGroup;
import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.util.ChatUtils;
import com.forgeessentials.util.FunctionHelper;
import com.google.common.collect.HashMultimap;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

public class CommandList extends FEcmdModuleCommands {
    @Override
    public String[] getDefaultAliases()
    {
        return new String[] { "who", "online", "players" };
    }

    @Override
    public String getCommandName()
    {
        return "list";
    }

    @Override
    public RegGroup getReggroup()
    {
        return RegGroup.GUESTS;
    }

    @Override
    public void processCommandPlayer(EntityPlayer sender, String[] args)
    {
        sendList(sender);
    }

    @Override
    public void processCommandConsole(ICommandSender sender, String[] args)
    {
        sendList(sender);
    }

    private void sendList(ICommandSender sender)
    {
        // Group => Player(s)
        HashMultimap<String, String> map = HashMultimap.create();
        for (String username : MinecraftServer.getServer().getConfigurationManager().getAllUsernames())
        {
            map.put(FunctionHelper.getGroupRankString(username), username);
        }

        ChatUtils.sendLocalizedMessage(sender, "commands.players.list",
                MinecraftServer.getServer().getCurrentPlayerCount(), MinecraftServer.getServer().getMaxPlayers());
        for (String group : map.keySet())
        {
            ChatUtils.sendMessage(sender, group + ": " + FunctionHelper.niceJoin(map.get(group).toArray()));
        }
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/list List all online players.";
    }
}