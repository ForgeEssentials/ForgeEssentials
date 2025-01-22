package com.forgeessentials.playermarket;

import static com.forgeessentials.playermarket.ModulePlayerMarket.PERM_CMD;
import static com.forgeessentials.playermarket.ModulePlayerMarket.PERM_CMD_REMOVE;
import static com.forgeessentials.playermarket.ModulePlayerMarket.PERM_CMD_SELL_BASE;
import static com.forgeessentials.playermarket.ModulePlayerMarket.PERM_CMD_SERVER;
import static com.forgeessentials.playermarket.PlayerMarketContainer.initItems;
import static com.forgeessentials.util.ServerUtil.getItemPermission;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import org.jetbrains.annotations.NotNull;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.core.BasicInteraction;
import com.forgeessentials.core.commands.ParserCommandBase;
import com.forgeessentials.economy.ModuleEconomy;
import com.forgeessentials.playermarket.PlayerMarketData.AuctionStack;
import com.forgeessentials.util.CommandParserArgs;

public class PlayerMarketCommand extends ParserCommandBase
{
    @Override public String getUsage(ICommandSender sender)
    {
        return "market [sell]? [price]?";
    }

    @Override public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override public String getPermissionNode()
    {
        return PERM_CMD;
    }

    @Override public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.ALL;
    }

    @NotNull @Override protected String getPrimaryAlias()
    {
        return "market";
    }

    @Override public List<String> getAliases()
    {
        return Arrays.asList("pshop", "playershop", "auctionhouse", "ah");
    }

    protected void ShowPlayerMarket(CommandParserArgs args, boolean remove)
    {
        if (args.isTabCompletion)
        {
            return;
        }
        EntityPlayerMP player = args.senderPlayer;
        //Take a local copy of itemsListed for basic concurrency.
        ArrayList<AuctionStack> _itemsListed = new ArrayList<>(ModulePlayerMarket.instance().data.itemsListed);
        final boolean multiPage = _itemsListed.size() > 54;
        final int[] currentPage = new int[1];
        InventoryBasic source = new InventoryBasic("Chest", false, multiPage ? 54 : _itemsListed.size());

        if (multiPage)
        {
            initItems(source, 0, 45, _itemsListed, args);
            //Init Menu
            source.setInventorySlotContents(48, new ItemStack(Items.ARROW));
            source.setInventorySlotContents(50, new ItemStack(Items.TIPPED_ARROW));
        }
        else
        {
            initItems(source, 0, 54, _itemsListed, args);
        }
        BasicInteraction menuChest = new BasicInteraction("Player Market", true, source)
        {

            @Override public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn)
            {
                return new PlayerMarketContainer(playerInventory, this, playerIn, multiPage, remove, _itemsListed, args);
            }

            @Override public String getGuiID()
            {
                return "minecraft:chest";
            }
        };

        player.displayGUIChest(menuChest);
    }

    @Override public void parse(CommandParserArgs args) throws CommandException
    {
        if (args.senderPlayer == null)
        {
            args.error("Must be a player to use command!");
            return;
        }
        if (args.isEmpty())
        {
            ShowPlayerMarket(args, false);
            return;
        }

        args.tabComplete("buy", "remove", "server", "sell");
        String arg = args.remove();
        AuctionStack newStack = new AuctionStack();
        switch (arg)
        {
        case "buy":
            ShowPlayerMarket(args, false);
            break;
        case "remove":
            if (!args.hasPermission(PERM_CMD_REMOVE))
            {
                args.error("Not allowed to use subcommand!");
                break;
            }
            ShowPlayerMarket(args, true);
            break;
        case "server":
            if (!args.hasPermission(PERM_CMD_SERVER))
            {
                args.error("Not allowed to use subcommand!");
                break;
            }
            newStack.sellerId = APIRegistry.IDENT_SERVER.getUuid();
            newStack.sellerName = APIRegistry.IDENT_SERVER.getUsername();
        case "sell":
            if (ModulePlayerMarket.instance().data.marketSize >= 0
                    && ModulePlayerMarket.instance().data.itemsListed.size() >= ModulePlayerMarket.instance().data.marketSize)
            {
                args.error("The market is currently full!");
                break;
            }
            newStack.stack = args.senderPlayer.inventory.getCurrentItem();
            Long price = ModuleEconomy.getItemPrice(newStack.stack, UserIdent.get(args.senderPlayer));
            if (newStack.stack == ItemStack.EMPTY)
            {
                args.error("Can't sell your hand!");
                break;
            }

            if (!args.hasPermission(PERM_CMD_SELL_BASE + "." + getItemPermission(newStack.stack)))
            {
                args.error("You don't have permission to sell %s", newStack.stack);
                break;
            }
            if (!args.isEmpty())
            {
                newStack.price = args.parseInt();
                if (newStack.price < 0)
                {
                    args.error("Price can not be negative!");
                    break;
                }
                if (price != null && newStack.price < price)
                {
                    args.warn("Price for %s is lower than server price of %s.  If this is an error, you will need to remove your item!",
                            newStack.stack, price);
                }
            }
            else
            {
                if (price != null)
                {
                    newStack.price = price;
                }
                else
                {
                    args.error("No default price set for %s!", newStack.stack);
                    break;
                }
            }
            if (arg.equals("sell"))
            {
                newStack.sellerId = args.senderPlayer.getUniqueID();
                newStack.sellerName = args.senderPlayer.getName();
                args.senderPlayer.inventory.removeStackFromSlot(args.senderPlayer.inventory.currentItem);

            }

            newStack.stack = newStack.stack.copy();
            ModulePlayerMarket.instance().data.itemsListed.add(newStack);
            args.confirm("%s sold for %s", newStack.stack, APIRegistry.economy.toString(newStack.price));
            ModulePlayerMarket.logTrade("SELL", args.senderPlayer.getName(), newStack);
            break;
        }
    }
}
