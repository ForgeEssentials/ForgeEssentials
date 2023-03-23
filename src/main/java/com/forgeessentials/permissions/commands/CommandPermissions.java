package com.forgeessentials.permissions.commands;


import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.arguments.DimensionArgument;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.playerlogger.FilterConfig.ActionEnum;
import com.forgeessentials.util.CommandUtils;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;

public class CommandPermissions extends ForgeEssentialsCommandBuilder
{
    public CommandPermissions(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public final String getPrimaryAlias()
    {
        return "perm";
    }

    @Override
    public String[] getDefaultSecondaryAliases()
    {
        return new String[] { "fep", "p" };
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public String getPermissionNode()
    {
        return PermissionCommandParser.PERM;
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.ALL;
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        return builder
                .then(Commands.literal("help")
                        .executes(CommandContext -> execute(CommandContext, "help")
                                )
                        )
                .then(Commands.literal("user")
                        .executes(CommandContext -> execute(CommandContext, "user")
                                )
                        .then(Commands.literal("help")
                                .executes(CommandContext -> execute(CommandContext, "user")
                                        )
                                )
                        .then(Commands.argument("player", StringArgumentType.word())
                                .executes(CommandContext -> execute(CommandContext, "user-"+StringArgumentType.getString(CommandContext, "player"))
                                        )
                                )
                        )
                .then(Commands.literal("group")
                        .executes(CommandContext -> execute(CommandContext, "group")
                                )
                        .then(Commands.literal("help")
                                .executes(CommandContext -> execute(CommandContext, "group")
                                        )
                                )
                        //, "clear", "value", "true", "false", "spawn", "prefix", "suffix", "perms",
                        //"priority", "parent", "include", "denydefault", };
                        .then(Commands.argument("group", StringArgumentType.greedyString())
                                .suggests(SUGGEST_group)
                                .then(Commands.literal("create")//p
                                        .executes(CommandContext -> execute(CommandContext, "group-"+StringArgumentType.getString(CommandContext, "group")+"-create")
                                                )
                                        )
                                .then(Commands.literal("zone")
                                        .executes(CommandContext -> execute(CommandContext, "group-"+StringArgumentType.getString(CommandContext, "group")+"-zone")
                                                )
                                        )
                                .then(Commands.literal("users")//p
                                        .executes(CommandContext -> execute(CommandContext, "group-"+StringArgumentType.getString(CommandContext, "group")+"-users")
                                                )
                                        )
                                .then(Commands.literal("allow")//p
                                        .then(Commands.argument("perm", StringArgumentType.greedyString())
                                                .suggests(SUGGEST_perm)
                                                .executes(CommandContext -> execute(CommandContext, "group-"+StringArgumentType.getString(CommandContext, "group")+"-allow-"+StringArgumentType.getString(CommandContext, "perm"))
                                                        )
                                                )
                                        )
                                .then(Commands.literal("deny")//p
                                        .then(Commands.argument("perm", StringArgumentType.greedyString())
                                                .suggests(SUGGEST_perm)
                                                .executes(CommandContext -> execute(CommandContext, "group-"+StringArgumentType.getString(CommandContext, "group")+"-deny-"+StringArgumentType.getString(CommandContext, "perm"))
                                                        )
                                                )
                                        )
                                .then(Commands.literal("clear")//p
                                        .then(Commands.argument("perm", StringArgumentType.greedyString())
                                                .suggests(SUGGEST_Cperm)
                                                .executes(CommandContext -> execute(CommandContext, "group-"+StringArgumentType.getString(CommandContext, "group")+"-clear-"+StringArgumentType.getString(CommandContext, "perm"))
                                                        )
                                                )
                                        )
                                .then(Commands.literal("value")//p
                                        .then(Commands.argument("perm", StringArgumentType.greedyString())
                                                .suggests(SUGGEST_perm)
                                                .executes(CommandContext -> execute(CommandContext, "group-"+StringArgumentType.getString(CommandContext, "group")+"-value-"+StringArgumentType.getString(CommandContext, "perm"))
                                                        )
                                                )
                                        )
                                .then(Commands.literal("spawn")//p
                                        .then(Commands.literal("help")
                                                .executes(CommandContext -> execute(CommandContext, "group-"+StringArgumentType.getString(CommandContext, "group")+"-spawn")
                                                        )
                                                )
                                        .then(Commands.literal("bed")
                                                .then(Commands.literal("enable")
                                                        .executes(CommandContext -> execute(CommandContext, "group-"+StringArgumentType.getString(CommandContext, "group")+"-spawn-bed-enable")
                                                                )
                                                        )
                                                .then(Commands.literal("disable")
                                                        .executes(CommandContext -> execute(CommandContext, "group-"+StringArgumentType.getString(CommandContext, "group")+"-spawn-bed-disable")
                                                                )
                                                        )
                                                )
                                        .then(Commands.literal("here")
                                                .executes(CommandContext -> execute(CommandContext, "group-"+StringArgumentType.getString(CommandContext, "group")+"-spawn-here")
                                                        )
                                                )
                                        .then(Commands.literal("clear")
                                                .executes(CommandContext -> execute(CommandContext, "group-"+StringArgumentType.getString(CommandContext, "group")+"-spawn-clear")
                                                        )
                                                )
                                        .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                                .then(Commands.argument("dim", DimensionArgument.dimension())
                                                        .executes(CommandContext -> execute(CommandContext, "group-"+StringArgumentType.getString(CommandContext, "group")+"-spawn-"+Integer.toString(BlockPosArgument.getLoadedBlockPos(CommandContext, "pos").getX())+"-"+Integer.toString(BlockPosArgument.getLoadedBlockPos(CommandContext, "pos").getY())+"-"+Integer.toString(BlockPosArgument.getLoadedBlockPos(CommandContext, "pos").getZ())+"-"+DimensionArgument.getDimension(CommandContext, "dim").dimension().location().toString())
                                                                )
                                                        )
                                                )
                                        )
                                .then(Commands.literal("prefix")//p
                                        .then(Commands.argument("prefix", StringArgumentType.greedyString())
                                                .executes(CommandContext -> execute(CommandContext, "group-"+StringArgumentType.getString(CommandContext, "group")+"-prefix-"+StringArgumentType.getString(CommandContext, "prefix"))
                                                        )
                                                )
                                        )
                                .then(Commands.literal("suffix")//p
                                        .then(Commands.argument("suffix", StringArgumentType.greedyString())
                                                .executes(CommandContext -> execute(CommandContext, "group-"+StringArgumentType.getString(CommandContext, "group")+"-suffix-"+StringArgumentType.getString(CommandContext, "suffix"))
                                                        )
                                                )
                                        )
                                .then(Commands.literal("perms")//p
                                        .executes(CommandContext -> execute(CommandContext, "group-"+StringArgumentType.getString(CommandContext, "group")+"-perms")
                                                )
                                        )
                                .then(Commands.literal("priority")//p
                                        .then(Commands.literal("help")
                                                .executes(CommandContext -> execute(CommandContext, "group-"+StringArgumentType.getString(CommandContext, "group")+"-priority")
                                                        )
                                                )
                                        .then(Commands.argument("priority", IntegerArgumentType.integer())
                                                .executes(CommandContext -> execute(CommandContext, "group-"+StringArgumentType.getString(CommandContext, "group")+"-priority-"+Integer.toString(IntegerArgumentType.getInteger(CommandContext, "priority")))
                                                        )
                                                )
                                        )
                                .then(Commands.literal("parent")//p
                                        .then(Commands.literal("help")
                                                .executes(CommandContext -> execute(CommandContext, "group-"+StringArgumentType.getString(CommandContext, "group")+"-parent")
                                                        )
                                                )
                                        .then(Commands.literal("add")
                                                .then(Commands.argument("group1", StringArgumentType.greedyString())
                                                        .suggests(SUGGEST_group)
                                                        .executes(CommandContext -> execute(CommandContext, "group-"+StringArgumentType.getString(CommandContext, "group")+"-parent-add-"+StringArgumentType.getString(CommandContext, "group1"))
                                                                )
                                                        )
                                                )
                                        .then(Commands.literal("remove")
                                                .then(Commands.argument("group1", StringArgumentType.greedyString())
                                                        .suggests(SUGGEST_group)
                                                        .executes(CommandContext -> execute(CommandContext, "group-"+StringArgumentType.getString(CommandContext, "group")+"-parent-remove-"+StringArgumentType.getString(CommandContext, "group1"))
                                                                )
                                                        )
                                                )
                                        .then(Commands.literal("clear")
                                                .executes(CommandContext -> execute(CommandContext, "group-"+StringArgumentType.getString(CommandContext, "group")+"-parent-clear")
                                                        )
                                                )
                                        .executes(CommandContext -> execute(CommandContext, "group-"+StringArgumentType.getString(CommandContext, "group")+"-parent")
                                                )
                                        )
                                .then(Commands.literal("include")//p
                                        .then(Commands.literal("help")
                                                .executes(CommandContext -> execute(CommandContext, "group-"+StringArgumentType.getString(CommandContext, "group")+"-include")
                                                        )
                                                )
                                        .then(Commands.literal("add")
                                                .then(Commands.argument("group1", StringArgumentType.greedyString())
                                                        .suggests(SUGGEST_group)
                                                        .executes(CommandContext -> execute(CommandContext, "group-"+StringArgumentType.getString(CommandContext, "group")+"-include-add-"+StringArgumentType.getString(CommandContext, "group1"))
                                                                )
                                                        )
                                                )
                                        .then(Commands.literal("remove")
                                                .then(Commands.argument("group1", StringArgumentType.greedyString())
                                                        .suggests(SUGGEST_group)
                                                        .executes(CommandContext -> execute(CommandContext, "group-"+StringArgumentType.getString(CommandContext, "group")+"-include-remove-"+StringArgumentType.getString(CommandContext, "group1"))
                                                                )
                                                        )
                                                )
                                        .then(Commands.literal("clear")
                                                .executes(CommandContext -> execute(CommandContext, "group-"+StringArgumentType.getString(CommandContext, "group")+"-include-clear")
                                                        )
                                                )
                                        .executes(CommandContext -> execute(CommandContext, "group-"+StringArgumentType.getString(CommandContext, "group")+"-include")
                                                )
                                        )
                                .then(Commands.literal("denydefault")//p
                                        .executes(CommandContext -> execute(CommandContext, "group-"+StringArgumentType.getString(CommandContext, "group")+"-denydefault")
                                                )
                                        )
                                )
                        )
                .then(Commands.literal("global")
                        .executes(CommandContext -> execute(CommandContext, "help")
                                )
                        )
                .then(Commands.literal("list")
                        .executes(CommandContext -> execute(CommandContext, "list")
                                )
                        .then(Commands.literal("help")
                                .executes(CommandContext -> execute(CommandContext, "list")
                                        )
                                )
                        .then(Commands.argument("type", StringArgumentType.greedyString())
                                .suggests(SUGGEST_ListArgs)
                                .executes(CommandContext -> execute(CommandContext, "list-"+StringArgumentType.getString(CommandContext, "type"))
                                        )
                                )
                        )
                .then(Commands.literal("test")
                        .executes(CommandContext -> execute(CommandContext, "help")
                                )
                        )
                .then(Commands.literal("reload")//p
                        .executes(CommandContext -> execute(CommandContext, "reload")
                                )
                        )
                .then(Commands.literal("save")//p
                        .then(Commands.literal("help")
                                .executes(CommandContext -> execute(CommandContext, "save")
                                        )
                                )
                        .then(Commands.literal("disable")
                                .executes(CommandContext -> execute(CommandContext, "save-disable")
                                        )
                                )
                        .then(Commands.literal("enable")
                                .executes(CommandContext -> execute(CommandContext, "save-enable")
                                        )
                                )
                        .then(Commands.literal("flatfile")
                                .executes(CommandContext -> execute(CommandContext, "save-flatfile")
                                        )
                                )
                        .then(Commands.literal("singlejson")
                                .executes(CommandContext -> execute(CommandContext, "save-singlejson")
                                        )
                                )
                        .then(Commands.literal("json")
                                .executes(CommandContext -> execute(CommandContext, "savejson")
                                        )
                                )
                        )
                .then(Commands.literal("debug")//p
                        .executes(CommandContext -> execute(CommandContext, "debug")
                                )
                        );
    }


    public static final SuggestionProvider<CommandSource> SUGGEST_ListArgs = (ctx, builder) -> {
        List<String> listArgs = new ArrayList<>();
        for (String arg : PermissionCommandParser.parseListArgs)
        {
            listArgs.add(arg);
        }
        return ISuggestionProvider.suggest(listArgs, builder);
        };
    public static final SuggestionProvider<CommandSource> SUGGEST_zones = (ctx, builder) -> {
         List<String> listzones = new ArrayList<>();
         for (Zone z : APIRegistry.perms.getZones())
         {
             listzones.add(z.getName());
         }
         for (String n : APIRegistry.namedWorldHandler.getWorldNames())
         {
             listzones.add(n);
         }
         return ISuggestionProvider.suggest(listzones, builder);
         };
    public static final SuggestionProvider<CommandSource> SUGGEST_group = (ctx, builder) -> {
          List<String> listgroup = new ArrayList<>();
          for (String z : APIRegistry.perms.getServerZone().getGroups())
          {
              listgroup.add(z);
          }
          return ISuggestionProvider.suggest(listgroup, builder);
          };
    public static final SuggestionProvider<CommandSource> SUGGEST_perm = (ctx, builder) -> {
           List<String> listperm = new ArrayList<>();
           for (String z : APIRegistry.perms.getServerZone().getRootZone().enumRegisteredPermissions())
           {
               listperm.add(z);
           }
           return ISuggestionProvider.suggest(listperm, builder);
           };
    public static final SuggestionProvider<CommandSource> SUGGEST_Cperm = (ctx, builder) -> {
               List<String> listclear = new ArrayList<>();
               //for (String z : zone.getGroupPermissions(StringArgumentType.getString(ctx, "group")).keySet())
               //{
               //    listclear.add(z);
               //}
               return ISuggestionProvider.suggest(listclear, builder);
               };
    public static final SuggestionProvider<CommandSource> SUGGEST_parseGroupIncludeArgs = (ctx, builder) -> {
        List<String> listArgs = new ArrayList<>();
        for (String arg : PermissionCommandParser.parseGroupIncludeArgs)
        {
            listArgs.add(arg);
        }
        return ISuggestionProvider.suggest(listArgs, builder);
                   };
    @Override
    public int execute(CommandContext<CommandSource> ctx, Object... params) throws CommandSyntaxException
    {
        if(params.toString().equals("help")) {
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "/feperm " + StringUtils.join(PermissionCommandParser.parseMainArgs, "|") + ": Displays help for the subcommands");
            return Command.SINGLE_SUCCESS;
        }
        List<String> args = Arrays.asList(params.toString().split("-")); 
        PermissionCommandParser.parseMain(ctx, args);
        return Command.SINGLE_SUCCESS;
    }

}
