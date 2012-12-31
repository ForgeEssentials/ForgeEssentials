package com.ForgeEssentials.chat;

import java.util.ArrayList;

import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet3Chat;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.ServerChatEvent;

import com.ForgeEssentials.permission.Group;
import com.ForgeEssentials.permission.GroupManager;
import com.ForgeEssentials.permission.PermissionsAPI;
import com.ForgeEssentials.permission.PlayerManager;
import com.ForgeEssentials.permission.PlayerPermData;
import com.ForgeEssentials.permission.Zone;
import com.ForgeEssentials.permission.ZoneManager;
import com.ForgeEssentials.permission.query.PermQueryPlayer;
import com.ForgeEssentials.util.FEChatFormatCodes;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.AreaSelector.Point;

import cpw.mods.fml.common.network.IChatListener;

public class Chat implements IChatListener
{

	@ForgeSubscribe
	public void chatEvent(ServerChatEvent event)
	{
		String message = event.message;
		
		/*
		 * Colorize!
		 */
		
		if(event.message.contains("&") && PermissionsAPI.checkPermAllowed(new PermQueryPlayer(event.player, "ForgeEssentials.chat.usecolor.chat")))
		{
			System.out.println("COLOR CODE");
			message = event.message.replaceAll("&", FEChatFormatCodes.CODE.toString());
		}
		
		String prefix = "";
		String suffix = "";
		String rank = "";
		String zoneID = "";
		
		try
		{
			Zone zone = ZoneManager.getWhichZoneIn(new Point(event.player), event.player.worldObj);
			PlayerPermData playerData = PlayerManager.getPlayerData(zone.getZoneID(), event.username);

			prefix = playerData.prefix;
			suffix = playerData.suffix;

			ArrayList<Group> groups = GroupManager.getApplicableGroups(event.player, false);

			if (groups.isEmpty())
			{
				rank = GroupManager.DEFAULT.name;
				prefix = GroupManager.DEFAULT.prefix + prefix;
				suffix = suffix + GroupManager.DEFAULT.suffix;
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
		
		event.line = format.replaceAll("%health", "" + event.player.getHealth()).replaceAll("%reset", FEChatFormatCodes.RESET + "").replaceAll("%red", FEChatFormatCodes.RED + "").replaceAll("%yellow", FEChatFormatCodes.YELLOW + "").replaceAll("%black", FEChatFormatCodes.BLACK + "").replaceAll("%darkblue", FEChatFormatCodes.DARKBLUE + "").replaceAll("%darkgreen", FEChatFormatCodes.DARKGREEN + "").replaceAll("%darkaqua", FEChatFormatCodes.DARKAQUA + "").replaceAll("%darkred", FEChatFormatCodes.DARKRED + "").replaceAll("%purple", FEChatFormatCodes.PURPLE + "").replaceAll("%gold", FEChatFormatCodes.GOLD + "").replaceAll("%grey", FEChatFormatCodes.GREY + "").replaceAll("%darkgrey", FEChatFormatCodes.DARKGREY + "").replaceAll("%indigo", FEChatFormatCodes.INDIGO + "").replaceAll("%green", FEChatFormatCodes.GREEN + "").replaceAll("%aqua", FEChatFormatCodes.AQUA + "").replaceAll("%pink", FEChatFormatCodes.PINK + "").replaceAll("%white", FEChatFormatCodes.WHITE + "").replaceAll("%random", FEChatFormatCodes.RANDOM + "").replaceAll("%bold", FEChatFormatCodes.BOLD + "").replaceAll("%strike", FEChatFormatCodes.STRIKE + "").replaceAll("%underline", FEChatFormatCodes.UNDERLINE + "").replaceAll("%italics", FEChatFormatCodes.ITALICS + "").replaceAll("%message", message).replaceAll("%username", event.username).replaceAll("%rank", rank).replaceAll("%zone", zoneID).replace("%prefix", prefix).replaceAll("%suffix", suffix);
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

}
