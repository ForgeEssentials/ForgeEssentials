package com.forgeessentials.economy.commands;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.economy.Wallet;
import com.forgeessentials.core.commands.ParserCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.economy.ModuleEconomy;
import com.forgeessentials.util.CommandParserArgs;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.questioner.Questioner;
import com.forgeessentials.util.questioner.QuestionerCallback;
import com.forgeessentials.util.questioner.QuestionerStillActiveException;

public class CommandTrade extends ParserCommandBase
{

    @Override
    public String getCommandName()
    {
        return "trade";
    }

    @Override
    public String getPermissionNode()
    {
        return ModuleEconomy.PERM_COMMAND + ".trade";
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {
        return RegisteredPermValue.TRUE;
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_)
    {
        return "/trade <player> <price>: Trade item in hand for money";
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public void parse(final CommandParserArgs arguments)
    {
        if (arguments.isEmpty())
        {
            arguments.confirm(Translator.translate("/trade <player> [price-per-item]:"));
            arguments.confirm(Translator.translate("  Trade the item you are holding in your hand"));
            return;
        }

        final UserIdent buyer = arguments.parsePlayer(true);
        if (!buyer.hasPlayer())
            throw new TranslatedCommandException("Player %s is not online", buyer.getUsernameOrUUID());

        // if (arguments.isEmpty())
        // throw new TranslatedCommandException("Missing argument");
        // final int amount = arguments.parseInt();

        final ItemStack itemStack = arguments.senderPlayer.getCurrentEquippedItem();
        if (itemStack == null)
            throw new TranslatedCommandException("You need to hold an item first!");

        // if (arguments.isEmpty())
        // throw new TranslatedCommandException("Missing argument");
        final long price;
        if (arguments.isEmpty())
        {
            Long p = ModuleEconomy.getItemPrice(itemStack, arguments.ident);
            if (p == null)
                throw new TranslatedCommandException("No default price available. Please specify price.");
            price = p;
        }
        else
            price = arguments.parseLong();

        final Wallet sellerWallet = APIRegistry.economy.getWallet(arguments.ident);
        final Wallet buyerWallet = APIRegistry.economy.getWallet(buyer);

        if (!buyerWallet.covers(price * itemStack.stackSize))
            throw new TranslatedCommandException("%s can't affort that!", buyer.getUsernameOrUUID());

        QuestionerCallback sellerHandler = new QuestionerCallback() {
            @Override
            public void respond(Boolean response)
            {
                if (response == null)
                {
                    arguments.error("Trade request timed out");
                    return;
                }
                else if (response == false)
                {
                    arguments.error("Canceled");
                    return;
                }
                QuestionerCallback buyerHandler = new QuestionerCallback() {
                    @Override
                    public void respond(Boolean response)
                    {
                        if (response == null)
                        {
                            arguments.error("Trade request timed out");
                            return;
                        }
                        else if (response == false)
                        {
                            OutputHandler.chatError(buyer.getPlayerMP(), Translator.translate("Trade declined"));
                            arguments.error(Translator.format("Player %s declined the trade", buyer.getUsernameOrUUID()));
                            return;
                        }

                        ItemStack currentItemStack = arguments.senderPlayer.getCurrentEquippedItem();
                        if (!ItemStack.areItemStacksEqual(currentItemStack, itemStack) || !ItemStack.areItemStackTagsEqual(currentItemStack, itemStack))
                        {
                            OutputHandler.chatError(buyer.getPlayerMP(), Translator.translate("Error in transaction"));
                            arguments.error(Translator.translate("You need to keep the item equipped until trade is finished!"));
                            return;
                        }

                        if (!buyerWallet.withdraw(price * itemStack.stackSize))
                        {
                            OutputHandler.chatError(buyer.getPlayerMP(), Translator.translate("You can't afford that"));
                            return;
                        }
                        sellerWallet.add(price * itemStack.stackSize);

                        InventoryPlayer inventory = arguments.senderPlayer.inventory;
                        inventory.mainInventory[inventory.currentItem] = null;
                        FunctionHelper.givePlayerItem(buyer.getPlayerMP(), currentItemStack);
                    }
                };
                try
                {
                    String message;
                    if (itemStack.stackSize == 1)
                        message = Translator.format("Buy one %s for %s from %s?", itemStack.getDisplayName(), APIRegistry.economy.toString(price),
                                arguments.sender.getCommandSenderName());
                    else
                        message = Translator.format("Buy %d x %s each for %s (total: %s) from %s?", itemStack.stackSize, itemStack.getDisplayName(),
                                APIRegistry.economy.toString(price), APIRegistry.economy.toString(price * itemStack.stackSize),
                                arguments.sender.getCommandSenderName());
                    Questioner.add(buyer.getPlayerMP(), message, buyerHandler, 60);
                    arguments.confirm(Translator.format("Waiting on %s...", buyer.getUsernameOrUUID()));
                }
                catch (QuestionerStillActiveException e)
                {
                    throw new QuestionerStillActiveException.CommandException();
                }
            }
        };
        try
        {
            String message;
            if (itemStack.stackSize == 1)
                message = Translator.format("Sell one %s for %s to %s?", itemStack.getDisplayName(), APIRegistry.economy.toString(price),
                        buyer.getUsernameOrUUID());
            else
                message = Translator.format("Sell %d x %s each for %s (total: %s) to %s?", itemStack.stackSize, itemStack.getDisplayName(),
                        APIRegistry.economy.toString(price), APIRegistry.economy.toString(price * itemStack.stackSize), buyer.getUsernameOrUUID());
            Questioner.add(arguments.sender, message, sellerHandler, 20);
        }
        catch (QuestionerStillActiveException e)
        {
            throw new QuestionerStillActiveException.CommandException();
        }
    }

}
