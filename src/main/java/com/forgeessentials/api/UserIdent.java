package com.forgeessentials.api;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerSelector;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.fml.common.eventhandler.Event;

import com.forgeessentials.permissions.ModulePermissions;
import com.forgeessentials.util.ServerUtil;
import com.forgeessentials.util.UserIdentUtils;
import com.google.gson.annotations.Expose;
import com.mojang.authlib.GameProfile;

import javax.annotation.Nullable;

public class UserIdent
{

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
        this.username = player.getName();
        byUuid.put(uuid, this);
        byUsername.put(username.toLowerCase(), this);
    }

    private UserIdent(UUID identUuid, String identUsername, EntityPlayerMP identPlayer)
    {
        if (identUsername != null && identUsername.isEmpty())
            identUsername = null;
        player = identPlayer == null ? null : new WeakReference<EntityPlayer>(identPlayer);
        if (identPlayer != null)
        {
            uuid = identPlayer.getPersistentID();
            username = identPlayer.getName();
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

            if (uuid == null && username != null)
                uuid = UserIdentUtils.resolveMissingUUID(username);
            else if (uuid != null && username == null)
                username = UserIdentUtils.resolveMissingUsername(uuid);
        }
    }

    /* ------------------------------------------------------------ */

    public static synchronized UserIdent get(GameProfile profile)
    {
        return get(profile.getId(), profile.getName());
    } 

    public static synchronized UserIdent get(UUID uuid, String username)
    {
        if (uuid == null && (username == null || username.isEmpty()))
            throw new IllegalArgumentException();
        if (username != null && username.isEmpty())
            username = null;

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
                if (uuid != null && ident.uuid != uuid)
                {
                    ident.uuid = uuid;
                    byUuid.put(uuid, ident);
                }
                return ident;
            }

            if (username.startsWith("$NPC") || username.startsWith("[") || UUID.nameUUIDFromBytes(username.getBytes()).equals(uuid))
            {
                return new NpcUserIdent(uuid, username);
            }
            else if (username.startsWith("$"))
            {
                return new ServerUserIdent(uuid, username);
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
        if (player == null)
            throw new IllegalArgumentException();
        
        if (!(player instanceof EntityPlayerMP) || player instanceof FakePlayer) {
            return getNpc(player.getName(), ModulePermissions.fakePlayerIsSpecialBunny ? null : player.getPersistentID());
        }

        UserIdent ident = byUuid.get(player.getPersistentID());
        if (ident == null)
        {
            ident = byUsername.get(player.getName().toLowerCase());
            if (ident != null)
            {
                if (ident.uuid != player.getPersistentID())
                {
                    if (ident.uuid != null) {
                        byUuid.remove(ident.uuid);
                    }
                    ident.uuid = player.getPersistentID();
                    byUuid.put(ident.uuid, ident);
                }
            }
            else
                ident = new UserIdent((EntityPlayerMP) player);
        }
        else
        {
            String name = player.getName();
            if (name != null && !name.equals(ident.username))
            {
                byUsername.remove(ident.username);
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
            return get(UUID.fromString(uuidOrUsername));
        }
        catch (IllegalArgumentException e)
        {
            UserIdent ident = byUsername.get(uuidOrUsername.toLowerCase());
            if (ident != null)
                return ident;

            EntityPlayerMP player = sender != null ? UserIdent.getPlayerByMatchOrUsername(sender, uuidOrUsername) : //
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
            try
            {
                _uuid = UUID.fromString(uuid);
            }catch (IllegalArgumentException e)
            {
                //If UUID is invalid, lookup by username
            }

        UserIdent ident = byUuid.get(_uuid);
        if (ident == null)
            ident = byUsername.get(username);

        if (!(ident instanceof ServerUserIdent))
            ident = new ServerUserIdent(_uuid, username);

        return (ServerUserIdent) ident;
    }

    public static synchronized NpcUserIdent getNpc(String npcName) {
        return getNpc(npcName, null);
    }

    public static synchronized NpcUserIdent getNpc(String npcName, @Nullable UUID uuid)
    {
        String username = "$NPC" + (npcName == null ? "" : "_" + npcName.toUpperCase());
        UUID _uuid = uuid != null ? uuid : UUID.nameUUIDFromBytes(username.getBytes());

        UserIdent ident = byUuid.get(_uuid);
        if (ident == null)
            ident = byUsername.get(username);
        else if (ident instanceof NpcUserIdent)
        {
            if (!username.equals(ident.username))
                ident.username = username;
        }

        if (ident instanceof NpcUserIdent)
        {
            if (!_uuid.equals(ident.uuid))
            {
                byUuid.remove(ident.uuid);
                ident.uuid = uuid;
                byUuid.put(ident.uuid, ident);
            }
        }

        if (!(ident instanceof NpcUserIdent))
            ident = new NpcUserIdent(_uuid, username);

        return (NpcUserIdent) ident;
    }

    public static synchronized void login(EntityPlayerMP player)
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
        ident.player = new WeakReference<EntityPlayer>(player);
        ident.username = player.getName();
        ident.uuid = player.getPersistentID();

        if (usernameIdent != null && usernameIdent != ident)
        {
            APIRegistry.getFEEventBus().post(new UserIdentInvalidatedEvent(usernameIdent, ident));

            // Change data for already existing references to old UserIdent
            usernameIdent.player = new WeakReference<EntityPlayer>(player);
            usernameIdent.username = player.getName();

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
     * Returns the player's UUID, or a generated one if it is not available. Use this if you need to make sure that there is always a UUID available (for example for storage in
     * maps).
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
                return new GameProfile(getOrGenerateUuid(), player.getName());

                /*
                 * // Safeguard against stupid mods who set UUID to null UserIdent playerIdent = UserIdent.byUsername.get(player.getName()); if (playerIdent != this)
                 * return playerIdent.getGameProfile();
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
        return "(" + (uuid == null ? "" : uuid.toString()) + "|" + (username != null ? username : "") + ")";
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
            return username != null && this.username.equalsIgnoreCase((String) other);
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
        return configurationManager == null ? null : configurationManager.getPlayerByUsername(username);
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
        GameProfile profile = MinecraftServer.getServer().getPlayerProfileCache().getProfileByUUID(uuid);
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
