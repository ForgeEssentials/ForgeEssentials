package com.ForgeEssentials.scripting;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.packet.Packet100OpenWindow;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.Configuration;

import com.ForgeEssentials.api.APIRegistry;
import com.ForgeEssentials.api.permissions.IPermRegisterEvent;
import com.ForgeEssentials.api.permissions.RegGroup;
import com.ForgeEssentials.api.permissions.query.PermQueryPlayer;
import com.ForgeEssentials.commands.util.AFKdata;
import com.ForgeEssentials.commands.util.FEcmdModuleCommands;
import com.ForgeEssentials.commands.util.TickHandlerCommands;
import com.ForgeEssentials.util.FEChatFormatCodes;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;

public class CommandScriptBook extends FEcmdModuleCommands
{

  public static File	commandscripts	= new File(ModuleScripting.moduleDir, "scripts/");

	@Override
	public String getCommandName()
	{
		return "scriptbook";
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		ItemStack i = sender.inventory.getCurrentItem();
		NBTTagList pages;
		String filename = "";
		if (i != null)
		{
			if (i.hasTagCompound())
			{
				if (i.getTagCompound().hasKey("title") && i.getTagCompound().hasKey("pages"))
				{
					filename = i.getTagCompound().getString("title") + ".txt";
					pages = (NBTTagList) i.getTagCompound().getTag("pages");
					File savefile = new File(commandscripts, filename);
					if (savefile.exists())
					{
						savefile.delete();
					}
					try
					{
						savefile.createNewFile();
						FileWriter fstream = new FileWriter(savefile);
						BufferedWriter out = new BufferedWriter(fstream);
						for (int c = 0; c < pages.tagCount(); c++)
						{
							String line = pages.tagAt(c).toString();
							while(line.contains("\n")){
								out.write(line.substring(0, line.indexOf("\n")));
								out.newLine();
								line = line.substring(line.indexOf("\n") + 1);
							}
							if(line.length() > 0){
								out.write(line);
							}
						}
						out.close();
						fstream.close();
					}
					catch (Exception e)
					{
						OutputHandler.felog.info("Something went wrong...");
					}
				}

			}
		}
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
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
		return null;
	}

	@Override
	public RegGroup getReggroup()
	{
		return RegGroup.OWNERS;
	}

}
