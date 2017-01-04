package com.forgeessentials.jscripting.fewrapper;

import net.minecraftforge.server.permission.DefaultPermissionLevel;

public class JsPermissionLevel
{

    private JsPermissionLevel() {}

    public static final DefaultPermissionLevel TRUE = DefaultPermissionLevel.ALL;
    public static final DefaultPermissionLevel OP = DefaultPermissionLevel.OP;
    public static final DefaultPermissionLevel FALSE = DefaultPermissionLevel.NONE;

}
