package com.forgeessentials.commons;

import java.util.List;
import java.util.UUID;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerSelector;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.util.FakePlayer;

import com.forgeessentials.api.APIRegistry;
import com.google.gson.annotations.Expose;
import com.mojang.authlib.GameProfile;

import cpw.mods.fml.common.FMLCommonHandler;

public class UserIdent {

    protected UUID uuid;

    protected String username;

    @Expose(serialize = false)
    protected EntityPlayerMP player;

    @Expose(serialize = false)
    protected GameProfile profile;

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
        if (ident == null)
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

    public UserIdent(EntityPlayerMP player)
    {
        if (player == null)
            throw new IllegalArgumentException();
        this.player = player;
        this.uuid = player.getPersistentID();
        this.username = player.getCommandSenderName();
    }

    public UserIdent(EntityPlayer player)
    {
        if (player == null)
            throw new IllegalArgumentException();
        if (player instanceof EntityPlayerMP)
            this.player = (EntityPlayerMP) player;
        this.uuid = player.getPersistentID();
        this.username = player.getCommandSenderName();
    }

    public UserIdent(UUID uuid, String username)
    {
        if (uuid == null && (username == null || username.isEmpty()))
            throw new IllegalArgumentException();
        this.uuid = uuid;
        this.username = username;
    }

    public UserIdent(String uuid, String username)
    {
        if (uuid == null && (username == null || username.isEmpty()))
            throw new IllegalArgumentException();
        this.username = username;
        if (uuid != null && !uuid.isEmpty())
        {
            this.uuid = UUID.fromString(uuid);
            this.username = username;
        }
    }

    public UserIdent(String ident, ICommandSender sender)
    {
        if (ident == null)
        {
            throw new IllegalArgumentException();
        }
        try
        {
            this.uuid = UUID.fromString(ident);
        }
        catch (IllegalArgumentException e)
        {
            this.player = getPlayerByMatchOrUsername(sender, ident);
            if (this.player == null)
                this.username = ident;
            else
            {
                this.username = player.getCommandSenderName();
                this.uuid = player.getPersistentID();
            }
        }
    }

    public UserIdent(UserIdent ident)
    {
        this.uuid = ident.uuid;
        this.player = ident.player;
        this.username = ident.username;
        this.profile = ident.profile;

        if (player == null)
        {
            if (uuid != null)
                player = getPlayerByUuid(uuid);
            else if (username != null)
                player = getPlayerByUsername(username);
            else if (profile != null)
                player = getPlayerByUuid(profile.getId());
        }

        if (profile == null && uuid != null)
        {
            profile = getGameProfileByUuid(uuid);
            if (profile != null)
                username = profile.getName();
        }

        if (uuid == null)
        {
            if (profile != null)
                uuid = profile.getId();
            else if (player != null)
                uuid = player.getPersistentID();
            else if (username != null)
                uuid = getUuidByUsername(username);
        }
    }

    /* ------------------------------------------------------------ */

    public void identifyUser()
    {
        if (player == null)
        {
            if (uuid != null)
                player = getPlayerByUuid(uuid);
            else if (username != null)
                player = getPlayerByUsername(username);
            else if (profile != null)
                player = getPlayerByUuid(profile.getId());
        }

        if (profile == null && uuid != null)
        {
            profile = getGameProfileByUuid(uuid);
            if (profile != null)
                username = profile.getName();
        }

        if (uuid == null)
        {
            if (profile != null)
                uuid = profile.getId();
            else if (player != null)
                uuid = player.getPersistentID();
            else if (username != null)
                uuid = getUuidByUsername(username);
        }
    }

    public void updateUsername()
    {
        if (profile != null)
            username = profile.getName();
        else if (player != null)
            username = player.getCommandSenderName();
        else if (uuid != null)
            username = getUsernameByUuid(uuid);
    }

    public boolean uncheckedHasUUID()
    {
        return uuid != null;
    }

    public boolean hasUsername()
    {
        identifyUser();
        return username != null;
    }

    public boolean hasUUID()
    {
        identifyUser();
        return uuid != null;
    }

    public boolean hasPlayer()
    {
        identifyUser();
        return player != null;
    }

    public boolean hasGameProfile()
    {
        identifyUser();
        return profile != null;
    }

    /* ------------------------------------------------------------ */

    public UUID getUuid()
    {
        if (uuid == null)
            identifyUser();
        return uuid;
    }

    public String getUsername()
    {
        if (username == null)
            identifyUser();
        return username;
    }

    public EntityPlayerMP getPlayer()
    {
        if (player == null)
            identifyUser();
        return player;
    }

    public GameProfile getGameProfile()
    {
        if (profile == null)
            identifyUser();
        return profile;
    }

    public String getUsernameOrUUID()
    {
        identifyUser();
        return username == null ? uuid.toString() : username;
    }

    /**
     * Returns the player's UUID, or a generated one if it is not available. Use this if you need to make sure that
     * there is always a UUID available.
     * 
     * @return
     */
    public UUID getOrGenerateUuid()
    {
        if (uuid != null)
            return uuid;
        return getNameUuid();
    }

    public UUID getNameUuid()
    {
        return UUID.nameUUIDFromBytes(username.getBytes());
    }

    /* ------------------------------------------------------------ */

    @Override
    public String toString()
    {
        identifyUser();
        return "(" + (uuid == null ? "" : uuid.toString()) + "|" + username + ")";
    }

    @Override
    public int hashCode()
    {
        return getOrGenerateUuid().hashCode();
    }

    @Override
    public boolean equals(Object other)
    {
        if (this == other)
        {
            return true;
        }
        else if (other instanceof String)
        {
            identifyUser();
            if (this.uuid != null)
            {
                try
                {
                    UUID otherUUID = UUID.fromString((String) other);
                    return this.uuid.equals(otherUUID);
                }
                catch (IllegalArgumentException e)
                {
                    // Do nothing
                }
            }
            return this.username.equals(other);
        }
        else if (other instanceof UserIdent)
        {
            UserIdent ident = (UserIdent) other;
            identifyUser();
            ident.identifyUser();
            if (uuid != null && ident.uuid != null)
                return uuid.equals(ident.uuid);
            if (username != null && ident.username != null)
                return username.equals(ident.username);
            return false;
        }
        else if (other instanceof UUID)
        {
            identifyUser();
            return other.equals(uuid);
        }
        else
        {
            return false;
        }
    }

    /* ------------------------------------------------------------ */

    public static UserIdent fromString(String string)
    {
        if (string.charAt(0) != '(' || string.charAt(string.length() - 1) != ')' || string.indexOf('|') < 0)
            throw new IllegalArgumentException("UserIdent string needs to be in the format \"(<uuid>|<username>)\"");
        String[] parts = string.substring(1, string.length() - 1).split("\\|", 2);
        return new UserIdent(UUID.fromString(parts[0]), parts[1]);
    }

    public static String getUsernameByUuid(String uuid)
    {
        return getUsernameByUuid(UUID.fromString(uuid));
    }

    public static String getUsernameByUuid(UUID uuid)
    {
        GameProfile profile = getGameProfileByUuid(uuid);
        if (profile != null)
            return profile.getName();
        for (UserIdent ident : APIRegistry.perms.getServerZone().getKnownPlayers())
            if (ident.hasUUID() && ident.getUuid().equals(uuid))
                return ident.getUsername();
        return null;
    }

    public static GameProfile getGameProfileByUuid(UUID uuid)
    {
        GameProfile profile = MinecraftServer.getServer().func_152358_ax().func_152652_a(uuid);
        return profile;
    }

    @SuppressWarnings("unchecked")
    public static EntityPlayerMP getPlayerByUuid(UUID uuid)
    {
        for (EntityPlayerMP player : (List<EntityPlayerMP>) MinecraftServer.getServer().getConfigurationManager().playerEntityList)
            if (player.getGameProfile().getId().equals(uuid))
                return player;
        return null;
    }

    @SuppressWarnings("unchecked")
    public static EntityPlayerMP getPlayerByUsername(String name)
    {
        for (EntityPlayerMP player : (List<EntityPlayerMP>) FMLCommonHandler.instance().getSidedDelegate().getServer().getConfigurationManager().playerEntityList)
            if (player.getGameProfile().getName().equals(name))
                return player;
        return null;
    }

    public static EntityPlayerMP getPlayerByMatchOrUsername(ICommandSender sender, String match)
    {
        EntityPlayerMP player = PlayerSelector.matchOnePlayer(sender, match);
        if (player != null)
            return player;
        return getPlayerByUsername(match);
    }

    public static UUID getUuidByUsername(String username)
    {
        EntityPlayerMP player = MinecraftServer.getServer().getConfigurationManager().func_152612_a(username);
        if (player != null)
            return player.getGameProfile().getId();
        for (UserIdent ident : APIRegistry.perms.getServerZone().getKnownPlayers())
            if (ident.uuid != null && username.equals(ident.getUsername()))
                return ident.getUuid();
        return null;
    }

    public boolean isFakePlayer()
    {
        identifyUser();
        return player instanceof FakePlayer;
    }

}
