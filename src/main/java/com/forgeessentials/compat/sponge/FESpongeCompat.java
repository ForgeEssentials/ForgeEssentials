package com.forgeessentials.compat.sponge;

import javax.inject.Inject;

import org.spongepowered.api.Game;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.state.ConstructionEvent;
import org.spongepowered.api.event.state.PreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;

import com.forgeessentials.commons.BuildInfo;
import com.forgeessentials.core.environment.Environment;

/**
 * Dummy plugin class for FE-Sponge compatibility.
 *
 * Watch this space, more to come.
 */
@Plugin(id = "forgeessentials", name = "FESpongeCompat", version = BuildInfo.BASE_VERSION)
public class FESpongeCompat
{

    @Inject
    private Game game;

    @Subscribe
    public void checkEnvironment(ConstructionEvent e)
    {
        if (!game.getPlatform().getName().equals("Sponge"))
        {
            throw new RuntimeException("You must be running the Forge implementation of SpongeAPI on Minecraft Forge in order to load ForgeEssentials!");
        }
    }

    @Subscribe
    public void register(PreInitializationEvent e)
    {
        Environment.registerSpongeCompatPlugin(game.getPluginManager().isLoaded("worldedit"));
    }
}
