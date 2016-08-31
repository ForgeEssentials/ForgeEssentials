package com.forgeessentials.playerlogger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import net.minecraft.block.Block;

import com.forgeessentials.api.UserIdent;
import com.forgeessentials.util.CommandParserArgs;

public class FilterConfig
{

    public enum ActionEnum
    {
        player,
        command,
        block,
        blockbreak,
        blockplace,
        explode,
        burn
    }

    public static HashMap<UserIdent,FilterConfig> perPlayerFilters = new HashMap<>();

    public static FilterConfig globalConfig = new FilterConfig();

    public HashSet<ActionEnum> actions = new HashSet<>();

    public HashSet<Block> blocks = new HashSet<>();

    public static HashSet<String> keywords = new HashSet<>();

    public static ArrayList<String> actiontabs = new ArrayList<>();

    static {
        keywords.add("action");
        keywords.add("blockid");
        keywords.add("before");
        keywords.add("after");

        ActionEnum[] enums = ActionEnum.values();

        for (ActionEnum ae : enums)
        {
            actiontabs.add(ae.name());
        }
        actiontabs.add("reset");
    }

    public final static long default_after = 365L*24*60*60*1000;
    public long before = 0;
    public long after = default_after;
    public void parse(CommandParserArgs args)
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
            case "blockid":
                parseBlock(args);
                break;
            case "before":
                parseBefore(args);
                break;
            case "after":
                parseAfter(args);
                break;
            default:
                throw new IllegalArgumentException("Expected Keyword here!");

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
    }
    public FilterConfig()
    {
    }

    public void parseActions(CommandParserArgs args)
    {
        while (!keywords.contains(args.peek()))
        {
            String arg = args.remove();

            args.tabComplete(actiontabs);

            if (arg.equals("reset"))
            {
                actions.clear();
            }
            else
            {
                actions.add(ActionEnum.valueOf(arg));
            }

        }
    }

    public void parseBlock(CommandParserArgs args)
    {
        while (!keywords.contains(args.peek()))
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

    public void parseBefore(CommandParserArgs args)
    {
        args.tabComplete("reset");
        if (args.peek().equals("reset"))
        {
            before = 0;
        }
        else
        {
            before = 0;
            while (!keywords.contains(args.peek()))
            {
                before += args.parseTimeReadable();
            }
        }
    }
    public void parseAfter(CommandParserArgs args)
    {
        args.tabComplete("reset");
        if (args.peek().equals("reset"))
        {
            after = default_after;
        }
        else
        {
            after = 0;
            while (!keywords.contains(args.peek()))
            {
                after += args.parseTimeReadable();
            }
        }
    }

}
