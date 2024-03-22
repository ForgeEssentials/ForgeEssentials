package com.forgeessentials.permissions.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.commands.registration.FECommandParsingException;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraftforge.fmllegacy.server.ServerLifecycleHooks;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import org.jetbrains.annotations.NotNull;

public class CommandPermissions extends ForgeEssentialsCommandBuilder
{
    public CommandPermissions(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public final @NotNull String getPrimaryAlias()
    {
        return "perm";
    }

    @Override
    public String @NotNull [] getDefaultSecondaryAliases()
    {
        return new String[] { "fep", "p" };
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
                .then(Commands.literal("help")
                        .executes(CommandContext -> execute(CommandContext, "help")
                                )
                        )
                .then(Commands.literal("user")
                        .then(Commands.literal("help")
                                .executes(CommandContext -> execute(CommandContext, "user")
                                        )
                                )
                        .then(Commands.argument("player", StringArgumentType.word())
                                .suggests(SUGGEST_players)
                                .executes(CommandContext -> execute(CommandContext, "user&&"+StringArgumentType.getString(CommandContext, "player"))
                                        )
                                .then(Commands.literal("perms")
                                        .executes(CommandContext -> execute(CommandContext, "user&&"+StringArgumentType.getString(CommandContext, "player")+"&&perms")
                                                )
                                        )
                                .then(Commands.literal("prefix")
                                        .then(Commands.argument("prefix", StringArgumentType.greedyString())
                                                .executes(CommandContext -> execute(CommandContext, "user&&"+StringArgumentType.getString(CommandContext, "player")+"&&prefix&&"+StringArgumentType.getString(CommandContext, "prefix"))
                                                        )
                                                )
                                        .then(Commands.literal("clear")
                                                .executes(CommandContext -> execute(CommandContext, "user&&"+StringArgumentType.getString(CommandContext, "player")+"&&prefix&&clear")
                                                        )
                                                )
                                        )
                                .then(Commands.literal("suffix")
                                        .then(Commands.argument("suffix", StringArgumentType.greedyString())
                                                .executes(CommandContext -> execute(CommandContext, "user&&"+StringArgumentType.getString(CommandContext, "player")+"&&suffix&&"+StringArgumentType.getString(CommandContext, "suffix"))
                                                        )
                                                )
                                        .then(Commands.literal("clear")
                                                .executes(CommandContext -> execute(CommandContext, "user&&"+StringArgumentType.getString(CommandContext, "player")+"&&suffix&&clear")
                                                        )
                                                )
                                        )
                                .then(Commands.literal("zone")
                                        .then(Commands.argument("zone", StringArgumentType.string())
                                                .suggests(SUGGEST_zones)
                                                .executes(CommandContext -> execute(CommandContext, "user&&"+StringArgumentType.getString(CommandContext, "player")+"&&zone&&"+StringArgumentType.getString(CommandContext, "zone"))
                                                        )
                                                .then(Commands.literal("group")
                                                        .then(Commands.literal("list")
                                                                .executes(CommandContext -> execute(CommandContext, "user&&"+StringArgumentType.getString(CommandContext, "player")+"&&zone&&"+StringArgumentType.getString(CommandContext, "zone")+"&&group")
                                                                        )
                                                                )
                                                        .then(Commands.argument("arg", StringArgumentType.string())
                                                                .suggests(SUGGEST_parseUserGroupArgs)
                                                                .executes(CommandContext -> execute(CommandContext, "user&&"+StringArgumentType.getString(CommandContext, "player")+"&&zone&&"+StringArgumentType.getString(CommandContext, "zone")+"&&group&&"+StringArgumentType.getString(CommandContext, "arg"))
                                                                        )
                                                                .then(Commands.argument("group", StringArgumentType.string())
                                                                        .suggests(SUGGEST_group)
                                                                        .executes(CommandContext -> execute(CommandContext, "user&&"+StringArgumentType.getString(CommandContext, "player")+"&&zone&&"+StringArgumentType.getString(CommandContext, "zone")+"&&group&&"+StringArgumentType.getString(CommandContext, "arg")+"&&"+StringArgumentType.getString(CommandContext, "group"))
                                                                                )
                                                                        )
                                                                )
                                                        )
                                                .then(Commands.literal("perms")
                                                        .executes(CommandContext -> execute(CommandContext, "user&&"+StringArgumentType.getString(CommandContext, "player")+"&&zone&&"+StringArgumentType.getString(CommandContext, "zone")+"&&perms")
                                                                )
                                                        )
                                                .then(Commands.literal("allow")
                                                        .then(Commands.argument("perm", StringArgumentType.greedyString())
                                                                .suggests(SUGGEST_perm)
                                                                .executes(CommandContext -> execute(CommandContext, "user&&"+StringArgumentType.getString(CommandContext, "player")+"&&zone&&"+StringArgumentType.getString(CommandContext, "zone")+"&&allow&&"+StringArgumentType.getString(CommandContext, "perm"))
                                                                        )
                                                                )
                                                        )
                                                .then(Commands.literal("deny")
                                                        .then(Commands.argument("perm", StringArgumentType.greedyString())
                                                                .suggests(SUGGEST_perm)
                                                                .executes(CommandContext -> execute(CommandContext, "user&&"+StringArgumentType.getString(CommandContext, "player")+"&&zone&&"+StringArgumentType.getString(CommandContext, "zone")+"&&deny&&"+StringArgumentType.getString(CommandContext, "perm"))
                                                                        )
                                                                )
                                                        )
                                                .then(Commands.literal("clear")
                                                        .then(Commands.argument("perm", StringArgumentType.greedyString())
                                                                .suggests(SUGGEST_PlayerPerm)
                                                                .executes(CommandContext -> execute(CommandContext, "user&&"+StringArgumentType.getString(CommandContext, "player")+"&&zone&&"+StringArgumentType.getString(CommandContext, "zone")+"&&clear&&"+StringArgumentType.getString(CommandContext, "perm"))
                                                                        )
                                                                )
                                                        )
                                                .then(Commands.literal("value")
                                                        .then(Commands.argument("perm", StringArgumentType.greedyString())
                                                                .suggests(SUGGEST_perm)
                                                                .executes(CommandContext -> execute(CommandContext, "user&&"+StringArgumentType.getString(CommandContext, "player")+"&&zone&&"+StringArgumentType.getString(CommandContext, "zone")+"&&value&&"+StringArgumentType.getString(CommandContext, "perm"))
                                                                        )
                                                                )
                                                        )
                                                .then(Commands.literal("spawn")
                                                        .then(Commands.literal("help")
                                                                .executes(CommandContext -> execute(CommandContext, "user&&"+StringArgumentType.getString(CommandContext, "player")+"&&zone&&"+StringArgumentType.getString(CommandContext, "zone")+"&&spawn")
                                                                        )
                                                                )
                                                        .then(Commands.literal("bed")
                                                                .then(Commands.literal("enable")
                                                                        .executes(CommandContext -> execute(CommandContext, "user&&"+StringArgumentType.getString(CommandContext, "player")+"&&zone&&"+StringArgumentType.getString(CommandContext, "zone")+"&&spawn&&bed&&enable")
                                                                                )
                                                                        )
                                                                .then(Commands.literal("disable")
                                                                        .executes(CommandContext -> execute(CommandContext, "user&&"+StringArgumentType.getString(CommandContext, "player")+"&&zone&&"+StringArgumentType.getString(CommandContext, "zone")+"&&spawn&&bed&&disable")
                                                                                )
                                                                        )
                                                                )
                                                        .then(Commands.literal("here")
                                                                .executes(CommandContext -> execute(CommandContext, "user&&"+StringArgumentType.getString(CommandContext, "player")+"&&zone&&"+StringArgumentType.getString(CommandContext, "zone")+"&&spawn&&here")
                                                                        )
                                                                )
                                                        .then(Commands.literal("clear")
                                                                .executes(CommandContext -> execute(CommandContext, "user&&"+StringArgumentType.getString(CommandContext, "player")+"&&zone&&"+StringArgumentType.getString(CommandContext, "zone")+"&&spawn&&clear")
                                                                        )
                                                                )
                                                        .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                                                .then(Commands.argument("dim", DimensionArgument.dimension())
                                                                        .executes(CommandContext -> execute(CommandContext, "user&&"+StringArgumentType.getString(CommandContext, "player")+"&&zone&&"+StringArgumentType.getString(CommandContext, "zone")+"&&spawn&&"+Integer.toString(BlockPosArgument.getLoadedBlockPos(CommandContext, "pos").getX())+"&&"+Integer.toString(BlockPosArgument.getLoadedBlockPos(CommandContext, "pos").getY())+"&&"+Integer.toString(BlockPosArgument.getLoadedBlockPos(CommandContext, "pos").getZ())+"&&"+DimensionArgument.getDimension(CommandContext, "dim").dimension().location().toString())
                                                                                )
                                                                        )
                                                                )
                                                        )
                                                .then(Commands.literal("denydefault")
                                                        .executes(CommandContext -> execute(CommandContext, "user&&"+StringArgumentType.getString(CommandContext, "player")+"&&zone&&"+StringArgumentType.getString(CommandContext, "zone")+"&&denydefault")
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                .then(Commands.literal("group")
                        .then(Commands.literal("help")
                                .executes(CommandContext -> execute(CommandContext, "group")
                                        )
                                )
                        .then(Commands.argument("group", StringArgumentType.string())
                                .suggests(SUGGEST_group)
                                .then(Commands.literal("create")
                                        .executes(CommandContext -> execute(CommandContext, "group&&"+StringArgumentType.getString(CommandContext, "group")+"&&create")
                                                )
                                        )
                                .then(Commands.literal("perms")
                                        .executes(CommandContext -> execute(CommandContext, "group&&"+StringArgumentType.getString(CommandContext, "group")+"&&perms")
                                                )
                                        )
                                .then(Commands.literal("users")
                                        .executes(CommandContext -> execute(CommandContext, "group&&"+StringArgumentType.getString(CommandContext, "group")+"&&users")
                                                )
                                        )
                                .then(Commands.literal("priority")
                                        .then(Commands.literal("help")
                                                .executes(CommandContext -> execute(CommandContext, "group&&"+StringArgumentType.getString(CommandContext, "group")+"&&priority")
                                                        )
                                                )
                                        .then(Commands.argument("priority", IntegerArgumentType.integer())
                                                .executes(CommandContext -> execute(CommandContext, "group&&"+StringArgumentType.getString(CommandContext, "group")+Integer.toString(IntegerArgumentType.getInteger(CommandContext, "priority")))
                                                        )
                                                )
                                        )
                                .then(Commands.literal("parent")
                                        .then(Commands.literal("help")
                                                .executes(CommandContext -> execute(CommandContext, "group&&"+StringArgumentType.getString(CommandContext, "group")+"&&parent")
                                                        )
                                                )
                                        .then(Commands.literal("add")
                                                .then(Commands.argument("group1", StringArgumentType.string())
                                                        .suggests(SUGGEST_group)
                                                        .executes(CommandContext -> execute(CommandContext, "group&&"+StringArgumentType.getString(CommandContext, "group")+"&&parent&&add&&"+StringArgumentType.getString(CommandContext, "group1"))
                                                                )
                                                        )
                                                )
                                        .then(Commands.literal("remove")
                                                .then(Commands.argument("group1", StringArgumentType.string())
                                                        .suggests(SUGGEST_group)
                                                        .executes(CommandContext -> execute(CommandContext, "group&&"+StringArgumentType.getString(CommandContext, "group")+"&&parent&&remove&&"+StringArgumentType.getString(CommandContext, "group1"))
                                                                )
                                                        )
                                                )
                                        .then(Commands.literal("clear")
                                                .executes(CommandContext -> execute(CommandContext, "group&&"+StringArgumentType.getString(CommandContext, "group")+"&&parent&&clear")
                                                        )
                                                )
                                        .executes(CommandContext -> execute(CommandContext, "group&&"+StringArgumentType.getString(CommandContext, "group")+"&&parent")
                                                )
                                        )
                                .then(Commands.literal("include")
                                        .then(Commands.literal("help")
                                                .executes(CommandContext -> execute(CommandContext, "group&&"+StringArgumentType.getString(CommandContext, "group")+"&&include")
                                                        )
                                                )
                                        .then(Commands.literal("add")
                                                .then(Commands.argument("group1", StringArgumentType.string())
                                                        .suggests(SUGGEST_group)
                                                        .executes(CommandContext -> execute(CommandContext, "group&&"+StringArgumentType.getString(CommandContext, "group")+"&&include&&add&&"+StringArgumentType.getString(CommandContext, "group1"))
                                                                )
                                                        )
                                                )
                                        .then(Commands.literal("remove")
                                                .then(Commands.argument("group1", StringArgumentType.string())
                                                        .suggests(SUGGEST_group)
                                                        .executes(CommandContext -> execute(CommandContext, "group&&"+StringArgumentType.getString(CommandContext, "group")+"&&include&&remove&&"+StringArgumentType.getString(CommandContext, "group1"))
                                                                )
                                                        )
                                                )
                                        .then(Commands.literal("clear")
                                                .executes(CommandContext -> execute(CommandContext, "group&&"+StringArgumentType.getString(CommandContext, "group")+"&&include&&clear")
                                                        )
                                                )
                                        .executes(CommandContext -> execute(CommandContext, "group&&"+StringArgumentType.getString(CommandContext, "group")+"&&include")
                                                )
                                        )
                                .then(Commands.literal("prefix")
                                        .then(Commands.argument("prefix", StringArgumentType.greedyString())
                                                .executes(CommandContext -> execute(CommandContext, "group&&"+StringArgumentType.getString(CommandContext, "group")+"&&prefix&&"+StringArgumentType.getString(CommandContext, "prefix"))
                                                        )
                                                )
                                        .then(Commands.literal("clear")
                                                .executes(CommandContext -> execute(CommandContext, "group&&"+StringArgumentType.getString(CommandContext, "group")+"&&prefix&&clear")
                                                        )
                                                )
                                        )
                                .then(Commands.literal("suffix")
                                        .then(Commands.argument("suffix", StringArgumentType.greedyString())
                                                .executes(CommandContext -> execute(CommandContext, "group&&"+StringArgumentType.getString(CommandContext, "group")+"&&suffix&&"+StringArgumentType.getString(CommandContext, "suffix"))
                                                        )
                                                )
                                        .then(Commands.literal("clear")
                                                .executes(CommandContext -> execute(CommandContext, "group&&"+StringArgumentType.getString(CommandContext, "group")+"&&suffix&&clear")
                                                        )
                                                )
                                        )
                                .then(Commands.literal("zone")
                                        .then(Commands.argument("zone", StringArgumentType.string())
                                                .suggests(SUGGEST_zones)
                                                .executes(CommandContext -> execute(CommandContext, "group&&"+StringArgumentType.getString(CommandContext, "group")+"&&zone&&"+StringArgumentType.getString(CommandContext, "zone"))
                                                        )
                                                .then(Commands.literal("allow")
                                                        .then(Commands.argument("perm", StringArgumentType.greedyString())
                                                                .suggests(SUGGEST_perm)
                                                                .executes(CommandContext -> execute(CommandContext, "group&&"+StringArgumentType.getString(CommandContext, "group")+"&&zone&&"+StringArgumentType.getString(CommandContext, "zone")+"&&allow&&"+StringArgumentType.getString(CommandContext, "perm"))
                                                                        )
                                                                )
                                                        )
                                                .then(Commands.literal("deny")
                                                        .then(Commands.argument("perm", StringArgumentType.greedyString())
                                                                .suggests(SUGGEST_perm)
                                                                .executes(CommandContext -> execute(CommandContext, "group&&"+StringArgumentType.getString(CommandContext, "group")+"&&zone&&"+StringArgumentType.getString(CommandContext, "zone")+"&&deny&&"+StringArgumentType.getString(CommandContext, "perm"))
                                                                        )
                                                                )
                                                        )
                                                .then(Commands.literal("clear")
                                                        .then(Commands.argument("perm", StringArgumentType.greedyString())
                                                                .suggests(SUGGEST_GroupPerm)
                                                                .executes(CommandContext -> execute(CommandContext, "group&&"+StringArgumentType.getString(CommandContext, "group")+"&&zone&&"+StringArgumentType.getString(CommandContext, "zone")+"&&clear&&"+StringArgumentType.getString(CommandContext, "perm"))
                                                                        )
                                                                )
                                                        )
                                                .then(Commands.literal("value")
                                                        .then(Commands.argument("perm", StringArgumentType.greedyString())
                                                                .suggests(SUGGEST_perm)
                                                                .executes(CommandContext -> execute(CommandContext, "group&&"+StringArgumentType.getString(CommandContext, "group")+"&&zone&&"+StringArgumentType.getString(CommandContext, "zone")+"&&value&&"+StringArgumentType.getString(CommandContext, "perm"))
                                                                        )
                                                                )
                                                        )
                                                .then(Commands.literal("spawn")
                                                        .then(Commands.literal("help")
                                                                .executes(CommandContext -> execute(CommandContext, "group&&"+StringArgumentType.getString(CommandContext, "group")+"&&zone&&"+StringArgumentType.getString(CommandContext, "zone")+"&&spawn")
                                                                        )
                                                                )
                                                        .then(Commands.literal("bed")
                                                                .then(Commands.literal("enable")
                                                                        .executes(CommandContext -> execute(CommandContext, "group&&"+StringArgumentType.getString(CommandContext, "group")+"&&zone&&"+StringArgumentType.getString(CommandContext, "zone")+"&&spawn&&bed&&enable")
                                                                                )
                                                                        )
                                                                .then(Commands.literal("disable")
                                                                        .executes(CommandContext -> execute(CommandContext, "group&&"+StringArgumentType.getString(CommandContext, "group")+"&&zone&&"+StringArgumentType.getString(CommandContext, "zone")+"&&spawn&&bed&&disable")
                                                                                )
                                                                        )
                                                                )
                                                        .then(Commands.literal("here")
                                                                .executes(CommandContext -> execute(CommandContext, "group&&"+StringArgumentType.getString(CommandContext, "group")+"&&zone&&"+StringArgumentType.getString(CommandContext, "zone")+"&&spawn&&here")
                                                                        )
                                                                )
                                                        .then(Commands.literal("clear")
                                                                .executes(CommandContext -> execute(CommandContext, "group&&"+StringArgumentType.getString(CommandContext, "group")+"&&zone&&"+StringArgumentType.getString(CommandContext, "zone")+"&&spawn&&clear")
                                                                        )
                                                                )
                                                        .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                                                .then(Commands.argument("dim", DimensionArgument.dimension())
                                                                        .executes(CommandContext -> execute(CommandContext, "group&&"+StringArgumentType.getString(CommandContext, "group")+"&&zone&&"+StringArgumentType.getString(CommandContext, "zone")+"&&spawn&&"+Integer.toString(BlockPosArgument.getLoadedBlockPos(CommandContext, "pos").getX())+"&&"+Integer.toString(BlockPosArgument.getLoadedBlockPos(CommandContext, "pos").getY())+"&&"+Integer.toString(BlockPosArgument.getLoadedBlockPos(CommandContext, "pos").getZ())+"&&"+DimensionArgument.getDimension(CommandContext, "dim").dimension().location().toString())
                                                                                )
                                                                        )
                                                                )
                                                        )
                                                .then(Commands.literal("denydefault")
                                                        .executes(CommandContext -> execute(CommandContext, "group&&"+StringArgumentType.getString(CommandContext, "group")+"&&zone&&"+StringArgumentType.getString(CommandContext, "zone")+"&&denydefault")
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                .then(Commands.literal("global")
                        .then(Commands.literal("perms")
                                .executes(CommandContext -> execute(CommandContext, "global&&perms")
                                        )
                                )
                        .then(Commands.literal("users")
                                .executes(CommandContext -> execute(CommandContext, "global&&users")
                                        )
                                )
                        .then(Commands.literal("priority")
                                .then(Commands.literal("help")
                                        .executes(CommandContext -> execute(CommandContext, "global&&priority")
                                                )
                                        )
                                .then(Commands.argument("priority", IntegerArgumentType.integer())
                                        .executes(CommandContext -> execute(CommandContext, "global&&"+Integer.toString(IntegerArgumentType.getInteger(CommandContext, "priority")))
                                                )
                                        )
                                )
                        .then(Commands.literal("parent")
                                .then(Commands.literal("help")
                                        .executes(CommandContext -> execute(CommandContext, "global&&parent")
                                                )
                                        )
                                .then(Commands.literal("add")
                                        .then(Commands.argument("group1", StringArgumentType.string())
                                                .suggests(SUGGEST_group)
                                                .executes(CommandContext -> execute(CommandContext, "global&&parent&&add&&"+StringArgumentType.getString(CommandContext, "group1"))
                                                        )
                                                )
                                        )
                                .then(Commands.literal("remove")
                                        .then(Commands.argument("group1", StringArgumentType.string())
                                                .suggests(SUGGEST_group)
                                                .executes(CommandContext -> execute(CommandContext, "global&&parent&&remove&&"+StringArgumentType.getString(CommandContext, "group1"))
                                                        )
                                                )
                                        )
                                .then(Commands.literal("clear")
                                        .executes(CommandContext -> execute(CommandContext, "global&&parent&&clear")
                                                )
                                        )
                                .executes(CommandContext -> execute(CommandContext, "global&&parent")
                                        )
                                )
                        .then(Commands.literal("include")
                                .then(Commands.literal("help")
                                        .executes(CommandContext -> execute(CommandContext, "global&&include")
                                                )
                                        )
                                .then(Commands.literal("add")
                                        .then(Commands.argument("group1", StringArgumentType.string())
                                                .suggests(SUGGEST_group)
                                                .executes(CommandContext -> execute(CommandContext, "global&&include&&add&&"+StringArgumentType.getString(CommandContext, "group1"))
                                                        )
                                                )
                                        )
                                .then(Commands.literal("remove")
                                        .then(Commands.argument("group1", StringArgumentType.string())
                                                .suggests(SUGGEST_group)
                                                .executes(CommandContext -> execute(CommandContext, "global&&include&&remove&&"+StringArgumentType.getString(CommandContext, "group1"))
                                                        )
                                                )
                                        )
                                .then(Commands.literal("clear")
                                        .executes(CommandContext -> execute(CommandContext, "global&&include&&clear")
                                                )
                                        )
                                .executes(CommandContext -> execute(CommandContext, "global&&include")
                                        )
                                )
                        .then(Commands.literal("allow")
                                .then(Commands.argument("perm", StringArgumentType.greedyString())
                                        .suggests(SUGGEST_perm)
                                        .executes(CommandContext -> execute(CommandContext, "global&&allow&&"+StringArgumentType.getString(CommandContext, "perm"))
                                                )
                                        )
                                )
                        .then(Commands.literal("deny")
                                .then(Commands.argument("perm", StringArgumentType.greedyString())
                                        .suggests(SUGGEST_perm)
                                        .executes(CommandContext -> execute(CommandContext, "global&&deny&&"+StringArgumentType.getString(CommandContext, "perm"))
                                                )
                                        )
                                )
                        .then(Commands.literal("clear")
                                .then(Commands.argument("perm", StringArgumentType.greedyString())
                                        .suggests(SUGGEST_GlobalPerm)
                                        .executes(CommandContext -> execute(CommandContext, "global&&clear&&"+StringArgumentType.getString(CommandContext, "perm"))
                                                )
                                        )
                                )
                        .then(Commands.literal("value")
                                .then(Commands.argument("perm", StringArgumentType.greedyString())
                                        .suggests(SUGGEST_perm)
                                        .executes(CommandContext -> execute(CommandContext, "global&&value&&"+StringArgumentType.getString(CommandContext, "perm"))
                                                )
                                        )
                                )
                        .then(Commands.literal("spawn")
                                .then(Commands.literal("help")
                                        .executes(CommandContext -> execute(CommandContext, "global&&spawn")
                                                )
                                        )
                                .then(Commands.literal("bed")
                                        .then(Commands.literal("enable")
                                                .executes(CommandContext -> execute(CommandContext, "global&&spawn&&bed&&enable")
                                                        )
                                                )
                                        .then(Commands.literal("disable")
                                                .executes(CommandContext -> execute(CommandContext, "global&&spawn&&bed&&disable")
                                                        )
                                                )
                                        )
                                .then(Commands.literal("here")
                                        .executes(CommandContext -> execute(CommandContext, "global&&spawn&&here")
                                                )
                                        )
                                .then(Commands.literal("clear")
                                        .executes(CommandContext -> execute(CommandContext, "global&&spawn&&clear")
                                                )
                                        )
                                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                        .then(Commands.argument("dim", DimensionArgument.dimension())
                                                .executes(CommandContext -> execute(CommandContext, "global&&spawn&&"+Integer.toString(BlockPosArgument.getLoadedBlockPos(CommandContext, "pos").getX())+"&&"+Integer.toString(BlockPosArgument.getLoadedBlockPos(CommandContext, "pos").getY())+"&&"+Integer.toString(BlockPosArgument.getLoadedBlockPos(CommandContext, "pos").getZ())+"&&"+DimensionArgument.getDimension(CommandContext, "dim").dimension().location().toString())
                                                        )
                                                )
                                        )
                                )
                        .then(Commands.literal("denydefault")
                                .executes(CommandContext -> execute(CommandContext, "global&&denydefault")
                                        )
                                )
                        )
                .then(Commands.literal("list")
                        .then(Commands.literal("help")
                                .executes(CommandContext -> execute(CommandContext, "list")
                                        )
                                )
                        .then(Commands.argument("type", StringArgumentType.string())
                                .suggests(SUGGEST_ListArgs)
                                .executes(CommandContext -> execute(CommandContext, "list&&"+StringArgumentType.getString(CommandContext, "type"))
                                        )
                                )
                        )
                .then(Commands.literal("test")
                        .then(Commands.argument("perm", StringArgumentType.string())
                                .suggests(SUGGEST_perm)
                                .executes(CommandContext -> execute(CommandContext, "test&&"+StringArgumentType.getString(CommandContext, "perm"))
                                        )
                                )
                        )
                .then(Commands.literal("reload")
                        .executes(CommandContext -> execute(CommandContext, "reload")
                                )
                        )
                .then(Commands.literal("save")
                        .then(Commands.literal("saveNow")
                                .executes(CommandContext -> execute(CommandContext, "save")
                                        )
                                )
                        .then(Commands.literal("disable")
                                .executes(CommandContext -> execute(CommandContext, "save&&disable")
                                        )
                                )
                        .then(Commands.literal("enable")
                                .executes(CommandContext -> execute(CommandContext, "save&&enable")
                                        )
                                )
                        .then(Commands.literal("flatfile")
                                .executes(CommandContext -> execute(CommandContext, "save&&flatfile")
                                        )
                                )
                        .then(Commands.literal("singlejson")
                                .executes(CommandContext -> execute(CommandContext, "save&&singlejson")
                                        )
                                )
                        .then(Commands.literal("json")
                                .executes(CommandContext -> execute(CommandContext, "savejson")
                                        )
                                )
                        )
                .then(Commands.literal("debug")
                        .executes(CommandContext -> execute(CommandContext, "debug")
                                )
                        );
    }

    public static final SuggestionProvider<CommandSourceStack> SUGGEST_ListArgs = (ctx, builder) -> {
        List<String> listArgs = new ArrayList<>(Arrays.asList(PermissionCommandParser.parseListArgs));
        return SharedSuggestionProvider.suggest(listArgs, builder);
    };
    public static final SuggestionProvider<CommandSourceStack> SUGGEST_players = (ctx, builder) -> {
        List<String> listArgs = new ArrayList<>(Arrays.asList(ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayerNamesArray()));
        return SharedSuggestionProvider.suggest(listArgs, builder);
    };
    public static final SuggestionProvider<CommandSourceStack> SUGGEST_parseUserGroupArgs = (ctx, builder) -> {
        List<String> listArgs = new ArrayList<>(Arrays.asList(PermissionCommandParser.parseUserGroupArgs));
        return SharedSuggestionProvider.suggest(listArgs, builder);
    };
    public static final SuggestionProvider<CommandSourceStack> SUGGEST_zones = (ctx, builder) -> {
        List<String> listzones = new ArrayList<>();
        for (Zone z : APIRegistry.perms.getZones())
        {
            listzones.add(z.getName());
        }
        if(listzones.contains("_ROOT_")) {
            listzones.remove("_ROOT_");
        }
        if(listzones.contains("_SERVER_")) {
            listzones.remove("_SERVER_");
        }
        for (int index = 0; index < listzones.size(); index++)
        {
            if (listzones.get(index).contains(":"))
            {
                listzones.set(index, listzones.get(index).replace(":", "-"));
            }
        }
        listzones.add("MainServerZone");
        return SharedSuggestionProvider.suggest(listzones, builder);
    };
    public static final SuggestionProvider<CommandSourceStack> SUGGEST_group = (ctx, builder) -> {
        List<String> listgroup = new ArrayList<>(APIRegistry.perms.getServerZone().getGroups());
        return SharedSuggestionProvider.suggest(listgroup, builder);
    };
    public static final SuggestionProvider<CommandSourceStack> SUGGEST_perm = (ctx, builder) -> {
        List<String> listperm = new ArrayList<>(APIRegistry.perms.getServerZone().getRootZone().enumRegisteredPermissions());
        return SharedSuggestionProvider.suggest(listperm, builder);
    };
    public static final SuggestionProvider<CommandSourceStack> SUGGEST_GroupPerm = (ctx, builder) -> {
        Zone zone;
        try
        {
            zone = PermissionCommandParser.parseZoneSafe(ctx.getSource(), StringArgumentType.getString(ctx, "zone"));
        }
        catch (IllegalArgumentException e)
        {
            zone = APIRegistry.perms.getServerZone();
        }
        List<String> listclear = new ArrayList<>(zone.getGroupPermissions(StringArgumentType.getString(ctx, "group")).keySet());
        return SharedSuggestionProvider.suggest(listclear, builder);
    };
    public static final SuggestionProvider<CommandSourceStack> SUGGEST_GlobalPerm = (ctx, builder) -> {
        Zone zone;
        try
        {
            zone = PermissionCommandParser.parseZoneSafe(ctx.getSource(), StringArgumentType.getString(ctx, "zone"));
        }
        catch (IllegalArgumentException e)
        {
            zone = APIRegistry.perms.getServerZone();
        }
        List<String> listclear = new ArrayList<>(zone.getGroupPermissions(Zone.GROUP_DEFAULT).keySet());
        return SharedSuggestionProvider.suggest(listclear, builder);
    };
    public static final SuggestionProvider<CommandSourceStack> SUGGEST_PlayerPerm = (ctx, builder) -> {
        List<String> listclear = new ArrayList<>();
        Zone zone;
        try
        {
            zone = PermissionCommandParser.parseZoneSafe(ctx.getSource(), StringArgumentType.getString(ctx, "zone"));
        }
        catch (IllegalArgumentException e)
        {
            zone = APIRegistry.perms.getServerZone();
        }
        try
        {
            UserIdent ident = parsePlayer(StringArgumentType.getString(ctx, "player"), false, false);
            listclear.addAll(zone.getPlayerPermissions(ident).keySet());
        }
        catch (FECommandParsingException ignored){}
        return SharedSuggestionProvider.suggest(listclear, builder);
    };

    @Override
    public int execute(CommandContext<CommandSourceStack> ctx, String params) throws CommandSyntaxException
    {
        if (params.equals("help"))
        {
            ChatOutputHandler.chatConfirmation(ctx.getSource(),
                    "/feperm " + StringUtils.join(PermissionCommandParser.parseMainArgs, "|")
                            + ": Displays help for the subcommands");
            return Command.SINGLE_SUCCESS;
        }
        List<String> args = new ArrayList<>(Arrays.asList(params.split("&&")));
        PermissionCommandParser.parseMain(ctx, args);
        return Command.SINGLE_SUCCESS;
    }

}
