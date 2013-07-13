package com.ForgeEssentials.core.commands.selections;

//Depreciated
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.AreaSelector.Point;

public class CommandExpand extends ForgeEssentialsCommandBase
{

	public CommandExpand()
	{
		return;
	}

	@Override
	public String getCommandName()
	{
		return "/expand";
	}

	@Override
	public void processCommandPlayer(EntityPlayer player, String[] args)
	{
		if (args.length == 1)
		{
			int x = Math.round((float) player.getLookVec().xCoord);
			int y = Math.round((float) player.getLookVec().yCoord);
			int z = Math.round((float) player.getLookVec().zCoord);
			PlayerInfo info = PlayerInfo.getPlayerInfo(player.username);
			int expandby = Integer.decode(args[0]);

			// Check to see if selection is valid for expand.
			if (info.getPoint1() == null || info.getPoint2() == null)
			{
				player.sendChatToPlayer("Invalid previous selection.");
				return;
			}

			if (x == -1)
			{
				if (info.getPoint1().x < info.getPoint2().x)
				{
					info.setPoint1(new Point(info.getPoint1().x - expandby, info.getPoint1().y, info.getPoint1().z));
				}
				else
				{
					info.setPoint2(new Point(info.getPoint2().x - expandby, info.getPoint2().y, info.getPoint2().z));
				}
			}
			else if (z == 1)
			{
				if (info.getPoint1().z < info.getPoint2().z)
				{
					info.setPoint1(new Point(info.getPoint1().x, info.getPoint1().y, info.getPoint1().z + expandby));
				}
				else
				{
					info.setPoint2(new Point(info.getPoint2().x, info.getPoint2().y, info.getPoint2().z + expandby));
				}
			}
			else if (x == 1)
			{
				if (info.getPoint1().x < info.getPoint2().x)
				{
					info.setPoint1(new Point(info.getPoint1().x + expandby, info.getPoint1().y, info.getPoint1().z));
				}
				else
				{
					info.setPoint2(new Point(info.getPoint2().x + expandby, info.getPoint2().y, info.getPoint2().z));
				}
			}
			else if (z == -1)
			{
				if (info.getPoint1().z < info.getPoint2().z)
				{
					info.setPoint1(new Point(info.getPoint1().x, info.getPoint1().y, info.getPoint1().z - expandby));
				}
				else
				{
					info.setPoint2(new Point(info.getPoint2().x, info.getPoint2().y, info.getPoint2().z - expandby));
				}
			}
			else if (y == 1)
			{
				if (info.getPoint1().y > info.getPoint2().y)
				{
					info.setPoint1(new Point(info.getPoint1().x, info.getPoint1().y + expandby, info.getPoint1().z));
				}
				else
				{
					info.setPoint2(new Point(info.getPoint2().x, info.getPoint2().y + expandby, info.getPoint2().z));
				}
			}
			else if (y == -1)
			{
				if (info.getPoint1().y < info.getPoint2().y)
				{
					info.setPoint1(new Point(info.getPoint1().x, info.getPoint1().y - expandby, info.getPoint1().z));
				}
				else
				{
					info.setPoint2(new Point(info.getPoint2().x, info.getPoint2().y - expandby, info.getPoint2().z));
				}
			}
			player.sendChatToPlayer("Region expanded by: " + expandby);
			return;
		}
		else if (args.length == 2)
		{
			PlayerInfo info = PlayerInfo.getPlayerInfo(player.username);
			int expandby = 0;
			try
			{
				expandby = Integer.decode(args[0]);
			}
			catch (Exception e)
			{
				try
				{
					expandby = Integer.decode(args[1]);
				}
				catch (Exception ex)
				{
					OutputHandler.chatError(player, "Neither " + args[0] + " or " + args[1] + " is a number.");
					return;
				}
			}
			if (args[0].equalsIgnoreCase("north") || args[1].equalsIgnoreCase("north"))
			{
				if (info.getPoint1().z < info.getPoint2().z)
				{
					info.setPoint1(new Point(info.getPoint1().x, info.getPoint1().y, info.getPoint1().z - expandby));
				}
				else
				{
					info.setPoint2(new Point(info.getPoint2().x, info.getPoint2().y, info.getPoint2().z - expandby));
				}
			}
			else if (args[0].equalsIgnoreCase("east") || args[1].equalsIgnoreCase("east"))
			{
				if (info.getPoint1().x > info.getPoint2().x)
				{
					info.setPoint1(new Point(info.getPoint1().x + expandby, info.getPoint1().y, info.getPoint1().z));
				}
				else
				{
					info.setPoint2(new Point(info.getPoint2().x + expandby, info.getPoint2().y, info.getPoint2().z));
				}
			}
			else if (args[0].equalsIgnoreCase("south") || args[1].equalsIgnoreCase("south"))
			{
				if (info.getPoint1().z > info.getPoint2().z)
				{
					info.setPoint1(new Point(info.getPoint1().x, info.getPoint1().y, info.getPoint1().z + expandby));
				}
				else
				{
					info.setPoint2(new Point(info.getPoint2().x, info.getPoint2().y, info.getPoint2().z + expandby));
				}
			}
			else if (args[0].equalsIgnoreCase("west") || args[1].equalsIgnoreCase("west"))
			{
				if (info.getPoint1().x < info.getPoint2().x)
				{
					info.setPoint1(new Point(info.getPoint1().x - expandby, info.getPoint1().y, info.getPoint1().z));
				}
				else
				{
					info.setPoint2(new Point(info.getPoint2().x - expandby, info.getPoint2().y, info.getPoint2().z));
				}
			}
			else if (args[0].equalsIgnoreCase("up") || args[1].equalsIgnoreCase("up"))
			{
				if (info.getPoint1().z > info.getPoint2().z)
				{
					info.setPoint1(new Point(info.getPoint1().x, info.getPoint1().y + expandby, info.getPoint1().z));
				}
				else
				{
					info.setPoint2(new Point(info.getPoint2().x, info.getPoint2().y + expandby, info.getPoint2().z));
				}
			}
			else if (args[0].equalsIgnoreCase("down") || args[1].equalsIgnoreCase("down"))
			{
				if (info.getPoint1().y < info.getPoint2().y)
				{
					info.setPoint1(new Point(info.getPoint1().x, info.getPoint1().y - expandby, info.getPoint1().z));
				}
				else
				{
					info.setPoint2(new Point(info.getPoint2().x, info.getPoint2().y - expandby, info.getPoint2().z));
				}
			}
			else
			{
				OutputHandler.chatError(player, "Invalid Direction");
			}
			player.sendChatToPlayer("Region expanded by: " + expandby);
			return;
		}
		else
		{
			error(player);
		}
	}

	@Override
	public String getSyntaxPlayer(EntityPlayer player)
	{
		return "/" + getCommandName() + "[direction] <number of blocks to expand>";
	}

	@Override
	public String getInfoPlayer(EntityPlayer player)
	{
		return "Expands the currently selected area.";
	}

	@Override
	public String getCommandPerm()
	{
		return "ForgeEssentials.CoreCommands.select.pos";
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
	public List<?> addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		return getListOfStringsMatchingLastWord(args, new String[]
		{ "North", "South", "East", "West", "Up", "Down" });
	}

}
