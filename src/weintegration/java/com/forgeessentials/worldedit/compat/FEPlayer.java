package com.forgeessentials.worldedit.compat;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldVector;
import com.sk89q.worldedit.entity.BaseEntity;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.extension.platform.AbstractPlayerActor;
import com.sk89q.worldedit.extent.inventory.BlockBag;
import com.sk89q.worldedit.forge.ForgeWorldEdit;
import com.sk89q.worldedit.session.SessionKey;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.world.World;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.permissions.PermissionsManager;

import javax.annotation.Nullable;
import java.util.UUID;

// dummy class for permissions - otherwise is a passthrough to ForgePlayer
public class FEPlayer extends AbstractPlayerActor
{
    private Player player;
    private EntityPlayerMP mcPlayer;

    public FEPlayer(EntityPlayerMP mcPlayer, Player player)
    {
        this.player = ForgeWorldEdit.inst.getPlatform().matchPlayer(player);
        this.mcPlayer = mcPlayer;
    }

    @Override
    public World getWorld()
    {
        return player.getWorld();
    }

    @Override
    public int getItemInHand()
    {
        return player.getItemInHand();
    }

    @Override
    public void giveItem(int type, int amount)
    {
        player.giveItem(type, amount);
    }

    @Override
    public BlockBag getInventoryBlockBag()
    {
        return player.getInventoryBlockBag();
    }

    @Override
    public WorldVector getPosition()
    {
        return player.getPosition();
    }

    @Override
    public double getPitch()
    {
        return player.getPitch();
    }

    @Override
    public double getYaw()
    {
        return player.getYaw();
    }

    @Override
    public void setPosition(Vector pos, float pitch, float yaw)
    {
        player.setPosition(pos, pitch, yaw);
    }

    @Override
    public String getName()
    {
        return player.getName();
    }

    @Override
    public void printRaw(String msg)
    {
        player.printRaw(msg);
    }

    @Override
    public void printDebug(String msg)
    {
        player.printDebug(msg);
    }

    @Override
    public void print(String msg)
    {
        player.print(msg);
    }

    @Override
    public void printError(String msg)
    {
        player.printError(msg);
    }

    @Nullable
    @Override
    public BaseEntity getState()
    {
        return player.getState();
    }

    @Override
    public Location getLocation()
    {
        return player.getLocation();
    }

    @Nullable
    @Override
    public <T> T getFacet(Class<? extends T> cls)
    {
        return player.getFacet(cls);
    }

    @Override
    public UUID getUniqueId()
    {
        return player.getUniqueId();
    }

    @Override
    public SessionKey getSessionKey()
    {
        return player.getSessionKey();
    }

    @Override
    public String[] getGroups()
    {
        return player.getGroups();
    }

    // all because of this little guy...
    @Override
    public boolean hasPermission(String permission)
    {
        return PermissionsManager.checkPermission(mcPlayer, permission);
    }
}
