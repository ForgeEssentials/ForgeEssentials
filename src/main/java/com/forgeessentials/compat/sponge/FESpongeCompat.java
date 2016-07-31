package com.forgeessentials.compat.sponge;

import org.spongepowered.api.Game;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameConstructionEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;

import com.forgeessentials.commons.BuildInfo;
import com.forgeessentials.core.environment.Environment;
import com.google.inject.Inject;

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

    @Listener
    public void checkEnvironment(GameConstructionEvent e)
    {
        if (!game.getPlatform().getImplementation().getName().equals("Sponge"))
        {
            throw new RuntimeException("You must be running the Forge implementation of SpongeAPI on Minecraft Forge in order to load ForgeEssentials!");
        }
    }

    @Listener
    public void register(GamePreInitializationEvent e)
    {
        Environment.registerSpongeCompatPlugin(game.getPluginManager().isLoaded("worldedit"));
    }

}
