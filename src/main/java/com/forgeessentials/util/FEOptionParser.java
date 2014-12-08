package com.forgeessentials.util;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;
import java.util.StringTokenizer;

import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandUsageException;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.EnumChatFormatting;
import joptsimple.HelpFormatter;
import joptsimple.OptionDescriptor;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

public class FEOptionParser extends OptionParser {
	
	public final String name;
	
	public FEOptionParser (String commandname) {
		super();
		name = commandname;
		accepts("?").forHelp();
	}
	
	public void printHelpOn(ICommandSender sender){
		OutputHandler.chatColored(sender, "========================================", EnumChatFormatting.GREEN);
		OutputHandler.chatColored(sender, "Help for command: /" + name, EnumChatFormatting.GREEN);
		OutputHandler.chatColored(sender, "========================================", EnumChatFormatting.GREEN);
		try (StringWriter sw = new StringWriter()) {
			printHelpOn(sw);
			StringTokenizer st = new StringTokenizer(sw.toString(), "\n");
			while (st.hasMoreTokens())
				OutputHandler.chatColored(sender, st.nextToken().replace("\r", ""), EnumChatFormatting.GREEN);
		} catch (IOException e) {
			// Give up, fail silently... this ain't my problem anymore.
			OutputHandler.chatColored(sender, "No help available.", EnumChatFormatting.GREEN);
		}
		OutputHandler.chatColored(sender, "========================================", EnumChatFormatting.GREEN);
	}

	public OptionSet parse(ICommandSender sender, String... arguments) {
		try {
			OptionSet options = super.parse(arguments);
			
			if (options.has("?")) {
				printHelpOn(sender);
				return null;
			}
			else {
				return options;
			}
		}
		catch (joptsimple.OptionException e){
			throw new WrongUsageException(e.getLocalizedMessage());
		}
	}
}
