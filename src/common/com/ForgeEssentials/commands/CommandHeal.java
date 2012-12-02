package com.ForgeEssentials.commands;

import java.util.List;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICommandSender;
import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;
import cpw.mods.fml.common.FMLCommonHandler;

public class CommandHeal extends ForgeEssentialsCommandBase
{

	@Override
	public String getCommandName()
	{
		return "heal";
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		if (args.length == 0)
		{
			heal(sender);
		} 
		else if (args.length == 1)
		{
			EntityPlayer target = FMLCommonHandler.instance().getSidedDelegate().getServer().getConfigurationManager().getPlayerForUsername(args[0]);
			
			if (target != null)
			{
				heal(target);
			}
			else
			{
				sender.sendChatToPlayer(Localization.format(Localization.ERROR_NOPLAYER, args[0]));
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
		if (args.length == 1)
		{
			EntityPlayer target = FMLCommonHandler.instance().getSidedDelegate().getServer().getConfigurationManager().getPlayerForUsername(args[0]);
			
			if (target != null)
			{
				heal(target);
			}
			else
			{
				sender.sendChatToPlayer(Localization.format(Localization.ERROR_NOPLAYER, args[0]));
			}
		} 
		else
		{
			sender.sendChatToPlayer(Localization.get(Localization.ERROR_BADSYNTAX) + getSyntaxConsole());
		}
	}
	
	public void heal(EntityPlayer target)
	{
		target.heal(20);
		target.extinguish();
		target.getFoodStats().addStats(20, 1.0F);
		target.sendChatToPlayer(Localization.get(Localization.HEALED));
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
		return "ForgeEssentials.BasicCommands." + getCommandName();
	}
	
	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] args)
    {
    	if(args.length == 1)
    	{
    		return getListOfStringsMatchingLastWord(args, FMLCommonHandler.instance().getMinecraftServerInstance().getAllUsernames());
    	}
    	else
    	{
    		return null;
    	}
    }

}
