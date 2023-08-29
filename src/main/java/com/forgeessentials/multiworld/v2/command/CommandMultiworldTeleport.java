package com.forgeessentials.multiworld.v2.command;

import org.jetbrains.annotations.NotNull;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.NamedWorldHandler;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.multiworld.v2.ModuleMultiworldV2;
import com.forgeessentials.multiworld.v2.Multiworld;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.arguments.DimensionArgument;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

public class CommandMultiworldTeleport extends ForgeEssentialsCommandBuilder
{
    public CommandMultiworldTeleport(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public @NotNull String getPrimaryAlias()
    {
        return "mwtp";
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.OP;
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        return baseBuilder
        		.then(Commands.argument("target", EntityArgument.player())
        				.then(Commands.argument("dim", DimensionArgument.dimension())
        						.then(Commands.argument("pos", BlockPosArgument.blockPos())
        								.executes(CommandContext -> execute(CommandContext, "others")))));
    }

    @Override
    public int execute(CommandContext<CommandSource> ctx, String params) throws CommandSyntaxException
    {
    	ServerPlayerEntity player = EntityArgument.getPlayer(ctx, "target");
    	ServerWorld dim = DimensionArgument.getDimension(ctx, "dim");
    	BlockPos pos = BlockPosArgument.getOrLoadBlockPos(ctx, "pos");

    	Multiworld multiworld = ModuleMultiworldV2.getMultiworldManager()
				.getMultiworld(dim.dimension().location().toString());
		ServerWorld world = multiworld != null
				? multiworld.getWorldServer()
				: APIRegistry.namedWorldHandler.getWorld(dim.dimension().location().toString());
		if (world == null) {
			ChatOutputHandler.chatError(player.createCommandSourceStack(), "Could not find world " + dim.dimension().location().toString());
			return Command.SINGLE_SUCCESS;
		}
		
        String msg = "Teleporting to ";
		if (multiworld == null) {
			switch (dim.dimension().location().toString()) {
				case NamedWorldHandler.WORLD_NAME_OVERWORLD :
					msg += "the overworld";
					break;
				case NamedWorldHandler.WORLD_NAME_NETHER :
					msg += "the nether";
					break;
				case NamedWorldHandler.WORLD_NAME_END :
					msg += "the end";
					break;
				default :
					msg += "dimension #" + dim.dimension().location().toString();
					break;
			}
		} else {
			msg += multiworld.getName();
		}
		msg = Translator.format(msg + " at [%.0f, %.0f, %.0f]", pos.getX(), pos.getY(), pos.getZ());
		ChatOutputHandler.chatConfirmation(player.createCommandSourceStack(),
				msg);
		Multiworld.teleport(player, world, pos.getX(), pos.getY(), pos.getZ(), false);
        return Command.SINGLE_SUCCESS;
    }

    private static boolean canEntityTeleport(Entity entity)
    {
        return !entity.isPassenger() && !entity.isVehicle() && entity.canChangeDimensions();
    }
}
