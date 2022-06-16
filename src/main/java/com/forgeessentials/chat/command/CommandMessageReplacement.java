package com.forgeessentials.chat.command;

import net.minecraft.command.CommandException;
import net.minecraft.command.impl.MessageCommand;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;

import com.forgeessentials.chat.ModuleChat;

public class CommandMessageReplacement extends MessageCommand
{

    @Override
    public void sendMessage(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
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
                ITextComponent message = getChatComponentFromNthArg(sender, args, 1, !(sender instanceof EntityPlayer));
                ModuleChat.tell(sender, message, target);
            }
        }
    }

}
