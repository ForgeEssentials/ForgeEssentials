package com.forgeessentials.commands.item;

import java.lang.ref.WeakReference;

import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.inventory.container.WorkbenchContainer;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

public class CommandCraft extends ForgeEssentialsCommandBuilder
{

    protected WeakReference<PlayerEntity> lastPlayer = new WeakReference<>(null);

    public CommandCraft(boolean enabled)
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
        return baseBuilder.executes(CommandContext -> execute(CommandContext, "blank"));
    }

    @Override
    public int processCommandPlayer(CommandContext<CommandSource> ctx, String params) throws CommandSyntaxException
    {
        ServerPlayerEntity player = getServerPlayer(ctx.getSource());
        ctx.getSource().getPlayerOrException()
                .openMenu(new SimpleNamedContainerProvider(
                        (i, playerInventory, playerEntity) -> new WorkbenchContainer(i, playerInventory,
                                IWorldPosCallable.create(player.getCommandSenderWorld(), player.blockPosition())) {
                            public boolean stillValid(PlayerEntity p_75145_1_)
                            {
                                return true;
                            }
                        }, new StringTextComponent("FE Virtual Crafting")));
        return Command.SINGLE_SUCCESS;
    }
}
