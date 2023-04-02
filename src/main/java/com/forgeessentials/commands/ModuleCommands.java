package com.forgeessentials.commands;

import net.minecraft.command.CommandSource;
import net.minecraftforge.common.MinecraftForge;
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
import com.forgeessentials.commands.world.CommandPush;
import com.forgeessentials.commands.world.CommandRemove;
import com.forgeessentials.commands.world.CommandTime;
import com.forgeessentials.commands.world.CommandWeather;
import com.forgeessentials.compat.HelpFixer;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.commands.CommandFeSettings;
import com.forgeessentials.core.misc.FECommandManager;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStartingEvent;
import com.forgeessentials.util.events.FERegisterCommandsEvent;
import com.mojang.brigadier.CommandDispatcher;

@FEModule(name = "Commands", parentMod = ForgeEssentials.class)
public class ModuleCommands
{

    public static final String PERM = "fe.commands";

    public static CommandsEventHandler oldEventHandler = new CommandsEventHandler();

    public static ModuleCommandsEventHandler eventHandler = new ModuleCommandsEventHandler();

    @SubscribeEvent
    public void load(FEModuleServerStartingEvent event)
    {
    	MobTypeLoader.init();
        APIRegistry.perms.registerPermissionDescription("fe.commands", "Permission nodes for FE commands module");
    }

    @SubscribeEvent
    public void registerCommands(FERegisterCommandsEvent event)
    {
        CommandDispatcher<CommandSource> dispatcher = event.getRegisterCommandsEvent().getDispatcher();
        CommandTime time = new CommandTime(true);
        FECommandManager.registerCommand(time, dispatcher);
        MinecraftForge.EVENT_BUS.register(time);
        
        FECommandManager.registerCommand(new CommandEnchant(true), dispatcher);
        FECommandManager.registerCommand(new CommandDechant(true), dispatcher);
        FECommandManager.registerCommand(new CommandLocate(true), dispatcher);
        FECommandManager.registerCommand(new CommandRules(true), dispatcher);
        FECommandManager.registerCommand(new CommandModlist(true), dispatcher);
        FECommandManager.registerCommand(new CommandButcher(true), dispatcher);
        FECommandManager.registerCommand(new CommandRemove(true), dispatcher);
        //Afk
        FECommandManager.registerCommand(new CommandAFK(true), dispatcher);
        CommandFeSettings.addAlias("Afk", "timeout", CommandAFK.PERM_AUTOTIME);
        CommandFeSettings.addAlias("Afk", "auto_kick", CommandAFK.PERM_AUTOKICK);
        CommandFeSettings.addAlias("Afk", "warmup", CommandAFK.PERM_WARMUP);
        //Kit
        CommandKit kit = new CommandKit(true);
        FECommandManager.registerCommand(kit, dispatcher);
        MinecraftForge.EVENT_BUS.register(kit);
        
        FECommandManager.registerCommand(new CommandEnderchest(true), dispatcher);
        FECommandManager.registerCommand(new CommandVirtualchest(true), dispatcher);
        //Craft
        CommandCraft craft = new CommandCraft(true);
        FECommandManager.registerCommand(craft, dispatcher);
        MinecraftForge.EVENT_BUS.register(craft);

        FECommandManager.registerCommand(new CommandPing(true), dispatcher);
        FECommandManager.registerCommand(new CommandInventorySee(true), dispatcher);
        FECommandManager.registerCommand(new CommandSmite(true), dispatcher);
        FECommandManager.registerCommand(new CommandBurn(true), dispatcher);
        FECommandManager.registerCommand(new CommandPotion(true), dispatcher);
        FECommandManager.registerCommand(new CommandRepair(true), dispatcher);
        FECommandManager.registerCommand(new CommandHeal(true), dispatcher);
        FECommandManager.registerCommand(new CommandKill(true), dispatcher);
        FECommandManager.registerCommand(new CommandGameMode(true), dispatcher);
        FECommandManager.registerCommand(new CommandDoAs(true), dispatcher);
        FECommandManager.registerCommand(new CommandServerSettings(true), dispatcher);
        FECommandManager.registerCommand(new CommandGetCommandBook(true), dispatcher);
        //Weather
        CommandWeather weather = new CommandWeather(true);
        FECommandManager.registerCommand(weather, dispatcher);
        MinecraftForge.EVENT_BUS.register(weather);
        //Bind
        CommandBind bind = new CommandBind( true);
        FECommandManager.registerCommand(bind, dispatcher);
        MinecraftForge.EVENT_BUS.register(bind);

        FECommandManager.registerCommand(new CommandRename(true), dispatcher);
        FECommandManager.registerCommand(new CommandPush(true), dispatcher);
        FECommandManager.registerCommand(new CommandDrop(true), dispatcher);
        FECommandManager.registerCommand(new CommandFindblock(true), dispatcher);
        FECommandManager.registerCommand(new CommandNoClip(true), dispatcher);
        //Bubble
        CommandBubble bubble = new CommandBubble(true);
        FECommandManager.registerCommand(bubble, dispatcher);
        MinecraftForge.EVENT_BUS.register(bubble);
        
        FECommandManager.registerCommand(new CommandSpeed(true), dispatcher);
        FECommandManager.registerCommand(new CommandSeen(true), dispatcher);
        FECommandManager.registerCommand(new CommandTempBan(true), dispatcher);
        FECommandManager.registerCommand(new CommandFly(true), dispatcher);
        //Help
        CommandHelp help = new CommandHelp(true, dispatcher);
        FECommandManager.registerCommand(help, dispatcher);
        help.fixer = new HelpFixer();

        FECommandManager.registerCommand(new CommandVanish(true), dispatcher);
        FECommandManager.registerCommand(new CommandDuplicate(true), dispatcher);
        FECommandManager.registerCommand(new CommandDelayedAction(true), dispatcher);
    }
}
