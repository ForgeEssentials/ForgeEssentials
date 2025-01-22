package com.forgeessentials.playermarket;

import static com.forgeessentials.playermarket.ModulePlayerMarket.PERM_CMD_BUY_BASE;
import static com.forgeessentials.util.ServerUtil.getItemPermission;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.economy.Wallet;
import com.forgeessentials.chat.Mailer.MailerSender;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.playermarket.PlayerMarketData.AuctionStack;
import com.forgeessentials.util.CommandParserArgs;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.output.LoggingHandler;

public class PlayerMarketContainer extends ContainerChest
{
    boolean multiPage;
    boolean remove;
    int currentPage;
    ArrayList<AuctionStack> _itemsListed;
    CommandParserArgs args;

    public PlayerMarketContainer(IInventory playerInventory, IInventory chestInventory,
            EntityPlayer player, boolean multiPage, boolean remove, ArrayList<AuctionStack> _itemsListed, CommandParserArgs args)
    {
        super(playerInventory, chestInventory, player);
        this.multiPage = multiPage;
        this.remove = remove;
        this._itemsListed = _itemsListed;
        this.args = args;

    }

    public static void initItems(IInventory source, int offset, int amount, List<AuctionStack> _itemsListed, CommandParserArgs args)
    {
        int size = offset + amount;
        int invSize = source.getSizeInventory();
        for (int i = offset; i < size; i++)
        {
            if (i >= _itemsListed.size())
            {
                if (i - offset < invSize)
                {
                    source.setInventorySlotContents(i - offset, ItemStack.EMPTY);
                }
                continue;
            }

            AuctionStack auctionStack = _itemsListed.get(i);
            ItemStack stack = auctionStack.stack.copy();

            NBTTagCompound tag = stack.getTagCompound();
            if (tag == null)
            {
                tag = new NBTTagCompound();
            }

            NBTTagCompound display = tag.getCompoundTag("display");
            NBTTagList lore = display.getTagList("Lore", 8);

            String prefix = ChatOutputHandler.COLOR_FORMAT_CHARACTER + "6";
            lore.appendTag(new NBTTagString(prefix + "Click to " + (args.senderPlayer.getUniqueID().equals(auctionStack.sellerId) ? "remove" : "buy")));
            lore.appendTag(new NBTTagString(prefix + "Price: " + APIRegistry.economy.toString(auctionStack.price)));
            lore.appendTag(new NBTTagString(prefix + "Seller: " + auctionStack.sellerName));
            display.setTag("Lore", lore);
            tag.setTag("display", display);
            stack.setTagCompound(tag);

            source.setInventorySlotContents(i - offset, stack);
        }
        source.markDirty();
    }

    public void initItems()
    {
        _itemsListed.clear();
        _itemsListed.addAll(ModulePlayerMarket.instance().data.itemsListed);
        initItems(getLowerChestInventory(), multiPage ? currentPage * 45 : 0, multiPage ? 45 : 54, _itemsListed, args);
    }

    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player)
    {
        LoggingHandler.felog.debug("SlotId: {}, dragType: {}, clickType: {}, player: {}", slotId, dragType, clickTypeIn, player);

        if (clickTypeIn == ClickType.PICKUP &&
                !(new Exception()).getStackTrace()[1].getClassName().equals("invtweaks.network.packets.ITPacketClick"))
        {
            slotPickup(slotId, dragType, clickTypeIn, player);
        }

        if (clickTypeIn.equals(ClickType.QUICK_MOVE))
        {
            return inventorySlots.get(slotId).getStack();
        }
        else
        {
            return ItemStack.EMPTY;
        }
    }

    public synchronized void slotPickup(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player)
    {

        if (inventorySlots.get(slotId).inventory.equals(getLowerChestInventory()))
        {
            if (multiPage && slotId >= 45)
            {
                if (slotId == 48)
                {
                    if (currentPage > 0)
                    {
                        currentPage--;
                    }
                    initItems();
                }
                else if (slotId == 50)
                {
                    if (currentPage < (_itemsListed.size() - 1) / 45)
                    {
                        currentPage++;
                    }
                    initItems();
                }
                return;
            }
            AuctionStack stack = _itemsListed.get(slotId + currentPage * 45);

            //Add Item to inventory here
            if (!ModulePlayerMarket.instance().data.itemsListed.contains(stack))
            {
                args.error("%s already sold!", stack.stack);
                initItems();
                return;
            }

            if (!remove && !args.hasPermission(PERM_CMD_BUY_BASE + "." + getItemPermission(stack.stack)))
            {
                args.error("You don't have permission to buy %s", stack.stack);
                return;
            }

            if (!remove && !stack.sellerId.equals(player.getUniqueID()))
            {
                Wallet purchaseWallet = APIRegistry.economy.getWallet(UserIdent.get(player));
                Wallet sellerWallet = APIRegistry.economy.getWallet(UserIdent.get(stack.sellerId));
                if (!purchaseWallet.covers(stack.price))
                {
                    args.confirm("Not enough %s to buy %s", APIRegistry.economy.currency(2), stack.stack);
                    return;
                }

                purchaseWallet.withdraw(stack.price);
                sellerWallet.add(stack.price);

                args.confirm("%s purchased for %s", stack.stack, APIRegistry.economy.toString(stack.price));
            }
            else
            {
                args.confirm("%s removed from market", stack.stack);
            }
            ModulePlayerMarket.instance().data.itemsListed.remove(stack);
            if (!remove)
            {
                player.inventory.addItemStackToInventory(stack.stack);
            }
            else
            {
                UserIdent removedUser = UserIdent.get(stack.sellerId);
                ICommandSender sender;
                if (removedUser.hasPlayer())
                {
                    sender = removedUser.getPlayer();
                }
                else
                {
                    sender = new MailerSender(APIRegistry.IDENT_SERVER, removedUser);
                }
                ChatOutputHandler.chatConfirmation(sender, Translator.format("Your Item %s was removed by an Admin!", stack.stack));
            }
            initItems();
        }
    }

    @Override public void onContainerClosed(EntityPlayer playerIn)
    {
        super.onContainerClosed(playerIn);
    }

    @Override public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
    {
        return ItemStack.EMPTY;
    }

    @Override public boolean canMergeSlot(ItemStack p_canMergeSlot_1_, Slot p_canMergeSlot_2_)
    {
        return false;
    }

    @Override public boolean canDragIntoSlot(Slot p_canDragIntoSlot_1_)
    {
        return false;
    }
}
