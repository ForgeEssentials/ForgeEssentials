package com.forgeessentials.commands.world;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.commands.util.TickTaskBlockFinder;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.FECommandManager.ConfigurableCommand;

import cpw.mods.fml.common.registry.GameData;

public class CommandFindblock extends FEcmdModuleCommands implements ConfigurableCommand
{

    public static final int defaultCount = 1;
    public static int defaultRange = 20 * 16;
    public static int defaultSpeed = 16 * 16;

    @Override
    public void loadConfig(Configuration config, String category)
    {
        defaultRange = config.get(category, "defaultRange", defaultRange, "Default max distance used.").getInt();
        defaultSpeed = config.get(category, "defaultSpeed", defaultSpeed, "Default speed used.").getInt();
    }

    @Override
    public String[] getDefaultAliases()
    {
        return new String[] { "fb" };
    }

    @Override
    public String getCommandName()
    {
        return "findblock";
    }

    /*
     * syntax: /fb <block> [max distance, def = 20 * 16] [amount of blocks, def = 1] [speed, def = 10]
     */
    @Override
    public void processCommandPlayer(EntityPlayerMP sender, String[] args)
    {
        if (args.length < 2)
        {
            throw new TranslatedCommandException(getCommandUsage(sender));
        }
        String id = args[0];
        int meta = parseInt(sender, args[1]);
        int range = (args.length < 2) ? defaultRange : parseIntWithMin(sender, args[2], 1);
        int amount = (args.length < 3) ? defaultCount : parseIntWithMin(sender, args[3], 1);
        int speed = (args.length < 4) ? defaultSpeed : parseIntWithMin(sender, args[4], 1);

        new TickTaskBlockFinder(sender, id, meta, range, amount, speed);
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        if (args.length == 1)
        {
            List<String> names = new ArrayList<String>();
            for (Item i : GameData.getItemRegistry().typeSafeIterable())
            {
                names.add(i.getUnlocalizedName());
            }
            return getListOfStringsMatchingLastWord(args, names);
        }
        else if (args.length == 2)
        {
            return getListOfStringsMatchingLastWord(args, defaultRange + "");
        }
        else if (args.length == 3)
        {
            return getListOfStringsMatchingLastWord(args, defaultCount + "");
        }
        else if (args.length == 4)
        {
            return getListOfStringsMatchingLastWord(args, defaultSpeed + "");
        }
        else
        {
            throw new TranslatedCommandException(getCommandUsage(sender));
        }
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {
        return RegisteredPermValue.OP;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {

        return "/fb <block> [max distance] [amount of blocks] [speed] Finds a block.";
    }
}