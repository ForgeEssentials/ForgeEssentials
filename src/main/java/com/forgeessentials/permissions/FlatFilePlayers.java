package com.forgeessentials.permissions;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.util.PlayerInfo;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.config.Configuration;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

public class FlatFilePlayers {
    File file;

    public FlatFilePlayers(File file)
    {
        this.file = new File(file, "players.txt");
    }

    public ArrayList<String> load()
    {
        ArrayList<String> players = new ArrayList<String>();

        Configuration config = new Configuration(file);

        PlayerInfo info;
        for (String cat : config.getCategoryNames())
        {
            if (cat.contains("."))
            {
                continue;
            }
            else if (cat.equalsIgnoreCase(APIRegistry.perms.getEntryPlayer().toString()))
            {
                APIRegistry.perms.setEPPrefix(config.get(cat, "prefix", " ").getString());
                APIRegistry.perms.setEPSuffix(config.get(cat, "suffix", " ").getString());
                continue;
            }
            info = PlayerInfo.getPlayerInfo(UUID.fromString(cat));

            if (info != null)
            {
                info.prefix = config.get(cat, "prefix", " ").getString();
                info.suffix = config.get(cat, "suffix", " ").getString();
            }

            players.add(cat);
            discardInfo(info, new String[] { });
        }

        return players;
    }

    public void save(ArrayList<String> players)
    {
        // clear it.
        if (file.exists())
        {
            file.delete();
        }

        String[] allPlayers = new String[0];
        MinecraftServer server = FMLCommonHandler.instance().getSidedDelegate().getServer();
        if (server != null)
        {
            allPlayers = server.func_152358_ax().func_152654_a();
        }

        Configuration config = new Configuration(file);

        PlayerInfo info;
        for (String name : players)
        {
            if (name.equalsIgnoreCase(APIRegistry.perms.getEntryPlayer().toString()))
            {
                config.get(name, "prefix", APIRegistry.perms.getEPPrefix());
                config.get(name, "suffix", APIRegistry.perms.getEPSuffix());
            }

            info = PlayerInfo.getPlayerInfo(UUID.fromString(name));
            config.get(name, "prefix", info.prefix == null ? "" : info.prefix);
            config.get(name, "suffix", info.suffix == null ? "" : info.suffix);
            discardInfo(info, allPlayers);
        }

        config.save();
    }

    private void discardInfo(PlayerInfo info, String[] allPlayers)
    {
        for (String name : allPlayers)
        {
            if (info.playerID.toString().equalsIgnoreCase(name))
            {
                return;
            }
        }

        // not logged in?? kill it.
        PlayerInfo.discardInfo(info.playerID);
    }

}
