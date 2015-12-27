package com.forgeessentials.core.misc;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.util.output.LoggingHandler;
import com.google.common.base.Charsets;

public final class Translator
{

    public static final String TRANSLATOR_FILE = "translations.cfg";

    public static final String COMMENT = "This is the automatically generated translation file.\n"
            + "All texts appearing here are found dynamically while running the server.\n" + "You can put translations after the \"=\" beind each entry.\n"
            + "FE is NOT responsible for translations and we do NOT guarantee that all texts can be translated.";

    public static final TreeMap<String, String> translations = new TreeMap<String, String>();

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

    public static void save()
    {
        File file = new File(ForgeEssentials.getFEDirectory(), TRANSLATOR_FILE);
        try (OutputStreamWriter w = new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(file)), Charsets.UTF_8))
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
        try (BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(file), Charsets.UTF_8)))
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
