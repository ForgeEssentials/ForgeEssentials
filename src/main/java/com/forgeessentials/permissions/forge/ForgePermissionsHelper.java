package com.forgeessentials.permissions.forge;

import com.forgeessentials.api.permissions.Group;
import com.forgeessentials.api.permissions.query.PermQueryPlayer;
import com.forgeessentials.permissions.PermissionsHelper;
import com.forgeessentials.permissions.SqlHelper;
import com.forgeessentials.util.FunctionHelper;
import net.minecraft.dispenser.ILocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.permissions.api.IGroup;
import net.minecraftforge.permissions.api.PermBuilderFactory;
import net.minecraftforge.permissions.api.context.IContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public class ForgePermissionsHelper implements PermBuilderFactory{

    @Override public String getName()
    {
        return "ForgeEssentials";
    }

    @Override public boolean checkPerm(EntityPlayer player, String node, Map<String, IContext> contextInfo)
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

    @Override public void registerPermission(String node, RegisteredPermValue allow)
    {

    }

    @Override public Collection<IGroup> getGroups(UUID playerID)
    {
        ArrayList<IGroup> returned = new ArrayList<>();
        for (Group g : PermissionsHelper.INSTANCE.getApplicableGroups(FunctionHelper.getPlayerForUUID(playerID), true))
        {
            returned.add(ForgeGroup.make(g));
        }

        return returned;
    }

    @Override public IGroup getGroup(String name)
    {
        return ForgeGroup.make(SqlHelper.getGroupForName(name));
    }

    @Override public Collection<IGroup> getAllGroups()
    {
        ArrayList<IGroup> returned = new ArrayList<>();
        for (Group g : PermissionsHelper.INSTANCE.getGroupsInZone())
        {
            returned.add(ForgeGroup.make(g));
        }

        return returned;
    }
}
