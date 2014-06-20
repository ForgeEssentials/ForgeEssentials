package com.forgeessentials.commands;

import com.forgeessentials.api.permissions.RegGroup;
import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.OutputHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.List;

public class CommandLocate extends FEcmdModuleCommands {
    @Override
    public String getCommandName()
    {
        return "locate";
    }

    @Override
    public String[] getDefaultAliases()
    {
        return new String[]
                { "gps", "loc", "playerinfo" };
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args)
    {
        if (args.length != 1)
        {
            OutputHandler.chatError(sender, "Improper syntax. Please specify a player name.");
        }
        else
        {
            EntityPlayerMP player = FunctionHelper.getPlayerForName(sender, args[0]);
            if (player == null)
            {
                OutputHandler.chatError(sender, String.format("Player %s does not exist, or is not online.", args[0]));
            }
            else
            {
                OutputHandler.chatConfirmation(sender,
                        String.format("%1$s is at %2$d, %3$d, %4$d in dim %5$d with gamemode %6$s", player.username, (int) player.posX, (int) player.posY,
                                (int) player.posZ, player.dimension, player.theItemInWorldManager.getGameType().getName()));
            }
        }
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        if (args.length == 1)
        {
            return getListOfStringsMatchingLastWord(args, FMLCommonHandler.instance().getMinecraftServerInstance().getAllUsernames());
        }
        else
        {
            return null;
        }
    }

    @Override
    public RegGroup getReggroup()
    {
        return RegGroup.OWNERS;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {

        return "/locate <player> Locates a player.";
    }
}
