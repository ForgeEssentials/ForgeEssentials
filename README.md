ForgeEssentialsMain
===================

The code on this branch is for Minecraft 1.7.10. File issues at the issue tracker.

[![Build Status][master]][travis]

*******************
####Join us on our IRC channel: [#forgeessentials][irc] on EsperNet.####
*******************

The Forge Essentials project consists of a permissions and protection system to be used with forge servers, incorporating a certain extent of WorldEdit functionality. Development by the team has officially stalled, though PRs are welcome, be it to fix bugs, or add modules, or whatever.

*More information can be found at the (wip - rewrite in progress) [wiki](https://github.com/ForgeEssentials/ForgeEssentialsMain/wiki).*

#### This repo is for a port to Minecraft 1.7.10. ####
There are compiled builds [here][build].
Betas are to be considered very unstable. Approach with caution. Please make backups before attempting to use this on a live server.

Development Setup
=================
Set up for ForgeEssentials development is simllar to any other ForgeGradle-based mod. Please refer to ForgeGradle instructions for importing a project.

*******************
##### Additional Step (VERY IMPORTANT, OR EVENTS WILL NOT FIRE!!!) #####

1. Open your IDE run configs, and select the preconfigured "Minecraft Client" run configuration.
2. Under "VM Arguments", add the following:
    `-Dfml.coreMods.load=com.forgeessentials.core.preloader.FEPreLoader`
3. Repeat for the "Minecraft Server" run configuration.

*******************

#### Note:  ####

1. FE CURRENTLY DOES NOT SUPPORT THE FORGEGRADLE RUN TASKS.
2. Please consider squashing all commits before initially submitting pull requests.
2. For a local .gitignore use .git/info/exclude
3. If you have any questions, hop on [IRC][irc].
4. ForgeGradle versions for Minecraft 1.7 recommends Java 7.

<br>

Downloads
=========

If you would like test builds, go [here][build]. Untested and probably buggy!

FE Team Members:
================
<a href="https://github.com/luacs1998">luacs1998</a> (lead developer)

<a href="https://github.com/olee">olee</a>

<a href="https://github.com/helinus">Helinus</a>

<a href="https://github.com/Malkierian">Malkierian</a>

Retired:

<a href="https://github.com/AbrarSyed">AbrarSyed</a>

<a href="https://github.com/Bob-A-Red-Dino">Bob A Red Dino</a>

<a href="https://github.com/bspkrs">bspkrs</a>

<a href="https://github.com/MysteriousAges">MysteriousAges</a>

<a href="https://github.com/dries007">Dries007</a>

<a href="https://github.com/Weneg">Weneg</a>

<a href="https://github.com/Jgdovin">Jgdovin</a>

<a href="https://github.com/RlonRyan">RlonRyan</a>

<a href="https://github.com/jovino">Jovino</a>

[irc]: http://webchat.esper.net/?channels=forgeessentials&prompt=1
[build]: http://198.23.242.205:8080/job/ForgeEssentials/
[travis]: https://travis-ci.org/ForgeEssentials/ForgeEssentialsMain
[master]: https://travis-ci.org/ForgeEssentials/ForgeEssentialsMain.svg?branch=develop
