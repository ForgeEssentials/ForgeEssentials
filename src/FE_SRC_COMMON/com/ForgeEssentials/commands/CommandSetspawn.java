package com.ForgeEssentials.commands;

import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.util.DataStorage;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.AreaSelector.Point;
import com.ForgeEssentials.util.AreaSelector.WarpPoint;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;

import cpw.mods.fml.common.FMLCommonHandler;

public class CommandSetspawn extends ForgeEssentialsCommandBase
{

	@Override
	public String getCommandName()
	{
		return "setspawn";
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		if (args.length >= 3)
		{
			int x;
			int y;
			int z;
			int dim = sender.dimension;
			if(args.length == 4)
			{
				try
				{
					dim = Integer.parseInt(args[3]);
				}
				catch (NumberFormatException e)
				{
					sender.sendChatToPlayer(Localization.format(Localization.ERROR_NAN, args[3]));
					return;
				}
			}
			try
			{
				x = new Integer(args[0]);
			}
			catch (NumberFormatException e)
			{
				sender.sendChatToPlayer(Localization.format(Localization.ERROR_NAN, args[2]));
				return;
			}
			try
			{
				y = new Integer(args[1]);
			}
			catch (NumberFormatException e)
			{
				sender.sendChatToPlayer(Localization.format(Localization.ERROR_NAN, args[3]));
				return;
			}
			try
			{
				z = new Integer(args[2]);
			}
			catch (NumberFormatException e)
			{
				sender.sendChatToPlayer(Localization.format(Localization.ERROR_NAN, args[4]));
				return;
			}
//			Point point = new Point(x, y, z);
//			NBTTagCompound spawn = new NBTTagCompound();
//			spawn.setDouble("x", point.x);
//			spawn.setDouble("y", point.y);
//			spawn.setDouble("z", point.z);
//			spawn.setInteger("dim", dim);
//			DataStorage.setData("spawn", spawn);
			FMLCommonHandler.instance().getMinecraftServerInstance().worldServers[0].provider.setSpawnPoint(x, y, z);
			sender.sendChatToPlayer(Localization.get(Localization.SPAWNSET));
		}
		else
		{
//			WarpPoint point = new WarpPoint(sender);
//			NBTTagCompound spawn = new NBTTagCompound();
//			spawn.setDouble("x", point.x);
//			spawn.setDouble("y", point.y);
//			spawn.setDouble("z", point.z);
//			spawn.setInteger("dim", sender.dimension);
//			spawn.setFloat("pich", point.pitch);
//			spawn.setFloat("yaw", point.yaw);
//			DataStorage.setData("spawn", spawn);
//			DataStorage.save();
			FMLCommonHandler.instance().getMinecraftServerInstance().worldServers[0].provider.setSpawnPoint((int)sender.posX, (int)sender.posY, (int)sender.posZ);
			sender.sendChatToPlayer(Localization.get(Localization.SPAWNSET));
		}
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		if (args.length >= 3)
		{
			int x;
			int y;
			int z;
			try
			{
				x = new Integer(args[0]);
			}
			catch (NumberFormatException e)
			{
				sender.sendChatToPlayer(Localization.format(Localization.ERROR_NAN, args[2]));
				return;
			}
			try
			{
				y = new Integer(args[1]);
			}
			catch (NumberFormatException e)
			{
				sender.sendChatToPlayer(Localization.format(Localization.ERROR_NAN, args[3]));
				return;
			}
			try
			{
				z = new Integer(args[2]);
			}
			catch (NumberFormatException e)
			{
				sender.sendChatToPlayer(Localization.format(Localization.ERROR_NAN, args[4]));
				return;
			}
			Point point = new Point(x, y, z);
			NBTTagCompound spawn = new NBTTagCompound();
			spawn.setDouble("x", point.x);
			spawn.setDouble("y", point.y);
			spawn.setDouble("z", point.z);
			spawn.setInteger("dim", 0);
			DataStorage.setData("spawn", spawn);
			DataStorage.save();
			FMLCommonHandler.instance().getMinecraftServerInstance().worldServers[0].provider.setSpawnPoint(point.x, point.y, point.z);
			sender.sendChatToPlayer(Localization.get(Localization.SPAWNSET));
		}
		else
		{
			sender.sendChatToPlayer(Localization.get(Localization.ERROR_BADSYNTAX));
		}
	}

	@Override
	public boolean canConsoleUseCommand()
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
		return null;
	}
}
