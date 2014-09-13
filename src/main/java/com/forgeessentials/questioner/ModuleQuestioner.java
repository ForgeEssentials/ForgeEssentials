package com.forgeessentials.questioner;

import java.io.File;

import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.core.moduleLauncher.FEModule.Init;
import com.forgeessentials.core.moduleLauncher.FEModule.ServerInit;
import com.forgeessentials.core.moduleLauncher.FEModule.ServerStop;
import com.forgeessentials.data.AbstractDataDriver;
import com.forgeessentials.data.api.DataStorageManager;
import com.forgeessentials.util.events.modules.FEModuleInitEvent;
import com.forgeessentials.util.events.modules.FEModuleServerInitEvent;
import com.forgeessentials.util.events.modules.FEModuleServerStopEvent;

@FEModule(name = "Questioner", parentMod = ForgeEssentials.class)
public class ModuleQuestioner {
    @FEModule.ModuleDir
    public static File cmddir;

    public static ModuleQuestioner instance;

    public AbstractDataDriver data;

    @Init
    public void load(FEModuleInitEvent e)
    {

    }

    @ServerInit
    public void serverStarting(FEModuleServerInitEvent e)
    {
        data = DataStorageManager.getReccomendedDriver();
    }

    @ServerStop
    public void serverStopping(FEModuleServerStopEvent e)
    {

    }
}
