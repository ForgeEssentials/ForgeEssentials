package com.forgeessentials.commons;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

public abstract class BuildInfo
{

    private static final String BUILD_TYPE_NIGHTLY = "nightly";

    private static String buildHash = "N/A";

    public static boolean checkVersion;

    private static int buildNumber = 0;

    private static int buildNumberLatest = 0;

    private static Thread checkVersionThread;

    private static Thread checkBuildTypesThread;

    private static Properties buildTypes = new Properties();

    public static final String MC_BASE_VERSION = "_MCVERSION_";

    public static final String BASE_VERSION = "_BASEVERSION_";

    public static final String DEPENDENCIES = "required-after:forge";

    public static void startVersionChecks()
    {
        if (checkVersion)
        {
            // Check for latest version asap
            checkVersionThread = new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    doCheckLatestVersion();
                }
            });
            checkVersionThread.start();

            checkBuildTypesThread = new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    doCheckBuildTypes();
                }
            });
            checkBuildTypesThread.start();
        }
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
                System.err.println(String.format("Unable to get FE version information (dev env / %s)", BASE_VERSION));
            }
        }
        catch (IOException e1)
        {
            System.err.println(String.format("Unable to get FE version information (%s)", BASE_VERSION));
        }
    }

    private static void doCheckLatestVersion()
    {
        try
        {
            URL buildInfoUrl = new URL("http://ci.forgeessentials.com/job/FE-1.12.2/lastSuccessfulBuild/api/json");
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

    private static void joinVersionThread()
    {
        if (checkVersionThread == null)
            return;
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

    private static void doCheckBuildTypes()
    {
        try
        {
            URL buildInfoUrl = new URL("http://files.forgeessentials.com/buildtypes_" + MC_BASE_VERSION + ".txt");
            URLConnection con = buildInfoUrl.openConnection();
            con.setConnectTimeout(6000);
            con.setReadTimeout(12000);
            con.connect();
            buildTypes.load(con.getInputStream());
        }
        catch (IOException e)
        {
            System.err.println("Unable to retrieve build types");
        }
    }

    private static void joinBuildTypeThread()
    {
        if (checkBuildTypesThread == null)
            return;
        try
        {
            checkBuildTypesThread.join();
            checkBuildTypesThread = null;
        }
        catch (InterruptedException e)
        {
            /* do nothing */
        }
    }

    public static void cancelVersionCheck()
    {
        // Set to null, which will disable joining of the thread and kill any possible delay
        checkVersionThread = null;
    }

    public static String getFullVersion()
    {
        if (buildNumber == 0)
            return BASE_VERSION;
        return BASE_VERSION + '.' + buildNumber;
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
        joinVersionThread();
        return buildNumberLatest;
    }

    public static boolean isOutdated()
    {
        joinVersionThread();
        return buildNumber > 0 && buildNumberLatest > buildNumber;
    }

    public static String getBuildType()
    {
        joinBuildTypeThread();
        return buildTypes.getProperty(Integer.toString(buildNumber), BUILD_TYPE_NIGHTLY);
    }

}
