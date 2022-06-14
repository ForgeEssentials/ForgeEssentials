package com.forgeessentials.core.misc;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.Item;

import com.forgeessentials.commons.BuildInfo;
import com.forgeessentials.core.FEConfig;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.util.output.LoggingHandler;

import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

public class BlockModListFile
{

    private static Calendar calen = Calendar.getInstance();

    @SuppressWarnings("unchecked")
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
                out.println("# Change the location of this file in " + ForgeEssentials.getConfigManager().getMainConfigName() + ".toml");
                out.println();
                
                ModList l = ModList.get();
                Field f = ModList.class.getDeclaredField("mods");
                f.setAccessible(true);
                List<ModContainer> mods = (List<ModContainer>) f.get(l);
                
                for (ModContainer mod : mods)
                {
                    String url = "";
                    if (mod.getModInfo().getUpdateURL().toString() != null)
                    {
                        url = mod.getModInfo().getUpdateURL().toString();
                    }
                    out.println(mod.getModInfo().getDisplayName() + ";" + mod.getModInfo().getVersion() + ";" + mod.getModInfo().getOwningFile() + ";" + url);
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
                    out.println(i.getRegistryName());
                }
                for (Block b : ForgeRegistries.BLOCKS)
                {
                    out.println(b.getRegistryName());
                }
            }
        }
        catch (Exception e)
        {
            /* do nothing */
        }
    }

}
