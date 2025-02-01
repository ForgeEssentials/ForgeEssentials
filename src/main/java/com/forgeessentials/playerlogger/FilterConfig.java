package com.forgeessentials.playerlogger;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import net.minecraft.block.Block;
import net.minecraft.command.CommandException;

import com.forgeessentials.api.UserIdent;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.util.CommandParserArgs;

public class FilterConfig
{

    public enum ActionEnum
    {
        blockPlace,
        blockBreak,
        blockDetonate,
        blockUse_Left,
        blockUse_Right,
        blockBurn,
        command,
        playerLogin,
        playerLogout,
        playerRespawn,
        playerChangeDim,
        playerPosition,
        other
    }

    public static HashMap<UserIdent,FilterConfig> perPlayerFilters = new HashMap<>();

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

    static {
        keywords.add("action");
        keywords.add("block");
        keywords.add("blockid");
        keywords.add("before");
        keywords.add("after");
        keywords.add("range");
        keywords.add("whitelist");
        keywords.add("blacklist");

        ActionEnum[] enums = ActionEnum.values();

        for (ActionEnum ae : enums)
        {
            actiontabs.add(ae.name());
        }
        actiontabs.add("reset");

        try
        {
            globalConfig.parse(null);
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
    public void parse(CommandParserArgs args) throws CommandException
    {
        if (args != null)
        {
            while (!args.isEmpty())
            {
                args.tabComplete(keywords);
                String next = args.remove();
                switch (next)
                {
                case "action":
                    parseActions(args);
                    break;
                case "block":
                case "blockid":
                    parseBlock(args);
                    break;
                case "before":
                    parseBefore(args);
                    break;
                case "after":
                    parseAfter(args);
                    break;
                case "range":
                    parseRange(args);
                    break;
                case "whitelist":
                    parseWhitelist(args,true);
                    break;
                case "blacklist":
                    parseWhitelist(args,false);
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

    public void parseWhitelist(CommandParserArgs args, boolean enabled)
    {
        while (!args.isEmpty() && !keywords.contains(args.peek()))
        {
            String name = args.remove();
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
        pickerRange = c.pickerRange;
    }
    public FilterConfig()
    {
    }

    public void parseActions(CommandParserArgs args) throws CommandException
    {
        while (!args.isEmpty() && !keywords.contains(args.peek()))
        {
            args.tabComplete(actiontabs);

            String arg = args.remove();
            if (arg.equals("reset"))
            {
                actions.clear();
            }
            else
            {
                try
                {
                    actions.add(ActionEnum.valueOf(arg));
                } catch (IllegalArgumentException e)
                {
                    throw new TranslatedCommandException("Invalid Action");
                }
            }

        }
    }

    public void parseBlock(CommandParserArgs args) throws CommandException
    {
        while (!args.isEmpty() && !keywords.contains(args.peek()))
        {
            if (args.peek().equals("reset") && !args.isTabCompletion)
            {
                blocks.clear();
                args.remove();
            }
            else
            {
                if (args.isTabCompletion && "reset".startsWith(args.peek()) && args.size() == 1)
                    args.tabCompletion.add("reset");
                blocks.add(args.parseBlock());
            }
        }
    }

    public void parseBefore(CommandParserArgs args) throws CommandException
    {
        args.tabComplete("reset");
        if (!args.isEmpty())
        {
            if (args.peek().equals("reset"))
            {
                before = 0;
            }
            else
            {
                before = 0;
                while (!args.isEmpty() && !keywords.contains(args.peek()))
                    before += args.parseTimeReadable();
            }
        }
        else
            throw new TranslatedCommandException("A time must be specified here!");
    }
    public void parseAfter(CommandParserArgs args) throws CommandException
    {
        args.tabComplete("reset");
        if (!args.isEmpty())
        {
            if (args.peek().equals("reset"))
            {
                after = default_after;
            }
            else
            {
                after = 0;
                while (!args.isEmpty() && !keywords.contains(args.peek()))
                    after += args.parseTimeReadable();
            }
        }
            else
                throw new TranslatedCommandException("A time must be specified here!");
    }

    public void parseRange(CommandParserArgs args) throws CommandException
    {
        if (!args.isEmpty())
        {
            pickerRange = args.parseInt();
        }
        else
            throw new TranslatedCommandException("A integer must be specified here!");
    }
    public String toReadableString()
    {
        return "Before: " + before + "\nAfter: " + after + "\nActions: " + actions + "\nBlocks: " + blocks + "\nWhitelist: " + Awhitelist;
    }

}
