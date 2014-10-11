package com.forgeessentials.teleport.util;

import com.forgeessentials.core.moduleLauncher.ModuleConfigBase;
import com.forgeessentials.teleport.TeleportModule;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.common.config.Configuration;

public class ConfigTeleport extends ModuleConfigBase {

    private Configuration config;

    @Override
    public void init()
    {
        config = new Configuration(file, true);
        TeleportModule.timeout = config.get("main", "timeout", 25, "Amount of sec a user has to accept a TPA request").getInt();
        config.save();

    }

    @Override
    public void forceSave()
    {
        config = new Configuration(file, true);
        config.get("main", "timeout", 25, "Amount of sec a user has to accept a TPA request").set(TeleportModule.timeout);
        config.save();
    }

    @Override
    public void forceLoad(ICommandSender sender)
    {
        config = new Configuration(file, true);
        TeleportModule.timeout = config.get("main", "timeout", 25, "Amount of sec a user has to accept a TPA request").getInt();
        config.save();
    }

    public boolean universalConfigAllowed(){return true;}

}