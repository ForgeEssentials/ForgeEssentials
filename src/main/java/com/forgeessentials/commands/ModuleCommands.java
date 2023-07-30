package com.forgeessentials.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
import com.forgeessentials.commands.player.CommandGod;
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
import com.forgeessentials.core.config.ConfigBase;
import com.forgeessentials.core.config.ConfigData;
import com.forgeessentials.core.config.ConfigLoader;
import com.forgeessentials.core.misc.FECommandManager;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStartingEvent;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@FEModule(name = "Commands", parentMod = ForgeEssentials.class)
public class ModuleCommands implements ConfigLoader
{
    private static ForgeConfigSpec COMMAND_CONFIG;
    private static final ConfigData data = new ConfigData("Commands", COMMAND_CONFIG, new ForgeConfigSpec.Builder());

    public static final String PERM = "fe.commands";

    public static CommandsEventHandler oldEventHandler = new CommandsEventHandler();

    public static ModuleCommandsEventHandler eventHandler = new ModuleCommandsEventHandler();

    public static final int COMMANDS_VERSION = 5;

    public static boolean newMappings = false;

    @SubscribeEvent
    public void load(FEModuleServerStartingEvent event)
    {
        MobTypeLoader.init();
        APIRegistry.perms.registerPermissionDescription("fe.commands", "Permission nodes for FE commands module");
    }

    @SubscribeEvent
    public void registerCommands(RegisterCommandsEvent event)
    {
        CommandTime time = new CommandTime(true);
        FECommandManager.registerCommand(time, event.getDispatcher());
        MinecraftForge.EVENT_BUS.register(time);

        FECommandManager.registerCommand(new CommandEnchant(true), event.getDispatcher());
        FECommandManager.registerCommand(new CommandDechant(true), event.getDispatcher());
        FECommandManager.registerCommand(new CommandLocate(true), event.getDispatcher());
        FECommandManager.registerCommand(new CommandRules(true), event.getDispatcher());
        FECommandManager.registerCommand(new CommandModlist(true), event.getDispatcher());
        FECommandManager.registerCommand(new CommandButcher(true), event.getDispatcher());
        FECommandManager.registerCommand(new CommandRemove(true), event.getDispatcher());
        // Afk
        FECommandManager.registerCommand(new CommandAFK(true), event.getDispatcher());
        CommandFeSettings.addSetting("Afk", "timeout", CommandAFK.PERM_AUTOTIME);
        CommandFeSettings.addSetting("Afk", "auto_kick", CommandAFK.PERM_AUTOKICK);
        CommandFeSettings.addSetting("Afk", "warmup", CommandAFK.PERM_WARMUP);
        // Kit
        CommandKit kit = new CommandKit(true);
        FECommandManager.registerCommand(kit, event.getDispatcher());
        MinecraftForge.EVENT_BUS.register(kit);

        FECommandManager.registerCommand(new CommandEnderchest(true), event.getDispatcher());
        FECommandManager.registerCommand(new CommandVirtualchest(true), event.getDispatcher());
        // Craft
        CommandCraft craft = new CommandCraft(true);
        FECommandManager.registerCommand(craft, event.getDispatcher());
        MinecraftForge.EVENT_BUS.register(craft);

        FECommandManager.registerCommand(new CommandPing(true), event.getDispatcher());
        FECommandManager.registerCommand(new CommandInventorySee(true), event.getDispatcher());
        FECommandManager.registerCommand(new CommandSmite(true), event.getDispatcher());
        FECommandManager.registerCommand(new CommandBurn(true), event.getDispatcher());
        FECommandManager.registerCommand(new CommandPotion(true), event.getDispatcher());
        FECommandManager.registerCommand(new CommandRepair(true), event.getDispatcher());
        FECommandManager.registerCommand(new CommandHeal(true), event.getDispatcher());
        FECommandManager.registerCommand(new CommandKill(true), event.getDispatcher());
        FECommandManager.registerCommand(new CommandGameMode(true), event.getDispatcher());
        FECommandManager.registerCommand(new CommandDoAs(true), event.getDispatcher());
        FECommandManager.registerCommand(new CommandServerSettings(true), event.getDispatcher());
        FECommandManager.registerCommand(new CommandGetCommandBook(true), event.getDispatcher());
        // Weather
        CommandWeather weather = new CommandWeather(true);
        FECommandManager.registerCommand(weather, event.getDispatcher());
        MinecraftForge.EVENT_BUS.register(weather);
        // Bind
        CommandBind bind = new CommandBind(true);
        FECommandManager.registerCommand(bind, event.getDispatcher());
        MinecraftForge.EVENT_BUS.register(bind);

        FECommandManager.registerCommand(new CommandRename(true), event.getDispatcher());
        FECommandManager.registerCommand(new CommandPush(true), event.getDispatcher());
        FECommandManager.registerCommand(new CommandDrop(true), event.getDispatcher());
        FECommandManager.registerCommand(new CommandFindblock(true), event.getDispatcher());
        FECommandManager.registerCommand(new CommandNoClip(true), event.getDispatcher());
        FECommandManager.registerCommand(new CommandBubble(true), event.getDispatcher());

        FECommandManager.registerCommand(new CommandSpeed(true), event.getDispatcher());
        FECommandManager.registerCommand(new CommandSeen(true), event.getDispatcher());
        FECommandManager.registerCommand(new CommandTempBan(true), event.getDispatcher());
        FECommandManager.registerCommand(new CommandFly(true), event.getDispatcher());
        FECommandManager.registerCommand(new CommandGod(true), event.getDispatcher());
        // Help
        CommandHelp help = new CommandHelp(true, event.getDispatcher());
        FECommandManager.registerCommand(help, event.getDispatcher());
        help.fixer = new HelpFixer();

        FECommandManager.registerCommand(new CommandVanish(true), event.getDispatcher());
        FECommandManager.registerCommand(new CommandDuplicate(true), event.getDispatcher());
        FECommandManager.registerCommand(new CommandDelayedAction(true), event.getDispatcher());
    }

    static ForgeConfigSpec.IntValue FECversion;
    static ForgeConfigSpec.ConfigValue<String> Rulesname;
    static ForgeConfigSpec.IntValue FEVCsize;
    static ForgeConfigSpec.ConfigValue<String> FEVCname;
    ForgeConfigSpec.IntValue FECFBdefaultRange;
    ForgeConfigSpec.IntValue FECFBdefaultSpeed;
    static ForgeConfigSpec.ConfigValue<List<? extends String>> FEHmessages;
    static ForgeConfigSpec.IntValue FEHentriesPerPage;
    static ForgeConfigSpec.IntValue FEHcommandColor;
    static ForgeConfigSpec.IntValue FEHsubCommandColor;
    static ForgeConfigSpec.ConfigValue<String> FEPresponse;
    static ForgeConfigSpec.ConfigValue<String> FEkitForNewPlayers;

    @Override
    public void load(Builder BUILDER, boolean isReload)
    {
        BUILDER.push("Commands Config");
        FECversion = BUILDER.comment("Don't change this value!").defineInRange("version", COMMANDS_VERSION, 0,
                Integer.MAX_VALUE);
        BUILDER.pop();

        BUILDER.push("Command Rules Settings");
        Rulesname = BUILDER.comment("Name for rules file").define("filename", "rules.txt");
        BUILDER.pop();

        BUILDER.push("Command VirtualChest Settings");
        FEVCsize = BUILDER.comment("1 row = 9 slots. 3 = 1 chest, 6 = double chest (max size!).")
                .defineInRange("VirtualChestRows", 6, 1, 6);
        FEVCname = BUILDER.comment("Don't use special stuff....").define("VirtualChestName", "Vault 13");
        BUILDER.pop();

        BUILDER.push("Command Findblock Settings");
        FECFBdefaultRange = BUILDER.comment("Default max distance used.").defineInRange("defaultRange", 20 * 16, 0,
                Integer.MAX_VALUE);
        FECFBdefaultSpeed = BUILDER.comment("Default speed used.").defineInRange("defaultSpeed", 16 * 16, 0,
                Integer.MAX_VALUE);
        BUILDER.pop();

        BUILDER.comment("Configure ForgeEssentials Help Command.").push("CommandHelp Settings");
        FEHmessages = BUILDER.comment("Add custom messages here that will appear when /help is run")
                .defineList("custom_help", new ArrayList<>(), ConfigBase.stringValidator);
        FEHentriesPerPage = BUILDER.comment("Amount to commands to show per help page").defineInRange("commandPerPage",
                8, 1, 50);
        FEHcommandColor = BUILDER
                .comment("Color for the command in /help. The possible values are; "
                        + "0: black, 1: dark_blue, 2: dark_green, 3: dark_aqua, 4: dark_red, "
                        + "5: dark_purple, 6: gold, 7: gray, 8: dark_gray, 9: blue, "
                        + "10: green, 11: aqua, 12: red, 13: light_purple, 14: yellow, 15: white.")
                .defineInRange("commandColor", 2, 0, 15);
        FEHsubCommandColor = BUILDER
                .comment("Color for the subcommand in /help. The possible values are; "
                        + "0: black, 1: dark_blue, 2: dark_green, 3: dark_aqua, 4: dark_red, "
                        + "5: dark_purple, 6: gold, 7: gray, 8: dark_gray, 9: blue, "
                        + "10: green, 11: aqua, 12: red, 13: light_purple, 14: yellow, 15: white.")
                .defineInRange("subCommandColor", 7, 0, 15);
        BUILDER.pop();

        BUILDER.push("Command Ping Settings");
        FEPresponse = BUILDER.comment("Response Format for command must include %time.").define("response",
                "Pong! %time");
        BUILDER.pop();

        BUILDER.push("Command Kit Settings");
        FEkitForNewPlayers = BUILDER
                .comment("Name of kit to issue to new players. If this is left blank, it will be ignored.")
                .define("kitForNewPlayers", "");
        BUILDER.pop();
    }

    @Override
    public void bakeConfig(boolean reload)
    {
        if (FECversion.get() < COMMANDS_VERSION)
        {
            newMappings = true;
            FECversion.set(COMMANDS_VERSION);
        }
        CommandRules.rulesFile = new File(ForgeEssentials.getFEDirectory(), Rulesname.get());
        CommandRules.rules = CommandRules.loadRules();
        CommandVirtualchest.size = FEVCsize.get() * 9;
        CommandVirtualchest.rowCount = FEVCsize.get();
        CommandVirtualchest.name = FEVCname.get();
        CommandFindblock.defaultRange = FECFBdefaultRange.get();
        CommandFindblock.defaultSpeed = FECFBdefaultSpeed.get();
        CommandHelp.messages = new ArrayList<>(FEHmessages.get());
        CommandHelp.entriesPerPage = FEHentriesPerPage.get();
        CommandHelp.commandColor = FEHcommandColor.get();
        CommandHelp.subCommandColor = FEHsubCommandColor.get();
        CommandPing.response = FEPresponse.get();
        CommandKit.kitForNewPlayers = FEkitForNewPlayers.get();
    }

    @Override
    public ConfigData returnData()
    {
        return data;
    }
}
