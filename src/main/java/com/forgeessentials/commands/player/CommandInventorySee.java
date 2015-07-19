package com.forgeessentials.commands.player;

import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.permission.PermissionLevel;

import com.forgeessentials.api.UserIdent;
import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.commands.util.PlayerInvChest;
import com.forgeessentials.core.misc.TranslatedCommandException;

/**
 * Opens other player inventory.
 *
 * @author Dries007
 */
public class CommandInventorySee extends FEcmdModuleCommands
{

    public CommandInventorySee()
    {
    }

    @Override
    public String getCommandName()
    {
        return "invsee";
    }

    @Override
    public void processCommandPlayer(EntityPlayerMP sender, String[] args) throws CommandException
    {
        if (args[0] == null)
            throw new TranslatedCommandException("You need to specify a player!");

        if (!FMLCommonHandler.instance().getEffectiveSide().isServer())
        {
            return;
        }
        EntityPlayerMP player = sender;
        EntityPlayerMP victim = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);
        if (victim == null)
            throw new TranslatedCommandException("Player %s not found.", args[0]);

        if (player.openContainer != player.inventoryContainer)
        {
            player.closeScreen();
        }
        player.getNextWindowId();

        PlayerInvChest chest = new PlayerInvChest(victim, sender);
        player.displayGUIChest(chest);
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos)
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
    public PermissionLevel getPermissionLevel()
    {
        return PermissionLevel.OP;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/invsee See a player's inventory.";
    }

}
