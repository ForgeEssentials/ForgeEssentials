package com.forgeessentials.commands.world;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

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
    public String getUsage(ICommandSender sender)
    {
        return "/fb <block> [meta] [max distance] [amount of blocks] [speed] Finds a block.";
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
    public void processCommandPlayer(MinecraftServer server, EntityPlayerMP sender, String[] args) throws CommandException
    {
        if (args.length < 1)
        {
            throw new TranslatedCommandException(getUsage(sender));
        }
        String id = args[0];
        int meta = (args.length < 2) ? 0 : parseInt(args[1]);
        int range = (args.length < 3) ? defaultRange : parseInt(args[2], 1, Integer.MAX_VALUE);
        int amount = (args.length < 4) ? defaultCount : parseInt(args[3], 1, Integer.MAX_VALUE);
        int speed = (args.length < 5) ? defaultSpeed : parseInt(args[4], 1, Integer.MAX_VALUE);

        new TickTaskBlockFinder(sender, id, meta, range, amount, speed);
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos)
    {
        if (args.length == 1)
        {
            List<String> names = new ArrayList<>();
            for (Item i : GameRegistry.findRegistry(Item.class))
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
        return null;
    }

}