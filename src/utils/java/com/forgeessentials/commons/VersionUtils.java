package com.forgeessentials.commons;

import java.io.File;
import java.io.IOException;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class VersionUtils
{

    public static VersionUtils INSTANCE;

    public static final String FEVERSION = "1.4.0-beta8";

    private File source;

    public VersionUtils(File source)
    {
        this.source = source;
        INSTANCE = this;
    }
    public String getBuildNumber()
    {
        try
        {
            if (source != null)
            {
                try (JarFile jar = new JarFile(source))
                {
                    Manifest manifest = jar.getManifest();
                    return manifest.getMainAttributes().getValue("Build-Number");
                }
            }
        }
        catch (IOException e1)
        {
            e1.printStackTrace();
        }
        return "0";
    }

    public String getBuildHash()
    {
        try
        {
            if (source != null)
            {
                try (JarFile jar = new JarFile(source))
                {
                    Manifest manifest = jar.getManifest();
                    return manifest.getMainAttributes().getValue("BuildID");
                }
            }
        }
        catch (IOException e1)
        {
            e1.printStackTrace();
        }
        return "null";
    }
}
