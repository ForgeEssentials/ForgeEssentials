package com.forgeessentials.util.selections;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

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

public class CommandPos2 extends ForgeEssentialsCommandBuilder
{
    public CommandPos2(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public String getPrimaryAlias()
    {
        return "SELfepos2";
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        return baseBuilder
                .then(Commands.literal("atLocation")
                        .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                .executes(CommandContext -> execute(CommandContext, "cord")
                                        )
                                )
                        )
                .then(Commands.literal("here")
                        .executes(CommandContext -> execute(CommandContext, "here")
                                )
                        )
                .executes(CommandContext -> execute(CommandContext, "lookPos")
                        );
    }

    @Override
    public int processCommandPlayer(CommandContext<CommandSource> ctx, String params) throws CommandSyntaxException
    {
        ServerPlayerEntity player = getServerPlayer(ctx.getSource());
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

        RayTraceResult mop = PlayerUtil.getPlayerLookingSpot(player);

        if (mop == null){
            ChatOutputHandler.chatError(player, "You must first look at the ground!");
            return Command.SINGLE_SUCCESS;
        }

        x = (int) mop.getLocation().x;
        y = (int) mop.getLocation().y;
        z = (int) mop.getLocation().z;

        WorldPoint point = new WorldPoint(player.level, x, y, z);
        if (!APIRegistry.perms.checkUserPermission(UserIdent.get(player), point, getPermissionNode())){
            ChatOutputHandler.chatError(player, "Insufficient permissions.");
            return Command.SINGLE_SUCCESS;
        }

        SelectionHandler.setEnd(player, point);

        ChatOutputHandler.chatConfirmation(player, "Pos2 set to " + x + ", " + y + ", " + z);
        return Command.SINGLE_SUCCESS;
    }

    @Override
    public String getPermissionNode()
    {
        return "fe.core.pos.pos";
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
