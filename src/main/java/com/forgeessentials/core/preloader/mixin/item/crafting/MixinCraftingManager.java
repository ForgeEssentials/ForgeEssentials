package com.forgeessentials.core.preloader.mixin.item.crafting;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import com.forgeessentials.protection.ModuleProtection;

@Mixin(CraftingManager.class)
public abstract class MixinCraftingManager
{

    @Shadow
    private List<IRecipe> recipes;

    @Overwrite
    public ItemStack findMatchingRecipe(InventoryCrafting inventory, World world)
    {
        EntityPlayer player = ModuleProtection.getCraftingPlayer(inventory);
        for (IRecipe recipe : recipes)
        {
            if (recipe.matches(inventory, world))
            {
                ItemStack result = recipe.getCraftingResult(inventory);
                if (ModuleProtection.canCraft(player, result))
                    return result;
            }
        }
        return null;
    }

}
