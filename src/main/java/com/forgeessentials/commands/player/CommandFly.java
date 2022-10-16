package com.forgeessentials.commands.player;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.core.commands.BaseCommand;
import com.forgeessentials.util.WorldUtil;
import com.forgeessentials.util.output.ChatOutputHandler;

public class CommandFly extends BaseCommand
{
    @Override
    public String getPrimaryAlias()
    {
        return "fly";
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.OP;
    }

    @Override
    public String getPermissionNode()
    {
        return ModuleCommands.PERM + ".fly";
    }

    @Override
    public void processCommandPlayer(MinecraftServer server, ServerPlayerEntity player, String[] args)
    {
        if (args.length == 0)
        {
            if (!player.abilities.mayfly)
                player.abilities.mayfly = true;
            else
                player.abilities.mayfly = false;
        }
        else
        {
            player.abilities.mayfly = Boolean.parseBoolean(args[0]);
        }
        if (!player.isOnGround())
            player.abilities.flying = player.abilities.mayfly;
        if (!player.abilities.mayfly)
            WorldUtil.placeInWorld(player);
        player.onUpdateAbilities();
        ChatOutputHandler.chatNotification(player, "Flying " + (player.abilities.mayfly ? "enabled" : "disabled"));
    }

}
