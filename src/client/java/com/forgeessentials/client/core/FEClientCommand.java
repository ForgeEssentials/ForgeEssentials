package com.forgeessentials.client.core;

import com.forgeessentials.commons.VersionUtils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

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
        if (args[0].equalsIgnoreCase("reinit"))
        {
            ClientProxy.INSTANCE.sentHandshake = false;
            sender.addChatMessage(new ChatComponentText("Resent handshake packet to server."));
        }
        else if (args[0].equalsIgnoreCase("info"))
        {
            sender.addChatMessage(new ChatComponentText("You are currently running ForgeEssentials Client version " + VersionUtils.FEVERSION));
            sender.addChatMessage(new ChatComponentText("Build information: Build number is: " + ClientProxy.INSTANCE.version.getBuildNumber() + ", build hash is: "
                    + ClientProxy.INSTANCE.version.getBuildHash()));
            sender.addChatMessage(new ChatComponentText("Please refer to https://github.com/ForgeEssentials/ForgeEssentialsMain/wiki/Team-Information if you would like more information about the FE developers."));
        }
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 0;
    }
}
