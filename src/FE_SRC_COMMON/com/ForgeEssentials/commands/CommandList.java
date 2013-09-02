package com.ForgeEssentials.commands;

import java.util.List;

import com.ForgeEssentials.util.ChatUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

import com.ForgeEssentials.api.permissions.RegGroup;
import com.ForgeEssentials.commands.util.FEcmdModuleCommands;
import com.ForgeEssentials.util.FunctionHelper;
import com.google.common.collect.HashMultimap;

public class CommandList extends FEcmdModuleCommands
{
    @Override
    public String[] getDefaultAliases()
    {
        return new String[] {"who", "online", "players"};
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
            map.put(FunctionHelper.getGroupRankString(username), username);

		ChatUtils.sendLocalizedMessage(sender, "commands.players.list",
				MinecraftServer.getServer().getCurrentPlayerCount(), MinecraftServer.getServer().getMaxPlayers());
        for (String group : map.keySet())
			ChatUtils.sendMessage(sender, group + ": " + FunctionHelper.niceJoin(map.get(group).toArray()));
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public List<?> addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        return null;
    }

    @Override
    public String getCommandPerm()
    {
        return "ForgeEssentials.BasicCommands." + getCommandName();
    }
}