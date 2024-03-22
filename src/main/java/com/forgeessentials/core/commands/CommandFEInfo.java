package com.forgeessentials.core.commands;

import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.commons.BuildInfo;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.core.mixin.FEMixinConfig;
import com.forgeessentials.core.moduleLauncher.ModuleLauncher;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import org.jetbrains.annotations.NotNull;

public class CommandFEInfo extends ForgeEssentialsCommandBuilder
{

    public CommandFEInfo(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public @NotNull String getPrimaryAlias()
    {
        return "feinfo";
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
    public LiteralArgumentBuilder<CommandSourceStack> setExecution()
    {
        return baseBuilder
                .then(Commands.literal("reload").executes(CommandContext -> execute(CommandContext, "reload")))
                .then(Commands.literal("modules").executes(CommandContext -> execute(CommandContext, "modules")))
                .then(Commands.literal("mixin").executes(CommandContext -> execute(CommandContext, "mixin")))
                .executes(CommandContext -> execute(CommandContext, "blank"));
    }

    @Override
    public int execute(CommandContext<CommandSourceStack> ctx, String params) throws CommandSyntaxException
    {
        if (params.equals("blank"))
        {
            ChatOutputHandler.chatNotification(ctx.getSource(), Translator.format("Running ForgeEssentials %s (%s)-%s",
                    BuildInfo.getCurrentVersion(), BuildInfo.getBuildHash(), BuildInfo.getBuildType()));
            if (BuildInfo.isOutdated())
                ChatOutputHandler.chatError(ctx.getSource(),
                        String.format("Outdated! Latest build is #%s", BuildInfo.getLatestVersion()));
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "/feinfo reload: Reload FE configs");
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "/feinfo modules: Show loaded modules");
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "/feinfo mixin: Show loaded mixin patches");
            return Command.SINGLE_SUCCESS;
        }

        switch (params)
        {
        case "reload":
            CommandFeReload.reload(ctx.getSource());
            return Command.SINGLE_SUCCESS;
        case "modules":
            ChatOutputHandler.chatConfirmation(ctx.getSource(),
                    "Loaded FE modules: " + StringUtils.join(ModuleLauncher.getModuleList(), ", "));
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
