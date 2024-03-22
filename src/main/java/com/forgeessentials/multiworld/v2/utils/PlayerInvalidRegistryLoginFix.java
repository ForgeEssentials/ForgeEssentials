package com.forgeessentials.multiworld.v2.utils;

import com.forgeessentials.commons.selections.WarpPoint;
import com.forgeessentials.core.misc.TeleportHelper;
import com.forgeessentials.multiworld.v2.ModuleMultiworldV2;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.events.ServerEventHandler;
import com.forgeessentials.util.output.ChatOutputHandler;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fmllegacy.server.ServerLifecycleHooks;

public class PlayerInvalidRegistryLoginFix extends ServerEventHandler {
	@SubscribeEvent(priority = EventPriority.HIGH)
	public void playerLogin(PlayerLoggedInEvent event) {
		PlayerInfo player = PlayerInfo.get(event.getPlayer().getUUID());
		if (player.getActualLogOutPoint() != null) {
			TeleportHelper.doTeleport(event.getPlayer(), player.getActualLogOutPoint());
			player.setActualLogOutPoint(null);
			ChatOutputHandler.chatWarning(event.getPlayer(), "You logged into a dynamic dimension using a non-vanilla dimensionType, your game will think you are in minecraft:overworld!"
					+ " This could cause issues with client mods thinking you are in the overworld, but according the server you are not. Please refrain from using multiworlds in the configuration!");
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public void playerLoggedOut(PlayerLoggedOutEvent event) {
		if (event.getPlayer() instanceof ServerPlayer
				&& ModuleMultiworldV2.isMultiWorld(((ServerPlayer) event.getPlayer()).getLevel())) {
			if (!ModuleMultiworldV2.getMultiworldManager().getProviderHandler().getVanillaDimensionTypes()
					.containsValue(((ServerPlayer) event.getPlayer()).getLevel().dimensionType())) {
				System.out.println("!vanillaDimensionTypes.contains(dimensionType())");
				PlayerInfo player = PlayerInfo.get(event.getPlayer().getUUID());
				player.setActualLogOutPoint(new WarpPoint(event.getPlayer()));
				((ServerPlayer) event.getPlayer()).teleportTo(
						ServerLifecycleHooks.getCurrentServer().getLevel(ServerLevel.OVERWORLD), 0, 1000, 0, 0, 0);
			}
		}
	}
}
