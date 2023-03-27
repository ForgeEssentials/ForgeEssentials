package com.forgeessentials.commands.world;

import java.util.Arrays;
import java.util.List;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.arguments.DimensionArgument;
import net.minecraft.tileentity.CommandBlockLogic;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.commands.util.CommandButcherTickTask;
import com.forgeessentials.commands.util.CommandButcherTickTask.ButcherMobType;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.util.ServerUtil;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;

public class CommandButcher extends ForgeEssentialsCommandBuilder
{

    public CommandButcher(boolean enabled)
    {
        super(enabled);
    }

    public static List<String> typeList = ButcherMobType.getNames();


    @Override
    public String getPrimaryAlias()
    {
        return "butcher";
    }

    @Override
    public String[] getDefaultSecondaryAliases()
    {
        return new String[] { "butcher" };
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
    public String getPermissionNode()
    {
        return ModuleCommands.PERM + ".butcher";
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        return builder
                .then(Commands.argument("radius", IntegerArgumentType.integer(-1))
                        .then(Commands.argument("mob", StringArgumentType.greedyString())
                                .suggests(mob_types)
                                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                        .then(Commands.argument("world", DimensionArgument.dimension())
                                                .executes(CommandContext -> execute(CommandContext, 
                                                        Integer.toString(IntegerArgumentType.getInteger(CommandContext, "radius"))+"&&"+
                                                        StringArgumentType.getString(CommandContext, "mob")+"&&"+
                                                        Integer.toString(BlockPosArgument.getLoadedBlockPos(CommandContext, "pos").getX())+"&&"+
                                                        Integer.toString(BlockPosArgument.getLoadedBlockPos(CommandContext, "pos").getX())+"&&"+
                                                        Integer.toString(BlockPosArgument.getLoadedBlockPos(CommandContext, "pos").getX())+"&&"+
                                                        DimensionArgument.getDimension(CommandContext, "world").dimension().location().toString())
                                                        )
                                                )
                                        )
                                )
                        )
                .executes(CommandContext -> execute(CommandContext, "help")
                        );
    }

    public static final SuggestionProvider<CommandSource> mob_types = (ctx, builder) -> {
        return ISuggestionProvider.suggest(typeList, builder);};

    @Override
    public int processCommandPlayer(CommandContext<CommandSource> ctx, Object... params) throws CommandSyntaxException
    {
        if(params.toString().equals("help")) {
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "Use /butcher <radius> [type] [x y z] [world]");
            return Command.SINGLE_SUCCESS;
        }
        List<String> args = Arrays.asList(params.toString().split("&")); 

        ServerPlayerEntity sender = getServerPlayer(ctx.getSource());
        int radius = -1;
        double x = sender.position().x;
        double y = sender.position().y;
        double z = sender.position().z;
        ServerWorld world = sender.getLevel();
        String mobType = ButcherMobType.HOSTILE.toString();

        if (!args.isEmpty())
        {
            String radiusValue = args.remove(0);
            if (radiusValue.equalsIgnoreCase("world"))
                radius = -1;
            else
                radius = parseInt(radiusValue, -1, Integer.MAX_VALUE);
        }

        if (!args.isEmpty())
            mobType = args.remove(0);

        if (!args.isEmpty())
        {
            if (args.size() < 3)
                throw new TranslatedCommandException("Improper syntax: <radius> [type] [x y z] [world]");
            x = parseDouble(args.remove(0), sender.position().x);
            y = parseDouble(args.remove(0), sender.position().y);
            z = parseDouble(args.remove(0), sender.position().z);
        }

        if (!args.isEmpty())
        {
            world = ServerUtil.getWorldFromString(args.remove(0));
            if (world == null)
                throw new TranslatedCommandException("The specified dimension does not exist");
        }

        AxisAlignedBB pool = new AxisAlignedBB(x - radius, y - radius, z - radius, x + radius + 1, y + radius + 1, z + radius + 1);
        CommandButcherTickTask.schedule(sender.createCommandSourceStack(), world, mobType, pool, radius);
        return Command.SINGLE_SUCCESS;
    }

    @Override
    public int processCommandConsole(CommandContext<CommandSource> ctx, Object... params) throws CommandSyntaxException
    {
        int radius = -1;
        double x = 0;
        double y = 0;
        double z = 0;
        ServerWorld world = ServerLifecycleHooks.getCurrentServer().overworld();
        String mobType = ButcherMobType.HOSTILE.toString();
        List<String> args = Arrays.asList(params.toString().split("&")); 

        if (!args.isEmpty())
        {
            String radiusValue = args.remove(0);
            if (radiusValue.equalsIgnoreCase("world"))
                radius = -1;
            else
                radius = parseInt(radiusValue, 0, Integer.MAX_VALUE);
        }

        if (!args.isEmpty())
            mobType = args.remove(0);

        if (!args.isEmpty())
        {
            if (args.size() < 3)
                throw new TranslatedCommandException("Improper syntax: <radius> [type] [x y z] [world]");
            x = parseInt(args.remove(0));
            y = parseInt(args.remove(0));
            z = parseInt(args.remove(0));
        }
        else
        {
            if (GetSource(ctx.getSource()) instanceof CommandBlockLogic)
            {
                CommandBlockLogic cb = (CommandBlockLogic) GetSource(ctx.getSource());
                world = cb.getLevel();
                Vector3d coords = cb.getPosition();
                x = coords.x;
                y = coords.y;
                z = coords.z;
            }
        }

        if (!args.isEmpty())
        {
            world = ServerUtil.getWorldFromString(args.remove(0));
            if (world == null)
                throw new TranslatedCommandException("This dimension does not exist");
        }
        AxisAlignedBB pool = new AxisAlignedBB(x - radius, y - radius, z - radius, x + radius + 1, y + radius + 1, z + radius + 1);
        CommandButcherTickTask.schedule(ctx.getSource(), world, mobType, pool, radius);
        return Command.SINGLE_SUCCESS;
    }

}
