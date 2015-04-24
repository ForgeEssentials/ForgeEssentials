package com.forgeessentials.multiworld.command;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.multiworld.ModuleMultiworld;
import com.forgeessentials.multiworld.Multiworld;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.UserIdent;

/**
 * @author Olee
 */
public class CommandMultiworldTeleport extends ForgeEssentialsCommandBase {

    @Override
    public String getCommandName()
    {
        return "mwtp";
    }

    @Override
    public String getCommandUsage(ICommandSender commandSender)
    {
        return "/mwtp <world> [player] [x y z]";
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] argsArray)
    {
        switch (argsArray.length)
        {
        case 1:
            return ForgeEssentialsCommandBase.getListOfStringsMatchingLastWord(argsArray[0], ModuleMultiworld.getMultiworldManager().getWorldMap().keySet());
        case 2:
            return ForgeEssentialsCommandBase.completePlayername(argsArray[1]);
        case 0:
        default:
            return null;
        }
    }

    @Override
    public void processCommand(ICommandSender sender, String[] argsArray)
    {
        EntityPlayerMP player = (sender instanceof EntityPlayerMP) ? (EntityPlayerMP) sender : null;
        Queue<String> args = new LinkedList<>(Arrays.asList(argsArray));

        if (args.isEmpty())
            throw new TranslatedCommandException("Missing world argument.");

        String worldName = args.remove();
        if (args.isEmpty())
        {
            if (player == null)
                throw new TranslatedCommandException("Missing player-name argument.");
        }
        else
        {
            String playerName = args.remove();
            player = UserIdent.getPlayerByMatchOrUsername(sender, playerName);
            if (player == null)
                throw new TranslatedCommandException("Could not find player " + playerName);
        }

        double x = Math.floor(player.posX) + 0.5;
        double y = Math.floor(player.posY);
        double z = Math.floor(player.posZ) + 0.5;
        if (!args.isEmpty())
        {
            if (args.size() < 3)
                throw new TranslatedCommandException("Too few arguments for location.");
            x = parseDouble(sender, args.remove());
            y = parseDouble(sender, args.remove());
            z = parseDouble(sender, args.remove());
        }

        Multiworld multiworld = ModuleMultiworld.getMultiworldManager().getMultiworld(worldName);
        WorldServer world = multiworld != null ? multiworld.getWorldServer() : APIRegistry.namedWorldHandler.getWorld(worldName);
        if (world == null)
            throw new TranslatedCommandException("Could not find world " + worldName);
        int dimId = world.provider.dimensionId;

        //if (dimId < 0 || dimId == 1)
        //    throw new TranslatedCommandException("You are not allowed to teleport to that dimension");

        String msg = "Teleporting to ";
        if (multiworld == null)
        {
            switch (dimId)
            {
            case 0:
                msg += "the overworld";
                break;
            case 1:
                msg += "the nether";
                break;
            case -1:
                msg += "the end";
                break;
            default:
                msg += "dimension #" + dimId;
                break;
            }
        }
        else
        {
            msg += multiworld.getName();
        }
        msg = Translator.format(msg + " at [%.0f, %.0f, %.0f]", x, y, z);
        OutputHandler.chatConfirmation(player, msg);
        Multiworld.teleport(player, world, x, y, z, true);
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public String getPermissionNode()
    {
        return ModuleMultiworld.PERM_TELEPORT;
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {
        return RegisteredPermValue.OP;
    }

}
