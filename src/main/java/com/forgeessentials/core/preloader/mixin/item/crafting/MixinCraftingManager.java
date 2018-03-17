package com.forgeessentials.core.preloader.mixin.item.crafting;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.RegistryNamespaced;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import com.forgeessentials.protection.ModuleProtection;

import javax.annotation.Nullable;

@Mixin(CraftingManager.class)
public abstract class MixinCraftingManager
{
    @Shadow
    public static RegistryNamespaced<ResourceLocation, IRecipe> REGISTRY;

    /**
     * Try to find a crafting result that the player is able to craft.
     *
     * @param craftMatrix the crafting inventory
     * @param worldIn the world
     */
    @Overwrite
    public static ItemStack findMatchingResult(InventoryCrafting craftMatrix, World worldIn)
    {
        EntityPlayer player = ModuleProtection.getCraftingPlayer(craftMatrix);
        for (IRecipe irecipe : REGISTRY)
        {
            if (irecipe.matches(craftMatrix, worldIn))
            {
                ItemStack result = irecipe.getCraftingResult(craftMatrix);
                if (ModuleProtection.canCraft(player, result))
                {
                    return irecipe.getCraftingResult(craftMatrix);
                }
            }
        }

        return ItemStack.EMPTY;
    }

    @Overwrite
    @Nullable
    public static IRecipe findMatchingRecipe(InventoryCrafting craftMatrix, World worldIn)
    {
        EntityPlayer player = ModuleProtection.getCraftingPlayer(craftMatrix);
        for (IRecipe irecipe : REGISTRY)
        {
            if (irecipe.matches(craftMatrix, worldIn))
            {
                ItemStack result = irecipe.getCraftingResult(craftMatrix);
                if (ModuleProtection.canCraft(player, result))
                {
                    return irecipe;
                }
            }
        }

        return null;
    }

}
