package com.forgeessentials.util.events.modules;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.minecraft.server.MinecraftServer;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.APIRegistry.ForgeEssentialsRegistrar.PermRegister;
import com.forgeessentials.api.permissions.IPermRegisterEvent;
import com.forgeessentials.api.permissions.RegGroup;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.moduleLauncher.ModuleContainer;

import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLStateEvent;

public class FEModuleServerInitEvent extends FEModuleEvent
{
	private FMLServerStartingEvent	event;
	private static Map<String, RegGroup> permList = new HashMap<String, RegGroup>();

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
		this.permList.put(command.getCommandPerm(), command.getReggroup());
		event.registerServerCommand(command);
	}
	
	@PermRegister
	public void registerPermissions(IPermRegisterEvent e){
		Iterator it = this.permList.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pairs = (Map.Entry)it.next();
	        e.registerPermissionLevel((String)pairs.getKey(), (RegGroup)pairs.getValue());
	        it.remove(); // avoids a ConcurrentModificationException
	    }
	}
}
