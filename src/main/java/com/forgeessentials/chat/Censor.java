package com.forgeessentials.chat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.config.Configuration;

import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.moduleLauncher.config.ConfigLoader.ConfigLoaderBase;
import com.forgeessentials.util.OutputHandler;
import com.google.common.base.Strings;

public class Censor extends ConfigLoaderBase
{

    private static final String CONFIG_CATEGORY = "Chat.Censor";

    private static final String[] DEFAULT_WORDS = new String[] { "fuck", "ass", "bitch", "shit" };

    public static List<String> bannedWords = new ArrayList<>();

    public static Map<String, Pattern> bannedPatterns = new HashMap<>();

    public boolean enabled;

    public String censorSymbol;

    public int censorSlap;

    public Censor()
    {
        ForgeEssentials.getConfigManager().registerLoader(ModuleChat.CONFIG_FILE, this);
    }

    @Override
    public void load(Configuration config, boolean isReload)
    {
        enabled = config.get(CONFIG_CATEGORY, "enable", true).getBoolean(true);
        bannedWords = new ArrayList<>(Arrays.asList(config.get(CONFIG_CATEGORY, "words", DEFAULT_WORDS, "Words to be censored").getStringList()));
        censorSlap = config.get(CONFIG_CATEGORY, "slapDamage", 1, "Damage to a player when he uses a censored word").getInt();
        censorSymbol = config.get(CONFIG_CATEGORY, "censorSymbol", "#", "Replace censored words with this character").getString();
        if (censorSymbol.length() > 1)
        {
            OutputHandler.felog.warning("Censor symbol is too long!");
            censorSymbol = censorSymbol.substring(1);
        }
        else if (censorSymbol.isEmpty())
        {
            OutputHandler.felog.warning("Censor symbol is empty!");
            censorSymbol = "#";
        }
        buildPatterns();
    }

    public void buildPatterns()
    {
        bannedPatterns.clear();
        for (String word : bannedWords)
        {
            if (word.startsWith("!"))
                word = word.substring(1);
            else
                word = "\\b" + word + "\\b";
            bannedPatterns.put(Strings.repeat(censorSymbol, word.length()),
                    Pattern.compile(word, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.MULTILINE));
        }
    }

    public String filter(EntityPlayerMP player, String message)
    {
        if (!enabled)
            return message;
        for (Entry<String, Pattern> word : bannedPatterns.entrySet())
        {
            Matcher m = word.getValue().matcher(message);
            if (m.find())
            {
                m.replaceAll(word.getKey());
                if (censorSlap != 0)
                    player.attackEntityFrom(DamageSource.generic, censorSlap);
            }
        }
        return message;
    }

}
