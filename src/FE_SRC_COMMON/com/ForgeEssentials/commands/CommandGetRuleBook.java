package com.ForgeEssentials.commands;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.Configuration;

import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.util.FEChatFormatCodes;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.OutputHandler;

public class CommandGetRuleBook extends ForgeEssentialsCommandBase
{
  
	public static ArrayList<String>	rules;
	public static File				rulesFile	= new File(ForgeEssentials.FEDIR, "rules.txt");

	@Override
	public void doConfig(Configuration config, String category)
	{
		rulesFile = new File(ForgeEssentials.FEDIR, config.get(category, "filename", "rules.txt").value);
		rules = loadRules();
	}

	public ArrayList<String> loadRules()
	{
		// Rules the rules file will be a flat strings file.. nothing special.
		ArrayList<String> rules = new ArrayList<String>();

		// somehow a new rules.txt is generated EVERY load.

		OutputHandler.info("Loading rules");
		if (!rulesFile.exists())
		{
			try
			{
				OutputHandler.info("No rules file found. Generating with default rules..");

				rulesFile.createNewFile();

				// create streams
				FileOutputStream stream = new FileOutputStream(rulesFile);
				OutputStreamWriter streamWriter = new OutputStreamWriter(stream);
				BufferedWriter writer = new BufferedWriter(streamWriter);

				writer.write("# " + rulesFile.getName() + " | numbers are automatically added");
				writer.newLine();

				writer.write("Obey the Admins");
				rules.add("Obey the Admins");
				writer.newLine();

				writer.write("Do not grief");
				rules.add("Do not grief");
				writer.newLine();

				writer.close();
				streamWriter.close();
				stream.close();

				OutputHandler.info("Completed generating rules file.");
			}
			catch (Exception e)
			{
				Logger lof = OutputHandler.felog;
				lof.logp(Level.SEVERE, "FEConfig", "Generating Rules", "Error writing the Rules file: " + rulesFile.getName(), e);
			}
		}
		else
		{
			try
			{
				OutputHandler.info("Rules file found. Reading...");

				FileInputStream stream = new FileInputStream(rulesFile);
				InputStreamReader streamReader = new InputStreamReader(stream);
				BufferedReader reader = new BufferedReader(streamReader);

				String read = reader.readLine();
				int counter = 0;

				while (read != null)
				{
					// ignore the comment things...
					if (read.startsWith("#"))
					{
						read = reader.readLine();
						continue;
					}

					// add to the rules list.
					rules.add(read);

					// read the next string
					read = reader.readLine();

					// increment counter
					counter++;
				}

				reader.close();
				streamReader.close();
				stream.close();

				OutputHandler.info("Completed reading rules file. " + counter + " rules read.");
			}
			catch (Exception e)
			{
				Logger lof = OutputHandler.felog;
				lof.logp(Level.SEVERE, "FEConfig", "Constructor-Rules", "Error reading or writing the Rules file: " + rulesFile.getName(), e);
			}
		}

		return rules;
	}
	
	@Override
	public String getCommandName()
	{
		return "getrulebook";
	}

	@Override
	public String[] getDefaultAliases()
	{
		return new String[]
		{ "rb"};
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		
		NBTTagCompound tag = new NBTTagCompound();
		NBTTagList pages = new NBTTagList();

		HashMap<String, String> map = new HashMap();

		for (int i = 0; i < rules.size(); i++)
		{	
				map.put(FEChatFormatCodes.UNDERLINE + "Rule #" + i + 1 + "\n\n", FEChatFormatCodes.RESET + FunctionHelper.formatColors(rules.get(i)));
		}

		SortedSet<String> keys = new TreeSet<String>(map.keySet());
		for (String name : keys)
		{
			pages.appendTag(new NBTTagString("", name + map.get(name)));
		}

		tag.setString("author", "ForgeEssentials");
		tag.setString("title", "RuleBook");
		tag.setTag("pages", pages);

		ItemStack is = new ItemStack(Item.writtenBook);
		is.setTagCompound(tag);
		sender.inventory.addItemStackToInventory(is);
	}

	public static String joinAliases(Object[] par0ArrayOfObj)
	{
		StringBuilder var1 = new StringBuilder();

		for (int var2 = 0; var2 < par0ArrayOfObj.length; ++var2)
		{
			String var3 = "/" + par0ArrayOfObj[var2].toString();

			if (var2 > 0)
			{
				var1.append(", ");
			}

			var1.append(var3);
		}

		return var1.toString();
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
