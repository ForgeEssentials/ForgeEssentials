ForgeEssentialsMain
===================
The code on this branch is for Minecraft 1.7.10. File issues at the [Issue Tracker][issues].

[![Build Status][master]][travis]

*******************
####Join us on our IRC channel: [#forgeessentials][irc] on EsperNet.####
*******************

The Forge Essentials project consists of a permissions and protection system to be used with forge servers, incorporating a certain extent of WorldEdit functionality. Development by the team has officially stalled, though PRs are welcome, be it to fix bugs, or add modules, or whatever.

#### This repo is for a port to Minecraft 1.7.10. ####

There are compiled builds [here][build].
Betas are to be considered very unstable. Approach with caution. Please make backups before attempting to use this on a live server.

*More information can be found at the [wiki][wiki] (wip - rewrite in progress) .*

Development Setup
=================
Set up for ForgeEssentials development is similar to any other ForgeGradle-based mod. Please refer to ForgeGradle instructions for full instructions on importing a project.

##### Basic Steps: #####

1. Clone this repo.
2. Download the respective MinecraftForge ForgeGradle version into the repo.
3. Open a terminal, and navigate to the cloned repo.
4. Depending on your system, run one of the following commands:
    * For Windows Command Prompt: `gradlew.bat setupDecompWorkspace <eclipse|idea> --refresh-dependencies` 
    * Windows PowerShell:`./gradlew.bat setupDecompWorkspace <eclipse|idea> --refresh-dependencies` 
    * For Unix: `./gradlew setupDecompWorkspace <eclipse|idea> --refresh-dependencies`
5. Open the newly created workspace in your IDE, and you will be good to go!

*******************
##### Additional Step (VERY IMPORTANT, OR EVENTS WILL NOT FIRE!!!) #####

1. Open your IDE run configs, and select the preconfigured "Minecraft Client" run configuration.
2. Under "VM Arguments", add the following:
    `-Dfml.coreMods.load=com.forgeessentials.core.preloader.FEPreLoader`
3. Repeat for the "Minecraft Server" run configuration.

*******************

#### Notes:  ####

1. FE does not currently support the ForgeGradle run tasks. Use your IDE instead.
2. Please consider squashing all commits before initially submitting pull requests.
   This may be done using `git rebase -i head~(number of commits)`.
3. For a local `.gitignore` use `.git/info/exclude`.
4. If you have any questions, hop on [IRC][irc].
5. ForgeGradle versions for Minecraft 1.7 recommends Java 7.

Downloads
=========
If you would like test builds, go [here][build]. Untested and probably buggy!

FE Team Members:
================
##### Active: #####
- <a href="https://github.com/luacs1998">luacs1998</a> (lead developer)
- <a href="https://github.com/olee">olee</a>
- <a href="https://github.com/helinus">Helinus</a>
- <a href="https://github.com/Malkierian">Malkierian</a>
- <a href="https://github.com/RlonRyan">RlonRyan</a>

##### Retired: #####
- <a href="https://github.com/AbrarSyed">AbrarSyed</a>
- <a href="https://github.com/Bob-A-Red-Dino">Bob A Red Dino</a>
- <a href="https://github.com/bspkrs">bspkrs</a>
- <a href="https://github.com/MysteriousAges">MysteriousAges</a>
- <a href="https://github.com/dries007">Dries007</a>
- <a href="https://github.com/Weneg">Weneg</a>
- <a href="https://github.com/Jgdovin">Jgdovin</a>
- <a href="https://github.com/jovino">Jovino</a>

Links:
================
- [Espernet][irc]
- [Builds][build]
- [Travis][travis]
- [Issues][issues]
- [Wiki][wiki]

[irc]: http://webchat.esper.net/?channels=forgeessentials&prompt=1 "#ForgeEssentials"
[build]: http://198.23.242.205:8080/job/ForgeEssentials "Jenkins Build Server"
[travis]: https://travis-ci.org/ForgeEssentials/ForgeEssentialsMain "Travis Build Server"
[master]: https://travis-ci.org/ForgeEssentials/ForgeEssentialsMain.svg?branch=develop "Travis Build Server"
[issues]: https://github.com/ForgeEssentials/ForgeEssentialsMain/issues "ForgeEssentials Issue Tracker"
[wiki]: https://github.com/ForgeEssentials/ForgeEssentialsMain/wiki "ForgeEssentials Wiki"
