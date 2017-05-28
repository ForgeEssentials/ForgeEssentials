package com.forgeessentials.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.forgeessentials.api.UserIdent.NpcUserIdent;
import com.forgeessentials.api.UserIdent.ServerUserIdent;
import com.forgeessentials.api.economy.Economy;
import com.forgeessentials.api.permissions.IPermissionsHelper;
import com.forgeessentials.api.remote.FERemoteHandler;
import com.forgeessentials.api.remote.RemoteHandler;
import com.forgeessentials.api.remote.RemoteManager;

import net.minecraftforge.fml.common.eventhandler.EventBus;

/**
 * This is the central access point for all FE API functions
 */
public class APIRegistry
{

    public static final ServerUserIdent IDENT_SERVER = UserIdent.getServer("fefefefe-fefe-fefe-fefe-fefefefefefe", "$SERVER");

    public static final ServerUserIdent IDENT_RCON = UserIdent.getServer("fefefefe-fefe-fefe-fefe-fefefefefecc", "$RCON");

    public static final ServerUserIdent IDENT_CMDBLOCK = UserIdent.getServer("fefefefe-fefe-fefe-fefe-fefefefefecb", "$COMMANDBLOCK");

    public static final NpcUserIdent IDENT_NPC = UserIdent.getNpc(null);

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
     * Allows identifying worlds by name. If you change this handler, remember to call the old one in your
     * implementation!
     */
    public static NamedWorldHandler namedWorldHandler = new NamedWorldHandler.DefaultNamedWorldHandler();

    /**
     * This manager allows registering custom {@link RemoteHandler}s for remote-module. Please be careful to use unique
     * IDs when registering handlers.
     * 
     * Using this instance to register handlers is deprecated. Use the {@link FERemoteHandler} annotation instead.
     */
    @Deprecated
    public static RemoteManager remoteManager = new RemoteManager.DefaultRemoteHandlerManager();

    /**
     * The FE internal event-bus
     */
    public static final EventBus FE_EVENTBUS = new EventBus();

    public static EventBus getFEEventBus()
    {
        return FE_EVENTBUS;
    }

    /**
     * Use this annotation to mark classes where static methods with other FE annotations might be.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.TYPE })
    public @interface ForgeEssentialsRegistrar
    {
        String ident();
    }

}
