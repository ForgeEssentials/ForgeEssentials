package com.forgeessentials.util;

import java.util.UUID;

import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.command.PlayerSelector;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class UserIdent {

	private UUID uuid;

	private String username;

	public UserIdent(UUID uuid)
	{
		if (uuid == null)
		{
			throw new IllegalArgumentException();
		}
		this.uuid = uuid;
	}

	public UserIdent(String ident)
	{
		if (username == null)
		{
			throw new IllegalArgumentException();
		}
		try
		{
			this.uuid = UUID.fromString(ident);
		}
		catch (IllegalArgumentException e)
		{
			this.username = ident;
		}
	}

	public UserIdent(EntityPlayer player)
	{
		if (player == null)
			throw new IllegalArgumentException();
		this.uuid = player.getPersistentID();
		this.username = player.getCommandSenderName();
	}

	public UserIdent(UUID uuid, String username)
	{
		if (uuid == null && username == null)
			throw new IllegalArgumentException();
		this.uuid = uuid;
		this.username = username;
	}

	public void identifyUser()
	{
		if (uuid == null)
		{
			uuid = getUuidByUsername(username);
		}
		else if (username == null)
		{
			username = getUsernameByUuid(uuid);
		}
	}

	public void updateUsername()
	{
		username = getUsernameByUuid(uuid);
	}

	public UUID getUuid()
	{
		identifyUser();
		return uuid;
	}

	public String getUsername()
	{
		identifyUser();
		return username;
	}

	public boolean wasValidUUID()
	{
		return uuid != null;
	}

	public boolean isValidUUID()
	{
		identifyUser();
		return uuid != null;
	}

	@Override
	public String toString()
	{
		identifyUser();
		return "(" + (uuid == null ? "" : uuid.toString()) + "|" + username + ")";
	}

	@Override
	public int hashCode()
	{
		identifyUser();
		if (uuid == null)
		{
			// throw new PlayerNotFoundException();
			return username.hashCode();
		}
		else
		{
			return uuid.hashCode();
		}
	}

	public static UserIdent fromString(String string)
	{
		return new UserIdent(string);
	}

	public static String getUsernameByUuid(String uuid)
	{
		return MinecraftServer.getServer().func_152358_ax().func_152652_a(UUID.fromString(uuid)).getName();
	}

	public static String getUsernameByUuid(UUID uuid)
	{
		return MinecraftServer.getServer().func_152358_ax().func_152652_a(uuid).getName();
	}

	public static UUID getUuidByUsername(String username)
	{
		EntityPlayerMP player = MinecraftServer.getServer().getConfigurationManager().func_152612_a(username);
		if (player == null)
		{
			return null;
		}
		return player.getGameProfile().getId();
	}

}
