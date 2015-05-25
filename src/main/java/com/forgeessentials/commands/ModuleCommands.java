package com.forgeessentials.commands;

import net.minecraftforge.common.MinecraftForge;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.commands.util.CommandDataManager;
import com.forgeessentials.commands.util.CommandRegistrar;
import com.forgeessentials.commands.util.CommandsEventHandler;
import com.forgeessentials.commands.util.ConfigCmd;
import com.forgeessentials.commands.util.LoginMessage;
import com.forgeessentials.commands.util.MobTypeLoader;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModulePreInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStopEvent;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

@FEModule(name = "Commands", parentMod = ForgeEssentials.class)
public class ModuleCommands
{

    public static CommandsEventHandler oldEventHandler = new CommandsEventHandler();

    public static ModuleCommandsEventHandler eventHandler = new ModuleCommandsEventHandler();

    @SubscribeEvent
    public void preLoad(FEModulePreInitEvent e)
    {
        MobTypeLoader.preLoad((FMLPreInitializationEvent) e.getFMLEvent());
        MinecraftForge.EVENT_BUS.register(oldEventHandler);
        FMLCommonHandler.instance().bus().register(oldEventHandler);
        LoginMessage.loadFile();
    }

    @SubscribeEvent
    public void load(FEModuleInitEvent e)
    {
        ForgeEssentials.getConfigManager().registerLoader("Commands", new ConfigCmd());
    }

    @SubscribeEvent
    public void serverStarting(FEModuleServerInitEvent e)
    {
        CommandRegistrar.registerCommands(e);
        CommandDataManager.load();

        APIRegistry.perms.registerPermissionDescription("fe.commands", "Permission nodes for FE commands module");
    }

    @SubscribeEvent
    public void serverStopping(FEModuleServerStopEvent e)
    {
        CommandDataManager.save();
    }

}
