package com.ForgeEssentials.api.modules.event;

import net.minecraft.command.ICommand;
import net.minecraft.server.MinecraftServer;

import com.ForgeEssentials.core.moduleLauncher.ModuleContainer;

import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLStateEvent;

public class FEModuleServerInitEvent extends FEModuleEvent
{
	private FMLServerStartingEvent	event;

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

	public void registerServerCommand(ICommand command)
	{
		// TODO: any fancy module command stuff

		// continue to register the commad.
		event.registerServerCommand(command);
	}
}
