package com.forgeessentials.api;

import com.forgeessentials.api.UserIdent.NpcUserIdent;
import com.forgeessentials.api.UserIdent.ServerUserIdent;
import com.forgeessentials.api.economy.Economy;
import com.forgeessentials.api.modules.FEModules;
import com.forgeessentials.api.permissions.IPermissionsHelper;
import com.forgeessentials.api.remote.FERemoteHandler;
import com.forgeessentials.api.remote.RemoteHandler;
import com.forgeessentials.api.remote.RemoteManager;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;

/**
 * This is the central access point for all FE API functions
 */
public class APIRegistry
{

    public static final ServerUserIdent IDENT_SERVER = UserIdent.getServer("fefefefe-fefe-fefe-fefe-fefefefefefe",
            "$SERVER");

    public static final ServerUserIdent IDENT_RCON = UserIdent.getServer("fefefefe-fefe-fefe-fefe-fefefefefecc",
            "$RCON");

    public static final ServerUserIdent IDENT_CMDBLOCK = UserIdent.getServer("fefefefe-fefe-fefe-fefe-fefefefefecb",
            "$COMMANDBLOCK");

    public static final ServerUserIdent IDENT_COMMANDFAKER = UserIdent.getServer("fefefefe-fefe-fefe-fefe-fefefefefecf",
            "$COMMANDFAKER");

    public static final NpcUserIdent IDENT_NPC = UserIdent.getNpc(null);

    /**
     * Use this to call API functions available in the Module Launcher
     */
    public static FEModules modules = new FEModules();

    /**
     * Use this to call API functions available in the economy module.
     */
    public static Economy economy;

    /**
     * Use to call API functions from the permissions module.
     */
    public static IPermissionsHelper perms;

    /**
     * Use to call scripting API functions, or to invoke a script run from outside the module.
     */
    public static ScriptHandler scripts;

    /**
     * Allows identifying worlds by name. If you change this handler, remember to call the old one in your implementation!
     */
    public static NamedWorldHandler namedWorldHandler = new NamedWorldHandler.DefaultNamedWorldHandler();

    /**
     * This manager allows registering custom {@link RemoteHandler}s for remote-module. Please be careful to use unique IDs when registering handlers.
     * 
     * Using this instance to register handlers is deprecated. Use the {@link FERemoteHandler} annotation instead.
     */
    @Deprecated
    public static RemoteManager remoteManager = new RemoteManager.DefaultRemoteHandlerManager();

    /**
     * The FE internal event-bus
     */

    public static final IEventBus FE_EVENTBUS = MinecraftForge.EVENT_BUS;

    /**
     * Gets the Mod event bus
     * 
     * The events fail to pick up a post if running on the mod event bus is being redirected to the FORGE EVENT_BUS.
     */
    public static IEventBus getFEEventBus()
    {
        return FE_EVENTBUS;
    }
}
