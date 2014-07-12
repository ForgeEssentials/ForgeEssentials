package net.minecraftforge.permissions.opbasedimpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraftforge.permissions.api.IGroup;
 
/**
 * This class acts as merely a wrapper around the currently existing ops system in minecraft.
 * It does not provide any additional functionality, nor does it allow for group manipulation.
 * You are not recommended to use this in your mods, as this class is not guaranteed to exist, especially
 * when another permissions framework is installed.
 *
 */
public class OpBasedGroup implements IGroup
{
    private String name;
    private IGroup parent;
    
    private MinecraftServer server;
    
    public OpBasedGroup(String name)
    {
        this.name = name;
        this.server = MinecraftServer.getServer();
    }
    
    // noop - groups in opBased are just wrappers around the ops list
    @Override
    public void addPlayer(UUID playerID){}
 
    @Override
    public boolean removePlayer(UUID playerID)
    {
        return false; // use op/deop to remove players from ops group
    }
    
    @Override
    public boolean isMember(UUID playerID)
    {
        if (name.equals("OP"))
        {
            MinecraftServer server = FMLCommonHandler.instance().getSidedDelegate().getServer();
            GameProfile player = server.func_152358_ax().func_152652_a(playerID);

            // SP and LAN
            if (server.isSinglePlayer())
            {
                if (server instanceof IntegratedServer)
                    return server.getServerOwner().equalsIgnoreCase(player.getName());
                else
                    return server.getConfigurationManager().func_152596_g(player);
            }

            // SMP
            return server.getConfigurationManager().func_152596_g(player);
        }
        else return true;
    }
 
    @Override
    public Collection<UUID> getAllPlayers()
    {
        List<UUID> players = new ArrayList<UUID>();
        
        if (name.equals("OP"))
        {
            for(String s : server.getConfigurationManager().func_152606_n())
            {
                GameProfile p = server.getConfigurationManager().func_152603_m().func_152700_a(s);
                players.add(p.getId());
            }
        }
        else
        {
            for (GameProfile p : server.getConfigurationManager().func_152600_g()) players.add(p.getId());
        }
        return players;
    }
    
    @Override
    public IGroup setParent(IGroup parent)
    {
        this.parent = parent;
        return this;
    }
    
    @Override
    public IGroup getParent()
    {
        return parent;
    }
 
    @Override
    public String getName()
    {
        return name;
    }
 
    @Override
    public void setName(String name)
    {
        this.name = name;
    }
}