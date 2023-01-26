package com.forgeessentials.worldborder;

import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.world.World;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.commons.selections.AreaShape;
import com.forgeessentials.commons.selections.Point;
import com.forgeessentials.core.commands.BaseCommand;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.output.LoggingHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class CommandWorldBorder extends BaseCommand
{

    public CommandWorldBorder(String name, int permissionLevel, boolean enabled)
    {
        super(name, permissionLevel, enabled);
    }

    @Override
    public String getPrimaryAlias()
    {
        return "worldborder";
    }

    @Override
    public String[] getDefaultSecondaryAliases()
    {
        return new String[] { "wb" };
    }

    @Override
    public String getPermissionNode()
    {
        return ModuleWorldBorder.PERM_ADMIN;
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
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int execute(CommandContext<CommandSource> ctx, Object... params) throws CommandSyntaxException
    {
        Integer dim = null;
        if (!arguments.isEmpty())
        {
            try
            {
                dim = Integer.parseInt(arguments.peek());
                arguments.remove();
            }
            catch (NumberFormatException e)
            {
                LoggingHandler.felog.info(e);
            }
        }

        World worldToUse;
        if (dim != null || arguments.senderPlayer == null)
        {
            worldToUse = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(dim != null ? dim : 0);
        }
        else
        {
            worldToUse = arguments.senderPlayer.level;
        }

        WorldBorder border = ModuleWorldBorder.getInstance().getBorder(worldToUse);

        if (arguments.isEmpty())
        {
            ChatOutputHandler.chatConfirmation(ctx.getSource(),"/wb enable|disable");
            ChatOutputHandler.chatConfirmation(ctx.getSource(),"/wb center here: Set worldborder center");
            ChatOutputHandler.chatConfirmation(ctx.getSource(),"/wb size <xz> [z]: Set worldborder size");
            ChatOutputHandler.chatConfirmation(ctx.getSource(),"/wb shape box|ellipse: Set worldborder center");
            if (border == null)
            {
                ChatOutputHandler.chatError(ctx.getSource(),"No worldborder set for this world");
                return Command.SINGLE_SUCCESS;
            }
            ChatOutputHandler.chatNotification(ctx.getSource(),"Worldborder info:");
            ChatOutputHandler.chatNotification(ctx.getSource(),"  center  = " + border.getCenter());
            ChatOutputHandler.chatNotification(ctx.getSource(),"  size    = " + border.getSize().getX() + " x " + border.getSize().getZ());
            ChatOutputHandler.chatNotification(ctx.getSource(),"  start   = " + border.getArea().getLowPoint());
            ChatOutputHandler.chatNotification(ctx.getSource(),"  end     = " + border.getArea().getHighPoint());
            ChatOutputHandler.chatNotification(ctx.getSource(),"  shape   = " + (border.getShape() == AreaShape.BOX ? "box" : "ellipse"));
            if (border.isEnabled())
                ChatOutputHandler.chatConfirmation(ctx.getSource(),"  enabled = true");
            else
                ChatOutputHandler.chatError(ctx.getSource(),"  enabled = false");
            return Command.SINGLE_SUCCESS;
        }

        //arguments.tabComplete("center", "disable", "enable", "shape", "size", "effect");
        String subCommand = arguments.remove().toLowerCase();
        switch (subCommand)
        {
        case "enable":
            border.setEnabled(true);
            border.save();
            ChatOutputHandler.chatConfirmation(ctx.getSource(),"Worldborder enabled");
            break;
        case "disable":
            border.setEnabled(false);
            border.save();
            ChatOutputHandler.chatConfirmation(ctx.getSource(),"Worldborder disabled");
            break;
        case "shape":
            parseShape(ctx, border);
            break;
        case "center":
            parseCenter(ctx, border);
            break;
        case "size":
            parseRadius(ctx, border);
            break;
        case "effect":
            parseEffect(ctx, border);
            break;
        default:
            throw new TranslatedCommandException(FEPermissions.MSG_UNKNOWN_SUBCOMMAND, subCommand);
        }
    }

    public static void parseCenter(CommandContext<CommandSource> ctx, WorldBorder border, Object... params) throws CommandException
    {
        if (arguments.isEmpty())
        {
            ChatOutputHandler.chatConfirmation(ctx.getSource(),Translator.format("Worldborder center at %s", border.getCenter()));
            ChatOutputHandler.chatConfirmation(ctx.getSource(),"/wb center here: Set worldborder center to player position");
            ChatOutputHandler.chatConfirmation(ctx.getSource(),"/wb center X Z: Set worldborder center to coordinates");
            return;
        }

        //arguments.tabComplete("here");

        if (arguments.peek().equalsIgnoreCase("here"))
        {
            border.setCenter(new Point(arguments.senderPlayer));
            border.save();
            ChatOutputHandler.chatConfirmation(ctx.getSource(),"Worldborder center set to current location");
            return;
        }

        int x = arguments.parseInt();
        int z = arguments.parseInt();
        border.setCenter(new Point(x, 64, z));
        border.save();
        ChatOutputHandler.chatConfirmation(ctx.getSource(),Translator.format("Worldborder center set to [%d, %d]", x, z));
    }

    public static void parseRadius(CommandContext<CommandSource> ctx, WorldBorder border, Object... params) throws CommandException
    {
        if (arguments.isEmpty())
        {
            ChatOutputHandler.chatConfirmation(ctx.getSource(),Translator.format("Worldborder size: %d x %d", border.getSize().getX(), border.getSize().getZ()));
            ChatOutputHandler.chatConfirmation(ctx.getSource(),"/wb size <xz> [z]: Set worldborder size");
            return;
        }
        int xSize = arguments.parseInt();
        if (arguments.isEmpty())
        {
            border.getSize().setX(xSize);
            border.getSize().setZ(xSize);
        }
        else
        {
            int zSize = arguments.parseInt();
            border.getSize().setX(xSize);
            border.getSize().setZ(zSize);
        }
        border.updateArea();
        border.save();
        ChatOutputHandler.chatConfirmation(ctx.getSource(),Translator.format("Worldborder size set to %d x %d", border.getSize().getX(), border.getSize().getZ()));
    }

    public static void parseShape(CommandContext<CommandSource> ctx, WorldBorder border, Object... params) throws CommandException
    {
        if (arguments.isEmpty())
        {
            ChatOutputHandler.chatConfirmation(ctx.getSource(),Translator.format("Worldborder shape: %s", border.getShape() == AreaShape.BOX ? "box" : "ellipse"));
            ChatOutputHandler.chatConfirmation(ctx.getSource(),"/wb shape box|ellipse");
            return;
        }

        //arguments.tabComplete("box", "ellipse");

        String subCommand = arguments.remove().toLowerCase();
        switch (subCommand)
        {
        case "box":
            border.setShape(AreaShape.BOX);
            border.save();
            ChatOutputHandler.chatConfirmation(ctx.getSource(),"Worldborder shape set to box");
            break;
        case "ellipse":
            border.setShape(AreaShape.ELLIPSOID);
            border.save();
            ChatOutputHandler.chatConfirmation(ctx.getSource(),"Worldborder shape set to ellipse");
            break;
        default:
            throw new TranslatedCommandException("Unknown shape type %s", subCommand);
        }
    }

    public static void parseEffect(CommandContext<CommandSource> ctx, WorldBorder border, Object... params) throws CommandException
    {
        if (arguments.isEmpty())
        {
            if (!border.getEffects().isEmpty())
            {
                ChatOutputHandler.chatNotification(ctx.getSource(),"Effects applied on this worldborder:");
                for (WorldBorderEffect effect : border.getEffects())
                {
                    ChatOutputHandler.chatNotification(ctx.getSource(),String.format("%d: %s", border.getEffects().indexOf(effect), effect.toString()));
                }
            }
            else
            {
                ChatOutputHandler.chatNotification(ctx.getSource(),"No effects are currently applied on this worldborder!");
            }
            ChatOutputHandler.chatConfirmation(ctx.getSource(),"/wb effect <add [command|damage|kick|knockback|message|potion|smite|block] <trigger> | remove <index>");
            return;
        }

        //arguments.tabComplete("add", "remove");

        String subCommand = arguments.remove().toLowerCase();
        switch (subCommand)
        {
        case "add":
            addEffect(border, arguments);
            break;
        case "remove":
            int index = Integer.parseInt(arguments.remove().toLowerCase());
            if (border.getEffects().size() >= index && border.getEffects().remove(border.getEffects().get(index)))
                ChatOutputHandler.chatConfirmation(ctx.getSource(),"Removed effect");
            else
            {
                ChatOutputHandler.chatError(ctx.getSource(),"No such effect!");
                ChatOutputHandler.chatError(ctx.getSource(),"Try using /wb effect to view all available effects.");
                ChatOutputHandler.chatError(ctx.getSource(),"Each one is identified by a number, use that to identify the effect you want to remove.");
            }
            break;
        default:
            ChatOutputHandler.chatError(ctx.getSource(),"Wrong syntax! Try /wb effect <add [command|damage|kick|knockback|message|potion|smite|block] <trigger> | remove <index>");
        }

        border.save();

    }

    public static void addEffect(CommandContext<CommandSource> ctx, WorldBorder border, Object... params) throws CommandException
    {
        // Get effect type argument
        if (arguments.isEmpty())
        {
            ChatOutputHandler.chatError(ctx.getSource(),"No effect provided! How about trying one of these:");
            ChatOutputHandler.chatError(ctx.getSource(),"command, damage, kick, knockback, message, potion ,smite");
            return;
        }
        //arguments.tabComplete("command", "damage", "kick", "knockback", "message", "potion", "smite");
        String subCommand = arguments.remove().toLowerCase();

        // Get distance argument
        if (arguments.isEmpty())
            throw new TranslatedCommandException("Missing distance argument");
        int trigger = arguments.parseInt();

        WorldBorderEffect effect = WorldBorderEffects.valueOf(subCommand.toUpperCase()).get();
        if (effect == null)
        {
            ChatOutputHandler.chatError(ctx.getSource(),String.format("Could not find an effect with name %s, how about trying one of these:", subCommand));
            ChatOutputHandler.chatError(ctx.getSource(),"command, damage, kick, knockback, message, potion ,smite");
            return;
        }

        effect.provideArguments(arguments);
        effect.triggerDistance = trigger;
        border.addEffect(effect);
        ChatOutputHandler.chatConfirmation(ctx.getSource(),"Effect added!");
    }
}
