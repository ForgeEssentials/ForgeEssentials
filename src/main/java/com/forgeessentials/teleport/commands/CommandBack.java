package com.forgeessentials.teleport.commands;

import com.forgeessentials.commons.selections.WarpPoint;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.TeleportHelper;
import com.forgeessentials.teleport.TeleportModule;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import org.jetbrains.annotations.NotNull;

public class CommandBack extends ForgeEssentialsCommandBuilder
{

    public CommandBack(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public @NotNull String getPrimaryAlias()
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
    public LiteralArgumentBuilder<CommandSourceStack> setExecution()
    {
        return baseBuilder.executes(CommandContext -> execute(CommandContext, "blank"));
    }

    @Override
    public int processCommandPlayer(CommandContext<CommandSourceStack> ctx, String params) throws CommandSyntaxException
    {
        ServerPlayer player = getServerPlayer(ctx.getSource());
        PlayerInfo pi = PlayerInfo.get(player.getGameProfile().getId());
        WarpPoint point = null;
        if (hasPermission(player.createCommandSourceStack(), TeleportModule.PERM_BACK_ONDEATH))
            point = pi.getLastDeathLocation();
        if (point == null && hasPermission(player.createCommandSourceStack(), TeleportModule.PERM_BACK_ONTP))
            point = pi.getLastTeleportOrigin();
        if (point == null)
        {
            ChatOutputHandler.chatError(ctx.getSource(), "You have nowhere to get back to");
            return Command.SINGLE_SUCCESS;
        }

        TeleportHelper.teleport(player, point);
        return Command.SINGLE_SUCCESS;
    }

}
