package com.ForgeEssentials.WorldControl;

import com.ForgeEssentials.WorldControl.FunctionHandler;
import com.ForgeEssentials.WorldControl.WandProxy;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;
import net.minecraft.src.WorldClient;

@SideOnly(value=Side.CLIENT)
public class WandProxyClient extends WandProxy {
	public boolean tryPlaceIntoWorld(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10)
    {
		FunctionHandler.instance.point2X.put(par2EntityPlayer.username, par4);
		FunctionHandler.instance.point2Y.put(par2EntityPlayer.username, par5);
		FunctionHandler.instance.point2Z.put(par2EntityPlayer.username, par6);
		if(par2EntityPlayer.worldObj instanceof WorldClient)par2EntityPlayer.addChatMessage("Pos2 set to: "+FunctionHandler.instance.point2X.get(par2EntityPlayer.username)+", "+FunctionHandler.instance.point2Y.get(par2EntityPlayer.username)+", "+FunctionHandler.instance.point2Z.get(par2EntityPlayer.username));
		return true;
    }
	
	public boolean onBlockStartBreak(ItemStack itemstack, int X, int Y, int Z, EntityPlayer player) 
    {
		FunctionHandler.instance.point1X.put(player.username, X);
		FunctionHandler.instance.point1Y.put(player.username, Y);
		FunctionHandler.instance.point1Z.put(player.username, Z);
		if(player.worldObj instanceof WorldClient)player.addChatMessage("Pos1 set to: "+FunctionHandler.instance.point1X.get(player.username)+", "+FunctionHandler.instance.point1Y.get(player.username)+", "+FunctionHandler.instance.point1Z.get(player.username));
    	return true;
    }
}
