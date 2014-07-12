package net.minecraftforge.permissions.api;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.minecraft.dispenser.ILocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.permissions.api.context.IContext;

import com.google.gson.JsonObject;

public interface PermBuilderFactory
{
    /**
     * The name of this permissions provider (usually the modid)
     * @return name Name of permissions provider
     */
    String getName();
    
    /**
     * Check permissions
     * 
     * @param player
     * @param node
     * @param contextInfo
     * @return 
     */
    boolean checkPerm(EntityPlayer player, String node, Map<String, IContext> contextInfo);

    /**
     * @return The default IContext instance of this object for this Implementation.
     */
    IContext getDefaultContext(EntityPlayer player);

    /**
     * @return The default IContext instance of this object for this Implementation.
     */
    IContext getDefaultContext(TileEntity te);

    /**
     * @return The default IContext instance of this object for this Implementation.
     */
    IContext getDefaultContext(ILocation loc);

    /**
     * @return The default IContext instance of this object for this Implementation.
     */
    IContext getDefaultContext(Entity entity);

    /**
     * @return The default IContext instance of this object for this Implementation.
     */
    IContext getDefaultContext(World world);

    /**
     * @return A IContext signifying the Server as a whole.
     */
    IContext getGlobalContext();

    /**
     * At the very least, this method should return an anonymous instance of IContext.
     * This method should NEVER return null.
     * @return The default IContext instance of this object for this Implementation.
     */
    IContext getDefaultContext(Object whoKnows);

    /**
     * This is where permissions are registered with their default value.
     * @param perms
     */
    void registerPermission(String node, RegisteredPermValue allow);
    
    /**
     * Get the groups a player is in
     * @param player
     * @return A list of groups the player is in
     */
    Collection<IGroup> getGroups(UUID playerID);
    
    /**
     * Get a group with a given name
     * @param name
     * @return A group if it exists, null if not found
     */
    IGroup getGroup(String name);
    
    /**
     * Get all groups known to the implementation
     * @return A list of all groups
     */
    Collection<IGroup> getAllGroups();
    
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
