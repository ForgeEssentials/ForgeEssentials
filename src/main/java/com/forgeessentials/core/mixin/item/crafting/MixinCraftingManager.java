package com.forgeessentials.core.mixin.item.crafting;
//TODO finish
//@Mixin(CraftingManager.class)
public abstract class MixinCraftingManager
{
    // /**
    // * Try to find a crafting result that the player is able to craft.
    // *
    // * @param inventory
    // * the crafting inventory
    // * @param world
    // * the world
    // */
    // @SuppressWarnings("unchecked")
    // @Overwrite
    // public static ItemStack findMatchingResult(CraftingInventory inventory, World world)
    // {
    // PlayerEntity player = ModuleProtection.getCraftingPlayer(inventory);
    // Iterator var2 = ForgeRegistries.RECIPE_SERIALIZERS.iterator();
    //
    // IRecipe<?> irecipe;
    // do
    // {
    // if (!var2.hasNext())
    // {
    // return ItemStack.EMPTY;
    // }
    //
    // irecipe = (IRecipe) var2.next();
    // }
    // while (!irecipe.matches(inventory, world));
    //
    // ItemStack result = irecipe.assemble(inventory);
    // if (ModuleProtection.canCraft(player, result))
    // {
    // return result;
    // }
    //
    // return ItemStack.EMPTY;
    // }
}
