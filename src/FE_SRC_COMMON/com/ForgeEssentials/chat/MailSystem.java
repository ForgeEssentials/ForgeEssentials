package com.ForgeEssentials.chat;

import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.api.data.DataStorageManager;
import com.ForgeEssentials.util.FEChatFormatCodes;
import com.ForgeEssentials.util.FunctionHelper;
import com.google.common.collect.HashMultimap;

import cpw.mods.fml.common.IPlayerTracker;

public class MailSystem implements IPlayerTracker
{
	private static HashMultimap<String, Mail> map = HashMultimap.create();
	
	public static void AddMail(Mail mail)
	{
		map.put(mail.getReceiver(), mail);
		DataStorageManager.getReccomendedDriver().saveObject(mail);
		
		if(FunctionHelper.getPlayerFromPartialName(mail.getReceiver()) != null)
		{
			receiveMail(FunctionHelper.getPlayerFromPartialName(mail.getReceiver()));
		}
	}
	
	public static void LoadAll()
	{
		for(Object obj : DataStorageManager.getReccomendedDriver().loadAllObjects(MailSystem.class))
		{
			Mail mail = (Mail) obj;
			map.put(mail.getReceiver(), mail);
		}
	}
	
	public static void SaveAll()
	{
		for(Mail mail : map.values())
		{
			DataStorageManager.getReccomendedDriver().saveObject(mail);
		}
	}
	
	public static void receiveMail(EntityPlayer receiver)
	{
		if(map.containsKey(receiver.username))
		{
			receiver.sendChatToPlayer(FEChatFormatCodes.GREEN + "--- Your mail ---");
			for(Mail mail : map.get(receiver.username))
			{
				receiver.sendChatToPlayer(FEChatFormatCodes.GREEN + "{" + mail.getSender() + "} " + FEChatFormatCodes.WHITE + mail.getMessage());
					DataStorageManager.getReccomendedDriver().deleteObject(Mail.class, mail.getKey());
			}
			receiver.sendChatToPlayer(FEChatFormatCodes.GREEN + "--- End of mail ---");
		}
	}

	@Override
	public void onPlayerLogin(EntityPlayer player)
	{
		receiveMail(player);
	}

	@Override
	public void onPlayerLogout(EntityPlayer player) {}

	@Override
	public void onPlayerChangedDimension(EntityPlayer player) {}

	@Override
	public void onPlayerRespawn(EntityPlayer player) {}
}
