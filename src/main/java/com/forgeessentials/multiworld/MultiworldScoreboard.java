package com.forgeessentials.multiworld;

import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.MinecraftServer;

public class MultiworldScoreboard extends ServerScoreboard
{

    public Multiworld multiworld;

    public MultiworldScoreboard(MinecraftServer mcServer, Multiworld multiworld)
    {
        super(mcServer);
        this.multiworld = multiworld;
    }
}

