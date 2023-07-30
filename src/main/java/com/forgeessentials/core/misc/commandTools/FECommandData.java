package com.forgeessentials.core.misc.commandTools;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;


public class FECommandData
{
    private String name;
    private boolean registered;
    private ForgeEssentialsCommandBuilder data;

    public FECommandData(String mainName, ForgeEssentialsCommandBuilder commandData)
    {
        name = mainName;
        registered = false;
        data = commandData;
    }

    public FECommandData(ForgeEssentialsCommandBuilder commandBuilder)
    {
        name = commandBuilder.getName();
        registered = false;
        data = commandBuilder;
    }

    public String getName()
    {
        return name;
    }

    public ForgeEssentialsCommandBuilder getData()
    {
        return data;
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
