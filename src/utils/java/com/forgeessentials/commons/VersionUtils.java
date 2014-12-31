package com.forgeessentials.commons;

import java.io.File;
import java.io.IOException;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class VersionUtils
{
    public static String getBuildNumber(File source)
    {
        try
        {
            if (source != null)
            {
                JarFile jar = new JarFile(source);
                Manifest manifest = jar.getManifest();
                return manifest.getMainAttributes().getValue("Build-Number");
            }
        }
        catch (IOException e1)
        {
            e1.printStackTrace();
        }
        return "0";
    }

    public static String getBuildHash(File source)
    {
        try
        {
            if (source != null)
            {
                JarFile jar = new JarFile(source);
                Manifest manifest = jar.getManifest();
                return manifest.getMainAttributes().getValue("BuildID");
            }
        }
        catch (IOException e1)
        {
            e1.printStackTrace();
        }
        return "null";
    }
}
