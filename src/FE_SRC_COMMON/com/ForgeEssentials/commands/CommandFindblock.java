package com.ForgeEssentials.commands;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.Configuration;

import com.ForgeEssentials.api.permissions.RegGroup;
import com.ForgeEssentials.commands.util.FEcmdModuleCommands;
import com.ForgeEssentials.commands.util.TickTaskBlockFinder;
import com.ForgeEssentials.core.misc.FriendlyItemList;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;

public class CommandFindblock extends FEcmdModuleCommands
{
    public static int defaultRange = 20 * 16;
    public static final int defaultCount = 1;
    public static int defaultSpeed = 16 * 16;
    
    @Override
    public void doConfig(Configuration config, String category)
    {
        defaultRange = config.get(category, "defaultRange", defaultRange, "Default max distance used.").getInt();
        defaultSpeed = config.get(category, "defaultSpeed", defaultSpeed, "Default speed used.").getInt();
    }
    
    public String[] getDefaultAliases()
    {
        return new String[] {"fb"};
    }
    
	@Override
	public String getCommandName()
	{
		return "findblock";
	}

	/*
	 * syntax: /fb <block> [max distance, def = 20 * 16] [amount of blocks, def = 1] [speed, def = 10]
	 */
	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
	    if (args.length == 0)
	    {
	        OutputHandler.chatError(sender, Localization.get(Localization.ERROR_BADSYNTAX) + getCommandInfo(sender));
	        return;
	    }
	    int[] id = FunctionHelper.parseIdAndMetaFromString(args[0], true);
	    int range = (args.length < 2) ? defaultRange : parseIntWithMin(sender, args[1], 1);
	    int amount = (args.length < 3) ? defaultCount : parseIntWithMin(sender, args[2], 1);
	    int speed = (args.length < 4) ? defaultSpeed : parseIntWithMin(sender, args[3], 1);
	    
	    new TickTaskBlockFinder(sender, id, range, amount, speed);
	}
	
	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
	}

	@Override
	public boolean canConsoleUseCommand()
	{
		return false;
	}

	@Override
	public String getCommandPerm()
	{
		return "ForgeEssentials.BasicCommands." + getCommandName();
	}

	@Override
	public List<?> addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		if (args.length == 1)
		    return getListOfStringsFromIterableMatchingLastWord(args, FriendlyItemList.instance().getItemList());
		else if (args.length == 2)
		    return getListOfStringsMatchingLastWord(args, defaultRange + "");
		else if (args.length == 3)
            return getListOfStringsMatchingLastWord(args, defaultCount + "");
		else if (args.length == 4)
            return getListOfStringsMatchingLastWord(args, defaultSpeed + "");
		else
			return null;
	}

	@Override
	public RegGroup getReggroup()
	{
		return RegGroup.OWNERS;
	}
}