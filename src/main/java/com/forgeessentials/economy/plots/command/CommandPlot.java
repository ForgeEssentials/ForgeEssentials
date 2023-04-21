package com.forgeessentials.economy.plots.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import net.minecraft.block.Blocks;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.economy.Wallet;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.commons.selections.Selection;
import com.forgeessentials.commons.selections.WorldArea;
import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.economy.ModuleEconomy;
import com.forgeessentials.economy.plots.Plot;
import com.forgeessentials.economy.plots.Plot.PlotRedefinedException;
import com.forgeessentials.protection.MobType;
import com.forgeessentials.protection.ModuleProtection;
import com.forgeessentials.util.DoAsCommandSender;
import com.forgeessentials.util.ServerUtil;
import com.forgeessentials.util.events.EventCancelledException;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.questioner.Questioner;
import com.forgeessentials.util.questioner.QuestionerCallback;
import com.forgeessentials.util.selections.SelectionHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;

public class CommandPlot extends ForgeEssentialsCommandBuilder
{

    public CommandPlot(boolean enabled)
    {
        super(enabled);
    }

    public static enum PlotListingType
    {
        ALL, OWN, SALE;

        public boolean check(CommandSource sender, Plot plot)
        {
            switch (this)
            {
            case ALL:
                return true;
            case OWN:
                if (plot.getOwner() == null)
                    return true;
                if (!(sender.getEntity() instanceof ServerPlayerEntity))
                    return false;
                return plot.getOwner().getPlayer().equals((PlayerEntity) sender.getEntity());
            case SALE:
                return plot.isForSale();
            default:
                break;
            }
            return false;
        }

        public static Collection<String> stringValues()
        {
            List<String> values = new ArrayList<>();
            for (PlotListingType type : values())
                values.add(type.toString().toLowerCase());
            return values;
        }

    }

    @Override
    public String getPrimaryAlias()
    {
        return "plot";
    }

    @Override
    public String getPermissionNode()
    {
        return Plot.PERM_COMMAND;
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.ALL;
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
                .then(Commands.literal("list")
                        .then(Commands.literal("own")
                                .executes(CommandContext -> execute(CommandContext, "list-own")
                                        )
                                )
                        .then(Commands.literal("sale")
                                .executes(CommandContext -> execute(CommandContext, "list-sale")
                                        )
                                )
                        .then(Commands.literal("all")
                                .executes(CommandContext -> execute(CommandContext, "list-all")
                                        )
                                )
                        )
                .then(Commands.literal("define")
                        .executes(CommandContext -> execute(CommandContext, "define")
                                )
                        )
                .then(Commands.literal("claim")
                        .executes(CommandContext -> execute(CommandContext, "claim")
                                )
                        )
                .then(Commands.literal("set")
                        .then(Commands.literal("price")
                                .executes(CommandContext -> execute(CommandContext, "set-price")
                                        )
                                .then(Commands.literal("clear")
                                        .executes(CommandContext -> execute(CommandContext, "set-price-clear")
                                                )
                                        )
                                .then(Commands.argument("amount", IntegerArgumentType.integer())
                                        .executes(CommandContext -> execute(CommandContext, "set-price-"+Integer.toString(IntegerArgumentType.getInteger(CommandContext, "amount")))
                                                )
                                        )
                                )
                        .then(Commands.literal("fee")
                                .executes(CommandContext -> execute(CommandContext, "set-fee")
                                        )
                                .then(Commands.argument("amount", IntegerArgumentType.integer())
                                        .then(Commands.argument("timeout", IntegerArgumentType.integer())
                                                .executes(CommandContext -> execute(CommandContext, "set-fee-"+Integer.toString(IntegerArgumentType.getInteger(CommandContext, "amount"))+"-"+Integer.toString(IntegerArgumentType.getInteger(CommandContext, "timeout")))
                                                        )
                                                )
                                        )
                                )
                        .then(Commands.literal("name")
                                .then(Commands.argument("name", StringArgumentType.word())
                                        .executes(CommandContext -> execute(CommandContext, "set-name-"+StringArgumentType.getString(CommandContext, "name"))
                                                )
                                        )
                                )
                        .then(Commands.literal("owner")
                                .then(Commands.argument("player", StringArgumentType.word())
                                        .executes(CommandContext -> execute(CommandContext, "set-owner-"+StringArgumentType.getString(CommandContext, "player"))
                                                )
                                        )
                                )
                        )
                .then(Commands.literal("guestperms")
                        .executes(CommandContext -> execute(CommandContext, "guestperms")
                                )
                        .then(Commands.argument("type", StringArgumentType.string())
                                .suggests(SUGGEST_PermTypes)
                                .then(Commands.literal("allow")
                                        .executes(CommandContext -> execute(CommandContext, "guestperms-"+StringArgumentType.getString(CommandContext, "type")+"-allow")
                                                )
                                        )
                                .then(Commands.literal("deny")
                                        .executes(CommandContext -> execute(CommandContext, "guestperms-"+StringArgumentType.getString(CommandContext, "type")+"-deny")
                                                )
                                        )
                                )
                        )
                .then(Commands.literal("select")
                        .executes(CommandContext -> execute(CommandContext, "select")
                                )
                        )
                .then(Commands.literal("userperms")
                        .executes(CommandContext -> execute(CommandContext, "userperms")
                                )
                        .then(Commands.argument("type", StringArgumentType.string())
                                .suggests(SUGGEST_PermTypes)
                                .then(Commands.literal("allow")
                                        .executes(CommandContext -> execute(CommandContext, "userperms-"+StringArgumentType.getString(CommandContext, "type")+"-allow")
                                                )
                                        )
                                .then(Commands.literal("deny")
                                        .executes(CommandContext -> execute(CommandContext, "userperms-"+StringArgumentType.getString(CommandContext, "type")+"-deny")
                                                )
                                        )
                                )
                        )
                .then(Commands.literal("mods")
                        .executes(CommandContext -> execute(CommandContext, "mods")
                                )
                        .then(Commands.literal("add")
                                .then(Commands.argument("player", StringArgumentType.word())
                                        .executes(CommandContext -> execute(CommandContext, "mods-add-"+StringArgumentType.getString(CommandContext, "player"))
                                                )
                                        )
                                )
                        .then(Commands.literal("remove")
                                .then(Commands.argument("player", StringArgumentType.word())
                                        .executes(CommandContext -> execute(CommandContext, "mods-remove-"+StringArgumentType.getString(CommandContext, "player"))
                                                )
                                        )
                                )
                                  
                        )
                .then(Commands.literal("users")
                        .executes(CommandContext -> execute(CommandContext, "users")
                                )
                        .then(Commands.literal("add")
                                .then(Commands.argument("player", StringArgumentType.word())
                                        .executes(CommandContext -> execute(CommandContext, "users-add-"+StringArgumentType.getString(CommandContext, "player"))
                                                )
                                        )
                                )
                        .then(Commands.literal("remove")
                                .then(Commands.argument("player", StringArgumentType.word())
                                        .executes(CommandContext -> execute(CommandContext, "users-remove-"+StringArgumentType.getString(CommandContext, "player"))
                                                )
                                        )
                                )
                        )
                .then(Commands.literal("limits")
                        .executes(CommandContext -> execute(CommandContext, "limits")
                                )
                        )
                .then(Commands.literal("buy")
                        .executes(CommandContext -> execute(CommandContext, "buy")
                                )
                        .then(Commands.argument("amount", LongArgumentType.longArg())
                                .executes(CommandContext -> execute(CommandContext, "buy-"+Long.toString(LongArgumentType.getLong(CommandContext, "amount")))
                                        )
                                )
                        )
                .then(Commands.literal("sell")
                        .executes(CommandContext -> execute(CommandContext, "sell")
                                )
                        )
                .then(Commands.literal("delete")
                        .executes(CommandContext -> execute(CommandContext, "delete")
                                )
                        )
                .executes(CommandContext -> execute(CommandContext, "help")
                        );
    }

    public static final SuggestionProvider<CommandSource> SUGGEST_PermTypes = (ctx, builder) -> {
        List<String> listArgs = new ArrayList<>();
        listArgs.add("build");
        listArgs.add("interact");
        listArgs.add("use");
        listArgs.add("chest");
        listArgs.add("button");
        listArgs.add("lever");
        listArgs.add("door");
        listArgs.add("animal");
        return ISuggestionProvider.suggest(listArgs, builder);
        };

    @Override
    public int execute(CommandContext<CommandSource> ctx, String params) throws CommandSyntaxException
    {
        if (params.equals("help"))
        {
            if (APIRegistry.perms.checkPermission((PlayerEntity) ctx.getSource().getEntity(),Plot.PERM_LIST))
                ChatOutputHandler.chatConfirmation(ctx.getSource(), "/plot list [own|sale|all]: List plots");
            if (APIRegistry.perms.checkPermission((PlayerEntity) ctx.getSource().getEntity(),Plot.PERM_DEFINE))
                ChatOutputHandler.chatConfirmation(ctx.getSource(), "/plot define: Define selection as plot");
            if (APIRegistry.perms.checkPermission((PlayerEntity) ctx.getSource().getEntity(),Plot.PERM_CLAIM))
                ChatOutputHandler.chatConfirmation(ctx.getSource(), "/plot claim: Buy your selected area as plot");
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "/plot limits: Show your plot limits");
            if (APIRegistry.perms.checkPermission((PlayerEntity) ctx.getSource().getEntity(),Plot.PERM_SET))
                ChatOutputHandler.chatConfirmation(ctx.getSource(), "/plot set: Control plot settings");
            if (APIRegistry.perms.checkPermission((PlayerEntity) ctx.getSource().getEntity(),Plot.PERM_PERMS))
                ChatOutputHandler.chatConfirmation(ctx.getSource(), "/plot perms: Control plot permissions");
            ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator
                    .translate("/plot buy [amount]: Buy the plot you are standing in. Owner needs to approve the transaction if plot is not up for sale"));
            return Command.SINGLE_SUCCESS;
        }
        List<String> args = new ArrayList<String>(Arrays.asList(params.split("-")));
        switch (args.remove(0))
        {
        case "define":
            parseDefine(ctx);
            break;
        case "delete":
            parseDelete(ctx);
            break;
        case "claim":
            parseClaim(ctx);
            break;
        case "list":
            parseList(ctx, args);
            break;
        case "limits":
            parseLimits(ctx);
            break;
        case "select":
            parseSelect(ctx);
            break;
        case "mods":
            parseMods(ctx, args, false);
            break;
        case "users":
            parseMods(ctx, args, true);
            break;
        case "set":
            parseSet(ctx, args);
            break;
        case "guestperms":
            parsePerms(ctx, args, false);
            break;
        case "userperms":
            parsePerms(ctx, args, true);
            break;
        case "buy":
            parseBuyStart(ctx, args);
            break;
        case "sell":
            throw new TranslatedCommandException("Not yet implemented. Use \"/plot set price\" instead.");
        default:
            throw new TranslatedCommandException(FEPermissions.MSG_UNKNOWN_SUBCOMMAND, args.get(0));
        }
        return Command.SINGLE_SUCCESS;
    }

    public static void parseDefine(CommandContext<CommandSource> ctx) throws CommandException
    {
    	if(!hasPermission(ctx.getSource(), Plot.PERM_DEFINE)) {
    		ChatOutputHandler.chatError(ctx.getSource(), FEPermissions.MSG_NO_COMMAND_PERM);
    		return;
    	}
        if(!(ctx.getSource().getEntity() instanceof ServerPlayerEntity)) {
            ChatOutputHandler.chatWarning(ctx.getSource(), "Only a player can do this!");
            return;
        }


        Selection selection = SelectionHandler.getSelection((ServerPlayerEntity) ctx.getSource().getEntity());
        if (selection == null || !selection.isValid())
            throw new TranslatedCommandException("Need a valid selection to define a plot");

        try
        {
            if (!Plot.hasPlots(selection))
            {
                Plot.define(selection, getIdent(ctx.getSource()));
                ChatOutputHandler.chatConfirmation(ctx.getSource(), "Plot created!");
            }
            else
            {
                throw new TranslatedCommandException("Can not create overlapping plots.");
            }
        }
        catch (PlotRedefinedException e)
        {
            throw new TranslatedCommandException("There is already a plot defined in this area");
        }
        catch (EventCancelledException e)
        {
            throw new TranslatedCommandException("Plot creation cancelled");
        }
    }

    public static void parseDelete(CommandContext<CommandSource> ctx) throws CommandException
    {
        Plot plot = getPlot(ctx.getSource());
        if (plot.getOwner() == UserIdent.get((ServerPlayerEntity) ctx.getSource().getEntity()) || APIRegistry.perms.checkPermission((PlayerEntity) ctx.getSource().getEntity(),Plot.PERM_DELETE))
        {
            ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Plot \"%s\" has been deleted.", plot.getNameNotNull()));
            long cost = plot.getCalculatedPrice();
            Plot.deletePlot(plot);
            Wallet wallet = APIRegistry.economy.getWallet(plot.getOwner());
            wallet.add(cost);
        }
        else
            throw new TranslatedCommandException("You are not the owner of this plot, you can't delete it!");
    }

    public static void parseClaim(final CommandContext<CommandSource> ctx) throws CommandException
    {
        if(!hasPermission(ctx.getSource(), Plot.PERM_CLAIM)) {
    		ChatOutputHandler.chatError(ctx.getSource(), FEPermissions.MSG_NO_COMMAND_PERM);
    		return;
    	}
        if(!(ctx.getSource().getEntity() instanceof ServerPlayerEntity)) {
            ChatOutputHandler.chatWarning(ctx.getSource(), "Only a player can do this!");
            return;
        }


        final Selection selection = SelectionHandler.getSelection((ServerPlayerEntity) ctx.getSource().getEntity());
        if (selection == null || !selection.isValid())
            throw new TranslatedCommandException("Need a valid selection to define a plot");

        final long price = Plot.getCalculatedPrice(selection);

        QuestionerCallback handler = new QuestionerCallback() {
            @Override
            public void respond(Boolean response)
            {
                if (response == null)
                {
                    ChatOutputHandler.chatError(ctx.getSource(),"Claim request timed out");
                    return;
                }
                if (response == false)
                {
                    ChatOutputHandler.chatError(ctx.getSource(),"Canceled");
                    return;
                }
                try
                {
                    Wallet wallet = APIRegistry.economy.getWallet(getIdent(ctx.getSource()));
                    if (!wallet.covers(price))
                        throw new ModuleEconomy.CantAffordException();

                    checkLimits(ctx, selection);

                    try
                    {
                        Plot.define(selection, getIdent(ctx.getSource()));
                        wallet.withdraw(price);
                        ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Plot created for %s!", APIRegistry.economy.toString(price)));
                    }
                    catch (PlotRedefinedException e)
                    {
                        throw new TranslatedCommandException("There is already a plot defined in this area");
                    }
                    catch (EventCancelledException e)
                    {
                        throw new TranslatedCommandException("Plot creation cancelled");
                    }
                }
                catch (CommandException e)
                {
                    ChatOutputHandler.chatError(ctx.getSource(), e.getMessage());
                }
            }
        };
        if (GetSource(ctx.getSource()) instanceof DoAsCommandSender)
        {
            handler.respond(true);
            return;
        }
        String message = Translator.format("Really claim this plot for %s", APIRegistry.economy.toString(price));
        Questioner.addChecked(ctx.getSource(), message, handler, 30);

    }

    private static void checkLimits(CommandContext<CommandSource> ctx, WorldArea newArea) throws CommandException
    {
        int plotSize = newArea.getXLength() * newArea.getZLength() * (Plot.isColumnMode(newArea.getDimension()) ? 1 : newArea.getYLength());

        int minAxis = ServerUtil.parseIntDefault(APIRegistry.perms.getGlobalPermissionProperty(Plot.PERM_SIZE_MIN), Integer.MIN_VALUE);
        int maxAxis = ServerUtil.parseIntDefault(APIRegistry.perms.getGlobalPermissionProperty(Plot.PERM_SIZE_MAX), Integer.MAX_VALUE);

        if (newArea.getXLength() < minAxis || newArea.getZLength() < minAxis)
        {
            throw new TranslatedCommandException("Plot is too small!");
        }

        if (newArea.getXLength() > maxAxis || newArea.getZLength() > maxAxis)
        {
            throw new TranslatedCommandException("Plot is too big!");
        }

        int limitCount = ServerUtil.parseIntDefault(APIRegistry.perms.getUserPermissionProperty(getIdent(ctx.getSource()), Plot.PERM_LIMIT_COUNT), Integer.MAX_VALUE);
        int limitSize = ServerUtil.parseIntDefault(APIRegistry.perms.getUserPermissionProperty(getIdent(ctx.getSource()), Plot.PERM_LIMIT_SIZE), Integer.MAX_VALUE);
        int usedCount = 0;
        long usedSize = 0;
        for (Plot plot : Plot.getPlots())
            if (getIdent(ctx.getSource()).equals(plot.getOwner()))
            {
                usedCount++;
                usedSize += plot.getAccountedSize();
            }
        if (usedCount + 1 > limitCount)
            throw new TranslatedCommandException("You have reached your limit of %s plots already!", limitCount);
        if (usedSize + plotSize > limitSize)
            throw new TranslatedCommandException("You have reached your limit of %s blocks^2 already!", limitSize);
    }

    public static void parseList(final CommandContext<CommandSource> ctx, List<String> params) throws CommandException
    {
        if(!hasPermission(ctx.getSource(), Plot.PERM_LIST)) {
    		ChatOutputHandler.chatError(ctx.getSource(), FEPermissions.MSG_NO_COMMAND_PERM);
    		return;
    	}

        PlotListingType listType = PlotListingType.OWN;
        if (!params.isEmpty())
        {
            try
            {
                listType = PlotListingType.valueOf(params.remove(0).toUpperCase());
            }
            catch (IllegalArgumentException e)
            {
                throw new TranslatedCommandException(FEPermissions.MSG_INVALID_SYNTAX);
            }
        }


        final WorldPoint playerRef = (ServerPlayerEntity) ctx.getSource().getEntity() != null ? new WorldPoint(((ServerPlayerEntity) ctx.getSource().getEntity()).getLevel(), ((ServerPlayerEntity) ctx.getSource().getEntity()).blockPosition().getX(), 0, ((ServerPlayerEntity) ctx.getSource().getEntity()).blockPosition().getZ()) : new WorldPoint(ServerWorld.OVERWORLD.location().toString(), 0, 0, 0);
        SortedSet<Plot> plots = new TreeSet<Plot>(new Comparator<Plot>() {
            @Override
            public int compare(Plot a, Plot b)
            {
                if (a.getDimension() != playerRef.getDimension())
                {
                    if (b.getDimension() == playerRef.getDimension())
                        return 1;
                }
                else
                {
                    if (b.getDimension() != playerRef.getDimension())
                        return -1;
                }
                double aDist = a.getZone().getArea().getCenter().setY(0).distance(playerRef);
                double bDist = b.getZone().getArea().getCenter().setY(0).distance(playerRef);
                return (int) Math.signum(aDist - bDist);
            }
        });

        for (Plot plot : Plot.getPlots())
            if (listType.check(ctx.getSource(), plot))
                plots.add(plot);

        ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.translate("Listing " + listType.toString().toLowerCase() + " plots:"));
        for (Plot plot : plots)
            plot.printInfo(ctx.getSource());
    }

    public static void parseLimits(CommandContext<CommandSource> ctx) throws CommandException
    {
        String limitCount = APIRegistry.perms.getUserPermissionProperty(getIdent(ctx.getSource()), Plot.PERM_LIMIT_COUNT);
        if (limitCount == null || limitCount.isEmpty())
            limitCount = "infinite";

        String limitSize = APIRegistry.perms.getUserPermissionProperty(getIdent(ctx.getSource()), Plot.PERM_LIMIT_SIZE);
        if (limitSize == null || limitSize.isEmpty())
            limitSize = "infinite";

        int usedCount = 0;
        long usedSize = 0;
        for (Plot plot : Plot.getPlots())
            if (getIdent(ctx.getSource()).equals(plot.getOwner()))
            {
                usedCount++;
                usedSize += plot.getAccountedSize();
            }

        ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("You use %d of %s allowed plot count.", usedCount, limitCount));
        ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("You use %d of %s allowed plot size.", usedSize, limitSize));
    }

    public static void parseSelect(CommandContext<CommandSource> ctx) throws CommandException
    {
        Plot plot = getPlot(ctx.getSource());
        SelectionHandler.select((ServerPlayerEntity) ctx.getSource().getEntity(), plot.getDimension(), plot.getZone().getArea());
        ChatOutputHandler.chatConfirmation(ctx.getSource(), "Selected plot");
    }

    public static void parseMods(CommandContext<CommandSource> ctx, List<String> params, boolean modifyUsers) throws CommandException
    {
        Plot plot = getPlot(ctx.getSource());
        String type = modifyUsers ? "users" : "mods";
        String group = modifyUsers ? Plot.GROUP_PLOT_USER : Plot.GROUP_PLOT_MOD;

        if(!hasPermission(ctx.getSource(), Plot.PERM_MODS)) {
    		ChatOutputHandler.chatError(ctx.getSource(), FEPermissions.MSG_NO_COMMAND_PERM);
    		return;
    	}
        if (params.isEmpty())
        {
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "/plot " + type + " add|remove <player>: Add / remove " + type);
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "Plot " + type + ":");
            for (UserIdent user : APIRegistry.perms.getServerZone().getKnownPlayers())
                if (plot.getZone().getStoredPlayerGroups(user).contains(group))
                    ChatOutputHandler.chatConfirmation(ctx.getSource(), "  " + user.getUsernameOrUuid());
            return;
        }
        String action = params.remove(0).toLowerCase();

        UserIdent player = parsePlayer(params.remove(0),null, true, false);

        switch (action)
        {
        case "add":
            plot.getZone().addPlayerToGroup(player, modifyUsers ? Plot.GROUP_PLOT_USER : Plot.GROUP_PLOT_MOD);
            ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Added %s to plot " + type, player.getUsernameOrUuid()));
            break;
        case "remove":
            plot.getZone().removePlayerFromGroup(player, modifyUsers ? Plot.GROUP_PLOT_USER : Plot.GROUP_PLOT_MOD);
            ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Removed %s from plot " + type, player.getUsernameOrUuid()));
            break;
        default:
            ChatOutputHandler.chatError(ctx.getSource(), "Invalid Arguments");
        }
    }

    public static void parseSet(CommandContext<CommandSource> ctx, List<String> params) throws CommandException
    {
        if (params.isEmpty())
        {
            if (APIRegistry.perms.checkPermission((PlayerEntity) ctx.getSource().getEntity(),Plot.PERM_SET_PRICE))
                ChatOutputHandler.chatConfirmation(ctx.getSource(), "/plot set price: Put up plot for sale");
            if (APIRegistry.perms.checkPermission((PlayerEntity) ctx.getSource().getEntity(),Plot.PERM_SET_FEE))
                ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.translate("/plot set fee: Set a fee (WIP)")); // TODO WIP plots
            if (APIRegistry.perms.checkPermission((PlayerEntity) ctx.getSource().getEntity(),Plot.PERM_SET_NAME))
                ChatOutputHandler.chatConfirmation(ctx.getSource(), "/plot set name: Set the plot name");
            return;
        }

        String subcmd = params.remove(0).toLowerCase();
        switch (subcmd)
        {
        case "price":
            parseSetPrice(ctx, params);
            break;
        case "fee":
            parseSetFee(ctx, params);
            break;
        case "name":
            parseSetName(ctx, params);
            break;
        case "owner":
            parseSetOwner(ctx, params);
            break;
        default:
            break;
        }
    }

    public static void parseSetPrice(CommandContext<CommandSource> ctx, List<String> params) throws CommandException
    {
        Plot plot = getPlot(ctx.getSource());
        if (params.isEmpty())
        {
            if (APIRegistry.perms.checkPermission((PlayerEntity) ctx.getSource().getEntity(),Plot.PERM_SET_PRICE))
            {
                ChatOutputHandler.chatConfirmation(ctx.getSource(), "/plot set price <amount>: Offer plot for sale");
                ChatOutputHandler.chatConfirmation(ctx.getSource(), "/plot set price clear: Remove plot from sale");
            }
            long price = plot.getPrice();
            if (price >= 0)
                ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Current plot price: %s", APIRegistry.economy.toString(price)));
            else
                ChatOutputHandler.chatConfirmation(ctx.getSource(), "Current plot is not up for sale");
            return;
        }
        if(!hasPermission(ctx.getSource(), Plot.PERM_SET_PRICE)) {
    		ChatOutputHandler.chatError(ctx.getSource(), FEPermissions.MSG_NO_COMMAND_PERM);
    		return;
    	}

        String priceStr = params.remove(0).toLowerCase();
        int price = -1;
        if (!priceStr.equals("clear"))
            price = parseInt(priceStr);

        if (price >= 0)
        {
            plot.setPrice(price);
            ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Put up plot for sale for %s", APIRegistry.economy.toString(price)));
        }
        else
        {
            plot.setPrice(-1);
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "Removed plot from sale");
        }
    }

    public static void parseSetFee(CommandContext<CommandSource> ctx, List<String> params) throws CommandException
    {
        Plot plot = getPlot(ctx.getSource());
        if (params.isEmpty())
        {
            if (APIRegistry.perms.checkPermission((PlayerEntity) ctx.getSource().getEntity(),Plot.PERM_SET_FEE))
                ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.translate("/plot set fee <amount> <timeout>: Set fee (WIP)")); // TODO WIP
                                                                                                            // plots
            ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Current plot fee: %s", APIRegistry.economy.toString(plot.getFee())));
            return;
        }
        if(!hasPermission(ctx.getSource(), Plot.PERM_SET_FEE)) {
    		ChatOutputHandler.chatError(ctx.getSource(), FEPermissions.MSG_NO_COMMAND_PERM);
    		return;
    	}

        int amount = parseInt(params.remove(0));
        int timeout = parseInt(params.remove(0));

        plot.setFee(amount);
        plot.setFeeTimeout(timeout);
        ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Set plot price to %s and timeout to %d", APIRegistry.economy.toString(amount), timeout));
    }

    public static void parseSetName(CommandContext<CommandSource> ctx, List<String> params) throws CommandException
    {
        Plot plot = getPlot(ctx.getSource());
        if (params.isEmpty())
        {
            if (APIRegistry.perms.checkPermission((PlayerEntity) ctx.getSource().getEntity(),Plot.PERM_SET_NAME))
                ChatOutputHandler.chatConfirmation(ctx.getSource(), "/plot set name <name>: Set plot name");
            String name = APIRegistry.perms.getGroupPermissionProperty(Plot.GROUP_ALL, Plot.PERM_NAME);
            if (name == null || name.isEmpty())
                name = "none";
            ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Current plot name: %s", name));
            return;
        }
        String name = params.toString();
        if(!hasPermission(ctx.getSource(), Plot.PERM_SET_NAME)) {
    		ChatOutputHandler.chatError(ctx.getSource(), FEPermissions.MSG_NO_COMMAND_PERM);
    		return;
    	}
        plot.getZone().setGroupPermissionProperty(Plot.GROUP_ALL, Plot.PERM_NAME, name);
        ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Set plot name to \"%s\"", name));
    }

    public static void parseSetOwner(CommandContext<CommandSource> ctx, List<String> params) throws CommandException
    {
        Plot plot = getPlot(ctx.getSource());
        if (params.isEmpty())
        {
            if (APIRegistry.perms.checkPermission((PlayerEntity) ctx.getSource().getEntity(),Plot.PERM_SET_OWNER))
            {
                ChatOutputHandler.chatConfirmation(ctx.getSource(), "/plot set owner <player>: Set plot owner");
                ChatOutputHandler.chatConfirmation(ctx.getSource(), "/plot set owner " + APIRegistry.IDENT_SERVER.getUsernameOrUuid() + ": Set plot owner to server");
            }
            UserIdent owner = plot.getOwner();
            if (owner == null)
                owner = APIRegistry.IDENT_SERVER;
            ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Current plot owner: %s", owner.getUsernameOrUuid()));
            return;
        }
        UserIdent newOwner = parsePlayer(params.remove(0), null, true, false);
        if(!hasPermission(ctx.getSource(), Plot.PERM_SET_OWNER)) {
    		ChatOutputHandler.chatError(ctx.getSource(), FEPermissions.MSG_NO_COMMAND_PERM);
    		return;
    	}
        plot.setOwner(newOwner);
        ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Set plot owner to \"%s\"", newOwner.getUsernameOrUuid()));
    }

    public static void parsePerms(CommandContext<CommandSource> ctx, List<String> params, boolean userPerms) throws CommandException
    {
        final String[] tabCompletion = new String[] { "build", "interact", "use", "chest", "button", "lever", "door", "animal" };
        if(!hasPermission(ctx.getSource(), Plot.PERM_PERMS)) {
    		ChatOutputHandler.chatError(ctx.getSource(), FEPermissions.MSG_NO_COMMAND_PERM);
    		return;
    	}
        Plot plot = getPlot(ctx.getSource());
        if (params.isEmpty())
        {
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "/plot perms <type> true|false: Control what other players can do in a plot");
            ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Possible perms: %s", StringUtils.join(tabCompletion, ", ")));
            return;
        }

        String perm = params.remove(0).toLowerCase();

        String allowDeny = params.remove(0).toLowerCase();

        boolean allow;
        switch (allowDeny)
        {
        case "allow":
            allow = true;
            break;
        case "deny":
            allow = false;
            break;
        default:
            throw new TranslatedCommandException(FEPermissions.MSG_INVALID_SYNTAX);
        }

        String msgBase = (allow ? "Allowed " : "Denied ") + (userPerms ? "users " : "guests ");
        switch (perm)
        {
        case "build":
            plot.setPermission(ModuleProtection.PERM_PLACE + Zone.ALL_PERMS, userPerms, allow);
            plot.setPermission(ModuleProtection.PERM_BREAK + Zone.ALL_PERMS, userPerms, allow);
            ChatOutputHandler.chatConfirmation(ctx.getSource(), msgBase + "to build");
            break;
        case "use":
            plot.setPermission(ModuleProtection.PERM_USE + Zone.ALL_PERMS, userPerms, allow);
            ChatOutputHandler.chatConfirmation(ctx.getSource(), msgBase + "to use items");
            break;
        case "interact":
            plot.setPermission(ModuleProtection.PERM_INTERACT + Zone.ALL_PERMS, userPerms, allow);
            ChatOutputHandler.chatConfirmation(ctx.getSource(), msgBase + "to interact with objects");
            break;
        case "chest":
            plot.setPermission(ModuleProtection.getBlockBreakPermission(Blocks.CHEST) + Zone.ALL_PERMS, userPerms, allow);
            plot.setPermission(ModuleProtection.getBlockBreakPermission(Blocks.CHEST) + Zone.ALL_PERMS, userPerms, allow);
            plot.setPermission(ModuleProtection.getBlockInteractPermission(Blocks.CHEST) + Zone.ALL_PERMS, userPerms, allow);
            plot.setPermission(ModuleProtection.getBlockBreakPermission(Blocks.TRAPPED_CHEST) + Zone.ALL_PERMS, userPerms, allow);
            plot.setPermission(ModuleProtection.getBlockInteractPermission(Blocks.TRAPPED_CHEST) + Zone.ALL_PERMS, userPerms, allow);
            ChatOutputHandler.chatConfirmation(ctx.getSource(), msgBase + "to interact with chests");
            break;
        case "button":
            plot.setPermission(ModuleProtection.getBlockInteractPermission(Blocks.ACACIA_BUTTON) + Zone.ALL_PERMS, userPerms, allow);
            plot.setPermission(ModuleProtection.getBlockInteractPermission(Blocks.BIRCH_BUTTON) + Zone.ALL_PERMS, userPerms, allow);
            plot.setPermission(ModuleProtection.getBlockInteractPermission(Blocks.CRIMSON_BUTTON) + Zone.ALL_PERMS, userPerms, allow);
            plot.setPermission(ModuleProtection.getBlockInteractPermission(Blocks.DARK_OAK_BUTTON) + Zone.ALL_PERMS, userPerms, allow);
            plot.setPermission(ModuleProtection.getBlockInteractPermission(Blocks.JUNGLE_BUTTON) + Zone.ALL_PERMS, userPerms, allow);
            plot.setPermission(ModuleProtection.getBlockInteractPermission(Blocks.OAK_BUTTON) + Zone.ALL_PERMS, userPerms, allow);
            plot.setPermission(ModuleProtection.getBlockInteractPermission(Blocks.SPRUCE_BUTTON) + Zone.ALL_PERMS, userPerms, allow);
            plot.setPermission(ModuleProtection.getBlockInteractPermission(Blocks.WARPED_BUTTON) + Zone.ALL_PERMS, userPerms, allow);
            plot.setPermission(ModuleProtection.getBlockInteractPermission(Blocks.STONE_BUTTON) + Zone.ALL_PERMS, userPerms, allow);
            plot.setPermission(ModuleProtection.getBlockInteractPermission(Blocks.POLISHED_BLACKSTONE_BUTTON) + Zone.ALL_PERMS, userPerms, allow);
            ChatOutputHandler.chatConfirmation(ctx.getSource(), msgBase + "to interact with buttons");
            break;
        case "lever":
            plot.setPermission(ModuleProtection.getBlockInteractPermission(Blocks.LEVER) + Zone.ALL_PERMS, userPerms, allow);
            ChatOutputHandler.chatConfirmation(ctx.getSource(), msgBase + "to interact with levers");
            break;
        case "door":
            plot.setPermission(ModuleProtection.getBlockInteractPermission(Blocks.ACACIA_DOOR) + Zone.ALL_PERMS, userPerms, allow);
            plot.setPermission(ModuleProtection.getBlockInteractPermission(Blocks.BIRCH_DOOR) + Zone.ALL_PERMS, userPerms, allow);
            plot.setPermission(ModuleProtection.getBlockInteractPermission(Blocks.CRIMSON_DOOR) + Zone.ALL_PERMS, userPerms, allow);
            plot.setPermission(ModuleProtection.getBlockInteractPermission(Blocks.DARK_OAK_DOOR) + Zone.ALL_PERMS, userPerms, allow);
            plot.setPermission(ModuleProtection.getBlockInteractPermission(Blocks.JUNGLE_DOOR) + Zone.ALL_PERMS, userPerms, allow);
            plot.setPermission(ModuleProtection.getBlockInteractPermission(Blocks.OAK_DOOR) + Zone.ALL_PERMS, userPerms, allow);
            plot.setPermission(ModuleProtection.getBlockInteractPermission(Blocks.OAK_DOOR) + Zone.ALL_PERMS, userPerms, allow);
            plot.setPermission(ModuleProtection.getBlockInteractPermission(Blocks.WARPED_DOOR) + Zone.ALL_PERMS, userPerms, allow);

            ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.translate(msgBase + "to interact with doors"));
            break;
        case "animal":
            plot.setPermission(MobType.PASSIVE.getDamageToPermission(), userPerms, allow);
            plot.setPermission(MobType.TAMED.getDamageToPermission(), userPerms, allow);
            ChatOutputHandler.chatConfirmation(ctx.getSource(), msgBase + "to hurt animals");
            break;
        default:
            throw new TranslatedCommandException(FEPermissions.MSG_INVALID_SYNTAX);
        }
    }

    public static void parseBuyStart(final CommandContext<CommandSource> ctx, List<String> params) throws CommandException
    {
        final Plot plot = getPlot(ctx.getSource());
        if (plot == null)
            throw new TranslatedCommandException("There is no plot at this position");
        if (plot.getOwner() != null && plot.getOwner().equals(getIdent(ctx.getSource())))
            throw new TranslatedCommandException("You already own this plot");

        checkLimits(ctx, plot.getZone().getWorldArea());

        final long plotPrice = plot.getCalculatedPrice();
        final long sellPrice = plot.getPrice();
        final long buyPrice;
        if (!params.isEmpty())
        {
            buyPrice = parseLong(params.remove(0));
            if (sellPrice >= 0 && sellPrice < buyPrice)
                ChatOutputHandler.chatNotification(ctx.getSource(), Translator.format("%s is above the plots default price of %s", APIRegistry.economy.toString(buyPrice),
                        APIRegistry.economy.toString(sellPrice)));
        }
        else
        {
            if (sellPrice >= 0)
                buyPrice = sellPrice;
            else
                buyPrice = plotPrice;
        }
        final String buyPriceStr = APIRegistry.economy.toString(buyPrice);

        if (!plot.hasOwner())
        {
            if (sellPrice < 0)
            {
                ChatOutputHandler.chatError(ctx.getSource(), Translator.format("This plot is not for sale!"));
                return;
            }
            if (buyPrice != sellPrice)
            {
                ChatOutputHandler.chatError(ctx.getSource(), Translator.format("The fixed price of this plot is %s.", APIRegistry.economy.toString(sellPrice)));
                return;
            }
        }

        QuestionerCallback handler = new QuestionerCallback() {
            @Override
            public void respond(Boolean response) throws CommandException
            {
                if (response == null)
                {
                    ChatOutputHandler.chatError(ctx.getSource(), "Buy request timed out");
                    return;
                }
                if (response == false)
                {
                    ChatOutputHandler.chatError(ctx.getSource(), "Canceled");
                    return;
                }
                if (sellPrice < 0 || sellPrice > buyPrice)
                {
                    if (sellPrice < 0 && !plot.getOwner().hasPlayer())
                        throw new TranslatedCommandException("You cannot buy this plot because the owner is not online.");
                    String message = Translator.format("Player %s wants to buy your plot \"%s\" for %s.", //
                            getIdent(ctx.getSource()).getUsernameOrUuid(), plot.getName(), buyPriceStr);
                    if (buyPrice < sellPrice && sellPrice >= 0)
                        message += " \u00a7c" + Translator.format("This is below the price of %s you set up!", APIRegistry.economy.toString(sellPrice));
                    if (buyPrice < plotPrice)
                        message += " \u00a7c" + Translator.format("This is below the plots value of %s!", APIRegistry.economy.toString(sellPrice));

                    QuestionerCallback handler = new QuestionerCallback() {
                        @Override
                        public void respond(Boolean response) throws CommandException
                        {
                            if (response == null)
                            {
                                ChatOutputHandler.chatError(ctx.getSource(), Translator.format("%s did not respond to your buy request", plot.getOwner().getUsernameOrUuid()));
                                return;
                            }
                            else if (response == false)
                            {
                                ChatOutputHandler.chatError(plot.getOwner().getPlayerMP(), Translator.translate("Trade declined"));
                                ChatOutputHandler.chatError(ctx.getSource(), Translator.format("%s declined to sell you plot \"%s\" for %s", //
                                        plot.getOwner().getUsernameOrUuid(), plot.getName(), buyPriceStr));
                                return;
                            }
                            buyPlot(ctx, plot, plotPrice);
                        }
                    };
                    Questioner.addChecked(plot.getOwner().getPlayerMP().createCommandSourceStack(), message, handler, 60);
                }
                else
                {
                    buyPlot(ctx, plot, buyPrice);
                }
            }
        };
        if (GetSource(ctx.getSource()) instanceof DoAsCommandSender)
        {
            handler.respond(true);
            return;
        }
        String message = Translator.format("Really buy this plot for %s", buyPriceStr);
        Questioner.addChecked(ctx.getSource(), message, handler, 30);
    }

    public static void buyPlot(CommandContext<CommandSource> ctx, Plot plot, long price) throws CommandException
    {
        String priceStr = APIRegistry.economy.toString(price);
        Wallet buyerWallet = APIRegistry.economy.getWallet(getIdent(ctx.getSource()));
        if (!buyerWallet.withdraw(price))
        {
            ChatOutputHandler.chatError(ctx.getSource(), Translator.translate("You can't afford that"));
            return;
        }
        if (plot.hasOwner())
        {
            Wallet sellerWallet = APIRegistry.economy.getWallet(plot.getOwner());
            sellerWallet.add(price);
            if (plot.getOwner().hasPlayer())
            {
                ChatOutputHandler.chatConfirmation(plot.getOwner().getPlayerMP(), Translator.format("You sold plot \"%s\" to %s for %s", 
                        plot.getName(), ctx.getSource().getEntity().getDisplayName().getString(), priceStr));
                ModuleEconomy.confirmNewWalletAmount(plot.getOwner(), sellerWallet);
            }
            ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("%s sold plot \"%s\" to you for %s", //
                    plot.getOwner().getUsernameOrUuid(), plot.getName(), priceStr));
            ModuleEconomy.confirmNewWalletAmount(getIdent(ctx.getSource()), buyerWallet);
        }
        else
        {
            ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("You bought plot \"%s\" from the server for %s", plot.getName(), priceStr));
            ModuleEconomy.confirmNewWalletAmount(getIdent(ctx.getSource()), buyerWallet);
        }
        plot.setOwner(getIdent(ctx.getSource()));
        plot.setPrice(-1);
    }

    public static Plot getPlot(CommandSource sender) throws CommandException
    {
        Plot plot = Plot.getPlot(new WorldPoint(sender.getEntity().level.dimension(), sender.getPosition()));
        if (plot == null)
            throw new TranslatedCommandException("There is no plot at this position. You have to stand inside it to use plot commands.");
        return plot;
    }
}
