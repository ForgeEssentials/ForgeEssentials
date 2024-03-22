package com.forgeessentials.commands.item;

import java.lang.ref.WeakReference;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import org.jetbrains.annotations.NotNull;

public class CommandCraft extends ForgeEssentialsCommandBuilder
{

    protected WeakReference<Player> lastPlayer = new WeakReference<>(null);

    public CommandCraft(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public @NotNull String getPrimaryAlias()
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

    @SubscribeEvent
    public void playerOpenContainerEvent(PlayerContainerEvent.Open event)
    {
        if (!event.getContainer().stillValid(event.getPlayer()) && lastPlayer.get() == event.getPlayer())
        {
            event.setResult(Result.ALLOW);
        }
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> setExecution()
    {
        return baseBuilder.executes(CommandContext -> execute(CommandContext, "blank"));
    }

    @Override
    public int processCommandPlayer(CommandContext<CommandSourceStack> ctx, String params) throws CommandSyntaxException
    {
        ServerPlayer player = getServerPlayer(ctx.getSource());
        ctx.getSource().getPlayerOrException()
                .openMenu(new SimpleMenuProvider(
                        (i, playerInventory, playerEntity) -> new CraftingMenu(i, playerInventory,
                                ContainerLevelAccess.create(player.getCommandSenderWorld(), player.blockPosition())) {
                            public boolean stillValid(@NotNull Player p_75145_1_)
                            {
                                return true;
                            }
                        }, new TextComponent("FE Virtual Crafting")));
        return Command.SINGLE_SUCCESS;
    }
}
