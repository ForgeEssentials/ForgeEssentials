package com.forgeessentials.economy.plots.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import net.minecraft.block.Blocks;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
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
import com.forgeessentials.core.commands.BaseCommand;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.economy.ModuleEconomy;
import com.forgeessentials.economy.plots.Plot;
import com.forgeessentials.economy.plots.Plot.PlotRedefinedException;
import com.forgeessentials.protection.MobType;
import com.forgeessentials.protection.ModuleProtection;
import com.forgeessentials.util.CommandParserArgs;
import com.forgeessentials.util.CommandUtils;
import com.forgeessentials.util.DoAsCommandSender;
import com.forgeessentials.util.ServerUtil;
import com.forgeessentials.util.events.EventCancelledException;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.questioner.Questioner;
import com.forgeessentials.util.questioner.QuestionerCallback;
import com.forgeessentials.util.selections.SelectionHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class CommandPlot extends BaseCommand
{

    public CommandPlot(String name, int permissionLevel, boolean enabled)
    {
        super(name, permissionLevel, enabled);
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
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int execute(CommandContext<CommandSource> ctx, Object... params) throws CommandSyntaxException
    {
        if (params.toString() == "blank")
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

        //arguments.tabComplete("define", "claim", "list", "select", "set", "perms", "userperms", "mods", "users", "limits", "buy", "sell", "delete");
        String subcmd = params.toString();
        switch (subcmd)
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
            parseList(ctx);
            break;
        case "limits":
            parseLimits(ctx);
            break;
        case "select":
            parseSelect(ctx);
            break;
        case "mods":
            parseMods(ctx, false);
            break;
        case "users":
            parseMods(ctx, true);
            break;
        case "set":
            parseSet(ctx);
            break;
        case "perms":
            parsePerms(ctx, false);
            break;
        case "userperms":
            parsePerms(ctx, true);
            break;
        case "buy":
            parseBuyStart(ctx);
            break;
        case "sell":
            throw new TranslatedCommandException("Not yet implemented. Use \"/plot set price\" instead.");
        default:
            throw new TranslatedCommandException(FEPermissions.MSG_UNKNOWN_SUBCOMMAND, subcmd);
        }
        return Command.SINGLE_SUCCESS;
    }

    public static void parseDefine(CommandContext<CommandSource> ctx) throws CommandException
    {
        arguments.checkPermission(Plot.PERM_DEFINE);
        arguments.requirePlayer();


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
        arguments.checkPermission(Plot.PERM_CLAIM);
        arguments.requirePlayer();


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

    public static void parseList(final CommandContext<CommandSource> ctx) throws CommandException
    {
        arguments.checkPermission(Plot.PERM_LIST);

        PlotListingType listType = PlotListingType.OWN;
        if (!arguments.isEmpty())
        {
            arguments.tabComplete(PlotListingType.stringValues());
            try
            {
                listType = PlotListingType.valueOf(arguments.remove().toUpperCase());
            }
            catch (IllegalArgumentException e)
            {
                throw new TranslatedCommandException(FEPermissions.MSG_INVALID_SYNTAX);
            }
        }


        final WorldPoint playerRef = (ServerPlayerEntity) ctx.getSource().getEntity() != null ? arguments.getSenderPoint().setY(0) : new WorldPoint(0, 0, 0, 0);
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

    public static void parseMods(CommandContext<CommandSource> ctx, boolean modifyUsers) throws CommandException
    {
        Plot plot = getPlot(ctx.getSource());
        String type = modifyUsers ? "users" : "mods";
        String group = modifyUsers ? Plot.GROUP_PLOT_USER : Plot.GROUP_PLOT_MOD;

        arguments.checkPermission(Plot.PERM_MODS);
        if (arguments.isEmpty())
        {
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "/plot " + type + " add|remove <player>: Add / remove " + type);
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "Plot " + type + ":");
            for (UserIdent user : APIRegistry.perms.getServerZone().getKnownPlayers())
                if (plot.getZone().getStoredPlayerGroups(user).contains(group))
                    ChatOutputHandler.chatConfirmation(ctx.getSource(), "  " + user.getUsernameOrUuid());
            return;
        }
        arguments.tabComplete("add", "remove");
        String action = arguments.remove().toLowerCase();

        UserIdent player = arguments.parsePlayer(true, false);

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
            throw new TranslatedCommandException.InvalidSyntaxException();
        }
    }

    public static void parseSet(CommandContext<CommandSource> ctx) throws CommandException
    {
        if (arguments.isEmpty())
        {
            if (APIRegistry.perms.checkPermission((PlayerEntity) ctx.getSource().getEntity(),Plot.PERM_SET_PRICE))
                ChatOutputHandler.chatConfirmation(ctx.getSource(), "/plot set price: Put up plot for sale");
            if (APIRegistry.perms.checkPermission((PlayerEntity) ctx.getSource().getEntity(),Plot.PERM_SET_FEE))
                ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.translate("/plot set fee: Set a fee (WIP)")); // TODO WIP plots
            if (APIRegistry.perms.checkPermission((PlayerEntity) ctx.getSource().getEntity(),Plot.PERM_SET_NAME))
                ChatOutputHandler.chatConfirmation(ctx.getSource(), "/plot set name: Set the plot name");
            return;
        }

        arguments.tabComplete("price", "fee", "name", "owner");
        String subcmd = arguments.remove().toLowerCase();
        switch (subcmd)
        {
        case "price":
            parseSetPrice(ctx);
            break;
        case "fee":
            parseSetFee(ctx);
            break;
        case "name":
            parseSetName(ctx);
            break;
        case "owner":
            parseSetOwner(ctx);
            break;
        default:
            break;
        }
    }

    public static void parseSetPrice(CommandContext<CommandSource> ctx) throws CommandException
    {
        Plot plot = getPlot(ctx.getSource());
        if (arguments.isEmpty())
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
        arguments.checkPermission(Plot.PERM_SET_PRICE);

        arguments.tabComplete("clear");
        String priceStr = arguments.remove().toLowerCase();
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

    public static void parseSetFee(CommandContext<CommandSource> ctx) throws CommandException
    {
        Plot plot = getPlot(ctx.getSource());
        if (arguments.isEmpty())
        {
            if (APIRegistry.perms.checkPermission((PlayerEntity) ctx.getSource().getEntity(),Plot.PERM_SET_FEE))
                ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.translate("/plot set fee <amount> <timeout>: Set fee (WIP)")); // TODO WIP
                                                                                                            // plots
            ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Current plot fee: %s", APIRegistry.economy.toString(plot.getFee())));
            return;
        }
        arguments.checkPermission(Plot.PERM_SET_FEE);

        int amount = arguments.parseInt();
        int timeout = arguments.parseInt();

        plot.setFee(amount);
        plot.setFeeTimeout(timeout);
        ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Set plot price to %s and timeout to %d", APIRegistry.economy.toString(amount), timeout));
    }

    public static void parseSetName(CommandContext<CommandSource> ctx) throws CommandException
    {
        Plot plot = getPlot(ctx.getSource());
        if (arguments.isEmpty())
        {
            if (APIRegistry.perms.checkPermission((PlayerEntity) ctx.getSource().getEntity(),Plot.PERM_SET_NAME))
                ChatOutputHandler.chatConfirmation(ctx.getSource(), "/plot set name <name>: Set plot name");
            String name = APIRegistry.perms.getGroupPermissionProperty(Plot.GROUP_ALL, Plot.PERM_NAME);
            if (name == null || name.isEmpty())
                name = "none";
            ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Current plot name: %s", name));
            return;
        }
        String name = arguments.toString();
        arguments.checkPermission(Plot.PERM_SET_NAME);
        plot.getZone().setGroupPermissionProperty(Plot.GROUP_ALL, Plot.PERM_NAME, name);
        ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Set plot name to \"%s\"", name));
    }

    public static void parseSetOwner(CommandContext<CommandSource> ctx) throws CommandException
    {
        Plot plot = getPlot(ctx.getSource());
        if (arguments.isEmpty())
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
        UserIdent newOwner = arguments.parsePlayer(true, false);
        arguments.checkPermission(Plot.PERM_SET_OWNER);
        plot.setOwner(newOwner);
        ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Set plot owner to \"%s\"", newOwner.getUsernameOrUuid()));
    }

    public static void parsePerms(CommandContext<CommandSource> ctx, boolean userPerms) throws CommandException
    {
        final String[] tabCompletion = new String[] { "build", "interact", "use", "chest", "button", "lever", "door", "animal" };

        arguments.checkPermission(Plot.PERM_PERMS);
        Plot plot = getPlot(ctx.getSource());
        if (arguments.isEmpty())
        {
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "/plot perms <type> true|false: Control what other players can do in a plot");
            ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Possible perms: %s", StringUtils.join(tabCompletion, ", ")));
            return;
        }

        arguments.tabComplete(tabCompletion);
        String perm = arguments.remove().toLowerCase();

        arguments.tabComplete("yes", "no", "true", "false", "allow", "deny");
        String allowDeny = arguments.remove().toLowerCase();

        boolean allow;
        switch (allowDeny)
        {
        case "yes":
        case "true":
        case "allow":
            allow = true;
            break;
        case "no":
        case "false":
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
            plot.setPermission(ModuleProtection.getBlockBreakPermission(Blocks.CHEST, 0) + Zone.ALL_PERMS, userPerms, allow);
            plot.setPermission(ModuleProtection.getBlockBreakPermission(Blocks.CHEST, 0) + Zone.ALL_PERMS, userPerms, allow);
            plot.setPermission(ModuleProtection.getBlockInteractPermission(Blocks.CHEST, 0) + Zone.ALL_PERMS, userPerms, allow);
            plot.setPermission(ModuleProtection.getBlockBreakPermission(Blocks.TRAPPED_CHEST, 0) + Zone.ALL_PERMS, userPerms, allow);
            plot.setPermission(ModuleProtection.getBlockInteractPermission(Blocks.TRAPPED_CHEST, 0) + Zone.ALL_PERMS, userPerms, allow);
            ChatOutputHandler.chatConfirmation(ctx.getSource(), msgBase + "to interact with chests");
            break;
        case "button":
            plot.setPermission(ModuleProtection.getBlockInteractPermission(Blocks.ACACIA_BUTTON, 0) + Zone.ALL_PERMS, userPerms, allow);
            plot.setPermission(ModuleProtection.getBlockInteractPermission(Blocks.BIRCH_BUTTON, 0) + Zone.ALL_PERMS, userPerms, allow);
            plot.setPermission(ModuleProtection.getBlockInteractPermission(Blocks.CRIMSON_BUTTON, 0) + Zone.ALL_PERMS, userPerms, allow);
            plot.setPermission(ModuleProtection.getBlockInteractPermission(Blocks.DARK_OAK_BUTTON, 0) + Zone.ALL_PERMS, userPerms, allow);
            plot.setPermission(ModuleProtection.getBlockInteractPermission(Blocks.JUNGLE_BUTTON, 0) + Zone.ALL_PERMS, userPerms, allow);
            plot.setPermission(ModuleProtection.getBlockInteractPermission(Blocks.OAK_BUTTON, 0) + Zone.ALL_PERMS, userPerms, allow);
            plot.setPermission(ModuleProtection.getBlockInteractPermission(Blocks.SPRUCE_BUTTON, 0) + Zone.ALL_PERMS, userPerms, allow);
            plot.setPermission(ModuleProtection.getBlockInteractPermission(Blocks.WARPED_BUTTON, 0) + Zone.ALL_PERMS, userPerms, allow);
            plot.setPermission(ModuleProtection.getBlockInteractPermission(Blocks.STONE_BUTTON, 0) + Zone.ALL_PERMS, userPerms, allow);
            plot.setPermission(ModuleProtection.getBlockInteractPermission(Blocks.POLISHED_BLACKSTONE_BUTTON, 0) + Zone.ALL_PERMS, userPerms, allow);
            ChatOutputHandler.chatConfirmation(ctx.getSource(), msgBase + "to interact with buttons");
            break;
        case "lever":
            plot.setPermission(ModuleProtection.getBlockInteractPermission(Blocks.LEVER, 0) + Zone.ALL_PERMS, userPerms, allow);
            ChatOutputHandler.chatConfirmation(ctx.getSource(), msgBase + "to interact with levers");
            break;
        case "door":
            plot.setPermission(ModuleProtection.getBlockInteractPermission(Blocks.ACACIA_DOOR, 0) + Zone.ALL_PERMS, userPerms, allow);
            plot.setPermission(ModuleProtection.getBlockInteractPermission(Blocks.BIRCH_DOOR, 0) + Zone.ALL_PERMS, userPerms, allow);
            plot.setPermission(ModuleProtection.getBlockInteractPermission(Blocks.CRIMSON_DOOR, 0) + Zone.ALL_PERMS, userPerms, allow);
            plot.setPermission(ModuleProtection.getBlockInteractPermission(Blocks.DARK_OAK_DOOR, 0) + Zone.ALL_PERMS, userPerms, allow);
            plot.setPermission(ModuleProtection.getBlockInteractPermission(Blocks.JUNGLE_DOOR, 0) + Zone.ALL_PERMS, userPerms, allow);
            plot.setPermission(ModuleProtection.getBlockInteractPermission(Blocks.OAK_DOOR, 0) + Zone.ALL_PERMS, userPerms, allow);
            plot.setPermission(ModuleProtection.getBlockInteractPermission(Blocks.OAK_DOOR, 0) + Zone.ALL_PERMS, userPerms, allow);
            plot.setPermission(ModuleProtection.getBlockInteractPermission(Blocks.WARPED_DOOR, 0) + Zone.ALL_PERMS, userPerms, allow);

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

    public static void parseBuyStart(final CommandContext<CommandSource> ctx) throws CommandException
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
        if (!arguments.isEmpty())
        {
            buyPrice = arguments.parseLong();
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
                ChatOutputHandler.chatConfirmation(plot.getOwner().getPlayerMP(), Translator.format("You sold plot \"%s\" to %s for %s", //
                        plot.getName(), ctx.getSource().getEntity().getName().getString(), priceStr));
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
