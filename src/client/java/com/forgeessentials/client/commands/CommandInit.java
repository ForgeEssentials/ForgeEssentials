package com.forgeessentials.client.commands;

import java.util.ArrayList;

import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.command.CommandSource;
import net.minecraftforge.event.RegisterCommandsEvent;

public class CommandInit {
	private static final ArrayList<BaseCommand> commands = new ArrayList<>();

	public static void registerCommands(final RegisterCommandsEvent event) {
		CommandDispatcher<CommandSource> dispatcher = event.getDispatcher();
		
		commands.add(new FEClientCommand(true));
		
		commands.forEach(command -> {
			if (command.isEnabled() && command.setExecution() != null) {
				dispatcher.register(command.getBuilder());
			}
		});
	}
}
