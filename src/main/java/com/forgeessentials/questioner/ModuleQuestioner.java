package com.forgeessentials.questioner;

import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.data.AbstractDataDriver;
import com.forgeessentials.data.api.DataStorageManager;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

import java.io.File;

@FEModule(name = "Questioner", parentMod = ForgeEssentials.class)
public class ModuleQuestioner {
    @FEModule.ModuleDir
    public static File cmddir;

    public static ModuleQuestioner instance;

    public AbstractDataDriver data;

    @SubscribeEvent
    public void serverStarting(FMLServerStartingEvent e)
    {
        data = DataStorageManager.getReccomendedDriver();
    }
}
