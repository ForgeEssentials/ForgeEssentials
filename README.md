ForgeEssentialsMain
===================

The code on this branch is for Minecraft 1.7.10. File issues at the issue tracker.

*******************
####Join us on our IRC channel: [#forgeessentials](http://webchat.esper.net/?channels=forgeessentials&prompt=1) on EsperNet.####

*******************

The Forge Essentials project consists of a permissions and protection system to be used with forge servers, incorporating a certain extent of WorldEdit functionality. Development by the team has officially stalled, though PRs are welcome, be it to fix bugs, or add modules, or whatever.

*More information can be found at the (wip - rewrite in progress) [wiki](https://github.com/ForgeEssentials/ForgeEssentialsMain/wiki).*

####This repo is for a port to Minecraft 1.7.10. There are compiled builds [here](http://198.23.242.205:8080/job/ForgeEssentials/). Betas are to be considered very unstable. Approach with caution. Please make backups before attempting to use this on a live server.####

<br>

Development Setup
=================
Set up for ForgeEssentials development is simalar to any other ForgeGradle-based mod.

*Note: ForgeGradle versions for Minecraft 1.7 recommends Java 7.*

1. Clone this repo.
2. Open a terminal, and navigate to the cloned repo.
3. Prepare your environment by running the command for your system. To prepare IntelliJ, replace 'eclipse' with 'idea'.
 * 'gradlew.bat setupDecompWorkspace eclipse' (Windows Command Prompt)
 * './gradlew.bat setupDecompWorkspace eclipse' (Windows PowerShell)
 * './gradlew setupDecompWorkspace eclipse' (Unix)
4. Open the newly created workspace, and you will be good to go!

*Notes:*
1. Please consider squashing all commits before initially submitting pull requests.
2. For a local .gitignore use .git/info/exclude

<br>

Downloads
=========

If you would like test builds, go [here](http://198.23.242.205:8080/job/ForgeEssentials/). Untested and probably buggy!

Troubleshooting
===============

_gradlew fails due to insufficient memory_
 * Open the gradlew file used by your platform. In the quotes beside DEFAULT_JVM_OPTS, enter *-Xmx4096M*, where 4096 is the amount of memory you wish to allocate.

_When I launch my debugger, I get the exception "ForgeEssentialsClient should not be installed on a server!"_
 * This happens if developer mode has not been enabled. Create an environmental variable named "forgeessentials.developermode" with the value "true".

FE Team Members:
================
<a href="https://github.com/luacs1998">luacs1998</a> (lead developer)

<a href="https://github.com/olee">olee</a>

<a href="https://github.com/RlonRyan">RlonRyan</a>

<a href="https://github.com/helinus">Helinus</a>

<a href="https://github.com/Malkierian">Malkierian</a>

Retired:

<a href="https://github.com/AbrarSyed">AbrarSyed</a>  (inactive)

<a href="https://github.com/Bob-A-Red-Dino">Bob A Red Dino</a>  (inactive)

<a href="https://github.com/bspkrs">bspkrs</a> (inactive)

<a href="https://github.com/MysteriousAges">MysteriousAges</a> (inactive)

<a href="https://github.com/dries007">Dries007</a> (inactive)

<a href="https://github.com/Weneg">Weneg</a> (inactive)

<a href="https://github.com/Jgdovin">Jgdovin</a>  (inactive)

<a href="https://github.com/jovino">Jovino</a> (inactive)
