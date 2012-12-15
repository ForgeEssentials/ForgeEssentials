package com.ForgeEssentials.chat;

import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet3Chat;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.ServerChatEvent;

import com.ForgeEssentials.util.FEChatFormatCodes;

import cpw.mods.fml.common.network.IChatListener;

public class Chat implements IChatListener {


	@ForgeSubscribe
	public  void chatEvent(ServerChatEvent event) {
		String format = ModuleChat.conf.chatFormat;
		format = ModuleChat.conf.chatFormat == null || ModuleChat.conf.chatFormat == "" ? "<%username>%message" :ModuleChat.conf.chatFormat;
		event.line =format.replaceAll("%health", ""+event.player.getHealth()).replaceAll("%reset", FEChatFormatCodes.RESET+"")
				.replaceAll("%red",FEChatFormatCodes.RED+"").replaceAll("%yellow",FEChatFormatCodes.YELLOW+"").replaceAll("%black",FEChatFormatCodes.BLACK+"").replaceAll("%darkblue",FEChatFormatCodes.DARKBLUE+"")
				.replaceAll("%darkgreen",FEChatFormatCodes.DARKGREEN+"").replaceAll("%darkaqua",FEChatFormatCodes.DARKAQUA+"").replaceAll("%darkred",FEChatFormatCodes.DARKRED+"").replaceAll("%purple",FEChatFormatCodes.PURPLE+"")
				.replaceAll("%gold",FEChatFormatCodes.GOLD+"").replaceAll("%grey",FEChatFormatCodes.GREY+"").replaceAll("%darkgrey",FEChatFormatCodes.DARKGREY+"").replaceAll("%indigo",FEChatFormatCodes.INDIGO+"")
				.replaceAll("%green",FEChatFormatCodes.GREEN+"").replaceAll("%aqua",FEChatFormatCodes.AQUA+"").replaceAll("%pink",FEChatFormatCodes.PINK+"").replaceAll("%white",FEChatFormatCodes.WHITE+"")
				.replaceAll("%random",FEChatFormatCodes.RANDOM+"").replaceAll("%bold",FEChatFormatCodes.BOLD+"").replaceAll("%strike",FEChatFormatCodes.STRIKE+"").replaceAll("%underline",FEChatFormatCodes.UNDERLINE+"")
				.replaceAll("%italics",FEChatFormatCodes.ITALICS+"").replaceAll("%message", event.message).replaceAll("%username", event.username);
	}
	@Override
	public Packet3Chat serverChat(NetHandler handler, Packet3Chat message) {
		return message;
	}

	@Override
	public Packet3Chat clientChat(NetHandler handler, Packet3Chat message) {
		return message;
	}

	

}
