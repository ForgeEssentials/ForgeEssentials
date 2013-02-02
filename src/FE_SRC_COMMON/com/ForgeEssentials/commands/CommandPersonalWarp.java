package com.ForgeEssentials.commands;

import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.PWarp;
import com.ForgeEssentials.util.TeleportCenter;
import com.ForgeEssentials.util.Warp;
import com.ForgeEssentials.util.AreaSelector.WarpPoint;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.TcpConnection;
import net.minecraft.server.MinecraftServer;

import net.minecraftforge.common.DimensionManager;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

import cpw.mods.fml.common.FMLCommonHandler;

public class CommandPersonalWarp extends ForgeEssentialsCommandBase
{
	@Override
	public String getCommandName()
	{
		return "personalwarp";
	}

	@Override
	public String[] getDefaultAliases()
	{
		return new String[] {"pw"};
	}
	
	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		HashMap<String, PWarp> map = TeleportCenter.pwMap.get(sender.username);
		
		if(args.length != 2)
		{
			String msg = "Warp list: ";
			for(String name : map.keySet())
			{
				msg = msg + ", " + name;
			}
			sender.sendChatToPlayer(msg);
		}
		else
		{
			if(args[0].equalsIgnoreCase("goto"))
			{
				if(map.containsKey(args[1]))
				{
					PWarp warp = map.get(args[1]);
					PlayerInfo playerInfo = PlayerInfo.getPlayerInfo(sender.username);
					playerInfo.back = new WarpPoint(sender);
					TeleportCenter.addToTpQue(warp.getPoint(), sender);
				}
				else
				{
					sender.sendChatToPlayer("PW does not exist.");
				}
			}
			else if(args[0].equalsIgnoreCase("add"))
			{
				if(!map.containsKey(args[1]))
				{
					map.put(args[1], new PWarp(sender.username, args[1], new WarpPoint(sender)));
					sender.sendChatToPlayer("PW added.");
				}
				else
				{
					sender.sendChatToPlayer("PW already exists.");
				}
			}
			else if(args[0].equalsIgnoreCase("remove"))
			{
				if(map.containsKey(args[1]))
				{
					ModuleCommands.data.deleteObject(PWarp.class, map.get(args[1]).getFilename());
					map.remove(args[1]);
					sender.sendChatToPlayer("PW removed.");
				}
				else
				{
					sender.sendChatToPlayer("PW does not exist.");
				}
			}
		}
		TeleportCenter.pwMap.put(sender.username, map);
		
		ModuleCommands.saveWarps();
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args) {}

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
	public List addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		if(args.length == 1)
		{
			return getListOfStringsMatchingLastWord(args, "goto", "add", "remove");
		}
		if(args.length == 2)
		{
			if(TeleportCenter.pwMap.get(sender.getCommandSenderName()) == null) TeleportCenter.pwMap.put(sender.getCommandSenderName(), new HashMap<String, PWarp>());
			return this.getListOfStringsFromIterableMatchingLastWord(args, TeleportCenter.pwMap.get(sender.getCommandSenderName()).keySet());
		}
		return null;
	}
}
