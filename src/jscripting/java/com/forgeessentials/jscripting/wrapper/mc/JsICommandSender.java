package com.forgeessentials.jscripting.wrapper.mc;

import java.util.UUID;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import com.forgeessentials.api.FEApi;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.jscripting.wrapper.JsWrapper;
import com.forgeessentials.jscripting.wrapper.mc.entity.JsEntityPlayer;
import com.forgeessentials.util.ChatUtil;
import com.forgeessentials.util.DoAsCommandSender;

/**
 *
 */
public class JsICommandSender extends JsWrapper<ICommandSender>
{

    private JsEntityPlayer player;

    /**
     * @tsd.ignore
     */
    public static JsICommandSender get(ICommandSender sender)
    {
        return sender == null ? null : new JsICommandSender(sender);
    }

    private JsICommandSender(ICommandSender that)
    {
        super(that);
    }

    public JsICommandSender(EntityPlayer that, JsEntityPlayer jsPlayer)
    {
        super(that);
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

    public JsICommandSender doAs(Object userIdOrPlayer, boolean hideChatOutput)
    {
        UserIdent doAsUser = userIdOrPlayer instanceof UUID ? UserIdent.get((UUID) userIdOrPlayer)
                : userIdOrPlayer instanceof JsEntityPlayer ? UserIdent.get(((JsEntityPlayer) userIdOrPlayer).getThat()) : FEApi.IDENT_SERVER;
        DoAsCommandSender result = new DoAsCommandSender(doAsUser, that);
        result.setHideChatMessages(hideChatOutput);
        return new JsICommandSender(result);
    }

    public void chat(String message)
    {
        ChatUtil.sendMessage(that, message);
    }

    public void chatConfirm(String message)
    {
        ChatUtil.chatConfirmation(that, message);
    }

    public void chatNotification(String message)
    {
        ChatUtil.chatNotification(that, message);
    }

    public void chatError(String message)
    {
        ChatUtil.chatError(that, message);
    }

    public void chatWarning(String message)
    {
        ChatUtil.chatWarning(that, message);
    }

}
