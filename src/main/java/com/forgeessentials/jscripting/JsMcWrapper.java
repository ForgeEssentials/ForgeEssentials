package com.forgeessentials.jscripting;

import net.minecraft.command.ICommandSender;

import com.forgeessentials.util.output.ChatOutputHandler;

public class JsMcWrapper
{

    public void confirm(ICommandSender player, String message)
    {
        ChatOutputHandler.chatConfirmation(player, message);
    }

}
