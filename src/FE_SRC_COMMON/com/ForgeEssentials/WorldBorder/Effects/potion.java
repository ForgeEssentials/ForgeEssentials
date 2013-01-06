package com.ForgeEssentials.WorldBorder.Effects;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.common.Configuration;

public class potion implements IEffect
{
	private List<PotionEffect> potionEffectsList = new ArrayList();

	@Override
	public void registerConfig(Configuration config, String category)
	{
		String[] potionEffects = { "9:5:0" };

		config.addCustomCategoryComment(
				category,
				"For more information, go to http://www.minecraftwiki.net/wiki/Potion_effects#Parameters");
		potionEffects = config.get(category, "potionEffects", potionEffects,
				"Format like this: 'ID:duration:amplifire'").valueList;

		for (String poisonEffect : potionEffects)
		{
			String[] split = poisonEffect.split(":");
			potionEffectsList
					.add(new PotionEffect(Integer.parseInt(split[0]), Integer
							.parseInt(split[1]) * 20, Integer
							.parseInt(split[2])));
		}

	}

	@Override
	public void execute(EntityPlayerMP player)
	{
		for (PotionEffect potionEffect : potionEffectsList)
		{
			player.addPotionEffect(potionEffect);
		}
	}
}
