package com.forgeessentials.client.gui.forge;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;

import com.forgeessentials.client.core.ClientConfig;

import cpw.mods.fml.client.config.GuiConfig;

public class FEConfigGUI extends GuiConfig {

    public FEConfigGUI(GuiScreen parentScreen)
    {
        super(parentScreen,
                new ConfigElement(ClientConfig.config.getCategory(Configuration.CATEGORY_GENERAL)).getChildElements(),
                "TestMod", false, false, "FE Client Addon Config");
    }
    
}
