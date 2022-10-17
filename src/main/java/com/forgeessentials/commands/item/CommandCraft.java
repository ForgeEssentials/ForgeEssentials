package com.forgeessentials.commands.item;

import java.lang.ref.WeakReference;

import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.WorkbenchContainer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.core.commands.BaseCommand;
import com.forgeessentials.util.CommandUtils;
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
        if (event.getContainer().canInteractWith(event.getPlayer()) == false && lastPlayer.get() == event.getPlayer())
        {
            event.setResult(Result.ALLOW);
        }
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int processCommandPlayer(CommandContext<CommandSource> ctx, Object... params) throws CommandSyntaxException
    {
        lastPlayer = new WeakReference<>(CommandUtils.getServerPlayer(ctx.getSource()));
        CommandUtils.getServerPlayer(ctx.getSource()).openMenu(new WorkbenchContainer.InterfaceCraftingTable(player.level, player.getPosition()));
    }
}
