package com.forgeessentials.commons;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
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

    private static Thread checkVersionThread;

    public static final String VERSION = "1.4.2"; // update manually because gradle is a derp

    static
    {
        // Check for latest version asap
        checkLatestVersion();
    }

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
                        buildNumber = Integer.parseInt(manifest.getMainAttributes().getValue("BuildNumber"));
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
        if (checkVersionThread != null && checkVersionThread.isAlive())
            return;
        checkVersionThread = new Thread(new Runnable() {
            @Override
            public void run()
            {
                doCheckLatestVersion();
            }
        });
        checkVersionThread.start();
    }

    private static void doCheckLatestVersion()
    {
        try
        {
            URL buildInfoUrl = new URL("http://ci.forgeessentials.com/job/FE/lastSuccessfulBuild/api/json");
            URLConnection con = buildInfoUrl.openConnection();
            con.setConnectTimeout(6000);
            con.setReadTimeout(12000);
            con.connect();
            try (InputStreamReader is = new InputStreamReader(con.getInputStream()))
            {
                JsonObject versionInfo = new GsonBuilder().create().fromJson(is, JsonObject.class);
                buildNumberLatest = versionInfo.get("number").getAsInt();
            }
            // TODO update to support milestone/recommended releases
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

    private static void joinCheckThread()
    {
        if (checkVersionThread != null)
        {
            try
            {
                checkVersionThread.join();
                checkVersionThread = null;
            }
            catch (InterruptedException e)
            {
                /* do nothing */
            }
        }
    }

    public static void cancelVersionCheck()
    {
        // Set to null, which will disable joining of the thread and kill any possible delay
        checkVersionThread = null;
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
        joinCheckThread();
        return buildNumberLatest;
    }

    public static boolean isOutdated()
    {
        joinCheckThread();
        return buildNumber > 0 && buildNumberLatest > buildNumber;
    }

}
