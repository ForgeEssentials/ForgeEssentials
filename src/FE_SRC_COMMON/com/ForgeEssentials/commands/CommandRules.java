package com.ForgeEssentials.commands;

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

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;

public class CommandRules extends ForgeEssentialsCommandBase
{

	public static ArrayList<String> rules;
	public static File rulesFile = new File(ForgeEssentials.FEDIR, "rules.txt");

	public CommandRules()
	{
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
			} catch (Exception e)
			{
				Logger lof = OutputHandler.felog;
				lof.logp(Level.SEVERE, "FEConfig", "Generating Rules", "Error writing the Rules file: " + rulesFile.getName(), e);
			}
		} else
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
			} catch (Exception e)
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
				rulesFile.createNewFile();

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
		} catch (Exception e)
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
		if (args.length > 1)
		{
			if (args[1].equals("remove"))
			{
				try
				{
					rules.remove(new Integer(args[0]) - 1);
				} catch (NumberFormatException e)
				{
					OutputHandler.chatError(sender, Localization.format(Localization.ERROR_NAN, args[0]));
				} catch (IndexOutOfBoundsException e)
				{
					sender.sendChatToPlayer("That rule does not exist.");
				}
			} else
			{
				try
				{
					String newRule = "";
					for (int i = 1; i < args.length; i++)
						newRule = newRule + args[i] + " ";
					rules.add(new Integer(args[0]) - 1, newRule);
				} catch (NumberFormatException e)
				{
					sender.sendChatToPlayer("Not a number. Try " + getSyntaxConsole());
				}
			}
			saveRules();
		} else
			for (String rule : rules)
				sender.sendChatToPlayer(rule);
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		if (args.length > 1)
		{
			if (args[1].equals("remove"))
			{
				try
				{
					rules.remove(new Integer(args[0]) - 1);
				} catch (NumberFormatException e)
				{
					sender.sendChatToPlayer(Localization.format(Localization.ERROR_NAN, args[0]));
					error(sender);
				} catch (IndexOutOfBoundsException e)
				{
					sender.sendChatToPlayer("That rule does not exist.");
				}
			} else
			{
				try
				{
					String newRule = "";
					for (int i = 1; i < args.length; i++)
						newRule = newRule + args[i] + " ";
					rules.add(new Integer(args[0]) - 1, newRule);
				} catch (NumberFormatException e)
				{
					sender.sendChatToPlayer(Localization.format(Localization.ERROR_NAN, args[0]));
					error(sender);
				}
			}
			saveRules();
		} else
			for (String rule : rules)
				sender.sendChatToPlayer(rule);
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
    	if(args.length == 1)
    	{
    		return getListOfStringsMatchingLastWord(args, "remove");
    	}
    	else
    	{
    		return null;
    	}
    }

}
