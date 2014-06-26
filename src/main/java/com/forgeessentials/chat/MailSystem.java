package com.forgeessentials.chat;

import com.forgeessentials.data.api.ClassContainer;
import com.forgeessentials.data.api.DataStorageManager;
import com.forgeessentials.util.ChatUtils;
import com.forgeessentials.util.FunctionHelper;
import com.google.common.collect.HashMultimap;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;

public class MailSystem {
    private static HashMultimap<String, Mail> map = HashMultimap.create();

    public static void AddMail(Mail mail)
    {
        map.put(mail.getReceiver(), mail);
        DataStorageManager.getReccomendedDriver().saveObject(new ClassContainer(Mail.class), mail);

        EntityPlayer player = FunctionHelper.getPlayerForName(mail.getReceiver());

        if (player != null)
        {
            receiveMail(player);
        }
    }

    public static void LoadAll()
    {
        for (Object obj : DataStorageManager.getReccomendedDriver().loadAllObjects(new ClassContainer(Mail.class)))
        {
            Mail mail = (Mail) obj;
            map.put(mail.getReceiver(), mail);
        }
    }

    public static void SaveAll()
    {
        for (Mail mail : map.values())
        {
            DataStorageManager.getReccomendedDriver().saveObject(new ClassContainer(Mail.class), mail);
        }
    }

    public static void receiveMail(EntityPlayer receiver)
    {
        if (map.containsKey(receiver.username))
        {
            ChatUtils.sendMessage(receiver, EnumChatFormatting.GREEN + "--- Your mail ---");
            for (Mail mail : map.get(receiver.username))
            {
                ChatUtils.sendMessage(receiver, EnumChatFormatting.GREEN + "{" + mail.getSender() + "} " + EnumChatFormatting.WHITE + mail.getMessage());
                DataStorageManager.getReccomendedDriver().deleteObject(new ClassContainer(Mail.class), mail.getKey());
            }
            ChatUtils.sendMessage(receiver, EnumChatFormatting.GREEN + "--- End of mail ---");
        }
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent e)
    {
        receiveMail(e.player);
    }
}
