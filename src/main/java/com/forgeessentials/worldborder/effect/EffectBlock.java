package com.forgeessentials.worldborder.effect;

import com.forgeessentials.commons.CommandParserArgs;
import com.forgeessentials.util.ChatUtil;
import com.forgeessentials.util.events.PlayerMoveEvent;
import com.forgeessentials.util.questioner.QuestionerStillActiveException.CommandException;
import com.forgeessentials.worldborder.WorldBorder;
import com.forgeessentials.worldborder.WorldBorderEffect;

public class EffectBlock extends WorldBorderEffect
{
    @Override
    public void provideArguments(CommandParserArgs args) throws CommandException
    {
    }

    @Override
    public String getSyntax()
    {
        return "";
    }

    @Override
    public void playerMove(WorldBorder border, PlayerMoveEvent event)
    {
        ChatUtil.chatWarning(event.getPlayer(), "You're not allowed to move past the world border!");
        event.setCanceled(true);
    }
}
