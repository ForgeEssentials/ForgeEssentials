package com.forgeessentials.auth;

import com.forgeessentials.util.OutputHandler;
import cpw.mods.fml.common.FMLCommonHandler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.TimerTask;

public class VanillaServiceChecker extends TimerTask {
    private boolean online = true;
    private boolean oldOnline;

    private static final String MC_SERVER = "http://session.minecraft.net/game/checkserver.jsp";
    private static final String ONLINE = "NOT YET";

    public VanillaServiceChecker()
    {
        online = oldOnline = check();
        OutputHandler.felog
                .info("VanillaServiceChecker initialized. Vanilla online mode: '" + ModuleAuth.vanillaMode() + "' Mojang login servers: '" + online + "'");
    }

    @Override
    public void run()
    {
        oldOnline = online;
        online = check();

        if (oldOnline != online)
        {
            FMLCommonHandler.instance().getSidedDelegate().getServer().setOnlineMode(online);
            ModuleAuth.onStatusChange();
        }
    }

    private static boolean check()
    {
        try
        {
            URL url = new URL(MC_SERVER);
            BufferedReader stream = new BufferedReader(new InputStreamReader(url.openStream()));
            String input = stream.readLine();
            stream.close();

            return ONLINE.equals(input);
        }
        catch (Exception e)
        {
            return false;
        }
    }

    public boolean isServiceUp()
    {
        return online;
    }

}
