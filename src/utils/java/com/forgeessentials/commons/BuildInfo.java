package com.forgeessentials.commons;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

public abstract class BuildInfo
{

    private static String buildHash = "N/A";

    private static int buildNumber = 0;

    private static int buildNumberLatest = 0;

    public static final String VERSION = "%FE_VERSION%";

    public static void getBuildInfo(File jarFile)
    {
        try
        {
            if (jarFile != null)
            {
                try (JarFile jar = new JarFile(jarFile))
                {
                    Manifest manifest = jar.getManifest();
                    buildHash = manifest.getMainAttributes().getValue("BuildID");
                    try
                    {
                        buildNumber = Integer.parseInt(manifest.getMainAttributes().getValue("Build-Number"));
                    }
                    catch (NumberFormatException e)
                    {
                        buildNumber = 0;
                    }
                }
            }
            else
            {
                System.err.println(String.format("Unable to get FE version information (dev env / %s)", VERSION));
            }
        }
        catch (IOException e1)
        {
            System.err.println(String.format("Unable to get FE version information (%s)", VERSION));
        }
    }

    public static void checkLatestVersion()
    {
        try
        {
            URL buildInfoUrl = new URL("http://ci.forgeessentials.com/job/FE/lastSuccessfulBuild/api/json");
            try (InputStreamReader is = new InputStreamReader(buildInfoUrl.openStream()))
            {
                JsonObject versionInfo = new GsonBuilder().create().fromJson(is, JsonObject.class);
                buildNumberLatest = versionInfo.get("number").getAsInt();
            }
        }
        catch (JsonSyntaxException | JsonIOException e)
        {
            System.err.println("Unable to parse version info");
        }
        catch (IOException e)
        {
            System.err.println("Unable to retrieve version info");
        }
    }

    public static String getBuildHash()
    {
        return buildHash;
    }

    public static int getBuildNumber()
    {
        return buildNumber;
    }

    public static int getBuildNumberLatest()
    {
        return buildNumberLatest;
    }

    public static boolean isOutdated()
    {
        return buildNumber > 0 && buildNumberLatest > buildNumber;
    }

}
