package com.forgeessentials.core.misc;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.command.CommandSource;

public class FECommandData
{
    public FECommandData(String mainName, ForgeEssentialsCommandBuilder commandData, CommandDispatcher<CommandSource> dispatcher)
    {
        name = mainName;
        registered= false;
        data =commandData;
        disp = dispatcher;
    }
    public FECommandData(ForgeEssentialsCommandBuilder commandBuilder, CommandDispatcher<CommandSource> dispatcher)
    {
        name = commandBuilder.getName();
        registered= false;
        data = commandBuilder;
        disp = dispatcher;
    }

    private String name;
    private boolean registered;
    private ForgeEssentialsCommandBuilder data;
    private CommandDispatcher<CommandSource> disp;

    public String getName()
    {
        return name;
    }
    public ForgeEssentialsCommandBuilder getData()
    {
        return data;
    }
    public CommandDispatcher<CommandSource> getDisp()
    {
        return disp;
    }
    public void setRegistered(boolean registered)
    {
        this.registered = registered;
    }
    public boolean isRegistered()
    {
        return registered;
    }
}
