package com.forgeessentials.worldborder.effect;

import com.forgeessentials.core.commands.registration.FECommandParsingException;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.worldborder.WorldBorder;
import com.forgeessentials.worldborder.WorldBorderEffect;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;

/**
 * Expected syntax: <interval> <damage>
 *
 * Use 20 for damage if you want to kill him, interval is always in seconds.
 */
public class EffectDamage extends WorldBorderEffect
{

    private int interval = 1000;

    private int damage = 1;

    @Override
    public void provideArguments(CommandContext<CommandSourceStack> ctx) throws FECommandParsingException
    {
        interval = IntegerArgumentType.getInteger(ctx, "interval");
        damage = IntegerArgumentType.getInteger(ctx, "damage");
    }

    @Override
    public void tick(WorldBorder border, ServerPlayer player)
    {
        PlayerInfo pi = PlayerInfo.get(player);
        if (pi.checkTimeout(this.getClass().getName()))
        {
            player.hurt(DamageSource.OUT_OF_WORLD, damage);
            pi.startTimeout(this.getClass().getName(), interval * 1000L);
        }
    }

    public String toString()
    {
        return "damage trigger: " + triggerDistance + " damage: " + damage;
    }

    public String getSyntax()
    {
        return "<interval> <damage>";
    }

}
