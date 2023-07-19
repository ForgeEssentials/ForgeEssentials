package com.forgeessentials.auth;

import java.util.UUID;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.commons.network.NetworkUtils;
import com.forgeessentials.commons.network.packets.Packet06AuthLogin;
import com.forgeessentials.commons.network.packets.Packet09AuthRequest;
import com.forgeessentials.util.CommandUtils;
import com.forgeessentials.util.CommandUtils.CommandInfo;
import com.forgeessentials.util.events.ServerEventHandler;
import com.forgeessentials.util.events.player.FEPlayerEvent.ClientHandshakeEstablished;
import com.forgeessentials.util.events.player.PlayerAuthLoginEvent;
import com.forgeessentials.util.events.player.PlayerAuthLoginEvent.Success.Source;
import com.forgeessentials.util.events.player.PlayerMoveEvent;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.output.logger.LoggingHandler;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

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
        else
            unregister();
    }

    private static boolean notPlayer(Object player)
    {
        return player == null || !(player instanceof PlayerEntity) || player instanceof FakePlayer;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void playerMoveEvent(PlayerMoveEvent event)
    {
        if (!ModuleAuth.isEnabled() || notPlayer(event.getPlayer()))
            return;

        if (ModuleAuth.canMoveWithoutLogin
                || (event.before.getX() == event.after.getX() && event.before.getZ() == event.after.getZ()))
        {
            return;
        }
        if (!ModuleAuth.isAuthenticated(event.getPlayer()))
        {
            event.setCanceled(true);
            ChatOutputHandler.chatError(event.getPlayer().createCommandSourceStack(),
                    "Login required. Try /auth help.");
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void serverChatEvent(ServerChatEvent event)
    {
        if (!ModuleAuth.isEnabled() || notPlayer(event.getPlayer()))
            return;
        if (!ModuleAuth.isAuthenticated(event.getPlayer()))
        {
            event.setCanceled(true);
            ChatOutputHandler.chatError(event.getPlayer().createCommandSourceStack(),
                    "Login required. Try /auth help.");
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void commandEvent(CommandEvent event)
    {
        if (!ModuleAuth.isEnabled() || notPlayer(event.getParseResults().getContext().getSource().getEntity()))
            return;
        if (event.getParseResults().getContext().getNodes().isEmpty())
            return;
        CommandInfo info = CommandUtils.getCommandInfo(event);
        PlayerEntity player = (PlayerEntity) info.getSource().getEntity();
        if (!ModuleAuth.isAuthenticated(player) && !ModuleAuth.isGuestCommand(info))
        {
            event.setCanceled(true);
            ChatOutputHandler.chatError(player.createCommandSourceStack(), "Login required. Try /auth help.");
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void playerInteractEvent(PlayerInteractEvent event)
    {
        if (!ModuleAuth.isEnabled() || notPlayer(event.getPlayer()))
            return;
        if (!ModuleAuth.isAuthenticated(event.getPlayer()))
        {
            event.setCanceled(true);
            ChatOutputHandler.chatError(event.getPlayer().createCommandSourceStack(),
                    "Login required. Try /auth help.");
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void entityInteractEvent(PlayerInteractEvent.EntityInteract event)
    {
        if (!ModuleAuth.isEnabled() || notPlayer(event.getPlayer()))
            return;
        if (!ModuleAuth.isAuthenticated(event.getPlayer()))
        {
            event.setCanceled(true);
            ChatOutputHandler.chatError(event.getPlayer().createCommandSourceStack(),
                    "Login required. Try /auth help.");
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void minecartInteractEvent(PlayerInteractEvent.EntityInteractSpecific event)
    {
        if (!ModuleAuth.isEnabled() || notPlayer(event.getPlayer()))
            return;
        if (!ModuleAuth.isAuthenticated(event.getPlayer()))
        {
            event.setCanceled(true);
            ChatOutputHandler.chatError(event.getPlayer().createCommandSourceStack(),
                    "Login required. Try /auth help.");
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void itemTossEvent(ItemTossEvent event)
    {
        if (!ModuleAuth.isEnabled() || notPlayer(event.getPlayer()))
            return;
        if (!ModuleAuth.isAuthenticated(event.getPlayer()))
        {
            ChatOutputHandler.chatError(event.getPlayer().createCommandSourceStack(),
                    "Login required. Try /auth help.");
            // add the item back to the inventory
            ItemStack stack = event.getEntityItem().getItem();
            event.getPlayer().inventory.add(stack);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void entityItemPickupEvent(EntityItemPickupEvent event)
    {
        if (!ModuleAuth.isEnabled() || notPlayer(event.getPlayer()))
            return;
        if (!ModuleAuth.isAuthenticated(event.getPlayer()))
        {
            event.setCanceled(true);
            ChatOutputHandler.chatError(event.getPlayer().createCommandSourceStack(),
                    "Login required. Try /auth help.");
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void livingHurtEvent(LivingHurtEvent event)
    {
        if (!ModuleAuth.isEnabled() || notPlayer(event.getEntityLiving()))
            return;
        PlayerEntity player = (PlayerEntity) event.getEntityLiving();
        if (!ModuleAuth.isAuthenticated(player))
        {
            event.setCanceled(true);
            ChatOutputHandler.chatError(player.createCommandSourceStack(), "Login required. Try /auth help.");
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void attackEntityEvent(AttackEntityEvent event)
    {
        if (!ModuleAuth.isEnabled() || notPlayer(event.getPlayer()))
            return;
        if (!ModuleAuth.isAuthenticated(event.getPlayer()))
        {
            event.setCanceled(true);
            ChatOutputHandler.chatError(event.getPlayer().createCommandSourceStack(),
                    "Login required. Try /auth help.");
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerOpenContainer(PlayerContainerEvent event)
    {
        if (!ModuleAuth.isEnabled() || notPlayer(event.getPlayer()))
            return;

        if (!ModuleAuth.isAuthenticated(event.getPlayer()))
        {
            event.setResult(Result.DENY);
            ChatOutputHandler.chatError(event.getPlayer().createCommandSourceStack(),
                    "Login required. Try /auth help.");
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void playerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent event)
    {
        if (!ModuleAuth.isEnabled())
            return;
        if (!ModuleAuth.isRegistered(event.getPlayer().getGameProfile().getId()))
        {
            ChatOutputHandler.chatError(event.getPlayer().createCommandSourceStack(),
                    "Registration required. Try /auth help.");
        }

        if (!APIRegistry.perms.checkPermission(event.getPlayer(), "fe.auth.isVIP"))
        {
            int onlinePlayers = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayerCount();
            int availableSlots = ServerLifecycleHooks.getCurrentServer().getPlayerList().getMaxPlayers() - vipSlots
                    - reservedSlots;
            if (onlinePlayers >= availableSlots)
            {
                ((ServerPlayerEntity) event.getPlayer()).connection
                        .disconnect(new StringTextComponent(nonVipKickMessage));
            }
        }
    }

    @SubscribeEvent
    public void playerLoggedOutEvent(PlayerEvent.PlayerLoggedOutEvent event)
    {
        ModuleAuth.deauthenticate(event.getPlayer().getGameProfile().getId());
    }

    // autologin

    @SubscribeEvent
    public void onClientHandshake(ClientHandshakeEstablished e)
    {
        if (!ModuleAuth.isEnabled())
            return;
        if (ModuleAuth.isRegistered(e.getPlayer().getGameProfile().getId()) && !ModuleAuth.isAuthenticated(e.getPlayer()))
        {
            NetworkUtils.sendTo(new Packet06AuthLogin(), (ServerPlayerEntity) e.getPlayer());
        }
    }

    @SubscribeEvent
    public void onAuthLogin(PlayerAuthLoginEvent.Success e)
    {
        if (e.source == Source.COMMAND && ModuleAuth.allowAutoLogin)
        {
            UUID token = UUID.randomUUID();
            NetworkUtils.sendTo(new Packet09AuthRequest(token.toString()), (ServerPlayerEntity) e.getPlayer());
            PasswordManager.addSession(e.getPlayer().getGameProfile().getId(), token);
            ChatOutputHandler.chatConfirmation(e.getPlayer(), "AutoAuth Login Successful.");
        }
        APIRegistry.scripts.runEventScripts(ModuleAuth.SCRIPT_KEY_SUCCESS, e.getPlayer().createCommandSourceStack());
    }

    @SubscribeEvent
    public void onAuthLoginFail(PlayerAuthLoginEvent.Failure e)
    {
        APIRegistry.scripts.runEventScripts(ModuleAuth.SCRIPT_KEY_FAILURE, e.getPlayer().createCommandSourceStack());
    }

}
