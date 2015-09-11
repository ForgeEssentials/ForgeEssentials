package com.forgeessentials.core.preloader.mixin.item.crafting;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.permission.PermissionManager;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.relauncher.ReflectionHelper;

@Mixin(CraftingManager.class)
public abstract class MixinCraftingManager
{

    private static final String PERMISSION_BASE = "craft.";

    @Shadow
    private List<IRecipe> recipes;

    @Overwrite
    public ItemStack findMatchingRecipe(InventoryCrafting inventory, World world)
    {
        int itemCount = 0;
        ItemStack stack1 = null;
        ItemStack stack2 = null;
        int j;

        for (j = 0; j < inventory.getSizeInventory(); ++j)
        {
            ItemStack stack = inventory.getStackInSlot(j);
            if (stack != null)
            {
                if (itemCount == 0)
                {
                    stack1 = stack;
                }
                if (itemCount == 1)
                {
                    stack2 = stack;
                }
                ++itemCount;
            }
        }

        if (itemCount == 2 && stack1.getItem() == stack2.getItem() && stack1.stackSize == 1 && stack2.stackSize == 1 && stack1.getItem().isRepairable())
        {
            Item item = stack1.getItem();
            int dmg1 = item.getMaxDamage() - stack1.getItemDamageForDisplay();
            int dmg2 = item.getMaxDamage() - stack2.getItemDamageForDisplay();
            int newDmg = dmg1 + dmg2 + item.getMaxDamage() * 5 / 100;
            int newDurability = item.getMaxDamage() - newDmg;
            if (newDurability < 0)
            {
                newDurability = 0;
            }
            return new ItemStack(stack1.getItem(), 1, newDurability);
        }
        else
        {
            EntityPlayer player = getCraftingPlayer(inventory);
            for (j = 0; j < this.recipes.size(); ++j)
            {
                IRecipe irecipe = this.recipes.get(j);
                if (irecipe.matches(inventory, world))
                {
                    ItemStack result = irecipe.getCraftingResult(inventory);
                    String permission = getCraftingPermission(result);
                    if (PermissionManager.checkPermission(player, permission))
                        return result;
                }
            }
            return null;
        }
    }

    private static String getItemId(Item item)
    {
        return GameData.getItemRegistry().getNameForObject(item).replace(':', '.');
    }

    private static String getItemPermission(ItemStack stack, boolean checkMeta)
    {
        int dmg = stack.getItemDamage();
        if (!checkMeta || dmg == 0 || dmg == 32767)
            return getItemId(stack.getItem());
        else
            return getItemId(stack.getItem()) + "." + dmg;
    }

    private static String getCraftingPermission(ItemStack stack)
    {
        return PERMISSION_BASE + getItemPermission(stack, true);
    }

    private static EntityPlayer getCraftingPlayer(InventoryCrafting inventory)
    {
        Container abstractContainer = ReflectionHelper.getPrivateValue(InventoryCrafting.class, inventory, "field_70465_c", "eventHandler");
        if (abstractContainer instanceof ContainerPlayer)
        {
            ContainerPlayer container = (ContainerPlayer) abstractContainer;
            return ReflectionHelper.getPrivateValue(ContainerPlayer.class, container, "field_82862_h", "thePlayer");
        }
        else if (abstractContainer instanceof ContainerWorkbench)
        {
            SlotCrafting slot = (SlotCrafting) abstractContainer.getSlot(0);
            return ReflectionHelper.getPrivateValue(SlotCrafting.class, slot, "field_75238_b", "thePlayer");
        }
        return null;
    }

}
