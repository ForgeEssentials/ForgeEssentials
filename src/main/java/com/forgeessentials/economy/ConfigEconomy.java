package com.forgeessentials.economy;

import net.minecraft.command.ICommandSender;

import com.forgeessentials.core.moduleLauncher.ModuleConfigBase;

public class ConfigEconomy extends ModuleConfigBase {

    public static final String CAT = "Economy";

    public static String currencySingular;
    public static String currencyPlural;

    @Override
    public void init()
    {

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

    @Override
    public boolean universalConfigAllowed()
    {
        return true;
    }
}
