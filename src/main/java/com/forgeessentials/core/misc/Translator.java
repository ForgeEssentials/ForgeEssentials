package com.forgeessentials.core.misc;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.util.output.logger.LoggingHandler;
import com.google.common.base.Charsets;

import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.BaseComponent;

public final class Translator
{

    public static final String TRANSLATOR_FILE = "translations.cfg";

    public static final String COMMENT = "This is the automatically generated translation file.\n"
            + "All texts appearing here are found dynamically while running the server.\n"
            + "You can put translations after the \"=\" beind each entry.\n"
            + "FE is NOT responsible for translations and we do NOT guarantee that all texts can be translated.";

    public static final TreeMap<String, String> translations = new TreeMap<>();

    public static String format(String text, Object... args)
    {
        return String.format(translate(text), args);
    }

    public static String translate(String text)
    {
        String translated = translations.get(text);
        if (translated != null)
            return translated;
        translations.put(text, null);
        save();
        return text;
    }

    public static BaseComponent translateITC(String text)
    {
        String translated = translations.get(text);
        if (translated != null)
            return new TextComponent(translated);
        translations.put(text, null);
        save();
        return new TextComponent(text);
    }

    public static BaseComponent translateITC(String text, Object[] args)
    {
        String translated = translations.get(text);
        if (translated != null)
            return new TextComponent(translated + Arrays.toString(args));
        translations.put(text, null);
        save();
        return new TextComponent(text + Arrays.toString(args));
    }

    public static void save()
    {
        File file = new File(ForgeEssentials.getFEDirectory(), TRANSLATOR_FILE);
        try (OutputStreamWriter w = new OutputStreamWriter(new BufferedOutputStream(Files.newOutputStream(file.toPath())),
                Charsets.UTF_8))
        {
            for (String line : COMMENT.split("\n"))
            {
                w.write("# ");
                w.write(line);
                w.write(System.lineSeparator());
            }
            for (Entry<String, String> translation : translations.entrySet())
            {
                w.write(translation.getKey());
                w.write('=');
                if (translation.getValue() != null)
                    w.write(translation.getValue());
                w.write(System.lineSeparator());
            }
        }
        catch (IOException e)
        {
            LoggingHandler.felog.error("Error writing translation file.");
        }
    }

    public static void load()
    {
        File file = new File(ForgeEssentials.getFEDirectory(), TRANSLATOR_FILE);
        try (BufferedReader r = new BufferedReader(new InputStreamReader(Files.newInputStream(file.toPath()), Charsets.UTF_8)))
        {
            String line;
            while ((line = r.readLine()) != null)
            {
                if (line.charAt(0) == '#')
                    continue;
                String[] parts = line.split("=", 2);
                if (parts.length < 2)
                    continue;
                translations.put(parts[0], parts[1].isEmpty() ? null : parts[1]);
            }
        }
        catch (IOException e)
        {
            LoggingHandler.felog.warn("Error loading translation file.");
        }
    }
}
