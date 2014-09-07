package net.minecraftforge.permissions.opbasedimpl;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.permissions.api.IGroup;

import java.util.Arrays;
import java.util.Collection;
 
/**
 * This class acts as merely a wrapper around the currently existing ops system in minecraft.
 * It does not provide any additional functionality.
 * You are not recommended to use this in your mods, as this class is not guaranteed to exist, especially
 * when another permissions framework is installed.
 *
 */
public class OpBasedGroup implements IGroup
{
    private String name;
    
    private MinecraftServer server = MinecraftServer.getServer();
    
    public OpBasedGroup(String name)
    {
        this.name = name;
    }
    
    @Override
    public boolean isMember(String playerID)
    {
        if (name.equals("OP"))
        {
            /*
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
            */
            return MinecraftServer.getServer().getConfigurationManager().isPlayerOpped(playerID);
        }
        else return true;
    }
 
    @Override
    public Collection<String> getAllPlayers()
    {
        //List<UUID> players = new ArrayList<UUID>();
        Collection<String>players;
        
        if (name.equals("OP"))
        {
            /*for(String s : server.getConfigurationManager().func_152606_n())
            {
                GameProfile p = server.getConfigurationManager().func_152603_m().func_152700_a(s);
                players.add(p.getId());
            }*/
            players = server.getConfigurationManager().getOps();
        }
        else
        {
            //for (GameProfile p : server.getConfigurationManager().func_152600_g()) players.add(p.getId());
            players = Arrays.asList(server.getConfigurationManager().getAllUsernames());
        }
        return players;
    }
 
    @Override
    public String getName()
    {
        return name;
    }
}