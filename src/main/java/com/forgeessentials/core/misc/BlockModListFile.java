package com.forgeessentials.core.misc;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.List;

import com.forgeessentials.commons.BuildInfo;
import com.forgeessentials.core.FEConfig;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.util.output.logger.LoggingHandler;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.registries.ForgeRegistries;

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
                out.println("# Generated: " + calen.get(Calendar.DAY_OF_MONTH) + "-" + calen.get(Calendar.MONTH) + "-"
                        + calen.get(Calendar.YEAR) + " (Server time)");
                out.println("# Build: " + BuildInfo.getCurrentVersion());
                out.println("# Change the location of this file in "
                        + ForgeEssentials.getConfigManager().getMainConfigName() + ".toml");
                out.println();

                List<IModInfo> mods = ModList.get().getMods();
                for (IModInfo mod : mods)
                {
                    out.println(
                            "#######################################################################################");
                    out.println("Name:        " + mod.getDisplayName());
                    out.println("Version:     " + mod.getVersion());
                    out.println("Description: " + mod.getDescription().trim());
                    out.println("ModID:       " + mod.getModId());
                    out.println("Namespace:   " + mod.getNamespace());
                    out.println("Properties:  " + mod.getModProperties().toString());
                    out.println("License:     " + mod.getOwningFile().getLicense());
                    out.println("Modfile:     " + mod.getOwningFile().getFile());
                    out.println("UpdateURL:   " + mod.getUpdateURL());
                }
            }
        }
        catch (Exception e)
        {
            LoggingHandler.felog.error("Error writing the modlist file: " + FEConfig.modlistLocation);
            e.printStackTrace();
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
                out.println("# Generated: " + calen.get(Calendar.DAY_OF_MONTH) + "-" + calen.get(Calendar.MONTH) + "-"
                        + calen.get(Calendar.YEAR) + " (Server time)");
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
