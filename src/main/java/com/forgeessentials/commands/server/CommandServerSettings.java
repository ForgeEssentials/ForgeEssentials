package com.forgeessentials.commands.server;

import java.util.Arrays;
import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldSettings;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraft.world.GameType;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.core.commands.ParserCommandBase;
import com.forgeessentials.scripting.ScriptArguments;
import com.forgeessentials.util.CommandParserArgs;
import com.forgeessentials.util.output.ChatOutputHandler;

public class CommandServerSettings extends ParserCommandBase
{

    public static List<String> options = Arrays.asList("allowFlight", "allowPVP", "buildLimit", "difficulty", "MotD", "spawnProtection", "gamemode");

    @Override
    public String getPrimaryAlias()
    {
        return "serversettings";
    }

    @Override
    public String[] getDefaultSecondaryAliases()
    {
        return new String[] { "ss" };
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "/serversettings [option] [value]: View or change server settings (server.properties)";
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.OP;
    }

    @Override
    public String getPermissionNode()
    {
        return ModuleCommands.PERM + ".serversettings";
    }

    @SideOnly(Side.SERVER)
    public void doSetProperty(String id, Object value)
    {
        DedicatedServer server = (DedicatedServer) FMLCommonHandler.instance().getMinecraftServerInstance();
        server.setProperty(id, value);
        server.saveProperties();
    }

    public void setProperty(String id, Object value)
    {
        if (FMLCommonHandler.instance().getSide() == Side.SERVER)
            doSetProperty(id, value);
    }

    @Override
    public void parse(CommandParserArgs arguments) throws CommandException
    {
        if (!FMLCommonHandler.instance().getMinecraftServerInstance().isDedicatedServer())
            arguments.error("You can use this command only on dedicated servers");
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();

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
                setProperty("allow-flight", allowFlight);
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
                setProperty("pvp", allowPvP);
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
                setProperty("max-build-height", buildLimit);
                arguments.confirm("Set max-build-height to %d", buildLimit);
            }
            break;
        case "motd":
            if (arguments.isEmpty())
                arguments.confirm("MotD = %s", server.getMOTD());
            else
            {
                String motd = ScriptArguments.process(arguments.toString(), null);
                server.getServerStatusResponse().setServerDescription(new TextComponentString(ChatOutputHandler.formatColors(motd)));
                server.setMOTD(motd);
                setProperty("motd", motd);
                arguments.confirm("Set MotD to %s", motd);
            }
            break;
        case "spawnprotection":
            if (arguments.isEmpty())
                arguments.confirm("Spawn protection size: %d", server.getSpawnProtectionSize());
            else
            {
                int spawnSize = arguments.parseInt(0, Integer.MAX_VALUE);
                setProperty("spawn-protection", spawnSize);
                arguments.confirm("Set spawn-protection to %d", spawnSize);
            }
            break;
        case "gamemode":
            if (arguments.isEmpty())
                arguments.confirm("Default gamemode set to %s", server.getGameType().getName());
            else
            {
                GameType gamemode = GameType.getByID(arguments.parseInt());
                server.setGameType(gamemode);
                setProperty("gamemode", gamemode.ordinal());
                arguments.confirm("Set default gamemode to %s", gamemode.getName());
            }
            break;
        case "difficulty":
            if (arguments.isEmpty())
                arguments.confirm("Difficulty set to %s", server.getDifficulty());
            else
            {
                EnumDifficulty difficulty = EnumDifficulty.getDifficultyEnum(arguments.parseInt());
                server.setDifficultyForAllWorlds(difficulty);
                setProperty("difficulty", difficulty.ordinal());
                arguments.confirm("Set difficulty to %s", difficulty.name());
            }
            break;

        default:
            arguments.error(FEPermissions.MSG_UNKNOWN_SUBCOMMAND, subCmd);
        }
    }

}
