package com.forgeessentialsclient.core;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.Entity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import com.forgeessentialsclient.ForgeEssentialsClient;
import com.forgeessentialsclient.utils.commons.BuildInfo;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;

/**
 * Note: This command only exists within FEClient so no FE server utilities can be used!
 */
public class FEClientCommand extends BaseCommand {
	
	public FEClientCommand(String name, int permissionLevel, boolean enabled) {
		super(name, permissionLevel, enabled);
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
    	Entity entity = commandContext.getSource().getEntity();
    	if (entity != null) {
    		if (num == 0)
            {
            	ITextComponent msg = new StringTextComponent("/feclient info: Get FE client info");
            	commandContext.getSource().getEntity().sendMessage(msg, entity.getUUID());
                ITextComponent msg2 = new StringTextComponent("/feclient reinit: Redo server handshake");
            	commandContext.getSource().getEntity().sendMessage(msg2, entity.getUUID());
            }
            if (num == 1)
            {
            	ForgeEssentialsClient.resendHandshake();
                ITextComponent msg = new StringTextComponent("Resent handshake packet to server.");
            	commandContext.getSource().getEntity().sendMessage(msg, entity.getUUID());
            }
            if (num == 2)
            {
                ITextComponent msg = new StringTextComponent(String.format("Running ForgeEssentials client %s (%s)", //
                        BuildInfo.getFullVersion(), BuildInfo.getBuildHash()));
            	commandContext.getSource().getEntity().sendMessage(msg, entity.getUUID());
            	
                ITextComponent msg2 = new StringTextComponent("\"Please refer to https://github.com/ForgeEssentials/ForgeEssentialsMain/wiki/Team-Information if you would like more information about the FE developers.");
            	commandContext.getSource().getEntity().sendMessage(msg2, entity.getUUID());
            }
    	}
        return Command.SINGLE_SUCCESS;
    }

}
