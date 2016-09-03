package com.forgeessentials.jscripting.wrapper.item;

public class JsItemStatic
{

    /**
     * @deprecated Use mc.item.Item.get(name) instead
     */
    @Deprecated
    public JsItem getItem(String name)
    {
        return JsItem.get(name);
    }

}
