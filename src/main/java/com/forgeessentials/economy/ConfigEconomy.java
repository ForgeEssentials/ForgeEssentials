package com.forgeessentials.economy;

import com.forgeessentials.core.moduleLauncher.ModuleConfigBase;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.common.config.Configuration;

import java.io.File;

public class ConfigEconomy extends ModuleConfigBase {
    private Configuration config;

    public static final String CAT = "Economy";

    public static String currencySingular;
    public static String currencyPlural;

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
        ModuleEconomy.startbudget = config.get(CAT, "startbuget", 100).getInt();

        config.save();
    }

    @Override
    public void forceSave()
    {
        config.get(CAT, "currencySingular", "gold").set(currencySingular);
        config.get(CAT, "currencyPlural", "gold").set(currencyPlural);
        config.get(CAT, "startbudget", 100).set(ModuleEconomy.startbudget);

        config.save();
    }

    @Override
    public void forceLoad(ICommandSender sender)
    {
        config.load();

        currencySingular = config.get(CAT, "currencySingular", "gold").getString();
        currencyPlural = config.get(CAT, "currencyPlural", "gold").getString();
        ModuleEconomy.startbudget = config.get(CAT, "startbuget", 100).getInt();
    }
}
