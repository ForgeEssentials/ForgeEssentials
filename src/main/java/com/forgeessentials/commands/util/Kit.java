package com.forgeessentials.commands.util;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.server.permission.PermissionAPI;

import com.forgeessentials.commands.item.CommandKit;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.output.ChatOutputHandler;

public class Kit
{

    private static final long MILLISECONDS_PER_YEAR = 365L * 24L * 60L * 60L * 1000L;

    private String name;

    private int cooldown;

    private ItemStack[] items;

    private ItemStack[] armor;

    public Kit(EntityPlayer player, String name, int cooldown)
    {
        this.cooldown = cooldown;
        this.name = name;

        List<ItemStack> collapsedInventory = new ArrayList<ItemStack>();
        for (int i = 0; i < player.inventory.mainInventory.size(); i++)
            if (player.inventory.mainInventory.get(i) != ItemStack.EMPTY)
            {
                collapsedInventory.add(player.inventory.mainInventory.get(i).copy());
            }
        items = collapsedInventory.toArray(new ItemStack[collapsedInventory.size()]);

        armor = new ItemStack[player.inventory.armorInventory.size()];
        for (int i = 0; i < 4; i++)
            if (player.inventory.armorInventory.get(i) != ItemStack.EMPTY)
                armor[i] = player.inventory.armorInventory.get(i).copy();
    }

    public String getName()
    {
        return name;
    }

    public int getCooldown()
    {
        return cooldown;
    }

    public ItemStack[] getItems()
    {
        return items;
    }

    public ItemStack[] getArmor()
    {
        return armor;
    }

    public void giveKit(EntityPlayer player)
    {
        if (!PermissionAPI.hasPermission(player, CommandKit.PERM_BYPASS_COOLDOWN))
        {
            PlayerInfo pi = PlayerInfo.get(player.getPersistentID());
            long timeout = pi.getRemainingTimeout("KIT_" + name);
            if (timeout > 0)
            {
                ChatOutputHandler.chatWarning(player,
                        Translator.format("Kit cooldown active, %s to go!", ChatOutputHandler.formatTimeDurationReadable(timeout / 1000L, true)));
                return;
            }
            pi.startTimeout("KIT_" + name, cooldown < 0 ? 10L * MILLISECONDS_PER_YEAR : cooldown * 1000L);
        }

        boolean couldNotGiveItems = false;

        for (ItemStack stack : items)
            couldNotGiveItems |= !player.inventory.addItemStackToInventory(stack.copy());

        for (int i = 0; i < 4; i++)
            if (armor[i] != null)
                if (player.inventory.armorInventory.get(i) == ItemStack.EMPTY)
                {
                    player.inventory.armorInventory.set(i, armor[i].copy());
                }
                else
                    couldNotGiveItems |= !player.inventory.addItemStackToInventory(armor[i].copy());

        if (couldNotGiveItems)
            ChatOutputHandler.chatError(player, Translator.translate("Could not give some kit items."));
        ChatOutputHandler.chatConfirmation(player, "Kit dropped.");
    }

}
