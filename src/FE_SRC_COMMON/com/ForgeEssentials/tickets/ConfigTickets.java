package com.ForgeEssentials.tickets;

import java.io.File;
import java.util.Arrays;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.common.Configuration;

import com.ForgeEssentials.api.modules.ModuleConfigBase;
import com.ForgeEssentials.util.OutputHandler;

public class ConfigTickets extends ModuleConfigBase
{
	public Configuration	config;

	public ConfigTickets(File file)
	{
		super(file);
	}

	@Override
	public void init()
	{
		OutputHandler.finer("Loading Tickets Config");
		config = new Configuration(file, true);

		String cat = "Tickets";

		ModuleTickets.categories = Arrays.asList(config.get(cat, "categories", new String[] { "griefing", "overflow", "dispute" }).valueList);

		String subcat = cat + ".DONT_CHANGE";
		config.addCustomCategoryComment(subcat, "Don't change anythign in there.");
		ModuleTickets.currentID = config.get(subcat, "currentID", 0).getInt();

		config.save();
	}

	@Override
	public void forceSave()
	{
		config = new Configuration(file, true);

		String cat = "Tickets";

		config.get(cat, "categories", new String[] { "griefing", "overflow", "dispute" }).valueList = ModuleTickets.categories.toArray(new String[0]);

		String subcat = cat + ".DONT_CHANGE";
		config.addCustomCategoryComment(subcat, "Don't change anythign in there.");
		config.get(subcat, "currentID", 0).value = "" + ModuleTickets.currentID;

		config.save();
	}

	@Override
	public void forceLoad(ICommandSender sender)
	{
		config = new Configuration(file, true);

		String cat = "Tickets";

		ModuleTickets.categories = Arrays.asList(config.get(cat, "categories", new String[] { "griefing", "overflow", "dispute" }).valueList);

		String subcat = cat + ".DONT_CHANGE";
		config.addCustomCategoryComment(subcat, "Don't change anythign in there.");
		ModuleTickets.currentID = config.get(subcat, "currentID", 0).getInt();

		config.save();
	}
}
