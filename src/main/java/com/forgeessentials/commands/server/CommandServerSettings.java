package com.forgeessentials.commands.server;

import java.util.Arrays;
import java.util.List;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerPropertiesProvider;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameType;
import net.minecraft.world.server.ServerWorld;
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
                                .then(Commands.argument("maxSize", IntegerArgumentType.integer(0, Integer.MAX_VALUE))
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
                .then(Commands.literal("allowpvp")
                        .then(Commands.literal("modify")
                                .then(Commands.argument("toggle", BoolArgumentType.bool())
                                        .executes(CommandContext -> execute(CommandContext, "allowpvpT")
                                                )
                                        )
                                )
                        .then(Commands.literal("view")
                                .executes(CommandContext -> execute(CommandContext, "allowpvpV")
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
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        DedicatedServer Dserver = (DedicatedServer) ServerLifecycleHooks.getCurrentServer();

        if (params.equals("blank"))
        {
            ChatOutputHandler.chatNotification(ctx.getSource(), Translator.format("Options: %s", StringUtils.join(options, ", ")));
            return Command.SINGLE_SUCCESS;
        }
        ServerPropertiesProvider settings= ServerUtil.getServerPropProvider((DedicatedServer) ServerLifecycleHooks.getCurrentServer());
        try {
			switch (params)
			{
			case "allow-flightV":
			    ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Allow flight  is set to: %s", Boolean.toString(server.isFlightAllowed())));
			    return Command.SINGLE_SUCCESS;
			case "allow-flightT":
			    server.setFlightAllowed(BoolArgumentType.getBool(ctx, "toggle"));
			    ServerUtil.changeFinalField(settings.getProperties().getClass().getField("field_219013_g"), BoolArgumentType.getBool(ctx, "toggle"));
			    settings.forceSave();
			    ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Set allow-flight to %s", Boolean.toString(BoolArgumentType.getBool(ctx, "toggle"))));
			    return Command.SINGLE_SUCCESS;

			case "allow-netherV":
			    ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Allow nether is set to: %s", Boolean.toString(server.isNetherEnabled())));
			    return Command.SINGLE_SUCCESS;
			case "allow-netherT":
			    ServerUtil.changeFinalField(settings.getProperties().getClass().getField("field_218991_D"), BoolArgumentType.getBool(ctx, "toggle")); 
			    settings.forceSave();
			    ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Set allow-nether to %s", Boolean.toString(BoolArgumentType.getBool(ctx, "toggle"))));
			    return Command.SINGLE_SUCCESS;

			case "broadcast-console-to-opsV":
			    ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Allow broadcast-console-to-ops: %s", Boolean.toString(Dserver.shouldInformAdmins())));
			    return Command.SINGLE_SUCCESS;
			case "broadcast-console-to-opsT":
			    ServerUtil.changeFinalField(settings.getProperties().getClass().getField("field_219003_P"), BoolArgumentType.getBool(ctx, "toggle"));
			    settings.forceSave();
			    ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Set broadcast-console-to-ops to %s", Boolean.toString(BoolArgumentType.getBool(ctx, "toggle"))));
			    return Command.SINGLE_SUCCESS;

			case "broadcast-rcon-to-opsV":
			    ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Allow broadcast-rcon-to-ops: %s", Boolean.toString(Dserver.shouldRconBroadcast())));
			    return Command.SINGLE_SUCCESS;
			case "broadcast-rcon-to-opsT":
			    ServerUtil.changeFinalField(settings.getProperties().getClass().getField("field_219002_O"), BoolArgumentType.getBool(ctx, "toggle"));
			    settings.forceSave();
			    ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Set broadcast-rcon-to-ops to %s", Boolean.toString(BoolArgumentType.getBool(ctx, "toggle"))));
			    return Command.SINGLE_SUCCESS;

			case "difficultyV":
			    ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Difficulty is set to: %s", server.getWorldData().getDifficulty()));
			    return Command.SINGLE_SUCCESS;
			case "difficultyT":
			    Difficulty difficulty = Difficulty.byId(IntegerArgumentType.getInteger(ctx, "difficulity"));
			    server.setDifficulty(difficulty, true);
			    ServerUtil.changeFinalField(settings.getProperties().getClass().getField("field_219019_m"), BoolArgumentType.getBool(ctx, "toggle"));
			    settings.forceSave();
			    ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Set difficulty to %s", difficulty.name()));
			    return Command.SINGLE_SUCCESS;

			case "enable-command-blockV":
			    ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Allow enable-command-block is set to: %s", Boolean.toString(Dserver.isCommandBlockEnabled())));
			    return Command.SINGLE_SUCCESS;
			case "enable-command-blockT":
			    ServerUtil.changeFinalField(settings.getProperties().getClass().getField("field_218995_H"), BoolArgumentType.getBool(ctx, "toggle"));
			    settings.forceSave();
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
				ServerUtil.changeFinalField(settings.getProperties().getClass().getField("field_241080_Q_"), BoolArgumentType.getBool(ctx, "toggle"));
			    settings.forceSave();
			    ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Set enable-status to %s", Boolean.toString(BoolArgumentType.getBool(ctx, "toggle"))));
			    return Command.SINGLE_SUCCESS;

			case "enforce-whitelistV":
			    ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Allow enforce-whitelist is set to: %s", Boolean.toString(settings.getProperties().enforceWhitelist)));
			    return Command.SINGLE_SUCCESS;
			case "enforce-whitelistT":
			    ServerUtil.changeFinalField(settings.getProperties().getClass().getField("field_219017_k"), BoolArgumentType.getBool(ctx, "toggle"));
			    settings.forceSave();
			    server.setEnforceWhitelist(BoolArgumentType.getBool(ctx, "toggle"));
			    ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Set enforce-whitelist to %s", Boolean.toString(BoolArgumentType.getBool(ctx, "toggle"))));
			    return Command.SINGLE_SUCCESS;

			case "entity-broadcast-range-percentageV":
			    ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("entity-broadcast-range-percentage is set to: %s", Boolean.toString(Dserver.repliesToStatus())));
			    return Command.SINGLE_SUCCESS;
			case "entity-broadcast-range-percentageT":
				ServerUtil.changeFinalField(settings.getProperties().getClass().getField("field_241081_R_"), IntegerArgumentType.getInteger(ctx, "percentage"));
			    settings.forceSave();
			    ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Set entity-broadcast-range-percentage to %s", Integer.toString(IntegerArgumentType.getInteger(ctx, "percentage"))));
			    return Command.SINGLE_SUCCESS;

			case "force-gamemodeV":
			    ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("force-gamemode is set to: %s", Boolean.toString(server.getForceGameType())));
			    return Command.SINGLE_SUCCESS;
			case "force-gamemodeT":
				ServerUtil.changeFinalField(settings.getProperties().getClass().getField("field_219016_j"), BoolArgumentType.getBool(ctx, "toggle"));
			    settings.forceSave();
			    server.setForceGameType(BoolArgumentType.getBool(ctx, "toggle"));
			    ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Set force-gamemode to %s", Boolean.toString(BoolArgumentType.getBool(ctx, "toggle"))));
			    return Command.SINGLE_SUCCESS;

			case "function-permission-levelV":
			    ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("function-permission-level is set to: %s", Integer.toString(Dserver.getFunctionCompilationLevel())));
			    return Command.SINGLE_SUCCESS;
			case "function-permission-levelT":
				ServerUtil.changeFinalField(settings.getProperties().getClass().getField("field_225395_K"), IntegerArgumentType.getInteger(ctx, "level"));
			    settings.forceSave();
			    ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Set function-permission-level to %s", Integer.toString(IntegerArgumentType.getInteger(ctx, "level"))));
			    return Command.SINGLE_SUCCESS;

			case "gamemodeV":
			    ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Default gamemode is set to: %s", server.getDefaultGameType().getName()));
			    return Command.SINGLE_SUCCESS;
			case "gamemodeT":
			    GameType gamemode = GameType.byId(IntegerArgumentType.getInteger(ctx, "gamemode"));
			    server.setDefaultGameType(gamemode);
			    ServerUtil.changeFinalField(settings.getProperties().getClass().getField("field_219020_n"), gamemode);
			    settings.forceSave();
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
			    ServerUtil.changeFinalField(settings.getProperties().getClass().getField("field_219026_t"), IntegerArgumentType.getInteger(ctx, "buildlimit"));
			    settings.forceSave();
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
			    ServerUtil.changeFinalField(settings.getProperties().getClass().getField("field_219004_Q"), IntegerArgumentType.getInteger(ctx, "maxSize"));
			    settings.forceSave();
			    ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Set max-world-size to %d", IntegerArgumentType.getInteger(ctx, "maxSize")));
			    return Command.SINGLE_SUCCESS;

			case "motdV":
			    ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("MotD = %s", server.getMotd()));
			    return Command.SINGLE_SUCCESS;
			case "motdT":
			    String motd = ScriptArguments.process(StringArgumentType.getString(ctx, "motd"), null);
			    server.getStatus().setDescription(new StringTextComponent(ChatOutputHandler.formatColors(motd)));
			    server.setMotd(motd);
			    ServerUtil.changeFinalField(settings.getProperties().getClass().getField("field_219015_i"), motd);
			    settings.forceSave();
			    ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Set MotD to %s", motd));
			    return Command.SINGLE_SUCCESS;

			case "spawnprotectionV":
			    ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Spawn protection size: %d", server.getSpawnProtectionRadius()));
			    return Command.SINGLE_SUCCESS;
			case "spawnprotectionT":
			    int spawnSize = IntegerArgumentType.getInteger(ctx, "radius");
			    ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Set spawn-protection to %d", spawnSize));
			    return Command.SINGLE_SUCCESS;
			default:
			    ChatOutputHandler.chatError(ctx.getSource(), Translator.format(FEPermissions.MSG_UNKNOWN_SUBCOMMAND, params));
			}
		} catch (NoSuchFieldException e) {
		    ChatOutputHandler.chatError(ctx.getSource(), "Failed to change setting!");
			e.printStackTrace();
		} catch (SecurityException e) {
		    ChatOutputHandler.chatError(ctx.getSource(), "Failed to change setting!");
			e.printStackTrace();
		} catch (ScriptException e) {
		    ChatOutputHandler.chatError(ctx.getSource(), "Failed to change setting!");
			e.printStackTrace();
		} catch (Exception e) {
		    ChatOutputHandler.chatError(ctx.getSource(), "Failed to change setting!");
			e.printStackTrace();
		}
        return Command.SINGLE_SUCCESS;
    }
}
