package com.forgeessentials.playerlogger;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import com.forgeessentials.api.UserIdent;
import com.forgeessentials.core.commands.registration.FECommandParsingException;
import com.forgeessentials.util.CommandUtils;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.context.CommandContext;

import net.minecraft.world.level.block.Block;
import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.blocks.BlockStateArgument;

public class FilterConfig
{

    public enum ActionEnum
    {
        blockPlace, blockBreak, blockDetonate, blockUse_Left, blockUse_Right, blockBurn, command, playerLogin, playerLogout, playerRespawn, playerChangeDim, playerPosition, other
    }

    private static HashMap<UUID, FilterConfig> perPlayerFilters = new HashMap<>();

    public static FilterConfig globalConfig = new FilterConfig();

    private HashSet<ActionEnum> actions = new HashSet<>();

    public boolean hasAction(ActionEnum a)
    {
    	if(actions.isEmpty()) {
    		return true;
    	}
        return actions.contains(a);
    }

    private HashSet<Block> blocks = new HashSet<>();

    public boolean hasBlock(Block b)
    {
    	if(blocks.isEmpty()) {
    		return true;
    	}
        return blocks.contains(b);
    }

    public static HashSet<String> keywords = new HashSet<>();

    public static ArrayList<String> actiontabs = new ArrayList<>();

    public int pickerRange = 0;

    public UserIdent player;

    static
    {
        keywords.add("action");
        keywords.add("blockid");
        keywords.add("before");
        keywords.add("after");
        keywords.add("range");
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
        catch (CommandRuntimeException ignored)
        {

        }
    }

    public static FilterConfig getDefaultPlayerConfig(UserIdent ident)
    {
        if (perPlayerFilters.containsKey(ident.getUuid()))
            return perPlayerFilters.get(ident.getUuid());
        return null;
    }

    public final static long default_after = 365L * 24 * 60 * 60 * 1000;
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

    public void parse(CommandContext<CommandSourceStack> ctx, List<String> args) throws CommandRuntimeException
    {
        if (args != null)
        {
            // while (!(args.length==0))
            {
                // args.tabComplete(keywords);
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
                case "player":
                    try
                    {
                        player = CommandUtils.parsePlayer(args.remove(0), true, false);
                        ChatOutputHandler.chatConfirmation(ctx.getSource(), "Set Player To: " + player.getUsername());
                    }
                    catch (FECommandParsingException e)
                    {
                        ChatOutputHandler.chatError(ctx.getSource(), e.error);
                        return;
                    }
                    break;
                default:
                    ChatOutputHandler.chatError(ctx.getSource(), "Expected Keyword here!");
                    return;

                }

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
        player = c.player;
        pickerRange = c.pickerRange;
    }

    public FilterConfig()
    {
    }

    public void parseActions(CommandContext<CommandSourceStack> ctx, List<String> args) throws CommandRuntimeException
    {
        // while (!args.isEmpty() && !keywords.contains(args.peek()))
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
                    ChatOutputHandler.chatConfirmation(ctx.getSource(), "Added Action: " + arg);
                }
                catch (IllegalArgumentException e)
                {
                    ChatOutputHandler.chatError(ctx.getSource(), "Invalid Action");
                    return;
                }
            }

        }
    }

    public void parseBlock(CommandContext<CommandSourceStack> ctx, List<String> args) throws CommandRuntimeException
    {
        // while (!args.isEmpty() && !keywords.contains(args.peek()))
        {
            if (args.get(0).equals("reset"))
            {
                blocks.clear();
                args.remove(0);
            }
            else
            {
                blocks.add(BlockStateArgument.getBlock(ctx, "block").getState().getBlock());
                ChatOutputHandler.chatConfirmation(ctx.getSource(), "Added Block: "
                        + BlockStateArgument.getBlock(ctx, "block").getState().getBlock().getRegistryName());
            }
        }
    }

    public void parseBefore(CommandContext<CommandSourceStack> ctx, List<String> args) throws CommandRuntimeException
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
                try
                {
                    before += CommandUtils.parseTimeReadable(args.remove(0));
                    ChatOutputHandler.chatConfirmation(ctx.getSource(), "Set Before To: " + before);
                }
                catch (FECommandParsingException e)
                {
                    ChatOutputHandler.chatError(ctx.getSource(), e.error);
                    return;
                }
            }
        }
        else
        {
            ChatOutputHandler.chatError(ctx.getSource(), "A time must be specified here!");
            return;
        }
    }

    public void parseAfter(CommandContext<CommandSourceStack> ctx, List<String> args) throws CommandRuntimeException
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
                try
                {
                    after += CommandUtils.parseTimeReadable(args.remove(0));
                    ChatOutputHandler.chatConfirmation(ctx.getSource(), "Set After To: " + after);
                }
                catch (FECommandParsingException e)
                {
                    ChatOutputHandler.chatError(ctx.getSource(), e.error);
                    return;
                }
            }
        }
        else
        {
            ChatOutputHandler.chatError(ctx.getSource(), "A time must be specified here!");
            return;
        }
    }

    public void parseRange(CommandContext<CommandSourceStack> ctx, List<String> args) throws CommandRuntimeException
    {
        if (!args.isEmpty())
        {
            pickerRange = CommandUtils.parseInt(args.remove(0));
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "Set PickerRange To: " + pickerRange);
        }
        else
        {
            ChatOutputHandler.chatError(ctx.getSource(), "A integer must be specified here!");
            return;
        }
    }

    public static void setPerPlayerFilters(UserIdent user, FilterConfig PlayerFilter)
    {
        FilterConfig.perPlayerFilters.put(user.getUsernameUuid(), PlayerFilter);
    }

    public String toReadableString()
    {
        return "Before: " + before + "\nAfter: " + after + "\nActions: " + actions + "\nBlocks: " + blocks
                + "\nPickerRange: " + pickerRange;
    }

}
