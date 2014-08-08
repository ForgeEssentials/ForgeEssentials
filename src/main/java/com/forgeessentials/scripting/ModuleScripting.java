package com.forgeessentials.scripting;

import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.events.modules.FEModulePreInitEvent;
import com.forgeessentials.util.events.modules.FEModuleServerInitEvent;
import cpw.mods.fml.common.registry.GameRegistry;

import java.io.File;

@FEModule(name = "scripting", parentMod = ForgeEssentials.class, isCore = false)
public class ModuleScripting {

    @FEModule.ModuleDir
    public static File moduleDir = new File(ForgeEssentials.FEDIR, "scripting/");

    @FEModule.PreInit
    public void preInit(FEModulePreInitEvent e)
    {
        OutputHandler.felog.info("Scripts are being read from " + moduleDir.getAbsolutePath());
        startup();
        GameRegistry.registerPlayerTracker(new ScriptPlayerTracker());
    }

    @FEModule.ServerInit
    public void serverStarting(FEModuleServerInitEvent e)
    {
        e.registerServerCommand(new CommandScript());
        e.registerServerCommand(new TimedTaskManager());
    }

    public static void startup()
    {
        try
        {
            moduleDir.mkdirs();
            for (EventType e : EventType.values())
            {
                e.mkdirs();
            }

        }
        catch (Exception e)
        {
            OutputHandler.felog.warning("Could not setup scripting folders - you might have to do it yourself.");
        }
    }
}
