package com.forgeessentials.multiworld.command;

import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.core.commands.ParserCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.multiworld.ModuleMultiworld;
import com.forgeessentials.multiworld.Multiworld;
import com.forgeessentials.util.CommandParserArgs;
import com.forgeessentials.util.output.ChatOutputHandler;

public class CommandMultiworldTeleport //extends ParserCommandBase
{
/*
    @Override
    public String getPrimaryAlias()
    {
        return "mwtp";
    }

    @Override
    public String getPermissionNode()
    {
        return ModuleMultiworld.PERM_TELEPORT;
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.OP;
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

        ServerPlayerEntity player = arguments.parsePlayer(true, true).getPlayerMP();
        if (player == null)
            throw new TranslatedCommandException("Missing player-name argument.");

        double x = Math.floor(player.position().x) + 0.5;
        double y = Math.floor(player.position().y);
        double z = Math.floor(player.position().z) + 0.5;
        if (!arguments.isEmpty())
        {
            if (arguments.size() < 3)
                throw new TranslatedCommandException("Too few arguments for location.");
            x = arguments.parseDouble();
            y = arguments.parseDouble();
            z = arguments.parseDouble();
        }

        Multiworld multiworld = ModuleMultiworld.getMultiworldManager().getMultiworld(worldName);
        ServerWorld world = multiworld != null ? multiworld.getWorldServer() : APIRegistry.namedWorldHandler.getWorld(worldName);
        if (world == null)
            throw new TranslatedCommandException("Could not find world " + worldName);
        RegistryKey<World> dimId = world.dimension();

        // if (dimId < 0 || dimId == 1)
        // throw new TranslatedCommandException("You are not allowed to teleport to that dimension");

        String msg = "Teleporting to ";
        if (multiworld == null)
        {
            switch (dimId)
            {
            case World.OVERWORLD:
                msg += "the overworld";
                break;
            case World.NETHER:
                msg += "the nether";
                break;
            case World.END:
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
        ChatOutputHandler.chatConfirmation(player.createCommandSourceStack(), msg);
        Multiworld.teleport(player, world, x, y, z, false);
    }*/

}
