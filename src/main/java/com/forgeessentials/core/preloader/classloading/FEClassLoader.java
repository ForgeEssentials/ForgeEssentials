package com.forgeessentials.core.preloader.classloading;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.launchwrapper.LaunchClassLoader;

public class FEClassLoader {

    private static final FilenameFilter jarFilter = new FilenameFilter() {
        @Override
        public boolean accept(File dir, String name) {
            return name.endsWith(".jar");
        }
    };

    public void runClassLoad(LaunchClassLoader classloader, File root)
    {
        File lib = new File(root, "lib/");
        if (!lib.exists())
        {
            lib.mkdirs();
        }
        for (File f : lib.listFiles(jarFilter))
        {
            if (f != null)
            {
                try
                {
                    classloader.addURL(f.toURI().toURL());
                    System.out.println("[ForgeEssentials] Loaded library file " + f.getAbsolutePath());
                }
                catch (MalformedURLException e)
                {
                    throw new RuntimeException("Could not add library file " + f.getAbsolutePath() + ", there may be a classloading problem.");
                }
            }
        }

        File module = new File(root, "modules/");
        if (!module.exists())
        {
            module.mkdirs();
        }
        for (File f : module.listFiles(jarFilter))
        {
            if (f != null)
            {
                try
                {
                    classloader.addURL(f.toURI().toURL());
                }
                catch (MalformedURLException e)
                {
                    System.err.println("[ForgeEssentials] Could not add module file " + f.getAbsolutePath() + ", there may be a class loading problem.");
                }
            }
        }
        System.out.println("[ForgeEssentials] Loaded " + module.listFiles().length + " modules");

        checkLibs();
    }

    private static String[] compulsoryLibs = { "com.mysql.jdbc.Driver", "org.pircbotx.PircBotX", "org.h2.Driver" };

    public void checkLibs()
    {
        String prop = System.getProperty("forgeessentials.developermode");
        if (prop != null && prop.equals("true"))
        { // FOR DEVS ONLY! THAT IS WHY IT IS A PROPERTY!!!

            System.out.println("[ForgeEssentials] Running in developer mode. Libraries will not be checked.");
            return;
        }
        List<String> erroredLibs = new ArrayList<String>();
        for (String clazz : compulsoryLibs)
        {
            try
            {
                Class.forName(clazz);
                System.out.println("[ForgeEssentials] Found library " + clazz);
            }
            catch (ClassNotFoundException cnfe)
            {
                erroredLibs.add(clazz);
            }
        }
        if (!erroredLibs.isEmpty())
        {
            for (Object error : erroredLibs.toArray())
            {
                System.err.println(error);
            }
            throw new RuntimeException("[ForgeEssentials] You are missing one or more library files. See your FML log for details.");
        }

    }

}
