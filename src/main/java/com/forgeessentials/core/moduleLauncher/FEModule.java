package com.forgeessentials.core.moduleLauncher;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
// If you need more than one thing in the FE API, make a module class using this
// annotation.
public @interface FEModule {

    /**
     * "Module" is not automatically ie: "WorldControlModule" "SnooperModule"
     * etc.. this is what will show up in logs, especially about errors. This is
     * similar to the ModuleID, it is the identifying mark.. and shouldn't have
     * spaces. If an outside module overrides another module, they should have
     * the same name.
     */
    String name();

    String version() default "";

    /**
     * Marks it as core. Core modules are loaded first.
     *
     * @return
     */
    boolean isCore() default false;

    /**
     * Allows a module to override another one. You can only override core
     * modules.
     *
     * @return
     */
    boolean doesOverride() default false;

    /**
     * For all built in modules, this had better be the ForgeEssentials class.
     * For everyone else, this should be your @mod file.
     *
     * @return
     */
    Class<?> parentMod();

    /**
     * this should be obvious, This is the method that will be called when the
     * /reload command is called. Configs are relaoded just before this method
     * is called.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(
            { ElementType.METHOD })
    public @interface Reload {
    }

    /**
     * This field will be populated with an instance of this Module.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(
            { ElementType.FIELD })
    public @interface Instance {
    }

    /**
     * This field will be populated with an instance of this Module's parent
     * mod.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(
            { ElementType.FIELD })
    public @interface ParentMod {
    }

    /**
     * This field will be populated with an instance of this Module's
     * ModuleContainer object.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(
            { ElementType.FIELD })
    public @interface Container {
    }

    /**
     * This field will be populated with a File instance of this Modules
     * directory.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(
            { ElementType.FIELD })
    public @interface ModuleDir {
    }

}
