package com.forgeessentials.jscripting.wrapper.world;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;

import com.forgeessentials.jscripting.wrapper.JsWrapper;

import cpw.mods.fml.common.registry.GameData;

public class JsBlock extends JsWrapper<Block>
{

    private static Map<Block, JsBlock> blockCache = new HashMap<>();

    /**
     * @tsd.ignore
     */
    public static JsBlock get(Block block)
    {
        JsBlock result = blockCache.get(block);
        if (result == null)
        {
            result = new JsBlock(block);
            blockCache.put(block, result);
        }
        return result;
    }

    public static JsBlock get(String name)
    {
        Block block = Block.getBlockFromName(name);
        return block == null ? null : JsBlock.get(block);
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
