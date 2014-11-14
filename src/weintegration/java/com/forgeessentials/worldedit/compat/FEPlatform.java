package com.forgeessentials.worldedit.compat;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.preloader.FEModContainer;
import com.sk89q.worldedit.LocalConfiguration;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.extension.platform.AbstractPlatform;
import com.sk89q.worldedit.extension.platform.Capability;
import com.sk89q.worldedit.extension.platform.Preference;
import com.sk89q.worldedit.forge.ForgeWorldEdit;
import com.sk89q.worldedit.util.command.CommandMapping;
import com.sk89q.worldedit.util.command.Dispatcher;
import com.sk89q.worldedit.world.World;
import cpw.mods.fml.common.Mod;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.permissions.PermissionsManager;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import java.util.EnumMap;
import java.util.Map;

public class FEPlatform extends AbstractPlatform {

    protected LocalConfiguration config = new LocalConfiguration() {
        @Override
        public void load()
        {
        }
    };

    @Override
    public int resolveItem(String s)
    {
        return 0;
    }

    @Override
    public boolean isValidMobType(String s)
    {
        return false;
    }

    @Override
    public void reload()
    {
    }

    @Override
    public Player matchPlayer(Player player)
    {
        return null;
    }

    @Override
    public World matchWorld(World world)
    {
        return null;
    }

    @Override
    public void registerCommands(Dispatcher dispatcher)
    {
        final MinecraftServer server = MinecraftServer.getServer();
        if (server == null)
            return;

        for (CommandMapping cmdData : dispatcher.getCommands())
        {
            ForgeEssentialsCommandBase command = new WECommandWrapper(cmdData);
            command.register();

            // register additional permissions
            if (cmdData.getDescription().getPermissions().size() > 0)
                for (int i = 1; i < cmdData.getDescription().getPermissions().size(); i++)
                    PermissionsManager.registerPermission(cmdData.getDescription().getPermissions().get(i), RegisteredPermValue.OP);
        }
        
        PermissionsManager.registerPermission(WECommandWrapper.PERM_OTHER, RegisteredPermValue.TRUE);
    }

    @Override
    public void registerGameHooks()
    {
    }

    @Override
    public LocalConfiguration getConfiguration()
    {
        return config;
    }

    @Override
    public String getVersion()
    {
        return ForgeWorldEdit.class.getAnnotation(Mod.class).version();
    }

    @Override
    public String getPlatformName()
    {
        return "Forge-ForgeEssentials";
    }

    @Override
    public String getPlatformVersion()
    {
        return FEModContainer.version;
    }

    @Override
    public Map<Capability, Preference> getCapabilities()
    {
        Map<Capability, Preference> capabilities = new EnumMap<Capability, Preference>(Capability.class);
        capabilities.put(Capability.PERMISSIONS, Preference.PREFERRED);
        capabilities.put(Capability.USER_COMMANDS, Preference.PREFERRED);

        capabilities.put(Capability.CONFIGURATION, Preference.PREFER_OTHERS);
        capabilities.put(Capability.WORLDEDIT_CUI, Preference.PREFER_OTHERS);
        capabilities.put(Capability.GAME_HOOKS, Preference.PREFER_OTHERS);
        capabilities.put(Capability.WORLD_EDITING, Preference.PREFER_OTHERS);
        return capabilities;
    }
}
