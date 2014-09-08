package com.forgeessentials.chat.irc;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.ServerChatEvent;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.chat.ConfigChat;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.selections.WorldPoint;
import com.google.common.base.Strings;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

// Largely copied from ChatFormatter, to deal with special cases for IRC
public class IRCChatFormatter {

    public static List<String> bannedWords = new ArrayList<String>();
    public static boolean censor;
    public static String censorSymbol;

    public static String gmS;
    public static String gmC;
    public static String gmA;
    public static int censorSlap;

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void chatEvent(ServerChatEvent event)
    {
        // muting this should probably be done elsewhere
        if (event.player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG).getBoolean("mute"))
        {
            event.setCanceled(true);
            return;
        }

        String message = event.message;
        String nickname = event.username;

        // censoring
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

                    if (censorSlap != 0)
                    {
                        event.player.attackEntityFrom(DamageSource.generic, censorSlap);
                    }
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

        // replacing stuff...

        String rank = "";
        String zoneID = "";
        String gPrefix = "";
        String gSuffix = "";

        PlayerInfo info = PlayerInfo.getPlayerInfo(event.player.getPersistentID());
        String playerPrefix = info.prefix == null ? "" : FunctionHelper.formatColors(info.prefix).trim();
        String playerSuffix = info.suffix == null ? "" : FunctionHelper.formatColors(info.suffix).trim();

        zoneID = APIRegistry.perms.getZonesAt(new WorldPoint(event.player)).get(0).getName();

        // Group stuff!!! DO NOT TOUCH!!!
        {
            rank = FunctionHelper.getGroupRankString(event.username);

            gPrefix = FunctionHelper.getGroupPrefixString(event.username);
            gPrefix = FunctionHelper.formatColors(gPrefix).trim();

            gSuffix = FunctionHelper.getGroupSuffixString(event.username);
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

        float health = event.player.getHealth(); // No nice name for player health yet

        format = FunctionHelper.replaceAllIgnoreCase(format, "%healthcolor", "");

        format = FunctionHelper.replaceAllIgnoreCase(format, "%rank", rank);
        format = FunctionHelper.replaceAllIgnoreCase(format, "%zone", zoneID);
        format = FunctionHelper.replaceAllIgnoreCase(format, "%groupPrefix", gPrefix);
        format = FunctionHelper.replaceAllIgnoreCase(format, "%groupSuffix", gSuffix);

        // random nice things...
        format = FunctionHelper.replaceAllIgnoreCase(format, "%health", "" + health);

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
        // TODO: This is probably incorrect with regards to coloring
        IRCHelper.postIRC("<" + event.username + "> " + event.message);
    }

}
