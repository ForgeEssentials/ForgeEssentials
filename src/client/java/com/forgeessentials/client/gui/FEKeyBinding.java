package com.forgeessentials.client.gui;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;
import cpw.mods.fml.common.TickType;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.world.World;

import java.util.EnumSet;

public class FEKeyBinding extends KeyHandler {

    public static int fekeycode;

    public FEKeyBinding(KeyBinding[] keyBindings)
    {
        super(new KeyBinding[] { new KeyBinding("ForgeEssentials Menu", fekeycode) }, new boolean[] { false });
    }

    @Override
    public String getLabel()
    {

        return "forgeessentials.keyhandler";
    }

    @Override
    public void keyDown(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd, boolean isRepeat)
    {

    }

    @Override
    public void keyUp(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd)
    {

        if (FMLClientHandler.instance().getClient().currentScreen == null)
        {
            FMLClientHandler.instance().getClient().displayGuiScreen(new GuiFEMain());
            World world = FMLClientHandler.instance().getClient().theWorld;
        }
        // TODO: tap key again to close fe menu
    }

    @Override
    public EnumSet<TickType> ticks()
    {
        return EnumSet.of(TickType.CLIENT);
    }

}
