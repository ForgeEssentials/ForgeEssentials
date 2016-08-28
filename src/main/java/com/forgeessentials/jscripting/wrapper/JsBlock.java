package com.forgeessentials.jscripting.wrapper;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;

import cpw.mods.fml.common.registry.GameData;

public class JsBlock extends JsWrapper<Block>
{

    private static Map<Block, JsBlock> blockCache = new HashMap<>();

    public static JsBlock get(Block block) // tsgen ignore
    {
        JsBlock result = blockCache.get(block);
        if (result == null)
        {
            result = new JsBlock(block);
            blockCache.put(block, result);
        }
        return result;
    }

    private JsBlock(Block that)
    {
        super(that);
    }

    public String getName()
    {
        return GameData.getBlockRegistry().getNameForObject(that);
    }

}
