package com.forgeessentials.core.misc;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Calendar;

import net.minecraft.block.Block;
import net.minecraft.item.Item;

import com.forgeessentials.commons.BuildInfo;
import com.forgeessentials.core.FEConfig;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.util.output.LoggingHandler;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class BlockModListFile
{

    private static Calendar calen = Calendar.getInstance();

    public static void makeModList()
    {
        try
        {
            File modListFile = new File(ForgeEssentials.getFEDirectory(), FEConfig.modlistLocation);
            if (modListFile.exists())
            {
                modListFile.delete();
            }
            try (PrintWriter out = new PrintWriter(new FileWriter(modListFile)))
            {
                out.println("# --- ModList ---");
                out.println("# Generated: " + calen.get(Calendar.DAY_OF_MONTH) + "-" + calen.get(Calendar.MONTH) + "-" + calen.get(Calendar.YEAR)
                        + " (Server time)");
                out.println("# Build: " + BuildInfo.getBuildNumber());
                out.println("# Change the location of this file in " + ForgeEssentials.getConfigManager().getMainConfigName() + ".cfg");
                out.println();

                for (ModContainer mod : Loader.instance().getModList())
                {
                    String url = "";
                    if (!mod.getMetadata().url.isEmpty())
                    {
                        url = mod.getMetadata().url;
                    }
                    out.println(mod.getName() + ";" + mod.getVersion() + ";" + mod.getSource().getName() + ";" + url);
                }
            }
        }
        catch (Exception e)
        {
            LoggingHandler.felog.error("Error writing the modlist file: " + FEConfig.modlistLocation);
        }
    }

    public static void dumpFMLRegistries()
    {
        try
        {
            File modListFile = new File(ForgeEssentials.getFEDirectory(), "ItemList.txt");
            if (modListFile.exists())
            {
                modListFile.delete();
            }
            try (PrintWriter out = new PrintWriter(new FileWriter(modListFile)))
            {
                out.println("# --- Block/Item List ---");
                out.println("# Generated: " + calen.get(Calendar.DAY_OF_MONTH) + "-" + calen.get(Calendar.MONTH) + "-" + calen.get(Calendar.YEAR)
                        + " (Server time)");
                out.println();

                for (Item i : ForgeRegistries.ITEMS)
                {
                    out.println(i.getUnlocalizedName());
                }
                for (Block b : ForgeRegistries.BLOCKS)
                {
                    out.println(b.getUnlocalizedName());
                }
            }
        }
        catch (Exception e)
        {
            /* do nothing */
        }
    }

}
