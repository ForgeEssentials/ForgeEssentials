package com.forgeessentials.commands;

import java.io.File;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.commands.shortcut.ShortcutCommands;
import com.forgeessentials.commands.util.CommandDataManager;
import com.forgeessentials.commands.util.CommandRegistrar;
import com.forgeessentials.commands.util.CommandsEventHandler;
import com.forgeessentials.commands.util.ConfigCmd;
import com.forgeessentials.commands.util.MobTypeLoader;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModulePreInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStopEvent;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

@FEModule(configClass = ConfigCmd.class, name = "CommandsModule", parentMod = ForgeEssentials.class)
public class ModuleCommands {
    @FEModule.Config
    public static ConfigCmd conf;

    @FEModule.ModuleDir
    public static File cmddir;

    public static CommandsEventHandler eventHandler = new CommandsEventHandler();

    @SubscribeEvent
    public void preLoad(FEModulePreInitEvent e)
    {
        MobTypeLoader.preLoad(e);
        MinecraftForge.EVENT_BUS.register(eventHandler);
        FMLCommonHandler.instance().bus().register(eventHandler);
    }

    @SubscribeEvent
    public void load(FEModuleInitEvent e)
    {
        CommandRegistrar.commandConfigs(conf.getConfig());
        ShortcutCommands.loadConfig(cmddir);
    }

    @SubscribeEvent
    public void serverStarting(FEModuleServerInitEvent e)
    {
    	CommandRegistrar.registerCommands(e);
        ShortcutCommands.load();
        CommandDataManager.load();
        APIRegistry.perms.registerPermissionDescription("fe.commands", "Permission nodes for FE commands module");
        APIRegistry.perms.registerPermission("fe.commands.*", RegisteredPermValue.OP);
    }

    @FEModule.Reload
    public void reload(ICommandSender sender)
    {
        ShortcutCommands.parseConfig();
        ShortcutCommands.load();
    }

    @SubscribeEvent
    public void serverStopping(FEModuleServerStopEvent e)
    {
        CommandDataManager.save();
    }

}
