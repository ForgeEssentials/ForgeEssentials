package com.forgeessentials.core.preloader;

import java.util.HashMap;

/**
 * Kindly do not reference any FE classes outside the coremod package in this
 * class. This is a store room for all String[]s used by the coremod, 99% of
 * stuff is edited here and not in the actual coremod classes.
 * <p/>
 * Updating instructions:
 * Each OBF list has a corresponding DEV list. Using a mapping viewer of your choice, get the new OBF name for each
 * piece of data in the DEV list.
 * <p/>
 * Change me if this class is updated for future minecraft versions
 * Compatible MC version: 1.7.10
 */

public class Data {

    public static HashMap<String, String> ISob = new HashMap<String, String>();// ItemStack, OBF, placeEvent
    public static HashMap<String, String> ISdev = new HashMap<String, String>();// ItemStack, DEV, placeEvent

    static
    {

        ISob.put("className", "add");
        ISob.put("javaClassName", "add");
        ISob.put("targetMethodName", "a");// searge name func_77943_a
        ISob.put("itemstackJavaClassName", "add");
        ISob.put("entityPlayerJavaClassName", "yz");
        ISob.put("worldJavaClassName", "ahb");

        ISdev.put("className", "net.minecraft.item.ItemStack");
        ISdev.put("javaClassName", "net/minecraft/item/ItemStack");
        ISdev.put("targetMethodName", "tryPlaceItemIntoWorld");
        ISdev.put("itemstackJavaClassName", "net/minecraft/item/ItemStack");
        ISdev.put("entityPlayerJavaClassName", "net/minecraft/entity/player/EntityPlayer");
        ISdev.put("worldJavaClassName", "net/minecraft/world/World");

    }

    protected static String[] transformers = {
            "com.forgeessentials.core.preloader.asm.FEeventAdder",
            "com.forgeessentials.core.preloader.asm.FEAccessTransformer"
    };
}
