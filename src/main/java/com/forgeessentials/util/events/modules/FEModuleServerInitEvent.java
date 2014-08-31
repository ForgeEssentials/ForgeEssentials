package com.forgeessentials.util.events.modules;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.moduleLauncher.ModuleContainer;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLStateEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.CommandHandlerForge;

public class FEModuleServerInitEvent extends FEModuleEvent {
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
        if (command.getCommandPerm() != null && command.getReggroup() != null)
        {
            CommandHandlerForge.doPermissionReg(command.getCommandName(), command.getCommandPerm(), command.getReggroup().getEquivalent());
        }
        event.registerServerCommand(command);
    }
}
