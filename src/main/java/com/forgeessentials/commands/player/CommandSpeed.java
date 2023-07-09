package com.forgeessentials.commands.player;

import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

public class CommandSpeed extends ForgeEssentialsCommandBuilder
{

    public CommandSpeed(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public String getPrimaryAlias()
    {
        return "speed";
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
        return ModuleCommands.PERM + ".speed";
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        return baseBuilder.then(Commands.literal("reset").executes(CommandContext -> execute(CommandContext, "reset")))
                .then(Commands.literal("set")
                        .then(Commands.argument("multiplier", IntegerArgumentType.integer(0))
                                .executes(CommandContext -> execute(CommandContext, "set"))))
                .executes(CommandContext -> execute(CommandContext, "current"));
    }

    @Override
    public int processCommandPlayer(CommandContext<CommandSource> ctx, String params) throws CommandSyntaxException
    {
        ChatOutputHandler.chatWarning(ctx.getSource(),
                "Here be dragons. Proceed at own risk. Use /speed reset to reset your speed..");
        // float speed = Float.parseFloat(args[0]);
        ServerPlayerEntity player = getServerPlayer(ctx.getSource());
        if (params.equals("current"))
        {
            if (((player.getAttributeBaseValue(Attributes.MOVEMENT_SPEED)) / 0.05F) == 1.0)
            {
                ChatOutputHandler.chatNotification(ctx.getSource(), "You are currently at the base movement speed");
            }
            else
            {
                ChatOutputHandler.chatNotification(ctx.getSource(), "Current movement speed is at a muntiplier of x"
                        + Double.toString(((player.getAttributeBaseValue(Attributes.MOVEMENT_SPEED)) / 0.05F)));
            }
            if (((player.getAttributeBaseValue(Attributes.FLYING_SPEED)) / 0.1F) == 1.0)
            {
                ChatOutputHandler.chatNotification(ctx.getSource(), "You are currently at the base flying speed");
            }
            else
            {
                ChatOutputHandler.chatNotification(ctx.getSource(), "Current flying speed is at a muntiplier of x"
                        + Double.toString(((player.getAttributeBaseValue(Attributes.FLYING_SPEED)) / 0.1F)));
            }
            return Command.SINGLE_SUCCESS;
        }
        if (params.equals("reset"))
        {
            ChatOutputHandler.chatNotification(ctx.getSource(), "Resetting speed to regular walking speed.");
            player.setSpeed(0.0F);

            // tagCompound.getCompound("abilities").put("flySpeed", new FloatNBT(0.05F));
            player.onUpdateAbilities();
            return Command.SINGLE_SUCCESS;
        }

        float speed = 0.05F;

        int multiplier = IntegerArgumentType.getInteger(ctx, "multiplier");

        if (multiplier >= 10)
        {
            ChatOutputHandler.chatWarning(ctx.getSource(),
                    "Multiplier set too high. Bad things may happen, so we're throttling your speed to 10x walking speed.");
            multiplier = 10;
        }
        speed = speed * multiplier;
        player.setSpeed(speed);
        // tagCompound.getCompound("abilities").put("flySpeed", new FloatNBT(speed));
        player.onUpdateAbilities();

        ChatOutputHandler.chatNotification(ctx.getSource(), "Walk/fly speed set to " + speed);
        return Command.SINGLE_SUCCESS;
    }
}
