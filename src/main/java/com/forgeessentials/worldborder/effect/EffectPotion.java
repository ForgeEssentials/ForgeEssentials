package com.forgeessentials.worldborder.effect;

import com.forgeessentials.core.misc.FECommandParsingException;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.worldborder.WorldBorder;
import com.forgeessentials.worldborder.WorldBorderEffect;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.CommandSource;
import net.minecraft.command.arguments.PotionArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;

/**
 * Expected syntax: <interval> <effect> <seconds> <amplifier>
 */
public class EffectPotion extends WorldBorderEffect
{

    public int id;

    public int duration;

    public int modifier;

    public int interval;

    @Override
    public void provideArguments(CommandContext<CommandSource> ctx) throws FECommandParsingException
    {
        interval = IntegerArgumentType.getInteger(ctx, "interval");
        try
        {
            id = Effect.getId(PotionArgument.getEffect(ctx, "effect"));
        }
        catch (CommandSyntaxException e)
        {
            throw new FECommandParsingException("Bad effect argument");
        }
        duration = IntegerArgumentType.getInteger(ctx, "seconds");
        modifier = IntegerArgumentType.getInteger(ctx, "amplifier");
    }

    @Override
    public void activate(WorldBorder border, ServerPlayerEntity player)
    {
        if (interval <= 0)
        {
        }
        player.addEffect(new EffectInstance(Effect.byId(id), duration, modifier, false, true, true));
    }

    @Override
    public void tick(WorldBorder border, ServerPlayerEntity player)
    {
        if (interval <= 0)
            return;
        PlayerInfo pi = PlayerInfo.get(player);
        if (pi.checkTimeout(this.getClass().getName()))
        {
            player.addEffect(new EffectInstance(Effect.byId(id), duration, modifier, false, true, true));
            pi.startTimeout(this.getClass().getName(), interval * 1000);
        }
    }

    @Override
    public String getSyntax()
    {
        return "<interval> <effect> <seconds> <amplifier>";
    }

    @Override
    public String toString()
    {
        return String.format("potion interval: %d id: %d duration: %d amplifier: %d", interval, id, duration, modifier);
    }
}
