package com.forgeessentials.core.commands;

import net.minecraft.command.CommandSource;
import net.minecraft.world.World;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class CommandFEWorldInfo extends BaseCommand
{

    public CommandFEWorldInfo(String name, int permissionLevel, boolean enabled)
    {
        super(name, permissionLevel, enabled);
    }

    @Override
    public int execute(CommandContext<CommandSource> ctx, Object... params) throws CommandSyntaxException
    {
        ChatOutputHandler.chatNotification(ctx.getSource(), "Showing all world provider names:");
        for (World world : ServerLifecycleHooks.getCurrentServer().getAllLevels())
        {
            ChatOutputHandler.chatNotification(ctx.getSource(),Translator.format("%s - %s", world.dimension(), world.dimension().getRegistryName()));
        }
        return Command.SINGLE_SUCCESS;
    }

    @Override
    public String getPrimaryAlias()
    {
        return "feworldinfo";
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public String getPermissionNode()
    {
        return "fe.commands.feworldinfo";
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.OP;
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        return builder
                .executes(CommandContext -> execute(CommandContext)
                        );
    }
}
