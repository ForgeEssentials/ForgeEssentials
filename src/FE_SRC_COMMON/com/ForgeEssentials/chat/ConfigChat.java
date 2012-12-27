package com.ForgeEssentials.chat;

import java.io.File;

import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.util.OutputHandler;

public class ConfigChat {
	public static final File	chatConfig	= new File(ForgeEssentials.FEDIR, "chat.cfg");
	public static String chatFormat;
	public final Configuration	config;
	
	// this is designed so it will work for any class.
	public ConfigChat()
	{
		OutputHandler.debug("Loading chatconfigs");
		config = new Configuration(chatConfig, true);
		// config.load -- Configurations are loaded on Construction.
		config.addCustomCategoryComment("Chat", "Chatconfigs");
		Property prop = config.get("Chat", "chatformat", "%prefix<%username>%suffix %white%message");
		prop.comment = "This String formats the Chat.";
		prop.comment += "\nIf you want a red color and special formatcodes, the color needs to be first before the special code";
		prop.comment += "\nExamples: '%red%username' '%red%bold%username'\nNot OK:'%bold%gold%underline%username' In this example you would get the username in gold and underline but without bold";
		prop.comment += "\nList of possible variables:";
		prop.comment += "\nFor the username: %username The health of the player can be used with %health. The variable, you need for the message:%message ";
		prop.comment += "\nFor the prefix use %prefix and the suffix use %suffix";
		prop.comment += "\nColors:%black,%darkblue,%darkgreen,%darkaqua,%darkred,%purple,%gold,%grey,%darkgrey,%indigo,\n       %green,%aqua,%red,%pink,%yellow,%white";
		prop.comment += "\nSpecial formatcodes: %random,%bold,%strike,%underline,%italics";
		prop.comment += "\nTo reset all formatcodes, you can use %reset";
		prop.comment += "\nUse %rank to display a users rank, %zone to spcify there current zone";
		
		chatFormat = prop.value;
		
		
		
		// Finish init and save.
		config.save();
	}
}
