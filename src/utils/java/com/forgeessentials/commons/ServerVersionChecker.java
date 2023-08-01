package com.forgeessentials.commons;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.internal.storage.dfs.DfsRepositoryDescription;
import org.eclipse.jgit.internal.storage.dfs.InMemoryRepository;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.transport.RefSpec;

public class ServerVersionChecker {

	public static void doCheckLatestVersion()
    {
        try
        {
            DfsRepositoryDescription repoDesc = new DfsRepositoryDescription();
            InMemoryRepository repo = new InMemoryRepository(repoDesc);
            Git git = new Git(repo);

            if (!BuildInfo.continueRunning)
            {
                git.close();
                repo.close();
                return;
            }
            git.fetch().setRemote("https://github.com/ForgeEssentials/ForgeEssentials")
                    .setRefSpecs(new RefSpec("+refs/tags/*:refs/tags/*")).setInitialBranch("HEAD").call();
            repo.getObjectDatabase();
            List<Ref> call = git.tagList().call();
            if (call.isEmpty())
            {
            	BuildInfo.febuildinfo.error("Unable to retrieve version info from API");
                git.close();
                repo.close();
                BuildInfo.cancelVersionCheck(false);
                return;
            }
            if (!BuildInfo.continueRunning)
            {
                git.close();
                repo.close();
                return;
            }
            for (Ref ref : call)
            {
                String[] values = StringUtils.split(StringUtils.remove(ref.getName(), "refs/tags/"), '.');

                if (values.length != 3 || !values[0].matches("[0-9]+") || !values[1].matches("[0-9]+")
                        || !values[2].matches("[0-9]+") || !values[0].equals(BuildInfo.BASE_VERSION))
                {
                    continue;
                }
                if (!BuildInfo.continueRunning)
                {
                    git.close();
                    repo.close();
                    return;
                }
                BuildInfo.febuildinfo.debug("Found valid update tag: " + StringUtils.remove(ref.getName(), "refs/tags/"));
                if (Integer.parseInt(values[1]) > Integer.parseInt(BuildInfo.MAJOR_VERSION)
                        && Integer.parseInt(values[1]) > BuildInfo.majorNumberLatest)
                {
                	BuildInfo.majorNumberLatest = Integer.parseInt(values[1]);
                	BuildInfo.minorNumberLatest = Integer.parseInt(values[2]);
                    continue;
                }
                if (!BuildInfo.continueRunning)
                {
                    git.close();
                    repo.close();
                    return;
                }
                if (Integer.parseInt(values[2]) > BuildInfo.MINOR_VERSION && Integer.parseInt(values[1]) > BuildInfo.minorNumberLatest)
                {
                	BuildInfo.majorNumberLatest = Integer.parseInt(values[1]);
                	BuildInfo.minorNumberLatest = Integer.parseInt(values[2]);
                    continue;
                }
                if (!BuildInfo.continueRunning)
                {
                    git.close();
                    repo.close();
                    return;
                }
            }
            git.close();
            repo.close();
            BuildInfo.cancelVersionCheck(false);
        }
        catch (GitAPIException e)
        {
        	BuildInfo.febuildinfo.error("Unable to retrieve version info from API");
        }
    }
}
