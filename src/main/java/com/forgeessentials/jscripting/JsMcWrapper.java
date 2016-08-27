package com.forgeessentials.jscripting;

import java.util.UUID;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.util.DoAsCommandSender;
import com.forgeessentials.util.output.ChatOutputHandler;

public class JsMcWrapper
{

    public ICommandSender server = MinecraftServer.getServer();

    public ICommandSender doAs(ICommandSender sender, UUID doAsPlayer, boolean hideChatOutput)
    {
        UserIdent doAsUser = doAsPlayer == null ? APIRegistry.IDENT_SERVER : UserIdent.get(doAsPlayer);
        DoAsCommandSender result = new DoAsCommandSender(doAsUser, sender);
        result.setHideChatMessages(hideChatOutput);
        return result;
    }

    public ICommandSender doAs(ICommandSender sender, EntityPlayer doAsPlayer, boolean hideChatOutput)
    {
        return doAs(sender, doAsPlayer == null ? null : doAsPlayer.getPersistentID(), hideChatOutput);
    }

    public void cmd(ICommandSender sender, String cmd, Object... args)
    {
        if (sender == null)
            sender = MinecraftServer.getServer();

        ICommand mcCommand = (ICommand) MinecraftServer.getServer().getCommandManager().getCommands().get(cmd);
        if (mcCommand == null)
            return;

        try
        {
            String[] strArgs = new String[args.length];
            for (int i = 0; i < args.length; i++)
                strArgs[i] = args[i].toString();

            mcCommand.processCommand(sender, strArgs);
        }
        catch (CommandException e)
        {
            // if (!ignoreErrors)
            // throw e;
            // LoggingHandler.felog.info(String.format("Silent script command /%s %s failed: %s", cmd, StringUtils.join(args, " "), e.getMessage()));
            e.printStackTrace();
            throw e;
        }
    }

    public void confirm(ICommandSender player, String message)
    {
        ChatOutputHandler.chatConfirmation(player, message);
    }

}
