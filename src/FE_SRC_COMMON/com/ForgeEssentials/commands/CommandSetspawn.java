package com.ForgeEssentials.commands;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.util.DataStorage;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.AreaSelector.Point;
import com.ForgeEssentials.util.AreaSelector.WarpPoint;

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
			FMLCommonHandler.instance().getMinecraftServerInstance().worldServers[0].provider.setSpawnPoint(point.getX(), point.getY(), point.getZ());
			sender.sendChatToPlayer(Localization.get(Localization.SPAWNSET));
		}
		else
		{
			WarpPoint point = new WarpPoint(sender);
			NBTTagCompound spawn = new NBTTagCompound();
			spawn.setDouble("x", point.x);
			spawn.setDouble("y", point.y);
			spawn.setDouble("z", point.z);
			spawn.setInteger("dim", 0);
			spawn.setFloat("pich", point.pitch);
			spawn.setFloat("yaw", point.yaw);
			DataStorage.setData("spawn", spawn);
			DataStorage.save();
			FMLCommonHandler.instance().getMinecraftServerInstance().worldServers[0].provider.setSpawnPoint(point.getX(), point.getY(), point.getZ());
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
			FMLCommonHandler.instance().getMinecraftServerInstance().worldServers[0].provider.setSpawnPoint(point.getX(), point.getY(), point.getZ());
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
