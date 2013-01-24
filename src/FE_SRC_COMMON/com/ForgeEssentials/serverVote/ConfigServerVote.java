package com.ForgeEssentials.serverVote;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;

import com.ForgeEssentials.core.moduleLauncher.ModuleConfigBase;
import com.ForgeEssentials.util.OutputHandler;

public class ConfigServerVote extends ModuleConfigBase
{
	private static final String category = "ServerVote";
	
	private Configuration config;
	
	public boolean allowOfflineVotes;
	public String msgAll = "";
	public String msgVoter = "";
	public List<ItemStack> freeStuff = new ArrayList();
	
	public ConfigServerVote(File file)
	{
		super(file);
	}

	@Override
	public void init()
	{
		config = new Configuration(file, true);
		
		allowOfflineVotes = config.get(category, "allowOfflineVotes", true, "If false, votes of offline players will be canceled.").getBoolean(true);
		msgAll = config.get(category, "msgAll", "%player has voted for this server on %service.", "You can use color codes (&), %player and %service").value;
		msgVoter = config.get(category, "msgVoter", "Thanks for voting for our server!", "You can use color codes (&), %player and %service").value;
		
		String[] tempArray = config.get(category, "rewards", new String[] {}, "Format is like this: [amount]x<id>[:meta]").valueList;
		
		for(String temp : tempArray)
		{
			int amount = 1;
			int meta = 0;
			
			if(temp.contains("x"))
			{
				String[] temp2 = temp.split("x");
				amount = Integer.parseInt(temp2[0]);
				temp = temp2[1];
			}
			
			if(temp.contains(":"))
			{
				String[] temp2 = temp.split(":");
				meta = Integer.parseInt(temp2[1]);
				temp = temp2[0];
			}
			
			int id = Integer.parseInt(temp);
			ItemStack stack = new ItemStack(id, amount, meta);
			
			OutputHandler.debug(stack);
			
			freeStuff.add(stack);
		}
		
		config.save();
	}

	@Override
	public void forceSave()
	{
		
	}

	@Override
	public void forceLoad(ICommandSender sender)
	{
		config.load();
		allowOfflineVotes = config.get(category, "allowOfflineVotes", true, "If false, votes of offline players will be canceled.").getBoolean(true);
		msgAll = config.get(category, "msgAll", "%player has voted for this server on %service.", "You can use color codes (&), %player and %service").value;
		msgVoter = config.get(category, "msgVoter", "Thanks for voting for our server!", "You can use color codes (&), %player and %service").value;
		
	}

}
