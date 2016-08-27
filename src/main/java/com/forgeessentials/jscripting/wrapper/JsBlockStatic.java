package com.forgeessentials.jscripting.wrapper;

import net.minecraft.block.Block;

public class JsBlockStatic
{

    public JsBlock<Block> getBlock(String name)
    {
        Block block = Block.getBlockFromName(name);
        return block == null ? null : new JsBlock<>(block);
    }

}
