package com.forgeessentials.client.gui.forge;

import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;

public class FEGUIFactory implements IModGuiFactory
{
    @Override
    public void initialize(Minecraft minecraftInstance)
    {
        /* do nothing */
    }

    @Override
    public boolean hasConfigGui() {
        return true;
    }

    @Override
    public GuiScreen createConfigGui(GuiScreen guiScreen) {
        return new FEConfigGUI(guiScreen);
    }

    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories()
    {
        return null;
    }
}
