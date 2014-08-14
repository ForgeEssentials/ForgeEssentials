package com.forgeessentials.worldedit.compat;

import com.forgeessentials.api.permissions.RegGroup;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.preloader.FEModContainer;
import com.sk89q.worldedit.LocalConfiguration;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.extension.platform.AbstractPlatform;
import com.sk89q.worldedit.extension.platform.Capability;
import com.sk89q.worldedit.extension.platform.Preference;
import com.sk89q.worldedit.util.command.CommandMapping;
import com.sk89q.worldedit.util.command.Description;
import com.sk89q.worldedit.util.command.Dispatcher;
import com.sk89q.worldedit.world.World;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.server.MinecraftServer;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class FEPlatform extends AbstractPlatform{

    @Override public int resolveItem(String s)
    {
        return 0;
    }

    @Override public boolean isValidMobType(String s)
    {
        return false;
    }

    @Override public void reload()
    {

    }

    @Override public Player matchPlayer(Player player)
    {
        return null;
    }

    @Override public World matchWorld(World world)
    {
        return null;
    }

    @Override public void registerCommands(Dispatcher dispatcher)
    {
        final MinecraftServer server = MinecraftServer.getServer();
        if (server == null) return;
        ServerCommandManager mcMan = (ServerCommandManager) server.getCommandManager();

        for (final CommandMapping command : dispatcher.getCommands()) {
            final Description description = command.getDescription();

            mcMan.registerCommand(new ForgeEssentialsCommandBase() {
                @Override
                public String getCommandName() {
                    return command.getPrimaryAlias();
                }

                @Override
                public List<String> getCommandAliases() {
                    return Arrays.asList(command.getAllAliases());
                }

                @Override
                public void processCommand(ICommandSender var1, String[] var2) {}

                @Override
                public String getCommandUsage(ICommandSender icommandsender) {
                    return "/" + command.getPrimaryAlias() + " " + description.getUsage();
                }

                @Override
                public String getCommandPerm()
                {
                    return command.getDescription().getPermissions().get(0);
                }

                @Override
                public RegGroup getReggroup()
                { return RegGroup.ZONE_ADMINS;}

                @Override public boolean canConsoleUseCommand(){return true;}

            });
        }

    }

    @Override public void registerGameHooks()
    {

    }

    @Override public LocalConfiguration getConfiguration()
    {
        return null;
    }

    @Override public String getVersion()
    {
        return FEModContainer.version;
    }

    @Override public String getPlatformName()
    {
        return "Forge-ForgeEssentials";
    }

    @Override public String getPlatformVersion()
    {
        return FEModContainer.version;
    }

    @Override public Map<Capability, Preference> getCapabilities()
    {
        return null;
    }
}
