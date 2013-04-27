package com.ForgeEssentials.questioner;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.api.questioner.AnswerEnum;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;

public class CommandAffirmative extends QuestionerCommandBase
{
	
	@Override
	public String getCommandName()
	{
		return "yes";
	}
	
	@Override
	public List getCommandAliases()
	{
		ArrayList list = new ArrayList();
		list.add("accept");
		list.add("allow");
		list.add("give");
		return list;
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		QuestionCenter.processAnswer(sender, true);
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
	public boolean canPlayerUseCommand(EntityPlayer player)
	{
		return true;
	}

	@Override
	public String getCommandPerm()
	{
		return null;
	}

}
