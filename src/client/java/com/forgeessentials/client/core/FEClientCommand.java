package com.forgeessentials.client.core;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;

import com.forgeessentials.commons.BuildInfo;

/**
 * Note to olee: This command only exists within FEClient so ForgeEssentialsCommandBase or any of those parser stuff is not available to us.
 */
public class FEClientCommand extends CommandBase
{
    @Override
    public String getCommandName()
    {
        return "feclient";
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_)
    {
        return "/feclient ForgeEssentials clientside command.";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args)
    {
        if (args.length == 0)
        {
            sender.addChatMessage(new ChatComponentText("/feclient info: Get FE client info"));
            sender.addChatMessage(new ChatComponentText("/feclient reinit: Redo server handshake"));
        }
        else if (args[0].equalsIgnoreCase("reinit"))
        {
            ClientProxy.INSTANCE.sentHandshake = false;
            sender.addChatMessage(new ChatComponentText("Resent handshake packet to server."));
        }
        else if (args[0].equalsIgnoreCase("info"))
        {
            sender.addChatMessage(new ChatComponentText(String.format("Running ForgeEssentials client %s #%d (%s)", //
                    BuildInfo.VERSION, BuildInfo.getBuildNumber(), BuildInfo.getBuildHash())));
            sender.addChatMessage(new ChatComponentText(
                    "Please refer to https://github.com/ForgeEssentials/ForgeEssentialsMain/wiki/Team-Information if you would like more information about the FE developers."));
        }
        else
        {
            sender.addChatMessage(new ChatComponentTranslation("Unknown argument %s", args[0]));
        }
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 0;
    }
}
