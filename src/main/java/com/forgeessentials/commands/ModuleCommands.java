package com.forgeessentials.commands;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.commands.network.S5PacketNoclip;
import com.forgeessentials.commands.shortcut.ShortcutCommands;
import com.forgeessentials.commands.util.CommandDataManager;
import com.forgeessentials.commands.util.CommandRegistrar;
import com.forgeessentials.commands.util.CommandsEventHandler;
import com.forgeessentials.commands.util.ConfigCmd;
import com.forgeessentials.commands.util.MobTypeLoader;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModulePreInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStopEvent;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;

@FEModule(name = "Commands", parentMod = ForgeEssentials.class)
public class ModuleCommands {
    
    public static CommandsEventHandler eventHandler = new CommandsEventHandler();

    @SubscribeEvent
    public void preLoad(FEModulePreInitEvent e)
    {
        MobTypeLoader.preLoad((FMLPreInitializationEvent)e.getFMLEvent());
        MinecraftForge.EVENT_BUS.register(eventHandler);
        FMLCommonHandler.instance().bus().register(eventHandler);
        FunctionHelper.netHandler.registerMessage(S5PacketNoclip.class, S5PacketNoclip.class, 5, Side.CLIENT);
    }

    @SubscribeEvent
    public void load(FEModuleInitEvent e)
    {
        ForgeEssentials.getConfigManager().registerLoader("Commands", new ConfigCmd());
        ShortcutCommands.loadConfig(ForgeEssentials.getFEDirectory());
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
