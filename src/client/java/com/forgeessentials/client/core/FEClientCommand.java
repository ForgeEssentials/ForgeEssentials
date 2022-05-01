package com.forgeessentials.client.core;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import com.forgeessentials.commons.BuildInfo;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

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
				.then(Commands.literal("")
						.executes(source -> execute(source.getSource(), 0)))
				.then(Commands.literal("reinit")
						.executes(source -> execute(source.getSource(), 1)))
				.then(Commands.literal("info")
						.executes(source -> execute(source.getSource(), 2)));
		
	}

    @SuppressWarnings("resource")
	public int execute(CommandSource source, int num)
    {
        if (num == 0)
        {
        	ITextComponent msg = new StringTextComponent("/feclient info: Get FE client info");
        	Minecraft.getInstance().player.sendMessage(msg, Util.NIL_UUID);
            ITextComponent msg2 = new StringTextComponent("/feclient reinit: Redo server handshake");
        	Minecraft.getInstance().player.sendMessage(msg2, Util.NIL_UUID);
        	Minecraft.getInstance().close();
        }
        if (num == 1)
        {
            ClientProxy.resendHandshake();
            ITextComponent msg = new StringTextComponent("Resent handshake packet to server.");
        	Minecraft.getInstance().player.sendMessage(msg, Util.NIL_UUID);
        }
        if (num == 2)
        {
            ITextComponent msg = new StringTextComponent(String.format("Running ForgeEssentials client %s (%s)", //
                    BuildInfo.getFullVersion(), BuildInfo.getBuildHash()));
        	Minecraft.getInstance().player.sendMessage(msg, Util.NIL_UUID);
        	
            ITextComponent msg2 = new StringTextComponent("\"Please refer to https://github.com/ForgeEssentials/ForgeEssentialsMain/wiki/Team-Information if you would like more information about the FE developers.");
        	Minecraft.getInstance().player.sendMessage(msg2, Util.NIL_UUID);
        }
        return Command.SINGLE_SUCCESS;
    }

}
