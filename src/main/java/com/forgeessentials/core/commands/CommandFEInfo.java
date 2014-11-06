package com.forgeessentials.core.commands;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.core.moduleLauncher.ModuleLauncher;
import com.forgeessentials.core.preloader.FEModContainer;
import com.forgeessentials.util.OutputHandler;

public class CommandFEInfo extends ForgeEssentialsCommandBase {

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
            OutputHandler.chatNotification(sender, "/feinfo debug Produces ASM transformer debug output.");
            OutputHandler.chatNotification(sender, "/feinfo reload Reloads all configs.");
            OutputHandler.chatNotification(sender, "/feinfo modules Prints a list of loaded FE modules");
            OutputHandler.chatNotification(sender, "/feinfo about About ForgeEssentials");
        }
        else if (args[0].equalsIgnoreCase("reload"))
        {
            OutputHandler.chatNotification(sender, "Reloading ForgeEssentials configs. May not work for all settings!");
            OutputHandler.chatNotification(sender, EnumChatFormatting.RED + "This is experimental!");
            ModuleLauncher.instance.reloadConfigs(sender);
            OutputHandler.chatNotification(sender, "Done!");
        }
        else if (args[0].equalsIgnoreCase("modules"))
        {
            StringBuilder buff = new StringBuilder();
            String sep = "";
            for (String str : ModuleLauncher.getModuleList()) {
                buff.append(sep);
                buff.append(str);
                sep = ", ";
            }
            OutputHandler.chatNotification(sender, "Currently loaded modules: " + buff.toString());
        }
        else if (args[0].equalsIgnoreCase("about"))
        {
            OutputHandler.chatNotification(sender, "You are currently running ForgeEssentials version " + FEModContainer.version);
            OutputHandler.chatNotification(sender,
                    "Please refer to https://github.com/ForgeEssentials/ForgeEssentialsMain/wiki/Team-Information if you would like more information about the FE developers.");
        }
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {
        return RegisteredPermValue.OP;
    }

}
