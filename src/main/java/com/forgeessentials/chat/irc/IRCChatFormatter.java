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
import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.UserIdent;
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

    public static String ircHeader;
    public static String ircPrivateHeader;
    public static String mcHeader;


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

		// Player info
		String playerPrefix = FunctionHelper.formatColors(FunctionHelper.getPlayerPrefixSuffix(new UserIdent(event.player), false));
		String playerSuffix = FunctionHelper.formatColors(FunctionHelper.getPlayerPrefixSuffix(new UserIdent(event.player), true));
		String groupPrefix = FunctionHelper.formatColors(FunctionHelper.getPlayerGroupPrefixSuffix(new UserIdent(event.player), false));
		String groupSuffix = FunctionHelper.formatColors(FunctionHelper.getPlayerGroupPrefixSuffix(new UserIdent(event.player), true));
		String zoneID = APIRegistry.perms.getServerZone().getZoneAt(new WorldPoint(event.player)).getName();
		String rank = "";

        // It may be beneficial to make this a public function. -RlonRyan
        String format = mcHeader;

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

        // random nice things...
        format = FunctionHelper.replaceAllIgnoreCase(format, "%health", "" + health);

        format = FunctionHelper.format(format);

        // essentials
        format = FunctionHelper.replaceAllIgnoreCase(format, "%playerPrefix", playerPrefix);
        format = FunctionHelper.replaceAllIgnoreCase(format, "%playerSuffix", playerSuffix);
        format = FunctionHelper.replaceAllIgnoreCase(format, "%groupPrefix", groupPrefix);
        format = FunctionHelper.replaceAllIgnoreCase(format, "%groupSuffix", groupSuffix);
        format = FunctionHelper.replaceAllIgnoreCase(format, "%username", nickname);
        // if(!enable_chat%){ //whereas enable chat is a boolean that can be set
        // in the config or whatever
        // //allowing the use of %codes in chat
        format = FunctionHelper.replaceAllIgnoreCase(format, "%message", message);
        // }

        // finally make it the chat line.
        // TODO: This is probably incorrect with regards to coloring

        System.out.println(format);
        IRCHelper.postIRC(format);
    }

    public static String formatIRCHeader(String header, String channel, String ircUser)
    {
        String format = header;
        format = FunctionHelper.replaceAllIgnoreCase(format, "%channel", channel);
        format = FunctionHelper.replaceAllIgnoreCase(format, "%ircUser", ircUser);
        return FunctionHelper.formatColors(format);
    }
}
