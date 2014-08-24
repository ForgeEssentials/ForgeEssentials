package com.forgeessentials.permissions.forge;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.Group;
import com.forgeessentials.api.permissions.query.PermQueryPlayer;
import com.forgeessentials.permissions.PermissionsHelper;
import com.forgeessentials.permissions.SqlHelper;
import com.forgeessentials.util.FunctionHelper;
import com.google.common.collect.ImmutableMap;
import net.minecraft.dispenser.ILocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.permissions.PermissionsManager;
import net.minecraftforge.permissions.api.IGroup;
import net.minecraftforge.permissions.api.IPermissionsProvider;
import net.minecraftforge.permissions.api.context.IContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public class ForgePermissionsHelper implements IPermissionsProvider{

    @Override public String getName()
    {
        return "ForgeEssentials";
    }

    @Override public boolean checkPerm(EntityPlayer player, String node, ImmutableMap<String, IContext> contextInfo)
    {
        return PermissionsHelper.INSTANCE.checkPermAllowed(new PermQueryPlayer(player, node));
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

    @Override public void registerPermission(String node, PermissionsManager.RegisteredPermValue allow)
    {

    }

    @Override public Collection<IGroup> getGroups(UUID playerID)
    {
        ArrayList<IGroup> returned = new ArrayList<>();
        for (Group g : PermissionsHelper.INSTANCE.getApplicableGroups(FunctionHelper.getPlayerForUUID(playerID), true))
        {
            returned.add(g);
        }

        return returned;
    }

    @Override public IGroup getGroup(String name)
    {
        return SqlHelper.getGroupForName(name);
    }

    @Override public Collection<IGroup> getAllGroups()
    {
        ArrayList<IGroup> returned = new ArrayList<>();
        for (Group g : PermissionsHelper.INSTANCE.getGroupsInZone(APIRegistry.zones.getGLOBAL().getZoneName()))
        {
            returned.add(g);
        }

        return returned;
    }
}
