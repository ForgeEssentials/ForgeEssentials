package com.forgeessentials.commands.item;

import java.lang.ref.WeakReference;

import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class CommandCraft extends ForgeEssentialsCommandBuilder
{

    protected WeakReference<PlayerEntity> lastPlayer = new WeakReference<>(null);

    public CommandCraft(String name, int permissionLevel, boolean enabled)
    {
        super(enabled);
    }

    @Override
    public String getPrimaryAlias()
    {
        return "craft";
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
        return ModuleCommands.PERM + ".craft";
    }

    @SubscribeEvent
    public void playerOpenContainerEvent(PlayerContainerEvent.Open event)
    {
        if (event.getContainer().stillValid(event.getPlayer()) == false && lastPlayer.get() == event.getPlayer())
        {
            event.setResult(Result.ALLOW);
        }
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        return builder
                .executes(CommandContext -> execute(CommandContext));
    }

    @Override
    public int processCommandPlayer(CommandContext<CommandSource> ctx, Object... params) throws CommandSyntaxException
    {
        ChatOutputHandler.chatNotification(ctx.getSource(),"This feature is currently unimplimented");
        ChatOutputHandler.chatNotification(ctx.getSource(),"as forge made it impossable to make a custom");
        ChatOutputHandler.chatNotification(ctx.getSource(),"crafting gui without modifications on the client.");
        return Command.SINGLE_SUCCESS;
    }
}
