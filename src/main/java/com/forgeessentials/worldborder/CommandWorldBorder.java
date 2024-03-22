package com.forgeessentials.worldborder;

import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.commons.selections.AreaShape;
import com.forgeessentials.commons.selections.Point;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.commands.registration.FECommandParsingException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.output.logger.LoggingHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.commands.arguments.MobEffectArgument;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import org.jetbrains.annotations.NotNull;

public class CommandWorldBorder extends ForgeEssentialsCommandBuilder
{

    public CommandWorldBorder(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public @NotNull String getPrimaryAlias()
    {
        return "worldborder";
    }

    @Override
    public String @NotNull [] getDefaultSecondaryAliases()
    {
        return new String[] { "wb" };
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.OP;
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> setExecution()
    {
        return baseBuilder
                .then(Commands.argument("world", DimensionArgument.dimension())
                        .then(Commands.literal("info").executes(CommandContext -> execute(CommandContext, "info")))
                        .then(Commands.literal("enable").executes(CommandContext -> execute(CommandContext, "enable")))
                        .then(Commands.literal("disable")
                                .executes(CommandContext -> execute(CommandContext, "disable")))
                        .then(Commands.literal("center")
                                .then(Commands.literal("info")
                                        .executes(CommandContext -> execute(CommandContext, "center-info")))
                                .then(Commands.literal("set")
                                        .then(Commands.argument("location", BlockPosArgument.blockPos())
                                                .executes(CommandContext -> execute(CommandContext, "center-set")))))
                        .then(Commands.literal("shape")
                                .then(Commands.literal("info")
                                        .executes(CommandContext -> execute(CommandContext, "shape-info")))
                                .then(Commands
                                        .literal("set")
                                        .then(Commands.literal("box")
                                                .executes(CommandContext -> execute(CommandContext, "shape-box")))
                                        .then(Commands
                                                .literal("ellipse")
                                                .executes(CommandContext -> execute(CommandContext, "shape-ellipse")))))
                        .then(Commands.literal("size")
                                .then(Commands.literal("info")
                                        .executes(CommandContext -> execute(CommandContext, "size-info")))
                                .then(Commands.literal("set").then(Commands.literal("radius")
                                        .then(Commands.argument("radius", IntegerArgumentType.integer())
                                                .executes(CommandContext -> execute(CommandContext, "size-radius"))))
                                        .then(Commands.literal("XandZ").then(Commands
                                                .argument("x-radius", IntegerArgumentType.integer())
                                                .then(Commands.argument("z-radius", IntegerArgumentType.integer())
                                                        .executes(CommandContext -> execute(CommandContext,
                                                                "size-indiv")))))))
                        .then(Commands.literal("effect")
                                .then(Commands.literal("info")
                                        .executes(CommandContext -> execute(CommandContext, "effect-info")))
                                .then(Commands.literal("add")
                                        .then(Commands.literal(
                                                "list")
                                                .executes(CommandContext -> execute(CommandContext, "effect-add-list")))
                                        .then(Commands.literal("add").then(Commands
                                                .argument("TriggerDistance", IntegerArgumentType.integer())
                                                .then(Commands.literal("command").then(Commands
                                                        .argument("interval", IntegerArgumentType.integer())
                                                        .then(Commands
                                                                .argument("command", StringArgumentType.greedyString())
                                                                .executes(CommandContext -> execute(CommandContext,
                                                                        "effect-add-command")))))
                                                .then(Commands.literal("damage").then(Commands
                                                        .argument("timeout", IntegerArgumentType.integer())
                                                        .then(Commands.argument("damage", IntegerArgumentType.integer())
                                                                .executes(CommandContext -> execute(CommandContext,
                                                                        "effect-add-damage")))))
                                                .then(Commands.literal("kick").then(Commands.argument(
                                                        "timeout", IntegerArgumentType.integer())
                                                        .executes(CommandContext -> execute(CommandContext,
                                                                "effect-add-kick"))))
                                                .then(Commands.literal("knockback").executes(CommandContext -> execute(
                                                        CommandContext, "effect-add-knockback")))
                                                .then(Commands.literal("message").then(Commands.argument("interval",
                                                        IntegerArgumentType.integer())
                                                        .then(Commands.argument("message",
                                                                StringArgumentType.greedyString()).executes(
                                                                        CommandContext -> execute(CommandContext,
                                                                                "effect-add-message")))))
                                                .then(Commands.literal("potion").then(Commands
                                                        .argument("interval", IntegerArgumentType.integer())
                                                        .then(Commands.argument("effect", MobEffectArgument.effect())
                                                                .then(Commands
                                                                        .argument("seconds",
                                                                                IntegerArgumentType.integer())
                                                                        .then(Commands
                                                                                .argument("amplifier",
                                                                                        IntegerArgumentType.integer())
                                                                                .executes(CommandContext -> execute(
                                                                                        CommandContext,
                                                                                        "effect-add-potion")))))))
                                                .then(Commands.literal("smite")
                                                        .then(Commands
                                                                .argument("interval", IntegerArgumentType.integer())
                                                                .executes(CommandContext -> execute(CommandContext,
                                                                        "effect-add-smite")))))))
                                .then(Commands.literal("remove")
                                        .then(Commands.argument("index", IntegerArgumentType.integer()).executes(
                                                CommandContext -> execute(CommandContext, "effect-remove"))))))
                .then(Commands.literal("help").executes(CommandContext -> execute(CommandContext, "help")));
    }

    @Override
    public int execute(CommandContext<CommandSourceStack> ctx, String params) throws CommandSyntaxException
    {
        if (params.equals("help"))
        {
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "/wb enable|disable");
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "/wb center here: Set worldborder center");
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "/wb size <xz> [z]: Set worldborder size");
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "/wb shape box|ellipse: Set worldborder center");
            return Command.SINGLE_SUCCESS;
        }

        ServerLevel worldToUse = DimensionArgument.getDimension(ctx, "world");

        WorldBorder border = ModuleWorldBorder.getInstance().getBorder(worldToUse);

        if (params.equals("info"))
        {
            ChatOutputHandler.chatNotification(ctx.getSource(), "Worldborder info:");
            ChatOutputHandler.chatNotification(ctx.getSource(), "  center  = " + border.getCenter());
            ChatOutputHandler.chatNotification(ctx.getSource(),
                    "  size    = " + border.getSize().getX() + " x " + border.getSize().getZ());
            ChatOutputHandler.chatNotification(ctx.getSource(), "  start   = " + border.getArea().getLowPoint());
            ChatOutputHandler.chatNotification(ctx.getSource(), "  end     = " + border.getArea().getHighPoint());
            ChatOutputHandler.chatNotification(ctx.getSource(),
                    "  shape   = " + (border.getShape() == AreaShape.BOX ? "box" : "ellipse"));
            if (border.isEnabled())
                ChatOutputHandler.chatConfirmation(ctx.getSource(), "  enabled = true");
            else
                ChatOutputHandler.chatError(ctx.getSource(), "  enabled = false");
            return Command.SINGLE_SUCCESS;
        }

        if (params.equals("enable"))
        {
            border.setEnabled(true);
            border.save();
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "Worldborder enabled");
            return Command.SINGLE_SUCCESS;
        }

        if (params.equals("disable"))
        {
            border.setEnabled(false);
            border.save();
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "Worldborder disabled");
            return Command.SINGLE_SUCCESS;
        }

        if (params.startsWith("effect-"))
        {
            parseEffect(ctx, border, params);
        }
        else
        {
            switch (params)
            {
            case "shape-info":
            case "shape-ellipse":
            case "shape-box":
                parseShape(ctx, border, params);
                break;
            case "center-info":
            case "center-set":
                parseCenter(ctx, border, params);
                break;
            case "size-info":
            case "size-radius":
            case "size-indiv":
                parseRadius(ctx, border, params);
                break;
            default:
                ChatOutputHandler.chatError(ctx.getSource(),
                        Translator.format(FEPermissions.MSG_UNKNOWN_SUBCOMMAND, params));
            }
        }
        return Command.SINGLE_SUCCESS;
    }

    public static void parseCenter(CommandContext<CommandSourceStack> ctx, WorldBorder border, String params)
            throws CommandSyntaxException
    {
        if (params.equals("center-info"))
        {
            ChatOutputHandler.chatConfirmation(ctx.getSource(),
                    Translator.format("Worldborder center at %s", border.getCenter()));
            ChatOutputHandler.chatConfirmation(ctx.getSource(),
                    "/wb center here: Set worldborder center to player position");
            ChatOutputHandler.chatConfirmation(ctx.getSource(),
                    "/wb center X Z: Set worldborder center to coordinates");
            return;
        }

        int x = BlockPosArgument.getLoadedBlockPos(ctx, "location").getX();
        int z = BlockPosArgument.getLoadedBlockPos(ctx, "location").getZ();
        border.setCenter(new Point(x, 64, z));
        border.save();
        ChatOutputHandler.chatConfirmation(ctx.getSource(),
                Translator.format("Worldborder center set to [%d, %d]", x, z));
    }

    public static void parseRadius(CommandContext<CommandSourceStack> ctx, WorldBorder border, String params)
            throws CommandSyntaxException
    {
        if (params.equals("size-info"))
        {
            ChatOutputHandler.chatConfirmation(ctx.getSource(),
                    Translator.format("Worldborder size: %d x %d", border.getSize().getX(), border.getSize().getZ()));
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "/wb size <xz> [z]: Set worldborder size");
            return;
        }
        int xSize;
        int zSize;
        if (params.equals("size-radius"))
        {
            xSize = IntegerArgumentType.getInteger(ctx, "radius");
            zSize = xSize;
        }
        else
        {
            xSize = IntegerArgumentType.getInteger(ctx, "x-radius");
            zSize = IntegerArgumentType.getInteger(ctx, "z-radius");
        }
        border.getSize().setX(xSize);
        border.getSize().setZ(zSize);
        border.updateArea();
        border.save();
        ChatOutputHandler.chatConfirmation(ctx.getSource(),
                Translator.format("Worldborder size set to %d x %d", border.getSize().getX(), border.getSize().getZ()));
    }

    public static void parseShape(CommandContext<CommandSourceStack> ctx, WorldBorder border, String params)
            throws CommandSyntaxException
    {
        if (params.equals("shape-info"))
        {
            ChatOutputHandler.chatConfirmation(ctx.getSource(),
                    Translator.format("Worldborder shape: %s", border.getShape() == AreaShape.BOX ? "box" : "ellipse"));
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "/wb shape box|ellipse");
            return;
        }

        if (params.equals("shape-box"))
        {
            border.setShape(AreaShape.BOX);
            border.save();
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "Worldborder shape set to box");
            return;
        }
        if (params.equals("shape-ellipse"))
        {
            border.setShape(AreaShape.ELLIPSOID);
            border.save();
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "Worldborder shape set to ellipse");
            return;
        }
    }

    public static void parseEffect(CommandContext<CommandSourceStack> ctx, WorldBorder border, String params)
            throws CommandSyntaxException
    {
        if (params.equals("effect-info"))
        {
            if (!border.getEffects().isEmpty())
            {
                ChatOutputHandler.chatNotification(ctx.getSource(), "Effects applied on this worldborder:");
                for (WorldBorderEffect effect : border.getEffects())
                {
                    ChatOutputHandler.chatNotification(ctx.getSource(),
                            String.format("%d: %s", border.getEffects().indexOf(effect), effect.toString()));
                }
            }
            else
            {
                ChatOutputHandler.chatNotification(ctx.getSource(),
                        "No effects are currently applied on this worldborder!");
            }
            ChatOutputHandler.chatConfirmation(ctx.getSource(),
                    "/wb effect <add [command|damage|kick|knockback|message|potion|smite] <trigger> | remove <index>");
            return;
        }
        if (params.equals("effect-remove"))
        {
            int index = IntegerArgumentType.getInteger(ctx, "index");
            if (border.getEffects().size() >= index && border.getEffects().remove(border.getEffects().get(index)))
                ChatOutputHandler.chatConfirmation(ctx.getSource(), "Removed effect");
            else
            {
                ChatOutputHandler.chatError(ctx.getSource(), "No such effect!");
                ChatOutputHandler.chatError(ctx.getSource(), "Try using /wb effect to view all available effects.");
                ChatOutputHandler.chatError(ctx.getSource(),
                        "Each one is identified by a number, use that to identify the effect you want to remove.");
            }
        }
        else if (params.startsWith("effect-add"))
        {
            addEffect(ctx, border, params);
        }
        border.save();
        return;
    }

    public static void addEffect(CommandContext<CommandSourceStack> ctx, WorldBorder border, String params)
            throws CommandSyntaxException
    {
        // Get effect type argument
        if (params.equals("effect-add-list"))
        {
            ChatOutputHandler.chatError(ctx.getSource(), "No effect provided! How about trying one of these:");
            ChatOutputHandler.chatError(ctx.getSource(),
                    "command, damage, kick, knockback, message, potion, smite");
            return;
        }

        int trigger = IntegerArgumentType.getInteger(ctx, "TriggerDistance");

        String type = params.split("-")[2];
        LoggingHandler.felog.error(params.split("-")[0]);
        LoggingHandler.felog.error(params.split("-")[1]);
        LoggingHandler.felog.error(params.split("-")[2]);

        WorldBorderEffect effect = WorldBorderEffects.valueOf(type.toUpperCase()).get();
        if (effect == null)
        {
            ChatOutputHandler.chatError(ctx.getSource(),
                    String.format("Could not find an effect with name %s, how about trying one of these:", type));
            ChatOutputHandler.chatError(ctx.getSource(),
                    "command, damage, kick, knockback, message, potion, smite");
            return;
        }

        try
        {
            effect.provideArguments(ctx);
        }
        catch (FECommandParsingException e)
        {
            ChatOutputHandler.chatError(ctx.getSource(), e.error);
            return;
        }
        effect.triggerDistance = trigger;
        border.addEffect(effect);
        ChatOutputHandler.chatConfirmation(ctx.getSource(), "Effect added!");
    }
}
