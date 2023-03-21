package com.forgeessentials.playerlogger;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.arguments.BlockStateArgument;

import com.forgeessentials.api.UserIdent;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.util.CommandUtils;
import com.mojang.brigadier.context.CommandContext;

public class FilterConfig
{

    public enum ActionEnum
    {
        blockPlace, blockBreak, blockDetonate, blockUse_Left, blockUse_Right, blockBurn, command, playerLogin, playerLogout, playerRespawn, playerChangeDim, playerPosition, other
    }

    public static HashMap<UserIdent, FilterConfig> perPlayerFilters = new HashMap<>();

    public static FilterConfig globalConfig = new FilterConfig();

    private HashSet<ActionEnum> actions = new HashSet<>();

    public boolean hasAction(ActionEnum a)
    {
        return Awhitelist == actions.contains(a);
    }

    private HashSet<Block> blocks = new HashSet<>();

    public boolean hasBlock(Block b)
    {
        return Bwhitelist == blocks.contains(b);
    }

    public static HashSet<String> keywords = new HashSet<>();

    public static ArrayList<String> actiontabs = new ArrayList<>();

    public Boolean Awhitelist = null;
    public Boolean Bwhitelist = null;

    public int pickerRange = 0;

    public UserIdent player;

    static
    {
        keywords.add("action");
        keywords.add("blockid");
        keywords.add("before");
        keywords.add("after");
        keywords.add("range");
        keywords.add("whitelist");
        keywords.add("blacklist");
        keywords.add("player");

        ActionEnum[] enums = ActionEnum.values();

        for (ActionEnum ae : enums)
        {
            actiontabs.add(ae.name());
        }
        actiontabs.add("reset");

        try
        {
            globalConfig.parse(null, null);
        }
        catch (CommandException e)
        {

        }
    }

    public static FilterConfig getDefaultPlayerConfig(UserIdent ident)
    {
        if (perPlayerFilters.containsKey(ident))
            return perPlayerFilters.get(ident);
        return globalConfig;
    }

    public final static long default_after = 365L*24*60*60*1000;
    public long before = 0;
    public long after = default_after;

    public Date After()
    {
        return new Date(System.currentTimeMillis() - after);
    }

    public Date Before()
    {
        return new Date(System.currentTimeMillis() - before);
    }

    public void parse(CommandContext<CommandSource> ctx, List<String> args) throws CommandException
    {
        if (args != null)
        {
            //while (!(args.length==0))
            {
                //args.tabComplete(keywords);
                String next = args.remove(0);
                switch (next)
                {
                case "action":
                    parseActions(ctx, args);
                    break;
                case "blockid":
                    parseBlock(ctx, args);
                    break;
                case "before":
                    parseBefore(ctx, args);
                    break;
                case "after":
                    parseAfter(ctx, args);
                    break;
                case "range":
                    parseRange(ctx, args);
                    break;
                case "whitelist":
                    parseWhitelist(ctx, args, true);
                    break;
                case "blacklist":
                    parseWhitelist(ctx, args, false);
                    break;
                case "player":
                    player = CommandUtils.parsePlayer(args.remove(0),ctx.getSource(), true, false);
                    break;
                default:
                    throw new TranslatedCommandException("Expected Keyword here!");

                }

            }
        }
        if (Awhitelist == null)
        {
            Awhitelist = !actions.isEmpty();
        }
        if (Bwhitelist == null)
        {
            Bwhitelist = !blocks.isEmpty();
        }

    }

    public void parseWhitelist(CommandContext<CommandSource> ctx, List<String> args, boolean enabled)
    {
        //while (!args.isEmpty() && !keywords.contains(args.peek()))
        {
            String name = args.remove(0);
            if (name.equalsIgnoreCase("actions"))
            {
                Awhitelist = enabled;
            }
            else if (name.equalsIgnoreCase("blocks"))
            {
                Bwhitelist = enabled;
            }
        }
    }

    public FilterConfig(FilterConfig c)
    {
        this();
        actions.addAll(c.actions);
        blocks.addAll(c.blocks);
        before = c.before;
        after = c.after;
        Awhitelist = c.Awhitelist;
        Bwhitelist = c.Bwhitelist;
        player = c.player;
        pickerRange = c.pickerRange;
    }

    public FilterConfig()
    {
    }

    public void parseActions(CommandContext<CommandSource> ctx, List<String> args) throws CommandException
    {
        //while (!args.isEmpty() && !keywords.contains(args.peek()))
        {
            String arg = args.remove(0);
            if (arg.equals("reset"))
            {
                actions.clear();
            }
            else
            {
                try
                {
                    actions.add(ActionEnum.valueOf(arg));
                }
                catch (IllegalArgumentException e)
                {
                    throw new TranslatedCommandException("Invalid Action");
                }
            }

        }
    }

    public void parseBlock(CommandContext<CommandSource> ctx, List<String> args) throws CommandException
    {
        //while (!args.isEmpty() && !keywords.contains(args.peek()))
        {
            if (args.get(0).equals("reset"))
            {
                blocks.clear();
                args.remove(0);
            }
            else
            {
                blocks.add(BlockStateArgument.getBlock(ctx, "block").getState().getBlock());
            }
        }
    }

    public void parseBefore(CommandContext<CommandSource> ctx, List<String> args) throws CommandException
    {
        if (!args.isEmpty())
        {
            if (args.get(0).equals("reset"))
            {
                before = 0;
            }
            else
            {
                before = 0;
                before += CommandUtils.parseTimeReadable(args.remove(0));
            }
        }
        else
            throw new TranslatedCommandException("A time must be specified here!");
    }

    public void parseAfter(CommandContext<CommandSource> ctx, List<String> args) throws CommandException
    {
        if (!args.isEmpty())
        {
            if (args.get(0).equals("reset"))
            {
                after = default_after;
            }
            else
            {
                after = 0;
                after += CommandUtils.parseTimeReadable(args.remove(0));
            }
        }
        else
            throw new TranslatedCommandException("A time must be specified here!");
    }

    public void parseRange(CommandContext<CommandSource> ctx, List<String> args) throws CommandException
    {
        if (!args.isEmpty())
        {
            pickerRange = CommandUtils.parseInt(args.remove(0));
        }
        else
            throw new TranslatedCommandException("A integer must be specified here!");
    }

    public String toReadableString()
    {
        return "Before: " + before + "\nAfter: " + after + "\nActions: " + actions + "\nBlocks: " + blocks + "\nWhitelist: " + Awhitelist;
    }

}
