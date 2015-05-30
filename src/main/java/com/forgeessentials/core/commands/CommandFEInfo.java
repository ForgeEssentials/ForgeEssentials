package com.forgeessentials.core.commands;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.commons.VersionUtils;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.moduleLauncher.ModuleLauncher;
import com.forgeessentials.core.preloader.asm.EventInjector;
import com.forgeessentials.util.OutputHandler;

public class CommandFEInfo extends ForgeEssentialsCommandBase
{

    public static final String[] options = { "debug", "reload", "modules", "about" };

    @Override
    public String getCommandName()
    {
        return "feinfo";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/feinfo";
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public String getPermissionNode()
    {
        return "fe.core.info";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args)
    {
        if (args.length == 0)
        {
            OutputHandler.chatNotification(sender, "/feinfo reload Reloads all configs.");
            OutputHandler.chatNotification(sender, "/feinfo modules Prints a list of loaded FE modules");
            OutputHandler.chatNotification(sender, "/feinfo about About ForgeEssentials");
        }
        else if (args[0].equalsIgnoreCase("reload"))
        {
            OutputHandler.chatNotification(sender, "Reloading ForgeEssentials configs. May not work for all settings!");
            ModuleLauncher.instance.reloadConfigs(sender);
            OutputHandler.chatNotification(sender, "Done!");
        }
        else if (args[0].equalsIgnoreCase("modules"))
        {
            StringBuilder buff = new StringBuilder();
            String sep = "";
            for (String str : ModuleLauncher.getModuleList())
            {
                buff.append(sep);
                buff.append(str);
                sep = ", ";
            }
            OutputHandler.chatNotification(sender, "Currently loaded modules: " + buff.toString());
        }
        else if (args[0].equalsIgnoreCase("about"))
        {
            OutputHandler.chatNotification(sender, "You are currently running ForgeEssentials version " + VersionUtils.FEVERSION);
            OutputHandler.felog.info("Build information: Build number is: " + ForgeEssentials.version.getBuildNumber() + ", build hash is: "
                    + ForgeEssentials.version.getBuildHash());
            OutputHandler
                    .chatNotification(sender,
                            "Please refer to https://github.com/ForgeEssentials/ForgeEssentialsMain/wiki/Team-Information if you would like more information about the FE developers.");
        }
        else if (args[0].equalsIgnoreCase("debug"))
        {
            OutputHandler.chatNotification(sender, "Injected patches:");
            for (String s : EventInjector.injectedPatches)
            {
                OutputHandler.chatNotification(sender, s);
            }
        }
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {
        return RegisteredPermValue.OP;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args)
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

}
