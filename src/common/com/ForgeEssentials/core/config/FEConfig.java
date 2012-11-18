package com.ForgeEssentials.core.config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;

import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.core.ModuleLauncher;
import com.ForgeEssentials.core.OutputHandler;

import com.ForgeEssentials.WorldControl.ModuleWorldControl;

public class FEConfig
{
	public static final File	FECONFIG	= new File(ForgeEssentials.FEDIR, "config.cfg");

	// rules stuff
	public static File			rulesFile	= new File(ForgeEssentials.FEDIR, "rules.txt");
	public ArrayList<String>	rules;

	public final Configuration	config;

	// this is designed so it will work for any class.
	public FEConfig()
	{
		OutputHandler.SOP("Loading configs");
		config = new Configuration(FECONFIG);
		// config.load  -- COnfigurations are loaded on Construction.
		
		
		// do rules
		loadRules();
		
		// other stuff.
	}

	private void loadRules()
	{
		// Rules the rules file will be a flat strings file.. nothing special.
		rules = new ArrayList<String>();

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
				lof.logp(Level.SEVERE, "FEConfig", "Generating Rules", "Error reading or writing the Rules file", e);
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
				lof.logp(Level.SEVERE, "FEConfig", "Constructor-Rules", "Error reading or writing the Rules file", e);
			}
		}

	}

	/**
	 * @param name: ei WorldControl, Commands, Permissions, WorldEditCompat, WorldGuardCompat, etc... whatever comes after Module
	 * @return boolean
	 */
	public boolean isModuleEnabled(String name)
	{
		Property prop = config.get("Modules", "name" + "_Enabled", true);
		return prop.getBoolean(true);
	}

}
