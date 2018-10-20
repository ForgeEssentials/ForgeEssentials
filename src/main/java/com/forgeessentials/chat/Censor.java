package com.forgeessentials.chat;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.config.Configuration;

import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.moduleLauncher.config.ConfigLoaderBase;
import com.forgeessentials.util.output.LoggingHandler;
import com.google.common.base.Strings;

public class Censor extends ConfigLoaderBase
{

    private static final String CONFIG_CATEGORY = "Chat.Censor";

    private static final String[] DEFAULT_WORDS = new String[] { "fuck\\S*", "bastard", "moron", "ass", "asshole", "bitch", "shit" };

    private static final String CENSOR_HELP = "Words to be censored. Prepend with ! to disable word boundary check.";

    private List<CensoredWord> filterList = new ArrayList<>();

    public boolean enabled;

    public String censorSymbol;

    public int censorSlap;

    public static class CensoredWord
    {

        public String word;

        public String blank;

        public Pattern pattern;

        public CensoredWord(String word)
        {
            if (word.startsWith("!"))
                word = word.substring(1);
            else
                word = "\\b" + word + "\\b";
            pattern = Pattern.compile(word, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.MULTILINE);
        }

    }

    public Censor()
    {
        ForgeEssentials.getConfigManager().registerLoader(ModuleChat.CONFIG_FILE, this);
    }

    @Override
    public void load(Configuration config, boolean isReload)
    {
        enabled = config.get(CONFIG_CATEGORY, "enable", true).getBoolean(true);
        censorSlap = config.get(CONFIG_CATEGORY, "slapDamage", 1, "Damage to a player when he uses a censored word").getInt();
        censorSymbol = config.get(CONFIG_CATEGORY, "censorSymbol", "#", "Replace censored words with this character").getString();
        if (censorSymbol.length() > 1)
        {
            LoggingHandler.felog.warn("Censor symbol is too long!");
            censorSymbol = censorSymbol.substring(1);
        }
        else if (censorSymbol.isEmpty())
        {
            LoggingHandler.felog.warn("Censor symbol is empty!");
            censorSymbol = "#";
        }
        filterList.clear();
        for (String word : config.get(CONFIG_CATEGORY, "words", DEFAULT_WORDS, CENSOR_HELP).getStringList())
            filterList.add(new CensoredWord(word));
    }

    public String filter(String message)
    {
        return filter(message, null);
    }

    public String filter(String message, EntityPlayer player)
    {
        if (!enabled)
            return message;
        for (CensoredWord filter : filterList)
        {
            Matcher m = filter.pattern.matcher(message);
            if (m.find())
            {
                if (filter.blank == null)
                    filter.blank = Strings.repeat(censorSymbol, m.end() - m.start());
                message = m.replaceAll(filter.blank);
                if (player != null && censorSlap != 0)
                    player.attackEntityFrom(DamageSource.GENERIC, censorSlap);
            }
        }
        return message;
    }

    public String filterIRC(String message)
    {
        if (!enabled)
            return message;
        for (CensoredWord filter : filterList)
        {
            Matcher m = filter.pattern.matcher(message);
            if (m.find())
            {
                if (filter.blank == null)
                    filter.blank = Strings.repeat(censorSymbol, m.end() - m.start());
                message = m.replaceAll(filter.blank);
            }
        }
        return message;
    }

}
