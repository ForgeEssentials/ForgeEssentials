package com.ForgeEssentials.chat;

import com.ForgeEssentials.core.moduleLauncher.ModuleConfigBase;
import com.ForgeEssentials.permission.RegGroup;
import com.ForgeEssentials.permission.ZoneManager;
import com.ForgeEssentials.util.OutputHandler;

import net.minecraft.command.ICommandSender;

import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

import java.io.File;
import java.util.Arrays;
import java.util.regex.Pattern;

public class ConfigChat extends ModuleConfigBase
{
	public static String	chatFormat, groupPrefixFormat, groupSuffixFormat, groupRankFormat;
	public static Pattern	groupRegex	= Pattern.compile("\\{[a-zA-Z0-9._]*\\<\\:\\>[a-zA-Z0-9._]*\\}");
	public Configuration	config;

	// this is designed so it will work for any class.
	public ConfigChat(File file)
	{
		super(file);
	}

	@Override
	public void init()
	{
		OutputHandler.debug("Loading chatconfigs");
		config = new Configuration(file, true);

		// config.load -- Configurations are loaded on Construction.
		config.addCustomCategoryComment("Chat", "Chat Configs");

		Property prop = config.get("Chat", "chatformat", "%groupPrefix%playerPrefix<%username>%groupSuffix%playerSuffix %reset%message");
		prop.comment = "This String formats the Chat.";
		prop.comment += "\nIf you want both a color and special formatcodes, the color needs to be first before the special code";
		prop.comment += "\nExamples: '%red%username' '%red%bold%username'\nNot OK:'%bold%gold%underline%username' In this example you would get the username in gold and underline but without bold";
		prop.comment += "\nList of possible variables:";
		prop.comment += "\nFor the username: %username The health of the player can be used with %health. The variable, you need for the message:%message ";
		prop.comment += "\nFor the player prefix and sufix use %playerPrefix and %playerSuffix";
		prop.comment += "\nColors:%black,%darkblue,%darkgreen,%darkaqua,%darkred,%purple,%gold,%grey,%darkgrey,%indigo,\n       %green,%aqua,%red,%pink,%yellow,%white";
		prop.comment += "\nSpecial formatcodes: %random,%bold,%strike,%underline,%italics";
		prop.comment += "\nTo reset all formatcodes, you can use %reset";
		prop.comment += "\nUse %rank to display a users rank as specified, %zone to specify there current zone";
		prop.comment += "\nUse %groupPrefix and %groupSuffix to display the group prefixes and suffixes as specified";
		chatFormat = prop.value;

		Chat.censor = config.get("BannedWords", "censor", true, "censor the words in the censorList").getBoolean(true);
		Chat.bannedWords = Arrays.asList(config.get("BannedWords", "censorList", new String[] { "fuck", "ass", "bitch", "shit" }, "List of words to be censored").valueList);

		config.addCustomCategoryComment("Chat.groups",
				"You may put enything here that you want displaed as part of the group prefixes, suffixes, or ranks." +
						"\n {ladderName<:>Zone} will display the data for the highest priority group that the player is in that is part of the specified ladder and specified zone." +
						"\n {...<:>...} will display the data of each group the player is in in order of priority" +
						"\n you may put contsraints with ladders or zones with {...<:>zoneName} or {ladderName<:>...}" +
						"\n you may also use the color and MCFormat codes above.");
		groupPrefixFormat = config.get("Chat.groups", "groupPrefix", "{" + RegGroup.LADDER + "<:>" + ZoneManager.GLOBAL.getZoneName() + "}").value;
		groupSuffixFormat = config.get("Chat.groups", "groupSuffix", "{" + RegGroup.LADDER + "<:>" + ZoneManager.GLOBAL.getZoneName() + "}").value;
		groupRankFormat = config.get("Chat.groups", "rank", "[{" + RegGroup.LADDER + "<:>" + ZoneManager.GLOBAL.getZoneName() + "}]").value;

		config.save();
	}

	@Override
	public void forceSave()
	{
		// config.load -- Configurations are loaded on Construction.
		config.addCustomCategoryComment("Chat", "Chatconfigs");

		Property prop = config.get("Chat", "chatformat", "%groupPrefix%playerPrefix<%username>%playerSuffix%groupSuffix %reset%message");
		prop.comment = "This String formats the Chat.";
		prop.comment += "\nIf you want both a color and special formatcodes, the color needs to be first before the special code";
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

		config.get("BannedWords", "censor", true, "censor the words in the censorList").value = "" + Chat.censor;
		config.get("BannedWords", "censorList", new String[] {}, "List of words to be censored").valueList = Chat.bannedWords.toArray(new String[Chat.bannedWords.size()]);

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

		Chat.censor = config.get("BannedWords", "censor", true).getBoolean(true);
		Chat.bannedWords = Arrays.asList(config.get("BannedWords", "censorList", new String[] { "fuck", "ass", "bitch", "shit" }).valueList);

		groupPrefixFormat = config.get("Chat.groups", "groupPrefix", "{" + RegGroup.LADDER + "<:>" + ZoneManager.GLOBAL.getZoneName() + "}").value;
		groupSuffixFormat = config.get("Chat.groups", "groupSuffix", "{" + RegGroup.LADDER + "<:>" + ZoneManager.GLOBAL.getZoneName() + "}").value;
		groupRankFormat = config.get("Chat.groups", "rank", "[{" + RegGroup.LADDER + "<:>" + ZoneManager.GLOBAL.getZoneName() + "}]").value;
	}
}
