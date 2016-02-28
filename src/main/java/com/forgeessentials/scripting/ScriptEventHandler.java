package com.forgeessentials.scripting;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerPostInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStopEvent;
import com.forgeessentials.util.events.FEPlayerEvent.PlayerAFKEvent;
import com.forgeessentials.util.events.ServerEventHandler;

/**
 * Miscellaneous event handler for scripts.
 *
 * Any event that needs to run scripts should have a handler here.
 */
public class ScriptEventHandler extends ServerEventHandler
{
    public static final String SCRIPTKEY_SERVERSTART = "start";
    public static final String SCRIPTKEY_SERVERSTOP = "stop";
    public static final String SCRIPTKEY_PLAYERLOGIN = "login";
    public static final String SCRIPTKEY_PLAYERLOGOUT = "logout";
    public static final String SCRIPTKEY_PLAYERDEATH = "death";
    public static final String SCRIPTKEY_CRON = "cron";
    public static final String SCRIPTKEY_PLAYERAFK = "afk";
    public static final String SCRIPTKEY_PLAYERAFKRETURN = "afkreturn";

    public ScriptEventHandler()
    {
        super();
        register();
        APIRegistry.scripts.addScriptType(SCRIPTKEY_SERVERSTART);
        APIRegistry.scripts.addScriptType(SCRIPTKEY_SERVERSTOP);
        APIRegistry.scripts.addScriptType(SCRIPTKEY_PLAYERLOGIN);
        APIRegistry.scripts.addScriptType(SCRIPTKEY_PLAYERLOGOUT);
        APIRegistry.scripts.addScriptType(SCRIPTKEY_PLAYERDEATH);
        APIRegistry.scripts.addScriptType(SCRIPTKEY_CRON);
        APIRegistry.scripts.addScriptType(SCRIPTKEY_PLAYERAFK);
        APIRegistry.scripts.addScriptType(SCRIPTKEY_PLAYERAFKRETURN);
    }
    @SubscribeEvent
    public void serverStarted(FEModuleServerPostInitEvent event)
    {
        APIRegistry.scripts.runEventScripts(SCRIPTKEY_SERVERSTART, null);
    }

    @SubscribeEvent
    public void serverStopping(FEModuleServerStopEvent event)
    {
        APIRegistry.scripts.runEventScripts(SCRIPTKEY_SERVERSTOP, null);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void playerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent event)
    {
        APIRegistry.scripts.runEventScripts(SCRIPTKEY_PLAYERLOGIN, event.player);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void playerLoggedOutEvent(PlayerEvent.PlayerLoggedOutEvent event)
    {
        APIRegistry.scripts.runEventScripts(SCRIPTKEY_PLAYERLOGOUT, event.player);
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onPlayerDeath(LivingDeathEvent event)
    {
        if (event.entityLiving instanceof EntityPlayerMP)
        {
            APIRegistry.scripts.runEventScripts(SCRIPTKEY_PLAYERDEATH, (EntityPlayerMP) event.entityLiving);
        }
    }

    @SubscribeEvent
    public void playerAFK(PlayerAFKEvent e)
    {
        if (e.afk)
            APIRegistry.scripts.runEventScripts(SCRIPTKEY_PLAYERAFK, e.getPlayer());
        else
            APIRegistry.scripts.runEventScripts(SCRIPTKEY_PLAYERAFKRETURN, e.getPlayer());
    }

}
