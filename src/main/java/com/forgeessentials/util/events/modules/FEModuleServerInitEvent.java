package com.forgeessentials.util.events.modules;

import com.forgeessentials.api.APIRegistry.ForgeEssentialsRegistrar.PermRegister;
import com.forgeessentials.api.permissions.IPermRegisterEvent;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.moduleLauncher.ModuleContainer;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLStateEvent;
import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.List;

public class FEModuleServerInitEvent extends FEModuleEvent {
    private static List<ForgeEssentialsCommandBase> commands = new ArrayList<ForgeEssentialsCommandBase>();
    private FMLServerStartingEvent event;

    public FEModuleServerInitEvent(ModuleContainer container, FMLServerStartingEvent event)
    {
        super(container);
        this.event = event;
    }

    @Override
    public FMLStateEvent getFMLEvent()
    {
        return event;
    }

    public MinecraftServer getServer()
    {
        return event.getServer();
    }

    public void registerServerCommand(ForgeEssentialsCommandBase command)
    {
        commands.add(command);
        event.registerServerCommand(command);
    }

    @PermRegister
    public void registerPermissions(IPermRegisterEvent e)
    {
        for (ForgeEssentialsCommandBase cmd : commands)
        {
            e.registerPermissionLevel(cmd.getCommandPerm(), cmd.getReggroup());
        }
    }
}
