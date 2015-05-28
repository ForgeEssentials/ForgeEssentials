package com.forgeessentials.commands;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.commands.item.CommandBind;
import com.forgeessentials.commands.item.CommandCraft;
import com.forgeessentials.commands.item.CommandDrop;
import com.forgeessentials.commands.item.CommandEnchant;
import com.forgeessentials.commands.item.CommandEnderchest;
import com.forgeessentials.commands.item.CommandKit;
import com.forgeessentials.commands.item.CommandRename;
import com.forgeessentials.commands.item.CommandRepair;
import com.forgeessentials.commands.item.CommandVirtualchest;
import com.forgeessentials.commands.player.CommandAFK;
import com.forgeessentials.commands.player.CommandBubble;
import com.forgeessentials.commands.player.CommandBurn;
import com.forgeessentials.commands.player.CommandCapabilities;
import com.forgeessentials.commands.player.CommandDoAs;
import com.forgeessentials.commands.player.CommandFly;
import com.forgeessentials.commands.player.CommandGameMode;
import com.forgeessentials.commands.player.CommandHeal;
import com.forgeessentials.commands.player.CommandInventorySee;
import com.forgeessentials.commands.player.CommandKill;
import com.forgeessentials.commands.player.CommandLocate;
import com.forgeessentials.commands.player.CommandNoClip;
import com.forgeessentials.commands.player.CommandPotion;
import com.forgeessentials.commands.player.CommandSeen;
import com.forgeessentials.commands.player.CommandSmite;
import com.forgeessentials.commands.player.CommandSpeed;
import com.forgeessentials.commands.player.CommandTempBan;
import com.forgeessentials.commands.server.CommandGetCommandBook;
import com.forgeessentials.commands.server.CommandHelp;
import com.forgeessentials.commands.server.CommandModlist;
import com.forgeessentials.commands.server.CommandPing;
import com.forgeessentials.commands.server.CommandRules;
import com.forgeessentials.commands.server.CommandServerSettings;
import com.forgeessentials.commands.util.CommandDataManager;
import com.forgeessentials.commands.util.CommandsEventHandler;
import com.forgeessentials.commands.util.MobTypeLoader;
import com.forgeessentials.commands.util.ModuleCommandsEventHandler;
import com.forgeessentials.commands.world.CommandButcher;
import com.forgeessentials.commands.world.CommandFindblock;
import com.forgeessentials.commands.world.CommandPush;
import com.forgeessentials.commands.world.CommandRemove;
import com.forgeessentials.commands.world.CommandTime;
import com.forgeessentials.commands.world.CommandWeather;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.misc.FECommandManager;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.util.events.FEModuleEvent.FEModulePreInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStopEvent;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

@FEModule(name = "Commands", parentMod = ForgeEssentials.class)
public class ModuleCommands
{

    static
    {
        FECommandManager.registerCommand(new CommandTime());
        FECommandManager.registerCommand(new CommandEnchant());
        FECommandManager.registerCommand(new CommandLocate());
        FECommandManager.registerCommand(new CommandRules());
        FECommandManager.registerCommand(new CommandModlist());
        FECommandManager.registerCommand(new CommandButcher());
        FECommandManager.registerCommand(new CommandRemove());
        FECommandManager.registerCommand(new CommandAFK());
        FECommandManager.registerCommand(new CommandKit());
        FECommandManager.registerCommand(new CommandEnderchest());
        FECommandManager.registerCommand(new CommandVirtualchest());
        FECommandManager.registerCommand(new CommandCapabilities());
        FECommandManager.registerCommand(new CommandCraft());
        FECommandManager.registerCommand(new CommandPing());
        FECommandManager.registerCommand(new CommandInventorySee());
        FECommandManager.registerCommand(new CommandSmite());
        FECommandManager.registerCommand(new CommandBurn());
        FECommandManager.registerCommand(new CommandPotion());
        FECommandManager.registerCommand(new CommandRepair());
        FECommandManager.registerCommand(new CommandHeal());
        FECommandManager.registerCommand(new CommandKill());
        FECommandManager.registerCommand(new CommandGameMode());
        FECommandManager.registerCommand(new CommandDoAs());
        FECommandManager.registerCommand(new CommandServerSettings());
        FECommandManager.registerCommand(new CommandGetCommandBook());
        FECommandManager.registerCommand(new CommandWeather());
        FECommandManager.registerCommand(new CommandBind());
        FECommandManager.registerCommand(new CommandRename());
        // FECommandManager.registerCommand(new CommandVanish());
        FECommandManager.registerCommand(new CommandPush());
        FECommandManager.registerCommand(new CommandDrop());
        FECommandManager.registerCommand(new CommandFindblock());
        FECommandManager.registerCommand(new CommandNoClip());
        FECommandManager.registerCommand(new CommandBubble());
        FECommandManager.registerCommand(new CommandSpeed());
        FECommandManager.registerCommand(new CommandSeen());
        FECommandManager.registerCommand(new CommandTempBan());
        FECommandManager.registerCommand(new CommandFly());
        FECommandManager.registerCommand(new CommandHelp());
    }

    public static CommandsEventHandler oldEventHandler = new CommandsEventHandler();

    public static ModuleCommandsEventHandler eventHandler = new ModuleCommandsEventHandler();

    @SubscribeEvent
    public void preLoad(FEModulePreInitEvent e)
    {
        MobTypeLoader.preLoad((FMLPreInitializationEvent) e.getFMLEvent());
    }

    @SubscribeEvent
    public void serverStarting(FEModuleServerInitEvent e)
    {
        CommandDataManager.load();
        APIRegistry.perms.registerPermissionDescription("fe.commands", "Permission nodes for FE commands module");
    }

    @SubscribeEvent
    public void serverStopping(FEModuleServerStopEvent e)
    {
        CommandDataManager.save();
    }

}
