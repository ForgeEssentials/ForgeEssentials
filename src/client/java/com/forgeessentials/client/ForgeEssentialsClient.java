package com.forgeessentials.client;

import com.forgeessentials.client.cui.CUIPlayerLogger;
import com.forgeessentials.client.cui.CUIRenderrer;
import com.forgeessentials.client.cui.CUIRollback;
import cpw.mods.fml.client.IModGuiFactory;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Set;

@Mod(modid = "ForgeEssentialsClient", name = "Forge Essentials Client Addon", version = "%VERSION%", guiFactory = "com.forgeessentials.client.ForgeEssentialsClient.FEGUIFactory")
public class ForgeEssentialsClient {

    public static Logger feclientlog;
    @SideOnly(Side.CLIENT)
    private static PlayerInfoClient info;
    protected static boolean allowCUI;
    private ClientConfig config;

    @SideOnly(Side.CLIENT)
    public static PlayerInfoClient getInfo()
    {
        if (info == null)
        {
            info = new PlayerInfoClient();
        }
        return info;
    }

    @SideOnly(Side.CLIENT)
    public static void setInfo(PlayerInfoClient info)
    {
        ForgeEssentialsClient.info = info;
    }

    private boolean getDevOverride()
    {
        String prop = System.getProperty("forgeessentials.developermode");
        if (prop != null && prop.equals("true"))
        { // FOR DEVS ONLY! THAT IS WHY IT IS A PROPERTY!!!

            feclientlog.warn("Developer mode has been enabled, things may break.");
            return true;
        }
        else
        {
            return false;
        }
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent e)
    {
        feclientlog = LogManager.getLogger("ForgeEssentials");

        if (FMLCommonHandler.instance().getSide().isServer() && getDevOverride() == false)
        {
            throw new RuntimeException("ForgeEssentialsClient should not be installed on a server!");
        }

        if (FMLCommonHandler.instance().getSide().isClient())
        {
            config = new ClientConfig(new Configuration(e.getSuggestedConfigurationFile()));
            config.init();
        }
    }



    @SideOnly(Side.CLIENT)
    @EventHandler
    public void load(FMLInitializationEvent e)
    {

        FMLCommonHandler.instance().bus().register(new ClientEventHandler());
        if (allowCUI)
        {
            MinecraftForge.EVENT_BUS.register(new CUIRenderrer());
            MinecraftForge.EVENT_BUS.register(new CUIPlayerLogger());
            MinecraftForge.EVENT_BUS.register(new CUIRollback());
        }
    }

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

}
