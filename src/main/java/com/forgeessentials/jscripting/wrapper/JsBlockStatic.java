package com.forgeessentials.jscripting.wrapper;

import net.minecraft.block.Block;

public class JsBlockStatic
{

    public JsBlock getBlock(String name)
    {
        Block block = Block.getBlockFromName(name);
        return block == null ? null : JsBlock.get(block);
    }

}
