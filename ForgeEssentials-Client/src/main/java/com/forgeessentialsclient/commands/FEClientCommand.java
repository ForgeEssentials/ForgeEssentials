package com.forgeessentialsclient.commands;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.Entity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponent;

import com.forgeessentialsclient.ForgeEssentialsClient;
import com.forgeessentialsclient.utils.commons.BuildInfo;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;

/**
 * Note: This command only exists within FEClient so no FE server utilities can be used!
 */
public class FEClientCommand extends BaseCommand {
	
	public FEClientCommand(boolean enabled) {
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
	public LiteralArgumentBuilder<CommandSource> setExecution() {
		return builder
				.then(Commands.literal("reinit")
						.executes(CommandContext -> execute(CommandContext, 1)))
				.then(Commands.literal("info")
						.executes(CommandContext -> execute(CommandContext, 2)))
				.executes(CommandContext -> execute(CommandContext, 0));
		
	}

	public int execute(CommandContext<CommandSource> commandContext, int num)
    {
		Minecraft instance = Minecraft.getInstance();
    	Entity entity = commandContext.getSource().getEntity();
    	if (entity != null) {
    		if (num == 0)
            {
            	TextComponent msg = new StringTextComponent("/feclient info: Get FE client info");
            	instance.gui.getChat().addMessage(msg);
            	TextComponent msg2 = new StringTextComponent("/feclient reinit: Redo server handshake");
            	instance.gui.getChat().addMessage(msg2);
            }
            if (num == 1)
            {
            	ForgeEssentialsClient.resendHandshake();
            	TextComponent msg = new StringTextComponent("Resent handshake packet to server.");
            	instance.gui.getChat().addMessage(msg);
            }
            if (num == 2)
            {
            	TextComponent msg = new StringTextComponent(String.format("Running ForgeEssentials client %s (%s)", //
                        BuildInfo.getFullVersion(), BuildInfo.getBuildHash()));
            	instance.gui.getChat().addMessage(msg);
            	TextComponent msg2 = new StringTextComponent("\"Please refer to https://github.com/ForgeEssentials/ForgeEssentialsMain/wiki/Team-Information if you would like more information about the FE developers.");
            	instance.gui.getChat().addMessage(msg2);
            }
    	}
        return Command.SINGLE_SUCCESS;
    }

}
