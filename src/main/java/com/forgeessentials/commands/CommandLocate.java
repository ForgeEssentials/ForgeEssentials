package com.forgeessentials.commands;

import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.commons.UserIdent;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.OutputHandler;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

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
        	throw new TranslatedCommandException(getCommandUsage(sender));
        }
        else
        {
            EntityPlayerMP player = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);
            if (player == null)
            {
                OutputHandler.chatError(sender, String.format("Player %s does not exist, or is not online.", args[0]));
            }
            else
            {
                OutputHandler.chatConfirmation(sender,
                        Translator.format("%1$s is at %2$d, %3$d, %4$d in dim %5$d with gamemode %6$s", player.getCommandSenderName(), (int) player.posX,
                                (int) player.posY,
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
        	throw new TranslatedCommandException(getCommandUsage(sender));
        }
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {
        return RegisteredPermValue.OP;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/locate <player> Locates a player.";
    }
}
