package net.minecraftforge.permissions;

import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.FMLLog;

/**
 * The main entry point for the Permissions API.
 * At most, mods should only use this class.
 *
 */
public final class PermissionsManager
{
    private static IPermissionsProvider provider = new DefaultPermProvider();
    
    private static boolean wasSet = false;
    
    private PermissionsManager(){}
    
    public static boolean checkPermission(EntityPlayer player, String permissionNode)
    {
        return provider.checkPermission(new PermissionContext().setPlayer(player), permissionNode);
    }

    /**
     * Checks a permission
     * 
     * @param context
     *            Context, where the permission is checked in.
     * @param permissionNode
     *            The permission node, that should be checked
     * @return Whether the permission is allowed
     */
    public static boolean checkPermission(IContext contextInfo, String permissionNode)
    {
        return provider.checkPermission(contextInfo, permissionNode);
    }

    /**
     * This is where permissions are registered with their default value.
     * 
     * @param permissionNode
     * @param level
     *            Default level of the permission. This can be used to tell the
     *            underlying {@link IPermissionsProvider} whether a player
     *            should be allowed to access this permission by default, or as
     *            operator only.
     */
    public static void registerPermission(String permissionNode, RegisteredPermValue level)
    {
        provider.registerPermission(permissionNode, level);
    }

    /**
     * Framework authors:
     * Call this method in preinit, and only preinit.
     * You will not receive the registration call for vanilla permissions if you register your framework after preinit.
     * @param factory your permission framework class implementing IPermissionsProvider
     * @throws IllegalStateException
     */
    public static void setPermProvider(IPermissionsProvider factory) throws IllegalStateException
    {
        if (factory == null)
        {
            wasSet = false;
        }
        else if (wasSet)
        {
            throw new IllegalStateException(String.format("Attempted to register permissions framework %s1 when permissions framework %s2 is already registered!", factory.getClass().getName(), provider.getClass().getName()));
        }
        else
        {
            provider = factory;
            wasSet = true;
            FMLLog.fine("Registered permissions framework " + provider.getClass().getName());
        }
    }
    
    /**
     * Based on Bukkit's PermissionDefault system.
     * Accepted names: True, False, Op
     *
     */
    public static enum RegisteredPermValue
    {
        TRUE, FALSE, OP;
        
        public static RegisteredPermValue fromBoolean(boolean toConvert)
        {
            if (toConvert) return TRUE;
            else return FALSE;
        }
        
        public static RegisteredPermValue fromString(String name)
        {
            for (RegisteredPermValue value : values())
            {
                if (value.name().equalsIgnoreCase(name)) return value;
            }
            return null;
        }
    }
}
