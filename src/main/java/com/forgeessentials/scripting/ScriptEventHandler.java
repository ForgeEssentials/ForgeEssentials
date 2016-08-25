package com.forgeessentials.scripting;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerPostInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStopEvent;
import com.forgeessentials.util.events.FEPlayerEvent.PlayerAFKEvent;
import com.forgeessentials.util.events.ServerEventHandler;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;

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
    public static final String SCRIPTKEY_PLAYERRESPAWN = "respawn";
    public static final String SCRIPTKEY_CRON = "cron";
    public static final String SCRIPTKEY_PLAYERAFK = "afk";
    public static final String SCRIPTKEY_PLAYERAFKRETURN = "afkreturn";
    public static final String SCRIPTKEY_PLAYERINTERACT_LEFT = "interact_left";
    public static final String SCRIPTKEY_PLAYERINTERACT_RIGHT = "interact_right";
    public static final String SCRIPTKEY_PLAYERINTERACT_USE = "interact_use";
    public static final String SCRIPTKEY_PLAYERSLEEP = "sleep";
    public static final String SCRIPTKEY_PLAYERWAKE = "wake";

    public ScriptEventHandler()
    {
        super();
        register();
        APIRegistry.scripts.addScriptType(SCRIPTKEY_SERVERSTART);
        APIRegistry.scripts.addScriptType(SCRIPTKEY_SERVERSTOP);
        APIRegistry.scripts.addScriptType(SCRIPTKEY_PLAYERLOGIN);
        APIRegistry.scripts.addScriptType(SCRIPTKEY_PLAYERLOGOUT);
        APIRegistry.scripts.addScriptType(SCRIPTKEY_PLAYERDEATH);
        APIRegistry.scripts.addScriptType(SCRIPTKEY_PLAYERRESPAWN);
        APIRegistry.scripts.addScriptType(SCRIPTKEY_CRON);
        APIRegistry.scripts.addScriptType(SCRIPTKEY_PLAYERAFK);
        APIRegistry.scripts.addScriptType(SCRIPTKEY_PLAYERAFKRETURN);
        APIRegistry.scripts.addScriptType(SCRIPTKEY_PLAYERINTERACT_LEFT);
        APIRegistry.scripts.addScriptType(SCRIPTKEY_PLAYERINTERACT_RIGHT);
        APIRegistry.scripts.addScriptType(SCRIPTKEY_PLAYERINTERACT_USE);
        APIRegistry.scripts.addScriptType(SCRIPTKEY_PLAYERSLEEP);
        APIRegistry.scripts.addScriptType(SCRIPTKEY_PLAYERWAKE);
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

    @SubscribeEvent
    public void playerRespawn(PlayerEvent.PlayerRespawnEvent e)
    {
        APIRegistry.scripts.runEventScripts(SCRIPTKEY_PLAYERRESPAWN, e.player);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void playerInteract(PlayerInteractEvent e)
    {
        if (!(e.entityPlayer instanceof FakePlayer))
        {
            if (e.action.equals(PlayerInteractEvent.Action.LEFT_CLICK_BLOCK))
                APIRegistry.scripts.runEventScripts(SCRIPTKEY_PLAYERINTERACT_LEFT, e.entityPlayer);
            else if (e.action.equals(PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK))
                APIRegistry.scripts.runEventScripts(SCRIPTKEY_PLAYERINTERACT_RIGHT, e.entityPlayer);
            else if (e.action.equals(PlayerInteractEvent.Action.RIGHT_CLICK_AIR))
                APIRegistry.scripts.runEventScripts(SCRIPTKEY_PLAYERINTERACT_USE, e.entityPlayer);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void playerSleep(PlayerSleepInBedEvent e)
    {
        if (e.result == null || e.result.equals(EntityPlayer.EnumStatus.OK))
            APIRegistry.scripts.runEventScripts(SCRIPTKEY_PLAYERSLEEP, e.entityPlayer);
    }

    @SubscribeEvent
    public void playerWake(PlayerWakeUpEvent e)
    {
        APIRegistry.scripts.runEventScripts(SCRIPTKEY_PLAYERWAKE, e.entityPlayer);
    }

}
