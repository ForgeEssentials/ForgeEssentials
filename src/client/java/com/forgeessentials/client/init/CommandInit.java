package com.forgeessentials.client.init;

import java.util.ArrayList;

import com.forgeessentials.client.core.FEClientCommand;
import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.command.CommandSource;
import net.minecraftforge.event.RegisterCommandsEvent;

public class CommandInit {
	private static final ArrayList<com.forgeessentials.client.core.BaseCommand> commands = new ArrayList<>();

	public static void registerCommands(final RegisterCommandsEvent event) {
		CommandDispatcher<CommandSource> dispatcher = event.getDispatcher();
		
		commands.add(new FEClientCommand("feclient", 0, true));
		
		commands.forEach(command -> {
			if (command.isEnabled() && command.setExecution() != null) {
				dispatcher.register(command.getBuilder());
			}
		});
	}
}
