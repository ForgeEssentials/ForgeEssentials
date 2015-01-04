package com.forgeessentials.economy;

import com.forgeessentials.core.moduleLauncher.config.IConfigLoader.ConfigLoaderBase;
import cpw.mods.fml.common.registry.GameData;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.common.config.Configuration;

import java.util.HashMap;
import java.util.Map;

public class ItemTables extends ConfigLoaderBase
{
    protected Map<String, Integer> valueMap = new HashMap<>();

    private static final String category = "ItemTables";

    @Override
    public void load(Configuration config, boolean isReload)
    {
        for (Item i : GameData.getItemRegistry().typeSafeIterable())
        {
            valueMap.put(i.getUnlocalizedName(), config.get(category, i.getUnlocalizedName(), 1).getInt(1));
        }
        for (Block b : GameData.getBlockRegistry().typeSafeIterable())
        {
            valueMap.put(b.getUnlocalizedName(), config.get(category, b.getUnlocalizedName(), 1).getInt(1));
        }
    }
}
