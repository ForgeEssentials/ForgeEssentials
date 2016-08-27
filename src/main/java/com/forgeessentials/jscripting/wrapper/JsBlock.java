package com.forgeessentials.jscripting.wrapper;

import net.minecraft.block.Block;

import cpw.mods.fml.common.registry.GameData;

public class JsBlock<T extends Block> extends JsWrapper<T>
{

    public JsBlock(T that)
    {
        super(that);
    }

    public String getName()
    {
        return GameData.getBlockRegistry().getNameForObject(that);
    }

}
