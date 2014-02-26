package com.forgeessentials.worldborder.Effects;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.Configuration;

import com.forgeessentials.worldborder.WorldBorder;


public interface IEffect
{
	void registerConfig(Configuration config, String category);

	void execute(WorldBorder wb, EntityPlayerMP player);
}
