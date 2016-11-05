package com.forgeessentials.jscripting;

import java.io.File;
import java.io.IOException;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.core.Logger;

import com.forgeessentials.jscripting.wrapper.JsLocalStorage;
import com.forgeessentials.util.Utils;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;

@Mod(
        modid = JScriptingMod.MODID,
        name = "JScripting",
        version = "0.1",
        acceptableRemoteVersions = "*"
)
public class JScriptingMod
{

    public static final String MODID = "JScripting";

    @Instance(JScriptingMod.MODID)
    public static JScriptingMod instance;

    public static final File moduleDir = new File("./ForgeEssentials/JScripting");

    private static Logger logger;

    // @SidedProxy(clientSide = "com.forgeessentials.jscripting.ClientProxy", serverSide = "com.forgeessentials.jscripting.CommonProxy")
    // private static CommonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = (Logger) event.getModLog();
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(this);
        try
        {
            copyResourceFileIfNotExists("mc.d.ts");
            copyResourceFileIfNotExists("fe.d.ts");
            copyResourceFileIfNotExists("tsconfig.json");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void copyResourceFileIfNotExists(String fileName) throws IOException
    {
        File file = new File(moduleDir, fileName);
        if (!file.exists())
            FileUtils.copyInputStreamToFile(ModuleJScripting.class.getResourceAsStream(fileName), file);
    }

    @EventHandler
    public void serverStartedEvent(FMLServerStartingEvent event)
    {
        // FECommandManager.registerCommand(new CommandJScript());
        JsLocalStorage.load();
        ScriptManager.instance().loadScripts(MinecraftServer.getServer());
    }

    @EventHandler
    public void serverStartedEvent(FMLServerStartedEvent event)
    {
    }

    @EventHandler
    public void serverStoppedEvent(FMLServerStoppedEvent event)
    {
        ScriptManager.instance().unloadScripts();
        JsLocalStorage.save();
    }

}
