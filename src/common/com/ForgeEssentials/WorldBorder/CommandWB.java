package com.ForgeEssentials.WorldBorder;

import java.util.Arrays;
import java.util.List;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICommandSender;
import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;
import cpw.mods.fml.common.FMLCommonHandler;

public class CommandWB extends ForgeEssentialsCommandBase
{

	@Override
	public String getCommandName()
	{
		return "worldborder";
	}
	
	@Override
	public List getCommandAliases()
    {
		return Arrays.asList(new String[] {"wb"});
    }

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		if (args.length == 0)
		{
			sender.sendChatToPlayer("WorldBorder status: " + ModuleWorldBorder.WBenabled);
		}
		else if (args.length == 1)
		{
			if(args[0].equalsIgnoreCase("on"))
			{
				ModuleWorldBorder.WBenabled = true;
				sender.sendChatToPlayer("WorldBorder turned on.");
			}
			else if(args[0].equalsIgnoreCase("off"))
			{
				ModuleWorldBorder.WBenabled = false;
				sender.sendChatToPlayer("WorldBorder turned off.");
			}
			else
			{
				OutputHandler.chatError(sender, (Localization.get(Localization.ERROR_BADSYNTAX) + getSyntaxPlayer(sender)));
			}
		}
		else if (args.length == 2)
		{
			if(args[0].equalsIgnoreCase("set"))
			{
				int rad = parseIntWithMin(sender, args[1], 0);
				ModuleWorldBorder.setCenter(rad, (int) sender.posX, (int) sender.posZ);
				
				sender.sendChatToPlayer("WorldBorder set with radius " + rad + " at point X:" + (int) sender.posX + " Z:" + (int) sender.posZ);
			}
			else
			{
				OutputHandler.chatError(sender, (Localization.get(Localization.ERROR_BADSYNTAX) + getSyntaxPlayer(sender)));
			}
		}
		else if (args.length == 4)
		{
			if(args[0].equalsIgnoreCase("set"))
			{
				int rad = parseIntWithMin(sender, args[1], 0);
				int X = parseInt(sender, args[2]);
				int Z = parseInt(sender, args[3]);
				
				ModuleWorldBorder.setCenter(rad, X, Z);
				
				sender.sendChatToPlayer("WorldBorder set with radius " + rad + " at point X:" + X + " Z:" + Z);
			}
			else
			{
				OutputHandler.chatError(sender, (Localization.get(Localization.ERROR_BADSYNTAX) + getSyntaxPlayer(sender)));
			}
		}
		else
		{
			OutputHandler.chatError(sender, (Localization.get(Localization.ERROR_BADSYNTAX) + getSyntaxPlayer(sender)));
		}
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		if (args.length == 0)
		{
			sender.sendChatToPlayer("WorldBorder status: " + ModuleWorldBorder.WBenabled);
		}
		else if (args.length == 1)
		{
			if(args[0].equalsIgnoreCase("on"))
			{
				ModuleWorldBorder.WBenabled = true;
				sender.sendChatToPlayer("WorldBorder turned on.");
			}
			else if(args[0].equalsIgnoreCase("off"))
			{
				ModuleWorldBorder.WBenabled = false;
				sender.sendChatToPlayer("WorldBorder turned off.");
			}
			else
			{
				sender.sendChatToPlayer(Localization.get(Localization.ERROR_BADSYNTAX) + getSyntaxConsole());
			}
		}
		else if (args.length == 4)
		{
			if(args[0].equalsIgnoreCase("set"))
			{
				int rad = parseIntWithMin(sender, args[1], 0);
				int X = parseInt(sender, args[2]);
				int Z = parseInt(sender, args[3]);
				
				ModuleWorldBorder.setCenter(rad, X, Z);
				
				sender.sendChatToPlayer("WorldBorder set with radius " + rad + " at point X:" + X + " Z:" + Z);
			}
			else
			{
				sender.sendChatToPlayer(Localization.get(Localization.ERROR_BADSYNTAX) + getSyntaxConsole());
			}
		}
		else
		{
			sender.sendChatToPlayer(Localization.get(Localization.ERROR_BADSYNTAX) + getSyntaxConsole());
		}
	}
	
	@Override
	public boolean canConsoleUseCommand()
	{
		return true;
	}

	@Override
	public boolean canPlayerUseCommand(EntityPlayer player)
	{
		return true;
	}

	@Override
	public String getCommandPerm()
	{
		return "ForgeEssentials.worldborder.admin";
	}
	
	public List addTabCompletionOptions(ICommandSender sender, String[] args)
    {
    	if(args.length==1)
    	{
    		return getListOfStringsMatchingLastWord(args, "on", "off", "set");
    	}
    	else
    	{
    		return null;
    	}
    }

}
