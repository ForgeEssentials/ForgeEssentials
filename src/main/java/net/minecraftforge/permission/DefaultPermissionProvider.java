package net.minecraftforge.permission;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.UserListOpsEntry;

import com.mojang.authlib.GameProfile;

public class DefaultPermissionProvider implements IPermissionProvider
{

    private static final String PERM_SEED = PermissionManager.DEFAULT_COMMAND_NODE + ".seed";
    private static final String PERM_TELL = PermissionManager.DEFAULT_COMMAND_NODE + ".tell";
    private static final String PERM_HELP = PermissionManager.DEFAULT_COMMAND_NODE + ".help";
    private static final String PERM_ME = PermissionManager.DEFAULT_COMMAND_NODE + ".me";

    protected static final Map<String, PermissionLevel> permissions = new HashMap<String, PermissionLevel>();

    @Override
    public boolean checkPermission(PermissionContext context, String permission)
    {
        // Special permission checks from EntityPlayerMP
        if (PERM_SEED.equals(permission) && !MinecraftServer.getServer().isDedicatedServer())
            return true;
        if (PERM_TELL.equals(permission) || PERM_HELP.equals(permission) || PERM_ME.equals(permission))
            return true;

        PermissionLevel level = permissions.get(permission);
        if (level == null)
            return true;
        int opLevel = context.isPlayer() ? getOpLevel(context.getPlayer().getGameProfile()) : 0;
        return level.getOpLevel() <= opLevel;
    }

    @Override
    public void registerPermission(String permission, PermissionLevel level)
    {
        permissions.put(permission, level);
    }

    protected int getOpLevel(GameProfile profile)
    {
        if (!MinecraftServer.getServer().getConfigurationManager().canSendCommands(profile))
            return 0;
        UserListOpsEntry entry = MinecraftServer.getServer().getConfigurationManager().getOppedPlayers().getEntry(profile);
        return entry != null ? entry.getPermissionLevel() : MinecraftServer.getServer().getOpPermissionLevel();
    }

}
