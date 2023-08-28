package com.forgeessentials.multiworld.v2;

import java.util.List;

import com.forgeessentials.api.NamedWorldHandler;
import com.forgeessentials.util.events.ServerEventHandler;

import net.minecraft.world.server.ServerWorld;

public class MultiworldManager extends ServerEventHandler implements NamedWorldHandler {

	@Override
	public ServerWorld getWorld(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getWorldName(String dimId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getWorldNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getShortWorldNames() {
		// TODO Auto-generated method stub
		return null;
	}

	public void load() {
		// TODO Auto-generated method stub
		
	}

	public void serverStopped() {
		
	}
}
