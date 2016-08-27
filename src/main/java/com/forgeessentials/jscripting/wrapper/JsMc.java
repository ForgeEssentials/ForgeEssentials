package com.forgeessentials.jscripting.wrapper;

import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.server.MinecraftServer;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.util.DoAsCommandSender;
import com.forgeessentials.util.output.ChatOutputHandler;

public class JsMc
{

    public JsCommandSender server = new JsCommandSender(MinecraftServer.getServer());

    public JsCommandSender doAs(JsCommandSender sender, UUID doAsPlayer, boolean hideChatOutput)
    {
        UserIdent doAsUser = doAsPlayer == null ? APIRegistry.IDENT_SERVER : UserIdent.get(doAsPlayer);
        DoAsCommandSender result = new DoAsCommandSender(doAsUser, sender.getThat());
        result.setHideChatMessages(hideChatOutput);
        return new JsCommandSender(result);
    }

    public JsCommandSender doAs(JsCommandSender sender, JsEntityPlayer doAsPlayer, boolean hideChatOutput)
    {
        return doAs(sender, doAsPlayer == null ? null : doAsPlayer.getThat().getPersistentID(), hideChatOutput);
    }

    public void cmd(JsCommandSender sender, String cmd, Object... args)
    {
        if (sender == null)
            sender = server;

        ICommand mcCommand = (ICommand) MinecraftServer.getServer().getCommandManager().getCommands().get(cmd);
        if (mcCommand == null)
            return;

        try
        {
            String[] strArgs = new String[args.length];
            for (int i = 0; i < args.length; i++)
                strArgs[i] = args[i].toString();

            mcCommand.processCommand(sender.getThat(), strArgs);
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

    public void confirm(JsCommandSender player, String message)
    {
        ChatOutputHandler.chatConfirmation(player.getThat(), message);
    }

    public JsBlock<Block> getBlockFromName(String name)
    {
        return new JsBlock<>(Block.getBlockFromName(name));
    }

}
