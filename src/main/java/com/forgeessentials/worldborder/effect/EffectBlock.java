package com.forgeessentials.worldborder.effect;

import com.forgeessentials.core.commands.registration.FECommandParsingException;
import com.forgeessentials.util.events.player.PlayerMoveEvent;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.worldborder.WorldBorder;
import com.forgeessentials.worldborder.WorldBorderEffect;
import com.mojang.brigadier.context.CommandContext;

import net.minecraft.commands.CommandSourceStack;

public class EffectBlock extends WorldBorderEffect
{
    @Override
    public void provideArguments(CommandContext<CommandSourceStack> ctx) throws FECommandParsingException
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
        ChatOutputHandler.chatWarning(event.getPlayer(), "You're not allowed to move past the world border!");
        event.setCanceled(true);
    }
}
