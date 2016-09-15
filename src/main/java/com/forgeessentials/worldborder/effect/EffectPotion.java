package com.forgeessentials.worldborder.effect;

import net.minecraft.command.CommandException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.PotionEffect;

import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.data.v2.Loadable;
import com.forgeessentials.util.CommandParserArgs;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.worldborder.WorldBorder;
import com.forgeessentials.worldborder.WorldBorderEffect;

/**
 * Expected syntax: <interval> <effect> <seconds> <amplifier>
 */
public class EffectPotion extends WorldBorderEffect implements Loadable
{

    public int id;

    public int duration;

    public int modifier;

    public int interval;

    public EffectPotion()
    {
    }

    @Override
    public void provideArguments(CommandParserArgs args) throws CommandException
    {
        if (args.isEmpty())
            throw new TranslatedCommandException("Missing interval argument");
        interval = args.parseInt();

        if (args.isEmpty())
            throw new TranslatedCommandException("Missing potion id argument");
        id = args.parseInt();

        if (args.isEmpty())
            throw new TranslatedCommandException("Missing duration id argument");
        duration = args.parseInt();

        if (args.isEmpty())
            throw new TranslatedCommandException("Missing modifier id argument");
        modifier = args.parseInt();
    }

    @Override
    public void afterLoad()
    {
        if (id == 0)
        {
            id = 9;
            duration = 5;
            modifier = 0;
        }
    }

    @Override
    public void activate(WorldBorder border, EntityPlayerMP player)
    {
        if (interval <= 0)
            player.addPotionEffect(new PotionEffect(id, duration, modifier));
    }

    @Override
    public void tick(WorldBorder border, EntityPlayerMP player)
    {
        if (interval <= 0)
            return;
        PlayerInfo pi = PlayerInfo.get(player);
        if (pi.checkTimeout(this.getClass().getName()))
        {
            player.addPotionEffect(new PotionEffect(id, duration, modifier));
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
        return String.format("potion interval: %d1 id: %d2 duration: %d3 amplifier: %d4", interval, id, duration, modifier);
    }

}
