package com.forgeessentials.core.commands;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.permission.PermissionLevel;

import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.commons.BuildInfo;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.moduleLauncher.ModuleLauncher;
import com.forgeessentials.core.preloader.mixin.FEMixinConfig;
import com.forgeessentials.util.CommandParserArgs;

public class CommandFEInfo extends ParserCommandBase
{

    @Override
    public String getCommandName()
    {
        return "feinfo";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/feinfo: Show info about and mange FE";
    }

    @Override
    public String getPermissionNode()
    {
        return ForgeEssentials.PERM_INFO;
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
            arguments.notify("Running ForgeEssentials %s-%s", BuildInfo.getCurrentVersion(), BuildInfo.getBuildType());
            if (BuildInfo.isOutdated())
                arguments.error(String.format("Outdated! Latest build is #%s", BuildInfo.getLatestVersion()));
            arguments.confirm("/feinfo reload: Reload FE configs");
            arguments.confirm("/feinfo modules: Show loaded modules");
            arguments.confirm("/feinfo mixin: Show loaded mixin patches");
            return;
        }

        arguments.tabComplete("reload", "modules", "mixin");
        String subCmd = arguments.remove().toLowerCase();
        if (arguments.isTabCompletion)
            return;

        switch (subCmd)
        {
        case "reload":
            CommandFeReload.reload(arguments.sender);
            break;
        case "modules":
            arguments.confirm("Loaded FE modules: " + StringUtils.join(ModuleLauncher.getModuleList(), ", "));
            break;
        case "mixin":
            arguments.notify("Injected patches:");
            for (String patch : FEMixinConfig.getInjectedPatches())
                arguments.confirm("- " + patch);
            break;
        default:
            throw new TranslatedCommandException(FEPermissions.MSG_UNKNOWN_SUBCOMMAND, subCmd);
        }
    }

}
