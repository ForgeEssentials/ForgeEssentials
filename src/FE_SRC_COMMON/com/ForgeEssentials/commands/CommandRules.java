package com.ForgeEssentials.commands;

import com.ForgeEssentials.api.permissions.PermissionsAPI;
import com.ForgeEssentials.api.permissions.query.PermQueryPlayer;
import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import net.minecraftforge.common.Configuration;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CommandRules extends ForgeEssentialsCommandBase
{

	public static final String[]	autocomargs	= { "Add", "Remove", "Move" };
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

		OutputHandler.SOP("Loading rules");
		if (!rulesFile.exists())
		{
			try
			{
				OutputHandler.SOP("No rules file found. Generating with default rules..");

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

				OutputHandler.SOP("Completed generating rules file.");
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
				OutputHandler.SOP("Rules file found. Reading...");

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

				OutputHandler.SOP("Completed reading rules file. " + counter + " rules read.");
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
			OutputHandler.SOP("Saving rules");

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

			OutputHandler.SOP("Completed saving rules file.");
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
				sender.sendChatToPlayer(rule);
			}
		}
		else
		{

			if (args.length > 1 && PermissionsAPI.checkPermAllowed(new PermQueryPlayer(sender, getCommandPerm() + ".edit")))
			{
				if (args[0].equalsIgnoreCase("remove"))
				{
					try
					{
						rules.remove(new Integer(args[1]) - 1);
						sender.sendChatToPlayer("Rule #" + args[1] + " removed.");
					}
					catch (NumberFormatException e)
					{
						OutputHandler.chatError(sender, Localization.format(Localization.ERROR_NAN, args[1]));
					}
					catch (IndexOutOfBoundsException e)
					{
						sender.sendChatToPlayer("That rule does not exist.");
					}
				}
				else if (args[0].equalsIgnoreCase("add"))
				{
					String newRule = "";
					for (int i = 1; i < args.length; i++)
					{
						newRule = newRule + args[i] + " ";
					}
					rules.add(newRule);
					sender.sendChatToPlayer("Rule #" + rules.size() + ": " + rules.get(rules.size() - 1) + " Added!");
				}
				else if (args[0].equalsIgnoreCase("move"))
				{
					String temp = "";
					try
					{
						temp = rules.remove(new Integer(args[1]) - 1);
					}
					catch (NumberFormatException e)
					{
						OutputHandler.chatError(sender, Localization.format(Localization.ERROR_NAN, args[1]));
						return;
					}
					catch (IndexOutOfBoundsException e)
					{
						sender.sendChatToPlayer("That rule does not exist.");
						return;
					}
					try
					{
						if (new Integer(args[2]) < rules.size())
						{
							rules.add(new Integer(args[2]) - 1, temp);
							sender.sendChatToPlayer("Rule #" + args[1] + ": " + rules.get(new Integer(args[2]) - 1) + " Moved to: " + args[2]);
						}
						else
						{
							rules.add(temp);
							sender.sendChatToPlayer("Rule #" + args[1] + ": " + rules.get(rules.size() - 1) + " Moved to last position.");
						}
					}
					catch (NumberFormatException e)
					{
						OutputHandler.chatError(sender, Localization.format(Localization.ERROR_NAN, args[2]));
						rules.add(new Integer(args[1]) - 1, temp);
						return;
					}
				}
				else
				{
					OutputHandler.chatError(sender, Localization.format(Localization.ERROR_BADSYNTAX));
				}
				saveRules();
			}
			else
			{
				OutputHandler.chatError(sender, Localization.get("message.error.nopermission"));
			}
		}

	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		if (args.length > 1)
		{
			if (args[0].equalsIgnoreCase("remove"))
			{
				try
				{
					rules.remove(new Integer(args[1]) - 1);
					sender.sendChatToPlayer("Rule #" + args[1] + " removed.");
				}
				catch (NumberFormatException e)
				{
					sender.sendChatToPlayer(Localization.format(Localization.ERROR_NAN, args[1]));
				}
				catch (IndexOutOfBoundsException e)
				{
					sender.sendChatToPlayer("That rule does not exist.");
				}
			}
			else if (args[0].equalsIgnoreCase("add"))
			{
				String newRule = "";
				for (int i = 1; i < args.length; i++)
				{
					newRule = newRule + args[i] + " ";
				}
				rules.add(newRule);
				sender.sendChatToPlayer("Rule #" + rules.size() + ": " + rules.get(rules.size() - 1) + " Added!");
			}
			else if (args[0].equalsIgnoreCase("move"))
			{
				String temp = "";
				try
				{
					temp = rules.remove(new Integer(args[1]) - 1);
				}
				catch (NumberFormatException e)
				{
					sender.sendChatToPlayer(Localization.format(Localization.ERROR_NAN, args[1]));
					return;
				}
				catch (IndexOutOfBoundsException e)
				{
					sender.sendChatToPlayer("That rule does not exist.");
					return;
				}
				try
				{
					if (new Integer(args[2]) < rules.size())
					{
						rules.add(new Integer(args[2]) - 1, temp);
						sender.sendChatToPlayer("Rule #" + args[1] + ": " + rules.get(new Integer(args[2]) - 1) + " Moved to: " + args[2]);
					}
					else
					{
						rules.add(temp);
						sender.sendChatToPlayer("Rule #" + args[1] + ": " + rules.get(rules.size() - 1) + " Moved to last position.");
					}
					sender.sendChatToPlayer("Rule #" + args[1] + ": " + rules.get(new Integer(args[2]) - 1) + " Moved to: " + args[2]);
				}
				catch (NumberFormatException e)
				{
					sender.sendChatToPlayer(Localization.format(Localization.ERROR_NAN, args[2]));
					rules.add(new Integer(args[1]) - 1, temp);
					return;
				}
			}
			else
			{
				sender.sendChatToPlayer(Localization.format(Localization.ERROR_BADSYNTAX));
			}
			saveRules();
		}
		else
		{
			for (String rule : rules)
			{
				sender.sendChatToPlayer(rule);
			}
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

}