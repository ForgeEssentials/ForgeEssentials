package com.forgeessentials.permissions.commands;

import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import java.util.ArrayList;
import java.util.List;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.AreaZone;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.api.permissions.WorldZone;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.commons.selections.AreaBase;
import com.forgeessentials.commons.selections.AreaShape;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.events.EventCancelledException;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.selections.SelectionHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;

public class CommandZone extends ForgeEssentialsCommandBuilder
{

    public CommandZone(boolean enabled)
    {
        super(enabled);
    }

    public static final String PERM_NODE = "fe.perm.zone";
    public static final String PERM_ALL = PERM_NODE + Zone.ALL_PERMS;
    public static final String PERM_LIST = PERM_NODE + ".list";
    public static final String PERM_INFO = PERM_NODE + ".info";
    public static final String PERM_DEFINE = PERM_NODE + ".define";
    public static final String PERM_DELETE = PERM_NODE + ".delete";
    public static final String PERM_SETTINGS = PERM_NODE + ".settings";

    @Override
    public String getPrimaryAlias()
    {
        return "area";
    }

    @Override
    public String[] getDefaultSecondaryAliases()
    {
        return new String[] { "zone" };
    }

    @Override
    public String getPermissionNode()
    {
        return PERM_NODE;
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.OP;
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        return baseBuilder
                .then(Commands.literal("help")
                        .executes(CommandContext -> execute(CommandContext, "help")
                                )
                        )
                .then(Commands.literal("select")
                        .then(Commands.argument("zones", StringArgumentType.string())
                                .suggests(SUGGEST_WORLDZONES)
                                .executes(context -> execute(context, "select")
                                        )
                                )
                        )
                .then(Commands.literal("list")
                        .then(Commands.argument("pageLimit", IntegerArgumentType.integer(1))
                                .executes(context -> execute(context, "list-Zones")
                                        )
                                )
                        .executes(CommandContext -> execute(CommandContext, "list-empty")
                                )
                        )
                .then(Commands.literal("info")
                        .then(Commands.argument("zones", StringArgumentType.string())
                                .suggests(SUGGEST_WORLDZONES)
                                .executes(context -> execute(context, "info")
                                        )
                                )
                        )
                .then(Commands.literal("define")
                        .then(Commands.argument("zones", StringArgumentType.string())
                                .suggests(SUGGEST_WORLDZONES)
                                .then(Commands.argument("type", StringArgumentType.string())
                                        .suggests(SUGGEST_AREATYPES)
                                        .executes(context -> execute(context, "define")
                                                )
                                        )
                                )
                        )
                .then(Commands.literal("redefine")
                        .then(Commands.argument("zones", StringArgumentType.string())
                                .suggests(SUGGEST_WORLDZONES)
                                .then(Commands.argument("type", StringArgumentType.string())
                                        .suggests(SUGGEST_AREATYPES)
                                        .executes(context -> execute(context, "redefine")
                                                )
                                        )
                                )
                        )
                .then(Commands.literal("delete")
                        .then(Commands.argument("zones", StringArgumentType.string())
                                .suggests(SUGGEST_WORLDZONES)
                                .executes(context -> execute(context, "delete")
                                        )
                                )
                        )
                .then(Commands.literal("exit")
                        .then(Commands.argument("zones", StringArgumentType.string())
                                .suggests(SUGGEST_WORLDZONES)
                                .executes(context -> execute(context, "exit-empty")
                                        )
                                .then(Commands.literal("clear")
                                        .executes(context -> execute(context, "exit-clear")
                                                )
                                        )
                                .then(Commands.argument("message", StringArgumentType.greedyString())
                                        .executes(context -> execute(context, "exit-message")
                                                )
                                        )
                                )
                        )
                .then(Commands.literal("entry")
                        .then(Commands.argument("zones", StringArgumentType.string())
                                .suggests(SUGGEST_WORLDZONES)
                                .executes(context -> execute(context, "entry-empty")
                                        )
                                .then(Commands.literal("clear")
                                        .executes(context -> execute(context, "entry-clear")
                                                )
                                        )
                                .then(Commands.argument("message", StringArgumentType.greedyString())
                                        .executes(context -> execute(context, "entry-message")
                                                )
                                        )
                                )
                        );
    }

    public static final SuggestionProvider<CommandSource> SUGGEST_WORLDZONES = (ctx, builder) -> {
        List<String> availableZones = new ArrayList<>();
        for (Zone z : APIRegistry.perms.getZones())
        {
            if (z instanceof AreaZone)
            {
                availableZones.add(z.getName());
                availableZones.add(Integer.toString(z.getId()));
            }
        }
        return ISuggestionProvider.suggest(availableZones, builder);
     };
     public static final SuggestionProvider<CommandSource> SUGGEST_AREATYPES = (ctx, builder) -> {
         List<String> availableTypes = new ArrayList<>();
         for (String name : AreaShape.valueNames())
         {
             availableTypes.add(name);
         }
         return ISuggestionProvider.suggest(availableTypes, builder);
      };

    @Override
    public int processCommandPlayer(CommandContext<CommandSource> ctx, String params) throws CommandSyntaxException
    {
        if (params.equals("help"))
        {
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "/zone list [page]: Lists all zones");
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "/zone select <zone>: Selects a zone");
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "/zone info <zone>|here: Zone information");
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "/zone define|redefine <zone-name>: define or redefine a zone.");
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "/zone delete <zone-id>: Delete a zone.");
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "/zone entry|exit <zone-id> <message|clear>: Set the zone entry/exit message.");
            return Command.SINGLE_SUCCESS;
        }

        String[] arg = params.split("-");
        switch (arg[0])
        {
        case "select":
            parseSelect(ctx, params);
            break;
        case "info":
            parseInfo(ctx, params);
            break;
        case "list":
            parseList(ctx, params);
            break;
        case "define":
            parseDefine(ctx, false, params);
            break;
        case "redefine":
            parseDefine(ctx, true, params);
            break;
        case "delete":
            parseDelete(ctx, params);
            break;
        case "entry":
            parseEntryExitMessage(ctx, true, params);
            break;
        case "exit":
            parseEntryExitMessage(ctx, false, params);
            break;
        default:
            throw new TranslatedCommandException(FEPermissions.MSG_UNKNOWN_SUBCOMMAND, arg.toString());
        }
        return Command.SINGLE_SUCCESS;
    }

    public static AreaZone getAreaZone(WorldZone worldZone, String arg)
    {
        try
        {
            Zone z = APIRegistry.perms.getZoneById(arg);
            if (z != null && z instanceof AreaZone)
                return (AreaZone) z;
        }
        catch (NumberFormatException e)
        {
            /* none */
        }
        return worldZone.getAreaZone(arg);
    }

    public static void parseList(CommandContext<CommandSource> ctx, String params) throws CommandException
    {
    	if(hasPermission(ctx.getSource(), PERM_LIST)) {
    		ChatOutputHandler.chatError(ctx.getSource(), FEPermissions.MSG_NO_COMMAND_PERM);
    		return;
    	}
        String[] arg = params.split("-");
        final int PAGE_SIZE = 12;
        int limit = 1;
        if (arg[1]=="Zones")
        {
            limit = IntegerArgumentType.getInteger(ctx, "pageLimit");
        }
        ChatOutputHandler.chatConfirmation(ctx.getSource(), "List of areas (page #" + limit + "):");
        limit *= PAGE_SIZE;

        WorldZone worldZone = APIRegistry.perms.getServerZone().getWorldZone(getServerPlayer(ctx.getSource()).level);
        
        if (worldZone == null)
        {
            for (WorldZone wz : APIRegistry.perms.getServerZone().getWorldZones().values())
            {
                for (AreaZone areaZone : wz.getAreaZones())
                {
                    if (areaZone.isHidden())
                        continue;
                    if (limit >= 0)
                    {
                        if (limit <= PAGE_SIZE)
                            ChatOutputHandler.chatConfirmation(ctx.getSource(), "#" + areaZone.getId() + ": " + areaZone.toString());
                        limit--;
                    }
                    else
                    {
                        break;
                    }
                }
            }
        }
        else
        {
            for (AreaZone areaZone : worldZone.getAreaZones())
            {
                if (areaZone.isHidden())
                    continue;
                if (limit >= 0)
                {
                    if (limit <= PAGE_SIZE)
                        ChatOutputHandler.chatConfirmation(ctx.getSource(), "#" + areaZone.getId() + ": " + areaZone.toString());
                    limit--;
                }
                else
                {
                    break;
                }
            }
        }
    }

    public static void parseDefine(CommandContext<CommandSource> ctx, boolean redefine, String params) throws CommandException
    {
    	if(hasPermission(ctx.getSource(), PERM_DEFINE)) {
    		ChatOutputHandler.chatError(ctx.getSource(), FEPermissions.MSG_NO_COMMAND_PERM);
    		return;
    	}

        String areaName = StringArgumentType.getString(ctx, "Zone");

        WorldZone worldZone = APIRegistry.perms.getServerZone().getWorldZone(getServerPlayer(ctx.getSource()).level);
        AreaZone area = getAreaZone(worldZone, areaName);
        if (!redefine && area != null)
            throw new TranslatedCommandException(String.format("Area \"%s\" already exists!", areaName));
        if (redefine && area == null)
            throw new TranslatedCommandException(String.format("Area \"%s\" does not exist!", areaName));

        AreaShape shape = AreaShape.getByName(StringArgumentType.getString(ctx, "type"));
        if (shape == null)
            shape = AreaShape.BOX;

        AreaBase selection = SelectionHandler.getSelection(getServerPlayer(ctx.getSource()));
        if (selection == null)
            throw new TranslatedCommandException("No selection available. Please select a region first.");

        //arguments.context = new AreaContext(getServerPlayer(ctx.getSource()), selection.toAxisAlignedBB()); what this do? it isn't being called from commandparcerargs
        if(hasPermission(ctx.getSource(), PERM_DEFINE)) {
    		ChatOutputHandler.chatError(ctx.getSource(), FEPermissions.MSG_NO_COMMAND_PERM);
    		return;
    	}

        if (redefine && area != null)
        {
            area.setArea(selection);
            if (shape != null)
                area.setShape(shape);
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "Area \"%s\" has been redefined.", areaName);
        }
        else
        {
            try
            {
                area = new AreaZone(worldZone, areaName, selection);
                if (shape != null)
                    area.setShape(shape);
                ChatOutputHandler.chatConfirmation(ctx.getSource(), "Area \"%s\" has been defined.", areaName);
            }
            catch (EventCancelledException e)
            {
                throw new TranslatedCommandException("Defining area \"%s\" has been cancelled.", areaName);
            }
        }
    }

    public static void parseDelete(CommandContext<CommandSource> ctx, String params) throws CommandException
    {
    	if(hasPermission(ctx.getSource(), PERM_DELETE)) {
    		ChatOutputHandler.chatError(ctx.getSource(), FEPermissions.MSG_NO_COMMAND_PERM);
    		return;
    	}

        String areaName = StringArgumentType.getString(ctx, "Zone");

        WorldZone worldZone = APIRegistry.perms.getServerZone().getWorldZone(getServerPlayer(ctx.getSource()).level);
        AreaZone areaZone = getAreaZone(worldZone, areaName);
        if (areaZone == null)
            throw new TranslatedCommandException("Area \"%s\" has does not exist!", areaName);
        areaZone.getWorldZone().removeAreaZone(areaZone);
        ChatOutputHandler.chatConfirmation(ctx.getSource(), "Area \"%s\" has been deleted.", areaZone.getName());
    }

    public static void parseSelect(CommandContext<CommandSource> ctx, String params) throws CommandException
    {
    	if(hasPermission(ctx.getSource(), PERM_INFO)) {
    		ChatOutputHandler.chatError(ctx.getSource(), FEPermissions.MSG_NO_COMMAND_PERM);
    		return;
    	}

        String areaName = StringArgumentType.getString(ctx, "Zone");

        WorldZone worldZone = APIRegistry.perms.getServerZone().getWorldZone(getServerPlayer(ctx.getSource()).level);
        AreaZone areaZone = getAreaZone(worldZone, areaName);
        if (areaZone == null)
            throw new TranslatedCommandException("Area \"%s\" has does not exist!", areaName);

        AreaBase area = areaZone.getArea();
        SelectionHandler.select(getServerPlayer(ctx.getSource()), worldZone.getDimensionID(), area);
        ChatOutputHandler.chatConfirmation(ctx.getSource(), "Area \"%s\" has been selected.", areaName);
    }

    public static void parseInfo(CommandContext<CommandSource> ctx, String params) throws CommandException
    {
    	if(hasPermission(ctx.getSource(), PERM_INFO)) {
    		ChatOutputHandler.chatError(ctx.getSource(), FEPermissions.MSG_NO_COMMAND_PERM);
    		return;
    	}

        String areaName = StringArgumentType.getString(ctx, "Zone");

        WorldZone worldZone = APIRegistry.perms.getServerZone().getWorldZone(getServerPlayer(ctx.getSource()).level);
        AreaZone areaZone = getAreaZone(worldZone, areaName);
        if (areaZone == null)
            throw new TranslatedCommandException("Area \"%s\" has does not exist!", areaName);
        AreaBase area = areaZone.getArea();

        ChatOutputHandler.chatConfirmation(ctx.getSource(), "Area \"%s\"", areaZone.getName());
        ChatOutputHandler.chatNotification(ctx.getSource(), "  start = " + area.getLowPoint().toString());
        ChatOutputHandler.chatNotification(ctx.getSource(), "  end   = " + area.getHighPoint().toString());
    }

    public static void parseEntryExitMessage(CommandContext<CommandSource> ctx, boolean isEntry, String params) throws CommandException
    {
    	if(hasPermission(ctx.getSource(), PERM_SETTINGS)) {
    		ChatOutputHandler.chatError(ctx.getSource(), FEPermissions.MSG_NO_COMMAND_PERM);
    		return;
    	}
        String[] arg = params.toString().split("-");
        String areaName = StringArgumentType.getString(ctx, "Zone");

        WorldZone worldZone = APIRegistry.perms.getServerZone().getWorldZone(getServerPlayer(ctx.getSource()).level);
        AreaZone areaZone = getAreaZone(worldZone, areaName);
        if (areaZone == null)
            throw new TranslatedCommandException("Area \"%s\" has does not exist!", areaName);

        if (arg[1]=="empty")
        {
            ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format((isEntry ? "Entry" : "Exit") + " message for area %s:", areaZone.getName()));
            ChatOutputHandler.chatConfirmation(ctx.getSource(), areaZone.getGroupPermission(Zone.GROUP_DEFAULT, isEntry ? FEPermissions.ZONE_ENTRY_MESSAGE : FEPermissions.ZONE_EXIT_MESSAGE));
            return;
        }

        String msg = "";
        if(arg[1].equalsIgnoreCase("clear")) {
            msg = null;
        }else {
            msg = StringArgumentType.getString(ctx, "message");
        }

        areaZone.setGroupPermissionProperty(Zone.GROUP_DEFAULT, isEntry ? FEPermissions.ZONE_ENTRY_MESSAGE : FEPermissions.ZONE_EXIT_MESSAGE, msg);
    }

}
