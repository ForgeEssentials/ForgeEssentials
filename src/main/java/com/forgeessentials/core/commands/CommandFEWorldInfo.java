package com.forgeessentials.core.commands;

import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fmllegacy.server.ServerLifecycleHooks;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import org.jetbrains.annotations.NotNull;

public class CommandFEWorldInfo extends ForgeEssentialsCommandBuilder
{

    public CommandFEWorldInfo(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> setExecution()
    {
        return baseBuilder.executes(CommandContext -> execute(CommandContext, "blank"));
    }

    @Override
    public int execute(CommandContext<CommandSourceStack> ctx, String params) throws CommandSyntaxException
    {
        ChatOutputHandler.chatNotification(ctx.getSource(), "Showing all world provider names:");
        for (Level world : ServerLifecycleHooks.getCurrentServer().getAllLevels())
        {
            ChatOutputHandler.chatNotification(ctx.getSource(), Translator.format("%s - %s",
                    world.dimension().location().getPath(), world.dimension().location().toString()));
        }
        return Command.SINGLE_SUCCESS;
    }

    @Override
    public @NotNull String getPrimaryAlias()
    {
        return "feworldinfo";
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.OP;
    }
}
