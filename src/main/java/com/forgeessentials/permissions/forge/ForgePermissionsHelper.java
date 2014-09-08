package com.forgeessentials.permissions.forge;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.RegGroup;
import com.forgeessentials.api.permissions.query.PermQueryPlayer;
import com.forgeessentials.permissions.PermissionsHelper;
import net.minecraft.dispenser.ILocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.permissions.PermissionsManager;
import net.minecraftforge.permissions.api.IPermissionsProvider;
import net.minecraftforge.permissions.api.context.*;

import java.util.Map;

public class ForgePermissionsHelper implements IPermissionsProvider{

    public static final IContext GLOBAL = new IContext() {};

    @Override public String getName()
    {
        return "ForgeEssentials";
    }

    @Override public boolean checkPerm(EntityPlayer player, String node, Map<String, IContext> contextInfo)
    {
        return PermissionsHelper.INSTANCE.checkPermAllowed(new PermQueryPlayer(player, node));
    }

    @Override
    public IContext getDefaultContext(EntityPlayer player)
    {
        IContext context = new PlayerContext(player);
        return context;
    }

    @Override
    public IContext getDefaultContext(TileEntity te)
    {
        return new TileEntityContext(te);
    }

    @Override
    public IContext getDefaultContext(ILocation loc)
    {
        return new Point(loc);
    }

    @Override
    public IContext getDefaultContext(Entity entity)
    {
        return new EntityContext(entity);
    }

    @Override
    public IContext getDefaultContext(World world)
    {
        return new WorldContext(world);
    }

    @Override
    public IContext getGlobalContext()
    {
        return GLOBAL;
    }

    @Override
    public IContext getDefaultContext(Object entity)
    {
        if (entity instanceof EntityLiving)
        {
            return new EntityLivingContext((EntityLiving) entity);
        }
        else
        {
            return GLOBAL;
        }
    }

    @Override public void registerPermission(String node, PermissionsManager.RegisteredPermValue allow)
    {
        APIRegistry.permReg.registerPermissionLevel(node, RegGroup.fromForgeLevel(allow));

    }
}
