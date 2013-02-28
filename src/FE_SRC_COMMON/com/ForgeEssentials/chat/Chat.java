package com.ForgeEssentials.chat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.ServerChatEvent;

import com.ForgeEssentials.api.permissions.Group;
import com.ForgeEssentials.api.permissions.PermissionsAPI;
import com.ForgeEssentials.api.permissions.Zone;
import com.ForgeEssentials.api.permissions.ZoneManager;
import com.ForgeEssentials.api.permissions.query.PermQueryPlayer;
import com.ForgeEssentials.chat.commands.CommandPm;
import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.permission.SqlHelper;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.AreaSelector.WorldPoint;
import com.google.common.base.Strings;

public class Chat
{
	public static List<String>	bannedWords	= new ArrayList<String>();
	public static boolean		censor;
	public static String		censorSymbol;

	public static String		gmS;
	public static String		gmC;
	public static String		gmA;

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

		if (CommandPm.isMessagePersistent(event.player.getCommandSenderName()))
		{
			event.setCanceled(true);
			CommandPm.processChat(event.player, event.message.split(" "));
			return;
		}

		String message = event.message;
		String nickname = event.username;
		if (censor)
		{
			for (String word : bannedWords)
			{
				Pattern p = Pattern.compile("(?i)\\b" + word + "\\b");
				Matcher m = p.matcher(message);

				while (m.find())
				{
					int startIndex = m.start();
					int endIndex = m.end();

					int length = endIndex - startIndex;
					String replaceWith = Strings.repeat(censorSymbol, length);

					message = m.replaceAll(replaceWith);
				}
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

		Zone zone = ZoneManager.getWhichZoneIn(new WorldPoint(event.player));
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
		 * if(enable_chat%){ format = replaceAllIngnoreCase(format, "%message",
		 * message); }
		 */
		// replace group, zone, and rank

		if (format.contains("%gm"))
		{
			String gmCode = "";
			if (event.player.theItemInWorldManager.getGameType().isCreative())
			{
				gmCode = gmC;
			}
			else if (event.player.theItemInWorldManager.getGameType().isAdventure())
			{
				gmCode = gmA;
			}
			else
			{
				gmCode = gmS;
			}

			format = FunctionHelper.replaceAllIgnoreCase(format, "%gm", gmCode);
		}

		if (format.contains("%healthcolor"))
		{
			String c = "";
			if (event.player.getHealth() < 6)
			{
				c = "%red";
			}
			else if (event.player.getHealth() < 12)
			{
				c = "%yellow";
			}
			else
			{
				c = "%green";
			}
			format = FunctionHelper.replaceAllIgnoreCase(format, "%healthcolor", c);
		}

		format = FunctionHelper.replaceAllIgnoreCase(format, "%rank", rank);
		format = FunctionHelper.replaceAllIgnoreCase(format, "%zone", zoneID);
		format = FunctionHelper.replaceAllIgnoreCase(format, "%groupPrefix", gPrefix);
		format = FunctionHelper.replaceAllIgnoreCase(format, "%groupSuffix", gSuffix);

		// random nice things...
		format = FunctionHelper.replaceAllIgnoreCase(format, "%health", "" + event.player.getHealth());

		format = FunctionHelper.format(format);

		// essentials
		format = FunctionHelper.replaceAllIgnoreCase(format, "%playerPrefix", playerPrefix);
		format = FunctionHelper.replaceAllIgnoreCase(format, "%playerSuffix", playerSuffix);
		format = FunctionHelper.replaceAllIgnoreCase(format, "%username", nickname);
		// if(!enable_chat%){ //whereas enable chat is a boolean that can be set
		// in the config or whatever
		// //allowing the use of %codes in chat
		format = FunctionHelper.replaceAllIgnoreCase(format, "%message", message);
		// }

		// finally make it the chat line.
		event.line = format;
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
				if (temp.length() != 0)
				{
					temp.append("&r");
				}

				temp.append(g.name);
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
				if (g.prefix.trim().isEmpty())
				{
					continue;
				}

				if (temp.length() == 0)
				{
					temp.append(g.prefix);
				}
				else
				{
					temp.insert(0, g.prefix + "&r");
				}
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
				if (g.suffix.trim().isEmpty())
				{
					continue;
				}

				temp.append("&r").append(g.suffix);
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
