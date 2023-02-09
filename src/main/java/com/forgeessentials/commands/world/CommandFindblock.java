package com.forgeessentials.commands.world;

import net.minecraft.block.BlockState;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockStateArgument;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.commands.util.TickTaskBlockFinder;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.FECommandManager.ConfigurableCommand;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class CommandFindblock extends ForgeEssentialsCommandBuilder implements ConfigurableCommand
{

    public CommandFindblock(boolean enabled)
    {
        super(enabled);
    }

    public static final int defaultCount = 1;
    public static int defaultRange = 20 * 16;
    public static int defaultSpeed = 16 * 16;
    ForgeConfigSpec.IntValue FEdefaultRange;
    ForgeConfigSpec.IntValue FEdefaultSpeed;

    @Override
    public void loadConfig(ForgeConfigSpec.Builder BUILDER, String category)
    {
    	BUILDER.push(category);
    	FEdefaultRange = BUILDER.comment("Default max distance used.").defineInRange("defaultRange", defaultRange, 0, Integer.MAX_VALUE);
    	FEdefaultSpeed = BUILDER.comment("Default speed used.").defineInRange("defaultSpeed", defaultSpeed, 0, Integer.MAX_VALUE);
        BUILDER.pop();
    }

    @Override
    public void loadData()
    {
    	
    }

    @Override
    public void bakeConfig(boolean reload)
    {
    	defaultRange = FEdefaultRange.get();
    	defaultSpeed= FEdefaultSpeed.get();
    }

    @Override
    public String getPrimaryAlias()
    {
        return "findblock";
    }

    @Override
    public String[] getDefaultSecondaryAliases()
    {
        return new String[] { "fb" };
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.OP;
    }

    @Override
    public String getPermissionNode()
    {
        return ModuleCommands.PERM + ".findblock";
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        return builder
                .then(Commands.argument("block", BlockStateArgument.block())
                        .executes(CommandContext -> execute(CommandContext)
                                )
                        );
    }

    @Override
    public int processCommandPlayer(CommandContext<CommandSource> ctx, Object... params) throws CommandSyntaxException
    {
        BlockState id = BlockStateArgument.getBlock(ctx, "block").getState();
        //int range = (args.length < 3) ? defaultRange : parseInt(args[2], 1, Integer.MAX_VALUE);
        //int amount = (args.length < 4) ? defaultCount : parseInt(args[3], 1, Integer.MAX_VALUE);
        //int speed = (args.length < 5) ? defaultSpeed : parseInt(args[4], 1, Integer.MAX_VALUE);
        //TODO add custom ranges
        //new TickTaskBlockFinder(getServerPlayer(ctx.getSource()), id, range, amount, speed);
        new TickTaskBlockFinder(getServerPlayer(ctx.getSource()), id, defaultRange, defaultCount, defaultSpeed);
        return Command.SINGLE_SUCCESS;
    }
}