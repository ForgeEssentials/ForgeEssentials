package com.forgeessentials.commands;

import com.forgeessentials.api.APIRegistry.ForgeEssentialsRegistrar.PermRegister;
import com.forgeessentials.api.permissions.IPermRegisterEvent;
import com.forgeessentials.api.permissions.RegGroup;
import com.forgeessentials.commands.shortcut.ShortcutCommands;
import com.forgeessentials.commands.util.*;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.util.events.modules.FEModuleInitEvent;
import com.forgeessentials.util.events.modules.FEModulePreInitEvent;
import com.forgeessentials.util.events.modules.FEModuleServerInitEvent;
import com.forgeessentials.util.events.modules.FEModuleServerStopEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.common.MinecraftForge;

import java.io.File;

@FEModule(configClass = ConfigCmd.class, name = "CommandsModule", parentMod = ForgeEssentials.class)
public class ModuleCommands {
    @FEModule.Config
    public static ConfigCmd conf;

    @FEModule.ModuleDir
    public static File cmddir;

    public static EventHandler eventHandler = new EventHandler();

    @PermRegister
    public static void registerPermissions(IPermRegisterEvent event)
    {
        CommandRegistrar.registerPermissions(event);
        event.registerPermissionLevel("fe.commands._ALL_", RegGroup.OWNERS);
    }

    @FEModule.PreInit
    public void preLoad(FEModulePreInitEvent e)
    {
        MobTypeLoader.preLoad(e);
        MinecraftForge.EVENT_BUS.register(eventHandler);
    }

    @FEModule.Init
    public void load(FEModuleInitEvent e)
    {

        CommandRegistrar.commandConfigs(conf.config);
        ShortcutCommands.loadConfig(cmddir);
    }

    @FEModule.ServerInit
    public void serverStarting(FEModuleServerInitEvent e)
    {
        CommandRegistrar.load((FMLServerStartingEvent) e.getFMLEvent());
        ShortcutCommands.load();
        CommandDataManager.load();
    }

    @FEModule.Reload
    public void reload(ICommandSender sender)
    {
        ShortcutCommands.parseConfig();
        ShortcutCommands.load();
    }

    @FEModule.ServerStop
    public void serverStopping(FEModuleServerStopEvent e)
    {
        CommandDataManager.save();
    }

}
