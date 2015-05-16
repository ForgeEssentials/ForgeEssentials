##Developing on FE itself/Building FE (not for the faint of heart)


You need to install [msysgit](http://git-scm.com/) first. Once you have installed msysgit, open Bash (or cmd or whatever you told the installer to use) and run the following commands:

    git clone https://github.com/ForgeEssentials/ForgeEssentialsMain.git
    git submodule init
    git submodule update
    
These commands download the FE source code and configure it as the buildscripts expect the repos to be.

From here, you may refer to ForgeGradle instructions for importing a project.

*Note: ForgeGradle versions for Minecraft 1.7 recommends Java 7.*

You also need to add the hibernate-jpamodelgen.jar which is referenced in the project to your IDE as annotation processor, or it will complain that it cannot find classes like `Action_`.

Additional step (VERY IMPORTANT, OR EVENTS WILL NOT FIRE!!!)

Open your IDE run configs, and select the preconfigured "Minecraft Client" run configuration.

Under "Program Arguments", add the following:

    --tweakClass com.forgeessentials.core.preloader.FELaunchHandler

Repeat for the "Minecraft Server" run configuration.

**FE CURRENTLY DOES NOT SUPPORT THE FORGEGRADLE RUN TASKS.**

*Notes:*
1. Please consider squashing all commits before initially submitting pull requests.

2. For a local .gitignore use .git/info/exclude

3. If you have any questions, hop on IRC.
