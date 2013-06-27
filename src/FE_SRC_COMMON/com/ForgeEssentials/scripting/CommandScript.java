package com.ForgeEssentials.scripting;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.Configuration;

import com.ForgeEssentials.api.APIRegistry;
import com.ForgeEssentials.api.permissions.IPermRegisterEvent;
import com.ForgeEssentials.api.permissions.RegGroup;
import com.ForgeEssentials.api.permissions.query.PermQueryPlayer;
import com.ForgeEssentials.commands.util.AFKdata;
import com.ForgeEssentials.commands.util.FEcmdModuleCommands;
import com.ForgeEssentials.commands.util.TickHandlerCommands;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;

public class CommandScript extends FEcmdModuleCommands
{

	static List<String>	scripts			= new ArrayList<String>();
	public static File	commandscripts	= new File(ModuleScripting.moduleDir, "scripts/");

	@Override
	public String getCommandName()
	{
		return "script";
	}

	@Override
	public String[] getDefaultAliases()
	{
		return new String[]
		{ "s" };
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		executeScript(sender, args[0]);
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		EntityPlayerMP player = FunctionHelper.getPlayerForName(sender, args[0]);
		if (player != null)
		{
			executeScript(player, args[1]);
		}
	}

	@Override
	public boolean canConsoleUseCommand()
	{
		return false;
	}

	@Override
	public String getCommandPerm()
	{
		return "ForgeEssentials.ModuleScripting." + getCommandName();
	}

	@Override
	public List<?> addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		File[] files = commandscripts.listFiles();
		List<String> filenames = new ArrayList<String>();
		for (File file : files)
		{
			if (file.getName() != "")
				filenames.add(file.getName().replace(".txt", ""));
		}
		return filenames;
	}

	@Override
	public RegGroup getReggroup()
	{
		return RegGroup.OWNERS;
	}

	public void executeScript(EntityPlayer player, String script)
	{
		OutputHandler.felog.info("Running command script for player " + player.username + " on login");

		// run player scripts
		try
		{
			File pscript = new File(commandscripts, script + ".txt");
			FileInputStream stream = new FileInputStream(pscript);
			InputStreamReader streamReader = new InputStreamReader(stream);
			BufferedReader reader = new BufferedReader(streamReader);
			String read = reader.readLine();
			while (read != null)
			{
				// ignore the comment things...
				if (read.startsWith("#"))
				{
					read = reader.readLine();
					continue;
				}

				// add to the rules list.
				scripts.add(read);

				// read the next string
				read = reader.readLine();

				reader.close();
				streamReader.close();
				stream.close();

			}
		}
		catch (Exception e)
		{
			OutputHandler.felog.warning("Something went wrong...");
		}
		finally
		{
			for (Object s : scripts.toArray())
			{
				String s1 = s.toString();
				MinecraftServer.getServer().getCommandManager().executeCommand(player, s1);
				OutputHandler.felog.info("Successfully run command scripts for player " + player.username);
			}
		}
	}
}
