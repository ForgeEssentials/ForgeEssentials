package com.ForgeEssentials.playerLogger;

import java.util.Arrays;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;

public class CommandLb extends ForgeEssentialsCommandBase 
{

	@Override
	public String getCommandName() 
	{
		return "logblock";
	}
	
	@Override
	public List getCommandAliases()
    {
        return Arrays.asList(new String[]{"lb"});
    }

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args) 
	{
		sender.getEntityData().setBoolean("lb", true);
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args) 
	{
		
	}

	@Override
	public boolean canConsoleUseCommand() 
	{
		return true;
	}

	@Override
	public String getCommandPerm() 
	{
		return null;
	}

}
