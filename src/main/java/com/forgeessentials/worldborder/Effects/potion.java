package com.forgeessentials.worldborder.Effects;

import com.forgeessentials.worldborder.WorldBorder;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.common.Configuration;

import java.util.ArrayList;
import java.util.List;

public class potion implements IEffect {
    private List<PotionEffect> potionEffectsList = new ArrayList<PotionEffect>();

    @Override
    public void registerConfig(Configuration config, String category)
    {
        String[] potionEffects =
                { "9:5:0" };

        config.addCustomCategoryComment(category, "For more information, go to http://www.minecraftwiki.net/wiki/Potion_effects#Parameters");
        potionEffects = config.get(category, "potionEffects", potionEffects, "Format like this: 'ID:duration:amplifier'").getStringList();

        for (String poisonEffect : potionEffects)
        {
            String[] split = poisonEffect.split(":");
            potionEffectsList.add(new PotionEffect(Integer.parseInt(split[0]), Integer.parseInt(split[1]) * 20, Integer.parseInt(split[2])));
        }
    }

    @Override
    public void execute(WorldBorder wb, EntityPlayerMP player)
    {
        for (PotionEffect potionEffect : potionEffectsList)
        {
            player.addPotionEffect(potionEffect);
        }
    }
}
