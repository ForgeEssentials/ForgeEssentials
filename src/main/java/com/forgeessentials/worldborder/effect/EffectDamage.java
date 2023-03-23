package com.forgeessentials.worldborder.effect;

import net.minecraft.command.CommandException;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.DamageSource;

import java.util.List;

import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.util.CommandUtils;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.worldborder.WorldBorder;
import com.forgeessentials.worldborder.WorldBorderEffect;

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
    public void provideArguments(List<String> args) throws CommandException
    {
        if (args.isEmpty())
            throw new TranslatedCommandException("Missing interval argument");
        interval = CommandUtils.parseInt(args.remove(0));

        if (args.isEmpty())
            throw new TranslatedCommandException("Missing damage argument");
        damage = CommandUtils.parseInt(args.remove(0));
    }

    @Override
    public void tick(WorldBorder border, ServerPlayerEntity player)
    {
        PlayerInfo pi = PlayerInfo.get(player);
        if (pi.checkTimeout(this.getClass().getName()))
        {
            player.hurt(DamageSource.OUT_OF_WORLD, damage);
            pi.startTimeout(this.getClass().getName(), interval * 1000);
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
