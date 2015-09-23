package com.forgeessentials.tickets;

import java.util.Arrays;

import net.minecraftforge.common.config.Configuration;

import com.forgeessentials.core.moduleLauncher.config.ConfigLoaderBase;
import com.forgeessentials.util.output.LoggingHandler;

public class ConfigTickets extends ConfigLoaderBase
{

    @Override
    public void load(Configuration config, boolean isReload)
    {
        LoggingHandler.felog.debug("Loading Tickets Config");
        String cat = "Tickets";

        ModuleTickets.categories = Arrays.asList(config.get(cat, "categories", new String[] { "griefing", "overflow", "dispute" }).getStringList());

        String subcat = cat + ".DONT_CHANGE";
        config.addCustomCategoryComment(subcat, "Don't change anythign in there.");
        ModuleTickets.currentID = config.get(subcat, "currentID", 0).getInt();
    }

    @Override
    public void save(Configuration config)
    {
        String cat = "Tickets";

        config.get(cat, "categories", new String[] { "griefing", "overflow", "dispute" }).set(ModuleTickets.categories.toArray(new String[0]));

        String subcat = cat + ".DONT_CHANGE";
        config.addCustomCategoryComment(subcat, "Don't change anythign in there.");
        config.get(subcat, "currentID", 0).set(ModuleTickets.currentID);
    }

}
