package com.ForgeEssentials.chat;

import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.permission.Group;
import com.ForgeEssentials.permission.PermissionsAPI;
import com.ForgeEssentials.permission.SqlHelper;
import com.ForgeEssentials.permission.Zone;
import com.ForgeEssentials.permission.ZoneManager;
import com.ForgeEssentials.permission.query.PermQueryPlayer;
import com.ForgeEssentials.util.FEChatFormatCodes;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.AreaSelector.Point;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet3Chat;

import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.ServerChatEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;
import java.util.regex.Matcher;

import cpw.mods.fml.common.network.IChatListener;

public class Chat implements IChatListener
{
	public static List<String>	bannedWords	= new ArrayList<String>();
	public static boolean		censor;

	@ForgeSubscribe
	public void chatEvent(ServerChatEvent event)
	{
		/*
		 * Mute?
		 */

		if (event.player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG).getBoolean("mute"))
		{
			event.setCanceled(true);
			event.player.sendChatToPlayer("You are muted.");
			return;
		}

		String message = event.message;
		String nickname = event.username;
		// Perhaps add option to completely remove message?
		/*
		 * if (censor && remove)
		 * {
		 * event.setCanceled(true);
		 * event.player.sendChatToPlayer("Such language is not tolerated.");
		 * return;
		 * }
		 */
		if (censor)
		{
			for (String word : bannedWords)
			{
				message = replaceAllIgnoreCase(message, word, "###");
			}
		}

		/*
		 * Nickname
		 */

		if (event.player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG).hasKey("nickname"))
		{
			nickname = event.player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG).getString("nickname");
		}

		/*
		 * Colorize!
		 */

		if (event.message.contains("&"))
		{
			if (PermissionsAPI.checkPermAllowed(new PermQueryPlayer(event.player, "ForgeEssentials.chat.usecolor")))
			{
				message = FunctionHelper.formatColors(event.message);
			}
		}

		String rank = "";
		String zoneID = "";
		String gPrefix = "";
		String gSuffix = "";

		PlayerInfo info = PlayerInfo.getPlayerInfo(event.player.username);
		String playerPrefix = info.prefix == null ? "" : FunctionHelper.formatColors(info.prefix).trim();
		String playerSuffix = info.suffix == null ? "" : FunctionHelper.formatColors(info.suffix).trim();

		Zone zone = ZoneManager.getWhichZoneIn(new Point(event.player), event.player.worldObj);
		zoneID = zone.getZoneName();

		// Group stuff!!! DO NOT TOUCH!!!
		{
			rank = getGroupRankString(event.username);

			gPrefix = getGroupPrefixString(event.username);
			gPrefix = FunctionHelper.formatColors(gPrefix).trim();

			gSuffix = getGroupSuffixString(event.username);
			gSuffix = FunctionHelper.formatColors(gSuffix).trim();
		}

		// It may be beneficial to make this a public function. -RlonRyan
		String format = ConfigChat.chatFormat;
		format = ConfigChat.chatFormat == null || ConfigChat.chatFormat.trim().isEmpty() ? "<%username>%message" : ConfigChat.chatFormat;

		/*
		 * if(enable_chat%){
		 * format = replaceAllIngnoreCase(format, "%message", message);
		 * }
		 */
		// replace group, zone, prefix, suffix, and rank
		format = replaceAllIgnoreCase(format, "%playerPrefix", playerPrefix); //I say this goes here so it gets to join the parsing party.
		format = replaceAllIgnoreCase(format, "%playerSuffix", playerSuffix);
		format = replaceAllIgnoreCase(format, "%rank", rank);
		format = replaceAllIgnoreCase(format, "%zone", zoneID);
		format = replaceAllIgnoreCase(format, "%groupPrefix", gPrefix);
		format = replaceAllIgnoreCase(format, "%groupSuffix", gSuffix);

		// replace colors
		format = replaceAllIgnoreCase(format, "%red", FEChatFormatCodes.RED.toString());
		format = replaceAllIgnoreCase(format, "%yellow", FEChatFormatCodes.YELLOW.toString());
		format = replaceAllIgnoreCase(format, "%black", FEChatFormatCodes.BLACK.toString());
		format = replaceAllIgnoreCase(format, "%darkblue", FEChatFormatCodes.DARKBLUE.toString());
		format = replaceAllIgnoreCase(format, "%darkgreen", FEChatFormatCodes.DARKGREEN.toString());
		format = replaceAllIgnoreCase(format, "%darkaqua", FEChatFormatCodes.DARKAQUA.toString());
		format = replaceAllIgnoreCase(format, "%darkred", FEChatFormatCodes.DARKRED.toString());
		format = replaceAllIgnoreCase(format, "%purple", FEChatFormatCodes.PURPLE.toString());
		format = replaceAllIgnoreCase(format, "%gold", FEChatFormatCodes.GOLD.toString());
		format = replaceAllIgnoreCase(format, "%grey", FEChatFormatCodes.GREY.toString());
		format = replaceAllIgnoreCase(format, "%darkgrey", FEChatFormatCodes.DARKGREY.toString());
		format = replaceAllIgnoreCase(format, "%indigo", FEChatFormatCodes.INDIGO.toString());
		format = replaceAllIgnoreCase(format, "%green", FEChatFormatCodes.GREEN.toString());
		format = replaceAllIgnoreCase(format, "%aqua", FEChatFormatCodes.AQUA.toString());
		format = replaceAllIgnoreCase(format, "%pink", FEChatFormatCodes.PINK.toString());
		format = replaceAllIgnoreCase(format, "%white", FEChatFormatCodes.WHITE.toString());

		// replace MC formating
		format = replaceAllIgnoreCase(format, "%random", FEChatFormatCodes.RANDOM.toString());
		format = replaceAllIgnoreCase(format, "%bold", FEChatFormatCodes.BOLD.toString());
		format = replaceAllIgnoreCase(format, "%strike", FEChatFormatCodes.STRIKE.toString());
		format = replaceAllIgnoreCase(format, "%underline", FEChatFormatCodes.UNDERLINE.toString());
		format = replaceAllIgnoreCase(format, "%italics", FEChatFormatCodes.ITALICS.toString());
		format = replaceAllIgnoreCase(format, "%reset", FEChatFormatCodes.RESET.toString());

		// random nice things...
		format = replaceAllIgnoreCase(format, "%health", "" + event.player.getHealth());
		format = replaceAllIgnoreCase(format, "%smile", "\u263A");
		format = replaceAllIgnoreCase(format, "%copyrighted", "\u00A9");
		format = replaceAllIgnoreCase(format, "%registered", "\u00AE");
		format = replaceAllIgnoreCase(format, "%diamond", "\u2662");
		format = replaceAllIgnoreCase(format, "%spade", "\u2664");
		format = replaceAllIgnoreCase(format, "%club", "\u2667");
		format = replaceAllIgnoreCase(format, "%heart", "\u2661");
		format = replaceAllIgnoreCase(format, "%female", "\u2640");
		format = replaceAllIgnoreCase(format, "%male", "\u2642");

		// essentials
		format = replaceAllIgnoreCase(format, "%username", nickname);
		// if(!enable_chat%){ //whereas enable chat is a boolean that can be set in the config or whatever
		// //allowing the use of %codes in chat
		format = replaceAllIgnoreCase(format, "%message", message);
		// }

		// finally make it the chat line.
		event.line = format;
	}

	@Override
	public Packet3Chat serverChat(NetHandler handler, Packet3Chat message)
	{
		return message;
	}

	@Override
	public Packet3Chat clientChat(NetHandler handler, Packet3Chat message)
	{
		return message;
	}

	private String replaceAllIgnoreCase(String text, String search, String replacement)
	{
		if (search.equals(replacement))
			return text;
		StringBuilder buffer = new StringBuilder(text);
		String lowerSearch = search.toLowerCase();
		int i = 0;
		int prev = 0;
		while ((i = buffer.toString().toLowerCase().indexOf(lowerSearch, prev)) > -1)
		{
			buffer.replace(i, i + search.length(), replacement);
			prev = i + replacement.length();
		}
		return buffer.toString();
	}

	private String getGroupRankString(String username)
	{
		Matcher match = ConfigChat.groupRegex.matcher(ConfigChat.groupRankFormat);
		ArrayList<TreeSet<Group>> list = getGroupsList(match, username);

		String end = "";

		StringBuilder temp = new StringBuilder();
		for (TreeSet<Group> set : list)
		{
			for (Group g : set)
			{
				temp.append("&r").append(g.name).append("&r");
			}

			end = match.replaceFirst(temp.toString());
			temp = new StringBuilder();
		}

		return end;
	}

	private String getGroupPrefixString(String username)
	{
		Matcher match = ConfigChat.groupRegex.matcher(ConfigChat.groupPrefixFormat);

		ArrayList<TreeSet<Group>> list = getGroupsList(match, username);

		String end = "";

		StringBuilder temp = new StringBuilder();
		for (TreeSet<Group> set : list)
		{
			for (Group g : set)
			{
				temp.insert(0, "&r" + g.prefix + "&r");
			}

			end = match.replaceFirst(temp.toString());
			temp = new StringBuilder();
		}

		return end;
	}

	private String getGroupSuffixString(String username)
	{
		Matcher match = ConfigChat.groupRegex.matcher(ConfigChat.groupSuffixFormat);

		ArrayList<TreeSet<Group>> list = getGroupsList(match, username);

		String end = "";

		StringBuilder temp = new StringBuilder();
		for (TreeSet<Group> set : list)
		{
			for (Group g : set)
			{
				temp.append("&r").append(g.suffix).append("&r");
			}

			end = match.replaceFirst(temp.toString());
			temp = new StringBuilder();
		}

		return end;
	}

	private ArrayList<TreeSet<Group>> getGroupsList(Matcher match, String username)
	{
		ArrayList<TreeSet<Group>> list = new ArrayList<TreeSet<Group>>();

		String whole;
		String[] p;
		TreeSet<Group> set;
		while (match.find())
		{
			whole = match.group();
			whole = whole.replaceAll("\\{", "").replaceAll("\\}", "");
			p = whole.split("\\<\\:\\>", 2);
			if (p[0].equalsIgnoreCase("..."))
			{
				p[0] = null;
			}
			if (p[1].equalsIgnoreCase("..."))
			{
				p[1] = null;
			}

			set = SqlHelper.getGroupsForChat(p[0], p[1], username);
			if (set != null)
			{
				list.add(set);
			}
		}

		list = removeDuplicates(list);
		return list;
	}

	private ArrayList<TreeSet<Group>> removeDuplicates(ArrayList<TreeSet<Group>> list)
	{
		HashSet<Group> used = new HashSet<Group>();

		for (TreeSet set : list)
		{
			for (Group g : used)
			{
				set.remove(g);
			}

			// add all the remaining...
			used.addAll(set);
		}

		return list;
	}
}
