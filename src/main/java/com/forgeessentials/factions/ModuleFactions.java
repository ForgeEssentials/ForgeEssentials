package com.forgeessentials.factions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.permission.PermissionLevel;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.api.permissions.GroupEntry;
import com.forgeessentials.api.permissions.PermissionEvent;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.misc.FECommandManager;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.core.moduleLauncher.config.ConfigLoaderBase;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStopEvent;




@FEModule(name = "factions", parentMod = ForgeEssentials.class, canDisable = true)
public class ModuleFactions extends ConfigLoaderBase
{

    public static final String RANK_OWNER = "owner";
    public static final String RANK_OP = "op";

    public static final String GROUP_PREFIX = "faction_";
    public static final String RANK_PREFIX = "faction_rank_";
    public static final String GROUP_OWNER = RANK_PREFIX + RANK_OWNER;
    public static final String GROUP_OP = RANK_PREFIX + RANK_OP;

    public static final String PERM = "fe.faction";
    public static final String PERM_LIST = PERM + ".list";
    public static final String PERM_CREATE = PERM + ".create";
    public static final String PERM_DELETE = PERM + ".delete";
    public static final String PERM_JOIN = PERM + ".join";
    public static final String PERM_JOIN_ANY = PERM_JOIN + ".any";
    public static final String PERM_LEAVE = PERM + ".leave";
    public static final String PERM_INVITE = PERM + ".invite";
    public static final String PERM_ALLY = PERM + ".ally";
    public static final String PERM_MEMBERS = PERM + ".members";
    public static final String PERM_MEMBERS_ADD = PERM_MEMBERS + ".add";
    public static final String PERM_MEMBERS_KICK = PERM_MEMBERS + ".kick";
    public static final String PERM_FF = PERM + ".friendlyfire";
    public static final String PERM_BONUS = PERM + ".bonus";
    public static final String PERM_ADMIN = PERM + ".admin";

    public static final String PERM_DATA = PERM + ".data";
    public static final String PERM_DATA_NAME = PERM_DATA + ".name";
    public static final String PERM_DATA_LOCKED = PERM_DATA + ".locked";

    @FEModule.Instance
    protected static ModuleFactions instance;

    public static ModuleFactions getInstance()
    {
        return instance;
    }

    /* ------------------------------------------------------------ */

    public ModuleFactions()
    {
        MinecraftForge.EVENT_BUS.register(this);
        APIRegistry.FE_EVENTBUS.register(this);
    }

    @SubscribeEvent
    public void load(FEModuleInitEvent event)
    {
        FECommandManager.registerCommand(new CommandFaction());

        APIRegistry.perms.registerPermissionDescription(PERM_DATA_NAME,
                "Faction name (if this permission is set and the group name starts with \"faction_\", a group is considered a faction)");
        APIRegistry.perms.registerPermission(PERM_DATA_LOCKED, PermissionLevel.FALSE, "Locks a faction so players can only join on invitation");

        APIRegistry.perms.registerPermissionDescription(PERM, "Faction permissions");
        APIRegistry.perms.registerPermission(PERM_LIST, PermissionLevel.TRUE, "List existing factions");
        APIRegistry.perms.registerPermission(PERM_CREATE, PermissionLevel.TRUE, "Allows creating factions");
        APIRegistry.perms.registerPermission(PERM_DELETE, PermissionLevel.OP, "Allows to delete a faction");
        APIRegistry.perms.registerPermission(PERM_JOIN, PermissionLevel.TRUE, "Allows joining factions");
        APIRegistry.perms.registerPermission(PERM_JOIN_ANY, PermissionLevel.OP, "Allows joining even locked factions");
        APIRegistry.perms.registerPermission(PERM_LEAVE, PermissionLevel.TRUE, "Allows to leave factions");
        APIRegistry.perms.registerPermission(PERM_INVITE, PermissionLevel.TRUE, "Allows inviting other players to a faction");
        APIRegistry.perms.registerPermission(PERM_ALLY, PermissionLevel.OP, "Allows controlling faction allies");
        APIRegistry.perms.registerPermission(PERM_MEMBERS + ".*", PermissionLevel.OP, "Control members");
        APIRegistry.perms.registerPermission(PERM_FF, PermissionLevel.OP, "Allows controlling friendly fire setting");
        APIRegistry.perms.registerPermission(PERM_BONUS, PermissionLevel.OP, "Allows controlling faction bonuses");

        APIRegistry.perms.setGroupPermission(GROUP_OWNER, PERM_DELETE, true);
        APIRegistry.perms.setGroupPermission(GROUP_OWNER, PERM_LEAVE, false);

        APIRegistry.perms.setGroupPermission(GROUP_OWNER, PERM_MEMBERS + ".*", true);
        APIRegistry.perms.setGroupPermission(GROUP_OWNER, PERM_INVITE, true);
        APIRegistry.perms.setGroupPermission(GROUP_OWNER, PERM_ALLY, true);
        APIRegistry.perms.setGroupPermission(GROUP_OWNER, PERM_FF, true);
        APIRegistry.perms.setGroupPermissionProperty(GROUP_OWNER, FEPermissions.GROUP_PARENTS, GROUP_OP);

        APIRegistry.perms.setGroupPermission(GROUP_OP, PERM_MEMBERS + ".*", true);
        APIRegistry.perms.setGroupPermission(GROUP_OP, PERM_INVITE, true);
        APIRegistry.perms.setGroupPermission(GROUP_OP, PERM_ALLY, true);
        APIRegistry.perms.setGroupPermission(GROUP_OP, PERM_FF, true);
    }

    @SubscribeEvent
    public void afterPermissionLoadEvent(PermissionEvent.AfterLoad event)
    {
        event.serverZone.setGroupPermissionProperty(GROUP_OWNER, FEPermissions.GROUP_PRIORITY, Integer.toString(FEPermissions.GROUP_PRIORITY_DEFAULT - 2));
        event.serverZone.setGroupPermissionProperty(GROUP_OP, FEPermissions.GROUP_PRIORITY, Integer.toString(FEPermissions.GROUP_PRIORITY_DEFAULT - 4));
    }

    @SubscribeEvent
    public void serverStarting(FEModuleServerInitEvent event)
    {
    }

    @SubscribeEvent
    public void serverStopping(FEModuleServerStopEvent event)
    {
    }

    @Override
    public void load(Configuration config, boolean isReload)
    {
        // localhostOnly = config.get(CONFIG_CAT, "localhostOnly", true, "Allow connections from the web").getBoolean();
        // hostname = config.get(CONFIG_CAT, "hostname", "localhost",
        // "Hostname of your server. Used for QR code generation.").getString();
        // port = config.get(CONFIG_CAT, "port", 27020, "Port to connect remotes to").getInt();
        // useSSL = config.get(CONFIG_CAT, "use_ssl", false,
        // "Protect the communication against network sniffing by encrypting traffic with SSL (You don't really need it - believe me)").getBoolean();
        // passkeyLength = config.get(CONFIG_CAT, "passkey_length", 6,
        // "Length of the randomly generated passkeys").getInt();
    }

    /* ------------------------------------------------------------ */

    public static String getFactionGroup(String faction)
    {
        return GROUP_PREFIX + faction;
    }

    public static String getFactionName(String faction)
    {
        return APIRegistry.perms.getServerZone().getGroupPermission(getFactionGroup(faction), PERM_DATA_NAME);
    }

    public static void setFactionName(String faction, String name)
    {
        APIRegistry.perms.getServerZone().setGroupPermissionProperty(getFactionGroup(faction), PERM_DATA_NAME, name);
    }

    public static boolean isFaction(String faction)
    {
        String factionName = getFactionName(faction);
        return factionName != null && !factionName.isEmpty();
    }

    public static boolean isGroupFaction(String group)
    {
        if (!group.startsWith(GROUP_PREFIX))
            return false;
        return APIRegistry.perms.getServerZone().getGroupPermission(group, PERM_DATA_NAME) != null;
    }

    public static List<String> getFaction(UserIdent ident)
    {
        List<String> factions = new ArrayList<String>();
        SortedSet<GroupEntry> groups = APIRegistry.perms.getStoredPlayerGroups(ident);
        for (GroupEntry groupEntry : groups)
            if (isGroupFaction(groupEntry.getGroup()))
                factions.add(groupEntry.getGroup().substring(GROUP_PREFIX.length()));
        return factions;
    }

    public static boolean isInFaction(UserIdent ident, String faction)
    {
        String factionGroup = getFactionGroup(faction);
        SortedSet<GroupEntry> groups = APIRegistry.perms.getStoredPlayerGroups(ident);
        for (GroupEntry groupEntry : groups)
            if (groupEntry.getGroup().equals(factionGroup))
                return true;
        return false;
    }

    public static List<String> getFactions()
    {
        List<String> factions = new ArrayList<String>();
        for (String group : APIRegistry.perms.getServerZone().getGroups())
            if (group.startsWith(GROUP_PREFIX) && isGroupFaction(group))
                factions.add(group.substring(GROUP_PREFIX.length()));
        return factions;
    }

    public static boolean isLockedFaction(String id)
    {
        Boolean locked = APIRegistry.perms.getServerZone().checkGroupPermission(getFactionGroup(id), PERM_DATA_LOCKED);
        if (locked == null)
            return false;
        return locked;
    }

    public static boolean hasFactionRank(UserIdent ident, String rank)
    {
        Set<String> groups = APIRegistry.perms.getServerZone().getStoredPlayerGroups(ident);
        return groups.contains(RANK_PREFIX + rank);
    }

    public static void setRank(UserIdent ident, String rank)
    {
        Set<String> groups = APIRegistry.perms.getServerZone().getPlayerGroups().get(ident);
        if (groups != null)
            for (Iterator<String> it = groups.iterator(); it.hasNext();)
                if (it.next().startsWith(RANK_PREFIX))
                    it.remove();
        if (rank != null)
            APIRegistry.perms.getServerZone().addPlayerToGroup(ident, RANK_PREFIX + rank);
    }

}
