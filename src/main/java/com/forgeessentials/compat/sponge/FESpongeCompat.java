package com.forgeessentials.compat.sponge;

import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameConstructionEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.economy.EconomyService;

import com.forgeessentials.commons.BuildInfo;
import com.forgeessentials.compat.sponge.economy.FEEconService;
import com.forgeessentials.core.environment.Environment;
import com.google.inject.Inject;

/**
 * Plugin class for FE-Sponge compatibility.
 *
 * Watch this space, more to come.
 */
@Plugin(id = "forgeessentials-sponge", name = "FESpongeCompat", version = BuildInfo.BASE_VERSION,
        description = "ForgeEssentials interoperability module with the Sponge API, providing additional functionality to bridge Forge and Sponge.")
public class FESpongeCompat
{

    @Inject
    private Game game;

    @Listener
    public void checkEnvironment(GameConstructionEvent e)
    {
        if (!game.getPlatform().getImplementation().getName().equals("SpongeForge"))
        {
            throw new RuntimeException("You must be running the Forge implementation of SpongeAPI on Minecraft Forge in order to load ForgeEssentials!");
        }
    }

    @Listener
    public void register(GamePreInitializationEvent e)
    {
        Environment.registerSpongeCompatPlugin(game.getPluginManager().isLoaded("worldedit"));
    }

    @Listener
    public void init(GameInitializationEvent e)
    {
        Sponge.getServiceManager().setProvider(this, EconomyService.class, new FEEconService());
    }

}
