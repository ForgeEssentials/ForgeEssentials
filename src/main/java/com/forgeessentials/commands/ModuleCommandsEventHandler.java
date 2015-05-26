package com.forgeessentials.commands;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.core.misc.TaskRegistry;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.events.FEPlayerEvent.PlayerAFKEvent;
import com.forgeessentials.util.events.PlayerMoveEvent;
import com.forgeessentials.util.events.ServerEventHandler;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

public class ModuleCommandsEventHandler extends ServerEventHandler implements Runnable
{

    private static Set<UUID> afkPlayers = new HashSet<>();

    @Override
    public void register()
    {
        super.register();
        TaskRegistry.getInstance().scheduleRepeated(this, 1000 * 2);
    }

    @Override
    public void run()
    {
        for (PlayerInfo pi : PlayerInfo.getAll())
        {
            if (!pi.ident.hasPlayer())
                continue;
            int autoTime = FunctionHelper.parseIntDefault(pi.ident.getPermissionProperty(CommandAFK.PERM_AUTOTIME), 60 * 2);
            if (pi.getInactiveTime() / 1000 > autoTime)
                setAfk(pi.ident);
        }
    }

    public boolean isAfk(UUID uuid)
    {
        return afkPlayers.contains(uuid);
    }

    public void setAfk(UserIdent player)
    {
        if (isAfk(player.getUuid()))
            return;

        if (player.checkPermission(CommandAFK.PERM_AUTOKICK))
        {
            player.getPlayerMP().playerNetServerHandler.kickPlayerFromServer(Translator.translate("You have been kicked for being AFK"));
            return;
        }

        PlayerAFKEvent event = new PlayerAFKEvent(player.getPlayerMP(), true);
        APIRegistry.getFEEventBus().post(event);

        player.getPlayerMP().capabilities.disableDamage = true;
        if (player.checkPermission(CommandAFK.PERM_ANNOUNCE))
            OutputHandler.broadcast(OutputHandler.confirmation(Translator.format("Player %s is now AFK", player.getUsernameOrUUID())));
        else
            OutputHandler.chatConfirmation(player.getPlayer(), Translator.translate("You are now AFK"));

        afkPlayers.add(player.getUuid());
    }

    public void clearAfk(UserIdent player)
    {
        if (!isAfk(player.getUuid()))
            return;
        PlayerAFKEvent event = new PlayerAFKEvent(player.getPlayerMP(), false);
        APIRegistry.getFEEventBus().post(event);

        player.getPlayerMP().capabilities.disableDamage = false;
        if (player.checkPermission(CommandAFK.PERM_ANNOUNCE))
            OutputHandler.broadcast(OutputHandler.confirmation(Translator.format("Player %s is not AFK any more", player.getUsernameOrUUID())));
        else
            OutputHandler.chatConfirmation(player.getPlayer(), Translator.translate("You are not AFK any more"));

        afkPlayers.remove(player.getUuid());
    }

    public void playerActive(EntityPlayerMP player)
    {
        PlayerInfo pi = PlayerInfo.get(player);
        pi.setActive();
        clearAfk(pi.ident);
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
        afkPlayers.remove(event.player.getPersistentID());

        PlayerInfo pi = PlayerInfo.get(event.player);
        if (!pi.checkTimeout("tempban"))
        {
            pi.ident.getPlayerMP().playerNetServerHandler.kickPlayerFromServer(Translator.format("You are still banned for %s",
                    FunctionHelper.formatDateTimeReadable(pi.getRemainingTimeout("tempban") / 1000, true)));
        }
    }
}
