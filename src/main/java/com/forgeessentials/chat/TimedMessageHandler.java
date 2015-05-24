package com.forgeessentials.chat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.config.Configuration;

import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.misc.TaskRegistry;
import com.forgeessentials.core.moduleLauncher.config.IConfigLoader.ConfigLoaderBase;
import com.forgeessentials.scripting.ScriptArguments;
import com.forgeessentials.util.FunctionHelper;

public class TimedMessageHandler extends ConfigLoaderBase implements Runnable
{

    private static TimedMessageHandler instance;

    public static final String CATEGORY = ModuleChat.CONFIG_CATEGORY + ".TimedMessage";
    
    public static final String MESSAGES_HELP = "Each line is 1 message. You can use scripting arguments and color codes.";
    
    public static final String[] MESSAGES_DEFAULT = new String[] { "\"This server uses ForgeEssentials\"", "\"Change these messages in the Chat config\"",
            "\"The timing can be changed there too!\"" };

    private boolean enabled;

    private int interval;
    
    private boolean random;
    
    public List<String> messages = new ArrayList<>();
    
    public int currentMessageIdx;

    public TimedMessageHandler()
    {
        instance = this;
        ForgeEssentials.getConfigManager().registerLoader(ModuleChat.CONFIG_CATEGORY, this);
    }

    public static TimedMessageHandler getInstance()
    {
        return instance;
    }

    public boolean isEnabled()
    {
        return enabled;
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
        TaskRegistry.getInstance().remove(this);
        if (enabled)
            TaskRegistry.getInstance().scheduleRepeated(this, interval * 1000);
    }

    @Override
    public void run()
    {
        if (messages.isEmpty())
            return;

        if (random)
            currentMessageIdx = new Random().nextInt(messages.size());
        else if (++currentMessageIdx >= messages.size())
            currentMessageIdx = 0;
        send(currentMessageIdx);
    }

    @SuppressWarnings("unchecked")
    public void send(int idx)
    {
        if (idx < 0 || idx >= messages.size())
            return;
        String message = messages.get(idx);
        for (EntityPlayerMP player : (List<EntityPlayerMP>) MinecraftServer.getServer().getConfigurationManager().playerEntityList)
        {
            String formattedMsg = FunctionHelper.formatColors(FunctionHelper.format(ScriptArguments.process(message, player)));
            player.addChatMessage(new ChatComponentText(formattedMsg));
        }
        // OutputHandler.broadcast(new ChatComponentText(messages.get(currentMessageIdx)));
    }

    @Override
    public void load(Configuration config, boolean isReload)
    {
        config.addCustomCategoryComment(CATEGORY, "Automated spam");
        enabled = config.get(CATEGORY, "enable", false).getBoolean(true);
        random = config.get(CATEGORY, "random", false).getBoolean(false);
        interval = config.get(CATEGORY, "inverval", 60, "Interval in seconds").getInt();
        messages = Arrays.asList(config.get(CATEGORY, "messages", MESSAGES_DEFAULT, MESSAGES_HELP).getStringList());

        setEnabled(enabled);
    }

    @Override
    public void save(Configuration config)
    {
        config.get(CATEGORY, "enable", true).set(enabled);
        config.get(CATEGORY, "random", false).set(random);
        config.get(CATEGORY, "inverval", 60, "Interval in seconds").set(interval);
        config.get(CATEGORY, "messages", MESSAGES_DEFAULT, MESSAGES_HELP).set(messages.toArray(new String[0]));
    }

}
