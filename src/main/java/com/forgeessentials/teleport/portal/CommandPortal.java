package com.forgeessentials.teleport.portal;

import java.util.ArrayList;
import java.util.Map.Entry;

import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.commons.selections.Point;
import com.forgeessentials.commons.selections.Selection;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.util.NamedWorldArea;
import com.forgeessentials.util.NamedWorldPoint;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.selections.SelectionHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;

public class CommandPortal extends ForgeEssentialsCommandBuilder {

	public CommandPortal(boolean enabled) {
		super(enabled);
	}

	@Override
	public String getPrimaryAlias() {
		return "portal";
	}

	@Override
	public boolean canConsoleUseCommand() {
		return false;
	}

	@Override
	public DefaultPermissionLevel getPermissionLevel() {
		return DefaultPermissionLevel.OP;
	}

	@Override
	public LiteralArgumentBuilder<CommandSourceStack> setExecution() {
		return baseBuilder
				.then(Commands.literal("create")
						.then(Commands.argument("portalName", StringArgumentType.word())
								.then(Commands.argument("generateFrame", BoolArgumentType.bool())
										.then(Commands.argument("targetPos", BlockPosArgument.blockPos())
												.then(Commands.argument("targetDim", DimensionArgument.dimension())
														.executes(CommandContext -> execute(CommandContext, "create")
																)
														)
												)
										)
								)
						)
				.then(Commands.literal("recreate")
						.then(Commands.argument("portalName", StringArgumentType.word())
								.then(Commands.argument("generateFrame", BoolArgumentType.bool())
										.then(Commands.argument("targetPos", BlockPosArgument.blockPos())
												.then(Commands.argument("targetDim", DimensionArgument.dimension())
														.executes(CommandContext -> execute(CommandContext, "recreate")
																)
														)
												)
										)
								)
						)
				.then(Commands.literal("target")
						.then(Commands.argument("portalName", StringArgumentType.word())
								.suggests(SUGGEST_PORTALS)
								.then(Commands.argument("targetPos", BlockPosArgument.blockPos())
										.then(Commands.argument("targetDim", DimensionArgument.dimension())
												.executes(CommandContext -> execute(CommandContext, "target")
														)
												)
										)
								)
						)
				.then(Commands.literal("delete")
						.then(Commands.argument("portalName", StringArgumentType.word())
								.suggests(SUGGEST_PORTALS)
								.executes(CommandContext -> execute(CommandContext, "delete")
										)
								)
						)
				.then(Commands.literal("list")
						.executes(CommandContext -> execute(CommandContext, "list")
								)
						);
	}

    public static final SuggestionProvider<CommandSourceStack> SUGGEST_PORTALS = (ctx, builder) -> SharedSuggestionProvider.suggest(new ArrayList<>(PortalManager.getInstance().portals.keySet()), builder);

    @Override
	public int processCommandPlayer(CommandContext<CommandSourceStack> ctx, String params) throws CommandRuntimeException {

		switch (params) {
			case "create" :
				parseCreate(ctx, false);
				break;
			case "recreate" :
				parseCreate(ctx, true);
				break;
			case "target" :
				parseTarget(ctx);
				break;
			case "delete" :
				parseDelete(ctx);
				break;
			case "list" :
				listPortals(ctx);
				break;
			default :
		}
		return Command.SINGLE_SUCCESS;
	}

	private static void parseCreate(CommandContext<CommandSourceStack> ctx, boolean recreate) {

		String name = StringArgumentType.getString(ctx, "portalName");
		if (!recreate && PortalManager.getInstance().portals.containsKey(name)) {
			ChatOutputHandler.chatError(ctx.getSource(), "Portal by that name already exists. Use recreate!");
			return;
		}

		boolean frame = BoolArgumentType.getBool(ctx, "generateFrame");

		int x;
		int y;
		int z;
		String dim;
		try {
			x = BlockPosArgument.getLoadedBlockPos(ctx, "targetPos").getX();
			y = BlockPosArgument.getLoadedBlockPos(ctx, "targetPos").getY();
			z = BlockPosArgument.getLoadedBlockPos(ctx, "targetPos").getZ();
			dim = DimensionArgument.getDimension(ctx, "targetDim").dimension().location().toString();
		} catch (CommandSyntaxException e) {
			ChatOutputHandler.chatError(ctx.getSource(), "Invalid Position");
			return;
		}

		NamedWorldPoint target = new NamedWorldPoint(dim, x, y, z);

		Selection selection = SelectionHandler.getSelection(getServerPlayer(ctx.getSource()));
		if (selection == null || !selection.isValid()) {
			ChatOutputHandler.chatError(ctx.getSource(), "Missing selection");
			return;
		}

		Point size = selection.getSize();
		if (size.getX() > 0 && size.getY() > 0 && size.getZ() > 0) {
			ChatOutputHandler.chatError(ctx.getSource(), "Portal selection must be flat in one axis");
			return;
		}

		Portal portal = new Portal(new NamedWorldArea(selection.getDimension(), selection), target, frame);
		PortalManager.getInstance().add(name, portal);
		ChatOutputHandler.chatConfirmation(ctx.getSource(), "Created new portal leading to %s", target.toString());
	}

	private static void parseTarget(CommandContext<CommandSourceStack> ctx) {

		String name = StringArgumentType.getString(ctx, "portalName");
		if (!PortalManager.getInstance().portals.containsKey(name)) {
			ChatOutputHandler.chatError(ctx.getSource(), "Portal by that name does not exist.");
			return;
		}

		int x;
		int y;
		int z;
		String dim;
		try {
			x = BlockPosArgument.getLoadedBlockPos(ctx, "targetPos").getX();
			y = BlockPosArgument.getLoadedBlockPos(ctx, "targetPos").getY();
			z = BlockPosArgument.getLoadedBlockPos(ctx, "targetPos").getZ();
			dim = DimensionArgument.getDimension(ctx, "targetDim").dimension().location().toString();
		} catch (CommandSyntaxException e) {
			ChatOutputHandler.chatError(ctx.getSource(), "Invalid Position");
			return;
		}
		NamedWorldPoint target = new NamedWorldPoint(dim, x, y, z);

		PortalManager.getInstance().get(name).target = target;
		ChatOutputHandler.chatConfirmation(ctx.getSource(), "Set target for portal %s to %s", name, target.toString());
	}

	private static void parseDelete(CommandContext<CommandSourceStack> ctx) {

		String name = StringArgumentType.getString(ctx, "portalName");
		if (!PortalManager.getInstance().portals.containsKey(name)) {
			ChatOutputHandler.chatError(ctx.getSource(), "Portal by that name does not exist.");
			return;
		}

		PortalManager.getInstance().remove(name);
		ChatOutputHandler.chatConfirmation(ctx.getSource(), "Deleted portal " + name);
	}

	/**
	 * Print lists of portals, their locations and dimensions
	 */
	private static void listPortals(CommandContext<CommandSourceStack> ctx) {
		ChatOutputHandler.chatConfirmation(ctx.getSource(), "Registered portals:");
		for (Entry<String, Portal> entry : PortalManager.getInstance().portals
				.entrySet()) {
			ChatOutputHandler.chatConfirmation(ctx.getSource(), "- " + entry.getKey() + ": "
					+ entry.getValue().getPortalArea().toString());
		}
	}

}