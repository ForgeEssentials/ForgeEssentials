package com.forgeessentials.commands;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.RegGroup;
import com.forgeessentials.commands.shortcut.ShortcutCommands;
import com.forgeessentials.commands.util.*;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.compat.CompatMCStats;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.util.events.modules.FEModuleInitEvent;
import com.forgeessentials.util.events.modules.FEModulePreInitEvent;
import com.forgeessentials.util.events.modules.FEModuleServerInitEvent;
import com.forgeessentials.util.events.modules.FEModuleServerStopEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
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
    private static MCStatsHelper mcstats = new MCStatsHelper();

    @FEModule.PreInit
    public void preLoad(FEModulePreInitEvent e)
    {
        MobTypeLoader.preLoad(e);
        GameRegistry.registerPlayerTracker(new PlayerTrackerCommands());
    }

    @FEModule.Init
    public void load(FEModuleInitEvent e)
    {
        MinecraftForge.EVENT_BUS.register(eventHandler);
        CommandRegistrar.commandConfigs(conf.config);
        ShortcutCommands.loadConfig(cmddir);
        CompatMCStats.registerStats(mcstats);
        TickRegistry.registerScheduledTickHandler(new TickHandlerCommands(), Side.SERVER);
        new PacketAnalyzerCmd();
    }

    @FEModule.ServerInit
    public void serverStarting(FEModuleServerInitEvent e)
    {
        CommandRegistrar.load((FMLServerStartingEvent) e.getFMLEvent());
        ShortcutCommands.load();
        CommandDataManager.load();
        CommandRegistrar.registerPermissions();
        APIRegistry.permReg.registerPermissionLevel("fe.commands._ALL_", RegGroup.OWNERS);
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
