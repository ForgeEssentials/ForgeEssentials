package com.forgeessentials.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import com.forgeessentials.util.output.logger.LoggingHandler;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.text.TextFormatting;

public abstract class BookUtil
{

    public static void saveBookToFile(ItemStack book, File savefolder)
    {
        ListNBT pages;
        String filename = "";
        if (book != null)
        {
            if (book.hasTag())
            {
                if (book.getTag().contains("title") && book.getTag().contains("pages"))
                {
                    filename = book.getTag().getString("title") + ".txt";
                    pages = (ListNBT) book.getTag().get("pages");
                    File savefile = new File(savefolder, filename);
                    if (savefile.exists())
                    {
                        savefile.delete();
                    }
                    try
                    {
                        savefile.createNewFile();
                        try (BufferedWriter out = new BufferedWriter(new FileWriter(savefile)))
                        {
                            for (net.minecraft.nbt.INBT page : pages) {
                                String line = page.toString();
                                while (line.contains("\n")) {
                                    out.write(line.substring(0, line.indexOf("\n")));
                                    out.newLine();
                                    line = line.substring(line.indexOf("\n") + 1);
                                }
                                if (line.length() > 0) {
                                    out.write(line);
                                }
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        LoggingHandler.felog.info("Something went wrong...");
                    }
                }
            }
        }
    }

    public static void getBookFromFile(PlayerEntity player, File file)
    {
        ListNBT pages = new ListNBT();

        HashMap<String, String> map = new HashMap<>();
        if (file.isFile())
        {
            if (file.getName().contains(".txt"))
            {
                List<String> lines = new ArrayList<>();
                try
                {
                    lines.add(TextFormatting.GREEN + "START" + TextFormatting.BLACK);
                    lines.add("");
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(file.toPath()))))
                    {
                        String line = reader.readLine();
                        while (line != null)
                        {
                            while (line.length() > 21)
                            {
                                lines.add(line.substring(0, 20));
                                line = line.substring(20);
                            }
                            lines.add(line);
                            line = reader.readLine();
                        }
                        reader.close();
                    }
                    lines.add("");
                    lines.add(TextFormatting.RED + "END" + TextFormatting.BLACK);

                }
                catch (Exception e)
                {
                    LoggingHandler.felog.warn("Error reading script: " + file.getName());
                }
                int part = 0;
                int parts = lines.size() / 10 + 1;
                String filename = file.getName().replaceAll(".txt", "");
                if (filename.length() > 13)
                {
                    filename = filename.substring(0, 10) + "...";
                }
                while (lines.size() != 0)
                {
                    part++;
                    StringBuilder temp = new StringBuilder();
                    for (int i = 0; i < 10 && lines.size() > 0; i++)
                    {
                        temp.append(lines.get(0)).append("\n");
                        lines.remove(0);
                    }
                    map.put(TextFormatting.GOLD + " File: " + TextFormatting.GRAY + filename + TextFormatting.DARK_GRAY
                            + "\nPart " + part + " of " + parts + TextFormatting.BLACK + "\n\n", temp.toString());
                }
            }
        }

        ItemStack is = new ItemStack(Items.WRITTEN_BOOK);
        SortedSet<String> keys = new TreeSet<>(map.keySet());
        for (String name : keys)
        {
            pages.add(StringNBT.valueOf(name + map.get(name)));
        }

        is.addTagElement("author", StringNBT.valueOf("ForgeEssentials"));
        is.addTagElement("title", StringNBT.valueOf(file.getName().replace(".txt", "")));

        is.addTagElement("pages", pages);

        player.inventory.add(is);
    }

    public static void getBookFromFile(PlayerEntity player, File file, String title)
    {
        ListNBT pages = new ListNBT();

        HashMap<String, String> map = new HashMap<>();
        if (file.isFile())
        {
            if (file.getName().contains(".txt"))
            {
                List<String> lines = new ArrayList<>();
                try
                {
                    lines.add(TextFormatting.GREEN + "START" + TextFormatting.BLACK);
                    lines.add("");
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(file.toPath()))))
                    {
                        String line = reader.readLine();
                        while (line != null)
                        {
                            while (line.length() > 21)
                            {
                                lines.add(line.substring(0, 20));
                                line = line.substring(20);
                            }
                            lines.add(line);
                            line = reader.readLine();
                        }
                    }
                    lines.add("");
                    lines.add(TextFormatting.RED + "END" + TextFormatting.BLACK);

                }
                catch (Exception e)
                {
                    LoggingHandler.felog.warn("Error reading script: " + file.getName());
                }
                int part = 0;
                int parts = lines.size() / 10 + 1;
                String filename = file.getName().replaceAll(".txt", "");
                if (filename.length() > 13)
                {
                    filename = filename.substring(0, 10) + "...";
                }
                while (lines.size() != 0)
                {
                    part++;
                    StringBuilder temp = new StringBuilder();
                    for (int i = 0; i < 10 && lines.size() > 0; i++)
                    {
                        temp.append(lines.get(0)).append("\n");
                        lines.remove(0);
                    }
                    map.put(TextFormatting.GOLD + " File: " + TextFormatting.GRAY + filename + TextFormatting.DARK_GRAY
                            + "\nPart " + part + " of " + parts + TextFormatting.BLACK + "\n\n", temp.toString());
                }
            }
        }

        ItemStack is = new ItemStack(Items.WRITTEN_BOOK);
        SortedSet<String> keys = new TreeSet<>(map.keySet());
        for (String name : keys)
        {
            pages.add(StringNBT.valueOf(name + map.get(name)));
        }

        is.addTagElement("author", StringNBT.valueOf("ForgeEssentials"));
        is.addTagElement("title", StringNBT.valueOf(title));

        is.addTagElement("pages", pages);

        player.inventory.add(is);
    }

    public static void getBookFromFileUnformatted(PlayerEntity player, File file)
    {
        ListNBT pages = new ListNBT();

        HashMap<String, String> map = new HashMap<>();
        if (file.isFile())
        {
            if (file.getName().contains(".txt"))
            {
                List<String> lines = new ArrayList<>();
                try
                {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(file.toPath()))))
                    {
                        String line = reader.readLine();
                        while (line != null)
                        {
                            lines.add(line);
                            line = reader.readLine();
                        }
                    }
                }
                catch (Exception e)
                {
                    LoggingHandler.felog.warn("Error reading book: " + file.getName());
                }
                while (lines.size() != 0)
                {
                    StringBuilder temp = new StringBuilder();
                    for (int i = 0; i < 10 && lines.size() > 0; i++)
                    {
                        temp.append(lines.get(0)).append("\n");
                        lines.remove(0);
                    }
                    map.put("", temp.toString());
                }
            }
        }

        ItemStack is = new ItemStack(Items.WRITTEN_BOOK);
        SortedSet<String> keys = new TreeSet<>(map.keySet());
        for (String name : keys)
        {
            pages.add(StringNBT.valueOf(name + map.get(name)));
        }

        is.addTagElement("author", StringNBT.valueOf("ForgeEssentials"));
        is.addTagElement("title", StringNBT.valueOf(file.getName().replace(".txt", "")));

        is.addTagElement("pages", pages);

        player.inventory.add(is);
    }

    public static void getBookFromFileUnformatted(PlayerEntity player, File file, String title)
    {
        ListNBT pages = new ListNBT();

        HashMap<String, String> map = new HashMap<>();
        if (file.isFile())
        {
            if (file.getName().contains(".txt"))
            {
                List<String> lines = new ArrayList<>();
                try
                {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(file.toPath()))))
                    {
                        String line = reader.readLine();
                        while (line != null)
                        {
                            lines.add(line);
                            line = reader.readLine();
                        }
                    }
                }
                catch (Exception e)
                {
                    LoggingHandler.felog.warn("Error reading book: " + file.getName());
                }
                while (lines.size() != 0)
                {
                    StringBuilder temp = new StringBuilder();
                    for (int i = 0; i < 10 && lines.size() > 0; i++)
                    {
                        temp.append(lines.get(0)).append("\n");
                        lines.remove(0);
                    }
                    map.put("", temp.toString());
                }
            }
        }

        ItemStack is = new ItemStack(Items.WRITTEN_BOOK);
        SortedSet<String> keys = new TreeSet<>(map.keySet());
        for (String name : keys)
        {
            pages.add(StringNBT.valueOf(name + map.get(name)));
        }

        is.addTagElement("author", StringNBT.valueOf("ForgeEssentials"));
        is.addTagElement("title", StringNBT.valueOf(title));

        is.addTagElement("pages", pages);

        player.inventory.add(is);
    }

    public static void getBookFromFolder(PlayerEntity player, File folder)
    {
        ListNBT pages = new ListNBT();

        HashMap<String, String> map = new HashMap<>();

        File[] listOfFiles = folder.listFiles();

        for (File file : listOfFiles)
        {
            if (file.isFile())
            {
                if (file.getName().contains(".txt"))
                {
                    List<String> lines = new ArrayList<>();
                    try
                    {
                        lines.add(TextFormatting.GREEN + "START" + TextFormatting.BLACK);
                        lines.add("");
                        try (BufferedReader reader = new BufferedReader(
                                new InputStreamReader(Files.newInputStream(file.toPath()))))
                        {
                            String line = reader.readLine();
                            while (line != null)
                            {
                                while (line.length() > 21)
                                {
                                    lines.add(line.substring(0, 20));
                                    line = line.substring(20);
                                }
                                lines.add(line);
                                line = reader.readLine();
                            }
                        }
                        lines.add("");
                        lines.add(TextFormatting.RED + "END" + TextFormatting.BLACK);

                    }
                    catch (Exception e)
                    {
                        LoggingHandler.felog.warn("Error reading script: " + file.getName());
                    }
                    int part = 0;
                    int parts = lines.size() / 10 + 1;
                    String filename = file.getName().replaceAll(".txt", "");
                    if (filename.length() > 13)
                    {
                        filename = filename.substring(0, 10) + "...";
                    }
                    while (lines.size() != 0)
                    {
                        part++;
                        StringBuilder temp = new StringBuilder();
                        for (int i = 0; i < 10 && lines.size() > 0; i++)
                        {
                            temp.append(lines.get(0)).append("\n");
                            lines.remove(0);
                        }
                        map.put(TextFormatting.GOLD + " File: " + TextFormatting.GRAY + filename
                                + TextFormatting.DARK_GRAY + "\nPart " + part + " of " + parts + TextFormatting.BLACK
                                + "\n\n", temp.toString());
                    }
                }
            }
        }

        ItemStack is = new ItemStack(Items.WRITTEN_BOOK);
        SortedSet<String> keys = new TreeSet<>(map.keySet());
        for (String name : keys)
        {
            pages.add(StringNBT.valueOf(name + map.get(name)));
        }

        is.addTagElement("author", StringNBT.valueOf("ForgeEssentials"));
        is.addTagElement("title", StringNBT.valueOf(folder.getName()));

        is.addTagElement("pages", pages);

        player.inventory.add(is);
    }

    public static void getBookFromFolder(PlayerEntity player, File folder, String title)
    {
        ListNBT pages = new ListNBT();

        HashMap<String, String> map = new HashMap<>();

        File[] listOfFiles = folder.listFiles();

        for (File file : listOfFiles)
        {
            if (file.isFile())
            {
                if (file.getName().contains(".txt"))
                {
                    List<String> lines = new ArrayList<>();
                    try
                    {
                        lines.add(TextFormatting.GREEN + "START" + TextFormatting.BLACK);
                        lines.add("");
                        try (BufferedReader reader = new BufferedReader(
                                new InputStreamReader(Files.newInputStream(file.toPath()))))
                        {
                            String line = reader.readLine();
                            while (line != null)
                            {
                                while (line.length() > 21)
                                {
                                    lines.add(line.substring(0, 20));
                                    line = line.substring(20);
                                }
                                lines.add(line);
                                line = reader.readLine();
                            }
                        }
                        lines.add("");
                        lines.add(TextFormatting.RED + "END" + TextFormatting.BLACK);

                    }
                    catch (Exception e)
                    {
                        LoggingHandler.felog.warn("Error reading script: " + file.getName());
                    }
                    int part = 0;
                    int parts = lines.size() / 10 + 1;
                    String filename = file.getName().replaceAll(".txt", "");
                    if (filename.length() > 13)
                    {
                        filename = filename.substring(0, 10) + "...";
                    }
                    while (lines.size() != 0)
                    {
                        part++;
                        StringBuilder temp = new StringBuilder();
                        for (int i = 0; i < 10 && lines.size() > 0; i++)
                        {
                            temp.append(lines.get(0)).append("\n");
                            lines.remove(0);
                        }
                        map.put(TextFormatting.GOLD + " File: " + TextFormatting.GRAY + filename
                                + TextFormatting.DARK_GRAY + "\nPart " + part + " of " + parts + TextFormatting.BLACK
                                + "\n\n", temp.toString());
                    }
                }
            }
        }

        ItemStack is = new ItemStack(Items.WRITTEN_BOOK);
        SortedSet<String> keys = new TreeSet<>(map.keySet());
        for (String name : keys)
        {
            pages.add(StringNBT.valueOf(name + map.get(name)));
        }

        is.addTagElement("author", StringNBT.valueOf("ForgeEssentials"));
        is.addTagElement("title", StringNBT.valueOf(title));

        is.addTagElement("pages", pages);

        player.inventory.add(is);
    }

}
