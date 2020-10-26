package com.forgeessentials.commands.world;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.core.commands.ParserCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.util.CommandParserArgs;

import java.util.Iterator;

public class CommandBiome extends ParserCommandBase
{

    @Override
    public String getPrimaryAlias()
    {
        return "biome";
    }

    @Override
    public String[] getDefaultSecondaryAliases()
    {
        return new String[] { "biomeinfo" };
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "/febiome: Biome info tool";
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.OP;
    }

    @Override
    public String getPermissionNode()
    {
        return ModuleCommands.PERM + ".biome";
    }

    @Override
    public void parse(CommandParserArgs arguments) throws CommandException
    {
        int x = (int) Math.floor(arguments.senderPlayer.posX);
        int z = (int) Math.floor(arguments.senderPlayer.posZ);
        if (arguments.isEmpty())
        {
            if (arguments.isTabCompletion)
                return;
            Biome biome = arguments.senderPlayer.world.getBiome(new BlockPos(x, 0, z));
            arguments.confirm("Current biome: " + biome.biomeName);
            arguments.confirm("  " + biome.getClass().getName());
            arguments.notify("/febiome list: Show all registered biomes");
            return;
        }

        arguments.tabComplete("list");
        String subCmd = arguments.remove().toLowerCase();
        switch (subCmd)
        {
        case "list":
            if (arguments.isTabCompletion)
                return;
            arguments.confirm("Listing registered biomes:");
            boolean skip = false;
            Iterator<Biome> itor = Biome.REGISTRY.iterator();
            while (itor.hasNext())
            {
                Biome biome = itor.next();
                if (biome == null)
                {
                    skip = true;
                    continue;
                }
                if (skip)
                {
                    skip = false;
                    arguments.notify("----");
                }
                arguments.confirm(" #" + Biome.REGISTRY.getIDForObject(biome) + ": " + biome.biomeName);
            }
            break;
        case "dict":
            if (arguments.isTabCompletion)
                return;
            arguments.notify("Not yet implemented");
            break;
        default:
            throw new TranslatedCommandException(FEPermissions.MSG_UNKNOWN_SUBCOMMAND, subCmd);
        }
    }

}
