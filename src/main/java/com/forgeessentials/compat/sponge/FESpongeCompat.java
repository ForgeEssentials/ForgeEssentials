package com.forgeessentials.compat.sponge;



import com.forgeessentials.commons.BuildInfo;
import com.forgeessentials.core.environment.Environment;

/**
 * Dummy plugin class for FE-Sponge compatibility.
 *
 * Watch this space, more to come.
 */
//@Plugin(id = "forgeessentials", name = "FESpongeCompat", version = BuildInfo.BASE_VERSION)
public class FESpongeCompat
{

    /*
    to update for Sponge 3.0 and MC 1.8.9
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
    */
}
