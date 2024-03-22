package com.forgeessentials.jscripting.wrapper.mc.world;

import java.util.HashMap;
import java.util.Map;

import com.forgeessentials.jscripting.wrapper.JsWrapper;

import net.minecraft.world.level.block.Block;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * @tsd.static Block
 */
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
        Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(name));
        return block == null ? null : JsBlock.get(block);
    }

    private JsBlock(Block that)
    {
        super(that);
    }

    public String getName()
    {
        return ForgeRegistries.BLOCKS.getKey(that).toString();
    }

}
