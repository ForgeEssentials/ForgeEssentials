package com.ForgeEssentials.economy;

import java.io.File;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.common.Configuration;

import com.ForgeEssentials.core.moduleLauncher.ModuleConfigBase;

public class ConfigEconomy extends ModuleConfigBase
{
	private Configuration		config;

	public static final String	CAT	= "Economy";

	public static String		currencySingular;
	public static String		currencyPlural;

	public ConfigEconomy(File file)
	{
		super(file);
	}

	@Override
	public void init()
	{
		config = new Configuration(file);

		currencySingular = config.get(CAT, "currencySingular", "gold").getString();
		currencyPlural = config.get(CAT, "currencyPlural", "gold").getString();
		ModuleEconomy.startbuget = config.get(CAT, "startbuget", 100).getInt();

		config.save();
	}

	@Override
	public void forceSave()
	{
		config.get(CAT, "currencySingular", "gold").set(currencySingular);
		config.get(CAT, "currencyPlural", "gold").set(currencyPlural);
		config.get(CAT, "startbuget", 100).set(ModuleEconomy.startbuget);

		config.save();
	}

	@Override
	public void forceLoad(ICommandSender sender)
	{
		config.load();

		currencySingular = config.get(CAT, "currencySingular", "gold").getString();
		currencyPlural = config.get(CAT, "currencyPlural", "gold").getString();
		ModuleEconomy.startbuget = config.get(CAT, "startbuget", 100).getInt();
	}
}
