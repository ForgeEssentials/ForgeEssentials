package com.forgeessentials.util;

import net.minecraftforge.common.config.Configuration;

public class FECommandManager
{

    public static IFECommandManager instance;

    public interface ConfigurableCommand
    {

        void loadConfig(Configuration config, String category);

        void loadData();

    }

    public static void registerCommand(ForgeEssentialsCommandBase command)
    {
        registerCommand(command, false);
    }

    public static void registerCommand(ForgeEssentialsCommandBase command, boolean registerNow)
    {
        instance.registerCommand(command, registerNow);
    }

    public static void deegisterCommand(String name)
    {
        instance.deegisterCommand(name);
    }

    public static void registerCommands()
    {
        instance.registerCommands();
    }

    public static void clearRegisteredCommands()
    {
        instance.clearRegisteredCommands();
    }

}
