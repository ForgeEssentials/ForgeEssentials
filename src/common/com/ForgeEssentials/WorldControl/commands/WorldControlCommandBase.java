package com.ForgeEssentials.WorldControl.commands;

//Depreciated
import net.minecraft.src.ICommandSender;
import net.minecraft.src.TileEntityCommandBlock;

import com.ForgeEssentials.WorldControl.ModuleWorldControl;
import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;

public abstract class WorldControlCommandBase extends ForgeEssentialsCommandBase
{

	@Override
	public final String getCommandName()
	{
		if (ModuleWorldControl.useExtraSlash)
			return "/" + getName();
		else
			return getName();
	}

	public abstract String getName();

	@Override
	public String getSyntaxConsole()
	{
		// almost never called.
		return "/" + getCommandName();
	}

	@Override
	public String getInfoConsole()
	{
		// almost never called.
		return "";
	}

	@Override
	public String getSyntaxCommandBlock(TileEntityCommandBlock block)
	{
		// almost never called.
		return "/" + getCommandName();
	}

	@Override
	public void processCommandBlock(TileEntityCommandBlock block, String[] args)
	{
		// most probably never used.
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		sender.sendChatToPlayer("You cannot use the \"" + getCommandName() + "\" command from the console");
	}

	@Override
	public boolean canConsoleUseCommand()
	{
		return false;
	}

	public static int[] interpretIDAndMetaFromString(String msg)
	{
		int ID;
		int meta = -1;

		// try checking if its just an ID
		try
		{
			ID = Integer.parseInt(msg);
			return new int[] { ID, meta };
		}
		catch (NumberFormatException e)
		{
			// do nothing. continue checking.
		}

		// perhaps the ID:Meta format
		try
		{
			if (msg.contains(":"))
			{
				String[] pair = msg.split(":", 2);
				ID = Integer.parseInt(pair[0]);
				meta = Integer.parseInt(pair[1]);

				return new int[] { ID, meta };
			}
		}
		catch (NumberFormatException e)
		{
			// do nothing. continue checking.
		}

		// TODO: add name checking.

		return new int[] { 0, -1 };
	}

	public void completeCommand()
	{
	}
}
