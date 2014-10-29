package com.forgeessentials.core.misc;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Calendar;

import net.minecraft.block.Block;
import net.minecraft.item.Item;

import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.util.OutputHandler;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.registry.GameData;

public class BlockModListFile {

    private static Calendar calen = Calendar.getInstance();

    public static void makeModList()
    {
        try
        {
            File modListFile = new File(ForgeEssentials.FEDIR, ForgeEssentials.modlistLocation);
            if (modListFile.exists())
            {
                modListFile.delete();
            }
            FileWriter fstream = new FileWriter(modListFile);
            PrintWriter out = new PrintWriter(fstream);
            out.println("# --- ModList ---");
            out.println(
                    "# Generated: " + calen.get(Calendar.DAY_OF_MONTH) + "-" + calen.get(Calendar.MONTH) + "-" + calen.get(Calendar.YEAR) + " (Server time)");
            out.println("# Change the location of this file in " + CoreConfig.mainconfig);
            out.println();

            for (ModContainer mod : Loader.instance().getModList())
            {
                String url = "";
                if (!mod.getMetadata().url.isEmpty())
                {
                    url = mod.getMetadata().url;
                }
                if (!mod.getMetadata().updateUrl.isEmpty())
                {
                    url = mod.getMetadata().updateUrl;
                }
                out.println(mod.getName() + ";" + mod.getVersion() + ";" + url);
            }

            out.close();
        }
        catch (Exception e)
        {
            OutputHandler.felog.severe("Error writing the modlist file: " + ForgeEssentials.modlistLocation);
        }
    }

    public static void dumpFMLRegistries()
    {
        try
        {
            File modListFile = new File(ForgeEssentials.FEDIR, "ItemList.txt");
            if (modListFile.exists())
            {
                modListFile.delete();
            }
            FileWriter fstream = new FileWriter(modListFile);
            PrintWriter out = new PrintWriter(fstream);
            out.println("# --- Block/Item List ---");
            out.println(
                    "# Generated: " + calen.get(Calendar.DAY_OF_MONTH) + "-" + calen.get(Calendar.MONTH) + "-" + calen.get(Calendar.YEAR) + " (Server time)");
            out.println();

            for (Item i : GameData.getItemRegistry().typeSafeIterable())
            {
                out.println(i.getUnlocalizedName());
            }
            for (Block b : GameData.getBlockRegistry().typeSafeIterable())
            {
                out.println(b.getUnlocalizedName());
            }

            out.close();
        }
        catch (Exception e)
        {

        }
    }
}
