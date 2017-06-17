package com.forgeessentials.client.gui.forge;

import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class FEGUIFactory implements IModGuiFactory
{
    @Override
    public void initialize(Minecraft minecraftInstance)
    {
        /* do nothing */
    }

    @Override public boolean hasConfigGui()
    {
        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override public GuiScreen createConfigGui(GuiScreen guiScreen)
    {
        return new FEConfigGUI(guiScreen);
    }

    @Override
    public Class<? extends GuiScreen> mainConfigGuiClass()
    {
        return FEConfigGUI.class;
    }

    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories()
    {
        return null;
    }

    @Override
    public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element)
    {
        return null;
    }
}
