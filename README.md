ForgeEssentialsMain [![Build Status](https://travis-ci.org/ForgeEssentials/ForgeEssentialsMain.svg?branch=develop)](https://travis-ci.org/ForgeEssentials/ForgeEssentialsMain)
===================

The code on this branch is for Minecraft 1.7.10. File issues at the issue tracker.

*******************

The ForgeEssentials project consists many server features,  
especially a powerful permissions and protection system:
 - Permission management
 - Protection (WorldGuard)
 - Automatic backup management
 - Multiworld (in bukkit aka. multiverse)
 - Deathchests
 - Huge collection of utility commands
 - Remote server access and management with Andoid-app (WIP)
 - Any many more!!!

*******************

#### For more information and tutorials, please read the [__WIKI__](https://github.com/ForgeEssentials/ForgeEssentialsMain/wiki) ####

##### If you need help or have suggestions, join us on our IRC channel [*#forgeessentials*](http://webchat.esper.net/?channels=forgeessentials&prompt=1) on [esper.net](http://esper.net). #####

*******************

#### Download for the latest builds on [Jenkins](http://198.23.242.205:8080/job/ForgeEssentials/). or releases on [Curseforge](http://minecraft.curseforge.com/mc-mods/74735) ####

ForgeEssentials for Minecraft 1.7.10 is still in **beta**!  
Betas are to be considered unstable. Approach with caution. Please make backups before using / updating on a live server.

*******************

Development Setup
=================
Set up for ForgeEssentials development is simllar to any other ForgeGradle-based mod. Please refer to ForgeGradle instructions for importing a project.

*Note: ForgeGradle versions for Minecraft 1.7 recommends Java 7.*

Additional step (VERY IMPORTANT, OR EVENTS WILL NOT FIRE!!!)

Open your IDE run configs, and select the preconfigured "Minecraft Client" run configuration.

Under "VM Arguments", add the following:

    -Dfml.coreMods.load=com.forgeessentials.core.preloader.FEPreLoader

Repeat for the "Minecraft Server" run configuration.

**FE CURRENTLY DOES NOT SUPPORT THE FORGEGRADLE RUN TASKS.**

*Notes:*
1. Please consider squashing all commits before initially submitting pull requests.
2. For a local .gitignore use .git/info/exclude
3. If you have any questions, hop on IRC.

FE Team Members:
================
<a href="https://github.com/luacs1998">luacs1998</a> (lead developer)

<a href="https://github.com/olee">olee</a> (permissions-guru, protection, multiworld)

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
