package com.forgeessentials.core.misc;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.util.OutputHandler;

public final class Translator {

    public static final String TRANSLATOR_FILE = "translations.ini";

    public static final String COMMENT = "This is the automatically generated translation file.\n"
            + "All texts appearing here are found dynamically while running the server.\n" + "You can put translations after the \"=\" beind each entry.\n"
            + "FE is NOT responsible for translations and we do NOT guarantee that all texts can be translated.";

    protected static Map<String, String> translations = new HashMap<String, String>();

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
        return text;
    }

    public static void save()
    {
        Properties properties = new Properties();
        for (Entry<String, String> translation : translations.entrySet())
            properties.put(translation.getKey(), translation.getValue() == null ? "" : translation.getValue());
        try (OutputStream os = new BufferedOutputStream(new FileOutputStream(new File(ForgeEssentials.getFEDirectory(), TRANSLATOR_FILE))))
        {
            properties.store(os, COMMENT);
        }
        catch (IOException e)
        {
            OutputHandler.felog.severe("Error writing translation file.");
        }
    }

    public static void load()
    {
        Properties p = new Properties();
        try (InputStream is = new BufferedInputStream(new FileInputStream(new File(ForgeEssentials.getFEDirectory(), TRANSLATOR_FILE))))
        {
            p.load(is);
            for (Entry<Object, Object> message : p.entrySet())
            {
                String value = message.getValue().toString();
                translations.put(message.getKey().toString(), value.isEmpty() ? null : value);
            }
        }
        catch (IOException e)
        {
            OutputHandler.felog.warning("Error loading translation file.");
        }
    }

}
