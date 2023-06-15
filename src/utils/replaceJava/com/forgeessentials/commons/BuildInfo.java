package com.forgeessentials.commons;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.internal.storage.dfs.DfsRepositoryDescription;
import org.eclipse.jgit.internal.storage.dfs.InMemoryRepository;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;

public abstract class BuildInfo
{

    private static final String BUILD_TYPE_NIGHTLY = "nightly";

    private static String buildHash = "N/A";

    /* ------------------------------------------------------------ */

    public static boolean needCheckVersion = false;

    private static int minorNumberLatest = 0;

    private static int majorNumberLatest = 0;

    private static Thread checkVersionThread;

    //private static Thread checkBuildTypesThread;

    //private static Properties buildTypes = new Properties();

    /* ------------------------------------------------------------ */

    private static final String MC_BASE_VERSION = "@_MCVERSION_@";

    /**
     * Base version is the 16 in 16.0.x
     * */
    private static final String BASE_VERSION = "@_BASEVERSION_@";

    /**
     * Major version is the 0 in 16.0.x
     * */
    private static final String MAJOR_VERSION = "@_MAJORVERSION_@";

    /**
     * Minor version is the x in 16.0.x
     * */
    private static int MINOR_VERSION = 0;

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
                    doCheckLatestVersion();
                }
            });
            checkVersionThread.start();

//            checkBuildTypesThread = new Thread(new Runnable() {
//                @Override
//                public void run()
//                {
//                    doCheckBuildTypes();
//                }
//            });
//            checkBuildTypesThread.start();
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
        try {
            DfsRepositoryDescription repoDesc = new DfsRepositoryDescription();
            InMemoryRepository repo = new InMemoryRepository(repoDesc);
            Git git = new Git(repo);
            git.fetch()
                    .setRemote("https://github.com/ForgeEssentials/ForgeEssentials")
                    .call();
//            Git git = Git.cloneRepository()
//                    .setURI("https://github.com/eclipse/jgit.git")
//                    .setDirectory("/path/to/repo")
//                    .call();
            repo.getObjectDatabase();
            List<Ref> call = git.tagList().call();
            for (Ref ref : call) {
                System.out.println("Tag: " + ref + " " + ref.getName() + " " + ref.getObjectId().getName() +
                        (ref.getPeeledObjectId() == null ? "" : ", peeled: " + ref.getPeeledObjectId().getName()));
    
                // fetch all commits for this tag
                LogCommand log = git.log();
    
                Ref peeledRef = repo.getRefDatabase().peel(ref);
                if(peeledRef.getPeeledObjectId() != null) {
                    log.add(peeledRef.getPeeledObjectId());
                } else {
                    log.add(ref.getObjectId());
                }
    
                Iterable<RevCommit> logs = log.call();
                for (RevCommit rev : logs) {
                    System.out.println("Commit: " + rev /* + ", name: " + rev.getName() + ", id: " + rev.getId().getName() */);
                }
            }
            git.close();
        }
        catch (IOException | GitAPIException e)
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

//    private static void doCheckBuildTypes()
//    {
//        try
//        {
//            URL buildInfoUrl = new URL("http://files.forgeessentials.com/buildtypes_" + MC_BASE_VERSION + ".txt");
//            URLConnection con = buildInfoUrl.openConnection();
//            con.setConnectTimeout(6000);
//            con.setReadTimeout(12000);
//            con.connect();
//            buildTypes.load(con.getInputStream());
//        }
//        catch (IOException e)
//        {
//            System.err.println("Unable to retrieve build types");
//        }
//    }

//    private static void joinBuildTypeThread()
//    {
//        if (checkBuildTypesThread == null)
//            return;
//        try
//        {
//            checkBuildTypesThread.join();
//            checkBuildTypesThread = null;
//        }
//        catch (InterruptedException e)
//        {
//            /* do nothing */
//        }
//    }

    /**
     * Set to null, which will disable joining of the thread and kill any possible delay
     **/
    public static void cancelVersionCheck()
    {
        checkVersionThread = null;
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
        joinVersionThread();
        return BASE_VERSION + '.' + majorNumberLatest + '.' + minorNumberLatest;
    }

    public static boolean isOutdated()
    {
        joinVersionThread();
        return majorNumberLatest>Integer.parseInt(MAJOR_VERSION) || minorNumberLatest>MINOR_VERSION;
    }

    public static String getBuildType()
    {
        //joinBuildTypeThread();
        //return buildTypes.getProperty(Integer.toString(buildNumber), BUILD_TYPE_NIGHTLY);
        return BUILD_TYPE_NIGHTLY;
    }

}
