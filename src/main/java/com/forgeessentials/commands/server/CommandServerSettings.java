package com.forgeessentials.commands.server;

import java.util.Arrays;
import java.util.List;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.MessageArgument;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.core.commands.BaseCommand;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.scripting.ScriptArguments;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class CommandServerSettings extends BaseCommand
{

    public CommandServerSettings(String name, int permissionLevel, boolean enabled)
    {
        super(name, permissionLevel, enabled);
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

    @OnlyIn(Dist.DEDICATED_SERVER)
    public void doSetProperty(String id, Object value)
    {
        DedicatedServer server = (DedicatedServer) ServerLifecycleHooks.getCurrentServer();
        server.getProperties();//TODO add a save to save values
    }

    public void setProperty(String id, Object value)
    {
        if (FMLEnvironment.dist == Dist.DEDICATED_SERVER)
            doSetProperty(id, value);
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        return builder
                .then(Commands.literal("allowflight")
                        .then(Commands.literal("modify")
                                .then(Commands.argument("toggle", BoolArgumentType.bool())
                                        .executes(CommandContext -> execute(CommandContext, "allowflightT")
                                                )
                                        )
                                )
                        .then(Commands.literal("view")
                                .executes(CommandContext -> execute(CommandContext, "allowflightV")
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
                .then(Commands.literal("buildlimit")
                        .then(Commands.literal("modify")
                                .then(Commands.argument("buildlimit", IntegerArgumentType.integer(0, Integer.MAX_VALUE))
                                        .executes(CommandContext -> execute(CommandContext, "buildlimitT")
                                                )
                                        )
                                )
                        .then(Commands.literal("view")
                                .executes(CommandContext -> execute(CommandContext, "buildlimitV")
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
                .then(Commands.literal("motd")
                        .then(Commands.literal("modify")
                                .then(Commands.argument("motd", MessageArgument.message())
                                        .executes(CommandContext -> execute(CommandContext, "motdT")
                                                )
                                        )
                                )
                        .then(Commands.literal("view")
                                .executes(CommandContext -> execute(CommandContext, "motdV")
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
    public int execute(CommandContext<CommandSource> ctx, Object... params) throws CommandSyntaxException
    {
        if (!FMLEnvironment.dist.isDedicatedServer())
            ChatOutputHandler.chatError(ctx.getSource(), "You can use this command only on dedicated servers");
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();

        if (params.toString() == "blank")
        {
            ChatOutputHandler.chatNotification(ctx.getSource(), Translator.format("Options: %s", StringUtils.join(options, ", ")));
            return Command.SINGLE_SUCCESS;
        }

        String subCmd = params.toString();
        switch (subCmd)
        {
        case "allowflightV":
            ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Allow flight: %s", Boolean.toString(server.isFlightAllowed())));
            return Command.SINGLE_SUCCESS;
        case "allowflightT":
            boolean allowFlight = BoolArgumentType.getBool(ctx, "toggle");
            server.setFlightAllowed(allowFlight);
            setProperty("allow-flight", allowFlight);
            ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Set allow-flight to %s", Boolean.toString(allowFlight)));
            return Command.SINGLE_SUCCESS;
        case "allowpvpV":
            ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Allow PvP: %s", Boolean.toString(server.isPvpAllowed())));
            return Command.SINGLE_SUCCESS;
        case "allowpvpT":
            boolean allowPvP = BoolArgumentType.getBool(ctx, "toggle");
            server.setPvpAllowed(allowPvP);
            setProperty("pvp", allowPvP);
            ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Set pvp to %s", Boolean.toString(allowPvP)));
            return Command.SINGLE_SUCCESS;
        case "buildlimitV":
                ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Set build limit to %d", server.getMaxBuildHeight()));
                return Command.SINGLE_SUCCESS;
        case "buildlimitT":
            int buildLimit = IntegerArgumentType.getInteger(ctx, "buildlimit");
            server.setMaxBuildHeight(buildLimit);
            setProperty("max-build-height", buildLimit);
            ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Set max-build-height to %d", buildLimit));
            return Command.SINGLE_SUCCESS;
        case "motdV":
            ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("MotD = %s", server.getMotd()));
            return Command.SINGLE_SUCCESS;
        case "motdT":
            String motd = ScriptArguments.process(MessageArgument.getMessage(ctx, "motd").getString(), null);
            server.getStatus().setDescription(new StringTextComponent(ChatOutputHandler.formatColors(motd)));
            server.setMotd(motd);
            setProperty("motd", motd);
            ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Set MotD to %s", motd));
            return Command.SINGLE_SUCCESS;
        case "spawnprotectionV":
            ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Spawn protection size: %d", server.getSpawnProtectionRadius()));
            return Command.SINGLE_SUCCESS;
        case "spawnprotectionT":
            int spawnSize = IntegerArgumentType.getInteger(ctx, "radius");
            setProperty("spawn-protection", spawnSize);
            ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Set spawn-protection to %d", spawnSize));
            return Command.SINGLE_SUCCESS;
        case "gamemodeV":
            ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Default gamemode set to %s", server.getDefaultGameType().getName()));
            return Command.SINGLE_SUCCESS;
        case "gamemodeT":
            GameType gamemode = GameType.byId(IntegerArgumentType.getInteger(ctx, "gamemode"));
            server.setDefaultGameType(gamemode);
            setProperty("gamemode", gamemode.ordinal());
            ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Set default gamemode to %s", gamemode.getName()));
            return Command.SINGLE_SUCCESS;
        case "difficultyV":
            ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Difficulty set to %s", server.getWorldData().getDifficulty()));
            return Command.SINGLE_SUCCESS;
        case "difficultyT":
            Difficulty difficulty = Difficulty.byId(IntegerArgumentType.getInteger(ctx, "difficulity"));
            server.setDifficulty(difficulty, false);
            setProperty("difficulty", difficulty.ordinal());
            ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Set difficulty to %s", difficulty.name()));
            return Command.SINGLE_SUCCESS;

        default:
            ChatOutputHandler.chatError(ctx.getSource(), Translator.format(FEPermissions.MSG_UNKNOWN_SUBCOMMAND, subCmd));
        }
        return Command.SINGLE_SUCCESS;
    }
}
