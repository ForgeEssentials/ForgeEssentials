package com.forgeessentials.core.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigData
{
    private String Name;
    private ForgeConfigSpec config;
    private ForgeConfigSpec.Builder builder;

    public ConfigData(String Name, ForgeConfigSpec config, ForgeConfigSpec.Builder builder)
    {
        if (Name == null || builder == null)
        {
            throw new NullPointerException(
                    "WTF, why are we geting null config items\nSomeone is doing some schetchy shit");
        }
        this.Name = Name;
        this.config = config;
        this.builder = builder;
    }

    public void setSpec(ForgeConfigSpec config)
    {
        this.config = config;
    }

    public ForgeConfigSpec getSpec()
    {
        return this.config;
    }

    public String getName()
    {
        return Name;
    }

    public ForgeConfigSpec.Builder getSpecBuilder()
    {
        return this.builder;
    }
}
