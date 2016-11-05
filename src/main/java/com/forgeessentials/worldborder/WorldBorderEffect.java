package com.forgeessentials.worldborder;

import net.minecraft.entity.player.EntityPlayerMP;

import com.forgeessentials.util.FeCommandParserArgs;
import com.forgeessentials.util.events.PlayerMoveEvent;
import com.forgeessentials.util.questioner.QuestionerStillActiveException.CommandException;

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

    public abstract void provideArguments(FeCommandParserArgs args) throws CommandException;

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
