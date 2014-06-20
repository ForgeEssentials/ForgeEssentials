package com.forgeessentials.commands;

import com.forgeessentials.api.permissions.RegGroup;
import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.core.misc.FriendlyItemList;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.OutputHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.List;

public class CommandGive extends FEcmdModuleCommands {
    @Override
    public String getCommandName()
    {
        return "give";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args)
    {
        if (args.length < 2 || args.length > 3)
        {
            OutputHandler.chatError(sender, "Improper syntax. Please try this instead: <player> <item[:meta]> [amount]");
        }

        int id = 1;
        int amount = 64;
        int dam = 0;

        // Amount is specified
        if (args.length == 3)
        {
            amount = parseIntBounded(sender, args[2], 0, 64);
        }

        // Parse the item
        int[] idAndMeta = FunctionHelper.parseIdAndMetaFromString(args[1], false);
        id = idAndMeta[0];
        dam = idAndMeta[1];

        if (dam == -1)
        {
            dam = 0;
        }

        EntityPlayerMP player = FunctionHelper.getPlayerForName(sender, args[0]);
        if (player != null)
        {
            ItemStack stack = new ItemStack(id, amount, dam);
            player.inventory.addItemStackToInventory(stack.copy());
            String name = Item.itemsList[id].getItemStackDisplayName(stack);
            OutputHandler.chatConfirmation(sender, String.format("Giving %1$s %2$d of %3$s", args[0], amount, name));
        }
        else
        {
            OutputHandler.chatError(sender, String.format("Player %s does not exist, or is not online.", args[0]));
        }
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        if (args.length == 1)
        {
            return getListOfStringsMatchingLastWord(args, FMLCommonHandler.instance().getMinecraftServerInstance().getAllUsernames());
        }
        else if (args.length == 2)
        {
            return getListOfStringsFromIterableMatchingLastWord(args, FriendlyItemList.instance().getItemList());
        }
        else
        {
            return null;
        }
    }

    @Override
    public RegGroup getReggroup()
    {
        return RegGroup.OWNERS;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {

        return "/give <player> <item[:meta]> [amount] Give a player an item.";
    }
}
