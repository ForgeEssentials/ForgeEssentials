package com.forgeessentials.commands.util;

import java.util.ArrayList;
import java.util.List;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.commands.item.CommandKit;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.output.ChatOutputHandler;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class Kit
{

    private static final long MILLISECONDS_PER_YEAR = 365L * 24L * 60L * 60L * 1000L;

    private String name;

    private int cooldown;

    private ItemStack[] items;

    private ItemStack[] armor;

    public Kit(Player player, String name, int cooldown)
    {
        this.cooldown = cooldown;
        this.name = name;

        List<ItemStack> collapsedInventory = new ArrayList<>();
        for (int i = 0; i < player.getInventory().items.size(); i++)
            if (player.getInventory().items.get(i) != ItemStack.EMPTY)
            {
                collapsedInventory.add(player.getInventory().items.get(i).copy());
            }
        items = collapsedInventory.toArray(new ItemStack[collapsedInventory.size()]);

        armor = new ItemStack[player.getInventory().armor.size()];
        for (int i = 0; i < 4; i++)
            if (player.getInventory().armor.get(i) != ItemStack.EMPTY)
                armor[i] = player.getInventory().armor.get(i).copy();
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

    public void giveKit(Player player)
    {
        if (!APIRegistry.perms.checkPermission(player, CommandKit.PERM_BYPASS_COOLDOWN))
        {
            PlayerInfo pi = PlayerInfo.get(player.getGameProfile().getId());
            long timeout = pi.getRemainingTimeout("KIT_" + name);
            if (timeout > 0)
            {
                ChatOutputHandler.chatWarning(player.createCommandSourceStack(),
                        Translator.format("Kit cooldown active, %s to go!",
                                ChatOutputHandler.formatTimeDurationReadable(timeout / 1000L, true)));
                return;
            }
            pi.startTimeout("KIT_" + name, cooldown < 0 ? 10L * MILLISECONDS_PER_YEAR : cooldown * 1000L);
        }

        boolean couldNotGiveItems = false;

        for (ItemStack stack : items)
            couldNotGiveItems |= !player.getInventory().add(stack.copy());

        for (int i = 0; i < 4; i++)
            if (armor[i] != null)
                if (player.getInventory().armor.get(i) == ItemStack.EMPTY)
                {
                    player.getInventory().armor.set(i, armor[i].copy());
                }
                else
                    couldNotGiveItems |= !player.getInventory().add(armor[i].copy());

        if (couldNotGiveItems)
            ChatOutputHandler.chatError(player.createCommandSourceStack(),
                    Translator.translate("Could not give some kit items."));
        ChatOutputHandler.chatConfirmation(player.createCommandSourceStack(), "Kit dropped.");
    }

}
