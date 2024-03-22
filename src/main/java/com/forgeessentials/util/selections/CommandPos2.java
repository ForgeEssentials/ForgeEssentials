package com.forgeessentials.util.selections;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.commons.selections.Point;
import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.util.PlayerUtil;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import org.jetbrains.annotations.NotNull;

public class CommandPos2 extends ForgeEssentialsCommandBuilder
{
    public CommandPos2(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public @NotNull String getPrimaryAlias()
    {
        return "SELfepos2";
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> setExecution()
    {
        return baseBuilder
                .then(Commands.literal("atLocation")
                        .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                .executes(CommandContext -> execute(CommandContext, "cord"))))
                .then(Commands.literal("here").executes(CommandContext -> execute(CommandContext, "here")))
                .executes(CommandContext -> execute(CommandContext, "lookPos"));
    }

    @Override
    public int processCommandPlayer(CommandContext<CommandSourceStack> ctx, String params) throws CommandSyntaxException
    {
        ServerPlayer player = getServerPlayer(ctx.getSource());
        int x, y, z;

        if (params.equals("here"))
        {
            x = (int) player.position().x;
            y = (int) player.position().y;
            z = (int) player.position().z;

            SelectionHandler.setEnd(player, new Point(x, y, z));

            ChatOutputHandler.chatConfirmation(player, "Pos2 set to " + x + ", " + y + ", " + z);
            return Command.SINGLE_SUCCESS;
        }

        if (params.equals("cord"))
        {

            x = BlockPosArgument.getLoadedBlockPos(ctx, "pos").getX();
            y = BlockPosArgument.getLoadedBlockPos(ctx, "pos").getY();
            z = BlockPosArgument.getLoadedBlockPos(ctx, "pos").getZ();

            SelectionHandler.setEnd(player, new Point(x, y, z));

            ChatOutputHandler.chatConfirmation(player, "Pos2 set to " + x + ", " + y + ", " + z);
            return Command.SINGLE_SUCCESS;
        }

        HitResult mop = PlayerUtil.getPlayerLookingSpot(player);

        if (mop.getType() == HitResult.Type.MISS)
        {
            ChatOutputHandler.chatError(player, "You must first look at the ground!");
            return Command.SINGLE_SUCCESS;
        }

        x = (int) mop.getLocation().x;
        y = (int) mop.getLocation().y;
        z = (int) mop.getLocation().z;

        WorldPoint point = new WorldPoint(player.level, x, y, z);
        if (!APIRegistry.perms.checkUserPermission(UserIdent.get(player), point, "fe.core.pos.pos"))
        {
            ChatOutputHandler.chatError(player, "Insufficient permissions.");
            return Command.SINGLE_SUCCESS;
        }

        SelectionHandler.setEnd(player, point);
        SelectionHandler.sendUpdate(getServerPlayer(ctx.getSource()));
        ChatOutputHandler.chatConfirmation(player, "Pos2 set to " + x + ", " + y + ", " + z);
        return Command.SINGLE_SUCCESS;
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

}
