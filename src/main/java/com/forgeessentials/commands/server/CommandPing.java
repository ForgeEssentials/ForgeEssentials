package com.forgeessentials.commands.server;

import net.minecraft.command.CommandException;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.FECommandManager.ConfigurableCommand;
import com.forgeessentials.util.output.ChatOutputHandler;

public class CommandPing extends ForgeEssentialsCommandBase implements ConfigurableCommand
{
    public String response = "Pong! %time";
    static ForgeConfigSpec.ConfigValue<String> FEresponse;

    @Override
    public String getPrimaryAlias()
    {
        return "ping";
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.ALL;
    }

    @Override
    public String getPermissionNode()
    {
        return ModuleCommands.PERM + ".ping";
    }

    @Override
    public void processCommandPlayer(MinecraftServer server, ServerPlayerEntity sender, String[] args) throws CommandException
    {
        ChatOutputHandler.chatNotification(sender, response.replaceAll("%time", sender.ping + "ms."));
    }

    @Override
    public void processCommandConsole(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        ChatOutputHandler.chatNotification(sender, response.replaceAll("%time", ""));
    }

    @Override
    public void loadConfig(ForgeConfigSpec.Builder BUILDER, String category)
    {
    	BUILDER.push(category);
    	FEresponse = BUILDER.comment("Response Format for command must include %time.").define("response", "Pong! %time");
    	BUILDER.pop();
    }

    @Override
    public void loadData()
    {
        /* do nothing */
    }

    @Override
    public void bakeConfig(boolean reload)
    {
    	response = FEresponse.get();
    }
}
