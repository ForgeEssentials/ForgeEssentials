package com.forgeessentials.util;

import java.util.UUID;

import org.apache.commons.compress.archivers.dump.InvalidFormatException;

import com.forgeessentials.commands.util.Kit;
import com.forgeessentials.data.api.IReconstructData;
import com.forgeessentials.data.api.SaveableObject;
import com.forgeessentials.data.api.SaveableObject.Reconstructor;
import com.forgeessentials.data.api.SaveableObject.SaveableField;
import com.mojang.authlib.GameProfile;

import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.command.PlayerSelector;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

@SaveableObject(SaveInline = true)
public class UserIdent {

	@SaveableField
	private UUID uuid;

	@SaveableField
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

	public UserIdent(String uuid, String username)
	{
		this.username = username;
		if (uuid != null)
		{
			this.uuid = UUID.fromString(uuid);
			this.username = username;
		}
	}

	@Reconstructor
	private static UserIdent reconstruct(IReconstructData tag)
	{
		return new UserIdent((UUID) tag.getFieldValue("uuid"), (String) tag.getFieldValue("username"));
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
		if (string.charAt(0) != '(' || string.charAt(string.length() - 1) != ')' || string.indexOf('|') < 0)
		{
			throw new IllegalArgumentException("UserIdent string needs to be in the format \"(<uuid>|<username>)\"");
		}
		String[] parts = string.substring(1, string.length() - 2).split("\\|", 1);
		return new UserIdent(UUID.fromString(parts[0]), parts[1]);
	}

	public static String getUsernameByUuid(String uuid)
	{
		return getUsernameByUuid(UUID.fromString(uuid));
	}

	public static String getUsernameByUuid(UUID uuid)
	{
		GameProfile profile = MinecraftServer.getServer().func_152358_ax().func_152652_a(uuid);
		if (profile == null)
			return null;
		return MinecraftServer.getServer().func_152358_ax().func_152652_a(uuid).getName();
	}

	public static UUID getUuidByUsername(String username)
	{
		EntityPlayerMP player = MinecraftServer.getServer().getConfigurationManager().func_152612_a(username);
		if (player == null)
			return null;
		return player.getGameProfile().getId();
	}

}
