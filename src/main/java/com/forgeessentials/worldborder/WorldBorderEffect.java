package com.forgeessentials.worldborder;

import net.minecraft.command.CommandException;
import net.minecraft.entity.player.ServerPlayerEntity;

import java.util.List;

import com.forgeessentials.util.events.PlayerMoveEvent;

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

    public abstract void provideArguments(List<String> args) throws CommandException;

    public abstract String getSyntax();

    public void activate(WorldBorder border, ServerPlayerEntity player)
    {
        /* do nothing */
    }

    public void deactivate(WorldBorder border, ServerPlayerEntity player)
    {
        /* do nothing */
    }

    public void tick(WorldBorder border, ServerPlayerEntity player)
    {
        /* do nothing */
    }

    public void playerMove(WorldBorder border, PlayerMoveEvent event)
    {
        /* do nothing */
    }

}
