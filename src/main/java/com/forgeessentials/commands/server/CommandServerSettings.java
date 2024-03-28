package com.forgeessentials.commands.server;

import java.util.Objects;
import java.util.Properties;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.scripting.ScriptArguments;
import com.forgeessentials.scripting.ScriptParser.ScriptException;
import com.forgeessentials.util.ServerUtil;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServerSettings;
import net.minecraft.server.dedicated.DedicatedPlayerList;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.dedicated.Settings;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.GameType;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.fmllegacy.server.ServerLifecycleHooks;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import org.jetbrains.annotations.NotNull;

public class CommandServerSettings extends ForgeEssentialsCommandBuilder
{

    public CommandServerSettings(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public @NotNull String getPrimaryAlias()
    {
        return "serversettings";
    }

    @Override
    public String @NotNull [] getDefaultSecondaryAliases()
    {
        return new String[] { "ss" };
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.OP;
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> setExecution()
    {
        return baseBuilder
                .then(Commands.literal("allow-flight")
                        .then(Commands.literal("modify")
                                .then(Commands.argument("toggle", BoolArgumentType.bool())
                                        .executes(CommandContext -> execute(CommandContext, "allow-flightT"))))
                        .then(Commands.literal("view")
                                .executes(CommandContext -> execute(CommandContext, "allow-flightV"))))
                .then(Commands.literal("allow-nether")
                        .then(Commands.literal("modify")
                                .then(Commands.argument("toggle", BoolArgumentType.bool())
                                        .executes(CommandContext -> execute(CommandContext, "allow-netherT"))))
                        .then(Commands.literal("view")
                                .executes(CommandContext -> execute(CommandContext, "allow-netherV"))))
                .then(Commands.literal("broadcast-console-to-ops")
                        .then(Commands.literal("modify")
                                .then(Commands.argument("toggle", BoolArgumentType.bool()).executes(
                                        CommandContext -> execute(CommandContext, "broadcast-console-to-opsT"))))
                        .then(Commands.literal("view")
                                .executes(CommandContext -> execute(CommandContext, "broadcast-console-to-opsV"))))
                .then(Commands.literal("broadcast-rcon-to-ops")
                        .then(Commands.literal("modify")
                                .then(Commands.argument("toggle", BoolArgumentType.bool())
                                        .executes(CommandContext -> execute(CommandContext, "broadcast-rcon-to-opsT"))))
                        .then(Commands.literal("view")
                                .executes(CommandContext -> execute(CommandContext, "broadcast-rcon-to-opsV"))))
                .then(Commands.literal("difficulty")
                        .then(Commands.literal("modify")
                                .then(Commands.argument("difficulity", IntegerArgumentType.integer(0, 3))
                                        .executes(CommandContext -> execute(CommandContext, "difficultyT"))))
                        .then(Commands.literal("view")
                                .executes(CommandContext -> execute(CommandContext, "difficultyV"))))
                .then(Commands.literal("enable-command-block")
                        .then(Commands.literal("modify")
                                .then(Commands.argument("toggle", BoolArgumentType.bool())
                                        .executes(CommandContext -> execute(CommandContext, "enable-command-blockT"))))
                        .then(Commands.literal("view")
                                .executes(CommandContext -> execute(CommandContext, "enable-command-blockV"))))
                .then(Commands.literal("enable-jmx-monitoring")
                        .executes(CommandContext -> execute(CommandContext, "enable-jmx-monitoring")))
                .then(Commands.literal("enable-query")
                        .executes(CommandContext -> execute(CommandContext, "enable-query")))
                .then(Commands.literal("enable-rcon")
                        .executes(CommandContext -> execute(CommandContext, "enable-rcon")))
                .then(Commands.literal("enable-status")
                        .then(Commands.literal("modify")
                                .then(Commands.argument("toggle", BoolArgumentType.bool())
                                        .executes(CommandContext -> execute(CommandContext, "enable-statusT"))))
                        .then(Commands.literal("view")
                                .executes(CommandContext -> execute(CommandContext, "enable-statusV"))))
                .then(Commands.literal("enforce-whitelist")
                        .then(Commands.literal("modify")
                                .then(Commands.argument("toggle", BoolArgumentType.bool())
                                        .executes(CommandContext -> execute(CommandContext, "enforce-whitelistT"))))
                        .then(Commands.literal("view")
                                .executes(CommandContext -> execute(CommandContext, "enforce-whitelistV"))))
                .then(Commands.literal("entity-broadcast-range-percentage").then(Commands.literal("modify")
                        .then(Commands.argument("percentage", IntegerArgumentType.integer(10, 1000)).executes(
                                CommandContext -> execute(CommandContext, "entity-broadcast-range-percentageT"))))
                        .then(Commands.literal("view").executes(
                                CommandContext -> execute(CommandContext, "entity-broadcast-range-percentageV"))))
                .then(Commands.literal("force-gamemode")
                        .then(Commands.literal("modify")
                                .then(Commands.argument("toggle", BoolArgumentType.bool())
                                        .executes(CommandContext -> execute(CommandContext, "force-gamemodeT"))))
                        .then(Commands.literal("view")
                                .executes(CommandContext -> execute(CommandContext, "force-gamemodeV"))))
                .then(Commands.literal("function-permission-level")
                        .then(Commands.literal("modify")
                                .then(Commands.argument("level", IntegerArgumentType.integer(1, 4)).executes(
                                        CommandContext -> execute(CommandContext, "function-permission-levelT"))))
                        .then(Commands.literal("view")
                                .executes(CommandContext -> execute(CommandContext, "function-permission-levelV"))))
                .then(Commands.literal("gamemode")
                        .then(Commands.literal("modify")
                                .then(Commands.argument("gamemode", IntegerArgumentType.integer(0, 3))
                                        .executes(CommandContext -> execute(CommandContext, "gamemodeT"))))
                        .then(Commands.literal("view")
                                .executes(CommandContext -> execute(CommandContext, "gamemodeV"))))
                .then(Commands.literal("generate-structures")
                        .executes(CommandContext -> execute(CommandContext, "generate-structures")))
                .then(Commands.literal("generator-settings")
                        .executes(CommandContext -> execute(CommandContext, "generator-settings")))
                .then(Commands.literal("hardcore").executes(CommandContext -> execute(CommandContext, "hardcore")))
                .then(Commands.literal("level-name").executes(CommandContext -> execute(CommandContext, "level-name")))
                .then(Commands.literal("level-seed").executes(CommandContext -> execute(CommandContext, "level-seed")))
                .then(Commands.literal("level-type").executes(CommandContext -> execute(CommandContext, "level-type")))
//                .then(Commands.literal("max-build-height")
//                        .then(Commands.literal("modify")
//                                .then(Commands.argument("buildlimit", IntegerArgumentType.integer(0, Integer.MAX_VALUE))
//                                        .executes(CommandContext -> execute(CommandContext, "max-build-heightT"))))
//                        .then(Commands.literal("view")
//                                .executes(CommandContext -> execute(CommandContext, "max-build-heightV"))))
                .then(Commands.literal("max-players")
                        .executes(CommandContext -> execute(CommandContext, "max-players")))
                .then(Commands.literal("max-tick-time")
                        .executes(CommandContext -> execute(CommandContext, "max-tick-time")))
                .then(Commands.literal("max-world-size")
                        .executes(CommandContext -> execute(CommandContext, "max-world-size")))
                .then(Commands.literal("motd")
                        .then(Commands.literal("modify")
                                .then(Commands.argument("motd", StringArgumentType.greedyString())
                                        .executes(CommandContext -> execute(CommandContext, "motdT"))))
                        .then(Commands.literal("clear").executes(CommandContext -> execute(CommandContext, "motdC")))
                        .then(Commands.literal("view").executes(CommandContext -> execute(CommandContext, "motdV"))))
                .then(Commands.literal("network-compression-threshold")
                        .then(Commands.literal("modify")
                                .then(Commands.argument("threshold", IntegerArgumentType.integer(0, 1500)).executes(
                                        CommandContext -> execute(CommandContext, "network-compression-thresholdT"))))
                        .then(Commands.literal("view")
                                .executes(CommandContext -> execute(CommandContext, "network-compression-thresholdV"))))
                .then(Commands.literal("online-mode")
                        .then(Commands.literal("modify")
                                .then(Commands.argument("toggle", BoolArgumentType.bool())
                                        .executes(CommandContext -> execute(CommandContext, "online-modeT"))))
                        .then(Commands.literal("view")
                                .executes(CommandContext -> execute(CommandContext, "online-modeV"))))
                .then(Commands.literal("op-permission-level")
                        .then(Commands.literal("modify")
                                .then(Commands.argument("level", IntegerArgumentType.integer(0, 4))
                                        .executes(CommandContext -> execute(CommandContext, "op-permission-levelT"))))
                        .then(Commands.literal("view")
                                .executes(CommandContext -> execute(CommandContext, "op-permission-levelV"))))
                .then(Commands.literal("player-idle-timeout")
                        .then(Commands.literal("modify")
                                .then(Commands.argument("timeout", IntegerArgumentType.integer(0, Integer.MAX_VALUE))
                                        .executes(CommandContext -> execute(CommandContext, "player-idle-timeoutT"))))
                        .then(Commands.literal("view")
                                .executes(CommandContext -> execute(CommandContext, "player-idle-timeoutV"))))
                .then(Commands.literal("prevent-proxy-connections")
                        .then(Commands.literal("modify")
                                .then(Commands.argument("toggle", BoolArgumentType.bool()).executes(
                                        CommandContext -> execute(CommandContext, "prevent-proxy-connectionsT"))))
                        .then(Commands.literal("view")
                                .executes(CommandContext -> execute(CommandContext, "prevent-proxy-connectionsV"))))
                .then(Commands.literal("pvp")
                        .then(Commands.literal("modify")
                                .then(Commands.argument("toggle", BoolArgumentType.bool())
                                        .executes(CommandContext -> execute(CommandContext, "pvpT"))))
                        .then(Commands.literal("view").executes(CommandContext -> execute(CommandContext, "pvpV"))))
                .then(Commands.literal("query.port").executes(CommandContext -> execute(CommandContext, "level-type")))
                .then(Commands.literal("rate-limit").executes(CommandContext -> execute(CommandContext, "level-type")))
                .then(Commands.literal("rcon.password")
                        .executes(CommandContext -> execute(CommandContext, "level-type")))
                .then(Commands.literal("rcon.port").executes(CommandContext -> execute(CommandContext, "level-type")))
                .then(Commands.literal("resource-pack")
                        .then(Commands.literal("modify")
                                .then(Commands.argument("PackName", StringArgumentType.greedyString())
                                        .executes(CommandContext -> execute(CommandContext, "resource-packT"))))
                        .then(Commands.literal("clear")
                                .executes(CommandContext -> execute(CommandContext, "resource-packC")))
                        .then(Commands.literal("view")
                                .executes(CommandContext -> execute(CommandContext, "resource-packV"))))
                .then(Commands.literal("resource-pack-sha1")
                        .then(Commands.literal("modify")
                                .then(Commands.argument("sha1", StringArgumentType.greedyString())
                                        .executes(CommandContext -> execute(CommandContext, "resource-pack-sha1T"))))
                        .then(Commands.literal("clear")
                                .executes(CommandContext -> execute(CommandContext, "resource-pack-sha1C")))
                        .then(Commands.literal("view")
                                .executes(CommandContext -> execute(CommandContext, "resource-pack-sha1V"))))
                .then(Commands.literal("server-ip").executes(CommandContext -> execute(CommandContext, "server-ip")))
                .then(Commands.literal("server-ip").executes(CommandContext -> execute(CommandContext, "server-port")))
                .then(Commands.literal("snooper-enabled")
                        .executes(CommandContext -> execute(CommandContext, "snooper-enabled")))
                .then(Commands.literal("spawn-animals")
                        .then(Commands.literal("modify")
                                .then(Commands.argument("toggle", BoolArgumentType.bool())
                                        .executes(CommandContext -> execute(CommandContext, "spawn-animalsT"))))
                        .then(Commands.literal("view")
                                .executes(CommandContext -> execute(CommandContext, "spawn-animalsV"))))
                .then(Commands.literal("spawn-monsters")
                        .then(Commands.literal("modify")
                                .then(Commands.argument("toggle", BoolArgumentType.bool())
                                        .executes(CommandContext -> execute(CommandContext, "spawn-monstersT"))))
                        .then(Commands.literal("view")
                                .executes(CommandContext -> execute(CommandContext, "spawn-monstersV"))))
                .then(Commands.literal("spawn-npcs")
                        .then(Commands.literal("modify")
                                .then(Commands.argument("toggle", BoolArgumentType.bool())
                                        .executes(CommandContext -> execute(CommandContext, "spawn-npcsT"))))
                        .then(Commands.literal("view")
                                .executes(CommandContext -> execute(CommandContext, "spawn-npcsV"))))
                .then(Commands.literal("spawn-protection")
                        .then(Commands.literal("modify")
                                .then(Commands.argument("radius", IntegerArgumentType.integer(0, Integer.MAX_VALUE))
                                        .executes(CommandContext -> execute(CommandContext, "spawn-protectionT"))))
                        .then(Commands.literal("view")
                                .executes(CommandContext -> execute(CommandContext, "spawn-protectionV"))))
                .then(Commands.literal("sync-chunk-writes")
                        .executes(CommandContext -> execute(CommandContext, "sync-chunk-writes")))
                .then(Commands.literal("text-filtering-config")
                        .executes(CommandContext -> execute(CommandContext, "text-filtering-config")))
                .then(Commands.literal("use-native-transport")
                        .executes(CommandContext -> execute(CommandContext, "text-filtering-config")))
                .then(Commands.literal("view-distance")
                        .then(Commands.literal("modify")
                                .then(Commands.argument("distance", IntegerArgumentType.integer(3, 32))
                                        .executes(CommandContext -> execute(CommandContext, "view-distanceT"))))
                        .then(Commands.literal("view")
                                .executes(CommandContext -> execute(CommandContext, "view-distanceV"))))
                .then(Commands.literal("white-list")
                        .then(Commands.literal("modify")
                                .then(Commands.argument("toggle", BoolArgumentType.bool())
                                        .executes(CommandContext -> execute(CommandContext, "white-listT"))))
                        .then(Commands.literal("view")
                                .executes(CommandContext -> execute(CommandContext, "white-listV"))))
                .executes(CommandContext -> execute(CommandContext, "blank"));
    }

    @Override
    public int execute(CommandContext<CommandSourceStack> ctx, String params) throws CommandSyntaxException
    {
        if (!FMLEnvironment.dist.isDedicatedServer())
        {
            ChatOutputHandler.chatError(ctx.getSource(), "You can use this command only on dedicated servers");
            return Command.SINGLE_SUCCESS;
        }

        if (params.equals("blank"))
        {
            ChatOutputHandler.chatNotification(ctx.getSource(), "Usage: /serversettings <setting> <option>");
            return Command.SINGLE_SUCCESS;
        }
        DedicatedServerSettings settings = ServerUtil
                .getServerPropProvider((DedicatedServer) ServerLifecycleHooks.getCurrentServer());
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        DedicatedServer Dserver = (DedicatedServer) ServerLifecycleHooks.getCurrentServer();
        try
        {
            switch (params)
            {
            case "allow-flightV":
                ChatOutputHandler.chatConfirmation(ctx.getSource(),
                        Translator.format("Allow flight  is set to: %s", Boolean.toString(server.isFlightAllowed())));
                return Command.SINGLE_SUCCESS;
            case "allow-flightT":
                server.setFlightAllowed(BoolArgumentType.getBool(ctx, "toggle"));
                saveSettings("allow-flight", "allowFlight", BoolArgumentType.getBool(ctx, "toggle"));
                ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Set allow-flight to %s",
                        Boolean.toString(BoolArgumentType.getBool(ctx, "toggle"))));
                return Command.SINGLE_SUCCESS;

            case "allow-netherV":
                ChatOutputHandler.chatConfirmation(ctx.getSource(),
                        Translator.format("Allow nether is set to: %s", Boolean.toString(server.isNetherEnabled())));
                return Command.SINGLE_SUCCESS;
            case "allow-netherT":
                saveSettings("allow-nether", "allowNether", BoolArgumentType.getBool(ctx, "toggle"));
                ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Set allow-nether to %s",
                        Boolean.toString(BoolArgumentType.getBool(ctx, "toggle"))));
                return Command.SINGLE_SUCCESS;

            case "broadcast-console-to-opsV":
                ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator
                        .format("Allow broadcast-console-to-ops: %s", Boolean.toString(Dserver.shouldInformAdmins())));
                return Command.SINGLE_SUCCESS;
            case "broadcast-console-to-opsT":
                saveSettings("broadcast-console-to-ops", "broadcastConsoleToOps", BoolArgumentType.getBool(ctx, "toggle"));
                ChatOutputHandler.chatConfirmation(ctx.getSource(),
                        Translator.format("Set broadcast-console-to-ops to %s",
                                Boolean.toString(BoolArgumentType.getBool(ctx, "toggle"))));
                return Command.SINGLE_SUCCESS;

            case "broadcast-rcon-to-opsV":
                ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Allow broadcast-rcon-to-ops: %s",
                        Boolean.toString(Dserver.shouldRconBroadcast())));
                return Command.SINGLE_SUCCESS;
            case "broadcast-rcon-to-opsT":
                saveSettings("broadcast-rcon-to-ops", "broadcastRconToOps", BoolArgumentType.getBool(ctx, "toggle"));
                ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Set broadcast-rcon-to-ops to %s",
                        Boolean.toString(BoolArgumentType.getBool(ctx, "toggle"))));
                return Command.SINGLE_SUCCESS;

            case "difficultyV":
                ChatOutputHandler.chatConfirmation(ctx.getSource(),
                        Translator.format("Difficulty is set to: %s", server.getWorldData().getDifficulty()));
                return Command.SINGLE_SUCCESS;
            case "difficultyT":
                Difficulty difficulty = Difficulty.byId(IntegerArgumentType.getInteger(ctx, "difficulity"));
                server.setDifficulty(difficulty, true);
                saveSettings("difficulty", "difficulty", difficulty);
                ChatOutputHandler.chatConfirmation(ctx.getSource(),
                        Translator.format("Set difficulty to %s", difficulty.name()));
                return Command.SINGLE_SUCCESS;

            case "enable-command-blockV":
                ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format(
                        "Allow enable-command-block is set to: %s", Boolean.toString(Dserver.isCommandBlockEnabled())));
                return Command.SINGLE_SUCCESS;
            case "enable-command-blockT":
                saveSettings("enable-command-block", "enableCommandBlock", BoolArgumentType.getBool(ctx, "toggle"));
                ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Set enable-command-block to %s",
                        Boolean.toString(BoolArgumentType.getBool(ctx, "toggle"))));
                return Command.SINGLE_SUCCESS;

            case "enable-statusV":
                ChatOutputHandler.chatConfirmation(ctx.getSource(),
                        Translator.format("enable-status is set to: %s", Boolean.toString(Dserver.repliesToStatus())));
                return Command.SINGLE_SUCCESS;
            case "enable-statusT":
                saveSettings("enable-status", "enableStatus", BoolArgumentType.getBool(ctx, "toggle"));
                ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Set enable-status to %s",
                        Boolean.toString(BoolArgumentType.getBool(ctx, "toggle"))));
                return Command.SINGLE_SUCCESS;

            case "enforce-whitelistV":
                ChatOutputHandler.chatConfirmation(ctx.getSource(),
                        Translator.format("Allow enforce-whitelist is set to: %s",
                                Boolean.toString(settings.getProperties().enforceWhitelist)));
                return Command.SINGLE_SUCCESS;
            case "enforce-whitelistT":
                saveSettings("enforce-whitelist", "enforceWhitelist", BoolArgumentType.getBool(ctx, "toggle"));
                server.setEnforceWhitelist(BoolArgumentType.getBool(ctx, "toggle"));
                ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Set enforce-whitelist to %s",
                        Boolean.toString(BoolArgumentType.getBool(ctx, "toggle"))));
                return Command.SINGLE_SUCCESS;

            case "entity-broadcast-range-percentageV":
                ChatOutputHandler.chatConfirmation(ctx.getSource(),
                        Translator.format("entity-broadcast-range-percentage is set to: %s",
                                Boolean.toString(Dserver.repliesToStatus())));
                return Command.SINGLE_SUCCESS;
            case "entity-broadcast-range-percentageT":
                saveSettings("entity-broadcast-range-percentage", "entityBroadcastRangePercentage",
                        IntegerArgumentType.getInteger(ctx, "percentage"));
                ChatOutputHandler.chatConfirmation(ctx.getSource(),
                        Translator.format("Set entity-broadcast-range-percentage to %s",
                                Integer.toString(IntegerArgumentType.getInteger(ctx, "percentage"))));
                return Command.SINGLE_SUCCESS;

            case "force-gamemodeV":
                ChatOutputHandler.chatConfirmation(ctx.getSource(),
                        Translator.format("force-gamemode is set to: %s", Boolean.toString(settings.getProperties().forceGameMode)));
                return Command.SINGLE_SUCCESS;
            case "force-gamemodeT":
                saveSettings("force-gamemode", "forceGameMode", BoolArgumentType.getBool(ctx, "toggle"));
//                server.setForceGameType(BoolArgumentType.getBool(ctx, "toggle"));
                ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Set force-gamemode to %s",
                        Boolean.toString(BoolArgumentType.getBool(ctx, "toggle"))));
                return Command.SINGLE_SUCCESS;

            case "function-permission-levelV":
                ChatOutputHandler.chatConfirmation(ctx.getSource(),
                        Translator.format("function-permission-level is set to: %s",
                                Integer.toString(Dserver.getFunctionCompilationLevel())));
                return Command.SINGLE_SUCCESS;
            case "function-permission-levelT":
                saveSettings("function-permission-level", "functionPermissionLevel",
                        IntegerArgumentType.getInteger(ctx, "level"));
                ChatOutputHandler.chatConfirmation(ctx.getSource(),
                        Translator.format("Set function-permission-level to %s",
                                Integer.toString(IntegerArgumentType.getInteger(ctx, "level"))));
                return Command.SINGLE_SUCCESS;

            case "gamemodeV":
                ChatOutputHandler.chatConfirmation(ctx.getSource(),
                        Translator.format("Default gamemode is set to: %s", server.getDefaultGameType().getName()));
                return Command.SINGLE_SUCCESS;
            case "gamemodeT":
                GameType gamemode = GameType.byId(IntegerArgumentType.getInteger(ctx, "gamemode"));
                server.setDefaultGameType(gamemode);
                saveSettings("gamemode", "gamemode", gamemode);
                ChatOutputHandler.chatConfirmation(ctx.getSource(),
                        Translator.format("Set default gamemode to %s", gamemode.getName()));
                return Command.SINGLE_SUCCESS;

//            case "max-build-heightV":
//                ChatOutputHandler.chatConfirmation(ctx.getSource(),
//                        Translator.format("build limit is set to: %d", server.getMaxBuildHeight()));
//                return Command.SINGLE_SUCCESS;
//            case "max-build-heightT":
//                server.setMaxBuildHeight(IntegerArgumentType.getInteger(ctx, "buildlimit"));
//                saveSettings("max-build-height", "maxBuildHeight", IntegerArgumentType.getInteger(ctx, "buildlimit"));
//                ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Set max-build-height to %d",
//                        IntegerArgumentType.getInteger(ctx, "buildlimit")));
//                return Command.SINGLE_SUCCESS;

            case "motdV":
                ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("MotD = %s", server.getMotd()));
                return Command.SINGLE_SUCCESS;
            case "motdC":
                server.getStatus().setDescription(new TextComponent("A Minecraft Server"));
                server.setMotd("A Minecraft Server");
                saveSettings("motd", "motd", "A Minecraft Server");
                ChatOutputHandler.chatConfirmation(ctx.getSource(),
                        Translator.format("Set MotD to %s", "A Minecraft Server"));
                return Command.SINGLE_SUCCESS;
            case "motdT":
                String motd = ScriptArguments.process(StringArgumentType.getString(ctx, "motd"), null);
                server.getStatus().setDescription(new TextComponent(ChatOutputHandler.formatColors(motd)));
                server.setMotd(motd);
                saveSettings("motd", "motd", motd);
                ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Set MotD to %s", motd));
                return Command.SINGLE_SUCCESS;

            case "network-compression-thresholdV":
                ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator
                        .format("network-compression-threshold is set to: %d", Dserver.getCompressionThreshold()));
                return Command.SINGLE_SUCCESS;
            case "network-compression-thresholdT":
                saveSettings("network-compression-threshold", "networkCompressionThreshold",
                        IntegerArgumentType.getInteger(ctx, "threshold"));
                ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format(
                        "Set network-compression-threshold to %d", IntegerArgumentType.getInteger(ctx, "threshold")));
                return Command.SINGLE_SUCCESS;

            case "online-modeV":
                ChatOutputHandler.chatConfirmation(ctx.getSource(),
                        Translator.format("online-mode is set to: %s", Boolean.toString(server.usesAuthentication())));
                return Command.SINGLE_SUCCESS;
            case "online-modeT":
                saveSettings("online-mode", "onlineMode", BoolArgumentType.getBool(ctx, "toggle"));
                server.setUsesAuthentication(BoolArgumentType.getBool(ctx, "toggle"));
                ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Set online-mode to %s",
                        Boolean.toString(BoolArgumentType.getBool(ctx, "toggle"))));
                return Command.SINGLE_SUCCESS;

            case "op-permission-levelV":
                ChatOutputHandler.chatConfirmation(ctx.getSource(),
                        Translator.format("op-permission-level is set to: %s",
                                Integer.toString(Dserver.getOperatorUserPermissionLevel())));
                return Command.SINGLE_SUCCESS;
            case "op-permission-levelT":
                saveSettings("op-permission-level", "opPermissionLevel", IntegerArgumentType.getInteger(ctx, "level"));
                ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Set op-permission-level to %s",
                        Integer.toString(IntegerArgumentType.getInteger(ctx, "level"))));
                return Command.SINGLE_SUCCESS;

            case "player-idle-timeoutV":
                ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator
                        .format("player-idle-timeout is set to: %s", Integer.toString(Dserver.getPlayerIdleTimeout())));
                return Command.SINGLE_SUCCESS;
            case "player-idle-timeoutT":
                saveSettings("player-idle-timeout", "playerIdleTimeout", IntegerArgumentType.getInteger(ctx, "timeout"));
                server.setPlayerIdleTimeout(IntegerArgumentType.getInteger(ctx, "timeout"));
                ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Set player-idle-timeout to %s",
                        Integer.toString(IntegerArgumentType.getInteger(ctx, "timeout"))));
                return Command.SINGLE_SUCCESS;

            case "prevent-proxy-connectionsV":
                ChatOutputHandler.chatConfirmation(ctx.getSource(),
                        Translator.format("prevent-proxy-connections is set to: %s",
                                Boolean.toString(server.getPreventProxyConnections())));
                return Command.SINGLE_SUCCESS;
            case "prevent-proxy-connectionsT":
                saveSettings("prevent-proxy-connections", "preventProxyConnections", BoolArgumentType.getBool(ctx, "toggle"));
                server.setPreventProxyConnections(BoolArgumentType.getBool(ctx, "toggle"));
                ChatOutputHandler.chatConfirmation(ctx.getSource(),
                        Translator.format("Set prevent-proxy-connections to %s",
                                Boolean.toString(BoolArgumentType.getBool(ctx, "toggle"))));
                return Command.SINGLE_SUCCESS;

            case "pvpV":
                ChatOutputHandler.chatConfirmation(ctx.getSource(),
                        Translator.format("pvp is set to: %s", Boolean.toString(server.isPvpAllowed())));
                return Command.SINGLE_SUCCESS;
            case "pvpT":
                saveSettings("pvp", "pvp", BoolArgumentType.getBool(ctx, "toggle"));
                server.setPvpAllowed(BoolArgumentType.getBool(ctx, "toggle"));
                ChatOutputHandler.chatConfirmation(ctx.getSource(),
                        Translator.format("Set pvp to %s", Boolean.toString(BoolArgumentType.getBool(ctx, "toggle"))));
                return Command.SINGLE_SUCCESS;

            case "resource-packV":
                ChatOutputHandler.chatConfirmation(ctx.getSource(),
                        Translator.format("resource-pack is set to: %s", server.getResourcePack()));
                return Command.SINGLE_SUCCESS;
            case "resource-packC":
                saveSettings("resource-pack", "resourcePack", "");
                server.setResourcePack("", server.getResourcePackHash());
                ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Set resource-pack to %s", ""));
                return Command.SINGLE_SUCCESS;
            case "resource-packT":
                saveSettings("resource-pack", "resourcePack", StringArgumentType.getString(ctx, "PackName"));
                server.setResourcePack(StringArgumentType.getString(ctx, "PackName"), server.getResourcePackHash());
                ChatOutputHandler.chatConfirmation(ctx.getSource(),
                        Translator.format("Set resource-pack to %s", StringArgumentType.getString(ctx, "PackName")));
                return Command.SINGLE_SUCCESS;

            case "resource-pack-sha1V":
                ChatOutputHandler.chatConfirmation(ctx.getSource(),
                        Translator.format("resource-pack-sha1 is set to: %s", server.getResourcePackHash()));
                return Command.SINGLE_SUCCESS;
            case "resource-pack-sha1C":
                saveSettings("resource-pack-sha1", "resourcePackSha1", "");
                server.setResourcePack(server.getResourcePack(), "");
                ChatOutputHandler.chatConfirmation(ctx.getSource(),
                        Translator.format("Set resource-pack-sha1 to %s", ""));
                return Command.SINGLE_SUCCESS;
            case "resource-pack-sha1T":
                saveSettings("resource-pack-sha1", "resourcePackSha1", StringArgumentType.getString(ctx, "sha1"));
                server.setResourcePack(server.getResourcePack(), StringArgumentType.getString(ctx, "sha1"));
                ChatOutputHandler.chatConfirmation(ctx.getSource(),
                        Translator.format("Set resource-pack-sha1 to %s", StringArgumentType.getString(ctx, "sha1")));
                return Command.SINGLE_SUCCESS;

            case "snooper-enabled":
                ChatOutputHandler.chatConfirmation(ctx.getSource(),
                        Translator.format("snooper-enabled is currently un-implimented by mojang"));
                return Command.SINGLE_SUCCESS;

            case "spawn-animalsV":
                ChatOutputHandler.chatConfirmation(ctx.getSource(),
                        Translator.format("spawn-animals is set to: %s", Boolean.toString(server.isSpawningAnimals())));
                return Command.SINGLE_SUCCESS;
            case "spawn-animalsT":
                saveSettings("spawn-animals", "spawnAnimals", BoolArgumentType.getBool(ctx, "toggle"));
                for (ServerLevel serverworld : server.getAllLevels())
                {
                    serverworld.setSpawnSettings(server.getWorldData().getDifficulty() != Difficulty.PEACEFUL,
                            BoolArgumentType.getBool(ctx, "toggle"));
                }
                ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Set spawn-animals to %s",
                        Boolean.toString(BoolArgumentType.getBool(ctx, "toggle"))));
                return Command.SINGLE_SUCCESS;

            case "spawn-monstersV":
                ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("spawn-monsters is set to: %s",
                        Boolean.toString(Dserver.isSpawningMonsters())));
                return Command.SINGLE_SUCCESS;
            case "spawn-monstersT":
                saveSettings("spawn-monsters", "spawnMonsters", BoolArgumentType.getBool(ctx, "toggle"));
                for (ServerLevel serverworld : server.getAllLevels())
                {
                    serverworld.setSpawnSettings(BoolArgumentType.getBool(ctx, "toggle"), server.isSpawningAnimals());
                }
                ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Set spawn-monsters to %s",
                        Boolean.toString(BoolArgumentType.getBool(ctx, "toggle"))));
                return Command.SINGLE_SUCCESS;

            case "spawn-npcsV":
                ChatOutputHandler.chatConfirmation(ctx.getSource(),
                        Translator.format("spawn-npcs is set to: %s", Boolean.toString(Dserver.areNpcsEnabled())));
                return Command.SINGLE_SUCCESS;
            case "spawn-npcsT":
                saveSettings("spawn-npcs", "spawnNpcs", BoolArgumentType.getBool(ctx, "toggle"));
                ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Set spawn-npcs to %s",
                        Boolean.toString(BoolArgumentType.getBool(ctx, "toggle"))));
                return Command.SINGLE_SUCCESS;

            case "spawn-protectionV":
                ChatOutputHandler.chatConfirmation(ctx.getSource(),
                        Translator.format("Spawn protection size: %d", server.getSpawnProtectionRadius()));
                return Command.SINGLE_SUCCESS;
            case "spawn-protectionT":
                saveSettings("spawn-protection", "spawnProtection", IntegerArgumentType.getInteger(ctx, "radius"));
                ChatOutputHandler.chatConfirmation(ctx.getSource(),
                        Translator.format("Set spawn-protection to %d", IntegerArgumentType.getInteger(ctx, "radius")));
                return Command.SINGLE_SUCCESS;

            case "text-filtering-config":
                ChatOutputHandler.chatConfirmation(ctx.getSource(),
                        Translator.format("text-filtering-config is currently un-implimented by mojang"));
                return Command.SINGLE_SUCCESS;

            case "view-distanceV":
                ChatOutputHandler.chatConfirmation(ctx.getSource(),
                        Translator.format("view-distance is set to: %d", server.getPlayerList().getViewDistance()));
                return Command.SINGLE_SUCCESS;
            case "view-distanceT":
                saveSettings("view-distance", "viewDistance", IntegerArgumentType.getInteger(ctx, "distance"));
                server.getPlayerList().setViewDistance(IntegerArgumentType.getInteger(ctx, "distance"));
                ChatOutputHandler.chatConfirmation(ctx.getSource(),
                        Translator.format("Set view-distance to %d", IntegerArgumentType.getInteger(ctx, "distance")));
                return Command.SINGLE_SUCCESS;

            case "white-listV":
                ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("white-list is set to: %s",
                        Boolean.toString(server.getPlayerList().isUsingWhitelist())));
                return Command.SINGLE_SUCCESS;
            case "white-listT":
                saveSettings("white-list", "whiteList", BoolArgumentType.getBool(ctx, "toggle"));
                if (server.getPlayerList() instanceof DedicatedPlayerList)
                {
                    ((DedicatedPlayerList) server.getPlayerList())
                            .setUsingWhiteList(BoolArgumentType.getBool(ctx, "toggle"));

                }
                else
                {
                    server.getPlayerList().setUsingWhiteList(BoolArgumentType.getBool(ctx, "toggle"));

                }
                saveSettings("white-list", "whiteList", BoolArgumentType.getBool(ctx, "toggle"));
                ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Set white-list to %s",
                        Boolean.toString(BoolArgumentType.getBool(ctx, "toggle"))));
                return Command.SINGLE_SUCCESS;
            default:
                ChatOutputHandler.chatError(ctx.getSource(),
                        Translator.format("%s can only be set from server.properties file before launch!", params));
                return Command.SINGLE_SUCCESS;
            }
        }
        catch (NoSuchFieldException e)
        {
            ChatOutputHandler.chatError(ctx.getSource(), "Failed to change setting NoSuchFieldException!");
            e.printStackTrace();
        }
        catch (SecurityException e)
        {
            ChatOutputHandler.chatError(ctx.getSource(), "Failed to change setting SecurityException!");
            e.printStackTrace();
        }
        catch (ScriptException e)
        {
            ChatOutputHandler.chatError(ctx.getSource(), "Failed to change setting ScriptException!");
            e.printStackTrace();
        }
        catch (Exception e)
        {
            ChatOutputHandler.chatError(ctx.getSource(), "Failed to change setting Exception!");
            e.printStackTrace();
        }
        return Command.SINGLE_SUCCESS;
    }

    public static void saveSettings(String propertiesName, String settingsFieldName, Object newValue)
            throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
    {
        // save changed setting to ServerProperties
        DedicatedServerSettings settings = ServerUtil
                .getServerPropProvider((DedicatedServer) ServerLifecycleHooks.getCurrentServer());
        ServerUtil.changeFinalFieldNonStaticField(settings.getProperties(), settingsFieldName, newValue);
        // save changed setting to server.properties file
        Properties props = ObfuscationReflectionHelper.getPrivateValue(Settings.class, settings.getProperties(),
                "properties");
        props.put(propertiesName, Objects.toString(newValue));
        ObfuscationReflectionHelper.setPrivateValue(Settings.class, settings.getProperties(), props,
                "properties");
        settings.forceSave();
        // try (OutputStream outputstream =
        // Files.newOutputStream(ObfuscationReflectionHelper.getPrivateValue(ServerPropertiesProvider.class,
        // settings, "source"))) {
        // net.minecraftforge.common.util.SortedProperties.store(props, outputstream,
        // "Minecraft server properties");
        // } catch (IOException ioexception) {
        // ChatOutputHandler.chatError(source, "Failed to save properties config");
        // ioexception.printStackTrace();
        // }
    }
}
