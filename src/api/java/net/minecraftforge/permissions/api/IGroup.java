package net.minecraftforge.permissions.api;
 
import net.minecraft.entity.player.EntityPlayer;
 
import java.util.Collection;
import java.util.UUID;
 
/**
 * A group object in the API.
 * 
 * Framework authors:
 * If you choose to support groups, they must be retrievable via a class implementing this interface.
 */
public interface IGroup {
 
    /**
     * Get all players in the group
     *
     * @return A list containing UUIDs of all players in the group
     */
    Collection<UUID> getAllPlayers();
    
    /**
     * Is the player in the group
     * 
     * @param player player to check
     * @return if the player was in the group
     */
    boolean isMember(UUID playerID);
 
    /**
     * Get the group name
     *
     * @return the group name
     */
    String getName();
}