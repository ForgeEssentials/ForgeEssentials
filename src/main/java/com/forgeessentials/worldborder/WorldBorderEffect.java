package com.forgeessentials.worldborder;

import com.forgeessentials.core.commands.registration.FECommandParsingException;
import com.forgeessentials.util.events.player.PlayerMoveEvent;
import com.mojang.brigadier.context.CommandContext;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

public abstract class WorldBorderEffect
{

    protected int triggerDistance = 0;

    public WorldBorderEffect()
    {
    }

    public WorldBorderEffect(int triggerDistance)
    {
        this.triggerDistance = triggerDistance;
    }

    public double getTriggerDistance()
    {
        return triggerDistance;
    }

    public abstract void provideArguments(CommandContext<CommandSourceStack> ctx) throws FECommandParsingException;

    public abstract String getSyntax();

    public void activate(WorldBorder border, ServerPlayer player)
    {
        /* do nothing */
    }

    public void deactivate(WorldBorder border, ServerPlayer player)
    {
        /* do nothing */
    }

    public void tick(WorldBorder border, ServerPlayer player)
    {
        /* do nothing */
    }

    public void playerMove(WorldBorder border, PlayerMoveEvent event)
    {
        /* do nothing */
    }

}
