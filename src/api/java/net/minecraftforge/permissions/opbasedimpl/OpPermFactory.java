package net.minecraftforge.permissions.opbasedimpl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.minecraft.dispenser.ILocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.permissions.PermissionsManager;
import net.minecraftforge.permissions.api.IGroup;
import net.minecraftforge.permissions.api.PermBuilderFactory;
import net.minecraftforge.permissions.api.context.EntityContext;
import net.minecraftforge.permissions.api.context.EntityLivingContext;
import net.minecraftforge.permissions.api.context.IContext;
import net.minecraftforge.permissions.api.context.PlayerContext;
import net.minecraftforge.permissions.api.context.Point;
import net.minecraftforge.permissions.api.context.TileEntityContext;
import net.minecraftforge.permissions.api.context.WorldContext;
import cpw.mods.fml.common.Loader;

public class OpPermFactory implements PermBuilderFactory
{
    static HashSet<String> opPerms      = new HashSet<String>();
    static HashSet<String> deniedPerms  = new HashSet<String>();
    static HashSet<String> allowedPerms = new HashSet<String>();
    static HashMap<String, IGroup> groups = new HashMap<String, IGroup>();

    public static final IContext GLOBAL = new IContext() {};
    
    private static Configuration config = new Configuration(new File(Loader.instance().getConfigDir(), "forgeOpBasedPerms.cfg"));
    
    public static OpPermFactory INSTANCE;
    
    @Override
    public String getName()
    {
        return "Forge";
    }
    
    public static void initialize()
    {
        
        
        config.save();
        
        IGroup all = new OpBasedGroup("ALL");
        IGroup ops = new OpBasedGroup("OP");
        
        groups.put("ALL", all);
        groups.put("OP", ops);
    }
    
    public boolean checkPerm(EntityPlayer player, String node, Map<String, IContext> contextInfo)
    {
        if (OpPermFactory.deniedPerms.contains(node))
            return false;
        else if (OpPermFactory.allowedPerms.contains(node))
            return true;
        else if (OpPermFactory.opPerms.contains(node))
            return PermissionsManager.getGroup("OP").isMember(player.getPersistentID());
        else
            throw new UnregisterredPermissionException(node);
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
    
    @Override
    public Collection<IGroup> getGroups(UUID playerID)
    {
        List<IGroup> reply = new ArrayList<IGroup>();
        Iterator it = groups.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            IGroup group = (IGroup)pairs.getValue();
            if (!group.getAllPlayers().contains(playerID)){break;}
            else reply.add(group);
            it.remove();
        }
        return reply;
    }
 
    @Override
    public IGroup getGroup(String name)
    {
        return groups.get(name);
    }
 
    @Override
    public Collection<IGroup> getAllGroups()
    {
        Collection<IGroup> reply = new ArrayList<IGroup>();
        Iterator it = groups.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            IGroup group = (IGroup)pairs.getValue();
            reply.add(group);
            it.remove();
        }
        return reply;
    }

    private static boolean isRegistered(String node)
    {
        return opPerms.contains(node) || allowedPerms.contains(node) || deniedPerms.contains(node);
    }

    @Override
    public void registerPermission(String node, PermBuilderFactory.RegisteredPermValue allow)
    {
        PermBuilderFactory.RegisteredPermValue status = PermBuilderFactory.RegisteredPermValue.fromString(config.get("Nodes", node, allow.toString()).getString());
        
        switch (status)
        {
        case TRUE: allowedPerms.add(node);
        case FALSE: deniedPerms.add(node);
        case OP: opPerms.add(node);
        }
    }
    
    private static class UnregisterredPermissionException extends RuntimeException
    {
        public final String node;
        public UnregisterredPermissionException(String node)
        {
            super("Unregisterred Permission encountered! "+node);
            this.node = node;
        }
    }
    
}
