package com.forgeessentials.auth;

import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.minecart.MinecartInteractEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.server.permission.PermissionAPI;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.commons.network.NetworkUtils;
import com.forgeessentials.commons.network.Packet6AuthLogin;
import com.forgeessentials.util.events.FEPlayerEvent.ClientHandshakeEstablished;
import com.forgeessentials.util.events.PlayerAuthLoginEvent;
import com.forgeessentials.util.events.PlayerAuthLoginEvent.Success.Source;
import com.forgeessentials.util.events.PlayerMoveEvent;
import com.forgeessentials.util.events.ServerEventHandler;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.output.LoggingHandler;

public class AuthEventHandler extends ServerEventHandler
{

    public static String playerBannedMessage;
    
    public static String nonVipKickMessage;
    
    public static int vipSlots;
    
    public static int reservedSlots;

    public AuthEventHandler()
    {
        super();
        LoggingHandler.felog.info("FEauth initialized. Enabled: " + ModuleAuth.isEnabled());
    }

    protected void enable(boolean status)
    {
        if (status)
        {
            register();
        }
        else unregister();
    }

    public static boolean isPlayer(Object player)
    {
        return player != null && player instanceof EntityPlayerMP;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void playerMoveEvent(PlayerMoveEvent event)
    {
        if (!ModuleAuth.isEnabled() || !isPlayer(event.getEntityPlayer()))
            return;

        if (ModuleAuth.canMoveWithoutLogin || (event.before.getX() == event.after.getX() && event.before.getZ() == event.after.getZ()))
        {
            return;
        }
        if (!ModuleAuth.isAuthenticated(event.getEntityPlayer()))
        {
            event.setCanceled(true);
            ChatOutputHandler.chatError(event.getEntityPlayer(), "Login required. Try /auth help.");
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void serverChatEvent(ServerChatEvent event)
    {
        if (!ModuleAuth.isEnabled() || !isPlayer(event.getPlayer()))
            return;
        if (!ModuleAuth.isAuthenticated(event.getPlayer()))
        {
            event.setCanceled(true);
            ChatOutputHandler.chatError(event.getPlayer(), "Login required. Try /auth help.");
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void commandEvent(CommandEvent event)
    {
        if (!ModuleAuth.isEnabled() || !isPlayer(event.getSender()))
            return;
        EntityPlayer player = (EntityPlayer) event.getSender();
        if (!ModuleAuth.isAuthenticated(player) && !ModuleAuth.isGuestCommand(event.getCommand()))
        {
            event.setCanceled(true);
            ChatOutputHandler.chatError(player, "Login required. Try /auth help.");
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void playerInteractEvent(PlayerInteractEvent event)
    {
        if (!ModuleAuth.isEnabled() || !isPlayer(event.getEntityPlayer()))
            return;
        if (!ModuleAuth.isAuthenticated(event.getEntityPlayer()))
        {
            event.setCanceled(true);
            ChatOutputHandler.chatError(event.getEntityPlayer(), "Login required. Try /auth help.");
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void entityInteractEvent(PlayerInteractEvent.EntityInteract event)
    {
        if (!ModuleAuth.isEnabled() || !isPlayer(event.getEntityPlayer()))
            return;
        if (!ModuleAuth.isAuthenticated(event.getEntityPlayer()))
        {
            event.setCanceled(true);
            ChatOutputHandler.chatError(event.getEntityPlayer(), "Login required. Try /auth help.");
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void minecartInteractEvent(MinecartInteractEvent event)
    {
        if (!ModuleAuth.isEnabled() || !isPlayer(event.getPlayer()))
            return;
        if (!ModuleAuth.isAuthenticated(event.getPlayer()))
        {
            event.setCanceled(true);
            ChatOutputHandler.chatError(event.getPlayer(), "Login required. Try /auth help.");
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void itemTossEvent(ItemTossEvent event)
    {
        if (!ModuleAuth.isEnabled() || !isPlayer(event.getPlayer()))
            return;
        if (!ModuleAuth.isAuthenticated(event.getPlayer()))
        {
            ChatOutputHandler.chatError(event.getPlayer(), "Login required. Try /auth help.");
            // add the item back to the inventory
            ItemStack stack = event.getEntityItem().getItem();
            event.getPlayer().inventory.addItemStackToInventory(stack);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void entityItemPickupEvent(EntityItemPickupEvent event)
    {
        if (!ModuleAuth.isEnabled() || !isPlayer(event.getEntityPlayer()))
            return;
        if (!ModuleAuth.isAuthenticated(event.getEntityPlayer()))
        {
            event.setCanceled(true);
            ChatOutputHandler.chatError(event.getEntityPlayer(), "Login required. Try /auth help.");
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void livingHurtEvent(LivingHurtEvent event)
    {
        if (!ModuleAuth.isEnabled() || !isPlayer(event.getEntityLiving()))
            return;
        EntityPlayerMP player = (EntityPlayerMP) event.getEntityLiving();
        if (!ModuleAuth.isAuthenticated(player))
        {
            event.setCanceled(true);
            ChatOutputHandler.chatError(player, "Login required. Try /auth help.");
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void attackEntityEvent(AttackEntityEvent event)
    {
        if (!ModuleAuth.isEnabled() || !isPlayer(event.getEntityPlayer()))
            return;
        if (!ModuleAuth.isAuthenticated(event.getEntityPlayer()))
        {
            event.setCanceled(true);
            ChatOutputHandler.chatError(event.getEntityPlayer(), "Login required. Try /auth help.");
        }
    }

    /*
     * @SubscribeEvent(priority = EventPriority.HIGHEST) public void onPlayerOpenContainer(PlayerOpenContainerEvent
     * event) { UUID username = event.getEntityPlayer();
     * 
     * if (!ModuleAuth.hasSession.contains(username)) { event.setResult(Result.DENY);
     * ChatOutputHandler.chatError(event.getEntityPlayer(), "Login required. Try /auth help."); } }
     */

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void playerLoggedInEvent(PlayerLoggedInEvent event)
    {
        if (!ModuleAuth.isEnabled())
            return;
        if (!ModuleAuth.isRegistered(event.player.getPersistentID()))
        {
            ChatOutputHandler.chatError(event.player, "Registration required. Try /auth help.");
        }
        else
        {
            ChatOutputHandler.chatError(event.player, "Login required. Try /auth help.");
        }

        if (!PermissionAPI.hasPermission(event.player, "fe.auth.isVIP"))
        {
            int onlinePlayers = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getCurrentPlayerCount();
            int availableSlots = FMLCommonHandler.instance().getMinecraftServerInstance().getMaxPlayers() - vipSlots - reservedSlots;
            if (onlinePlayers >= availableSlots)
            {
                ((EntityPlayerMP) event.player).connection.disconnect(new TextComponentTranslation(nonVipKickMessage));
            }
        }
    }

    @SubscribeEvent
    public void playerLoggedOutEvent(PlayerLoggedOutEvent event)
    {
        ModuleAuth.deauthenticate(event.player.getPersistentID());
    }

    // autologin

    @SubscribeEvent
    public void onClientHandshake(ClientHandshakeEstablished e)
    {
        if (!ModuleAuth.isEnabled())
            return;
        if (ModuleAuth.isRegistered(e.getPlayer().getPersistentID())  && !ModuleAuth.isAuthenticated(e.getPlayer()))
        {
            NetworkUtils.netHandler.sendTo(new Packet6AuthLogin(0, ""), e.getPlayer());
        }
    }

    @SubscribeEvent
    public void onAuthLogin(PlayerAuthLoginEvent.Success e)
    {
        if (e.source == Source.COMMAND && ModuleAuth.allowAutoLogin)
        {
            UUID token = UUID.randomUUID();
            NetworkUtils.netHandler.sendTo(new Packet6AuthLogin(2, token.toString()), e.getPlayer());
            PasswordManager.addSession(e.getPlayer().getPersistentID(), token);
        }
        APIRegistry.scripts.runEventScripts(ModuleAuth.SCRIPT_KEY_SUCCESS, e.getPlayer());
    }

    @SubscribeEvent
    public void onAuthLoginFail(PlayerAuthLoginEvent.Failure e)
    {
        APIRegistry.scripts.runEventScripts(ModuleAuth.SCRIPT_KEY_FAILURE, e.getPlayer());
    }

}
