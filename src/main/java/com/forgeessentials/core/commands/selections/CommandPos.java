package com.forgeessentials.core.commands.selections;

//Depreciated
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.query.PermQueryPlayerArea;
import com.forgeessentials.core.PlayerInfo;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.Localization;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.AreaSelector.Point;

public class CommandPos extends ForgeEssentialsCommandBase
{
	private int	type;

	public CommandPos(int type)
	{
		this.type = type;
	}

	@Override
	public String getCommandName()
	{
		return "fepos" + type;
	}

	@Override
	public void processCommandPlayer(EntityPlayer player, String[] args)
	{
		int x, y, z;

		if (args.length == 1)
		{
			if (args[0].toLowerCase().equals("here"))
			{
				x = (int) player.posX;
				y = (int) player.posY;
				z = (int) player.posZ;

				if (type == 1)
				{
					PlayerInfo.getPlayerInfo(player.username).setPoint1(new Point(x, y, z));
				}
				else
				{
					PlayerInfo.getPlayerInfo(player.username).setPoint2(new Point(x, y, z));
				}

				OutputHandler.chatConfirmation(player, "Pos" + type + " set to " + x + ", " + y + ", " + z);
				return;

			}
			else
			{
				error(player);
				return;
			}
		}

		if (args.length > 0)
		{
			if (args.length < 3)
			{
				error(player);
				return;
			}

			try
			{
				x = Integer.parseInt(args[0]);
				y = Integer.parseInt(args[1]);
				z = Integer.parseInt(args[2]);
			}
			catch (NumberFormatException e)
			{
				error(player);
				return;
			}

			if (type == 1)
			{
				PlayerInfo.getPlayerInfo(player.username).setPoint1(new Point(x, y, z));
			}
			else
			{
				PlayerInfo.getPlayerInfo(player.username).setPoint2(new Point(x, y, z));
			}

			OutputHandler.chatConfirmation(player, "Pos" + type + " set to " + x + ", " + y + ", " + z);
			return;
		}

		MovingObjectPosition mop = FunctionHelper.getPlayerLookingSpot(player, true);

		if (mop == null)
		{
			OutputHandler.chatError(player, Localization.get(Localization.ERROR_TARGET));
			return;
		}

		x = mop.blockX;
		y = mop.blockY;
		z = mop.blockZ;

		Point point = new Point(x, y, z);
		if (!APIRegistry.perms.checkPermAllowed(new PermQueryPlayerArea(player, getCommandPerm(), point)))
		{
			OutputHandler.chatError(player, Localization.get(Localization.ERROR_PERMDENIED));
			return;
		}

		if (type == 1)
		{
			PlayerInfo.getPlayerInfo(player.username).setPoint1(point);
		}
		else
		{
			PlayerInfo.getPlayerInfo(player.username).setPoint2(point);
		}

		OutputHandler.chatConfirmation(player, "Pos" + type + " set to " + x + ", " + y + ", " + z);
		return;
	}

	@Override
	public String getSyntaxPlayer(EntityPlayer player)
	{
		return "/" + getCommandName() + " [<x> <y> <z] or [here]";
	}

	@Override
	public String getInfoPlayer(EntityPlayer player)
	{
		return "set Selection Positions";
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
		return null;
	}

	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

}
