package com.forgeessentials.commands.server;

import java.util.Arrays;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldSettings;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.ServerUtil;

import cpw.mods.fml.common.FMLCommonHandler;

public class CommandServerSettings extends FEcmdModuleCommands
{

    public static List<String> options = Arrays.asList("allowFlight", "allowPVP", "buildLimit", "difficulty", "MOTD", "spawnProtection", "gamemode");

    @Override
    public String getCommandName()
    {
        return "serversettings";
    }

    @Override
    public String[] getDefaultAliases()
    {
        return new String[] { "ss" };
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args)
    {
        if (!FMLCommonHandler.instance().getMinecraftServerInstance().isDedicatedServer())
        {
            return;
        }
        DedicatedServer server = (DedicatedServer) FMLCommonHandler.instance().getMinecraftServerInstance();
        if (args.length == 0)
        {
            OutputHandler.chatNotification(sender, "Available options:");
            OutputHandler.chatNotification(sender, options.toString());
            return;
        }

        if (args[0].equalsIgnoreCase("allowFlight"))
        {
            if (args.length == 1)
            {
                OutputHandler.chatConfirmation(sender, "allowFlight: " + server.isFlightAllowed());
            }
            else
            {
                server.setAllowFlight(Boolean.parseBoolean(args[1]));
                server.setProperty("allow-flight", Boolean.parseBoolean(args[1]));
                server.saveProperties();
                OutputHandler.chatConfirmation(sender, "allowFlight: " + server.isFlightAllowed());
            }
            return;
        }

        if (args[0].equalsIgnoreCase("allowPVP"))
        {
            if (args.length == 1)
            {
                OutputHandler.chatConfirmation(sender, "allowPVP: " + server.isPVPEnabled());
            }
            else
            {
                server.setAllowPvp(Boolean.parseBoolean(args[1]));
                server.setProperty("pvp", Boolean.parseBoolean(args[1]));
                server.saveProperties();
                OutputHandler.chatConfirmation(sender, "allowPVP: " + server.isPVPEnabled());
            }
            return;
        }

        if (args[0].equalsIgnoreCase("buildLimit"))
        {
            if (args.length == 1)
            {
                OutputHandler.chatConfirmation(sender, "buildLimit: " + server.getBuildLimit());
            }
            else
            {
                server.setBuildLimit(parseIntWithMin(sender, args[1], 0));
                server.setProperty("max-build-height", parseIntWithMin(sender, args[1], 0));
                server.saveProperties();
                OutputHandler.chatConfirmation(sender, "buildLimit: " + server.getBuildLimit());
            }
            return;
        }

        if (args[0].equalsIgnoreCase("MOTD"))
        {
            if (args.length == 1)
            {
                OutputHandler.chatConfirmation(sender, "MOTD: " + server.getMOTD());
            }
            else
            {
                String msg = "";
                for (String var : ServerUtil.dropFirst(args))
                {
                    msg += " " + var;
                }
                server.setMOTD(msg.substring(1));
                server.setProperty("motd", msg.substring(1));
                server.saveProperties();
                OutputHandler.chatConfirmation(sender, "MOTD: " + server.getMOTD());
            }
            return;
        }

        if (args[0].equalsIgnoreCase("spawnProtection"))
        {
            if (args.length == 1)
            {
                OutputHandler.chatConfirmation(sender, "spawnProtection: " + server.getSpawnProtectionSize());
            }
            else
            {
                server.setProperty("spawn-protection", parseIntWithMin(sender, args[1], 0));
                server.saveProperties();
                OutputHandler.chatConfirmation(sender, "spawnProtection: " + server.getSpawnProtectionSize());
            }
            return;
        }

        if (args[0].equalsIgnoreCase("gamemode"))
        {
            if (args.length == 1)
            {
                OutputHandler.chatConfirmation(sender, "gamemode: " + server.getGameType().getName());
            }
            else
            {
                server.setProperty("gamemode", args[1]);
                server.setGameType(WorldSettings.GameType.getByID(Integer.parseInt(args[1])));
                server.saveProperties();
                OutputHandler.chatConfirmation(sender, "gamemode: " + server.getGameType().getName());
            }
            return;

        }

        if (args[0].equalsIgnoreCase("difficulty"))
        {
            if (args.length == 1)
            {
                OutputHandler.chatConfirmation(sender, "difficulty: " + server.func_147135_j().name());
            }
            else
            {
                server.setProperty("difficulty", args[1]);
                server.func_147139_a(EnumDifficulty.getDifficultyEnum(Integer.parseInt(args[1])));
                server.saveProperties();
                OutputHandler.chatConfirmation(sender, "difficulty: " + server.func_147135_j().name());
            }
            return;

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
    public RegisteredPermValue getDefaultPermission()
    {
        return RegisteredPermValue.OP;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {

        return "/serversettings [option] [value] View or change server settings (in server.properties).";
    }
}
