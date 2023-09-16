package com.forgeessentials.util;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.api.permissions.WorldZone;
import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.core.commands.registration.FECommandParsingException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.context.CommandContext;

import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.TextComponent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class CommandContextParcer {
    public final CommandSource sender;
    public final ServerPlayerEntity senderPlayer;
    public final UserIdent ident;
    public final CommandContext<CommandSource> context;
    public final String methodParms;
    
    public CommandContextParcer(CommandContext<CommandSource> ctx, String params)
    {
    	this.context = ctx;
    	this.sender = context.getSource();
    	this.senderPlayer = CommandUtils.getServerPlayer(sender);
        this.ident = UserIdent.get(sender);
        this.methodParms = params;
    }

    public void sendMessage(String message)
    {
    	ChatOutputHandler.sendMessage(sender, message);
    }

    public void sendMessage(TextComponent message)
    {
    	ChatOutputHandler.sendMessage(sender, message);
    }

    public void confirm(String message, Object... args)
    {
    	ChatOutputHandler.chatConfirmation(sender, Translator.format(message, args));
    }

    public void notify(String message, Object... args)
    {
    	ChatOutputHandler.chatNotification(sender, Translator.format(message, args));
    }

    public void warn(String message, Object... args)
    {
    	ChatOutputHandler.chatWarning(sender, Translator.format(message, args));
    }

    public void error(String message, Object... args)
    {
    	ChatOutputHandler.chatError(sender, Translator.format(message, args));
    }


    public boolean hasPlayer()
    {
        return senderPlayer != null;
    }


    public UserIdent parsePlayer(String name, boolean mustExist, boolean mustBeOnline) throws FECommandParsingException
    {
    	return CommandUtils.parsePlayer(name, mustExist, mustBeOnline);
    }

    public boolean hasPermission(String perm)
    {
        if (sender.getEntity() instanceof PlayerEntity)
        return APIRegistry.perms.checkPermission(senderPlayer, perm);
        else return true;
    }

    /**
     * Parses a Time string in Minecraft time format.
     * @return
     * @throws FECommandParsingException
     */
    public Long mcParseTimeReadable(String timeStr) throws FECommandParsingException
    {
    	return CommandUtils.mcParseTimeReadable(timeStr);
    }
    public long parseTimeReadable(String timeStr) throws FECommandParsingException
    {
    	return CommandUtils.mcParseTimeReadable(timeStr);
    }

    public WorldPoint getSenderPoint()
    {
        CommandSource s = sender != null ? sender : ServerLifecycleHooks.getCurrentServer().createCommandSourceStack();
        return new WorldPoint(s.getLevel().dimension(), s.getPosition());
    }

    public WorldZone getWorldZone() throws FECommandParsingException
    {
        if (senderPlayer == null)
            throw new FECommandParsingException("Player needed");
        return APIRegistry.perms.getServerZone().getWorldZone(senderPlayer.level);
    }

    public void needsPlayer() throws FECommandParsingException
    {
        if (senderPlayer == null)
            throw new FECommandParsingException(FEPermissions.MSG_NO_CONSOLE_COMMAND);
    }
}
