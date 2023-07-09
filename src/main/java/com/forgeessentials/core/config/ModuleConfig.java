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
    private TreeMap<String, Boolean> modules = new TreeMap<String, Boolean>();

    private Boolean fileNotFound;
    private Boolean needsSaving;

    private Properties props = new Properties() {

        private static final long serialVersionUID = 1L;

        @Override
        public synchronized Enumeration<Object> keys()
        {

            return Collections.enumeration(new TreeSet<Object>(super.keySet()));
        }

    };

    /*
     * Only call when ModuleLauncher is done loading modules.
     */
    public void setCreated()
    {
        if (getNeedsSaving())
            saveConfig();
    }

    public Boolean getNeedsSaving()
    {
        return needsSaving;
    }

    public void setNeedsSaving(boolean set)
    {
        needsSaving = set;
    }

    public boolean isFileNull()
    {
        return fileNotFound;
    }

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
            fileNotFound = false;
            setNeedsSaving(false);
        }
        catch (FileNotFoundException ex)
        {
            // file does not exist
            fileNotFound = true;
            setNeedsSaving(true);
        }
        catch (IOException ex)
        {
            // I/O error
            fileNotFound = true;
            setNeedsSaving(true);
        }
    }

    public void saveConfig()
    {
        File configFile = new File(ForgeEssentials.getFEDirectory() + "/Modules.cfg");

        try
        {
            for (Map.Entry<String, Boolean> entry : modules.entrySet())
            {
                props.setProperty(entry.getKey(), entry.getValue().toString());
            }
            FileWriter writer = new FileWriter(configFile);
            props.store(writer, "Enable/disable modules here.");
            writer.close();
            setNeedsSaving(false);
        }
        catch (FileNotFoundException ex)
        {
            fileNotFound = true;
            // file does not exist
        }
        catch (IOException ex)
        {
            fileNotFound = true;
            // I/O error
        }
        catch (NullPointerException ex)
        {
            setNeedsSaving(false);
            // Empty module Map
        }
    }

    public boolean get(String name, boolean defaultValue)
    {
        if (isFileNull())
        {
            addModule(name, defaultValue);
            return defaultValue;
        }
        Boolean value = modules.get(name);
        if (value == null)
        {
            addModule(name, defaultValue);
            setNeedsSaving(true);
            return defaultValue;
        }
        return value;
    }

}
