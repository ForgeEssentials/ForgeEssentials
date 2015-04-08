package com.forgeessentials.core.preloader.classloading;

import com.forgeessentials.core.preloader.FELaunchHandler;
import net.minecraft.launchwrapper.LaunchClassLoader;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FEClassLoader
{

    private static final FilenameFilter jarFilter = new FilenameFilter()
    {
        @Override
        public boolean accept(File dir, String name)
        {
            return name.endsWith(".jar");
        }
    };

    private File FEfolder;

    private boolean reExtract;

    public void extractLibs(File mcLocation, LaunchClassLoader cl)
    {
        FEfolder = new File(mcLocation, "ForgeEssentials/");
        if (!FEfolder.exists())
        {
            FEfolder.mkdirs();
        }
        File libfolder = new File(FEfolder, "lib/");
        if (!libfolder.exists())
        {
            System.out.println("[ForgeEssentials] Could not find library folder - will create new one and re-extract libraries.");
            reExtract = true;
        }

        //verify if we can find the current files
        runClassLoad(FEfolder, cl);
        checkLibs(cl);

        if (FELaunchHandler.runtimeDeobfEnabled)
        {
            doActualExtract(mcLocation);

            // after remediation, re-verify files

            runClassLoad(FEfolder, cl);
            checkLibs(cl);

        }

    }

    public void runClassLoad(File root, LaunchClassLoader cl)
    {
        File lib = new File(root, "lib/");
        if (!lib.exists())
        {
            lib.mkdirs();
            System.err.println("[ForgeEssentials] Something happened that shouldn't have happened. Trying to recover.");
        }
        for (File f : lib.listFiles(jarFilter))
        {
            if (f != null)
            {
                try
                {

                    cl.addURL(f.toURI().toURL());
                    System.out.println("[ForgeEssentials] Loaded library file " + f.getAbsolutePath());
                }
                catch (MalformedURLException e)
                {
                    throw new RuntimeException("Could not add library file " + f.getAbsolutePath() + ", there may be a classloading problem.");
                }
            }
        }

        // retained, for 3rdparty module support

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
                    cl.addURL(f.toURI().toURL());
                }
                catch (MalformedURLException e)
                {
                    System.err.println("[ForgeEssentials] Could not add module file " + f.getAbsolutePath() + ", there may be a class loading problem.");
                }
            }
        }
        System.out.println("[ForgeEssentials] Loaded " + module.listFiles().length + " modules");

    }

    private static String[] compulsoryLibs = { "com.mysql.jdbc.Driver", "org.pircbotx.PircBotX", "org.h2.Driver" };

    public void checkLibs(LaunchClassLoader cl)
    {
        List<String> erroredLibs = new ArrayList<String>();
        for (String clazz : compulsoryLibs)
        {
            try
            {
                cl.findClass(clazz);
                System.out.println("[ForgeEssentials] Found library " + clazz);
            }
            catch (ClassNotFoundException cnfe)
            {
                erroredLibs.add(clazz);
            }
        }
        if (!erroredLibs.isEmpty())
        {
            System.err.println("[ForgeEssentials] You are missing the following library files.");
            for (Object error : erroredLibs.toArray())
            {
                System.err.println(error);
            }
            System.err.println("[ForgeEssentials] We will now re-extract the missing library files.");
            reExtract = true;

            //throw new RuntimeException("[ForgeEssentials] You are missing one or more library files. See your FML log for details.");
        }

    }

    public void doActualExtract(File mcLocation)
    {
        System.out.println("[ForgeEssentials] Checking if we need to extract libraries");
        if (reExtract)
        {
            System.out.println("[ForgeEssentials] Extracting libraries");

            // clear old libs
            File lib = new File(FEfolder, "lib/");
            if (lib.exists())
            {
                lib.delete();
            }

            try
            {
                ZipInputStream zin = new ZipInputStream(getClass().getResourceAsStream("/libraries.zip"));
                ZipEntry entry;
                String name, dir;
                while ((entry = zin.getNextEntry()) != null)
                {
                    name = entry.getName();
                    System.out.println("[ForgeEssentials] Now extracting file " + name);
                    if (entry.isDirectory())
                    {
                        mkdirs(mcLocation, name);
                        continue;
                    }
                    dir = dirpart(name);
                    if (dir != null)
                        mkdirs(mcLocation, dir);

                    extractFile(zin, mcLocation, name);
                }
                zin.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * ZIP utils
     */

    private static final int BUFFER_SIZE = 4096;

    private static void extractFile(ZipInputStream in, File outdir, String name) throws IOException
    {
        byte[] buffer = new byte[BUFFER_SIZE];
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(new File(outdir, name)));
        int count = -1;
        while ((count = in.read(buffer)) != -1)
            out.write(buffer, 0, count);
        out.close();
    }

    private static void mkdirs(File outdir, String path)
    {
        File d = new File(outdir, path);
        if (!d.exists())
            d.mkdirs();
    }

    private static String dirpart(String name)
    {
        int s = name.lastIndexOf(File.separatorChar);
        return s == -1 ? null : name.substring(0, s);
    }

}
