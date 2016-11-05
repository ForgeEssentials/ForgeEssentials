package com.forgeessentials.util;

/**
 * Created by Bjoern on 05.11.2016.
 */
public interface IFECommandManager
{
    void registerCommand(ForgeEssentialsCommandBase command);

    void registerCommand(ForgeEssentialsCommandBase command, boolean registerNow);

    void deegisterCommand(String name);

    void registerCommands();

    void clearRegisteredCommands();
}
