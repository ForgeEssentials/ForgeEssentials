package com.forgeessentials.api;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerSelector;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.fml.common.eventhandler.Event;

import com.forgeessentials.util.ServerUtil;
import com.google.gson.annotations.Expose;
import com.mojang.authlib.GameProfile;

public class UserIdent
{

    public static class UserIdentInvalidatedEvent extends Event
    {

        public final UserIdent oldValue;

        public final UserIdent newValue;

        public UserIdentInvalidatedEvent(UserIdent oldValue, UserIdent newValue)
        {
            this.oldValue = oldValue;
            this.newValue = newValue;
        }

    }

    /* ------------------------------------------------------------ */

    private static final Map<UUID, UserIdent> byUuid = new HashMap<>();

    private static final Map<String, UserIdent> byUsername = new HashMap<>();

    /* ------------------------------------------------------------ */

    private UUID uuid;

    private String username;

    @Expose(serialize = false)
    private int hashCode;

    @Expose(serialize = false)
    protected EntityPlayer player;

    @Expose(serialize = false)
    protected GameProfile gameProfile;

    /* ------------------------------------------------------------ */

    private UserIdent(EntityPlayer player)
    {
        this.player = player;
        this.uuid = player.getPersistentID();
        this.username = player.getName();
        byUuid.put(uuid, this);
        byUsername.put(username.toLowerCase(), this);
    }

    private UserIdent(UUID uuid, String username, EntityPlayer player)
    {
        this.player = player;
        if (player != null)
        {
            this.uuid = player.getPersistentID();
            this.username = player.getName();
            byUuid.put(uuid, this);
            byUsername.put(username.toLowerCase(), this);
        }
        else
        {
            this.uuid = uuid;
            this.username = username;
            if (uuid != null)
                byUuid.put(uuid, this);
            if (username != null)
                byUsername.put(username.toLowerCase(), this);
        }
    }

    /* ------------------------------------------------------------ */

    public static synchronized UserIdent get(UUID uuid, String username)
    {
        if (uuid == null && (username == null || username.isEmpty()))
            throw new IllegalArgumentException();

        if (uuid != null)
        {
            UserIdent ident = byUuid.get(uuid);
            if (ident != null)
                return ident;
        }

        if (username != null)
        {
            UserIdent ident = byUsername.get(username.toLowerCase());
            if (ident != null)
                return ident;
        }

        return new UserIdent(uuid, username, UserIdent.getPlayerByUuid(uuid));
    }

    public static synchronized UserIdent get(String uuid, String username)
    {
        return get(uuid != null && !uuid.isEmpty() ? UUID.fromString(uuid) : null, username);
    }

    public static synchronized UserIdent get(UUID uuid)
    {
        if (uuid == null)
            throw new IllegalArgumentException();

        UserIdent ident = byUuid.get(uuid);
        if (ident != null)
            return ident;

        return new UserIdent(uuid, null, UserIdent.getPlayerByUuid(uuid));
    }

    public static synchronized UserIdent get(EntityPlayer player)
    {
        if (player == null)
            throw new IllegalArgumentException();

        UserIdent ident = byUuid.get(player.getPersistentID());
        if (ident != null)
            return ident;

        ident = byUsername.get(player.getName().toLowerCase());
        if (ident != null)
            return ident;

        return new UserIdent(player);
    }

    public static synchronized UserIdent get(String uuidOrUsername, ICommandSender sender, boolean mustExist)
    {
        if (uuidOrUsername == null)
            throw new IllegalArgumentException();
        try
        {
            return get(UUID.fromString(uuidOrUsername));
        }
        catch (IllegalArgumentException e)
        {
            UserIdent ident = byUsername.get(uuidOrUsername.toLowerCase());
            if (ident != null)
                return ident;

            EntityPlayer player = sender != null ? UserIdent.getPlayerByMatchOrUsername(sender, uuidOrUsername) : //
                    UserIdent.getPlayerByUsername(uuidOrUsername);
            if (player != null)
                return get(player.getPersistentID());

            return mustExist ? null : new UserIdent(null, uuidOrUsername, null);
        }
    }

    public static synchronized UserIdent get(String uuidOrUsername, ICommandSender sender)
    {
        return get(uuidOrUsername, sender, false);
    }

    public static synchronized UserIdent get(String uuidOrUsername, boolean mustExist)
    {
        return get(uuidOrUsername, (ICommandSender) null, mustExist);
    }

    public static synchronized UserIdent get(String uuidOrUsername)
    {
        return get(uuidOrUsername, false);
    }

    public static synchronized void login(EntityPlayer player)
    {
        UserIdent ident = byUuid.get(player.getPersistentID());
        UserIdent usernameIdent = byUsername.get(player.getName().toLowerCase());

        if (ident == null)
        {
            if (usernameIdent == null)
                ident = new UserIdent(player);
            else
            {
                ident = usernameIdent;
                byUuid.put(player.getPersistentID(), ident);
            }
        }
        ident.player = player;
        ident.username = player.getName();
        ident.uuid = player.getPersistentID();
        ident.gameProfile = null;

        if (usernameIdent != null && usernameIdent != ident)
        {
            APIRegistry.getFEEventBus().post(new UserIdentInvalidatedEvent(usernameIdent, ident));

            // Change data for already existing references to old UserIdent
            usernameIdent.player = player;
            usernameIdent.username = player.getName();

            // Replace entry in username map by the one from uuid map
            byUsername.remove(usernameIdent.username.toLowerCase());
            byUsername.put(ident.username.toLowerCase(), ident);
        }
    }

    public static synchronized void logout(EntityPlayer player)
    {
        UserIdent ident = UserIdent.get(player);
        ident.player = null;
    }

    /* ------------------------------------------------------------ */

    public boolean hasUsername()
    {
        return username != null;
    }

    public boolean hasUuid()
    {
        return uuid != null;
    }

    public boolean hasPlayer()
    {
        return player != null;
    }

    public boolean isFakePlayer()
    {
        return player instanceof FakePlayer;
    }

    /* ------------------------------------------------------------ */

    public UUID getUuid()
    {
        return uuid;
    }

    public String getUsername()
    {
        return username;
    }

    public String getUsernameOrUuid()
    {
        return username == null ? uuid.toString() : username;
    }

    public EntityPlayer getPlayer()
    {
        return player;
    }

    public EntityPlayerMP getPlayerMP()
    {
        return (EntityPlayerMP) player;
    }

    public EntityPlayer getFakePlayer()
    {
        if (player != null)
            return player;
        return FakePlayerFactory.get(MinecraftServer.getServer().worldServers[0], getGameProfile());
    }

    public EntityPlayerMP getFakePlayer(WorldServer world)
    {
        if (player != null)
            return (EntityPlayerMP) player;
        return FakePlayerFactory.get(world, getGameProfile());
    }

    /**
     * Returns the player's UUID, or a generated one if it is not available. Use this if you need to make sure that
     * there is always a UUID available (for example for storage in maps).
     * 
     * @return
     */
    public UUID getOrGenerateUuid()
    {
        if (uuid != null)
            return uuid;
        return getUsernameUuid();
    }

    /**
     * Returns a different UUID generated by the username
     * 
     * @return
     */
    public UUID getUsernameUuid()
    {
        return UUID.nameUUIDFromBytes(username.getBytes());
    }

    public GameProfile getGameProfile()
    {
        if (gameProfile == null)
        {
            if (player != null)
                gameProfile = player.getGameProfile();
            else
                gameProfile = new GameProfile(getOrGenerateUuid(), username);
        }
        return gameProfile;
    }

    /* ------------------------------------------------------------ */

    public static UserIdent fromString(String string)
    {
        if (string.charAt(0) != '(' || string.charAt(string.length() - 1) != ')' || string.indexOf('|') < 0)
            throw new IllegalArgumentException("UserIdent string needs to be in the format \"(<uuid>|<username>)\"");
        String[] parts = string.substring(1, string.length() - 1).split("\\|", 2);
        try
        {
            return get(UUID.fromString(parts[0]), parts[1]);
        }
        catch (IllegalArgumentException e)
        {
            return get((UUID) null, parts[1]);
        }
    }

    public String toSerializeString()
    {
        return "(" + (uuid == null ? "" : uuid.toString()) + "|" + username + ")";
    }

    @Override
    public String toString()
    {
        return toSerializeString();
    }

    @Override
    public int hashCode()
    {
        if (hashCode != 0)
            return hashCode;
        return hashCode = getOrGenerateUuid().hashCode();
    }

    @Override
    public boolean equals(Object other)
    {
        if (this == other)
        {
            return true;
        }
        else if (other instanceof UserIdent)
        {
            if (this == other)
                return true;
            // It might happen, that one UserIdent was previously initialized by username and another one by UUID, but
            // after the player in question logged in, they still become equal.
            UserIdent ident = (UserIdent) other;
            if (uuid != null && ident.uuid != null)
                return uuid.equals(ident.uuid);
            if (username != null && ident.username != null)
                return username.equalsIgnoreCase(ident.username);
            return false;
        }
        else if (other instanceof String)
        {
            if (this.uuid != null)
            {
                try
                {
                    return this.uuid.equals(UUID.fromString((String) other));
                }
                catch (IllegalArgumentException e)
                {
                    // The string was a username and not a UUID
                }
            }
            return username == null ? false : this.username.equalsIgnoreCase((String) other);
        }
        else if (other instanceof UUID)
        {
            return other.equals(uuid);
        }
        else if (other instanceof EntityPlayer)
        {
            return ((EntityPlayer) other).getPersistentID().equals(uuid);
        }
        else
        {
            return false;
        }
    }

    /* ------------------------------------------------------------ */

    public boolean checkPermission(String permissionNode)
    {
        return APIRegistry.perms.checkUserPermission(this, permissionNode);
    }

    public String getPermissionProperty(String permissionNode)
    {
        return APIRegistry.perms.getUserPermissionProperty(this, permissionNode);
    }

    /* ------------------------------------------------------------ */

    public static EntityPlayerMP getPlayerByUsername(String username)
    {
        return MinecraftServer.getServer().getConfigurationManager().getPlayerByUsername(username);
    }

    public static EntityPlayerMP getPlayerByMatchOrUsername(ICommandSender sender, String match)
    {
        EntityPlayerMP player = PlayerSelector.matchOnePlayer(sender, match);
        if (player != null)
            return player;
        return getPlayerByUsername(match);
    }

    public static EntityPlayerMP getPlayerByUuid(UUID uuid)
    {
        for (EntityPlayerMP player : ServerUtil.getPlayerList())
            if (player.getPersistentID().equals(uuid))
                return player;
        return null;
    }

    public static GameProfile getGameProfileByUuid(UUID uuid)
    {
        GameProfile profile = MinecraftServer.getServer().getPlayerProfileCache().func_152652_a(uuid);
        return profile;
    }

    public static String join(Iterable<UserIdent> users, String glue)
    {
        StringBuilder sb = new StringBuilder();
        Iterator<UserIdent> it = users.iterator();
        if (it.hasNext())
        {
            while (true)
            {
                UserIdent next = it.next();
                sb.append(next == null ? "server" : next.getUsernameOrUuid());
                if (it.hasNext())
                    sb.append(glue);
                else
                    break;
            }
        }
        return sb.toString();
    }

    public static String join(Iterable<UserIdent> users, String glue, String lastGlue)
    {
        StringBuilder sb = new StringBuilder();
        Iterator<UserIdent> it = users.iterator();
        if (it.hasNext())
        {
            UserIdent next = it.next();
            while (true)
            {
                sb.append(next == null ? "server" : next.getUsernameOrUuid());
                if (it.hasNext())
                {
                    next = it.next();
                    if (it.hasNext())
                        sb.append(glue);
                    else
                        sb.append(lastGlue);
                }
                else
                    break;
            }
        }
        return sb.toString();
    }

}
