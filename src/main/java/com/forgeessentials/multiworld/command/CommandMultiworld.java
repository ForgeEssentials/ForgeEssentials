package com.forgeessentials.multiworld.command;

import java.util.Random;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.world.GameRules;
import net.minecraft.world.WorldType;
import net.minecraftforge.permission.PermissionLevel;

import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.commands.ParserCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.multiworld.ModuleMultiworld;
import com.forgeessentials.multiworld.Multiworld;
import com.forgeessentials.multiworld.MultiworldException;
import com.forgeessentials.multiworld.MultiworldManager;
import com.forgeessentials.util.CommandParserArgs;

public class CommandMultiworld extends ParserCommandBase
{

    @Override
    public String getCommandName()
    {
        return "mw";
    }

    @Override
    public String getCommandUsage(ICommandSender commandSender)
    {
        return "/mw: Multiworld management command";
    }

    @Override
    public String getPermissionNode()
    {
        return ModuleMultiworld.PERM_BASE;
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
            arguments.confirm("Multiworld usage:");
            arguments.confirm("/mw create: Create a new world");
            arguments.confirm("/mw info <world>: Show world info");
            arguments.confirm("/mw delete <world>: Delete a world");
            arguments.confirm("/mw list [worlds|providers|worldtypes]");
            arguments.confirm("/mw gamerule <world>: Get and set world gamerules");
            return;
        }
        arguments.tabComplete("create", "info", "delete", "list", "gamerule");
        String subCmd = arguments.remove().toLowerCase();
        switch (subCmd)
        {
        case "create":
            parseCreate(arguments);
            break;
        case "info":
            parseInfo(arguments);
            break;
        case "delete":
            parseDelete(arguments);
            break;
        case "list":
            parseList(arguments);
            break;
        case "gamerule":
            parseGamerule(arguments);
            break;
        default:
            throw new TranslatedCommandException(FEPermissions.MSG_UNKNOWN_SUBCOMMAND, subCmd);
        }
    }

    /**
     * Create a new multiworld
     */
    public static void parseCreate(CommandParserArgs arguments) throws CommandException
    {
        arguments.checkPermission(ModuleMultiworld.PERM_MANAGE);

        if (arguments.isEmpty())
        {
            arguments.confirm("Usage: /mw create (name) [provider] [worldType] [seed*] [generatorOptions]"
                + "\n* You can enter \"-\" instead of a number or text to ignore the seed parameter for the cause want to enter generatorOptions but no seed.");
            return;
        }
        // Get the world name
        String name = arguments.remove();

        // Get the provider
        String provider = MultiworldManager.PROVIDER_NORMAL;
        if (arguments.isTabCompletion && arguments.size() == 1)
        {
            arguments.tabCompletion = ForgeEssentialsCommandBase.getListOfStringsMatchingLastWord(arguments.peek(), ModuleMultiworld.getMultiworldManager()
                    .getWorldProviders().keySet());
            return;
        }
        if (!arguments.isEmpty())
            provider = arguments.remove();

        // Get the World Type
        String worldType = WorldType.DEFAULT.getWorldTypeName();
        if (arguments.isTabCompletion && arguments.size() == 1)
        {
            arguments.tabCompletion = ForgeEssentialsCommandBase.getListOfStringsMatchingLastWord(arguments.peek(), ModuleMultiworld.getMultiworldManager()
                    .getWorldTypes().keySet());
            return;
        }
        if (!arguments.isEmpty())
            worldType = arguments.remove();

        // Get the World Seed
        long seed = new Random().nextLong();
        if (!arguments.isEmpty())
        {
            String arg = arguments.remove();
            try
            {
                seed = Long.parseLong(arg, 10);
            }
            catch (NumberFormatException e)
            {
                if (!arg.equals("-")) {
                    seed = arg.hashCode();
                }
            }
        }

        // Get generator options
        String generatorOptions = "";
        if (!arguments.isEmpty())
        {
            generatorOptions = arguments.remove();
        }

        if (!arguments.isEmpty())
            throw new TranslatedCommandException("Too many arguments");

        if (arguments.isTabCompletion)
            return;

        Multiworld world = new Multiworld(name, provider, worldType, seed, generatorOptions);
        try
        {
            ModuleMultiworld.getMultiworldManager().addWorld(world);
            if (arguments.senderPlayer != null)
            {
                world.teleport(arguments.senderPlayer, true);
            }
        }
        catch (MultiworldException e)
        {
            throw new TranslatedCommandException(e.type.error);
        }
    }

    /**
     * Delete a multiworld
     */
    public static void parseDelete(CommandParserArgs arguments) throws CommandException
    {
        arguments.checkPermission(ModuleMultiworld.PERM_DELETE);
        Multiworld world = parseWorld(arguments);

        ModuleMultiworld.getMultiworldManager().deleteWorld(world);
        arguments.confirm("Deleted multiworld " + world.getName());
    }

    public static void parseInfo(CommandParserArgs arguments) throws CommandException
    {
        arguments.checkPermission(ModuleMultiworld.PERM_MANAGE);
        Multiworld world = parseWorld(arguments);
        if (arguments.isTabCompletion)
            return;

        arguments.confirm("Multiworld %s:", world.getName());
        arguments.confirm("  DimID = %d", world.getDimensionId());
    }

    /**
     * Print lists of multiworlds, available providers and available world-types
     */
    public static void parseList(CommandParserArgs arguments) throws CommandException
    {
        arguments.checkPermission(ModuleMultiworld.PERM_LIST);
        arguments.tabComplete("worlds", "providers", "worldtypes");
        String subArg = arguments.isEmpty() ? "worlds" : arguments.remove().toLowerCase();
        switch (subArg)
        {
        case "providers":
            arguments.confirm("Available world providers:");
            for (String provider : ModuleMultiworld.getMultiworldManager().getWorldProviders().keySet())
            {
                arguments.confirm("  " + provider);
            }
            break;
        case "worldtypes":
            arguments.confirm("Available world types:");
            for (String worldType : ModuleMultiworld.getMultiworldManager().getWorldTypes().keySet())
            {
                arguments.confirm("  " + worldType);
            }
            break;
        case "worlds":
        default:
            arguments.confirm("Available worlds:");
            for (Multiworld world : ModuleMultiworld.getMultiworldManager().getWorlds())
            {
                arguments.confirm("#" + world.getDimensionId() + " " + world.getName() + ": " + world.getProvider());
            }
            break;
        }
    }

    public static void parseGamerule(CommandParserArgs arguments) throws CommandException
    {
        arguments.checkPermission(ModuleMultiworld.PERM_MANAGE);
        Multiworld world = parseWorld(arguments);

        GameRules rules = world.getWorldServer().getGameRules();
        arguments.tabComplete(rules.getRules());

        if (arguments.isEmpty())
        {
            // Check all gamerules
            if (!arguments.isTabCompletion)
            {
                arguments.confirm("Game rules for %s:", world.getName());
                for (String rule : rules.getRules())
                    arguments.confirm(rule + " = " + rules.getString(rule));
            }
            return;
        }

        String rule = arguments.remove();
        if (!rules.hasRule(rule))
            throw new CommandException("commands.gamerule.norule", rule);

        if (arguments.isEmpty())
        {
            // Check gamerule
            arguments.confirm(rule + " = " + rules.getString(rule));
            return;
        }

        // Set gamerule
        if (arguments.isTabCompletion)
            return;
        String value = arguments.remove();
        rules.setOrCreateGameRule(rule, value);
        arguments.confirm("Set gamerule %s = %s for world %s", rule, value, world.getName());
    }

    public static Multiworld parseWorld(CommandParserArgs arguments) throws CommandException
    {
        if (arguments.isEmpty())
            throw new TranslatedCommandException("Too few arguments!");
        arguments.tabComplete(ModuleMultiworld.getMultiworldManager().getWorldMap().keySet());
        String worldName = arguments.remove();
        Multiworld world = ModuleMultiworld.getMultiworldManager().getMultiworld(worldName);
        if (world == null)
            throw new TranslatedCommandException("Multiworld " + worldName + " does not exist!");
        return world;
    }

}
