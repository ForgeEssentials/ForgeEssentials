package com.ForgeEssentials.commands;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;

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
			} catch (NumberFormatException e)
			{
				sender.sendChatToPlayer(Localization.format(Localization.ERROR_NAN, args[2]));
				return;
			}
			try
			{
				y = new Integer(args[1]);
			} catch (NumberFormatException e)
			{
				sender.sendChatToPlayer(Localization.format(Localization.ERROR_NAN, args[3]));
				return;
			}
			try
			{
				z = new Integer(args[2]);
			} catch (NumberFormatException e)
			{
				sender.sendChatToPlayer(Localization.format(Localization.ERROR_NAN, args[4]));
				return;
			}
			Point point = new Point(x, y, z);
			setSpawn(point, sender);
		}
		else
		{
			WarpPoint point = new WarpPoint(sender);
			setSpawn(point, sender);
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
			} catch (NumberFormatException e)
			{
				sender.sendChatToPlayer(Localization.format(Localization.ERROR_NAN, args[2]));
				return;
			}
			try
			{
				y = new Integer(args[1]);
			} catch (NumberFormatException e)
			{
				sender.sendChatToPlayer(Localization.format(Localization.ERROR_NAN, args[3]));
				return;
			}
			try
			{
				z = new Integer(args[2]);
			} catch (NumberFormatException e)
			{
				sender.sendChatToPlayer(Localization.format(Localization.ERROR_NAN, args[4]));
				return;
			}
			Point point = new Point(x, y, z);
			setSpawn(point, sender);
		} 
		else
		{
			sender.sendChatToPlayer(Localization.get(Localization.ERROR_BADSYNTAX));
		}
	}

	private void setSpawn(Point point, ICommandSender sender) 
	{
		NBTTagCompound spawn = new NBTTagCompound();
		spawn.setInteger("x", point.x);
		spawn.setInteger("x", point.x);
		spawn.setInteger("x", point.x);
		spawn.setInteger("dim", 0);
		if(point instanceof WarpPoint)
		{
			spawn.setFloat("pich", ((WarpPoint) point).pitch);
			spawn.setFloat("yaw", ((WarpPoint) point).yaw);
		}
		DataStorage.setData("spawn", spawn);
	}

	@Override
	public boolean canConsoleUseCommand()
	{
		return true;
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
		return null;
	}
	
	public static void sendToSpawn(EntityPlayer player)
	{
		System.out.println("CALLED 1");
		if (DataStorage.getData("spawn").hasKey("dim"))
		{
			System.out.println("CALLED 2");
			ChunkCoordinates var4 = ((EntityPlayerMP) player).getBedLocation();
			if (var4 == null)
			{
				System.out.println("CALLED 3");
				NBTTagCompound spawn = DataStorage.getData("spawn");
				Integer X = spawn.getInteger("x");
				Integer Y = spawn.getInteger("y");
				Integer Z = spawn.getInteger("z");
				Float yaw = spawn.getFloat("yaw");
				Float pitch = spawn.getFloat("pitch");
				Integer dim = spawn.getInteger("Dim");
				if (player.dimension!=dim) FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().transferPlayerToDimension(((EntityPlayerMP) player), dim);
				((EntityPlayerMP) player).playerNetServerHandler.setPlayerLocation(X, Y, Z, yaw, pitch);
			}
		}
	}
}
