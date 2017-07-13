package com.forgeessentials.client.core;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

import com.forgeessentials.commons.BuildInfo;

/**
 * Note: This command only exists within FEClient so no FE server utilities can be used!
 */
public class FEClientCommand extends CommandBase
{

    @Override
    public String getName()
    {
        return "feclient";
    }

    @Override
    public String getUsage(ICommandSender p_71518_1_)
    {
        return "/feclient [info|reinit]: ForgeEssentials client helper";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args)
    {
        if (args.length == 0)
        {
            sender.sendMessage(new TextComponentString("/feclient info: Get FE client info"));
            sender.sendMessage(new TextComponentString("/feclient reinit: Redo server handshake"));
        }
        else if (args[0].equalsIgnoreCase("reinit"))
        {
            ClientProxy.resendHandshake();
            sender.sendMessage(new TextComponentString("Resent handshake packet to server."));
        }
        else if (args[0].equalsIgnoreCase("info"))
        {
            sender.sendMessage(new TextComponentString(String.format("Running ForgeEssentials client %s (%s)", //
                    BuildInfo.getFullVersion(), BuildInfo.getBuildHash())));
            sender.sendMessage(new TextComponentString(
                    "Please refer to https://github.com/ForgeEssentials/ForgeEssentialsMain/wiki/Team-Information if you would like more information about the FE developers."));
        }
        else
        {
            sender.sendMessage(new TextComponentTranslation("Unknown argument %s", args[0]));
        }
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 0;
    }

}
