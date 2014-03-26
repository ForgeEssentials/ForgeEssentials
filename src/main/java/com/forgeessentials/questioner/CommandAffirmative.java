package com.forgeessentials.questioner;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;

public class CommandAffirmative extends ForgeEssentialsCommandBase
{
	
	@Override
	public String getCommandName()
	{
		return "yes";
	}
	
	@Override
	public List<String> getCommandAliases()
	{
		ArrayList<String> list = new ArrayList<String>();
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

	@Override
	public int compareTo(Object o) {
		return 0;
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/yes Answer yes to a question";
	}

}
