package com.forgeessentials.teleport.commands;

import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;

import com.forgeessentials.commons.selections.WarpPoint;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.TeleportHelper;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.teleport.TeleportModule;
import com.forgeessentials.util.PlayerInfo;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class CommandBack extends ForgeEssentialsCommandBuilder
{

    public CommandBack(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public String getPrimaryAlias()
    {
        return "back";
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
        return TeleportModule.PERM_BACK;
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        return baseBuilder
                .executes(CommandContext -> execute(CommandContext, null)
                        );
    }

    @Override
    public int processCommandPlayer(CommandContext<CommandSource> ctx, String params) throws CommandSyntaxException
    {
        ServerPlayerEntity player = getServerPlayer(ctx.getSource());
        PlayerInfo pi = PlayerInfo.get(player.getUUID());
        WarpPoint point = null;
        if (PermissionAPI.hasPermission(player, TeleportModule.PERM_BACK_ONDEATH))
            point = pi.getLastDeathLocation();
        if (point == null)
            point = pi.getLastTeleportOrigin();
        if (point == null)
            throw new TranslatedCommandException("You have nowhere to get back to");

        TeleportHelper.teleport(player, point);
        return Command.SINGLE_SUCCESS;
    }

}
