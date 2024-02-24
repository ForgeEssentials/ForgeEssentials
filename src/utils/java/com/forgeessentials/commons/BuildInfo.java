package com.forgeessentials.commons;

import java.io.File;
import java.io.IOException;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.VersionChecker;

public abstract class BuildInfo
{

    public static final Logger febuildinfo = LogManager.getLogger("FEUpdateChecker");

    private static String BUILD_TYPE = "@_RELEASETYPE_@";

    private static String buildHash = "N/A";

    /* ------------------------------------------------------------ */

    public static boolean needCheckVersion = false;

    protected static boolean outdated = false;
    protected static String versionLatest = "N/A";

    private static Thread checkVersionThread;

    /* ------------------------------------------------------------ */

    private static final String MC_BASE_VERSION = "@_MCVERSION_@";

    /**
     * Base version is the 16 in 16.0.x
     */
    protected static final String BASE_VERSION = "@_BASEVERSION_@";

    /**
     * Major version is the 0 in 16.0.x
     */
    protected static final String MAJOR_VERSION = "@_MAJORVERSION_@";

    /**
     * Minor version is the x in 16.0.x
     */
    protected static int MINOR_VERSION = 0;

    /* ------------------------------------------------------------ */

    public static void startVersionChecks(String modid)
    {
        if (needCheckVersion)
        {
        	VersionChecker.CheckResult result = VersionChecker.getResult(ModList.get().getModContainerById(modid).get().getModInfo());
            if (result != null && (result.status == VersionChecker.Status.OUTDATED || result.status == VersionChecker.Status.BETA_OUTDATED))
            {
            	outdated=true;
            	versionLatest = result.target.toString();
            }
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
                    	if(manifest.getMainAttributes().getValue("BuildNumber").equals("DEV")) {
                    		BUILD_TYPE = "DevBuild";
                    	}
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
        return versionLatest;
    }

    public static boolean isOutdated()
    {
        return outdated;
    }

    public static String getBuildType()
    {
        return BUILD_TYPE;
    }

}
