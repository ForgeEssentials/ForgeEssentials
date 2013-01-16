package com.ForgeEssentials.core.moduleLauncher;

import java.io.File;

import net.minecraft.command.ICommandSender;

/**
 * The constructor of this should only set the file. Any creation of a Forge COnfiguration class will throw everything off. DO NOT DO IT.
 * 
 * @author AbrarSyed
 */
public abstract class ModuleConfigBase
{
	protected File file;
	protected boolean genrate;
	
	public ModuleConfigBase(File file)
	{
		this.file = file;
	}
	
	public void setGenerate(boolean generate)
	{
		this.genrate = generate;
	}

	/**
	 * this should check the generate boolean and do stuff accordingly. it should either load, or generate.
	 */
	public abstract void init();

	/**
	 * this forces a save for anything that may have been set through commands.
	 */
	public abstract void forceSave();

	/**
	 * This is called on the reload command. Ensure that it sets everything where needed. The sender is provided to spit any errors to if wanted.
	 */
	public abstract void forceLoad(ICommandSender sender);

	public File getFile()
	{
		return file;
	}
}
