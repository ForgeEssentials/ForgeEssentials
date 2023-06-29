package com.forgeessentials.commons;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.internal.storage.dfs.DfsRepositoryDescription;
import org.eclipse.jgit.internal.storage.dfs.InMemoryRepository;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.transport.RefSpec;

import com.forgeessentials.commons.events.NewVersionEvent;

import net.minecraftforge.common.MinecraftForge;

public abstract class BuildInfo {

	public static final Logger febuildinfo = LogManager.getLogger("FEUpdateChecker");

	private static final String BUILD_TYPE = "alpha";

	private static String buildHash = "N/A";

	/* ------------------------------------------------------------ */

	public static boolean needCheckVersion = false;

	private static int minorNumberLatest = 0;

	private static int majorNumberLatest = 0;

	private static Thread checkVersionThread;

	// private static Thread checkBuildTypesThread;

	// private static Properties buildTypes = new Properties();

	/* ------------------------------------------------------------ */

	private static final String MC_BASE_VERSION = "@_MCVERSION_@";

	/**
	 * Base version is the 16 in 16.0.x
	 */
	private static final String BASE_VERSION = "@_BASEVERSION_@";

	/**
	 * Major version is the 0 in 16.0.x
	 */
	private static final String MAJOR_VERSION = "@_MAJORVERSION_@";

	/**
	 * Minor version is the x in 16.0.x
	 */
	private static int MINOR_VERSION = 0;

	private static boolean continueRunning = true;

	/* ------------------------------------------------------------ */

	public static void startVersionChecks() {
		if (needCheckVersion) {
			// Check for latest version asap
			checkVersionThread = new Thread(new Runnable() {
				@Override
				public void run() {
					doCheckLatestVersion();
				}
			}, "FEversionCheckThread");
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

	public static void getBuildInfo(File jarFile) {
		try {
			if (jarFile != null) {
				try (JarFile jar = new JarFile(jarFile)) {
					Manifest manifest = jar.getManifest();
					buildHash = manifest.getMainAttributes().getValue("BuildID");
					try {
						MINOR_VERSION = Integer.parseInt(manifest.getMainAttributes().getValue("BuildNumber"));
					} catch (NumberFormatException e) {
						MINOR_VERSION = 0;
					}
				}
			} else {
				febuildinfo.error(String.format("Unable to get FE version information (dev env / %s)", BASE_VERSION));
			}
		} catch (IOException e1) {
			febuildinfo.error(String.format("Unable to get FE version information (%s)", BASE_VERSION));
		}
	}

	private static void doCheckLatestVersion() {
		try {
			DfsRepositoryDescription repoDesc = new DfsRepositoryDescription();
			InMemoryRepository repo = new InMemoryRepository(repoDesc);
			Git git = new Git(repo);
			if (!continueRunning) {
				git.close();
				repo.close();
				return;
			}
			git.fetch().setRemote("https://github.com/ForgeEssentials/ForgeEssentials")
					.setRefSpecs(new RefSpec("+refs/tags/*:refs/tags/*")).setInitialBranch("HEAD").call();
			repo.getObjectDatabase();
			List<Ref> call = git.tagList().call();
			if (call.isEmpty()) {
				febuildinfo.error("Unable to retrieve version info from API");
				git.close();
				repo.close();
				cancelVersionCheck(false);
				return;
			}
			if (!continueRunning) {
				git.close();
				repo.close();
				return;
			}
			for (Ref ref : call) {
				String[] values = StringUtils.split(StringUtils.remove(ref.getName(), "refs/tags/"), '.');
				if (values.length != 3 || !values[0].matches("[0-9]+") || !values[1].matches("[0-9]+")
						|| !values[2].matches("[0-9]+") || !values[0].equals(BASE_VERSION)) {
					continue;
				}
				if (!continueRunning) {
					git.close();
					repo.close();
					return;
				}
				febuildinfo.debug("Found valid update tag: " + StringUtils.remove(ref.getName(), "refs/tags/"));
				if (Integer.parseInt(values[1]) > Integer.parseInt(MAJOR_VERSION)
						&& Integer.parseInt(values[1]) > majorNumberLatest) {
					majorNumberLatest = Integer.parseInt(values[1]);
					minorNumberLatest = Integer.parseInt(values[2]);
					continue;
				}
				if (!continueRunning) {
					git.close();
					repo.close();
					return;
				}
				if (Integer.parseInt(values[2]) > MINOR_VERSION && Integer.parseInt(values[1]) > minorNumberLatest) {
					majorNumberLatest = Integer.parseInt(values[1]);
					minorNumberLatest = Integer.parseInt(values[2]);
					continue;
				}
				if (!continueRunning) {
					git.close();
					repo.close();
					return;
				}
			}
			git.close();
			repo.close();
			cancelVersionCheck(false);
		} catch (GitAPIException e) {
			febuildinfo.error("Unable to retrieve version info from API");
		}
	}

	private static void joinVersionThread() {
		if (checkVersionThread == null)
			return;
		try {
			checkVersionThread.join();
			checkVersionThread = null;
		} catch (InterruptedException e) {
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
	 * Set to null, which will disable joining of the thread and kill any possible
	 * delay
	 **/
	public static void cancelVersionCheck(boolean isStopping) {
		continueRunning = false;
		if (majorNumberLatest != 0 && !isStopping) {
			MinecraftForge.EVENT_BUS.post(new NewVersionEvent());
		}
	}

	public static String getCurrentVersion() {
		return BASE_VERSION + '.' + MAJOR_VERSION + '.' + MINOR_VERSION;
	}

	public static String getBuildHash() {
		return buildHash;
	}

	public static String getMinecraftVersion() {
		return MC_BASE_VERSION;
	}

	public static String getLatestVersion() {
		joinVersionThread();
		return BASE_VERSION + '.' + majorNumberLatest + '.' + minorNumberLatest;
	}

	public static boolean isOutdated() {
		joinVersionThread();
		return majorNumberLatest > Integer.parseInt(MAJOR_VERSION) || minorNumberLatest > MINOR_VERSION;
	}

	public static String getBuildType() {
		// joinBuildTypeThread();
		// return buildTypes.getProperty(Integer.toString(buildNumber),
		// BUILD_TYPE_NIGHTLY);
		return BUILD_TYPE;
	}

}
