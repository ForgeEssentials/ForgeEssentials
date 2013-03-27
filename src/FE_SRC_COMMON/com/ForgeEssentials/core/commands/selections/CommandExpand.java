package com.ForgeEssentials.core.commands.selections;

//Depreciated
import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;

import com.ForgeEssentials.api.permissions.PermissionsAPI;
import com.ForgeEssentials.api.permissions.query.PermQueryPlayerArea;
import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.Localization;
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
		if (args.length == 2)
		{
			PlayerInfo info = PlayerInfo.getPlayerInfo(player.username);
			int expandby = Integer.decode(args[1]);
			if(args[0].equalsIgnoreCase("north")){
					if (info.getPoint1().x < info.getPoint2().x) {
						info.setPoint1(new Point(info.getPoint1().x - expandby, info.getPoint1().y, info.getPoint1().z));
						return;
					}
					else {
						info.setPoint2(new Point(info.getPoint2().x - expandby, info.getPoint2().y, info.getPoint2().z));
						return;
					}
			}
			else if(args[0].equalsIgnoreCase("west")){
					if (info.getPoint1().z < info.getPoint2().z) {
						info.setPoint1(new Point(info.getPoint1().x, info.getPoint1().y, info.getPoint1().z + expandby));
						return;
					}
					else {
						info.setPoint2(new Point(info.getPoint2().x, info.getPoint2().y, info.getPoint2().z + expandby));
						return;
					}
			}
			else if(args[0].equalsIgnoreCase("south")){
					if (info.getPoint1().x < info.getPoint2().x) {
						info.setPoint1(new Point(info.getPoint1().x + expandby, info.getPoint1().y, info.getPoint1().z));
						return;
					}
					else {
						info.setPoint2(new Point(info.getPoint2().x + expandby, info.getPoint2().y, info.getPoint2().z));
						return;
					}
			}
			else if(args[0].equalsIgnoreCase("east")){
					if (info.getPoint1().z < info.getPoint2().z) {
						info.setPoint1(new Point(info.getPoint1().x, info.getPoint1().y, info.getPoint1().z - expandby));
						return;
					}
					else {
						info.setPoint2(new Point(info.getPoint2().x, info.getPoint2().y, info.getPoint2().z - expandby));
						return;
					}
			}
			else if(args[0].equalsIgnoreCase("up")){
				if (info.getPoint1().z > info.getPoint2().z) {
					info.setPoint1(new Point(info.getPoint1().x, info.getPoint1().y + expandby, info.getPoint1().z));
					return;
				}
				else {
					info.setPoint2(new Point(info.getPoint2().x, info.getPoint2().y  + expandby, info.getPoint2().z));
					return;
				}
		}
			else if(args[0].equalsIgnoreCase("down")){
				if (info.getPoint1().y < info.getPoint2().y) {
					info.setPoint1(new Point(info.getPoint1().x, info.getPoint1().y - expandby, info.getPoint1().z));
					return;
				}
				else {
					info.setPoint2(new Point(info.getPoint2().x, info.getPoint2().y - expandby, info.getPoint2().z));
					return;
				}
		}
		}
		else
		{
			error(player);
		}
	}

	@Override
	public String getSyntaxPlayer(EntityPlayer player)
	{
		return "/" + getCommandName() + "<direction> <number of blocks to expand>";
	}

	@Override
	public String getInfoPlayer(EntityPlayer player)
	{
		return "/expand Selection Positions";
	}

	@Override
	public String getCommandPerm()
	{
		return "ForgeEssentials.BasicCommands.pos";
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
