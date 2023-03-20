package com.forgeessentials.core.misc;

import java.util.List;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.command.CommandSource;

public class FEcommandData
{
    public FEcommandData(String mainName, List<String> aliases, ForgeEssentialsCommandBuilder commandData, CommandDispatcher<CommandSource> dispatcher)
    {
        name = mainName;
        Aliases = aliases;
        registered= false;
        data =commandData;
        disp = dispatcher;
    }
    public FEcommandData(ForgeEssentialsCommandBuilder commandBuilder, CommandDispatcher<CommandSource> dispatcher)
    {
        name = commandBuilder.getName();
        Aliases = commandBuilder.getDefaultAliases();
        registered= false;
        data = commandBuilder;
        disp = dispatcher;
    }

    private String name;
    protected List<String> Aliases;
    private boolean registered;
    private ForgeEssentialsCommandBuilder data;
    private CommandDispatcher<CommandSource> disp;

    public String getName()
    {
        return name;
    }
    public List<String> getAliases()
    {
        return Aliases;
    }
    public void setAliases(List<String> aliases)
    {
        Aliases = aliases;
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
