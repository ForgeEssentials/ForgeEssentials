package net.minecraftforge.permissions;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import cpw.mods.fml.common.FMLLog;
import net.minecraft.dispenser.ILocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.permissions.api.IGroup;
import net.minecraftforge.permissions.api.PermBuilderFactory;
import net.minecraftforge.permissions.api.PermBuilderFactory.RegisteredPermValue;
import net.minecraftforge.permissions.api.context.IContext;
import net.minecraftforge.permissions.opbasedimpl.OpPermFactory;

public final class PermissionsManager
{
    private static PermBuilderFactory FACTORY = new OpPermFactory();
    
    private PermissionsManager(){}
    
    public static void initialize()
    {
        setPermFactory(null);
    }

    private static       boolean            wasSet  = false;
    
    /**
     * Check a permissions with no contexts
     * @param player The EntityPlayer being checked
     * @param node The permissions node to be checked
     * @return true if permissions allowed, false if not
     */
    public static boolean checkPerm(EntityPlayer player, String node)
    {
        return FACTORY.checkPerm(player, node, null);
    }
    
    /**
     * Check a permissions with contexts
     * @param player
     * @param node
     * @param contextInfo
     * @return
     */
    public static boolean checkPerm(EntityPlayer player, String node, Map<String, IContext> contextInfo)
    {
        if (player instanceof FakePlayer)
            throw new IllegalArgumentException("You cannot check permissions with a fake player. Use PermManager.getPerm(username, node)");

        return FACTORY.checkPerm(player, node, contextInfo);
    }
    
    /**
     * Get all groups a player is in.
     * @param player Player to be queried
     * @return a Collection of groups that the player is a member of.
     */
    public static Collection<IGroup> getGroups(UUID playerID)
    {
        return FACTORY.getGroups(playerID);
    }
    
    /**
     * Get all groups
     * @return a Collection containing all groups
     */
    public static Collection<IGroup> getAllGroups()
    {
        return FACTORY.getAllGroups();
    }
    
    /**
     * Get a particular group
     * @param name Name of the group
     * @return the queried group
     */
    public static IGroup getGroup(String name)
    {
        return FACTORY.getGroup(name);
    }

    /**
     * Get the factory
     * FOR ADVANCED OPERATIONS
     * @return the current permissions implementor
     */
    public static PermBuilderFactory getPermFactory()
    {
        return FACTORY;
    }
    
    /**
     * This is where permissions are registered with their default value.
     * @param perms
     */
    public static void registerPermission(String node, RegisteredPermValue allow)
    {
        FACTORY.registerPermission(node, allow);
    }

    public static void setPermFactory(PermBuilderFactory factory) throws IllegalStateException
    {
        if (factory == null)
        {
            FACTORY = OpPermFactory.INSTANCE;
            wasSet = false;
        }
        else if (wasSet)
        {
            throw new IllegalStateException(String.format("Attempted to register permissions framework %s1 when permissions framework %s2 is already registered!", factory.getName(), FACTORY.getName()));
        }
        else
        {
            FACTORY = factory;
            wasSet = true;
            FMLLog.fine("Registered permissions framework " + FACTORY.getName());
        }
    }
}
