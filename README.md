ForgeEssentialsMain [![Build Status][develop]][travis]
===================
The code on this branch is for Minecraft 1.7.10. File issues at the [Issue Tracker][issues].

*******************
####Join us on our IRC channel: [#forgeessentials][irc] on EsperNet.####
*******************

The Forge Essentials project consists of a permissions and protection system to be used with forge servers, incorporating a certain extent of [WorldEdit][worldedit] functionality. Development by the team has officially stalled, though PRs are welcome, be it to fix bugs, or add modules, or whatever.

#### This repo is for a port to Minecraft 1.7.10. ####

There are compiled builds [available here][build-stable].
Betas are to be considered very unstable. Approach with caution. Please make backups before attempting to use this on a live server.

*More information can be found at the [wiki][wiki] (wip - rewrite in progress) .*

Development Setup
=================
Set up for ForgeEssentials development is similar to any other [ForgeGradle][forge]-based mod. Please refer to [ForgeGradle Instructions][FGHelp] for full instructions on importing a project.

*******************
##### Basic Steps: #####

1. Clone this repo.
2. Download the respective [MinecraftForge ForgeGradle Version][FGVersions] into the repo.
3. Open a terminal, and navigate to the cloned repo.
4. Depending on your system, run one of the following commands:
    * For Windows Command Prompt: `gradlew.bat setupDecompWorkspace <eclipse|idea> --refresh-dependencies` 
    * Windows PowerShell:`./gradlew.bat setupDecompWorkspace <eclipse|idea> --refresh-dependencies` 
    * For Unix: `./gradlew setupDecompWorkspace <eclipse|idea> --refresh-dependencies`
5. Open the newly created workspace in your IDE, and you will be good to go!

*******************
##### Additional Steps (VERY IMPORTANT, OR EVENTS WILL NOT FIRE!!!) #####

1. Open your IDE run configs, and select the preconfigured "Minecraft Client" run configuration.
2. Under "VM Arguments", add the following:
    `-Dfml.coreMods.load=com.forgeessentials.core.preloader.FEPreLoader`
3. Repeat for the "Minecraft Server" run configuration.

*******************

##### Notes:  #####

1. FE does not currently support the [ForgeGradle][forge] run tasks. Use your IDE instead.
2. Please consider squashing all commits before initially submitting pull requests.
   This may be done using `git rebase -i head~(number of commits)`.
3. For a local `.gitignore` use `.git/info/exclude`.
4. If you have any questions, hop on [IRC][irc].
5. [ForgeGradle versions][FGVersions] for Minecraft 1.7 recommends [Java 7][java].

Downloads
=========
If you would like the latest stable release, [go here][build-stable].
If you would like test builds, [go here][build-beta]. Please know they are untested and probably buggy!

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
[build-stable]: http://minecraft.curseforge.com/mc-mods/74735-forge-essentials "Stable Builds"
[build-beta]: http://198.23.242.205:8080/job/ForgeEssentials "Jenkins Build Server"
[travis]: https://travis-ci.org/ForgeEssentials/ForgeEssentialsMain "Travis Build Server"
[master]: https://travis-ci.org/ForgeEssentials/ForgeEssentialsMain.svg?branch=master "Travis Build Server"
[develop]: https://travis-ci.org/ForgeEssentials/ForgeEssentialsMain.svg?branch=develop "Travis Build Server"
[issues]: https://github.com/ForgeEssentials/ForgeEssentialsMain/issues "ForgeEssentials Issue Tracker"
[wiki]: https://github.com/ForgeEssentials/ForgeEssentialsMain/wiki "ForgeEssentials Wiki"

[forge]: http://www.minecraftforge.net/ "Minecraft Forge"
[FGHelp]: http://www.minecraftforge.net/forum/index.php/topic,14048.0.html "ForgeGradle Tutorial"
[FGVersions]: http://files.minecraftforge.net/ "Minecraft Forge"
[java]: http://www.oracle.com/technetwork/java/javase/downloads/jdk7-downloads-1880260.html "Java 7 SDK"
[worldedit]: http://wiki.sk89q.com/wiki/WorldEdit "World Edit"
