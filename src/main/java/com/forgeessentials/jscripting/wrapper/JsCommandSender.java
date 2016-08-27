package com.forgeessentials.jscripting.wrapper;

import java.util.UUID;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.util.DoAsCommandSender;
import com.forgeessentials.util.output.ChatOutputHandler;

public class JsCommandSender
{

    private ICommandSender that;

    public JsCommandSender(ICommandSender sender)
    {
        this.that = sender;
    }

    public ICommandSender getThat()
    {
        return that;
    }

    public String getName()
    {
        return that.getCommandSenderName();
    }

    public JsEntityPlayer getPlayer()
    {
        return that instanceof EntityPlayer ? new JsEntityPlayer((EntityPlayer) that) : null;
    }

    public JsCommandSender doAs(UUID userId, boolean hideChatOutput)
    {
        UserIdent doAsUser = userId == null ? APIRegistry.IDENT_SERVER : UserIdent.get(userId);
        DoAsCommandSender result = new DoAsCommandSender(doAsUser, that);
        result.setHideChatMessages(hideChatOutput);
        return new JsCommandSender(result);
    }

    public JsCommandSender doAs(JsEntityPlayer plazer, boolean hideChatOutput)
    {
        return doAs(plazer == null ? null : plazer.getUuid(), hideChatOutput);
    }

    public void chatConfirm(String message)
    {
        ChatOutputHandler.chatConfirmation(that, message);
    }

    public void chatNotification(String message)
    {
        ChatOutputHandler.chatNotification(that, message);
    }

    public void chatError(String message)
    {
        ChatOutputHandler.chatError(that, message);
    }

    public void chatWarning(String message)
    {
        ChatOutputHandler.chatWarning(that, message);
    }

}
