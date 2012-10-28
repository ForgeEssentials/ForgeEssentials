package com.ForgeEssentials.WorldControl;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.asm.SideOnly;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EnumToolMaterial;
import net.minecraft.src.Item;
import net.minecraft.src.ItemAxe;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ModLoader;
import net.minecraft.src.MovingObjectPosition;
import net.minecraft.src.World;

public class ItemWand extends ItemAxe {

	protected ItemWand(int par1) {
		super(par1, EnumToolMaterial.WOOD);
	}
	
	@SidedProxy(clientSide="maximumtechmodding.worldcontrol.WandProxyClient",serverSide="maximumtechmodding.worldcontrol.WandProxy")
	public static WandProxy itemProxy;
	
	@Override
	public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10)
    {
		return itemProxy.tryPlaceIntoWorld(par1ItemStack, par2EntityPlayer, par3World, par4, par5, par6, par7, par8, par9, par10);
    }
	
	/**
     * Called before a block is broken.  Return true to prevent default block harvesting.
     *
     * Note: In SMP, this is called on both client and server sides!
     * 
     * @param itemstack The current ItemStack
     * @param X The X Position
     * @param Y The X Position
     * @param Z The X Position
     * @param player The Player that is wielding the item
     * @return True to prevent harvesting, false to continue as normal
     */
	@Override
    public boolean onBlockStartBreak(ItemStack itemstack, int X, int Y, int Z, EntityPlayer player) 
    {
		return itemProxy.onBlockStartBreak(itemstack, X, Y, Z, player);
    }

}
