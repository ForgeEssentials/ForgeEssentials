package com.ForgeEssentials.chat.ircCommands;

import java.util.ArrayList;

import org.pircbotx.User;

public class ircCommands
{

	// Register commands here.
	public static ircCommand[]	ircCommands = {
		new ircCommandReply(),
		new ircCommandMessage(),
		new ircCommandHelp(),
		new ircCommandList()
	};
	
	
	public static void executeCommand(String raw, User user) {
		
		// Prepare arguments
		raw = raw.replace("%", "");
		ArrayList<String> args = new ArrayList<String>();
		while (raw.length() > 0)
		{
			int index = raw.indexOf(' ');
			if (index == -1) {
				args.add(raw.trim());
				raw = "";
				continue;
			}
			args.add(raw.substring(0, index));
			raw = raw.substring(index + 1);
		}
		String commandName = args.remove(0);
		
		// Execute command
		for (ircCommand command : ircCommands)
		{
			if (command.isCommand(commandName))
			{
				command.execute(args, user);
				return;
			}
		}
		
		// Uh-Oh
		user.sendMessage("Unable to find command: %" + commandName);
	}
}
