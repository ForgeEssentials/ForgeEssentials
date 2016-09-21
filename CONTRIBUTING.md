# Integrating ForgeEssentials with another mod
To use ForgeEssentials and its powerful permission framework with from another mod, please take a look at the [developer documentation in the wiki](https://github.com/ForgeEssentials/ForgeEssentials/wiki/Developer-documentation).

# Developing ForgeEssentials
1. Download ForgeEssentials with git
2. Run ForgeGradle setup
  (E.g., `./gradlew setupDecompWorkspace eclipse` when using eclipse)
3. Configure the annotation processor as outlined [below](#Configuring-Annotation-Processing)
4. Add ``-Dmixin.env.compatLevel=JAVA_7` to VM Options for both server and client
5. Add `--tweakClass com.forgeessentials.core.preloader.FELaunchHandler --mixin mixins.forgeessentials.json` to the launch arguments of the server
6. Add `--tweakClass com.forgeessentials.core.preloader.FELaunchHandler --mixin mixins.forgeessentials.json --tweakClass org.spongepowered.asm.launch.MixinTweaker --mixin mixins.forgeessentials.client.json` to the launch arguments of the client

# Configuring Annotation Processing
If you open ForgeEssentials in your IDE, you must turn on annotation processing, or your IDE will complain that it cannot find classes like `Action_`. You also might need to manually add `hibernate-jpamodelgen-4.3.7.jar` as annotation processor. Please refer to the respective IDE documentation on how to do this.

### Eclipse
- Go to `Project Properties` > `Java Compiler` > `Annotation processing`
  - Check `Enable annotation processing`
- Go to `Project Properties` > `Java Compiler` > `Annotation processing` > `Factory Path`
  - Add the `hibernate-jpamodelgen-4.3.7.jar`.  
    To find its location, check the referenced libraries in the project settings. It should be located somewhere in  
    `$HOME/.gradle/caches/modules-2/files-2.1/org.hibernate/hibernate-jpamodelgen/4.3.7.Final`

### IntelliJ IDEA
![](http://files.forgeessentials.com/Idea_apt_settings.jpg)
- Go to `Settings > Build, Execution and Deployment > Compiler > Annotation Processors`
 - Check `Enable annotation processing`
 - Check `Obtain processors from project classpath`
 - Enter `.apt_generated` as "Production sources directory"
 - Under `Annotation Processors`, add this fully qualified name: `org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor`
 - Run `Build > Rebuild Project`
 - Right click `.apt_generated` directory and select `Mark as > Source directory`

# Notes
1. Please consider squashing all commits before initially submitting pull requests
2. For a local `.gitignore` use `.git/info/exclude`
3. If you have any questions, join us on IRC
