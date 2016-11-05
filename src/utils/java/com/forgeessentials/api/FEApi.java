package com.forgeessentials.api;

import com.forgeessentials.api.UserIdent.NpcUserIdent;
import com.forgeessentials.api.UserIdent.ServerUserIdent;
import com.forgeessentials.api.permissions.IPermissionProviderBase;

import cpw.mods.fml.common.eventhandler.EventBus;

public class FEApi
{

    /**
     * The FE internal event-bus
     */
    public static final EventBus FE_EVENTBUS = new EventBus();

    public static final ServerUserIdent IDENT_SERVER = UserIdent.getServer("fefefefe-fefe-fefe-fefe-fefefefefefe", "$SERVER");
    public static final ServerUserIdent IDENT_RCON = UserIdent.getServer("fefefefe-fefe-fefe-fefe-fefefefefecc", "$RCON");
    public static final ServerUserIdent IDENT_CMDBLOCK = UserIdent.getServer("fefefefe-fefe-fefe-fefe-fefefefefecb", "$COMMANDBLOCK");
    public static final NpcUserIdent IDENT_NPC = UserIdent.getNpc(null);

    public static IPermissionProviderBase perms;

    /**
     * Allows identifying worlds by name. If you change this handler, remember to call the old one in your
     * implementation!
     */
    public static NamedWorldHandler namedWorldHandler = new NamedWorldHandler.DefaultNamedWorldHandler();

    public static EventBus getFEEventBus()
    {
        return FE_EVENTBUS;
    }
}
