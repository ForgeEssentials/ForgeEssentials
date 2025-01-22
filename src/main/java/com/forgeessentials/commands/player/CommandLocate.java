package com.forgeessentials.commands.player;

import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.permission.PermissionLevel;

import com.forgeessentials.api.UserIdent;
import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.output.ChatOutputHandler;



public class CommandLocate extends ForgeEssentialsCommandBase
{
    
    @Override
    public String getCommandName()
    {
        return "felocate";
    }

    @Override
    public String[] getDefaultAliases()
    {
        return new String[] { "locate", "gps", "loc", "playerinfo" };
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public PermissionLevel getPermissionLevel()
    {
        return PermissionLevel.OP;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/locate <player> Locates a player.";
    }

    @Override
    public String getPermissionNode()
    {
        return ModuleCommands.PERM + ".locate";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length != 1)
            throw new TranslatedCommandException(getCommandUsage(sender));

        EntityPlayerMP player = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);
        if (player == null)
            throw new TranslatedCommandException("Player %s does not exist, or is not online.", args[0]);

        WorldPoint point = new WorldPoint(player);
        ChatOutputHandler.chatConfirmation(sender, Translator.format("%s is at %d, %d, %d in dim %d with gamemode %s", //
                player.getName(), point.getX(), point.getY(), point.getZ(), point.getDimension(), //
                player.theItemInWorldManager.getGameType().getName()));
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos)
    {
    	if (args.length == 1)
        {
            return getListOfStringsMatchingLastWord(args, FMLCommonHandler.instance().getMinecraftServerInstance().getAllUsernames());
        }
        return null;
    }

}
