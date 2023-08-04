package com.forgeessentials.mapper;

package com.forgeessentials.mapper.command;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.mapper.MapperUtil;
import com.forgeessentials.mapper.ModuleMapper;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class CommandMapper extends ForgeEssentialsCommandBuilder {

	public CommandMapper(boolean enabled) {
		super(enabled);
	}

	@Override
	public String getPrimaryAlias() {
		return "mapper";
	}

	@Override
	public String getPermissionNode() {
		return "fe.mapper.command";
	}

	@Override
	public DefaultPermissionLevel getPermissionLevel() {
		return DefaultPermissionLevel.OP;
	}

	@Override
	public boolean canConsoleUseCommand() {
		return true;
	}

	@Override
	public LiteralArgumentBuilder<CommandSource> setExecution() {
		return builder.executes(CommandContext -> execute(CommandContext));
	}

	@Override
	public int processCommandPlayer(CommandContext<CommandSource> ctx,
			Object... params) throws CommandSyntaxException {
		ServerPlayerEntity player = (ServerPlayerEntity) ctx.getSource()
				.getEntity();
		int x = (int) Math.floor(player.position().x);
		int z = (int) Math.floor(player.position().z);
		ServerWorld world = (ServerWorld) player.getLevel();
		BufferedImage img = ModuleMapper.getInstance().getRegionImage(world,
				MapperUtil.worldToRegion(x), MapperUtil.worldToRegion(z));
		try {
			ImageIO.write(img, "png",
					new File(ForgeEssentials.getFEDirectory(), "region.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Command.SINGLE_SUCCESS;
	}
}
