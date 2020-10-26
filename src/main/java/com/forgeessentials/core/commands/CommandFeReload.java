package com.forgeessentials.core.commands;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.core.moduleLauncher.ModuleLauncher;
import com.forgeessentials.util.output.ChatOutputHandler;

public class CommandFeReload extends ForgeEssentialsCommandBase
{

    @Override
    public String getPrimaryAlias()
    {
        return "fereload";
    }

    @Override
    public String[] getDefaultSecondaryAliases()
    {
        return new String[] { "reload" };
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "/fereload: Reload FE configuration";
    }

    @Override
    public String getPermissionNode()
    {
        return ForgeEssentials.PERM_RELOAD;
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.OP;
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args)
    {
        reload(sender);
    }

    public static void reload(ICommandSender sender)
    {
        ModuleLauncher.instance.reloadConfigs();
        ChatOutputHandler.chatConfirmation(sender, Translator.translate("Reloaded configs. (may not work for all settings)"));
    }

}
