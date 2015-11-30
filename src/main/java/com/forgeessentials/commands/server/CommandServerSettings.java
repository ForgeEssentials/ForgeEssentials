package com.forgeessentials.commands.server;

import java.util.Arrays;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldSettings.GameType;
import net.minecraftforge.permission.PermissionLevel;

import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.core.commands.ParserCommandBase;
import com.forgeessentials.scripting.ScriptArguments;
import com.forgeessentials.util.CommandParserArgs;
import com.forgeessentials.util.output.ChatOutputHandler;

import cpw.mods.fml.common.FMLCommonHandler;

public class CommandServerSettings extends ParserCommandBase
{

    public static List<String> options = Arrays.asList("allowFlight", "allowPVP", "buildLimit", "difficulty", "MotD", "spawnProtection", "gamemode");

    @Override
    public String getCommandName()
    {
        return "serversettings";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/serversettings [option] [value]: View or change server settings (server.properties)";
    }

    @Override
    public String[] getDefaultAliases()
    {
        return new String[] { "ss" };
    }

    @Override
    public String getPermissionNode()
    {
        return ModuleCommands.PERM + "." + getCommandName();
    }

    @Override
    public void parse(CommandParserArgs arguments)
    {
        if (!FMLCommonHandler.instance().getMinecraftServerInstance().isDedicatedServer())
            arguments.error("You can use this command only on dedicated servers");
        DedicatedServer server = (DedicatedServer) FMLCommonHandler.instance().getMinecraftServerInstance();

        if (arguments.isEmpty())
        {
            arguments.notify("Options: %s", StringUtils.join(options, ", "));
            return;
        }

        arguments.tabComplete(options);
        String subCmd = arguments.remove().toLowerCase();
        switch (subCmd)
        {
        case "allowflight":
            if (arguments.isEmpty())
                arguments.confirm("Allow flight: %s", Boolean.toString(server.isFlightAllowed()));
            else
            {
                boolean allowFlight = arguments.parseBoolean();
                server.setAllowFlight(allowFlight);
                server.setProperty("allow-flight", allowFlight);
                server.saveProperties();
                arguments.confirm("Set allow-flight to %s", Boolean.toString(allowFlight));
            }
            break;
        case "allowpvp":
            if (arguments.isEmpty())
                arguments.confirm("Allow PvP: %s", Boolean.toString(server.isPVPEnabled()));
            else
            {
                boolean allowPvP = arguments.parseBoolean();
                server.setAllowPvp(allowPvP);
                server.setProperty("pvp", allowPvP);
                server.saveProperties();
                arguments.confirm("Set pvp to %s", Boolean.toString(allowPvP));
            }
            break;
        case "buildlimit":
            if (arguments.isEmpty())
                arguments.confirm("Set build limit to %d", server.getBuildLimit());
            else
            {
                int buildLimit = arguments.parseInt(0, Integer.MAX_VALUE);
                server.setBuildLimit(buildLimit);
                server.setProperty("max-build-height", buildLimit);
                server.saveProperties();
                arguments.confirm("Set max-build-height to %d", buildLimit);
            }
            break;
        case "motd":
            if (arguments.isEmpty())
                arguments.confirm("MotD = %s", server.getMOTD());
            else
            {
                String motd = ScriptArguments.process(arguments.toString(), null);
                server.func_147134_at().func_151315_a(new ChatComponentText(ChatOutputHandler.formatColors(motd)));
                server.setMOTD(motd);
                server.setProperty("motd", motd);
                server.saveProperties();
                arguments.confirm("Set MotD to %s", motd);
            }
            break;
        case "spawnprotection":
            if (arguments.isEmpty())
                arguments.confirm("Spawn protection size: %d", server.getSpawnProtectionSize());
            else
            {
                int spawnSize = arguments.parseInt(0, Integer.MAX_VALUE);
                server.setProperty("spawn-protection", spawnSize);
                server.saveProperties();
                arguments.confirm("Set spawn-protection to %d", spawnSize);
            }
            break;
        case "gamemode":
            if (arguments.isEmpty())
                arguments.confirm("Default gamemode set to %s", server.getGameType().getName());
            else
            {
                GameType gamemode = WorldSettings.GameType.getByID(arguments.parseInt());
                server.setGameType(gamemode);
                server.setProperty("gamemode", gamemode.ordinal());
                server.saveProperties();
                arguments.confirm("Set default gamemode to %s", gamemode.getName());
            }
            break;
        case "difficulty":
            if (arguments.isEmpty())
                arguments.confirm("Difficulty set to %s", server.func_147135_j());
            else
            {
                EnumDifficulty difficulty = EnumDifficulty.getDifficultyEnum(arguments.parseInt());
                server.func_147139_a(difficulty);
                server.setProperty("difficulty", difficulty.ordinal());
                server.saveProperties();
                arguments.confirm("Set difficulty to %s", difficulty.name());
            }
            break;

        default:
            arguments.error(FEPermissions.MSG_UNKNOWN_SUBCOMMAND, subCmd);
        }
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender par1ICommandSender, String[] args)
    {
        if (args.length == 1)
        {
            return getListOfStringsMatchingLastWord(args, options);
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

}
