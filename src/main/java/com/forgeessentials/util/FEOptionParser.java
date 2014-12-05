package com.forgeessentials.util;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;
import java.util.StringTokenizer;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.EnumChatFormatting;
import joptsimple.HelpFormatter;
import joptsimple.OptionDescriptor;
import joptsimple.OptionParser;

public class FEOptionParser extends OptionParser {
	
	public final String name;
	
	public FEOptionParser (String commandname) {
		super();
		name = commandname;
	}
	
	public void printHelpOn(ICommandSender sender){
		String help = new String();
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
}
