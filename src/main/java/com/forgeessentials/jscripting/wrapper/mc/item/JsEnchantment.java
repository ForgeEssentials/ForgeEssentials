package com.forgeessentials.jscripting.wrapper.mc.item;

import com.forgeessentials.jscripting.wrapper.JsWrapper;

import net.minecraft.world.item.enchantment.Enchantment;

public class JsEnchantment<T extends Enchantment> extends JsWrapper<T>
{

    public JsEnchantment(T that)
    {
        super(that);
    }

    // TODO: implement this
}
