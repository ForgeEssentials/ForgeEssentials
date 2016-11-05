package com.forgeessentials.commands.util;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.FEApi;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.commands.player.CommandAFK;
import com.forgeessentials.util.ChatUtil;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.TaskRegistry;
import com.forgeessentials.util.Translator;
import com.forgeessentials.util.Utils;
import com.forgeessentials.util.events.FEPlayerEvent.PlayerAFKEvent;
import com.forgeessentials.util.events.PlayerMoveEvent;
import com.forgeessentials.util.events.ServerEventHandler;
import com.forgeessentials.util.output.ChatOutputHandler;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

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
            Integer autoTime = Utils.tryParseInt(APIRegistry.perms.getUserPermissionProperty(pi.ident, CommandAFK.PERM_AUTOTIME));
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

        if (APIRegistry.perms.checkUserPermission(player, CommandAFK.PERM_AUTOKICK))
        {
            player.getPlayerMP().playerNetServerHandler.kickPlayerFromServer(Translator.translate("You have been kicked for being AFK"));
            return;
        }

        PlayerAFKEvent event = new PlayerAFKEvent(player.getPlayerMP(), true);
        FEApi.getFEEventBus().post(event);

        player.getPlayerMP().capabilities.disableDamage = true;
        if (APIRegistry.perms.checkUserPermission(player, CommandAFK.PERM_ANNOUNCE))
            ChatUtil.broadcast(ChatUtil.confirmation(Translator.format("Player %s is now AFK", player.getUsernameOrUuid())));
        else
            ChatUtil.chatConfirmation(player.getPlayer(), Translator.translate("You are now AFK"));

        afkPlayers.add(player);
    }

    public void clearAfk(UserIdent player)
    {
        if (!isAfk(player))
            return;
        PlayerAFKEvent event = new PlayerAFKEvent(player.getPlayerMP(), false);
        FEApi.getFEEventBus().post(event);

        switch (player.getPlayerMP().theItemInWorldManager.getGameType())
        {
        case NOT_SET:
        case SURVIVAL:
        case ADVENTURE:
            player.getPlayerMP().capabilities.disableDamage = false;
            break;
        default:
            break;
        }

        if (APIRegistry.perms.checkUserPermission(player, CommandAFK.PERM_ANNOUNCE))
            ChatUtil.broadcast(ChatUtil.confirmation(Translator.format("Player %s is not AFK any more", player.getUsernameOrUuid())));
        else
            ChatUtil.chatConfirmation(player.getPlayer(), Translator.translate("You are not AFK any more"));

        afkPlayers.remove(player);
    }

    public void playerActive(EntityPlayerMP player)
    {
        PlayerInfo pi = PlayerInfo.get(player);
        pi.setActive();
        clearAfk(pi.ident);
    }

    public static void checkAfkMessage(ICommandSender target, IChatComponent message)
    {
        if (!(target instanceof EntityPlayerMP))
            return;
        UserIdent targetIdent = UserIdent.get((EntityPlayerMP) target);
        if (target instanceof EntityPlayerMP && isAfk(targetIdent))
        {
            ChatUtil.notification(Translator.format("Player %s is currently AFK", targetIdent.getUsernameOrUuid()));
            return;
        }
        String msg = message.getUnformattedText().toLowerCase();
        for (UserIdent player : afkPlayers)
            if (msg.contains(player.getUsernameOrUuid().toLowerCase()))
                ChatUtil.notification(Translator.format("Player %s is currently AFK", player.getUsernameOrUuid()));
    }

    /* ------------------------------------------------------------ */

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void playerMoveEvent(PlayerMoveEvent event)
    {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient())
            return;
        playerActive((EntityPlayerMP) event.entityPlayer);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void playerMoveEvent(PlayerInteractEvent event)
    {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient())
            return;
        playerActive((EntityPlayerMP) event.entityPlayer);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void chatEvent(ServerChatEvent event)
    {
        playerActive(event.player);
        String msg = event.component.getUnformattedText().toLowerCase();
        for (UserIdent player : afkPlayers)
            if (msg.contains(player.getUsernameOrUuid().toLowerCase()))
                ChatUtil.notification(Translator.format("Player %s is currently AFK", player.getUsernameOrUuid()));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void commandEvent(CommandEvent event)
    {
        if (event.command instanceof CommandAFK)
            return;
        if (event.sender instanceof EntityPlayerMP)
            playerActive((EntityPlayerMP) event.sender);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void playerLogin(PlayerLoggedInEvent event)
    {
        afkPlayers.remove(UserIdent.get(event.player));

        PlayerInfo pi = PlayerInfo.get(event.player);
        if (!pi.checkTimeout("tempban"))
        {
            pi.ident.getPlayerMP().playerNetServerHandler.kickPlayerFromServer(Translator.format("You are still banned for %s",
                    ChatOutputHandler.formatTimeDurationReadable(pi.getRemainingTimeout("tempban") / 1000, true)));
        }
    }

}
