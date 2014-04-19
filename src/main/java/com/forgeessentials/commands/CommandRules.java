package com.forgeessentials.commands;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.Configuration;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.IPermRegisterEvent;
import com.forgeessentials.api.permissions.RegGroup;
import com.forgeessentials.api.permissions.query.PermQueryPlayer;
import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.util.ChatUtils;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.OutputHandler;

public class CommandRules extends FEcmdModuleCommands
{

	public static final String[]	autocomargs	=
												{ "add", "remove", "move", "change", "book" };
	public static ArrayList<String>	rules;
	public static File				rulesFile	= new File(ForgeEssentials.FEDIR, "rules.txt");

	@Override
	public void doConfig(Configuration config, String category)
	{
		rulesFile = new File(ForgeEssentials.FEDIR, config.get(category, "filename", "rules.txt").getString());
		rules = loadRules();
	}

	public ArrayList<String> loadRules()
	{
		ArrayList<String> rules = new ArrayList<String>();

		OutputHandler.felog.info("Loading rules");
		if (!rulesFile.exists())
		{
			try
			{
				OutputHandler.felog.info("No rules file found. Generating with default rules..");

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

				OutputHandler.felog.info("Completed generating rules file.");
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
				OutputHandler.felog.info("Rules file found. Reading...");

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

				OutputHandler.felog.info("Completed reading rules file. " + counter + " rules read.");
			}
			catch (Exception e)
			{
				Logger lof = OutputHandler.felog;
				lof.logp(Level.SEVERE, "FEConfig", "Constructor-Rules", "Error reading or writing the Rules file: " + rulesFile.getName(), e);
			}
		}

		return rules;
	}

	public void saveRules()
	{
		try
		{
			OutputHandler.felog.info("Saving rules");

			if (!rulesFile.exists())
			{
				rulesFile.createNewFile();
			}

			// create streams
			FileOutputStream stream = new FileOutputStream(rulesFile);
			OutputStreamWriter streamWriter = new OutputStreamWriter(stream);
			BufferedWriter writer = new BufferedWriter(streamWriter);

			writer.write("# " + rulesFile.getName() + " | numbers are automatically added");
			writer.newLine();

			for (String rule : rules)
			{
				writer.write(rule);
				writer.newLine();
			}

			writer.close();
			streamWriter.close();
			stream.close();

			OutputHandler.felog.info("Completed saving rules file.");
		}
		catch (Exception e)
		{
			Logger lof = OutputHandler.felog;
			lof.logp(Level.SEVERE, "FEConfig", "Saving Rules", "Error writing the Rules file: " + rulesFile.getName(), e);
		}
	}

	@Override
	public String getCommandName()
	{
		return "rules";
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		if (args.length == 0)
		{
			for (String rule : rules)
			{
				ChatUtils.sendMessage(sender, rule);
			}
			return;
		}
		if (args[0].equalsIgnoreCase("book"))
		{
			NBTTagCompound tag = new NBTTagCompound();
			NBTTagList pages = new NBTTagList();

			HashMap<String, String> map = new HashMap<String, String>();

			for (int i = 0; i < rules.size(); i++)
			{
				map.put(EnumChatFormatting.UNDERLINE + "Rule #" + (i + 1) + "\n\n", EnumChatFormatting.RESET + FunctionHelper.formatColors(rules.get(i)));
			}

			SortedSet<String> keys = new TreeSet<String>(map.keySet());
			for (String name : keys)
			{
				pages.appendTag(new NBTTagString("", name + map.get(name)));
			}

			tag.setString("author", "ForgeEssentials");
			tag.setString("title", "Rule Book");
			tag.setTag("pages", pages);

			ItemStack is = new ItemStack(Item.writtenBook);
			is.setTagCompound(tag);
			sender.inventory.addItemStackToInventory(is);
			return;
		}
		if (args.length == 1)
		{
			if (args[0].equalsIgnoreCase("help"))
			{
				OutputHandler.chatConfirmation(sender, " - /rules [#]");
				if (APIRegistry.perms.checkPermAllowed(new PermQueryPlayer(sender, getCommandPerm() + ".edit")))
				{
					OutputHandler.chatConfirmation(sender, " - /rules &lt;#> [changedRule]");
					OutputHandler.chatConfirmation(sender, " - /rules add &lt;newRule>");
					OutputHandler.chatConfirmation(sender, " - /rules remove &lt;#>");
					OutputHandler.chatConfirmation(sender, " - /rules move &lt;#> &lt;#>");
				}
				return;
			}

			ChatUtils.sendMessage(sender, rules.get(parseIntBounded(sender, args[0], 1, rules.size()) - 1));
			return;
		}

		if (!APIRegistry.perms.checkPermAllowed(new PermQueryPlayer(sender, getCommandPerm() + ".edit")))
		{
			OutputHandler.chatError(sender, "You have insufficient permission to do that. If you believe you received this message in error, please talk to a server admin.");
			return;
		}

		int index;

		if (args[0].equalsIgnoreCase("remove"))
		{
			index = parseIntBounded(sender, args[1], 1, rules.size());

			rules.remove(index - 1);
			OutputHandler.chatConfirmation(sender, String.format("Rule # %s removed", args[1]));
		}
		else if (args[0].equalsIgnoreCase("add"))
		{
			String newRule = "";
			for (int i = 1; i < args.length; i++)
			{
				newRule = newRule + args[i] + " ";
			}
			newRule = FunctionHelper.formatColors(newRule);
			rules.add(newRule);
			OutputHandler.chatConfirmation(sender, String.format("Rule added as # %s.", args[1]));
		}
		else if (args[0].equalsIgnoreCase("move"))
		{
			index = parseIntBounded(sender, args[1], 1, rules.size());

			String temp = rules.remove(index - 1);

			index = parseIntWithMin(sender, args[2], 1);

			if (index < rules.size())
			{
				rules.add(index - 1, temp);
				OutputHandler.chatConfirmation(sender, String.format("Rule # %1$s moved to # %2$s", args[1], args[2]));
			}
			else
			{
				rules.add(temp);
				OutputHandler.chatConfirmation(sender, String.format("Rule # %1$s moved to last position.", args[1]));
			}
		}
		else if (args[0].equalsIgnoreCase("change"))
		{
			index = parseIntBounded(sender, args[1], 1, rules.size());

			String newRule = "";
			for (int i = 2; i < args.length; i++)
			{
				newRule = newRule + args[i] + " ";
			}
			newRule = FunctionHelper.formatColors(newRule);
			rules.set(index - 1, newRule);
			OutputHandler.chatConfirmation(sender, String.format("Rules # %1$s changed to '%2$s'.", index + "", newRule));
		}
		else
		{
			error(sender);
		}
		saveRules();
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		if (args.length == 0)
		{
			for (String rule : rules)
			{
				ChatUtils.sendMessage(sender, rule);
			}
			return;
		}
		if (args.length == 1)
		{
			if (args[0].equalsIgnoreCase("help"))
			{
				OutputHandler.chatConfirmation(sender, " - /rules [#]");
				OutputHandler.chatConfirmation(sender, " - /rules &lt;#> [changedRule]");
				OutputHandler.chatConfirmation(sender, " - /rules add &lt;newRule>");
				OutputHandler.chatConfirmation(sender, " - /rules remove &lt;#>");
				OutputHandler.chatConfirmation(sender, " - /rules move &lt;#> &lt;#>");

			}

			ChatUtils.sendMessage(sender, rules.get(parseIntBounded(sender, args[0], 1, rules.size()) - 1));
			return;
		}

		int index;

		if (args[0].equalsIgnoreCase("remove"))
		{
			index = parseIntBounded(sender, args[1], 1, rules.size());

			rules.remove(index - 1);
			OutputHandler.chatConfirmation(sender, String.format("Rule # %s removed", args[1]));
		}
		else if (args[0].equalsIgnoreCase("add"))
		{
			String newRule = "";
			for (int i = 1; i < args.length; i++)
			{
				newRule = newRule + args[i] + " ";
			}
			newRule = FunctionHelper.formatColors(newRule);
			rules.add(newRule);
			OutputHandler.chatConfirmation(sender, String.format("Rule added as # %s.", args[1]));
		}
		else if (args[0].equalsIgnoreCase("move"))
		{
			index = parseIntBounded(sender, args[1], 1, rules.size());

			String temp = rules.remove(index - 1);

			index = parseIntWithMin(sender, args[2], 1);

			if (index < rules.size())
			{
				rules.add(index - 1, temp);
				OutputHandler.chatConfirmation(sender, String.format("Rule # %1$s moved to # %2$s", args[1], args[2]));
			}
			else
			{
				rules.add(temp);
				OutputHandler.chatConfirmation(sender, String.format("Rule # %1$s moved to last position.", args[1]));
			}
		}
		else if (args[0].equalsIgnoreCase("change"))
		{
			index = parseIntBounded(sender, args[1], 1, rules.size());

			String newRule = "";
			for (int i = 2; i < args.length; i++)
			{
				newRule = newRule + args[i] + " ";
			}
			newRule = FunctionHelper.formatColors(newRule);
			rules.set(index - 1, newRule);
			OutputHandler.chatConfirmation(sender, String.format("Rules # %1$s changed to '%2$s'.", index + "", newRule));
		}
		else
		{
			error(sender);
		}
		saveRules();
	}

	@Override
	public boolean canConsoleUseCommand()
	{
		return true;
	}

	@Override
	public void registerExtraPermissions(IPermRegisterEvent event)
	{
		event.registerPermissionLevel(getCommandPerm() + ".edit", RegGroup.OWNERS);
	}

	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		if (args.length == 1)
			return getListOfStringsMatchingLastWord(args, autocomargs);
		else if (args.length == 2)
		{
			List<String> opt = new ArrayList<String>();
			for (int i = 1; i < rules.size() + 1; i++)
			{
				opt.add(i + "");
			}
			return opt;
		}
		else if (args.length == 3 && args[0].equalsIgnoreCase("move"))
		{
			List<String> opt = new ArrayList<String>();
			for (int i = 1; i < rules.size() + 2; i++)
			{
				opt.add(i + "");
			}
			return opt;
		}
		else
			return null;
	}

	@Override
	public RegGroup getReggroup()
	{
		return RegGroup.GUESTS;
	}

	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		// TODO Auto-generated method stub
		return "/rules [#] help Gets or sets the rules of the server.";
	}

}
