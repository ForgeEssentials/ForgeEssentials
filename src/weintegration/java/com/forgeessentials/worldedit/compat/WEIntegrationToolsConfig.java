package com.forgeessentials.worldedit.compat;

import com.forgeessentials.core.moduleLauncher.ModuleConfigBase;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.common.Configuration;

import java.io.File;

public class WEIntegrationToolsConfig extends ModuleConfigBase {
    private Configuration config;

    public WEIntegrationToolsConfig(File file)
    {
        super(file);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void init()
    {
        config = new Configuration(file, true);
        WEIntegration.syncInterval = config.get("WEIntegrationTools", "syncInterval", 20, "Interval in ticks to sync selections.").getInt();
        config.save();
    }

    @Override
    public void forceSave()
    {
    }

    @Override
    public void forceLoad(ICommandSender sender)
    {
        config.load();
        WEIntegration.syncInterval = config.get("WEIntegrationTools", "syncInterval", 20, "Interval in ticks to sync selections.").getInt();
        config.save();
    }
}