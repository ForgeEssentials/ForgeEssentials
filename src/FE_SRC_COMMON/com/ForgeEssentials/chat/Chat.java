package com.ForgeEssentials.chat;

import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.permission.Group;
import com.ForgeEssentials.permission.PermissionsAPI;
import com.ForgeEssentials.permission.Zone;
import com.ForgeEssentials.permission.ZoneManager;
import com.ForgeEssentials.permission.query.PermQueryPlayer;
import com.ForgeEssentials.util.FEChatFormatCodes;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.AreaSelector.Point;

import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet3Chat;

import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.ServerChatEvent;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.common.network.IChatListener;

public class Chat implements IChatListener
{
	public static List<String> bannedWords = new ArrayList<String>();
	public static boolean censor;
	
	@ForgeSubscribe
	public void chatEvent(ServerChatEvent event)
	{
		/*
		 * Mute?
		 */
		
		if(event.player.getEntityData().getCompoundTag(event.player.PERSISTED_NBT_TAG).getBoolean("mute"))
		{
			event.setCanceled(true);
			event.player.sendChatToPlayer("You are muted.");
			return;
		}
		
		String message = event.message;
		String nickname = event.username;
		
		if(censor)
		{
			for(String word : bannedWords) message = replaceAllIgnoreCase(message, word, "###");
		}
		
		/*
		 * Nickname
		 */
		
		if(event.player.getEntityData().getCompoundTag(event.player.PERSISTED_NBT_TAG).hasKey("nickname"))
		{
			nickname = event.player.getEntityData().getCompoundTag(event.player.PERSISTED_NBT_TAG).getString("nickname");
		}
		
		/*
		 * Colorize!
		 */
		
		if(event.message.contains("&"))
		{
			if(PermissionsAPI.checkPermAllowed(new PermQueryPlayer(event.player, "ForgeEssentials.chat.usecolor")))
			{
				message = event.message.replaceAll("&", FEChatFormatCodes.CODE.toString());
			}
		}
		
		String prefix = "";
		String suffix = "";
		String rank = "";
		String zoneID = "";
		
		try
		{
			Zone zone = ZoneManager.getWhichZoneIn(new Point(event.player), event.player.worldObj);
			PlayerInfo info = PlayerInfo.getPlayerInfo(event.player);

			prefix = info.prefix;
			suffix = info.suffix;

			ArrayList<Group> groups = PermissionsAPI.getApplicableGroups(event.player, false);

			if (groups.isEmpty())
			{
				rank = PermissionsAPI.GROUP_DEFAULT;
				prefix = PermissionsAPI.DEFAULT.prefix + prefix;
				suffix = suffix + PermissionsAPI.DEFAULT.suffix;
			}
			else
			{
				rank = groups.get(groups.size() - 1).name;

				for (Group group : groups)
				{
					prefix = group.prefix + prefix;
					suffix = suffix + group.suffix;
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		OutputHandler.debug("TESTING!!!!!   prefix: " + prefix + "    suffix: " + suffix);

		String format = ModuleChat.conf.chatFormat;
		format = ModuleChat.conf.chatFormat == null || ModuleChat.conf.chatFormat == "" ? "<%username>%message" : ModuleChat.conf.chatFormat;
		
		event.line = format.replaceAll("%health", "" + event.player.getHealth()).replaceAll("%reset", FEChatFormatCodes.RESET + "").replaceAll("%red", FEChatFormatCodes.RED + "").replaceAll("%yellow", FEChatFormatCodes.YELLOW + "").replaceAll("%black", FEChatFormatCodes.BLACK + "").replaceAll("%darkblue", FEChatFormatCodes.DARKBLUE + "").replaceAll("%darkgreen", FEChatFormatCodes.DARKGREEN + "").replaceAll("%darkaqua", FEChatFormatCodes.DARKAQUA + "").replaceAll("%darkred", FEChatFormatCodes.DARKRED + "").replaceAll("%purple", FEChatFormatCodes.PURPLE + "").replaceAll("%gold", FEChatFormatCodes.GOLD + "").replaceAll("%grey", FEChatFormatCodes.GREY + "").replaceAll("%darkgrey", FEChatFormatCodes.DARKGREY + "").replaceAll("%indigo", FEChatFormatCodes.INDIGO + "").replaceAll("%green", FEChatFormatCodes.GREEN + "").replaceAll("%aqua", FEChatFormatCodes.AQUA + "").replaceAll("%pink", FEChatFormatCodes.PINK + "").replaceAll("%white", FEChatFormatCodes.WHITE + "").replaceAll("%random", FEChatFormatCodes.RANDOM + "").replaceAll("%bold", FEChatFormatCodes.BOLD + "").replaceAll("%strike", FEChatFormatCodes.STRIKE + "").replaceAll("%underline", FEChatFormatCodes.UNDERLINE + "").replaceAll("%italics", FEChatFormatCodes.ITALICS + "").replaceAll("%message", message).replaceAll("%username", nickname).replaceAll("%rank", rank).replaceAll("%zone", zoneID).replace("%prefix", prefix).replaceAll("%suffix", suffix);
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
        if(search.equals(replacement)) return text;
        StringBuffer buffer = new StringBuffer(text);
        String lowerSearch = search.toLowerCase();
        int i = 0;
        int prev = 0;
        while((i = buffer.toString().toLowerCase().indexOf(lowerSearch, prev)) > -1)
        {
            buffer.replace(i, i+search.length(), replacement);
            prev = i+replacement.length();
        }
        return buffer.toString();
	}
}
