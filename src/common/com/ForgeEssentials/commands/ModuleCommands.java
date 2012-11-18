package com.ForgeEssentials.commands;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.core.ModuleLauncher;
import com.ForgeEssentials.core.OutputHandler;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

/**
 * Kindly register all commands in commands module here.
 */

public class ModuleCommands
{
	public static String			motd;
	public static ArrayList<String>	rules;

	public static File				rulesFile	= new File(ForgeEssentials.FEDIR, "rules.txt");

	public ModuleCommands()
	{
		if (!ModuleLauncher.cmdEnabled)
			return;

		rules = loadRules(rulesFile);
	}

	public ArrayList<String> loadRules(File ruleFile)
	{
		// Rules the rules file will be a flat strings file.. nothing special.
		ArrayList<String> rules = new ArrayList<String>();

		OutputHandler.SOP("Loading rules");
		if (!ruleFile.exists())
		{
			try
			{
				OutputHandler.SOP("No rules file found. Generating with default rules..");

				ruleFile.createNewFile();

				// create streams
				FileOutputStream stream = new FileOutputStream(ruleFile);
				OutputStreamWriter streamWriter = new OutputStreamWriter(stream);
				BufferedWriter writer = new BufferedWriter(streamWriter);

				writer.write("# " + ruleFile.getName() + " | numbers are automatically added");
				writer.newLine();

				writer.write("Obey the Admins");
				rules.add("Obey the Admins");
				writer.newLine();

				writer.write("Do not greif");
				rules.add("Do not greif");
				writer.newLine();

				writer.close();
				streamWriter.close();
				stream.close();

				OutputHandler.SOP("Completed generating rules file.");
			}
			catch (Exception e)
			{
				Logger lof = OutputHandler.felog;
				lof.logp(Level.SEVERE, "FEConfig", "Generating Rules", "Error writing the Rules file: " + ruleFile.getName(), e);
			}
		}
		else
		{
			try
			{
				OutputHandler.SOP("Rules file found. Reading...");

				FileInputStream stream = new FileInputStream(ruleFile);
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
				lof.logp(Level.SEVERE, "FEConfig", "Constructor-Rules", "Error reading or writing the Rules file: " + ruleFile.getName(), e);
			}
		}

		return rules;
	}

	public void saveRules(File ruleFile, ArrayList<String> rules)
	{
		try
		{
			OutputHandler.SOP("Saving rules");

			if (!ruleFile.exists())
				ruleFile.createNewFile();

			// create streams
			FileOutputStream stream = new FileOutputStream(ruleFile);
			OutputStreamWriter streamWriter = new OutputStreamWriter(stream);
			BufferedWriter writer = new BufferedWriter(streamWriter);

			writer.write("# " + ruleFile.getName() + " | numbers are automatically added");
			writer.newLine();
			
			for (String rule: rules)
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
			lof.logp(Level.SEVERE, "FEConfig", "Saving Rules", "Error writing the Rules file: " + ruleFile.getName(), e);
		}
	}

	// load.
	public void preLoad(FMLPreInitializationEvent e)
	{
		OutputHandler.SOP("Commands module is enabled. Loading...");
	}

	// load.
	public void load(FMLInitializationEvent e)
	{

	}

	// load.
	public void serverStarting(FMLServerStartingEvent e)
	{
		// commands
		e.registerServerCommand(new CommandMotd());
		e.registerServerCommand(new CommandRules());
		e.registerServerCommand(new CommandButcher());
		e.registerServerCommand(new CommandRemove());
		e.registerServerCommand(new CommandHome());
		e.registerServerCommand(new CommandRestart());

	}
}