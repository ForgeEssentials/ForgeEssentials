package com.ForgeEssentials.commands;

import java.util.List;

import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.AreaSelector.Point;
import com.ForgeEssentials.util.AreaSelector.WorldPoint;

import cpw.mods.fml.common.FMLCommonHandler;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.ICommandSender;

public class CommandHome extends ForgeEssentialsCommandBase
{

	@Override
	public String getCommandName()
	{
		return "home";
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		if (args.length >= 1 && args[0].equals("here"))
			PlayerInfo.getPlayerInfo(sender).home = new WorldPoint((int) sender.posX, (int) sender.posY, (int) sender.posZ, sender.worldObj.getWorldInfo().getDimension());
		else if (args.length >= 3)
		{
			int x = 0;
			int y = 0;
			int z = 0;
			try
			{
				x = new Integer(args[0]);
			} catch (NumberFormatException e)
			{
				OutputHandler.chatError(sender, Localization.format(Localization.ERROR_NAN, args[0]));
				return;
			}
			try
			{
				y = new Integer(args[1]);
			} catch (NumberFormatException e)
			{
				OutputHandler.chatError(sender, Localization.format(Localization.ERROR_NAN, args[1]));
				return;
			}
			try
			{
				z = new Integer(args[2]);
			} catch (NumberFormatException e)
			{
				OutputHandler.chatError(sender, Localization.format(Localization.ERROR_NAN, args[2]));
				return;
			}
			PlayerInfo.getPlayerInfo(sender).home = new WorldPoint(x, y, z, sender.worldObj.getWorldInfo().getDimension());
		} else
		{
			Point home = PlayerInfo.getPlayerInfo(sender).home;
			if (home == null)
				OutputHandler.chatError(sender, Localization.get("message.error.nohome") + getSyntaxPlayer(sender));
			else
				((EntityPlayerMP) sender).playerNetServerHandler.setPlayerLocation(home.x, home.y, home.z, sender.rotationYaw, sender.rotationPitch);
		}
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
	public boolean canPlayerUseCommand(EntityPlayer player)
	{
		return true;
	}

	@Override
	public String getCommandPerm()
	{
		return "ForgeEssentials.BasicCommands." + getCommandName();
	}
	
	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] args)
    {
    	if(args.length == 1)
    	{
    		return getListOfStringsMatchingLastWord(args, "here");
    	}
    	else
    	{
    		return null;
    	}
    }
}
