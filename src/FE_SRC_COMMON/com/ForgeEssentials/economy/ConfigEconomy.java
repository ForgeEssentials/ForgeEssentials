package com.ForgeEssentials.economy;

import java.io.File;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.common.Configuration;

import com.ForgeEssentials.api.modules.ModuleConfigBase;

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

		currencySingular = config.get(CAT, "currencySingular", "gold").value;
		currencyPlural = config.get(CAT, "currencyPlural", "gold").value;
		ModuleEconomy.startbuget = config.get(CAT, "startbuget", 100).getInt();

		config.save();
	}

	@Override
	public void forceSave()
	{
		config.get(CAT, "currencySingular", "gold").value = currencySingular;
		config.get(CAT, "currencyPlural", "gold").value = currencyPlural;
		config.get(CAT, "startbuget", 100).value = ModuleEconomy.startbuget + "";

		config.save();
	}

	@Override
	public void forceLoad(ICommandSender sender)
	{
		config.load();

		currencySingular = config.get(CAT, "currencySingular", "gold").value;
		currencyPlural = config.get(CAT, "currencyPlural", "gold").value;
		ModuleEconomy.startbuget = config.get(CAT, "startbuget", 100).getInt();
	}
}
