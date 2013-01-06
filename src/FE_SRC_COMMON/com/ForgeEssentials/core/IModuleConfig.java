package com.ForgeEssentials.core;

import java.io.File;

import net.minecraft.command.ICommandSender;

/**
 * The constructor of this should only set the file. Any creation of a Forge COnfiguration class will throw everything off. DO NOT DO IT.
 * 
 * @author AbrarSyed
 */
public interface IModuleConfig
{
	/**
	 * this is to be set from the outside after checking if the file exists.
	 * 
	 * @param generate
	 */
	public void setGenerate(boolean generate);

	/**
	 * this should check the generate boolean and do stuff accordingly. it should either load, or generate.
	 */
	public void init();

	/**
	 * this forces a save for anything that may have been set through commands.
	 */
	public void forceSave();

	/**
	 * This is called on the reload command. Ensure that it sets everything where needed. The sender is provided to spit any errors to if wanted.
	 */
	public void forceLoad(ICommandSender sender);

	/**
	 * This method should be able to be called before init()
	 * 
	 * @return the absolute path in the file system where this config is saving to or loading from.
	 */
	public File getFile();
}
