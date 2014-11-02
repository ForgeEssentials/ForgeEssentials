package com.forgeessentials.snooper;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.core.moduleLauncher.ModuleLauncher;
import com.forgeessentials.snooper.response.PlayerInfoResponse;
import com.forgeessentials.snooper.response.PlayerInv;
import com.forgeessentials.snooper.response.Responses;
import com.forgeessentials.snooper.response.ServerInfo;
import com.forgeessentials.util.FunctionHelper;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.common.MinecraftForge;

import javax.crypto.KeyGenerator;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

@FEModule(name = "SnooperModule", parentMod = ForgeEssentials.class, configClass = ConfigSnooper.class)
public class ModuleSnooper {
    @FEModule.Config
    public static ConfigSnooper configSnooper;
    public static int port;
    public static String hostname;
    public static boolean enable;
    public static SocketListner socketListner;
    public static String key;
    private static int id = 0;
    @FEModule.ModuleDir
    public File folder;

    public ModuleSnooper()
    {
        MinecraftForge.EVENT_BUS.register(this);

        APIRegistry.registerResponse(0, new Responses());

        APIRegistry.registerResponse(1, new ServerInfo());

        APIRegistry.registerResponse(5, new PlayerInfoResponse());
        APIRegistry.registerResponse(6, new PlayerInv());
    }

    public static void start()
    {
        socketListner = new SocketListner();
    }

    public static void stop()
    {
        socketListner.stop();
    }

    public static int id()
    {
        return id++;
    }

    @SubscribeEvent
    public void load(FMLPreInitializationEvent e)
    {
        if (!enable)
        {
            ModuleLauncher.instance.unregister("SnooperModule");
        }
    }

    @SubscribeEvent
    public void serverStarting(FMLServerStartingEvent e)
    {
        getKey();
        FunctionHelper.registerServerCommand(new CommandReloadQuery());
        start();
    }

    private void getKey()
    {
        try
        {
            File file = new File(folder, "key.key");
            if (file.exists())
            {
                FileInputStream in = new FileInputStream(file);
                byte[] buffer = new byte[in.available()];
                in.read(buffer);
                in.close();
                key = new String(buffer);
            }
            else
            {
                file.createNewFile();
                FileOutputStream out = new FileOutputStream(file.getAbsoluteFile());
                KeyGenerator kgen = KeyGenerator.getInstance("AES");
                kgen.init(128);
                byte[] buffer = kgen.generateKey().getEncoded();
                out.write(buffer);
                out.close();
                key = new String(buffer);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    @SubscribeEvent
    public void serverStopping(FMLServerStoppingEvent e)
    {
        stop();
    }

    @FEModule.Reload()
    public void reload(ICommandSender sender)
    {
        stop();
        getKey();
        start();
    }
}
