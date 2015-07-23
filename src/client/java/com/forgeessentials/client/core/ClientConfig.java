package com.forgeessentials.client.core;

import net.minecraftforge.common.config.Configuration;

import com.forgeessentials.client.ForgeEssentialsClient;

import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientConfig
{

    private static Configuration config;

    public ClientConfig(Configuration config)
    {
        ClientConfig.config = config;
        FMLCommonHandler.instance().bus().register(this);
    }

    public void init()
    {
        getConfig().load();
        getConfig().addCustomCategoryComment(Configuration.CATEGORY_GENERAL, "Configure ForgeEssentials Client addon features.");
        syncConfig();
        getConfig().save();
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent e)
    {
        if (e.modID.equals("ForgeEssentialsClient"))
            syncConfig();
    }

    public void syncConfig()
    {
        ForgeEssentialsClient.allowCUI = getConfig().getBoolean("allowCUI", Configuration.CATEGORY_GENERAL, true,
                "Set to false to disable graphical selections.");
        ForgeEssentialsClient.allowQRCodeRender = getConfig().get(Configuration.CATEGORY_GENERAL, "allowQRCodeRender", true,
                "Set to false to disable QR code rendering when you enter /remote qr..").getBoolean(true);

    }

    public static Configuration getConfig()
    {
        return config;
    }

}
