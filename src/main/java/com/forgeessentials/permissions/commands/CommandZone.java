package com.forgeessentials.permissions.commands;

import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.MessageArgument;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.context.AreaContext;

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
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

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
        return builder
                .then(Commands.literal("help")
                        .executes(CommandContext -> execute(CommandContext, "help")
                                )
                        )
                .then(Commands.literal("list")
                        .then(Commands.argument("password", MessageArgument.message())
                                .executes(CommandContext -> execute(CommandContext, "login")
                                        )
                                )
                        )
                .then(Commands.literal("info")
                        .then(Commands.argument("password", MessageArgument.message())
                                .executes(CommandContext -> execute(CommandContext, "login")
                                        )
                                )
                        )
                .then(Commands.literal("define")
                        .then(Commands.argument("password", MessageArgument.message())
                                .executes(CommandContext -> execute(CommandContext, "login")
                                        )
                                )
                        )
                .then(Commands.literal("redefine")
                        .then(Commands.argument("password", MessageArgument.message())
                                .executes(CommandContext -> execute(CommandContext, "login")
                                        )
                                )
                        )
                .then(Commands.literal("delete")
                        .then(Commands.argument("password", MessageArgument.message())
                                .executes(CommandContext -> execute(CommandContext, "login")
                                        )
                                )
                        )
                .then(Commands.literal("exit")
                        .then(Commands.argument("password", MessageArgument.message())
                                .executes(CommandContext -> execute(CommandContext, "login")
                                        )
                                )
                        )
                .then(Commands.literal("entry")
                        .then(Commands.argument("password", MessageArgument.message())
                                .executes(CommandContext -> execute(CommandContext, "login")
                                        )
                                )
                        );
    }

    @Override
    public int processCommandPlayer(CommandContext<CommandSource> ctx, Object... params) throws CommandSyntaxException
    {
        if (params.toString().equals("help"))
        {
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "/zone list [page]: Lists all zones");
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "/zone info <zone>|here: Zone information");
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "/zone define|redefine <zone-name>: define or redefine a zone.");
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "/zone delete <zone-id>: Delete a zone.");
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "/zone entry|exit <zone-id> <message|clear>: Set the zone entry/exit message.");
            return Command.SINGLE_SUCCESS;
        }

        String[] arg = params.toString().split("-");
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
            throw new TranslatedCommandException(FEPermissions.MSG_UNKNOWN_SUBCOMMAND, params);
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

    public static void parseList(CommandContext<CommandSource> ctx, Object... params) throws CommandException
    {

        checkPermission(ctx.getSource(),PERM_LIST);

        final int PAGE_SIZE = 12;
        int limit = 1;
        if (!arguments.isEmpty())
        {
            try
            {
                limit = Integer.parseInt(arguments.remove());
            }
            catch (NumberFormatException e)
            {
                limit = 1;
            }
        }
        ChatOutputHandler.chatConfirmation(ctx.getSource(), "List of areas (page #" + limit + "):");
        limit *= PAGE_SIZE;

        WorldZone worldZone = arguments.getWorldZone();
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

    public static void parseDefine(CommandContext<CommandSource> ctx, boolean redefine, Object... params) throws CommandException
    {
        checkPermission(ctx.getSource(),PERM_DEFINE);

        tabCompleteArea(arguments);
        String areaName = arguments.remove();

        WorldZone worldZone = arguments.getWorldZone();
        AreaZone area = getAreaZone(worldZone, areaName);
        if (!redefine && area != null)
            throw new TranslatedCommandException(String.format("Area \"%s\" already exists!", areaName));
        if (redefine && area == null)
            throw new TranslatedCommandException(String.format("Area \"%s\" does not exist!", areaName));

        AreaShape shape = null;
        if (!arguments.isEmpty())
        {
            arguments.tabComplete(AreaShape.valueNames());
            shape = AreaShape.getByName(arguments.remove());
            if (shape == null)
                shape = AreaShape.BOX;
        }

        AreaBase selection = SelectionHandler.getSelection(arguments.senderPlayer);
        if (selection == null)
            throw new TranslatedCommandException("No selection available. Please select a region first.");

        if (arguments.hasPlayer())
        {
            arguments.context = new AreaContext(arguments.senderPlayer, selection.toAxisAlignedBB());
            checkPermission(ctx.getSource(),PERM_DEFINE);
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

    public static void parseDelete(CommandContext<CommandSource> ctx, Object... params) throws CommandException
    {
        checkPermission(ctx.getSource(),PERM_DELETE);

        tabCompleteArea(arguments);
        String areaName = arguments.remove();

        WorldZone worldZone = arguments.getWorldZone();
        AreaZone areaZone = getAreaZone(worldZone, areaName);
        if (areaZone == null)
            throw new TranslatedCommandException("Area \"%s\" has does not exist!", areaName);
        areaZone.getWorldZone().removeAreaZone(areaZone);
        ChatOutputHandler.chatConfirmation(ctx.getSource(), "Area \"%s\" has been deleted.", areaZone.getName());
    }

    public static void parseSelect(CommandContext<CommandSource> ctx, Object... params) throws CommandException
    {
        checkPermission(ctx.getSource(),PERM_INFO);

        tabCompleteArea(arguments);
        String areaName = arguments.remove();

        WorldZone worldZone = arguments.getWorldZone();
        AreaZone areaZone = getAreaZone(worldZone, areaName);
        if (areaZone == null)
            throw new TranslatedCommandException("Area \"%s\" has does not exist!", areaName);

        AreaBase area = areaZone.getArea();
        SelectionHandler.select(arguments.senderPlayer, worldZone.getDimensionID(), area);
        ChatOutputHandler.chatConfirmation(ctx.getSource(), "Area \"%s\" has been selected.", areaName);
    }

    public static void parseInfo(CommandContext<CommandSource> ctx, Object... params) throws CommandException
    {
        checkPermission(ctx.getSource(),PERM_INFO);

        tabCompleteArea(arguments);
        String areaName = arguments.remove();

        WorldZone worldZone = arguments.getWorldZone();
        AreaZone areaZone = getAreaZone(worldZone, areaName);
        if (areaZone == null)
            throw new TranslatedCommandException("Area \"%s\" has does not exist!", areaName);
        AreaBase area = areaZone.getArea();

        ChatOutputHandler.chatConfirmation(ctx.getSource(), "Area \"%s\"", areaZone.getName());
        ChatOutputHandler.chatNotification(ctx.getSource(), "  start = " + area.getLowPoint().toString());
        ChatOutputHandler.chatNotification(ctx.getSource(), "  end   = " + area.getHighPoint().toString());
    }

    public static void parseEntryExitMessage(CommandContext<CommandSource> ctx, boolean isEntry, Object... params) throws CommandException
    {
        checkPermission(ctx.getSource(),PERM_SETTINGS);

        tabCompleteArea(arguments);
        String areaName = arguments.remove();

        WorldZone worldZone = arguments.getWorldZone();
        AreaZone areaZone = getAreaZone(worldZone, areaName);
        if (areaZone == null)
            throw new TranslatedCommandException("Area \"%s\" has does not exist!", areaName);

        if (arguments.isEmpty())
        {
            ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format((isEntry ? "Entry" : "Exit") + " message for area %s:", areaZone.getName()));
            ChatOutputHandler.chatConfirmation(ctx.getSource(), areaZone.getGroupPermission(Zone.GROUP_DEFAULT, isEntry ? FEPermissions.ZONE_ENTRY_MESSAGE : FEPermissions.ZONE_EXIT_MESSAGE));
            return;
        }

        arguments.tabComplete("clear");
        String msg = arguments.toString();
        if (msg.equalsIgnoreCase("clear"))
            msg = null;

        areaZone.setGroupPermissionProperty(Zone.GROUP_DEFAULT, isEntry ? FEPermissions.ZONE_ENTRY_MESSAGE : FEPermissions.ZONE_EXIT_MESSAGE, msg);
    }

    public static void tabCompleteArea(CommandContext<CommandSource> ctx, Object... params) throws CommandException
    {
        if (arguments.isTabCompletion && arguments.size() == 1)
        {
            for (Zone z : APIRegistry.perms.getZones())
            {
                if (z instanceof AreaZone)
                {
                    if (z.getName().startsWith(arguments.peek()))
                        arguments.tabCompleteWord(z.getName());
                    if (Integer.toString(z.getId()).startsWith(arguments.peek()))
                        arguments.tabCompleteWord(Integer.toString(z.getId()));
                }
            }
            throw new CommandParserArgs.CancelParsingException();
        }
    }

}
