package com.forgeessentials.worldborder;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.permission.PermissionLevel;

import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.commons.selections.AreaShape;
import com.forgeessentials.commons.selections.Point;
import com.forgeessentials.core.commands.ParserCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.util.CommandParserArgs;

public class CommandWorldBorder extends ParserCommandBase
{

    @Override
    public String getCommandName()
    {
        return "worldborder";
    }

    @Override
    public String[] getDefaultAliases()
    {
        return new String[] { "wb" };
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_)
    {
        return "/wb: Configure worldborder";
    }

    @Override
    public String getPermissionNode()
    {
        return ModuleWorldBorder.PERM_ADMIN;
    }

    @Override
    public PermissionLevel getPermissionLevel()
    {
        return PermissionLevel.OP;
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public void parse(CommandParserArgs arguments) throws CommandException
    {

        if (arguments.isEmpty())
        {
            arguments.confirm("/wb enable|disable");
            arguments.confirm("/wb center here: Set worldborder center");
            arguments.confirm("/wb size <xz> [z]: Set worldborder size");
            arguments.confirm("/wb shape box|ellipse: Set worldborder center");
            WorldBorder border = ModuleWorldBorder.getInstance().getBorder(arguments.senderPlayer.worldObj);
            if (border == null)
            {
                arguments.error("No worldborder set for this world");
                return;
            }
            arguments.notify("Worldborder info:");
            arguments.notify("  center  = " + border.getCenter());
            arguments.notify("  size    = " + border.getSize().getX() + " x " + border.getSize().getZ());
            arguments.notify("  start   = " + border.getArea().getLowPoint());
            arguments.notify("  end     = " + border.getArea().getHighPoint());
            arguments.notify("  shape   = " + (border.getShape() == AreaShape.BOX ? "box" : "ellipse"));
            if (border.isEnabled())
                arguments.confirm("  enabled = true");
            else
                arguments.error("  enabled = false");
            return;
        }

        WorldBorder border = ModuleWorldBorder.getInstance().getBorder(arguments.senderPlayer.worldObj);

        arguments.tabComplete("center", "disable", "enable", "shape", "size", "effect");
        String subCommand = arguments.remove().toLowerCase();
        switch (subCommand)
        {
        case "enable":
            if (arguments.isTabCompletion)
                return;
            border.setEnabled(true);
            border.save();
            arguments.confirm("Worldborder enabled");
            break;
        case "disable":
            if (arguments.isTabCompletion)
                return;
            border.setEnabled(false);
            border.save();
            arguments.confirm("Worldborder disabled");
            break;
        case "shape":
            parseShape(arguments, border);
            break;
        case "center":
            parseCenter(arguments, border);
            break;
        case "size":
            parseRadius(arguments, border);
            break;
        case "effect":
            parseEffect(arguments, border);
            break;
        default:
            throw new TranslatedCommandException(FEPermissions.MSG_UNKNOWN_SUBCOMMAND, subCommand);
        }
    }

    public static void parseCenter(CommandParserArgs arguments, WorldBorder border) throws CommandException
    {
        if (arguments.isEmpty())
        {
            arguments.confirm("Worldborder center at %s", border.getCenter());
            arguments.confirm("/wb center here: Set worldborder center to player position");
            arguments.confirm("/wb center X Z: Set worldborder center to coordinates");
            return;
        }

        arguments.tabComplete("here");
        if (arguments.isTabCompletion)
            return;

        if (arguments.peek().equalsIgnoreCase("here"))
        {
            border.setCenter(new Point(arguments.senderPlayer));
            border.save();
            arguments.confirm("Worldborder center set to current location");
            return;
        }

        int x = arguments.parseInt();
        int z = arguments.parseInt();
        border.setCenter(new Point(x, 64, z));
        border.save();
        arguments.confirm("Worldborder center set to [%d, %d]", x, z);
    }

    public static void parseRadius(CommandParserArgs arguments, WorldBorder border) throws CommandException
    {
        if (arguments.isEmpty())
        {
            arguments.confirm("Worldborder size: %d x %d", border.getSize().getX(), border.getSize().getZ());
            arguments.confirm("/wb size <xz> [z]: Set worldborder size");
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
        arguments.confirm("Worldborder size set to %d x %d", border.getSize().getX(), border.getSize().getZ());
    }

    public static void parseShape(CommandParserArgs arguments, WorldBorder border) throws CommandException
    {
        if (arguments.isEmpty())
        {
            arguments.notify("Worldborder shape: %s", border.getShape() == AreaShape.BOX ? "box" : "ellipse");
            arguments.confirm("/wb shape box|ellipse");
            return;
        }

        arguments.tabComplete("box", "ellipse");
        if (arguments.isTabCompletion)
            return;

        String subCommand = arguments.remove().toLowerCase();
        switch (subCommand)
        {
        case "box":
            border.setShape(AreaShape.BOX);
            border.save();
            arguments.confirm("Worldborder shape set to box");
            break;
        case "ellipse":
            border.setShape(AreaShape.ELLIPSOID);
            border.save();
            arguments.confirm("Worldborder shape set to ellipse");
            break;
        default:
            throw new TranslatedCommandException("Unknown shape type %s", subCommand);
        }
    }

    public static void parseEffect(CommandParserArgs arguments, WorldBorder border) throws CommandException
    {
        if (arguments.isEmpty())
        {
            if (!border.getEffects().isEmpty())
            {
                arguments.notify("Effects applied on this worldborder:");
                for (WorldBorderEffect effect : border.getEffects())
                {
                    arguments.notify(String.format("%d: %s", border.getEffects().indexOf(effect), effect.toString()));
                }
            }
            else
            {
                arguments.notify("No effects are currently applied on this worldborder!");
            }
            arguments.confirm("/wb effect <add [command|damage|kick|knockback|message|potion|smite|block] <trigger> | remove <index>");
            return;
        }

        arguments.tabComplete("add", "remove");

        String subCommand = arguments.remove().toLowerCase();
        switch (subCommand)
        {
        case "add":
            addEffect(border, arguments);
            break;
        case "remove":
            if (arguments.isTabCompletion)
                return;
            int index = Integer.parseInt(arguments.remove().toLowerCase());
            if (border.getEffects().size() >= index && border.getEffects().remove(border.getEffects().get(index)))
                arguments.confirm("Removed effect");
            else
            {
                arguments.error("No such effect!");
                arguments.error("Try using /wb effect to view all available effects.");
                arguments.error("Each one is identified by a number, use that to identify the effect you want to remove.");
            }
            break;
        default:
            arguments.error("Wrong syntax! Try /wb effect <add [command|damage|kick|knockback|message|potion|smite|block] <trigger> | remove <index>");
        }

        border.save();

    }

    public static void addEffect(WorldBorder border, CommandParserArgs arguments) throws CommandException
    {
        // Get effect type argument
        if (arguments.isEmpty())
        {
            arguments.error("No effect provided! How about trying one of these:");
            arguments.error("command, damage, kick, knockback, message, potion ,smite");
            return;
        }
        arguments.tabComplete("command", "damage", "kick", "knockback", "message", "potion", "smite");
        String subCommand = arguments.remove().toLowerCase();

        // Get distance argument
        if (arguments.isEmpty())
            throw new TranslatedCommandException("Missing distance argument");
        int trigger = arguments.parseInt();

        if (arguments.isTabCompletion)
            return;

        WorldBorderEffect effect = WorldBorderEffects.valueOf(subCommand.toUpperCase()).get();
        if (effect == null)
        {
            arguments.error(String.format("Could not find an effect with name %s, how about trying one of these:", subCommand));
            arguments.error("command, damage, kick, knockback, message, potion ,smite");
            return;
        }

        effect.provideArguments(arguments);
        effect.triggerDistance = trigger;
        border.addEffect(effect);
        arguments.confirm("Effect added!");
    }

}
