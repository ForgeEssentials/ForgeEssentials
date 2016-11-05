package com.forgeessentials.core.commands;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.permission.PermissionLevel;

import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.moduleLauncher.ModuleLauncher;
import com.forgeessentials.util.ChatUtil;
import com.forgeessentials.util.ForgeEssentialsCommandBase;
import com.forgeessentials.util.Translator;

public class CommandFeReload extends ForgeEssentialsCommandBase
{

    @Override
    public String getCommandName()
    {
        return "fereload";
    }

    @Override
    public String[] getDefaultAliases()
    {
        return new String[] { "reload" };
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/fereload: Reload FE configuration";
    }

    @Override
    public String getPermissionNode()
    {
        return ForgeEssentials.PERM_RELOAD;
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
    public void processCommand(ICommandSender sender, String[] args)
    {
        reload(sender);
    }

    public static void reload(ICommandSender sender)
    {
        ModuleLauncher.instance.reloadConfigs();
        ChatUtil.chatConfirmation(sender, Translator.translate("Reloaded configs. (may not work for all settings)"));
    }

}
