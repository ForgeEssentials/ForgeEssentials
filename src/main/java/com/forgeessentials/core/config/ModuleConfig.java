package com.forgeessentials.core.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.forgeessentials.core.ForgeEssentials;

public class ModuleConfig
{
    private HashMap<String, Boolean> modules;

    private Boolean fileNotFound;
    private Boolean needsSaving;
    
    private Properties props = new Properties();

    /*
     * Only call when ModuleLauncher is done loading modules.
     */
    public void setCreated() {
        saveConfig();
    }

    public Boolean getNeedsSaving()
    {
        return needsSaving;
    }

    public boolean isFileNull() {
        return fileNotFound;
    }

    private void addModule(String name, boolean defaultValue) {
        if(!modules.containsKey(name)) {
            modules.put(name, defaultValue);
        }
    }

    /*
     * Called on startup only! 
     */
    public void loadModuleConfig() {
        
        File configFile = new File(ForgeEssentials.getFEDirectory()+"/Modules.cfg");
        
        try {
            FileReader reader = new FileReader(configFile);
            props.load(reader);
            for (String key : props.stringPropertyNames()) {
                String value = props.getProperty(key);
                modules.put(key, Boolean.valueOf(value));
            }
            reader.close();
            fileNotFound = false;
        } catch (FileNotFoundException ex) {
            // file does not exist
            fileNotFound = true;
            needsSaving = true;
        } catch (IOException ex) {
            // I/O error
            fileNotFound = true;
            needsSaving = true;
        }
        fileNotFound = false;
        needsSaving = false;
    }

    private void saveConfig() {
        File configFile = new File(ForgeEssentials.getFEDirectory()+"/Modules.cfg");
        
        try {
            for (Map.Entry<String, Boolean> entry : modules.entrySet()) {
                props.setProperty(entry.getKey(), entry.getValue().toString());
            }
            FileWriter writer = new FileWriter(configFile);
            props.store(writer, "Enable/disable modules here.");
            writer.close();
        } catch (FileNotFoundException ex) {
            // file does not exist
        } catch (IOException ex) {
            // I/O error
        }
        fileNotFound = false;
        needsSaving = false;
    }
    
    public boolean get(String name, boolean defaultValue) {
        if(isFileNull()) {
            addModule(name, defaultValue);
            return defaultValue;
        }
        Boolean value = modules.get(name);
        if (value == null) {
            addModule(name, defaultValue);
            needsSaving = true;
            return defaultValue;
        }
        return value;
    }
    
}
