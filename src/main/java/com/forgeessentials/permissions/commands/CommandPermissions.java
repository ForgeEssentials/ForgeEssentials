package com.forgeessentials.permissions.commands;


import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.playerlogger.FilterConfig.ActionEnum;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
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
                                .executes(CommandContext -> execute(CommandContext, "list")
                                        )
                                )
                        .then(Commands.argument("group", StringArgumentType.greedyString())
                                .suggests(SUGGEST_group)
                                .executes(CommandContext -> execute(CommandContext, "list-"+StringArgumentType.getString(CommandContext, "type"))
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
                .then(Commands.literal("reload")
                        .executes(CommandContext -> execute(CommandContext, "help")
                                )
                        )
                .then(Commands.literal("save")
                        .executes(CommandContext -> execute(CommandContext, "save")
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
                .then(Commands.literal("debug")
                        .executes(CommandContext -> execute(CommandContext, "help")
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
