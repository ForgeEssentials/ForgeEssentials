package com.forgeessentials.permissions.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.AreaZone;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.api.permissions.WorldZone;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.commons.selections.AreaBase;
import com.forgeessentials.commons.selections.AreaShape;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
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

import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import org.jetbrains.annotations.NotNull;

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
    public @NotNull String getPrimaryAlias()
    {
        return "area";
    }

    @Override
    public String @NotNull [] getDefaultSecondaryAliases()
    {
        return new String[] { "zone" };
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
    public LiteralArgumentBuilder<CommandSourceStack> setExecution()
    {
        return baseBuilder.executes(CommandContext -> execute(CommandContext, "help"))
                .then(Commands.literal("help").executes(CommandContext -> execute(CommandContext, "help")))
                .then(Commands.literal("select")
                        .then(Commands.argument("Zone", StringArgumentType.string()).suggests(SUGGEST_WORLDZONES)
                                .executes(context -> execute(context, "select"))))
                .then(Commands.literal("list")
                        .then(Commands.argument("pageLimit", IntegerArgumentType.integer(1))
                                .executes(context -> execute(context, "list-Zones")))
                        .executes(CommandContext -> execute(CommandContext, "list-empty")))
                .then(Commands.literal("info")
                        .then(Commands.argument("Zone", StringArgumentType.string()).suggests(SUGGEST_WORLDZONES)
                                .executes(context -> execute(context, "info"))))
                .then(Commands.literal("define")
                        .then(Commands.argument("Zone", StringArgumentType.string()).suggests(SUGGEST_WORLDZONES)
                                .then(Commands.argument("type", StringArgumentType.string()).suggests(SUGGEST_AREATYPES)
                                        .executes(context -> execute(context, "define")))))
                .then(Commands.literal("redefine")
                        .then(Commands.argument("Zone", StringArgumentType.string()).suggests(SUGGEST_WORLDZONES)
                                .then(Commands.argument("type", StringArgumentType.string()).suggests(SUGGEST_AREATYPES)
                                        .executes(context -> execute(context, "redefine")))))
                .then(Commands.literal("delete")
                        .then(Commands.argument("Zone", StringArgumentType.string()).suggests(SUGGEST_WORLDZONES)
                                .executes(context -> execute(context, "delete"))))
                .then(Commands.literal("message").then(Commands.literal("exit")
                        .then(Commands.argument("Zone", StringArgumentType.string()).suggests(SUGGEST_WORLDZONES)
                                .then(Commands.literal("get").executes(context -> execute(context, "exit-get")))
                                .then(Commands.literal("clear").executes(context -> execute(context, "exit-clear")))
                                .then(Commands.literal("set")
                                        .then(Commands.argument("message", StringArgumentType.greedyString())
                                                .executes(context -> execute(context, "exit-message"))))

                        ))
                        .then(Commands.literal("entry").then(Commands.argument("Zone", StringArgumentType.string())
                                .suggests(SUGGEST_WORLDZONES)
                                .then(Commands.literal("get").executes(context -> execute(context, "entry-get")))
                                .then(Commands.literal("clear").executes(context -> execute(context, "entry-clear")))
                                .then(Commands.literal("set")
                                        .then(Commands.argument("message", StringArgumentType.greedyString())
                                                .executes(context -> execute(context, "entry-message")))))));
    }

    public static final SuggestionProvider<CommandSourceStack> SUGGEST_WORLDZONES = (ctx, builder) -> {
        List<String> availableZones = new ArrayList<>();
        for (Zone z : APIRegistry.perms.getZones())
        {
            if (z instanceof AreaZone)
            {
                availableZones.add(z.getName());
                availableZones.add(Integer.toString(z.getId()));
            }
        }
        return SharedSuggestionProvider.suggest(availableZones, builder);
    };
    public static final SuggestionProvider<CommandSourceStack> SUGGEST_AREATYPES = (ctx, builder) -> {
        List<String> availableTypes = new ArrayList<>(Arrays.asList(AreaShape.valueNames()));
        return SharedSuggestionProvider.suggest(availableTypes, builder);
    };

    @Override
    public int processCommandPlayer(CommandContext<CommandSourceStack> ctx, String params) throws CommandSyntaxException
    {
        if (params.equals("help"))
        {
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "/zone list [page]: Lists all zones");
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "/zone select <zone>: Selects a zone");
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "/zone info <zone>: Zone information");
            ChatOutputHandler.chatConfirmation(ctx.getSource(),
                    "/zone define|redefine <zone-name>: define or redefine a zone.");
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "/zone delete <zone-id>: Delete a zone.");
            ChatOutputHandler.chatConfirmation(ctx.getSource(),
                    "/zone message entry|exit <zone-id> <get|clear|set <message>>: Set the zone entry/exit message.");
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
            ChatOutputHandler.chatError(ctx.getSource(), FEPermissions.MSG_UNKNOWN_SUBCOMMAND, Arrays.toString(arg));
            return Command.SINGLE_SUCCESS;
        }
        return Command.SINGLE_SUCCESS;
    }

    public AreaZone getAreaZone(WorldZone worldZone, String arg)
    {
        try
        {
            Zone z = APIRegistry.perms.getZoneById(arg);
            if (z instanceof AreaZone)
                return (AreaZone) z;
        }
        catch (NumberFormatException e)
        {
            /* none */
        }
        return worldZone.getAreaZone(arg);
    }

    public void parseList(CommandContext<CommandSourceStack> ctx, String params) throws CommandRuntimeException
    {
        String[] arg = params.split("-");
        final int PAGE_SIZE = 12;
        int limit = 1;
        if (arg[1].equals("Zones"))
        {
            limit = IntegerArgumentType.getInteger(ctx, "pageLimit");
        }
        ChatOutputHandler.chatConfirmation(ctx.getSource(), "List of areas (page #" + limit + "):");
        limit *= PAGE_SIZE;

        WorldZone worldZone = APIRegistry.perms.getServerZone()
                .getWorldZone(getServerPlayer(ctx.getSource()).getLevel());
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
                            ChatOutputHandler.chatConfirmation(ctx.getSource(),
                                    "#" + areaZone.getId() + ": " + areaZone.toString());
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
            if (worldZone.getAreaZones().isEmpty())
            {
                ChatOutputHandler.chatNotification(ctx.getSource(), "No areazones found");
            }
            for (AreaZone areaZone : worldZone.getAreaZones())
            {
                if (areaZone.isHidden())
                    continue;
                if (limit >= 0)
                {
                    if (limit <= PAGE_SIZE)
                        ChatOutputHandler.chatConfirmation(ctx.getSource(),
                                "#" + areaZone.getId() + ": " + areaZone.toString());
                    limit--;
                }
                else
                {
                    break;
                }
            }
        }
    }

    public void parseDefine(CommandContext<CommandSourceStack> ctx, boolean redefine, String params)
            throws CommandRuntimeException
    {

        String areaName = StringArgumentType.getString(ctx, "Zone");

        WorldZone worldZone = APIRegistry.perms.getServerZone()
                .getWorldZone(getServerPlayer(ctx.getSource()).getLevel());
        AreaZone area = getAreaZone(worldZone, areaName);
        if (!redefine && area != null)
        {
            ChatOutputHandler.chatError(ctx.getSource(), "Area %s already exists!", areaName);
            return;
        }
        if (redefine && area == null)
        {
            ChatOutputHandler.chatError(ctx.getSource(), "Area %s does not exist!", areaName);
            return;
        }

        AreaShape shape = AreaShape.getByName(StringArgumentType.getString(ctx, "type"));
        if (shape == null)
            shape = AreaShape.BOX;

        AreaBase selection = SelectionHandler.getSelection(getServerPlayer(ctx.getSource()));
        if (selection == null)
        {
            ChatOutputHandler.chatError(ctx.getSource(), "No selection available. Please select a region first.");
            return;
        }

        if (redefine && area != null)
        {
            area.setArea(selection);
            if (shape != null)
                area.setShape(shape);
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "Area %s has been redefined.", areaName);
        }
        else
        {
            try
            {
                area = new AreaZone(worldZone, areaName, selection);
                if (shape != null)
                    area.setShape(shape);
                ChatOutputHandler.chatConfirmation(ctx.getSource(), "Area %s has been defined.", areaName);
            }
            catch (EventCancelledException e)
            {
                ChatOutputHandler.chatError(ctx.getSource(), "Defining area %s has been cancelled.", areaName);
                return;
            }
        }
    }

    public void parseDelete(CommandContext<CommandSourceStack> ctx, String params) throws CommandRuntimeException
    {
        String areaName = StringArgumentType.getString(ctx, "Zone");

        WorldZone worldZone = APIRegistry.perms.getServerZone()
                .getWorldZone(getServerPlayer(ctx.getSource()).getLevel());
        AreaZone areaZone = getAreaZone(worldZone, areaName);
        if (areaZone == null)
        {
            ChatOutputHandler.chatError(ctx.getSource(), "Area %s does not exist!", areaName);
            return;
        }
        areaZone.getWorldZone().removeAreaZone(areaZone);
        ChatOutputHandler.chatConfirmation(ctx.getSource(), "Area %s has been deleted.", areaZone.getName());
    }

    public void parseSelect(CommandContext<CommandSourceStack> ctx, String params) throws CommandRuntimeException
    {

        String areaName = StringArgumentType.getString(ctx, "Zone");

        WorldZone worldZone = APIRegistry.perms.getServerZone()
                .getWorldZone(getServerPlayer(ctx.getSource()).getLevel());
        AreaZone areaZone = getAreaZone(worldZone, areaName);
        if (areaZone == null)
        {
            ChatOutputHandler.chatError(ctx.getSource(), "Area %s does not exist!", areaName);
            return;
        }

        AreaBase area = areaZone.getArea();
        SelectionHandler.select(getServerPlayer(ctx.getSource()), worldZone.getDimensionID(), area);
        ChatOutputHandler.chatConfirmation(ctx.getSource(), "Area %s has been selected.", areaName);
    }

    public void parseInfo(CommandContext<CommandSourceStack> ctx, String params) throws CommandRuntimeException
    {
        String areaName = StringArgumentType.getString(ctx, "Zone");

        WorldZone worldZone = APIRegistry.perms.getServerZone()
                .getWorldZone(getServerPlayer(ctx.getSource()).getLevel());
        AreaZone areaZone = getAreaZone(worldZone, areaName);
        if (areaZone == null)
        {
            ChatOutputHandler.chatError(ctx.getSource(), "Area %s does not exist!", areaName);
            return;
        }
        AreaBase area = areaZone.getArea();

        ChatOutputHandler.chatConfirmation(ctx.getSource(), "Area %s", areaZone.getName());
        ChatOutputHandler.chatNotification(ctx.getSource(), "  start = " + area.getLowPoint().toString());
        ChatOutputHandler.chatNotification(ctx.getSource(), "  end   = " + area.getHighPoint().toString());
    }

    public void parseEntryExitMessage(CommandContext<CommandSourceStack> ctx, boolean isEntry, String params)
            throws CommandRuntimeException
    {
        String[] arg = params.toString().split("-");
        String areaName = StringArgumentType.getString(ctx, "Zone");

        WorldZone worldZone = APIRegistry.perms.getServerZone()
                .getWorldZone(getServerPlayer(ctx.getSource()).getLevel());
        AreaZone areaZone = getAreaZone(worldZone, areaName);
        if (areaZone == null)
        {
            ChatOutputHandler.chatError(ctx.getSource(), "Area %s does not exist!", areaName);
            return;
        }

        if (arg[1].equals("get"))
        {
            ChatOutputHandler.chatConfirmation(ctx.getSource(),
                    Translator.format((isEntry ? "Entry" : "Exit") + " message for area %s:", areaZone.getName()));
            ChatOutputHandler.chatConfirmation(ctx.getSource(), areaZone.getGroupPermission(Zone.GROUP_DEFAULT,
                    isEntry ? FEPermissions.ZONE_ENTRY_MESSAGE : FEPermissions.ZONE_EXIT_MESSAGE));
            return;
        }

        String msg = "";
        if (arg[1].equals("clear"))
        {
            msg = null;
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "Cleared message for area %s", areaZone.getName());
        }
        else if (arg[1].equals("message"))
        {
            msg = StringArgumentType.getString(ctx, "message");
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "Set message for area %s:", areaZone.getName());
            ChatOutputHandler.chatConfirmation(ctx.getSource(), msg);
        }

        areaZone.setGroupPermissionProperty(Zone.GROUP_DEFAULT,
                isEntry ? FEPermissions.ZONE_ENTRY_MESSAGE : FEPermissions.ZONE_EXIT_MESSAGE, msg);
    }

}
