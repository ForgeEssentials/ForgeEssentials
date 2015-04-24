package com.forgeessentials.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.forgeessentials.util.events.FEPlayerEvent.PlayerNotAFKEvent;
import com.forgeessentials.util.events.FEPlayerEvent.PlayerWentAFKEvent;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.permissions.PermissionsManager;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.commands.util.AFKdata;
import com.forgeessentials.commands.util.CommandsEventHandler;
import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.OutputHandler;

public class CommandAFK extends FEcmdModuleCommands {
    public static CommandAFK instance;
    public static List<UUID> afkList = new ArrayList<UUID>();
    // Config
    public static int warmup = 5;
    public static String outMessage, inMessage, selfOutMessage, selfInMessage;
    public final String NOTICEPERM = getPermissionNode() + ".notice";

    public CommandAFK()
    {
        instance = this;
    }

    @Override
    public void loadConfig(Configuration config, String category)
    {
        warmup = config.get(category, "warmup", 5, "Time in sec. you have to stand still to activate AFK.").getInt();
        String messages = category + ".messages";
        outMessage = config.get(messages, "outMessage", "Player %s is now away").getString();
        inMessage = config.get(messages, "inMessage", "Player %s is no longer away").getString();
        selfOutMessage = config.get(messages, "selfOutMessage", "You are now away").getString();
        selfInMessage = config.get(messages, "selfInMessage", "You are no longer away").getString();
    }

    @Override
    public String getCommandName()
    {
        return "afk";
    }

    @Override
    public void processCommandPlayer(EntityPlayerMP sender, String[] args)
    {
        CommandsEventHandler.afkListToAdd.add(new AFKdata(sender));
        OutputHandler.chatConfirmation(sender, Translator.format("Stand still for %d seconds.", warmup));
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    public void abort(AFKdata afkData)
    {
        if (!afkData.player.capabilities.isCreativeMode)
        {
            afkData.player.capabilities.disableDamage = false;
        }
        afkData.player.sendPlayerAbilities();
        afkList.remove(afkData.player.getPersistentID());
        CommandsEventHandler.afkListToRemove.add(afkData);

        if (PermissionsManager.checkPermission(afkData.player, NOTICEPERM))
        {
            OutputHandler.sendMessageToAll(new ChatComponentText(Translator.format(inMessage, afkData.player.getDisplayName())));
        }
        else
        {
            OutputHandler.chatConfirmation(afkData.player, selfInMessage);
        }
        APIRegistry.getFEEventBus().post(new PlayerNotAFKEvent(afkData.player));
    }

    public void makeAFK(AFKdata afkData)
    {
        afkData.player.capabilities.disableDamage = true;
        afkData.player.sendPlayerAbilities();
        afkList.add(afkData.player.getPersistentID());

        if (PermissionsManager.checkPermission(afkData.player, NOTICEPERM))
        {
            OutputHandler.sendMessageToAll(new ChatComponentText(Translator.format(outMessage, afkData.player.getDisplayName())));
        }
        else
        {
            OutputHandler.chatConfirmation(afkData.player, selfOutMessage);
        }
        APIRegistry.getFEEventBus().post(new PlayerWentAFKEvent(afkData.player));
    }

    @Override
    public void registerExtraPermissions()
    {
        APIRegistry.perms.registerPermission(NOTICEPERM, RegisteredPermValue.TRUE);
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {
        return RegisteredPermValue.TRUE;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/afk Mark yourself as away.";
    }
}
