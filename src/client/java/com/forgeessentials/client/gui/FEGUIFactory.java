package com.forgeessentials.client.gui;

import com.forgeessentials.client.ClientConfig;
import cpw.mods.fml.client.IModGuiFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

import java.util.Set;

public class FEGUIFactory implements IModGuiFactory {
    @Override public void initialize(Minecraft minecraftInstance)
    {

    }

    @Override public Class<? extends GuiScreen> mainConfigGuiClass()
    {
        return ClientConfig.FEConfigGUI.class;
    }

    @Override public Set<RuntimeOptionCategoryElement> runtimeGuiCategories()
    {
        return null;
    }

    @Override public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element)
    {
        return null;
    }
}
