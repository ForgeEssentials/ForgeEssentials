package com.forgeessentials.worldborder.effect;

import com.forgeessentials.util.events.PlayerMoveEvent;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.worldborder.WorldBorder;
import com.forgeessentials.worldborder.WorldBorderEffect;

public class EffectBlock extends WorldBorderEffect
{
    @Override
    public boolean provideArguments(String[] args)
    {
        return true;
    }

    @Override
    public String getSyntax()
    {
        return "";
    }

    @Override
    public void playerMove(WorldBorder border, PlayerMoveEvent event)
    {
        ChatOutputHandler.chatWarning(event.getPlayer(), "You're not allowed to move past the world border!");
        event.setCanceled(true);
    }
}
