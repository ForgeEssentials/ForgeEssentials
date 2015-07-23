package com.forgeessentials.client;

import java.util.Map;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkCheckHandler;
import net.minecraftforge.fml.relauncher.Side;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.forgeessentials.client.util.DummyProxy;
import com.forgeessentials.commons.BuildInfo;

@Mod(modid = "ForgeEssentialsClient", name = "ForgeEssentials Client Addon", version = BuildInfo.VERSION, guiFactory = "com.forgeessentials.client.gui.forge.FEGUIFactory", useMetadata = true)
public class ForgeEssentialsClient
{

    public static final Logger feclientlog = LogManager.getLogger("forgeessentials");

    @SidedProxy(clientSide = "com.forgeessentials.client.core.ClientProxy", serverSide = "com.forgeessentials.client.util.DummyProxy")
    protected static DummyProxy proxy;

    @Instance("ForgeEssentialsClient")
    protected static ForgeEssentialsClient instance;

    private static boolean serverHasFE;
    public static boolean allowCUI;
    public static boolean allowQRCodeRender;

    @NetworkCheckHandler
    public boolean getServerMods(Map<String, String> map, Side side)
    {
        if (side.equals(Side.SERVER))
        {
            if (map.containsKey("ForgeEssentials"))
            {
                serverHasFE = true;
                feclientlog.info("The server is running ForgeEssentials.");
            }
        }
        return true;
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent e)
    {
        if (e.getSide() == Side.SERVER)
            feclientlog.error("ForgeEssentials client does nothing on servers. You should remove it!");
        proxy.doPreInit(e);
    }

    @EventHandler
    public void load(FMLInitializationEvent e)
    {
        proxy.load(e);
    }

    public static boolean serverHasFE()
    {
        return serverHasFE;
    }

}
