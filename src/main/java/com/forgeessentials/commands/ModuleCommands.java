package com.forgeessentials.commands;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.commands.item.CommandBind;
import com.forgeessentials.commands.item.CommandCraft;
import com.forgeessentials.commands.item.CommandDechant;
import com.forgeessentials.commands.item.CommandDrop;
import com.forgeessentials.commands.item.CommandDuplicate;
import com.forgeessentials.commands.item.CommandEnchant;
import com.forgeessentials.commands.item.CommandEnderchest;
import com.forgeessentials.commands.item.CommandKit;
import com.forgeessentials.commands.item.CommandRename;
import com.forgeessentials.commands.item.CommandRepair;
import com.forgeessentials.commands.item.CommandVirtualchest;
import com.forgeessentials.commands.player.CommandAFK;
import com.forgeessentials.commands.player.CommandBubble;
import com.forgeessentials.commands.player.CommandBurn;
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
import com.forgeessentials.commands.player.CommandVanish;
import com.forgeessentials.commands.server.CommandDelayedAction;
import com.forgeessentials.commands.server.CommandGetCommandBook;
import com.forgeessentials.commands.server.CommandHelp;
import com.forgeessentials.commands.server.CommandModlist;
import com.forgeessentials.commands.server.CommandPing;
import com.forgeessentials.commands.server.CommandRules;
import com.forgeessentials.commands.server.CommandServerSettings;
import com.forgeessentials.commands.util.CommandsEventHandler;
import com.forgeessentials.commands.util.MobTypeLoader;
import com.forgeessentials.commands.util.ModuleCommandsEventHandler;
import com.forgeessentials.commands.world.CommandButcher;
import com.forgeessentials.commands.world.CommandFindblock;
import com.forgeessentials.commands.world.CommandPregen;
import com.forgeessentials.commands.world.CommandPush;
import com.forgeessentials.commands.world.CommandRemove;
import com.forgeessentials.commands.world.CommandTime;
import com.forgeessentials.commands.world.CommandWeather;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.commands.CommandFeSettings;
import com.forgeessentials.core.misc.FECommandManager;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleCommonSetupEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleRegisterCommandsEvent;

@FEModule(name = "Commands", parentMod = ForgeEssentials.class)
public class ModuleCommands
{

    public static final String PERM = "fe.commands";

    public static CommandsEventHandler oldEventHandler = new CommandsEventHandler();

    public static ModuleCommandsEventHandler eventHandler = new ModuleCommandsEventHandler();

    @SubscribeEvent
    public void preLoad(FEModuleCommonSetupEvent e)
    {
        MobTypeLoader.preLoad((FMLCommonSetupEvent) e.getFMLEvent());
    }

    @SubscribeEvent
    public void load(FEModuleCommonSetupEvent event)
    {
        APIRegistry.perms.registerPermissionDescription("fe.commands", "Permission nodes for FE commands module");
    }

    @SubscribeEvent
    private void registerCommands(FEModuleRegisterCommandsEvent event)
    {
        CommandTime time = new CommandTime(true);
        FECommandManager.registerCommand(time);
        APIRegistry.getFEEventBus().register(time);
        
        FECommandManager.registerCommand(new CommandEnchant(true));
        FECommandManager.registerCommand(new CommandDechant(true));
        FECommandManager.registerCommand(new CommandLocate(true));
        FECommandManager.registerCommand(new CommandRules(true));
        FECommandManager.registerCommand(new CommandModlist(true));
        FECommandManager.registerCommand(new CommandButcher(true));
        FECommandManager.registerCommand(new CommandRemove(true));
        //Afk
        FECommandManager.registerCommand(new CommandAFK(true));
        CommandFeSettings.addAlias("Afk", "timeout", CommandAFK.PERM_AUTOTIME);
        CommandFeSettings.addAlias("Afk", "auto_kick", CommandAFK.PERM_AUTOKICK);
        CommandFeSettings.addAlias("Afk", "warmup", CommandAFK.PERM_WARMUP);
        //Kit
        CommandKit kit = new CommandKit(true);
        FECommandManager.registerCommand(kit);
        APIRegistry.getFEEventBus().register(kit);
        
        FECommandManager.registerCommand(new CommandEnderchest(true));
        FECommandManager.registerCommand(new CommandVirtualchest(true));
        //Craft
        CommandCraft craft = new CommandCraft(true);
        FECommandManager.registerCommand(craft);
        APIRegistry.getFEEventBus().register(craft);

        FECommandManager.registerCommand(new CommandPing(true));
        FECommandManager.registerCommand(new CommandInventorySee(true));
        FECommandManager.registerCommand(new CommandSmite(true));
        FECommandManager.registerCommand(new CommandBurn(true));
        FECommandManager.registerCommand(new CommandPotion(true));
        FECommandManager.registerCommand(new CommandRepair(true));
        FECommandManager.registerCommand(new CommandHeal(true));
        FECommandManager.registerCommand(new CommandKill(true));
        FECommandManager.registerCommand(new CommandGameMode(true));
        FECommandManager.registerCommand(new CommandDoAs(true));
        FECommandManager.registerCommand(new CommandServerSettings(true));
        FECommandManager.registerCommand(new CommandGetCommandBook(true));
        //Weather
        CommandWeather weather = new CommandWeather(true);
        FECommandManager.registerCommand(weather);
        APIRegistry.getFEEventBus().register(weather);
        //Bind
        CommandBind bind = new CommandBind( true);
        FECommandManager.registerCommand(bind);
        APIRegistry.getFEEventBus().register(bind);

        FECommandManager.registerCommand(new CommandRename(true));
        FECommandManager.registerCommand(new CommandPush(true));
        FECommandManager.registerCommand(new CommandDrop(true));
        FECommandManager.registerCommand(new CommandFindblock(true));
        FECommandManager.registerCommand(new CommandNoClip(true));
        //Bubble
        CommandBubble bubble = new CommandBubble(true);
        FECommandManager.registerCommand(bubble);
        APIRegistry.getFEEventBus().register(bubble);
        
        FECommandManager.registerCommand(new CommandSpeed(true));
        FECommandManager.registerCommand(new CommandSeen(true));
        FECommandManager.registerCommand(new CommandTempBan(true));
        FECommandManager.registerCommand(new CommandFly(true));
        FECommandManager.registerCommand(new CommandHelp());
        FECommandManager.registerCommand(new CommandPregen(true));
        FECommandManager.registerCommand(new CommandVanish(true));
        FECommandManager.registerCommand(new CommandDuplicate(true));
        FECommandManager.registerCommand(new CommandDelayedAction(true));
    }
}
