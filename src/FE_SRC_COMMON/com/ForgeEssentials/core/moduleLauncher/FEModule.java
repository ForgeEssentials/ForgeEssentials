package com.ForgeEssentials.core.moduleLauncher;

import java.io.File;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.minecraft.command.ICommandSender;

@Retention(RetentionPolicy.RUNTIME)
// If you need more than one thing in the FE API, make a module class using this
// annotation.
public @interface FEModule
{
	/**
	 * this may be null
	 */
	Class<? extends ModuleConfigBase> configClass() default DummyConfig.class;

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
	 * @return
	 */
	boolean isCore() default false;

	/**
	 * Allows a module to override another one. You can only override core
	 * modules.
	 * @return
	 */
	boolean doesOverride() default false;

	/**
	 * For all built in modules, this had better be the ForgeEssentials class.
	 * For everyone else, this should be your @mod file.
	 * @return
	 */
	Class<?> parentMod();

	/**
	 * Configs are instantiated before this.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(
	{ ElementType.METHOD })
	public @interface PreInit
	{
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(
	{ ElementType.METHOD })
	public @interface Init
	{
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(
	{ ElementType.METHOD })
	public @interface PostInit
	{
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(
	{ ElementType.METHOD })
	public @interface ServerInit
	{
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(
	{ ElementType.METHOD })
	public @interface ServerPostInit
	{
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(
	{ ElementType.METHOD })
	public @interface ServerStop
	{
	}

	/**
	 * this should be obvious, This is the method that will be called when the
	 * /reload command is called. Configs are relaoded just before this method
	 * is called.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(
	{ ElementType.METHOD })
	public @interface Reload
	{
	}

	/**
	 * This field will be populated with an instance of this Module.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(
	{ ElementType.FIELD })
	public @interface Instance
	{
	}

	/**
	 * This field will be populated with an instance of this Module's parent
	 * mod.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(
	{ ElementType.FIELD })
	public @interface ParentMod
	{
	}

	/**
	 * This field will be populated with an instance of this Module's
	 * ModuleContainer object.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(
	{ ElementType.FIELD })
	public @interface Container
	{
	}

	/**
	 * This field will be populated with an instance of this Module's Config
	 * object.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(
	{ ElementType.FIELD })
	public @interface Config
	{
	}

	/**
	 * This field will be populated with a File instance of this Modules
	 * directory.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(
	{ ElementType.FIELD })
	public @interface ModuleDir
	{
	}

	// dummy for the default config.
	class DummyConfig extends ModuleConfigBase
	{

		public DummyConfig(File file)
		{
			super(file);
		}

		@Override
		public void init()
		{
		}

		@Override
		public void forceSave()
		{
		}

		@Override
		public void forceLoad(ICommandSender sender)
		{
		}

		@Override
		public File getFile()
		{
			return null;
		}

	}
}
