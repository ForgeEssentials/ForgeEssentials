package com.forgeessentials.commons;

import java.io.File;
import java.io.IOException;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.forgeessentials.commons.events.NewVersionEvent;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

public abstract class BuildInfo
{

    public static final Logger febuildinfo = LogManager.getLogger("FEUpdateChecker");

    private static final String BUILD_TYPE = "release";

    private static String buildHash = "N/A";

    /* ------------------------------------------------------------ */

    public static boolean needCheckVersion = false;

    protected static int minorNumberLatest = 0;

    protected static int majorNumberLatest = 0;

    private static Thread checkVersionThread;

    // private static Thread checkBuildTypesThread;

    // private static Properties buildTypes = new Properties();

    /* ------------------------------------------------------------ */

    public static final String MC_BASE_VERSION = "@_MCVERSION_@";

    /**
     * Base version is the 16 in 16.0.x
     */
    public static final String BASE_VERSION = "@_BASEVERSION_@";

    /**
     * Major version is the 0 in 16.0.x
     */
    public static final String MAJOR_VERSION = "@_MAJORVERSION_@";

    /**
     * Minor version is the x in 16.0.x
     */
    public static int MINOR_VERSION = 0;

    public static final String DEPENDENCIES = "required-after:Forge@[10.13.4.1558,)";

    /* ------------------------------------------------------------ */

    public static void startVersionChecks()
    {
        if (needCheckVersion)
        {

            // Check for latest version asap
            checkVersionThread = new Thread(new Runnable() {
                @Override
                public void run()
                {

                	if(FMLCommonHandler.instance().getEffectiveSide()==Side.SERVER) {
                		ServerVersionChecker.doCheckLatestVersion();
                	}
                }
            }, "FEversionCheckThread");
            checkVersionThread.start();

            // checkBuildTypesThread = new Thread(new Runnable() {
            // @Override
            // public void run()
            // {
            // doCheckBuildTypes();
            // }
            // });
            // checkBuildTypesThread.start();
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
                        MINOR_VERSION = Integer.parseInt(manifest.getMainAttributes().getValue("BuildNumber"));
                    }
                    catch (NumberFormatException e)
                    {
                        MINOR_VERSION = 0;
                    }
                }
            }
            else
            {
                febuildinfo.error(String.format("Unable to get FE version information (dev env / %s)", BASE_VERSION));
            }
        }
        catch (IOException e1)
        {
            febuildinfo.error(String.format("Unable to get FE version information (%s)", BASE_VERSION));
        }
    }

    // private static void doCheckBuildTypes()
    // {
    // try
    // {
    // URL buildInfoUrl = new URL("http://files.forgeessentials.com/buildtypes_" + MC_BASE_VERSION + ".txt");
    // URLConnection con = buildInfoUrl.openConnection();
    // con.setConnectTimeout(6000);
    // con.setReadTimeout(12000);
    // con.connect();
    // buildTypes.load(con.getInputStream());
    // }
    // catch (IOException e)
    // {
    // System.err.println("Unable to retrieve build types");
    // }
    // }

    // private static void joinBuildTypeThread()
    // {
    // if (checkBuildTypesThread == null)
    // return;
    // try
    // {
    // checkBuildTypesThread.join();
    // checkBuildTypesThread = null;
    // }
    // catch (InterruptedException e)
    // {
    // /* do nothing */
    // }
    // }

    public static void postNewVersionNotice()
    {
        if (majorNumberLatest != 0)
        {
            MinecraftForge.EVENT_BUS.post(new NewVersionEvent());
        }
    }

    public static String getCurrentVersion()
    {
        return BASE_VERSION + '.' + MAJOR_VERSION + '.' + MINOR_VERSION;
    }

    public static String getBuildHash()
    {
        return buildHash;
    }

    public static String getMinecraftVersion()
    {
        return MC_BASE_VERSION;
    }

    public static String getLatestVersion()
    {
        return BASE_VERSION + '.' + majorNumberLatest + '.' + minorNumberLatest;
    }

    public static boolean isOutdated()
    {
        return majorNumberLatest > Integer.parseInt(MAJOR_VERSION) || minorNumberLatest > MINOR_VERSION;
    }

    public static String getBuildType()
    {
        // joinBuildTypeThread();
        // return buildTypes.getProperty(Integer.toString(buildNumber),
        // BUILD_TYPE_NIGHTLY);
        return BUILD_TYPE;
    }

}
