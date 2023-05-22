package com.forgeessentials.commands.server;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerPropertiesProvider;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.dedicated.PropertyManager;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameType;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.commands.ModuleCommands;
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

public class CommandServerSettings extends ForgeEssentialsCommandBuilder
{

    public CommandServerSettings(boolean enabled)
    {
        super(enabled);
    }

    public static List<String> options = Arrays.asList("allowFlight", "allowPVP", "buildLimit", "difficulty", "MotD", "spawnProtection", "gamemode");

    @Override
    public String getPrimaryAlias()
    {
        return "serversettings";
    }

    @Override
    public String[] getDefaultSecondaryAliases()
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
    public String getPermissionNode()
    {
        return ModuleCommands.PERM + ".serversettings";
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        return baseBuilder
                .then(Commands.literal("allow-flight")
                        .then(Commands.literal("modify")
                                .then(Commands.argument("toggle", BoolArgumentType.bool())
                                        .executes(CommandContext -> execute(CommandContext, "allow-flightT")
                                                )
                                        )
                                )
                        .then(Commands.literal("view")
                                .executes(CommandContext -> execute(CommandContext, "allow-flightV")
                                        )
                                )
                        )
                .then(Commands.literal("allow-nether")
                        .then(Commands.literal("modify")
                                .then(Commands.argument("toggle", BoolArgumentType.bool())
                                        .executes(CommandContext -> execute(CommandContext, "allow-netherT")
                                                )
                                        )
                                )
                        .then(Commands.literal("view")
                                .executes(CommandContext -> execute(CommandContext, "allow-netherV")
                                        )
                                )
                        )
                .then(Commands.literal("broadcast-console-to-ops")
                        .then(Commands.literal("modify")
                                .then(Commands.argument("toggle", BoolArgumentType.bool())
                                        .executes(CommandContext -> execute(CommandContext, "broadcast-console-to-opsT")
                                                )
                                        )
                                )
                        .then(Commands.literal("view")
                                .executes(CommandContext -> execute(CommandContext, "broadcast-console-to-opsV")
                                        )
                                )
                        )
                .then(Commands.literal("broadcast-rcon-to-ops")
                        .then(Commands.literal("modify")
                                .then(Commands.argument("toggle", BoolArgumentType.bool())
                                        .executes(CommandContext -> execute(CommandContext, "broadcast-rcon-to-opsT")
                                                )
                                        )
                                )
                        .then(Commands.literal("view")
                                .executes(CommandContext -> execute(CommandContext, "broadcast-rcon-to-opsV")
                                        )
                                )
                        )
                .then(Commands.literal("difficulty")
                        .then(Commands.literal("modify")
                                .then(Commands.argument("difficulity", IntegerArgumentType.integer(0, 3))
                                        .executes(CommandContext -> execute(CommandContext, "difficultyT")
                                                )
                                        )
                                )
                        .then(Commands.literal("view")
                                .executes(CommandContext -> execute(CommandContext, "difficultyV")
                                        )
                                )
                        )
                .then(Commands.literal("enable-command-block")
                        .then(Commands.literal("modify")
                                .then(Commands.argument("toggle", BoolArgumentType.bool())
                                        .executes(CommandContext -> execute(CommandContext, "enable-command-blockT")
                                                )
                                        )
                                )
                        .then(Commands.literal("view")
                                .executes(CommandContext -> execute(CommandContext, "enable-command-blockV")
                                        )
                                )
                        )
                .then(Commands.literal("enable-jmx-monitoring")
                        .then(Commands.literal("modify")
                                .then(Commands.argument("toggle", BoolArgumentType.bool())
                                        .executes(CommandContext -> execute(CommandContext, "enable-jmx-monitoringT")
                                                )
                                        )
                                )
                        .then(Commands.literal("view")
                                .executes(CommandContext -> execute(CommandContext, "enable-jmx-monitoringV")
                                        )
                                )
                        )
                .then(Commands.literal("enable-query")
                        .then(Commands.literal("modify")
                                .then(Commands.argument("toggle", BoolArgumentType.bool())
                                        .executes(CommandContext -> execute(CommandContext, "enable-queryT")
                                                )
                                        )
                                )
                        .then(Commands.literal("view")
                                .executes(CommandContext -> execute(CommandContext, "enable-queryV")
                                        )
                                )
                        )
                .then(Commands.literal("enable-rcon")
                        .then(Commands.literal("modify")
                                .then(Commands.argument("toggle", BoolArgumentType.bool())
                                        .executes(CommandContext -> execute(CommandContext, "enable-rconT")
                                                )
                                        )
                                )
                        .then(Commands.literal("view")
                                .executes(CommandContext -> execute(CommandContext, "enable-rconV")
                                        )
                                )
                        )
                .then(Commands.literal("enable-status")
                        .then(Commands.literal("modify")
                                .then(Commands.argument("toggle", BoolArgumentType.bool())
                                        .executes(CommandContext -> execute(CommandContext, "enable-statusT")
                                                )
                                        )
                                )
                        .then(Commands.literal("view")
                                .executes(CommandContext -> execute(CommandContext, "enable-statusV")
                                        )
                                )
                        )
                .then(Commands.literal("enforce-whitelist")
                        .then(Commands.literal("modify")
                                .then(Commands.argument("toggle", BoolArgumentType.bool())
                                        .executes(CommandContext -> execute(CommandContext, "enforce-whitelistT")
                                                )
                                        )
                                )
                        .then(Commands.literal("view")
                                .executes(CommandContext -> execute(CommandContext, "enforce-whitelistV")
                                        )
                                )
                        )
                .then(Commands.literal("entity-broadcast-range-percentage")
                        .then(Commands.literal("modify")
                                .then(Commands.argument("percentage", IntegerArgumentType.integer(10, 1000))
                                        .executes(CommandContext -> execute(CommandContext, "entity-broadcast-range-percentageT")
                                                )
                                        )
                                )
                        .then(Commands.literal("view")
                                .executes(CommandContext -> execute(CommandContext, "entity-broadcast-range-percentageV")
                                        )
                                )
                        )
                .then(Commands.literal("force-gamemode")
                        .then(Commands.literal("modify")
                                .then(Commands.argument("toggle", BoolArgumentType.bool())
                                        .executes(CommandContext -> execute(CommandContext, "force-gamemodeT")
                                                )
                                        )
                                )
                        .then(Commands.literal("view")
                                .executes(CommandContext -> execute(CommandContext, "force-gamemodeV")
                                        )
                                )
                        )
                .then(Commands.literal("function-permission-level")
                        .then(Commands.literal("modify")
                                .then(Commands.argument("level", IntegerArgumentType.integer(1, 4))
                                        .executes(CommandContext -> execute(CommandContext, "function-permission-levelT")
                                                )
                                        )
                                )
                        .then(Commands.literal("view")
                                .executes(CommandContext -> execute(CommandContext, "function-permission-levelV")
                                        )
                                )
                        )
                .then(Commands.literal("gamemode")
                        .then(Commands.literal("modify")
                                .then(Commands.argument("gamemode", IntegerArgumentType.integer(0, 3))
                                        .executes(CommandContext -> execute(CommandContext, "gamemodeT")
                                                )
                                        )
                                )
                        .then(Commands.literal("view")
                                .executes(CommandContext -> execute(CommandContext, "gamemodeV")
                                        )
                                )
                        )
                .then(Commands.literal("generate-structures")
                        .then(Commands.literal("modify")
                                .then(Commands.argument("toggle", BoolArgumentType.bool())
                                        .executes(CommandContext -> execute(CommandContext, "generate-structuresT")
                                                )
                                        )
                                )
                        .then(Commands.literal("view")
                                .executes(CommandContext -> execute(CommandContext, "generate-structuresV")
                                        )
                                )
                        )
                .then(Commands.literal("hardcore")
                        .then(Commands.literal("modify")
                                .then(Commands.argument("toggle", BoolArgumentType.bool())
                                        .executes(CommandContext -> execute(CommandContext, "hardcoreT")
                                                )
                                        )
                                )
                        .then(Commands.literal("view")
                                .executes(CommandContext -> execute(CommandContext, "hardcoreV")
                                        )
                                )
                        )
                .then(Commands.literal("max-build-height")
                        .then(Commands.literal("modify")
                                .then(Commands.argument("buildlimit", IntegerArgumentType.integer(0, Integer.MAX_VALUE))
                                        .executes(CommandContext -> execute(CommandContext, "max-build-heightT")
                                                )
                                        )
                                )
                        .then(Commands.literal("view")
                                .executes(CommandContext -> execute(CommandContext, "max-build-heightV")
                                        )
                                )
                        )
                .then(Commands.literal("max-players")
                        .then(Commands.literal("modify")
                                .then(Commands.argument("max", IntegerArgumentType.integer(0, Integer.MAX_VALUE))
                                        .executes(CommandContext -> execute(CommandContext, "max-playersT")
                                                )
                                        )
                                )
                        .then(Commands.literal("view")
                                .executes(CommandContext -> execute(CommandContext, "max-playersV")
                                        )
                                )
                        )
                .then(Commands.literal("max-tick-time")
                        .then(Commands.literal("modify")
                                .then(Commands.argument("max", IntegerArgumentType.integer(0, Integer.MAX_VALUE))
                                        .executes(CommandContext -> execute(CommandContext, "max-tick-timeT")
                                                )
                                        )
                                )
                        .then(Commands.literal("view")
                                .executes(CommandContext -> execute(CommandContext, "max-tick-timeV")
                                        )
                                )
                        )
                .then(Commands.literal("max-world-size")
                        .then(Commands.literal("modify")
                                .then(Commands.argument("maxSize", IntegerArgumentType.integer(1, 29999984))
                                        .executes(CommandContext -> execute(CommandContext, "max-world-sizeT")
                                                )
                                        )
                                )
                        .then(Commands.literal("view")
                                .executes(CommandContext -> execute(CommandContext, "max-world-sizeV")
                                        )
                                )
                        )
                .then(Commands.literal("motd")
                        .then(Commands.literal("modify")
                                .then(Commands.argument("motd", StringArgumentType.greedyString())
                                        .executes(CommandContext -> execute(CommandContext, "motdT")
                                                )
                                        )
                                )
                        .then(Commands.literal("view")
                                .executes(CommandContext -> execute(CommandContext, "motdV")
                                        )
                                )
                        )
                .then(Commands.literal("network-compression-threshold")
                        .then(Commands.literal("modify")
                                .then(Commands.argument("threshold", IntegerArgumentType.integer(0, 1500))
                                        .executes(CommandContext -> execute(CommandContext, "network-compression-thresholdT")
                                                )
                                        )
                                )
                        .then(Commands.literal("view")
                                .executes(CommandContext -> execute(CommandContext, "network-compression-thresholdV")
                                        )
                                )
                        )
                .then(Commands.literal("online-mode")
                        .then(Commands.literal("modify")
                                .then(Commands.argument("toggle", BoolArgumentType.bool())
                                        .executes(CommandContext -> execute(CommandContext, "online-modeT")
                                                )
                                        )
                                )
                        .then(Commands.literal("view")
                                .executes(CommandContext -> execute(CommandContext, "online-modeV")
                                        )
                                )
                        )
                .then(Commands.literal("op-permission-level")
                        .then(Commands.literal("modify")
                                .then(Commands.argument("level", IntegerArgumentType.integer(0, 4))
                                        .executes(CommandContext -> execute(CommandContext, "op-permission-levelT")
                                                )
                                        )
                                )
                        .then(Commands.literal("view")
                                .executes(CommandContext -> execute(CommandContext, "op-permission-levelV")
                                        )
                                )
                        )
                .then(Commands.literal("player-idle-timeout")
                        .then(Commands.literal("modify")
                                .then(Commands.argument("timeout", IntegerArgumentType.integer(0, Integer.MAX_VALUE))
                                        .executes(CommandContext -> execute(CommandContext, "player-idle-timeoutT")
                                                )
                                        )
                                )
                        .then(Commands.literal("view")
                                .executes(CommandContext -> execute(CommandContext, "player-idle-timeoutV")
                                        )
                                )
                        )
                .then(Commands.literal("prevent-proxy-connections")
                        .then(Commands.literal("modify")
                                .then(Commands.argument("toggle", BoolArgumentType.bool())
                                        .executes(CommandContext -> execute(CommandContext, "prevent-proxy-connectionsT")
                                                )
                                        )
                                )
                        .then(Commands.literal("view")
                                .executes(CommandContext -> execute(CommandContext, "prevent-proxy-connectionsV")
                                        )
                                )
                        )
                .then(Commands.literal("pvp")
                        .then(Commands.literal("modify")
                                .then(Commands.argument("toggle", BoolArgumentType.bool())
                                        .executes(CommandContext -> execute(CommandContext, "pvpT")
                                                )
                                        )
                                )
                        .then(Commands.literal("view")
                                .executes(CommandContext -> execute(CommandContext, "pvpV")
                                        )
                                )
                        )
                .then(Commands.literal("spawnprotection")
                        .then(Commands.literal("modify")
                                .then(Commands.argument("radius", IntegerArgumentType.integer(0, Integer.MAX_VALUE))
                                        .executes(CommandContext -> execute(CommandContext, "spawnprotectionT")
                                                )
                                        )
                                )
                        .then(Commands.literal("view")
                                .executes(CommandContext -> execute(CommandContext, "spawnprotectionV")
                                        )
                                )
                        )
                .executes(CommandContext -> execute(CommandContext, "blank")
                        );
    }

    @Override
    public int execute(CommandContext<CommandSource> ctx, String params) throws CommandSyntaxException
    {
        if (!FMLEnvironment.dist.isDedicatedServer()) {
            ChatOutputHandler.chatError(ctx.getSource(), "You can use this command only on dedicated servers");
            return Command.SINGLE_SUCCESS;
        }

        if (params.equals("blank"))
        {
            ChatOutputHandler.chatNotification(ctx.getSource(), Translator.format("Options: %s", StringUtils.join(options, ", ")));
            return Command.SINGLE_SUCCESS;
        }
        ServerPropertiesProvider settings= ServerUtil.getServerPropProvider((DedicatedServer) ServerLifecycleHooks.getCurrentServer());
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        DedicatedServer Dserver = (DedicatedServer) ServerLifecycleHooks.getCurrentServer();
        try {
			switch (params)
			{
			case "allow-flightV":
			    ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Allow flight  is set to: %s", Boolean.toString(server.isFlightAllowed())));
			    return Command.SINGLE_SUCCESS;
			case "allow-flightT":
			    server.setFlightAllowed(BoolArgumentType.getBool(ctx, "toggle"));
			    saveSettings("allow-flight", "field_219013_g", BoolArgumentType.getBool(ctx, "toggle"));
			    ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Set allow-flight to %s", Boolean.toString(BoolArgumentType.getBool(ctx, "toggle"))));
			    return Command.SINGLE_SUCCESS;

			case "allow-netherV":
			    ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Allow nether is set to: %s", Boolean.toString(server.isNetherEnabled())));
			    return Command.SINGLE_SUCCESS;
			case "allow-netherT":
			    saveSettings("allow-nether", "field_218991_D", BoolArgumentType.getBool(ctx, "toggle")); 
			    ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Set allow-nether to %s", Boolean.toString(BoolArgumentType.getBool(ctx, "toggle"))));
			    return Command.SINGLE_SUCCESS;

			case "broadcast-console-to-opsV":
			    ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Allow broadcast-console-to-ops: %s", Boolean.toString(Dserver.shouldInformAdmins())));
			    return Command.SINGLE_SUCCESS;
			case "broadcast-console-to-opsT":
			    saveSettings("broadcast-console-to-ops", "field_219003_P", BoolArgumentType.getBool(ctx, "toggle"));
			    ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Set broadcast-console-to-ops to %s", Boolean.toString(BoolArgumentType.getBool(ctx, "toggle"))));
			    return Command.SINGLE_SUCCESS;

			case "broadcast-rcon-to-opsV":
			    ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Allow broadcast-rcon-to-ops: %s", Boolean.toString(Dserver.shouldRconBroadcast())));
			    return Command.SINGLE_SUCCESS;
			case "broadcast-rcon-to-opsT":
			    saveSettings("broadcast-rcon-to-ops", "field_219002_O", BoolArgumentType.getBool(ctx, "toggle"));
			    ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Set broadcast-rcon-to-ops to %s", Boolean.toString(BoolArgumentType.getBool(ctx, "toggle"))));
			    return Command.SINGLE_SUCCESS;

			case "difficultyV":
			    ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Difficulty is set to: %s", server.getWorldData().getDifficulty()));
			    return Command.SINGLE_SUCCESS;
			case "difficultyT":
			    Difficulty difficulty = Difficulty.byId(IntegerArgumentType.getInteger(ctx, "difficulity"));
			    server.setDifficulty(difficulty, true);
			    saveSettings("difficulty", "field_219019_m", difficulty);
			    ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Set difficulty to %s", difficulty.name()));
			    return Command.SINGLE_SUCCESS;

			case "enable-command-blockV":
			    ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Allow enable-command-block is set to: %s", Boolean.toString(Dserver.isCommandBlockEnabled())));
			    return Command.SINGLE_SUCCESS;
			case "enable-command-blockT":
			    saveSettings("enable-command-block", "field_218995_H", BoolArgumentType.getBool(ctx, "toggle"));
			    ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Set enable-command-block to %s", Boolean.toString(BoolArgumentType.getBool(ctx, "toggle"))));
			    return Command.SINGLE_SUCCESS;

			case "enable-jmx-monitoringV":
			    ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Allow jmx-monitoring is set to: %s", Boolean.toString(settings.getProperties().enableJmxMonitoring)));
			    return Command.SINGLE_SUCCESS;
			case "enable-jmx-monitoringT":
			    ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("jmx-monitoring can only be set from server.properties file before launch!"));
			    return Command.SINGLE_SUCCESS;

			case "enable-queryV":
			    ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Allow enable-query is set to: %s", Boolean.toString(settings.getProperties().enableQuery)));
			    return Command.SINGLE_SUCCESS;
			case "enable-queryT":
			    ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("enable-query can only be set from server.properties file before launch!"));
			    return Command.SINGLE_SUCCESS;

			case "enable-rconV":
			    ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Allow enable-rcon is set to: %s", Boolean.toString(settings.getProperties().enableRcon)));
			    return Command.SINGLE_SUCCESS;
			case "enable-rconT":
			    ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("enable-rcon can only be set from server.properties file before launch!"));
			    return Command.SINGLE_SUCCESS;

			case "enable-statusV":
			    ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("enable-status is set to: %s", Boolean.toString(Dserver.repliesToStatus())));
			    return Command.SINGLE_SUCCESS;
			case "enable-statusT":
				saveSettings("enable-status", "field_241080_Q_", BoolArgumentType.getBool(ctx, "toggle"));
			    ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Set enable-status to %s", Boolean.toString(BoolArgumentType.getBool(ctx, "toggle"))));
			    return Command.SINGLE_SUCCESS;

			case "enforce-whitelistV":
			    ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Allow enforce-whitelist is set to: %s", Boolean.toString(settings.getProperties().enforceWhitelist)));
			    return Command.SINGLE_SUCCESS;
			case "enforce-whitelistT":
			    saveSettings("enforce-whitelist", "field_219017_k", BoolArgumentType.getBool(ctx, "toggle"));
			    server.setEnforceWhitelist(BoolArgumentType.getBool(ctx, "toggle"));
			    ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Set enforce-whitelist to %s", Boolean.toString(BoolArgumentType.getBool(ctx, "toggle"))));
			    return Command.SINGLE_SUCCESS;

			case "entity-broadcast-range-percentageV":
			    ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("entity-broadcast-range-percentage is set to: %s", Boolean.toString(Dserver.repliesToStatus())));
			    return Command.SINGLE_SUCCESS;
			case "entity-broadcast-range-percentageT":
				saveSettings("entity-broadcast-range-percentage", "field_241081_R_", IntegerArgumentType.getInteger(ctx, "percentage"));
			    ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Set entity-broadcast-range-percentage to %s", Integer.toString(IntegerArgumentType.getInteger(ctx, "percentage"))));
			    return Command.SINGLE_SUCCESS;

			case "force-gamemodeV":
			    ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("force-gamemode is set to: %s", Boolean.toString(server.getForceGameType())));
			    return Command.SINGLE_SUCCESS;
			case "force-gamemodeT":
				saveSettings("force-gamemode", "field_219016_j", BoolArgumentType.getBool(ctx, "toggle"));
			    server.setForceGameType(BoolArgumentType.getBool(ctx, "toggle"));
			    ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Set force-gamemode to %s", Boolean.toString(BoolArgumentType.getBool(ctx, "toggle"))));
			    return Command.SINGLE_SUCCESS;

			case "function-permission-levelV":
			    ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("function-permission-level is set to: %s", Integer.toString(Dserver.getFunctionCompilationLevel())));
			    return Command.SINGLE_SUCCESS;
			case "function-permission-levelT":
				saveSettings("function-permission-level", "field_225395_K", IntegerArgumentType.getInteger(ctx, "level"));
			    ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Set function-permission-level to %s", Integer.toString(IntegerArgumentType.getInteger(ctx, "level"))));
			    return Command.SINGLE_SUCCESS;

			case "gamemodeV":
			    ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Default gamemode is set to: %s", server.getDefaultGameType().getName()));
			    return Command.SINGLE_SUCCESS;
			case "gamemodeT":
			    GameType gamemode = GameType.byId(IntegerArgumentType.getInteger(ctx, "gamemode"));
			    server.setDefaultGameType(gamemode);
			    saveSettings("gamemode","field_219020_n", gamemode);
			    ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Set default gamemode to %s", gamemode.getName()));
			    return Command.SINGLE_SUCCESS;

			case "generate-structuresV":
			    ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Allow generate-structures is set to: %s", Boolean.toString(server.getWorldData().worldGenSettings().generateFeatures())));
			    return Command.SINGLE_SUCCESS;
			case "generate-structuresT":
			    ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("generate-structures can only be set from server.properties file before launch!"));
			    return Command.SINGLE_SUCCESS;

			case "hardcoreV":
			    ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("hardcore is set to: %s", Boolean.toString(Dserver.isHardcore())));
			    return Command.SINGLE_SUCCESS;
			case "hardcoreT":
			    ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("hardcore can only be set from server.properties file before launch!"));
			    return Command.SINGLE_SUCCESS;

			case "max-build-heightV":
			        ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("build limit is set to: %d", server.getMaxBuildHeight()));
			        return Command.SINGLE_SUCCESS;
			case "max-build-heightT":
			    server.setMaxBuildHeight(IntegerArgumentType.getInteger(ctx, "buildlimit"));
			    saveSettings("max-build-height","field_219026_t", IntegerArgumentType.getInteger(ctx, "buildlimit"));
			    ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Set max-build-height to %d", IntegerArgumentType.getInteger(ctx, "buildlimit")));
			    return Command.SINGLE_SUCCESS;

			case "max-playersV":
		        ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("maximum amount of players is set to: %d", server.getMaxPlayers()));
		        return Command.SINGLE_SUCCESS;
			case "max-playersT":
			    ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("maximum amount of players can only be set from server.properties file before launch!"));
			    return Command.SINGLE_SUCCESS;

			case "max-tick-timeV":
		        ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("max-tick-time is set to: %d", Dserver.getMaxTickLength()));
		        return Command.SINGLE_SUCCESS;
			case "max-tick-timeT":
			    ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("max-tick-time can only be set from server.properties file before launch!"));
			    return Command.SINGLE_SUCCESS;

			case "max-world-sizeV":
		        ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("max-world-size is set to: %d", Dserver.getAbsoluteMaxWorldSize()));
		        return Command.SINGLE_SUCCESS;
			case "max-world-sizeT":
				for(ServerWorld world : server.getAllLevels()) {
					world.getWorldBorder().setAbsoluteMaxSize(IntegerArgumentType.getInteger(ctx, "maxSize"));
				}
			    saveSettings("max-world-size", "field_219004_Q", IntegerArgumentType.getInteger(ctx, "maxSize"));
			    ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Set max-world-size to %d", IntegerArgumentType.getInteger(ctx, "maxSize")));
			    return Command.SINGLE_SUCCESS;

			case "motdV":
			    ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("MotD = %s", server.getMotd()));
			    return Command.SINGLE_SUCCESS;
			case "motdT":
			    String motd = ScriptArguments.process(StringArgumentType.getString(ctx, "motd"), null);
			    server.getStatus().setDescription(new StringTextComponent(ChatOutputHandler.formatColors(motd)));
			    server.setMotd(motd);
			    saveSettings("motd", "field_219015_i", motd);
			    ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Set MotD to %s", motd));
			    return Command.SINGLE_SUCCESS;

			case "spawnprotectionV":
			    ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Spawn protection size: %d", server.getSpawnProtectionRadius()));
			    return Command.SINGLE_SUCCESS;
			case "spawnprotectionT":
			    int spawnSize = IntegerArgumentType.getInteger(ctx, "radius");
			    ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Set spawn-protection to %d", spawnSize));
			    return Command.SINGLE_SUCCESS;

			case "network-compression-thresholdV":
		        ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("network-compression-threshold is set to: %d", Dserver.getCompressionThreshold()));
		        return Command.SINGLE_SUCCESS;
			case "network-compression-thresholdT":
			    saveSettings("network-compression-threshold", "field_219001_N", IntegerArgumentType.getInteger(ctx, "threshold"));
			    ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Set network-compression-threshold to %d", IntegerArgumentType.getInteger(ctx, "threshold")));
			    return Command.SINGLE_SUCCESS;

			case "online-modeV":
			    ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("online-mode is set to: %s", Boolean.toString(server.usesAuthentication())));
			    return Command.SINGLE_SUCCESS;
			case "online-modeT":
				saveSettings("online-mode", "field_219007_a", BoolArgumentType.getBool(ctx, "toggle"));
			    server.setUsesAuthentication(BoolArgumentType.getBool(ctx, "toggle"));
			    ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Set online-mode to %s", Boolean.toString(BoolArgumentType.getBool(ctx, "toggle"))));
			    return Command.SINGLE_SUCCESS;

			case "op-permission-levelV":
			    ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("op-permission-level is set to: %s", Integer.toString(Dserver.getOperatorUserPermissionLevel())));
			    return Command.SINGLE_SUCCESS;
			case "op-permission-levelT":
				saveSettings("op-permission-level", "field_218997_J", IntegerArgumentType.getInteger(ctx, "level"));
			    ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Set op-permission-level to %s", Integer.toString(IntegerArgumentType.getInteger(ctx, "level"))));
			    return Command.SINGLE_SUCCESS;

			case "player-idle-timeoutV":
			    ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("player-idle-timeout is set to: %s", Integer.toString(Dserver.getPlayerIdleTimeout())));
			    return Command.SINGLE_SUCCESS;
			case "player-idle-timeoutT":
				saveSettings("player-idle-timeout", "field_219005_R", IntegerArgumentType.getInteger(ctx, "timeout"));
				server.setPlayerIdleTimeout(IntegerArgumentType.getInteger(ctx, "timeout"));
			    ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Set player-idle-timeout to %s", Integer.toString(IntegerArgumentType.getInteger(ctx, "timeout"))));
			    return Command.SINGLE_SUCCESS;

			case "prevent-proxy-connectionsV":
			    ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("prevent-proxy-connections is set to: %s", Boolean.toString(server.getPreventProxyConnections())));
			    return Command.SINGLE_SUCCESS;
			case "prevent-proxy-connectionsT":
				saveSettings("prevent-proxy-connections", "field_219008_b", BoolArgumentType.getBool(ctx, "toggle"));
			    server.setPreventProxyConnections(BoolArgumentType.getBool(ctx, "toggle"));
			    ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Set prevent-proxy-connections to %s", Boolean.toString(BoolArgumentType.getBool(ctx, "toggle"))));
			    return Command.SINGLE_SUCCESS;

			case "pvpV":
			    ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("pvp is set to: %s", Boolean.toString(server.isPvpAllowed())));
			    return Command.SINGLE_SUCCESS;
			case "pvpT":
				saveSettings("pvp", "field_219012_f", BoolArgumentType.getBool(ctx, "toggle"));
			    server.setPvpAllowed(BoolArgumentType.getBool(ctx, "toggle"));
			    ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Set pvp to %s", Boolean.toString(BoolArgumentType.getBool(ctx, "toggle"))));
			    return Command.SINGLE_SUCCESS;

			default:
			    ChatOutputHandler.chatError(ctx.getSource(), Translator.format(FEPermissions.MSG_UNKNOWN_SUBCOMMAND, params));
			}
		} catch (NoSuchFieldException e) {
		    ChatOutputHandler.chatError(ctx.getSource(), "Failed to change setting NSFE!");
			e.printStackTrace();
		} catch (SecurityException e) {
		    ChatOutputHandler.chatError(ctx.getSource(), "Failed to change setting SeE!");
			e.printStackTrace();
		} catch (ScriptException e) {
		    ChatOutputHandler.chatError(ctx.getSource(), "Failed to change setting ScE!");
			e.printStackTrace();
		} catch (Exception e) {
		    ChatOutputHandler.chatError(ctx.getSource(), "Failed to change setting E!");
			e.printStackTrace();
		}
        return Command.SINGLE_SUCCESS;
    }
    public static void saveSettings(String propertiesName,  String settingsFieldName, Object newValue) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
    	//save changed setting to ServerProperties
    	ServerPropertiesProvider settings= ServerUtil.getServerPropProvider((DedicatedServer) ServerLifecycleHooks.getCurrentServer());
    	ServerUtil.changeFinalFieldNonStaticField(settings.getProperties(), settingsFieldName, newValue);
    	//save changed setting to server.properties file
    	Properties props = ObfuscationReflectionHelper.getPrivateValue(PropertyManager.class, settings.getProperties(), "field_73672_b");
    	props.put(propertiesName, Objects.toString(newValue));
    	ObfuscationReflectionHelper.setPrivateValue(PropertyManager.class, settings.getProperties(), props, "field_73672_b");
    	settings.forceSave();
    	//try (OutputStream outputstream = Files.newOutputStream(ObfuscationReflectionHelper.getPrivateValue(ServerPropertiesProvider.class, settings, "field_219036_a"))) {
    	//	net.minecraftforge.common.util.SortedProperties.store(props, outputstream, "Minecraft server properties");
    	//} catch (IOException ioexception) {
    	//	ChatOutputHandler.chatError(source, "Failed to save properties config");
    	//	ioexception.printStackTrace();
    	//}
    }
}
