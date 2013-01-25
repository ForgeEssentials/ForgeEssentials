package com.ForgeEssentials.WorldControl.weintegration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet3Chat;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

import com.ForgeEssentials.core.moduleLauncher.event.FEModuleServerInitEvent;
import com.sk89q.worldedit.LocalEntity;
import com.sk89q.worldedit.LocalPlayer;
import com.sk89q.worldedit.LocalWorld;
import com.sk89q.worldedit.WorldEdit;

import cpw.mods.fml.common.network.IChatListener;

public class WEIntegration implements IChatListener {
	
	public static WorldEdit instance;
	public static MinecraftServer server;

	protected WorldEdit we;
	private LocalConfig config;
	public static FEServerInterface serverInterface;

	protected List<String> whitelist = new ArrayList<String>();
	private Map<EntityPlayer, LocalPlayer> players = new WeakHashMap<EntityPlayer, LocalPlayer>();
	private static Map<World, LocalWorld> worlds = new WeakHashMap<World, LocalWorld>();
	private static Map<Entity, LocalEntity> entities = new WeakHashMap<Entity, LocalEntity>();

	public void serverStarting(FEModuleServerInitEvent e) {
		server = e.getServer();
		

		try {
			we = new com.sk89q.worldedit.WorldEdit(new FEServerInterface(), config);
		} catch (Throwable e1) 
		{}
		}

	@Override
	public Packet3Chat serverChat(NetHandler handler, Packet3Chat message) {
		if (message.message.startsWith("//")) {
			we.handleCommand(getPlayer(handler.getPlayer()), message.message.split(" "));
			return new Packet3Chat("");
		}

		return message;
	}

	@Override
	public Packet3Chat clientChat(NetHandler handler, Packet3Chat message) {
		return message;
	}

	protected LocalPlayer getPlayer(EntityPlayer player) {
		if (players.containsKey(player)) {
			return players.get(player);
		} else {
			LocalPlayer ret = new FELocalPlayer(player);
			players.put(player, ret);
			return ret;
		}
	}

	protected static LocalWorld getWorld(World world) {
		if (worlds.containsKey(world)) {
			return worlds.get(world);
		} else {
			LocalWorld ret = new FELocalWorld(world);
			worlds.put(world, ret);
			return ret;
		}
	}

	protected static LocalEntity getEntity(Entity entity) {
		if (entities.containsKey(entity)) {
			return entities.get(entity);
		} else {
			LocalEntity ret = new FELocalEntity(entity);
			entities.put(entity, ret);
			return ret;
		}
	}
}