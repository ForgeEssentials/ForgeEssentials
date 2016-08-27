package com.forgeessentials.jscripting.wrapper;

import net.minecraft.block.Block;

public class JsStaticBlock
{

    public JsBlock<Block> getBlockFromName(String name)
    {
        return new JsBlock<>(Block.getBlockFromName(name));
    }

}
