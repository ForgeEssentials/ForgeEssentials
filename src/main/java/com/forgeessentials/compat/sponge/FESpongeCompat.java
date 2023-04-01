package com.forgeessentials.compat.sponge;

import org.spongepowered.api.Game;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.*;
import org.spongepowered.api.event.lifecycle.ProvideServiceEvent.GameScoped;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.plugin.builtin.jvm.Plugin;

import com.forgeessentials.compat.sponge.economy.FEEconService;
import com.forgeessentials.core.environment.Environment;
import com.google.inject.Inject;

/**
 * Plugin class for FE-Sponge compatibility.
 *
 * Watch this space, more to come.
 */
@Plugin(value = "forgeessentials-sponge")
public class FESpongeCompat
{

    @Inject
    private Game game;

    @Listener
    public void checkEnvironment(StartingEngineEvent<?> e)
    {
        if (!game.platform().executionType().name().equals("SpongeForge"))
        {
            throw new RuntimeException("You must be running the Forge implementation of SpongeAPI on Minecraft Forge in order to load ForgeEssentials!");
        }
    }

    @Listener
    public void register(LoadedGameEvent e)
    {
        Environment.registerSpongeCompatPlugin(game.pluginManager().plugin("worldedit").isPresent());
    }

    @Listener
    public void provideEconomyService(final GameScoped<EconomyService> event) {
        event.suggest(() -> new FEEconService());
    }

}
