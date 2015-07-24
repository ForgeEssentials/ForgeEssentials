package com.forgeessentials.core.commands;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.permission.PermissionLevel;

import com.forgeessentials.commons.BuildInfo;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.moduleLauncher.ModuleLauncher;
import com.forgeessentials.core.preloader.mixin.FEMixinConfig;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.output.LoggingHandler;

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
        return ForgeEssentials.PERM_INFO;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args)
    {
        if (args.length == 0)
        {
            ChatOutputHandler.chatNotification(sender, "/feinfo reload Reloads all configs.");
            ChatOutputHandler.chatNotification(sender, "/feinfo modules Prints a list of loaded FE modules");
            ChatOutputHandler.chatNotification(sender, "/feinfo about About ForgeEssentials");
        }
        else if (args[0].equalsIgnoreCase("reload"))
        {
            ChatOutputHandler.chatNotification(sender, "Reloading ForgeEssentials configs. May not work for all settings!");
            ModuleLauncher.instance.reloadConfigs(sender);
            ChatOutputHandler.chatNotification(sender, "Done!");
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
            ChatOutputHandler.chatNotification(sender, "Currently loaded modules: " + buff.toString());
        }
        else if (args[0].equalsIgnoreCase("about"))
        {
            LoggingHandler.felog.info(String.format("Running ForgeEssentials %s #%d (%s)", //
                    BuildInfo.VERSION, BuildInfo.getBuildNumber(), BuildInfo.getBuildHash()));
            ChatOutputHandler
                    .chatNotification(sender,
                            "Please refer to https://github.com/ForgeEssentials/ForgeEssentialsMain/wiki/Team-Information if you would like more information about the FE developers.");
        }
        else if (args[0].equalsIgnoreCase("debug"))
        {
            ChatOutputHandler.chatNotification(sender, "Injected patches:");
            for (String s : FEMixinConfig.getInjectedPatches())
            {
                ChatOutputHandler.chatNotification(sender, s);
            }
        }
    }

    @Override
    public PermissionLevel getPermissionLevel()
    {
        return PermissionLevel.OP;
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
