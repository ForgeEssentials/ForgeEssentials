package com.forgeessentials.scripting;

import java.util.TimerTask;

import net.minecraft.server.MinecraftServer;

import com.forgeessentials.core.misc.TaskRegistry;

public class TimedTask extends TimerTask
{

    private int interval; // in seconds

    private String command;

    private String name;

    public TimedTask(int interval, String command, String name)
    {
        this.interval = interval;
        this.command = command;
        this.name = name;
        TaskRegistry.scheduleRepeated(this, interval * 1000);
    }

    @Override
    public void run()
    {
        MinecraftServer.getServer().getCommandManager().executeCommand(MinecraftServer.getServer(), command);
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
