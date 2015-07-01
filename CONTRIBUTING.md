## Using the FE API

[See here for more information.](https://github.com/ForgeEssentials/ForgeEssentialsMain/wiki/Developer-documentation)

## Developing on FE itself/Building FE (not for the faint of heart)

You need to install [msysgit](http://git-scm.com/) first. Once you have installed msysgit, open Bash (or cmd or whatever you told the installer to use) and run the following commands:

    git clone https://github.com/ForgeEssentials/ForgeEssentialsMain.git
    git submodule init
    git submodule update
    
These commands download the FE source code and configure it as the buildscripts expect the repos to be.

From here, you may refer to ForgeGradle instructions for importing a project.

*Note: FE is built on Java 7. As such, you will need either a JDK 7 or 8 installed and configured.*

### Additional steps (all very important):

#### Configuring the mixin injector (VERY IMPORTANT, OR EVENTS WILL NOT FIRE!!!)

Open your IDE run configs, and select the preconfigured "Minecraft Client" run configuration.

Under "Program Arguments", add the following:

    --tweakClass com.forgeessentials.core.preloader.FELaunchHandler

Repeat for the "Minecraft Server" run configuration.

#### Enabling annotation processing

If you intend to use the debugging features of your IDE, you must turn on annotation processing, or your IDE will complain that it cannot find classes like `Action_`.

Please refer to the respective IDE documentation on how to do this.

For Eclipse, you may need to add the referenced dependencies hibernate-jpamodelgen.jar and mixin.jar (names may differ) as annotation processors.

## Notes:
1. Please consider squashing all commits before initially submitting pull requests.

2. For a local .gitignore use .git/info/exclude

3. If you have any questions, hop on IRC.
