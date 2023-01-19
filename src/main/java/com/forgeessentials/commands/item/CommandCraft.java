package com.forgeessentials.commands.item;

import java.lang.ref.WeakReference;

import net.minecraft.block.CraftingTableBlock;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.core.commands.BaseCommand;
import com.forgeessentials.util.CommandUtils;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class CommandCraft extends BaseCommand
{

    protected WeakReference<PlayerEntity> lastPlayer = new WeakReference<>(null);

    public CommandCraft(String name, int permissionLevel, boolean enabled)
    {
        super(name, permissionLevel, enabled);
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
        lastPlayer = new WeakReference<>(CommandUtils.getServerPlayer(ctx.getSource()));
        CraftingTableBlock craftingTableBlock = new CraftingTableBlock(null);
        getServerPlayer(ctx.getSource()).openMenu(craftingTableBlock.getMenuProvider(null, getServerPlayer(ctx.getSource()).getCommandSenderWorld(), getServerPlayer(ctx.getSource()).blockPosition()));
        return Command.SINGLE_SUCCESS;
    }
}
