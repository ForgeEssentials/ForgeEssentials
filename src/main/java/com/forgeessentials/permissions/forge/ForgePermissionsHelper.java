package com.forgeessentials.permissions.forge;

import net.minecraft.dispenser.ILocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.permissions.api.IGroup;
import net.minecraftforge.permissions.api.PermBuilderFactory;
import net.minecraftforge.permissions.api.context.IContext;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

/**
 * This class is allowed to call PermissionsHelper directly, being in the same module.
 */
public class ForgePermissionsHelper implements PermBuilderFactory{
    @Override public String getName()
    {
        return "ForgeEssentials";
    }

    @Override public boolean checkPerm(EntityPlayer player, String node, Map<String, IContext> contextInfo)
    {
        return false;
    }

    @Override public IContext getDefaultContext(EntityPlayer player)
    {
        return null;
    }

    @Override public IContext getDefaultContext(TileEntity te)
    {
        return null;
    }

    @Override public IContext getDefaultContext(ILocation loc)
    {
        return null;
    }

    @Override public IContext getDefaultContext(Entity entity)
    {
        return null;
    }

    @Override public IContext getDefaultContext(World world)
    {
        return null;
    }

    @Override public IContext getGlobalContext()
    {
        return null;
    }

    @Override public IContext getDefaultContext(Object whoKnows)
    {
        return null;
    }

    @Override public void registerPermission(String node, RegisteredPermValue allow)
    {

    }

    @Override public Collection<IGroup> getGroups(UUID playerID)
    {
        return null;
    }

    @Override public IGroup getGroup(String name)
    {
        return null;
    }

    @Override public Collection<IGroup> getAllGroups()
    {
        return null;
    }
}
