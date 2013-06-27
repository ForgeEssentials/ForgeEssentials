package com.ForgeEssentials.scripting;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.Configuration;

import com.ForgeEssentials.api.APIRegistry;
import com.ForgeEssentials.api.permissions.IPermRegisterEvent;
import com.ForgeEssentials.api.permissions.RegGroup;
import com.ForgeEssentials.api.permissions.query.PermQueryPlayer;
import com.ForgeEssentials.commands.util.AFKdata;
import com.ForgeEssentials.commands.util.FEcmdModuleCommands;
import com.ForgeEssentials.commands.util.TickHandlerCommands;
import com.ForgeEssentials.util.FEChatFormatCodes;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;

public class CommandScriptBook extends FEcmdModuleCommands
{

  static List<String>	scripts			= new ArrayList<String>();
	public static File	commandscripts	= new File(ModuleScripting.moduleDir, "scripts/");

	@Override
	public String getCommandName()
	{
		return "scriptbook";
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		executeScript(sender);
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		EntityPlayerMP player = FunctionHelper.getPlayerForName(sender, args[0]);
		if (player != null)
		{
			executeScript(player);
		}
	}

	@Override
	public boolean canConsoleUseCommand()
	{
		return false;
	}

	@Override
	public String getCommandPerm()
	{
		return "ForgeEssentials.ModuleScripting." + getCommandName();
	}

	@Override
	public List<?> addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		return null;
	}

	@Override
	public RegGroup getReggroup()
	{
		return RegGroup.OWNERS;
	}

	public void executeScript(EntityPlayer player)
	{
		NBTTagCompound tag = new NBTTagCompound();
		NBTTagList pages = new NBTTagList();

		HashMap<String, String> map = new HashMap<String, String>();

		File[] listOfFiles = commandscripts.listFiles();

		for (File file : listOfFiles)
		{
			if (file.isFile())
			{
				List<String> lines = new ArrayList<String>();
				try
				{
					lines.add(FEChatFormatCodes.GREEN + "START" + FEChatFormatCodes.BLACK);
					lines.add("");
					FileInputStream stream = new FileInputStream(file);
					InputStreamReader streamReader = new InputStreamReader(stream);
					BufferedReader reader = new BufferedReader(streamReader);
					String line = reader.readLine();
					while (line != null)
					{
						while (line.length() > 21)
						{
							lines.add(line.substring(0, 20));
							line = line.substring(20);
						}
						lines.add(line);
						line = reader.readLine();
					}
					reader.close();
					streamReader.close();
					stream.close();
					lines.add("");
					lines.add(FEChatFormatCodes.RED + "END" + FEChatFormatCodes.BLACK);

				}
				catch (Exception e)
				{
					OutputHandler.felog.warning("Error reading script: " + file.getName());
				}
				int part = 0;
				int parts = lines.size() / 10 + 1;
				String filename = file.getName().replaceAll(".txt", "");
				if (filename.length() > 13)
					filename = filename.substring(0, 10) + "...";
				while (lines.size() != 0)
				{
					part++;
					String temp = "";
					for (int i = 0; i < 10 && lines.size() > 0; i++)
					{
						temp += lines.get(0) + "\n";
						lines.remove(0);
					}
					map.put(FEChatFormatCodes.GOLD + " Script: " + FEChatFormatCodes.GREY + filename + FEChatFormatCodes.DARKGREY + "\nPart " + part + " of " + parts + FEChatFormatCodes.BLACK + "\n\n", temp);
				}
			}
		}

		SortedSet<String> keys = new TreeSet<String>(map.keySet());
		for (String name : keys)
		{
			pages.appendTag(new NBTTagString("", name + map.get(name)));
		}

		tag.setString("author", "ForgeEssentials");
		tag.setString("title", "ScriptBook");
		tag.setTag("pages", pages);

		ItemStack is = new ItemStack(Item.writtenBook);
		is.setTagCompound(tag);
		player.inventory.addItemStackToInventory(is);
	}
}
