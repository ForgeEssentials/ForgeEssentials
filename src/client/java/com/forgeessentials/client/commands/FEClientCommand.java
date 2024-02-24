package com.forgeessentials.client.commands;

import com.forgeessentials.client.ForgeEssentialsClient;
import com.forgeessentials.client.mixin.FEClientMixinConfig;
import com.forgeessentials.commons.BuildInfo;
import com.forgeessentials.commons.network.NetworkUtils;
import com.forgeessentials.commons.network.packets.Packet00Handshake;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.Entity;
import net.minecraft.util.text.StringTextComponent;

/**
 * Note: This command only exists within FEClient so no FE server utilities can be used!
 */
public class FEClientCommand extends BaseCommand
{

    public FEClientCommand(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public String getPrimaryAlias()
    {
        return "feclient";
    }

    @Override
    public int getPermissionLevel()
    {
        return 0;
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        return builder
                .then(Commands.literal("reinit").executes(CommandContext -> execute(CommandContext, 1))
                        .then(Commands.literal("force").executes(CommandContext -> execute(CommandContext, 3))))
                .then(Commands.literal("info").executes(CommandContext -> execute(CommandContext, 2)))
                .executes(CommandContext -> execute(CommandContext, 0));

    }

    public int execute(CommandContext<CommandSource> commandContext, int num)
    {
        Minecraft instance = Minecraft.getInstance();
        Entity entity = commandContext.getSource().getEntity();
        if (entity != null)
        {
            if (num == 0)
            {
                instance.gui.getChat().addMessage(new StringTextComponent("/feclient info: Get FE client info"));
                instance.gui.getChat().addMessage(new StringTextComponent("/feclient reinit: Redo server handshake"));
                instance.gui.getChat()
                        .addMessage(new StringTextComponent("/feclient reinit force: Force send server handshake"));
            }
            if (num == 1)
            {
                ForgeEssentialsClient.resendHandshake();
                instance.gui.getChat().addMessage(new StringTextComponent("Resent handshake packet to server."));
            }
            if (num == 2)
            {
                instance.gui.getChat().addMessage(new StringTextComponent(String.format("Running ForgeEssentials client %s (%s)-%s", 
                		BuildInfo.getCurrentVersion(), BuildInfo.getBuildHash(), BuildInfo.getBuildType())));
                if (BuildInfo.isOutdated()) {
                	instance.gui.getChat().addMessage(new StringTextComponent(String.format("Outdated! Latest build is #%s", BuildInfo.getLatestVersion())));
                }
                instance.gui.getChat().addMessage(new StringTextComponent(
                        "\"Please refer to https://github.com/ForgeEssentials/ForgeEssentialsMain/wiki/Team-Information if you would like more information about the FE developers."));
                instance.gui.getChat().addMessage(new StringTextComponent("Injected patches:"));
                for (String patch : FEClientMixinConfig.getInjectedPatches())
                    instance.gui.getChat().addMessage(new StringTextComponent("- " + patch));
            }
            if (num == 3)
            {
                ForgeEssentialsClient.sentHandshake();
                NetworkUtils.sendToServer(new Packet00Handshake());
                instance.gui.getChat().addMessage(new StringTextComponent("Force Sent handshake packet to server."));
            }
        }
        return Command.SINGLE_SUCCESS;
    }

}
