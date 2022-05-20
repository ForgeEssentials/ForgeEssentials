package com.forgeessentials.client.init;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * User: The Grey Ghost
 * Date: 24/12/2014
 *
 * Register our commands when the server starts up.
 *
 * Don't forget to register this class on the MinecraftForge.EVENT_BUS.
 */
public class RegisterCommandEvent
{
  @SubscribeEvent
  public static void onRegisterCommandEvent(RegisterCommandsEvent event) {
	  CommandInit.registerCommands(event);
  }
}