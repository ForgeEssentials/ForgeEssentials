package com.forgeessentials.chat.command;

import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.impl.MessageCommand;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;

import java.util.Collection;

import com.forgeessentials.chat.ModuleChat;
import com.forgeessentials.core.misc.TranslatedCommandException.PlayerNotFoundException;
import com.forgeessentials.core.misc.TranslatedCommandException.WrongUsageException;

public class CommandMessageReplacement extends MessageCommand
{

    @Override
    public void sendMessage(CommandSource p_198538_0_, Collection<ServerPlayerEntity> p_198538_1_, ITextComponent p_198538_2_) throws CommandException
    {
        if (args.length < 2)
        {
            throw new WrongUsageException("commands.message.usage");
        }
        else
        {
            ServerPlayerEntity target = getPlayer(server, sender, args[0]);

            if (target == null)
            {
                throw new PlayerNotFoundException("commands.generic.player.notFound");
            }
            else if (target == sender)
            {
                throw new PlayerNotFoundException("commands.message.sameTarget");
            }
            else
            {
                ITextComponent message = getChatComponentFromNthArg(sender, args, 1, !(sender instanceof PlayerEntity));
                ModuleChat.tell(sender, message, target);
            }
        }
    }

}
