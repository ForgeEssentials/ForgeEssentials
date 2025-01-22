package com.forgeessentials.multiworld.command;

import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;
import net.minecraftforge.permission.PermissionLevel;

import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.core.commands.ParserCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.multiworld.ModuleMultiworld;
import com.forgeessentials.multiworld.Multiworld;
import com.forgeessentials.util.CommandParserArgs;
import com.forgeessentials.util.output.ChatOutputHandler;

public class CommandMultiworldTeleport extends ParserCommandBase
{

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
    public String getPermissionNode()
    {
        return ModuleMultiworld.PERM_TELEPORT;
    }

    @Override
    public PermissionLevel getPermissionLevel()
    {
        return PermissionLevel.OP;
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public void parse(CommandParserArgs arguments) throws CommandException
    {
        if (arguments.isEmpty())
        {
            arguments.confirm("/mwtp <world> [player] [x y z]");
            return;
        }

        List<String> worldNames = APIRegistry.namedWorldHandler.getWorldNames();
        worldNames.add(0, "list");
        arguments.tabComplete(worldNames);
        
        String worldName = arguments.remove();

        if (worldName.equalsIgnoreCase("list"))
        {
            arguments.confirm("List of worlds: %s", StringUtils.join(worldNames, ", "));
            return;
        }

        EntityPlayerMP player = arguments.parsePlayer(true, true).getPlayerMP();
        if (player == null)
            throw new TranslatedCommandException("Missing player-name argument.");

        double x = Math.floor(player.posX) + 0.5;
        double y = Math.floor(player.posY);
        double z = Math.floor(player.posZ) + 0.5;
        if (!arguments.isEmpty())
        {
            if (arguments.size() < 3)
                throw new TranslatedCommandException("Too few arguments for location.");
            x = arguments.parseDouble();
            y = arguments.parseDouble();
            z = arguments.parseDouble();
        }

        Multiworld multiworld = ModuleMultiworld.getMultiworldManager().getMultiworld(worldName);
        WorldServer world = multiworld != null ? multiworld.getWorldServer() : APIRegistry.namedWorldHandler.getWorld(worldName);
        if (world == null)
            throw new TranslatedCommandException("Could not find world " + worldName);
        int dimId = world.provider.getDimensionId();

        // if (dimId < 0 || dimId == 1)
        // throw new TranslatedCommandException("You are not allowed to teleport to that dimension");

        String msg = "Teleporting to ";
        if (multiworld == null)
        {
            switch (dimId)
            {
            case 0:
                msg += "the overworld";
                break;
            case -1:
                msg += "the nether";
                break;
            case 1:
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
        ChatOutputHandler.chatConfirmation(player, msg);
        Multiworld.teleport(player, world, x, y, z, false);
    }

}
