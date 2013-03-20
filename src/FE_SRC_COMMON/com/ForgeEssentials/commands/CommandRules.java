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
import net.minecraftforge.common.Configuration;

import com.ForgeEssentials.api.permissions.IPermRegisterEvent;
import com.ForgeEssentials.api.permissions.PermissionsAPI;
import com.ForgeEssentials.api.permissions.RegGroup;
import com.ForgeEssentials.api.permissions.query.PermQueryPlayer;
import com.ForgeEssentials.commands.util.FEcmdModuleCommands;
import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;

public class CommandRules extends FEcmdModuleCommands
{

	public static final String[]	autocomargs	=
												{ "add", "remove", "move", "change" };
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

	public void saveRules()
	{
		try
		{
			OutputHandler.info("Saving rules");

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

			OutputHandler.info("Completed saving rules file.");
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
			return;
		}
		if (args.length == 1)
		{
			if (args[0].equalsIgnoreCase("help"))
			{
				OutputHandler.chatConfirmation(sender, Localization.get("command.rules.help.1"));
				if (PermissionsAPI.checkPermAllowed(new PermQueryPlayer(sender, getCommandPerm() + ".edit")))
				{
					OutputHandler.chatConfirmation(sender, Localization.get("command.rules.help.2"));
					OutputHandler.chatConfirmation(sender, Localization.get("command.rules.help.3"));
					OutputHandler.chatConfirmation(sender, Localization.get("command.rules.help.4"));
					OutputHandler.chatConfirmation(sender, Localization.get("command.rules.help.5"));
				}
				return;
			}

			sender.sendChatToPlayer(rules.get(parseIntBounded(sender, args[0], 1, rules.size()) - 1));
			return;
		}

		if (!PermissionsAPI.checkPermAllowed(new PermQueryPlayer(sender, getCommandPerm() + ".edit")))
		{
			OutputHandler.chatError(sender, Localization.get(Localization.ERROR_NOPERMISSION));
			return;
		}

		int index;

		if (args[0].equalsIgnoreCase("remove"))
		{
			index = parseIntBounded(sender, args[1], 1, rules.size());

			rules.remove(index - 1);
			OutputHandler.chatConfirmation(sender, Localization.format("command.rules.remove", args[1]));
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
			OutputHandler.chatConfirmation(sender, Localization.format("command.rules.added", args[1]));
		}
		else if (args[0].equalsIgnoreCase("move"))
		{
			index = parseIntBounded(sender, args[1], 1, rules.size());

			String temp = rules.remove(index - 1);

			index = parseIntWithMin(sender, args[2], 1);

			if (index < rules.size())
			{
				rules.add(index - 1, temp);
				OutputHandler.chatConfirmation(sender, Localization.format("command.rules.moved", args[1], args[2]));
			}
			else
			{
				rules.add(temp);
				OutputHandler.chatConfirmation(sender, Localization.format("command.rules.movedToLast", args[1]));
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
			OutputHandler.chatConfirmation(sender, Localization.format("command.rules.changed", index + "", newRule));
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
				sender.sendChatToPlayer(rule);
			}
			return;
		}
		if (args.length == 1)
		{
			if (args[0].equalsIgnoreCase("help"))
			{
				OutputHandler.chatConfirmation(sender, Localization.get("command.rules.help.1"));
				OutputHandler.chatConfirmation(sender, Localization.get("command.rules.help.2"));
				OutputHandler.chatConfirmation(sender, Localization.get("command.rules.help.3"));
				OutputHandler.chatConfirmation(sender, Localization.get("command.rules.help.4"));
				OutputHandler.chatConfirmation(sender, Localization.get("command.rules.help.5"));

			}

			sender.sendChatToPlayer(rules.get(parseIntBounded(sender, args[0], 1, rules.size()) - 1));
			return;
		}

		int index;

		if (args[0].equalsIgnoreCase("remove"))
		{
			index = parseIntBounded(sender, args[1], 1, rules.size());

			rules.remove(index - 1);
			OutputHandler.chatConfirmation(sender, Localization.format("command.rules.remove", args[1]));
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
			OutputHandler.chatConfirmation(sender, Localization.format("command.rules.added", args[1]));
		}
		else if (args[0].equalsIgnoreCase("move"))
		{
			index = parseIntBounded(sender, args[1], 1, rules.size());

			String temp = rules.remove(index - 1);

			index = parseIntWithMin(sender, args[2], 1);

			if (index < rules.size())
			{
				rules.add(index - 1, temp);
				OutputHandler.chatConfirmation(sender, Localization.format("command.rules.moved", args[1], args[2]));
			}
			else
			{
				rules.add(temp);
				OutputHandler.chatConfirmation(sender, Localization.format("command.rules.movedToLast", args[1]));
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
			OutputHandler.chatConfirmation(sender, Localization.format("command.rules.changed", index + "", newRule));
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
	public String getCommandPerm()
	{
		return "ForgeEssentials.BasicCommands." + getCommandName();
	}

	@Override
	public void registerExtraPermissions(IPermRegisterEvent event)
	{
		event.registerPermissionLevel(getCommandPerm() + ".edit", RegGroup.OWNERS);
	}

	@Override
	public List<?> addTabCompletionOptions(ICommandSender sender, String[] args)
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
		return RegGroup.OWNERS;
	}

}
