package com.forgeessentials.jscripting.wrapper;

import java.util.UUID;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.util.DoAsCommandSender;
import com.forgeessentials.util.output.ChatOutputHandler;

public class JsCommandSender extends JsWrapper<ICommandSender>
{

    private JsEntityPlayer player;

    public JsCommandSender(ICommandSender sender)
    {
        super(sender);
    }

    public JsCommandSender(EntityPlayer player, JsEntityPlayer jsPlayer)
    {
        super(player);
        this.player = jsPlayer;
    }

    public String getName()
    {
        return that.getCommandSenderName();
    }

    public JsEntityPlayer getPlayer()
    {
        if (player != null || !(that instanceof EntityPlayer))
            return player;
        return player = new JsEntityPlayer((EntityPlayer) that, this);
    }

    public JsCommandSender doAs(Object userIdOrPlayer, boolean hideChatOutput)
    {
        UserIdent doAsUser = userIdOrPlayer instanceof UUID ? UserIdent.get((UUID) userIdOrPlayer)
                : userIdOrPlayer instanceof JsEntityPlayer ? UserIdent.get(((JsEntityPlayer) userIdOrPlayer).getThat()) : APIRegistry.IDENT_SERVER;
        DoAsCommandSender result = new DoAsCommandSender(doAsUser, that);
        result.setHideChatMessages(hideChatOutput);
        return new JsCommandSender(result);
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
