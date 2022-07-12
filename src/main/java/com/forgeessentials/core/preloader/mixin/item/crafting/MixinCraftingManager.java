package com.forgeessentials.core.preloader.mixin.item.crafting;

import java.util.Iterator;
import java.util.List;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import com.forgeessentials.protection.ModuleProtection;

@Mixin(CraftingManager.class)
public abstract class MixinCraftingManager
{
    /**
     * Try to find a crafting result that the player is able to craft.
     *
     * @param inventory
     *            the crafting inventory
     * @param world
     *            the world
     */
    @SuppressWarnings("unchecked")
    @Overwrite
    public static ItemStack findMatchingResult(CraftingInventory inventory, World world)
    {
        PlayerEntity player = ModuleProtection.getCraftingPlayer(inventory);
        Iterator var2 = ForgeRegistries.RECIPE_SERIALIZERS.iterator();

        IRecipe irecipe;
        do
        {
            if (!var2.hasNext())
            {
                return ItemStack.EMPTY;
            }

            irecipe = (IRecipe) var2.next();
        }
        while (!irecipe.matches(inventory, world));

        ItemStack result = irecipe.assemble(inventory);
        if (ModuleProtection.canCraft(player, result))
        {
            return result;
        }

        return ItemStack.EMPTY;
    }
}
