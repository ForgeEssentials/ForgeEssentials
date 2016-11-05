package com.forgeessentials.commands.player;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.permission.PermissionLevel;

import com.forgeessentials.api.UserIdent;
import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.util.ChatUtil;
import com.forgeessentials.util.ForgeEssentialsCommandBase;
import com.forgeessentials.util.TranslatedCommandException;
import com.forgeessentials.util.Translator;
import com.forgeessentials.util.Utils;

import cpw.mods.fml.common.FMLCommonHandler;

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
    public void processCommand(ICommandSender sender, String[] args)
    {
        if (args.length != 1)
            throw new TranslatedCommandException(getCommandUsage(sender));

        EntityPlayerMP player = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);
        if (player == null)
            throw new TranslatedCommandException("Player %s does not exist, or is not online.", args[0]);

        WorldPoint point = new WorldPoint(player);
        ChatUtil.chatConfirmation(sender, Translator.format("%s is at %d, %d, %d in dim %d with gamemode %s", //
                player.getCommandSenderName(), point.getX(), point.getY(), point.getZ(), point.getDimension(), //
                player.theItemInWorldManager.getGameType().getName()));
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        if (args.length == 1)
        {
            return Utils.getListOfStringsMatchingLastWord(args, FMLCommonHandler.instance().getMinecraftServerInstance().getAllUsernames());
        }
        else
        {
            throw new TranslatedCommandException(getCommandUsage(sender));
        }
    }

}
