package com.forgeessentials.worldborder;

import com.forgeessentials.util.output.LoggingHandler;
import com.forgeessentials.worldborder.effect.EffectBlock;
import com.forgeessentials.worldborder.effect.EffectCommand;
import com.forgeessentials.worldborder.effect.EffectDamage;
import com.forgeessentials.worldborder.effect.EffectKick;
import com.forgeessentials.worldborder.effect.EffectKnockback;
import com.forgeessentials.worldborder.effect.EffectMessage;
import com.forgeessentials.worldborder.effect.EffectPotion;
import com.forgeessentials.worldborder.effect.EffectSmite;

public enum WorldBorderEffects
{
    COMMAND(EffectCommand.class),
    DAMAGE(EffectDamage.class),
    KICK(EffectKick.class),
    KNOCKBACK(EffectKnockback.class),
    MESSAGE(EffectMessage.class),
    POTION(EffectPotion.class),
    SMITE(EffectSmite.class),
    BLOCK(EffectBlock.class);

    public Class<?> clazz;

    WorldBorderEffects(Class<?> clazz)
    {
        this.clazz = clazz;
    }

    public WorldBorderEffect get()
    {
        try
        {
            return (WorldBorderEffect) clazz.newInstance();
        }
        catch (Exception e)
        {
            LoggingHandler.felog.error("There was a problem initializing Worldborder effects.");
            return null;
        }
    }

}
