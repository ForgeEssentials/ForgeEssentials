package com.forgeessentials.commands.util;

import java.util.HashSet;
import java.util.Set;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.commands.player.CommandAFK;
import com.forgeessentials.core.misc.TaskRegistry;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.ServerUtil;
import com.forgeessentials.util.events.ServerEventHandler;
import com.forgeessentials.util.events.player.FEPlayerEvent.PlayerAFKEvent;
import com.forgeessentials.util.events.player.PlayerMoveEvent;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.BaseComponent;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;

public class ModuleCommandsEventHandler extends ServerEventHandler implements Runnable
{

    private static final Set<UserIdent> afkPlayers = new HashSet<>();

    public ModuleCommandsEventHandler()
    {
        TaskRegistry.scheduleRepeated(this, 1000 * 2);
    }

    @Override
    public void run()
    {
        for (PlayerInfo pi : PlayerInfo.getAll())
        {
            if (!pi.ident.hasPlayer())
                continue;
            Integer autoTime = ServerUtil.tryParseInt(pi.ident.getPermissionProperty(CommandAFK.PERM_AUTOTIME));
            if (autoTime != null && autoTime > 10)
                if (pi.getInactiveTime() / 1000 > autoTime)
                    setAfk(pi.ident);
        }
    }

    /* ------------------------------------------------------------ */

    public static boolean isAfk(UserIdent uuid)
    {
        return afkPlayers.contains(uuid);
    }

    public void setAfk(UserIdent player)
    {
        if (isAfk(player))
            return;

        if (player.checkPermission(CommandAFK.PERM_AUTOKICK))
        {
            player.getPlayerMP().connection
                    .disconnect(new TextComponent(Translator.translate("You have been kicked for being AFK")));
            return;
        }

        PlayerAFKEvent event = new PlayerAFKEvent(player.getPlayerMP(), true);
        APIRegistry.getFEEventBus().post(event);

        player.getPlayer().getAbilities().invulnerable = true;
        if (player.checkPermission(CommandAFK.PERM_ANNOUNCE))
            ChatOutputHandler.broadcast(ChatOutputHandler
                    .confirmation(Translator.format("Player %s is now AFK", player.getUsernameOrUuid())));
        else
            ChatOutputHandler.chatConfirmation(player.getPlayer().createCommandSourceStack(),
                    Translator.translate("You are now AFK"));

        afkPlayers.add(player);
    }

    public void clearAfk(UserIdent player)
    {
        if (!isAfk(player))
            return;
        PlayerAFKEvent event = new PlayerAFKEvent(player.getPlayerMP(), false);
        APIRegistry.getFEEventBus().post(event);

        switch (player.getPlayerMP().gameMode.getGameModeForPlayer())
        {
        case SURVIVAL:
        case ADVENTURE:
            player.getPlayerMP().getAbilities().invulnerable = false;
            break;
        default:
            break;
        }

        if (player.checkPermission(CommandAFK.PERM_ANNOUNCE))
            ChatOutputHandler.broadcast(ChatOutputHandler
                    .confirmation(Translator.format("Player %s is not AFK any more", player.getUsernameOrUuid())));
        else
            ChatOutputHandler.chatConfirmation(player.getPlayer().createCommandSourceStack(),
                    Translator.translate("You are not AFK any more"));

        afkPlayers.remove(player);
    }

    public void playerActive(ServerPlayer player)
    {
        PlayerInfo pi = PlayerInfo.get(player);
        pi.setActive();
        clearAfk(pi.ident);
    }

    public static void checkAfkMessage(CommandSourceStack target, BaseComponent message) throws CommandSyntaxException
    {
        if (!(target.getEntity() instanceof ServerPlayer))
            return;
        UserIdent targetIdent = UserIdent.get((ServerPlayer) target.getEntity());
        if (target.getEntity() instanceof ServerPlayer && isAfk(targetIdent))
        {
            ChatOutputHandler
                    .notification(Translator.format("Player %s is currently AFK", targetIdent.getUsernameOrUuid()));
            return;
        }
        String msg = message.getContents().toLowerCase();
        for (UserIdent player : afkPlayers)
            if (msg.contains(player.getUsernameOrUuid().toLowerCase()))
                ChatOutputHandler
                        .notification(Translator.format("Player %s is currently AFK", player.getUsernameOrUuid()));
    }

    /* ------------------------------------------------------------ */

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void playerMoveEvent(PlayerMoveEvent event)
    {
        if (FMLEnvironment.dist.isClient())
            return;
        playerActive((ServerPlayer) event.getPlayer());
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void playerMoveEvent(PlayerInteractEvent event)
    {
        if (FMLEnvironment.dist.isClient())
            return;
        playerActive((ServerPlayer) event.getPlayer());
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void chatEvent(ServerChatEvent event)
    {
        playerActive(event.getPlayer());
        String msg = event.getComponent().getContents().toLowerCase();
        for (UserIdent player : afkPlayers)
            if (msg.contains(player.getUsernameOrUuid().toLowerCase()))
                ChatOutputHandler
                        .notification(Translator.format("Player %s is currently AFK", player.getUsernameOrUuid()));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void commandEvent(CommandEvent event)
    {
        if (event.getParseResults().getContext().getSource().getEntity() != null
                && event.getParseResults().getContext().getSource().getEntity() instanceof ServerPlayer)
            playerActive((ServerPlayer) event.getParseResults().getContext().getSource().getEntity());
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void playerLogin(PlayerLoggedInEvent event)
    {
        afkPlayers.remove(UserIdent.get(event.getPlayer()));

        PlayerInfo pi = PlayerInfo.get(event.getPlayer());
        if (!pi.checkTimeout("tempban"))
        {
            pi.ident.getPlayerMP().connection.disconnect(new TextComponent(Translator.format(
                    "You are still banned for %s",
                    ChatOutputHandler.formatTimeDurationReadable(pi.getRemainingTimeout("tempban") / 1000, true))));
        }
    }

}
