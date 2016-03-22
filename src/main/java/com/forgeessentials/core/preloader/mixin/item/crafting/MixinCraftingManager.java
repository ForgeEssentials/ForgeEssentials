package com.forgeessentials.core.preloader.mixin.item.crafting;

import com.forgeessentials.protection.ModuleProtection;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(CraftingManager.class)
public abstract class MixinCraftingManager
{

    @Shadow
    private List<IRecipe> recipes;

    /**
     * Try to find a crafting result that the player is able to craft.
     *
     * @param inventory the crafting inventory
     * @param world the world
     * @param cir the callback info
     */
    @Inject(
        method = "findMatchingRecipe",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/List;size()I",
            shift = At.Shift.BEFORE
        ),
        cancellable = true
    )
    private void findCraftingResultPermissible(InventoryCrafting inventory, World world, CallbackInfoReturnable<ItemStack> cir)
    {
        EntityPlayer player = ModuleProtection.getCraftingPlayer(inventory);
        for (IRecipe recipe : this.recipes)
        {
            if (recipe.matches(inventory, world))
            {
                ItemStack result = recipe.getCraftingResult(inventory);
                if (ModuleProtection.canCraft(player, result))
                {
                    cir.setReturnValue(result);
                    return;
                }
            }
        }

        cir.setReturnValue(null);
    }

}
