package com.ForgeEssentials.WorldControl.commands;

//Depreciated
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;

import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.permission.PermissionsAPI;
import com.ForgeEssentials.permission.query.PermQueryPlayerArea;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.AreaSelector.Point;

public class CommandPos extends WorldControlCommandBase
{
	private int type;

	public CommandPos(int type)
	{
		super(true);
		this.type = type;
	}

	@Override
	public String getName()
	{
		return "pos" + type;
	}

	@Override
	public void processCommandPlayer(EntityPlayer player, String[] args)
	{
		int x, y, z;

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
				PlayerInfo.getPlayerInfo(player).setPoint1(new Point(x, y, z));
			}
			else
			{
				PlayerInfo.getPlayerInfo(player).setPoint2(new Point(x, y, z));
			}

			OutputHandler.chatConfirmation(player, "Pos" + type + " set to " + x + ", " + y + ", " + z);
			return;
		}

		MovingObjectPosition mop = FunctionHelper.getPlayerLookingSpot(player, true);

		if (mop == null)
		{
			OutputHandler.chatError(player, Localization.ERROR_TARGET);
			return;
		}

		x = mop.blockX;
		y = mop.blockY;
		z = mop.blockZ;

		Point point = new Point(x, y, z);
		if (!PermissionsAPI.checkPermAllowed(new PermQueryPlayerArea(player, getCommandPerm(), point)))
		{
			OutputHandler.chatError(player, Localization.get(Localization.ERROR_PERMDENIED));
			return;
		}

		if (type == 1)
		{
			PlayerInfo.getPlayerInfo(player).setPoint1(point);
		}
		else
		{
			PlayerInfo.getPlayerInfo(player).setPoint2(point);
		}

		OutputHandler.chatConfirmation(player, "Pos" + type + " set to " + x + ", " + y + ", " + z);
		return;
	}

	@Override
	public String getSyntaxPlayer(EntityPlayer player)
	{
		return "/" + getCommandName() + " [<x> <y> <z]";
	}

	@Override
	public String getInfoPlayer(EntityPlayer player)
	{
		return "set Selection Positions";
	}

	@Override
	public String getCommandPerm()
	{
		return "WorldControl.commands.pos";
	}

}
