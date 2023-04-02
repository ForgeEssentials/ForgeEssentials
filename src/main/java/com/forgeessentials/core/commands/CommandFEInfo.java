package com.forgeessentials.core.commands;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.commons.BuildInfo;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.core.moduleLauncher.ModuleLauncher;
import com.forgeessentials.core.preloader.mixin.FEMixinConfig;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class CommandFEInfo extends ForgeEssentialsCommandBuilder
{

    public CommandFEInfo(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public String getPrimaryAlias()
    {
        return "feinfo";
    }

    @Override
    public String getPermissionNode()
    {
        return ForgeEssentials.PERM_INFO;
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
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        return builder
                .then(Commands.literal("reload")
                        .executes(CommandContext -> execute(CommandContext, "reload")
                                )
                        )
                .then(Commands.literal("modules")
                        .executes(CommandContext -> execute(CommandContext, "modules")
                                )
                        )
                .then(Commands.literal("mixin")
                        .executes(CommandContext -> execute(CommandContext, "mixin")
                                )
                        )
                .executes(CommandContext -> execute(CommandContext, "blank")
                        );
    }

    @Override
    public int execute(CommandContext<CommandSource> ctx, Object... params) throws CommandSyntaxException
    {
        if (params.toString() == "blank")
        {
            ChatOutputHandler.chatNotification(ctx.getSource(), Translator.format("Running ForgeEssentials %s-%s", BuildInfo.getFullVersion(), BuildInfo.getBuildType()));
            if (BuildInfo.isOutdated())
                ChatOutputHandler.chatError(ctx.getSource(), String.format("Outdated! Latest build is #%d", BuildInfo.getBuildNumberLatest()));
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "/feinfo reload: Reload FE configs");
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "/feinfo modules: Show loaded modules");
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "/feinfo mixin: Show loaded mixin patches");
            return Command.SINGLE_SUCCESS;
        }
        
        String subCmd = params.toString();
        switch (subCmd)
        {
        case "reload":
            CommandFeReload.reload(ctx.getSource());
            return Command.SINGLE_SUCCESS;
        case "modules":
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "Loaded FE modules: " + StringUtils.join(ModuleLauncher.getModuleList(), ", "));
            return Command.SINGLE_SUCCESS;
        case "mixin":
            ChatOutputHandler.chatNotification(ctx.getSource(), "Injected patches:");
            for (String patch : FEMixinConfig.getInjectedPatches())
                ChatOutputHandler.chatConfirmation(ctx.getSource(), "- " + patch);
            return Command.SINGLE_SUCCESS;
        }
       return Command.SINGLE_SUCCESS;
    }
}
