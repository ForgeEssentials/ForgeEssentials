package com.forgeessentials.util;

public class ConnectionMonitor
{
    public static boolean isIRCConnected = false;

    public static String getIRCConnectionStatus()
    {
        return (isIRCConnected ? "online" : "offline");

    }
}
