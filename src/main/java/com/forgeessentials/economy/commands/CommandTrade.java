package com.forgeessentials.economy.commands;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.economy.Wallet;
import com.forgeessentials.core.commands.ParserCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.economy.ModuleEconomy;
import com.forgeessentials.util.CommandParserArgs;
import com.forgeessentials.util.PlayerUtil;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.questioner.Questioner;
import com.forgeessentials.util.questioner.QuestionerCallback;
import com.forgeessentials.util.questioner.QuestionerStillActiveException;

public class CommandTrade extends ParserCommandBase
{

    @Override
    public String getPrimaryAlias()
    {
        return "trade";
    }

    @Override
    public String getPermissionNode()
    {
        return ModuleEconomy.PERM_COMMAND + ".trade";
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.ALL;
    }

    @Override
    public String getUsage(ICommandSender p_71518_1_)
    {
        return "/trade <player> <price>: Trade item in hand for money";
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public void parse(final CommandParserArgs arguments) throws net.minecraft.command.CommandException
    {
        if (arguments.isEmpty())
        {
            arguments.confirm("/trade <player> [price-per-item]:");
            arguments.confirm("  Trade the item you are holding in your hand");
            return;
        }

        final UserIdent buyer = arguments.parsePlayer(true, true);

        // if (arguments.isEmpty())
        // throw new TranslatedCommandException("Missing argument");
        // final int amount = arguments.parseInt();

        final ItemStack itemStack = arguments.senderPlayer.getHeldItemMainhand();
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

        if (!buyerWallet.covers(price * itemStack.getCount()))
            throw new TranslatedCommandException("%s can't affort that!", buyer.getUsernameOrUuid());

        QuestionerCallback sellerHandler = new QuestionerCallback() {
            @Override
            public void respond(Boolean response) throws CommandException
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
                            ChatOutputHandler.chatError(buyer.getPlayerMP(), Translator.translate("Trade declined"));
                            arguments.confirm("Player %s declined the trade", buyer.getUsernameOrUuid());
                            return;
                        }

                        ItemStack currentItemStack = arguments.senderPlayer.getHeldItemMainhand();
                        if (!ItemStack.areItemStacksEqual(currentItemStack, itemStack) || !ItemStack.areItemStackTagsEqual(currentItemStack, itemStack))
                        {
                            ChatOutputHandler.chatError(buyer.getPlayerMP(), Translator.translate("Error in transaction"));
                            arguments.error(Translator.translate("You need to keep the item equipped until trade is finished!"));
                            return;
                        }

                        if (!buyerWallet.withdraw(price * itemStack.getCount()))
                        {
                            ChatOutputHandler.chatError(buyer.getPlayerMP(), Translator.translate("You can't afford that"));
                            return;
                        }
                        sellerWallet.add(price * itemStack.getCount());

                        InventoryPlayer inventory = arguments.senderPlayer.inventory;
                        inventory.mainInventory.set(inventory.currentItem, ItemStack.EMPTY);
                        PlayerUtil.give(buyer.getPlayerMP(), currentItemStack);

                        String priceStr = APIRegistry.economy.toString(price);
                        String totalPriceStr = APIRegistry.economy.toString(price * itemStack.getCount());
                        String buyerMsg = Translator.format("Bought %d x %s from %s for %s each (%s)", //
                                itemStack.getCount(), itemStack.getDisplayName(), //
                                arguments.ident.getUsernameOrUuid(), //
                                priceStr, totalPriceStr);
                        String sellerMsg = Translator.format("Sold %d x %s to %s for %s each (%s)", //
                                itemStack.getCount(), itemStack.getDisplayName(), //
                                buyer.getUsernameOrUuid(), //
                                priceStr, totalPriceStr);
                        arguments.notify(sellerMsg);
                        ChatOutputHandler.chatNotification(buyer.getPlayerMP(), buyerMsg);
                    }
                };
                try
                {
                    String message;
                    if (itemStack.getCount() == 1)
                        message = Translator.format("Buy one %s for %s from %s?", itemStack.getDisplayName(), APIRegistry.economy.toString(price),
                                arguments.sender.getName());
                    else
                        message = Translator.format("Buy %d x %s each for %s (total: %s) from %s?", itemStack.getCount(), itemStack.getDisplayName(),
                                APIRegistry.economy.toString(price), APIRegistry.economy.toString(price * itemStack.getCount()),
                                arguments.sender.getName());
                    Questioner.addChecked(buyer.getPlayerMP(), message, buyerHandler, 60);
                    arguments.confirm(Translator.format("Waiting on %s...", buyer.getUsernameOrUuid()));
                }
                catch (QuestionerStillActiveException.CommandException e)
                {
                    throw new QuestionerStillActiveException.CommandException();
                }
            }
        };
        String message;
        if (itemStack.getCount() == 1)
            message = Translator
                    .format("Sell one %s for %s to %s?", itemStack.getDisplayName(), APIRegistry.economy.toString(price), buyer.getUsernameOrUuid());
        else
            message = Translator.format("Sell %d x %s each for %s (total: %s) to %s?", itemStack.getCount(), itemStack.getDisplayName(),
                    APIRegistry.economy.toString(price), APIRegistry.economy.toString(price * itemStack.getCount()), buyer.getUsernameOrUuid());
        Questioner.addChecked(arguments.sender, message, sellerHandler, 20);
    }
    
}
