package com.forgeessentials.jscripting.wrapper.mc;

import java.util.UUID;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.jscripting.wrapper.JsWrapper;
import com.forgeessentials.jscripting.wrapper.mc.entity.JsPlayerEntity;
import com.forgeessentials.util.DoAsCommandSender;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.google.gson.JsonParseException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;

/**
 *
 */
public class JsCommandSource extends JsWrapper<CommandSourceStack>
{

    private JsPlayerEntity player;

    /**
     * @tsd.ignore
     */
    public static JsCommandSource get(CommandSourceStack sender)
    {
        return sender == null ? null : new JsCommandSource(sender);
    }

    private JsCommandSource(CommandSourceStack that)
    {
        super(that);
    }

    public JsCommandSource(CommandSourceStack that, JsPlayerEntity jsPlayer)
    {
        super(that);
        this.player = jsPlayer;
    }

    public String getName()
    {
        return that.getTextName();
    }

    public JsPlayerEntity getPlayer()
    {
        if (player != null || !(that.getEntity() instanceof Player))
            return player;
        return player = new JsPlayerEntity((Player) that.getEntity(), this);
    }

    public JsCommandSource doAs(Object userIdOrPlayer, boolean hideChatOutput)
    {
        UserIdent doAsUser = userIdOrPlayer instanceof UUID ? UserIdent.get((UUID) userIdOrPlayer)
                : userIdOrPlayer instanceof JsPlayerEntity ? UserIdent.get(((JsPlayerEntity) userIdOrPlayer).getThat())
                        : APIRegistry.IDENT_SERVER;
        DoAsCommandSender result = new DoAsCommandSender(doAsUser, that);
        result.setHideChatMessages(hideChatOutput);
        return new JsCommandSource(result.createCommandSourceStack());
    }

    public void chat(String message)
    {
        ChatOutputHandler.sendMessage(that, message);
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

    public void tellRaw(String msg)
    {
        if (msg.isEmpty())
        {
            return;
        }
        try
        {
            Entity senderEntity = this.that.getEntityOrException();
            if (senderEntity != null)
            {
                Component itextcomponent = Component.Serializer.fromJson(msg);
                this.that.sendSuccess(ComponentUtils.updateForEntity(this.that, itextcomponent, senderEntity, 0),
                        true);
            }
        }
        catch (JsonParseException jsonparseexception)
        {
            this.chatError("There is an error in your JSON: " + jsonparseexception.getMessage());
        }
        catch (CommandSyntaxException e)
        {
            this.chatError("There is an error in your input: " + e.getMessage());
        }
    }

}
