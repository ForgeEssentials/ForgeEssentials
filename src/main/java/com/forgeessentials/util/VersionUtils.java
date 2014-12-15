package com.forgeessentials.util;

import java.io.IOException;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import com.forgeessentials.core.preloader.FEPreLoader;

public class VersionUtils
{
    public static String getBuildNumber()
    {
        try
        {
            if (FEPreLoader.jarLocation != null)
            {
                JarFile jar = new JarFile(FEPreLoader.jarLocation);
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

    public static String getBuildHash()
    {
        try
        {
            if (FEPreLoader.jarLocation != null)
            {
                JarFile jar = new JarFile(FEPreLoader.jarLocation);
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
