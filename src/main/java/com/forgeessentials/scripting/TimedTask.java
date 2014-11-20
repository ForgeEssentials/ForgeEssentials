package com.forgeessentials.scripting;

import com.forgeessentials.commons.IReconstructData;
import com.forgeessentials.commons.SaveableObject;
import com.forgeessentials.commons.SaveableObject.Reconstructor;
import com.forgeessentials.commons.SaveableObject.SaveableField;
import com.forgeessentials.util.tasks.TaskRegistry;
import net.minecraft.server.MinecraftServer;

import java.util.TimerTask;

@SaveableObject
public class TimedTask extends TimerTask {

    @SaveableField
    private int interval; // in seconds

    @SaveableField
    private String command;

    @SaveableField
    private String name;

    public TimedTask(int interval, String command, String name)
    {
        this.interval = interval;
        this.command = command;
        this.name = name;

        TaskRegistry.registerRecurringTask(this, 0, 0, interval, 0, 0, 0, interval, 0);

    }

    @Override
    public void run()
    {
        MinecraftServer.getServer().getCommandManager().executeCommand(MinecraftServer.getServer(), command);
    }

    public TimedTask(Object interval, Object command, Object name)
    {
        this.interval = (int) interval;
        this.command = (String) command;
        this.name = (String) name;

        TaskRegistry.registerRecurringTask(this, 0, 0, (int) interval, 0, 0, 0, (int) interval, 0);
    }

    @Reconstructor
    private static TimedTask reconstruct(IReconstructData tag)
    {
        return new TimedTask(tag.getFieldValue("interval"), tag.getFieldValue("command"), tag.getFieldValue("name"));
    }

    public String getName()
    {
        return name;
    }

    public String getCommand()
    {
        return command;
    }

    public int getInterval()
    {
        return interval;
    }

}
