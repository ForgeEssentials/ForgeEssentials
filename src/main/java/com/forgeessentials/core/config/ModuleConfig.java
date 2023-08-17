package com.forgeessentials.core.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.TreeSet;

import com.forgeessentials.core.ForgeEssentials;

public class ModuleConfig
{
    private TreeMap<String, Boolean> modules = new TreeMap<>();

    private Properties props = new Properties() {

        private static final long serialVersionUID = 1L;

        @Override
        public synchronized Enumeration<Object> keys()
        {

            return Collections.enumeration(new TreeSet<>(super.keySet()));
        }

    };

    private void addModule(String name, boolean defaultValue)
    {
        if (!modules.containsKey(name))
        {
            modules.put(name, defaultValue);
        }
    }

    /*
     * Called on startup only!
     */
    public void loadModuleConfig()
    {
        File configFile = new File(ForgeEssentials.getFEDirectory() + "/Modules.cfg");

        try
        {
            FileReader reader = new FileReader(configFile);
            props.load(reader);
            for (String key : props.stringPropertyNames())
            {
                String value = props.getProperty(key);
                modules.put(key, Boolean.valueOf(value));
            }
            reader.close();
        }
        catch (FileNotFoundException ex)
        {
            try
            {
            	FileWriter writer = new FileWriter(configFile);
                props.store(writer, "Enable/disable modules here.");
                writer.close();
                FileReader reader = new FileReader(configFile);
                props.load(reader);
                reader.close();
            }
            catch (IOException ex1)
            {
                // I/O error
            }
        }
        catch (IOException ex)
        {
            // I/O error
        }
    }

    public void saveConfig()
    {
        File configFile = new File(ForgeEssentials.getFEDirectory() + "/Modules.cfg");

        try
        {
        	try {
				for (Map.Entry<String, Boolean> entry : modules.entrySet()) {
					props.setProperty(entry.getKey(),
							entry.getValue().toString());
				}
        	}catch (NullPointerException ex)
            {
                // Empty module Map
            }
            FileWriter writer = new FileWriter(configFile);
            props.store(writer, "Enable/disable modules here.");
            writer.close();
        }
        catch (IOException ex)
        {
            // I/O error
        }
    }

    public boolean get(String name, boolean defaultValue)
    {
        Boolean value = modules.get(name);
        if (value == null)
        {
            addModule(name, defaultValue);
            return defaultValue;
        }
        return value;
    }

}
