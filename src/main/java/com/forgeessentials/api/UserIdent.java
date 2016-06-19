package com.forgeessentials.api;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.util.output.LoggingHandler;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerSelector;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;

import com.forgeessentials.util.ServerUtil;
import com.google.gson.annotations.Expose;
import com.mojang.authlib.GameProfile;

import cpw.mods.fml.common.eventhandler.Event;

public class UserIdent
{

    public static UUID hexStringToUUID(String s)
    {
        if (s.length() != 32)
            throw new IllegalArgumentException();
        byte[] data = new byte[32];
        for (int i = 0; i < 32; i++)
        {
            data[i] = (byte) s.charAt(i);
        }
        return UUID.nameUUIDFromBytes(data);
    }

    public static UUID stringToUUID(String s)
    {
        if (s.length() == 32)
            return hexStringToUUID(s);
        else
            return UUID.fromString(s);

    }

    public static class ServerUserIdent extends UserIdent
    {

        private ServerUserIdent(UUID uuid, String username)
        {
            super(uuid, username, null);
        }

        @Override
        public boolean isPlayer()
        {
            return false;
        }

    }

    public static class NpcUserIdent extends UserIdent
    {

        private NpcUserIdent(UUID uuid, String username)
        {
            super(uuid, username, null);
        }

        @Override
        public boolean isPlayer()
        {
            return false;
        }

        @Override
        public boolean isNpc()
        {
            return true;
        }

    }

    /* ------------------------------------------------------------ */

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

    protected UUID uuid;

    protected String username;

    @Expose(serialize = false)
    protected int hashCode;

    @Expose(serialize = false)
    protected WeakReference<EntityPlayer> player;

    /* ------------------------------------------------------------ */

    private UserIdent(EntityPlayerMP player)
    {
        this.player = new WeakReference<EntityPlayer>(player);
        this.uuid = player.getPersistentID();
        this.username = player.getCommandSenderName();
        byUuid.put(uuid, this);
        byUsername.put(username.toLowerCase(), this);
    }

    private UserIdent(UUID identUuid, String identUsername, EntityPlayerMP identPlayer)
    {
        player = identPlayer == null ? null : new WeakReference<EntityPlayer>(identPlayer);
        if (identPlayer != null)
        {
            uuid = identPlayer.getPersistentID();
            username = identPlayer.getCommandSenderName();
            byUuid.put(uuid, this);
            byUsername.put(username.toLowerCase(), this);
        }
        else
        {
            uuid = identUuid;
            username = identUsername;
            if (uuid != null)
                byUuid.put(this.uuid, this);
            if (identUsername != null && identUsername.charAt(0) != '@')
                byUsername.put(identUsername.toLowerCase(), this);
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
            {
                if (uuid != null && ident.uuid == null)
                    ident.uuid = uuid;
                return ident;
            }
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

    public static synchronized UserIdent getFromUuid(String uuid)
    {
        if (uuid == null)
            return null;
        try
        {
            return get(UUID.fromString(uuid));
        }
        catch (IllegalArgumentException e)
        {
            return null;
        }
    }

    public static synchronized UserIdent get(EntityPlayer player)
    {
        return player instanceof EntityPlayerMP ? get((EntityPlayerMP) player) : null;
    }

    public static synchronized UserIdent get(EntityPlayerMP player)
    {
        if (player == null)
            throw new IllegalArgumentException();

        UserIdent ident = byUuid.get(player.getPersistentID());
        if (ident == null)
        {
            ident = byUsername.get(player.getCommandSenderName().toLowerCase());
            if (ident != null)
            {
                ident.uuid = player.getPersistentID();
                byUuid.put(ident.uuid, ident);
            }
            else
                ident = new UserIdent(player);
        }
        else
        {
            String name = player.getCommandSenderName();

            String newname = name + "_" + ident.uuid.hashCode();
            if (name != null && !name.equals(ident.username) && !newname.equals(ident.username))
            {
                if (byUsername.containsKey(name.toLowerCase()))
                {
                    try
                    {
                        throw new Throwable();
                    }
                    catch (Throwable t)
                    {
                        LoggingHandler.felog.fatal("Duplicate Player Error:\nA fake player, '" + name + "' is trying to change its name to one that already exists in the database.\nThis is not a forge essentials error! It is being caused by another mod.\nI have renamed this player to '"
                                + newname + "' to prevent forge essentials from failing to start up, but this is a mod error cause you can only have one player name per uuid.\nI have printed the stack trace to give you some more details on what mod is misbehaving.");
                        t.printStackTrace();
                    }

                    name = newname;
                }
                byUsername.remove(ident.username != null ? ident.username.toLowerCase() : null);
                ident.username = name;
                byUsername.put(ident.username.toLowerCase(), ident);
            }
        }
        if (ident.player == null || ident.player.get() != player)
            ident.player = new WeakReference<EntityPlayer>(player);
        return ident;
    }

    public static synchronized UserIdent get(String uuidOrUsername, ICommandSender sender, boolean mustExist)
    {
        if (uuidOrUsername == null)
            throw new IllegalArgumentException();
        try
        {
            return get(stringToUUID(uuidOrUsername));
        }
        catch (IllegalArgumentException e)
        {
            UserIdent ident = byUsername.get(uuidOrUsername.toLowerCase());
            if (ident != null)
                return ident;

            EntityPlayerMP player = sender != null ? UserIdent.getPlayerByMatchOrUsername(sender, uuidOrUsername)
                    : //
                    UserIdent.getPlayerByUsername(uuidOrUsername);
            if (player != null)
                return get(player);

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

    public static synchronized UserIdent getVirtualPlayer(String username)
    {
        return get(UUID.nameUUIDFromBytes(username.getBytes()), username);
    }

    public static synchronized ServerUserIdent getServer(String uuid, String username)
    {
    	
        UUID _uuid = null;
        if (uuid != null)
        	_uuid = UUID.fromString(uuid);

        UserIdent ident = byUuid.get(_uuid);
        if (ident == null)
            ident = byUsername.get(username);

        if (ident == null || !(ident instanceof ServerUserIdent))
            ident = new ServerUserIdent(_uuid, username);

        return (ServerUserIdent) ident;
    }

    public static synchronized NpcUserIdent getNpc(String npcName)
    {
        String username = "$NPC" + (npcName == null ? "" : "_" + npcName.toUpperCase());
        UUID _uuid = UUID.nameUUIDFromBytes(username.getBytes());

        UserIdent ident = byUuid.get(_uuid);
        if (ident != null)
            ident = byUsername.get(username);

        if (ident == null || !(ident instanceof NpcUserIdent))
            ident = new NpcUserIdent(_uuid, username);

        return (NpcUserIdent) ident;
    }

    public static synchronized void login(EntityPlayerMP player)
    {
        UserIdent ident = byUuid.get(player.getPersistentID());
        UserIdent usernameIdent = byUsername.get(player.getCommandSenderName().toLowerCase());

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
        ident.player = new WeakReference<EntityPlayer>(player);
        ident.username = player.getCommandSenderName();
        ident.uuid = player.getPersistentID();

        if (usernameIdent != null && usernameIdent != ident)
        {
            APIRegistry.getFEEventBus().post(new UserIdentInvalidatedEvent(usernameIdent, ident));

            // Change data for already existing references to old UserIdent
            usernameIdent.player = new WeakReference<EntityPlayer>(player);
            usernameIdent.username = player.getCommandSenderName();

            // Replace entry in username map by the one from uuid map
            byUsername.remove(usernameIdent.username.toLowerCase());
            byUsername.put(ident.username.toLowerCase(), ident);
        }
    }

    public static synchronized void logout(EntityPlayerMP player)
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
        EntityPlayer player = getPlayer();
        if (player == null || player instanceof FakePlayer)
            return false;
        return true;
        // return ServerUtil.getPlayerList().contains(player);
    }

    public boolean isFakePlayer()
    {
        return getPlayer() instanceof FakePlayer;
    }

    /**
     * Returns true for a normal UserIdent. Returns false for NPC or server UserIdents.
     * 
     * @return
     */
    public boolean isPlayer()
    {
        return true;
    }

    /**
     * Returns false for a normal UserIdent. Returns true for NPC.
     * 
     * @return
     */
    public boolean isNpc()
    {
        return false;
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

    public void refreshPlayer()
    {
        EntityPlayerMP player = UserIdent.getPlayerByUuid(uuid);
        this.player = player == null ? null : new WeakReference<EntityPlayer>(player);
    }

    public EntityPlayer getPlayer()
    {
        return player == null ? null : player.get();
    }

    public EntityPlayerMP getPlayerMP()
    {
        return player == null ? null : (EntityPlayerMP) player.get();
    }

    public EntityPlayerMP getFakePlayer()
    {
        EntityPlayerMP player = getPlayerMP();
        if (player != null)
            return player;
        return FakePlayerFactory.get(MinecraftServer.getServer().worldServers[0], getGameProfile());
    }

    public EntityPlayerMP getFakePlayer(WorldServer world)
    {
        EntityPlayerMP player = getPlayerMP();
        if (player != null)
            return player;
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
        EntityPlayer player = getPlayer();
        if (player != null)
        {
            if (!player.getGameProfile().isComplete())
            {
                return new GameProfile(getOrGenerateUuid(), player.getCommandSenderName());

                /*
                 * // Safeguard against stupid mods who set UUID to null UserIdent playerIdent =
                 * UserIdent.byUsername.get(player.getCommandSenderName()); if (playerIdent != this) return
                 * playerIdent.getGameProfile();
                 */
            }
            else
            {
                return player.getGameProfile();
            }
        }
        return new GameProfile(getOrGenerateUuid(), username);
    }

    /* ------------------------------------------------------------ */

    public static UserIdent fromString(String string)
    {
        if (string.charAt(0) != '(' || string.charAt(string.length() - 1) != ')' || string.indexOf('|') < 0)
            throw new IllegalArgumentException("UserIdent string needs to be in the format \"(<uuid>|<username>)\"");
        String[] parts = string.substring(1, string.length() - 1).split("\\|", 2);
        UUID id = null;
        String str = parts[1] != null && !parts[1].isEmpty() ? parts[1] : null;
        try
        {
            id = UUID.fromString(parts[0]);
        }
        catch (IllegalArgumentException e)
        {}
        return get(id,str);
    }

    public String toSerializeString()
    {
        return "(" + (uuid == null ? "" : uuid.toString()) + "|" + (username == null ? "" : username) + ")";
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
        else if (other instanceof EntityPlayerMP)
        {
            return ((EntityPlayerMP) other).getPersistentID().equals(uuid);
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
        MinecraftServer mc = MinecraftServer.getServer();
        if (mc == null)
            return null;
        ServerConfigurationManager configurationManager = mc.getConfigurationManager();
        return configurationManager == null ? null : configurationManager.func_152612_a(username);
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
        GameProfile profile = MinecraftServer.getServer().func_152358_ax().func_152652_a(uuid);
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
