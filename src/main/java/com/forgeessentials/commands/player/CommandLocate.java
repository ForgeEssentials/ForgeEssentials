package com.forgeessentials.commands.player;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.UserIdent;
import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.OutputHandler;

import cpw.mods.fml.common.FMLCommonHandler;

public class CommandLocate extends FEcmdModuleCommands
{
    @Override
    public String getCommandName()
    {
        return "locate";
    }

    @Override
    public String[] getDefaultAliases()
    {
        return new String[] { "gps", "loc", "playerinfo" };
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args)
    {
        if (args.length != 1)
            throw new TranslatedCommandException(getCommandUsage(sender));

        EntityPlayerMP player = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);
        if (player == null)
            throw new TranslatedCommandException("Player %s does not exist, or is not online.", args[0]);

        WorldPoint point = new WorldPoint(player);
        OutputHandler.chatConfirmation(sender, Translator.format("%s is at %d, %d, %d in dim %d with gamemode %s", //
                player.getCommandSenderName(), point.getX(), point.getY(), point.getZ(), point.getDimension(), //
                player.theItemInWorldManager.getGameType().getName()));
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
