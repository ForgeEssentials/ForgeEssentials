package com.forgeessentials.core.misc.commandTools;

import java.util.List;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;


public class FECommandData
{
    private String name;
    private boolean registered;
    private ForgeEssentialsCommandBuilder builder;
    private List<String> aliases;

    public FECommandData(String mainName, ForgeEssentialsCommandBuilder commandBuilder)
    {
        name = mainName;
        registered = false;
        builder = commandBuilder;
        aliases = commandBuilder.getAliases();
    }

    public FECommandData(ForgeEssentialsCommandBuilder commandBuilder)
    {
        name = commandBuilder.getName();
        registered = false;
        builder = commandBuilder;
        aliases = commandBuilder.getAliases();
    }

    public String getName()
    {
        return name;
    }

    public ForgeEssentialsCommandBuilder getBuilder()
    {
        return builder;
    }

    public void setRegistered(boolean registered)
    {
        this.registered = registered;
    }

    public boolean isRegistered()
    {
        return registered;
    }

	public List<String> getAliases() {
		return aliases;
	}

	public void setAliases(List<String> aliases) {
		this.aliases = aliases;
	}
}
