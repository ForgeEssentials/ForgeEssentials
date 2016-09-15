package com.forgeessentials.worldborder.effect;

import net.minecraft.command.CommandException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.DamageSource;

import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.util.CommandParserArgs;
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
    public void provideArguments(CommandParserArgs args) throws CommandException
    {
        if (args.isEmpty())
            throw new TranslatedCommandException("Missing interval argument");
        interval = args.parseInt();

        if (args.isEmpty())
            throw new TranslatedCommandException("Missing damage argument");
        damage = args.parseInt();
    }

    @Override
    public void tick(WorldBorder border, EntityPlayerMP player)
    {
        PlayerInfo pi = PlayerInfo.get(player);
        if (pi.checkTimeout(this.getClass().getName()))
        {
            player.attackEntityFrom(DamageSource.outOfWorld, damage);
            pi.startTimeout(this.getClass().getName(), interval *  1000);
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
