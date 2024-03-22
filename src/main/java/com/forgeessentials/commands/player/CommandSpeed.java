package com.forgeessentials.commands.player;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import java.util.UUID;

import org.jetbrains.annotations.NotNull;

public class CommandSpeed extends ForgeEssentialsCommandBuilder
{
	private static final UUID FE_SPEED_MODIFER = UUID.fromString("eb224087-0848-4d12-9b93-2a4a5cf4bafa");
    public CommandSpeed(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public @NotNull String getPrimaryAlias()
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
    public LiteralArgumentBuilder<CommandSourceStack> setExecution()
    {
        return baseBuilder
        		.then(Commands.literal("reset")
        				.executes(CommandContext -> execute(CommandContext, "reset")))
                .then(Commands.literal("set")
                        .then(Commands.argument("multiplier", IntegerArgumentType.integer(0))
                                .executes(CommandContext -> execute(CommandContext, "set"))))
                .then(Commands.literal("current")
        				.executes(CommandContext -> execute(CommandContext, "current")));
    }

    @Override
    public int processCommandPlayer(CommandContext<CommandSourceStack> ctx, String params) throws CommandSyntaxException
    {
        ChatOutputHandler.chatWarning(ctx.getSource(),
                "Here be dragons. Proceed at own risk. Use /speed reset to reset your speed..");
        // float speed = Float.parseFloat(args[0]);
        ServerPlayer player = getServerPlayer(ctx.getSource());
        if (params.equals("current"))
        {
            if (((player.getAttributeBaseValue(Attributes.MOVEMENT_SPEED)) / 0.05F) == 2.0)
            {
                ChatOutputHandler.chatNotification(ctx.getSource(), "You are currently at the base movement speed");
            }
            else
            {
                ChatOutputHandler.chatNotification(ctx.getSource(), "Current movement speed is at a multiplier of x"
                        + Double.toString(((player.getAttributeBaseValue(Attributes.MOVEMENT_SPEED)) / 0.05F)));
            }
            return Command.SINGLE_SUCCESS;
        }
        if (params.equals("reset"))
        {
            ChatOutputHandler.chatNotification(ctx.getSource(), "Resetting speed to regular walking speed.");
            AttributeInstance modifiableattributeinstance = player.getAttribute(Attributes.MOVEMENT_SPEED);
            if (modifiableattributeinstance != null) {
               if (modifiableattributeinstance.getModifier(FE_SPEED_MODIFER) != null) {
                  modifiableattributeinstance.removeModifier(FE_SPEED_MODIFER);
               }

            }
            return Command.SINGLE_SUCCESS;
        }

        int multiplier = IntegerArgumentType.getInteger(ctx, "multiplier");

        if (multiplier >= 10)
        {
            ChatOutputHandler.chatWarning(ctx.getSource(),
                    "Multiplier set too high. Bad things may happen, so we're throttling your speed to 10x walking speed.");
            multiplier = 10;
        }

        AttributeInstance modifiableattributeinstance = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (modifiableattributeinstance != null) {
            if (modifiableattributeinstance.getModifier(FE_SPEED_MODIFER) != null) {
               modifiableattributeinstance.removeModifier(FE_SPEED_MODIFER);
            }
        }
        else {
        	ChatOutputHandler.chatError(ctx.getSource(), "Failed to set movement speed!.");
        	return Command.SINGLE_SUCCESS;
        }

        modifiableattributeinstance.addTransientModifier(new AttributeModifier(FE_SPEED_MODIFER, "FE speed command boost", (double) multiplier, AttributeModifier.Operation.MULTIPLY_TOTAL));

        ChatOutputHandler.chatNotification(ctx.getSource(), "Walk/fly speed set to x" + multiplier + " base speed");
        return Command.SINGLE_SUCCESS;
    }
}
