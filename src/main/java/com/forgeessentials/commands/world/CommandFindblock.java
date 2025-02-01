package com.forgeessentials.commands.world;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.registry.GameData;
import net.minecraftforge.permission.PermissionLevel;

import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.commands.util.TickTaskBlockFinder;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.FECommandManager.ConfigurableCommand;
import com.forgeessentials.core.misc.TranslatedCommandException;

public class CommandFindblock extends ForgeEssentialsCommandBase implements ConfigurableCommand
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
    public void loadData()
    {
    }

    @Override
    public String getCommandName()
    {
        return "findblock";
    }

    @Override
    public String[] getDefaultAliases()
    {
        return new String[] { "fb" };
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/fb <block> [meta] [max distance] [amount of blocks] [speed] Finds a block.";
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public PermissionLevel getPermissionLevel()
    {
        return PermissionLevel.OP;
    }

    @Override
    public String getPermissionNode()
    {
        return ModuleCommands.PERM + ".findblock";
    }

    @Override
    public void processCommandPlayer(EntityPlayerMP sender, String[] args) throws CommandException
    {
        if (args.length < 1)
        {
            throw new TranslatedCommandException(getCommandUsage(sender));
        }
        String id = args[0];
        // int meta = parseInt(sender, args[1]);
        int meta = (args.length < 2) ? 0 : parseInt(args[1], 0);
        int range = (args.length < 3) ? defaultRange : parseInt(args[2], 1);
        int amount = (args.length < 4) ? defaultCount : parseInt(args[3], 1);
        int speed = (args.length < 5) ? defaultSpeed : parseInt(args[4], 1);

        new TickTaskBlockFinder(sender, id, meta, range, amount, speed);
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos)
    {
        if (args.length == 1)
        {
            List<String> names = new ArrayList<>();
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
            return null;
        }
    }

}