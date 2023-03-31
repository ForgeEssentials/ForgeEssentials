package com.forgeessentials.chat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.config.ConfigBase;
import com.forgeessentials.core.config.ConfigData;
import com.forgeessentials.core.config.ConfigSaver;
import com.forgeessentials.core.misc.TaskRegistry;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.output.LoggingHandler;
import com.google.gson.JsonParseException;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.Builder;

public class TimedMessages implements ConfigSaver, Runnable
{

    public static final String CATEGORY = "TimedMessage";

    public static final String MESSAGES_HELP = "Each line is 1 message. \nYou can use scripting arguments and color codes. "
            + "\nUsing json messages (tellraw) is also supported";

    public static final List<String> MESSAGES_DEFAULT = new ArrayList<String>(){{add("This server runs ForgeEssentials server management mod");}};

    private static List<String> messages = new ArrayList<>();

    protected static int interval;

    private static boolean shuffle;

    protected static boolean enabled;

    protected List<Integer> messageOrder = new ArrayList<>();

    protected int currentIndex;
    
    public static TimedMessages tm;

    public TimedMessages()
    {
        ForgeEssentials.getConfigManager().registerSpecs(ModuleChat.CONFIG_FILE, this);
        tm = this;
    }

    @Override
    public void run()
    {
        if (messages.isEmpty() || !enabled)
            return;
        if (currentIndex >= messages.size())
            currentIndex = 0;
        broadcastMessage(messageOrder.get(currentIndex));
        currentIndex++;
    }

    public void broadcastMessage(int index)
    {
        if (index >= 0 && index < messages.size())
            ChatOutputHandler.broadcast(formatMessage(messages.get(index)));
    }

    public void addMessage(String message)
    {
        messages.add(message);
        initMessageOrder();
    }

    public void initMessageOrder()
    {
        messageOrder.clear();
        for (int i = 0; i < messages.size(); i++)
            messageOrder.add(i);
        if (shuffle)
            Collections.shuffle(messageOrder);
    }

    public int getInterval()
    {
        return interval;
    }

    public void setInterval(int interval)
    {
        if (interval < 0)
            interval = 0;
        if (TimedMessages.interval == interval)
            return;
        if (TimedMessages.interval > 0)
            TaskRegistry.remove(this);
        TimedMessages.interval = interval;
        if (interval > 0)
            TaskRegistry.scheduleRepeated(this, interval * 1000);
    }

    public ITextComponent formatMessage(String message)
    {
        message = ModuleChat.processChatReplacements(null, message);
        try
        {
            return ITextComponent.Serializer.fromJson(message);
        }
        catch (JsonParseException e)
        {
            if (message.contains("{"))
            {
                LoggingHandler.felog.warn("Error in timedmessage format: " + ExceptionUtils.getRootCause(e).getMessage());
            }
            return new StringTextComponent(message);
        }
    }

    // ------------------------------------------------------------
    // Configs
    
    static ForgeConfigSpec.IntValue FEinverval;
    static ForgeConfigSpec.BooleanValue FEenabled;
    static ForgeConfigSpec.BooleanValue FEshuffle;
    static ForgeConfigSpec.ConfigValue<List<? extends String>> FEmessages;

    @Override
    public void load(Builder BUILDER, boolean isReload)
    {
        BUILDER.comment("Automated spam").push(CATEGORY);
        FEinverval = BUILDER.comment("Interval in seconds. 0 to disable").defineInRange("inverval", 60, 0, Integer.MAX_VALUE);
        FEenabled = BUILDER.comment("Enable TimedMessages.").define("enabled", false);
        FEshuffle = BUILDER.comment("Shuffle messages").define("shuffle", false);
        FEmessages = BUILDER.comment(MESSAGES_HELP).defineList("messages", MESSAGES_DEFAULT,ConfigBase.stringValidator);
        BUILDER.pop();
    }

    @Override
    public void bakeConfig(boolean reload)
    {
        setInterval(FEinverval.get());
        enabled = FEenabled.get();
        shuffle = FEshuffle.get();
        messages = new ArrayList<>(FEmessages.get());
        initMessageOrder();
    }

    @Override
    public void save(boolean reload)
    {
        FEinverval.set(interval);
        FEshuffle.set(shuffle);
        FEmessages.set(messages);
    }

    @Override
    public ConfigData returnData() {
        return ModuleChat.data;
    }

    // ------------------------------------------------------------
    // Getter functions
    public boolean getShuffle()
    {
        return shuffle;
    }

    public void setShuffle(boolean shuffle)
    {
        TimedMessages.shuffle = shuffle;
    }

    public List<String> getMessages()
    {
        return messages;
    }

    public void setMessages(List<String> messages)
    {
        TimedMessages.messages = messages;
    }
}
