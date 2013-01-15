package com.ForgeEssentials.chat;

import java.io.File;
import java.util.regex.Pattern;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.core.moduleLauncher.IModuleConfig;
import com.ForgeEssentials.permission.RegGroup;
import com.ForgeEssentials.permission.ZoneManager;
import com.ForgeEssentials.util.OutputHandler;

public class ConfigChat implements IModuleConfig
{
	public static final File chatConfig = new File(ForgeEssentials.FEDIR, "chat.cfg");
	public static String chatFormat, groupPrefixFormat, groupSuffixFormat, groupRankFormat;
	public static Pattern groupRegex = Pattern.compile("\\{[a...zA...Z\\.]+?\\<\\:\\>[a...zA...Z\\.]+?\\}");
	public Configuration config;

	// this is designed so it will work for any class.
	public ConfigChat()
	{
	}

	@Override
	public void setGenerate(boolean generate)
	{
	}

	@Override
	public void init()
	{
		OutputHandler.debug("Loading chatconfigs");
		config = new Configuration(chatConfig, true);

		// config.load -- Configurations are loaded on Construction.
		config.addCustomCategoryComment("Chat", "Chat Configs");

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

		Chat.censor = config.get("Chat", "censor", true, "Censor words in the 'bannedwords.txt' file").getBoolean(true);
		
		config.addCustomCategoryComment("Chat.groups",
				"You may put enything here that you want displaed as part of the group prefixes, suffixes, or ranks." +
				"\n {ladderName<:>Zone} will display the data for the highest priority group that the player is in that is part of the specified ladder and specified zone." +
				"\n {...} will display the data of each group the player is in in order of priority" +
				"\n you may put contsraints with ladders or zones with {...<:>zoneName} or {ladderName<:>...}" +
				"\n you may also use the color and MCFormat codes above.");
		groupPrefixFormat = config.get("Chat.groups", "groupPrefix", "{"+RegGroup.LADDER+"<:>"+ZoneManager.GLOBAL.getZoneName()+"}").value;
		groupSuffixFormat = config.get("Chat.groups", "groupSuffix", "{"+RegGroup.LADDER+"<:>"+ZoneManager.GLOBAL.getZoneName()+"}").value;
		groupRankFormat = config.get("Chat.groups", "rank", "[{"+RegGroup.LADDER+"<:>"+ZoneManager.GLOBAL.getZoneName()+"}]").value;
		

		config.save();
	}

	@Override
	public void forceSave()
	{
		// config.load -- Configurations are loaded on Construction.
		config.addCustomCategoryComment("Chat", "Chatconfigs");

		Property prop = config.get("Chat", "chatformat", "%groupPrefix%playerPrefix<%username>%playerSuffix%groupSuffix %reset%message");
		prop.comment = "This String formats the Chat.";
		prop.comment += "\nIf you want a red color and special formatcodes, the color needs to be first before the special code";
		prop.comment += "\nExamples: '%red%username' '%red%bold%username'\nNot OK:'%bold%gold%underline%username' In this example you would get the username in gold and underline but without bold";
		prop.comment += "\nList of possible variables:";
		prop.comment += "\nFor the username: %username The health of the player can be used with %health. The variable, you need for the message:%message ";
		prop.comment += "\nFor the players prefix and suffix use %playerPrefix and %playerSuffix";
		prop.comment += "\nColors:%black,%darkblue,%darkgreen,%darkaqua,%darkred,%purple,%gold,%grey,%darkgrey,%indigo,\n       %green,%aqua,%red,%pink,%yellow,%white";
		prop.comment += "\nSpecial formatcodes: %random,%bold,%strike,%underline,%italics";
		prop.comment += "\nTo reset all formatcodes, you can use %reset";
		prop.comment += "\nUse %rank to display a users rank as defined in the config below., %zone to specify there current zone";
		prop.comment += "\nUse %groupPrefix and groupSuffix to display the prefix and suffix of groups as defined in the config";
		prop.value = chatFormat;

		config.get("Chat", "censor", true, "Censor words in the 'bannedwords.txt' file").value = "" + Chat.censor;

		config.addCustomCategoryComment("Chat.groups",
				"You may put enything here that you want displaed as part of the group prefixes, suffixes, or ranks." +
				"\n {ladderName<:>Zone} will display the data for the highest priority group that the player is in that is part of the specified ladder and specified zone." +
				"\n {...} will display the data of each group the player is in in order of priority" +
				"\n you may put contsraints with ladders or zones with {...<:>zoneName} or {ladderName<:>...}" +
				"\n you may also use the color and MCFormat codes above.");
		
		config.get("Chat.groups", "groupPrefix", "").value = groupPrefixFormat;
		config.get("Chat.groups", "groupSuffix", "").value = groupSuffixFormat;
		config.get("Chat.groups", "rank", "").value = groupRankFormat;
		
		config.save();
	}

	@Override
	public void forceLoad(ICommandSender sender)
	{
		OutputHandler.debug("Loading chatconfigs");
		config.load();

		chatFormat = config.get("Chat", "chatformat", "%prefix<%username>%suffix %white%message").value;

		Chat.censor = config.get("Chat", "censor", true).getBoolean(true);
		
		groupPrefixFormat = config.get("Chat.groups", "groupPrefix", "{"+RegGroup.LADDER+"<:>"+ZoneManager.GLOBAL.getZoneName()+"}").value;
		groupSuffixFormat = config.get("Chat.groups", "groupSuffix", "{"+RegGroup.LADDER+"<:>"+ZoneManager.GLOBAL.getZoneName()+"}").value;
		groupRankFormat = config.get("Chat.groups", "rank", "[{"+RegGroup.LADDER+"<:>"+ZoneManager.GLOBAL.getZoneName()+"}]").value;
	}

	@Override
	public File getFile()
	{
		return chatConfig;
	}
}
