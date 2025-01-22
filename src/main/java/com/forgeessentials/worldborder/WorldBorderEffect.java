package com.forgeessentials.worldborder;

import net.minecraft.command.CommandException;
import net.minecraft.entity.player.EntityPlayerMP;

import com.forgeessentials.util.CommandParserArgs;
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

    public abstract void provideArguments(CommandParserArgs args) throws CommandException;

    public abstract String getSyntax();

    public void activate(WorldBorder border, EntityPlayerMP player)
    {
        /* do nothing */
    }

    public void deactivate(WorldBorder border, EntityPlayerMP player)
    {
        /* do nothing */
    }

    public void tick(WorldBorder border, EntityPlayerMP player)
    {
        /* do nothing */
    }

    public void playerMove(WorldBorder border, PlayerMoveEvent event)
    {
        /* do nothing */
    }

}
