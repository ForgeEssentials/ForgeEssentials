package com.forgeessentials.util.selections;

import com.forgeessentials.commons.selections.Point;
import com.forgeessentials.commons.selections.Selection;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import org.jetbrains.annotations.NotNull;

public class CommandExpand extends ForgeEssentialsCommandBuilder
{

    public CommandExpand(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public @NotNull String getPrimaryAlias()
    {
        return "SELexpand";
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> setExecution()
    {
        return baseBuilder.then(Commands.argument("expand", IntegerArgumentType.integer())
                .executes(CommandContext -> execute(CommandContext, "expand"))
                .then(Commands.literal("north").executes(CommandContext -> execute(CommandContext, "north")))
                .then(Commands.literal("east").executes(CommandContext -> execute(CommandContext, "east")))
                .then(Commands.literal("south").executes(CommandContext -> execute(CommandContext, "south")))
                .then(Commands.literal("west").executes(CommandContext -> execute(CommandContext, "west")))
                .then(Commands.literal("up").executes(CommandContext -> execute(CommandContext, "up")))
                .then(Commands.literal("down").executes(CommandContext -> execute(CommandContext, "down"))));
    }

    @Override
    public int processCommandPlayer(CommandContext<CommandSourceStack> ctx, String params) throws CommandSyntaxException
    {
        ServerPlayer player = getServerPlayer(ctx.getSource());
        Selection sel = SelectionHandler.getSelection(player);
        if (sel == null)
        {
            ChatOutputHandler.chatError(player, "Invalid selection.");
            return Command.SINGLE_SUCCESS;
        }

        if (params.equals("expand"))
        {
            int x = Math.round((float) player.getLookAngle().x);
            int y = Math.round((float) player.getLookAngle().y);
            int z = Math.round((float) player.getLookAngle().z);
            int expandby = IntegerArgumentType.getInteger(ctx, "expand");

            if (x == -1)
            {
                if (sel.getStart().getX() < sel.getEnd().getX())
                {
                    SelectionHandler.setStart(player,
                            new Point(sel.getStart().getX() - expandby, sel.getStart().getY(), sel.getStart().getZ()));
                }
                else
                {
                    SelectionHandler.setEnd(player,
                            new Point(sel.getEnd().getX() - expandby, sel.getEnd().getY(), sel.getEnd().getZ()));
                }
            }
            else if (z == 1)
            {
                if (sel.getStart().getZ() < sel.getEnd().getZ())
                {
                    SelectionHandler.setStart(player,
                            new Point(sel.getStart().getX(), sel.getStart().getY(), sel.getStart().getZ() + expandby));
                }
                else
                {
                    SelectionHandler.setEnd(player,
                            new Point(sel.getEnd().getX(), sel.getEnd().getY(), sel.getEnd().getZ() + expandby));
                }
            }
            else if (x == 1)
            {
                if (sel.getStart().getX() < sel.getEnd().getX())
                {
                    SelectionHandler.setStart(player,
                            new Point(sel.getStart().getX() + expandby, sel.getStart().getY(), sel.getStart().getZ()));
                }
                else
                {
                    SelectionHandler.setEnd(player,
                            new Point(sel.getEnd().getX() + expandby, sel.getEnd().getY(), sel.getEnd().getZ()));
                }
            }
            else if (z == -1)
            {
                if (sel.getStart().getZ() < sel.getEnd().getZ())
                {
                    SelectionHandler.setStart(player,
                            new Point(sel.getStart().getX(), sel.getStart().getY(), sel.getStart().getZ() - expandby));
                }
                else
                {
                    SelectionHandler.setEnd(player,
                            new Point(sel.getEnd().getX(), sel.getEnd().getY(), sel.getEnd().getZ() - expandby));
                }
            }
            else if (y == 1)
            {
                if (sel.getStart().getY() > sel.getEnd().getY())
                {
                    SelectionHandler.setStart(player,
                            new Point(sel.getStart().getX(), sel.getStart().getY() + expandby, sel.getStart().getZ()));
                }
                else
                {
                    SelectionHandler.setEnd(player,
                            new Point(sel.getEnd().getX(), sel.getEnd().getY() + expandby, sel.getEnd().getZ()));
                }
            }
            else if (y == -1)
            {
                if (sel.getStart().getY() < sel.getEnd().getY())
                {
                    SelectionHandler.setStart(player,
                            new Point(sel.getStart().getX(), sel.getStart().getY() - expandby, sel.getStart().getZ()));
                }
                else
                {
                    SelectionHandler.setEnd(player,
                            new Point(sel.getEnd().getX(), sel.getEnd().getY() - expandby, sel.getEnd().getZ()));
                }
            }
            ChatOutputHandler.chatConfirmation(player, "Region expanded by: " + expandby);
            SelectionHandler.sendUpdate(getServerPlayer(ctx.getSource()));
            return Command.SINGLE_SUCCESS;
        }
        int expandby = IntegerArgumentType.getInteger(ctx, "expand");
        switch (params) {
            case "north":
                if (sel.getStart().getZ() < sel.getEnd().getZ()) {
                    SelectionHandler.setStart(player,
                            new Point(sel.getStart().getX(), sel.getStart().getY(), sel.getStart().getZ() - expandby));
                } else {
                    SelectionHandler.setEnd(player,
                            new Point(sel.getEnd().getX(), sel.getEnd().getY(), sel.getEnd().getZ() - expandby));
                }
                break;
            case "east":
                if (sel.getStart().getX() > sel.getEnd().getX()) {
                    SelectionHandler.setStart(player,
                            new Point(sel.getStart().getX() + expandby, sel.getStart().getY(), sel.getStart().getZ()));
                } else {
                    SelectionHandler.setEnd(player,
                            new Point(sel.getEnd().getX() + expandby, sel.getEnd().getY(), sel.getEnd().getZ()));
                }
                break;
            case "south":
                if (sel.getStart().getZ() > sel.getEnd().getZ()) {
                    SelectionHandler.setStart(player,
                            new Point(sel.getStart().getX(), sel.getStart().getY(), sel.getStart().getZ() + expandby));
                } else {
                    SelectionHandler.setEnd(player,
                            new Point(sel.getEnd().getX(), sel.getEnd().getY(), sel.getEnd().getZ() + expandby));
                }
                break;
            case "west":
                if (sel.getStart().getX() < sel.getEnd().getX()) {
                    SelectionHandler.setStart(player,
                            new Point(sel.getStart().getX() - expandby, sel.getStart().getY(), sel.getStart().getZ()));
                } else {
                    SelectionHandler.setEnd(player,
                            new Point(sel.getEnd().getX() - expandby, sel.getEnd().getY(), sel.getEnd().getZ()));
                }
                break;
            case "up":
                if (sel.getStart().getZ() > sel.getEnd().getZ()) {
                    SelectionHandler.setStart(player,
                            new Point(sel.getStart().getX(), sel.getStart().getY() + expandby, sel.getStart().getZ()));
                } else {
                    SelectionHandler.setEnd(player,
                            new Point(sel.getEnd().getX(), sel.getEnd().getY() + expandby, sel.getEnd().getZ()));
                }
                break;
            case "down":
                if (sel.getStart().getY() < sel.getEnd().getY()) {
                    SelectionHandler.setStart(player,
                            new Point(sel.getStart().getX(), sel.getStart().getY() - expandby, sel.getStart().getZ()));
                } else {
                    SelectionHandler.setEnd(player,
                            new Point(sel.getEnd().getX(), sel.getEnd().getY() - expandby, sel.getEnd().getZ()));
                }
                break;
        }
        SelectionHandler.sendUpdate(getServerPlayer(ctx.getSource()));
        ChatOutputHandler.chatConfirmation(player, "Region expanded by: " + expandby);
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
