package com.forgeessentials.tickets;

import com.forgeessentials.core.moduleLauncher.ModuleConfigBase;
import com.forgeessentials.util.OutputHandler;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.common.config.Configuration;

import java.io.File;
import java.util.Arrays;

public class ConfigTickets extends ModuleConfigBase {
    public Configuration config;

    public ConfigTickets(File file)
    {
        super(file);
    }

    @Override
    public void init()
    {
        OutputHandler.felog.finer("Loading Tickets Config");
        config = new Configuration(file, true);

        String cat = "Tickets";

        ModuleTickets.categories = Arrays.asList(config.get(cat, "categories", new String[] { "griefing", "overflow", "dispute" }).getStringList());

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

        config.get(cat, "categories", new String[] { "griefing", "overflow", "dispute" }).set(ModuleTickets.categories.toArray(new String[0]));

        String subcat = cat + ".DONT_CHANGE";
        config.addCustomCategoryComment(subcat, "Don't change anythign in there.");
        config.get(subcat, "currentID", 0).set(ModuleTickets.currentID);

        config.save();
    }

    @Override
    public void forceLoad(ICommandSender sender)
    {
        config = new Configuration(file, true);

        String cat = "Tickets";

        ModuleTickets.categories = Arrays.asList(config.get(cat, "categories", new String[] { "griefing", "overflow", "dispute" }).getStringList());

        String subcat = cat + ".DONT_CHANGE";
        config.addCustomCategoryComment(subcat, "Don't change anythign in there.");
        ModuleTickets.currentID = config.get(subcat, "currentID", 0).getInt();

        config.save();
    }
}
