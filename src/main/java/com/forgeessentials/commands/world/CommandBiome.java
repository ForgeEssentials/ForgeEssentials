package com.forgeessentials.commands.world;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.permission.PermissionLevel;

import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.core.commands.ParserCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.util.CommandParserArgs;

public class CommandBiome extends ParserCommandBase
{

    @Override
    public String getCommandName()
    {
        return "febiome";
    }

    @Override
    public String[] getDefaultAliases()
    {
        return new String[] { "biomeinfo" };
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/febiome: Biome info tool";
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public PermissionLevel getPermissionLevel()
    {
        return PermissionLevel.OP;
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
            BiomeGenBase biome = arguments.senderPlayer.worldObj.getBiomeGenForCoords(new BlockPos(x, 0, z));
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
            for (int i = 0; i < BiomeGenBase.getBiomeGenArray().length; i++)
            {
                BiomeGenBase biome = BiomeGenBase.getBiomeGenArray()[i];
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
                arguments.confirm(" #" + i + ": " + biome.biomeName);
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
