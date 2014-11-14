package com.forgeessentials.questioner;

import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.core.moduleLauncher.FEModule.Instance;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerInitEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

@FEModule(name = "Questioner", parentMod = ForgeEssentials.class)
public class ModuleQuestioner {

    @Instance
    public static ModuleQuestioner instance;

    @SubscribeEvent
    public void serverStarting(FEModuleServerInitEvent e)
    {
    }

}
