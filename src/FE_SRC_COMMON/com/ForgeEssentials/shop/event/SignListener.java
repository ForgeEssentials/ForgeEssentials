package com.ForgeEssentials.shop;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntitySign;

import net.minecraft.world.WorldServer;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;

public class SignListener {

	public TileEntitySign sign;
	public Block block;
	public WorldServer world;

	@ForgeSubscribe
	public void onBlockRightClick(PlayerInteractEvent event) {
		String[] strings = new String[4];
		String Amount = null;
		String Cost = null;
		String ID = null;
		int Amount1 = 0;
		int ID1 = 0;
		if (event.action == Action.RIGHT_CLICK_BLOCK) {
			int blockID = event.entityPlayer.worldObj.getBlockId(event.x, event.y, event.z);

			if (blockID == Block.signPost.blockID || blockID == Block.signWall.blockID) {
				sign = (TileEntitySign) event.entityPlayer.worldObj.getBlockTileEntity(event.x, event.y, event.z);
				if (sign == null) {
					System.out.println("Problem!");
					return;
				}

				System.out.println("Found Sign!");

				if (sign.signText[0] != null && sign.signText[0].equals("[Buy]")) {

					System.out.println("asd");

					if (sign.signText[1] != null) {
						Amount = sign.signText[1];

						if (!Amount.equalsIgnoreCase("")) {
							Amount1 = Integer.parseInt(Amount);
						} else {
							sign.signText[0] = "\u00A74[Buy]";

						}
					}

					if (sign.signText[2] != null) {
						ID = sign.signText[2];
						if (!ID.equalsIgnoreCase("")) {
							ID1 = Integer.parseInt(ID);
						} else {
							sign.signText[0] = "\u00A74[Buy]";
						}

					}
					if (sign.signText[3] != null) {
						Cost = sign.signText[3];

						if (!Cost.equalsIgnoreCase("")) {
							Cost = Cost.substring(1);
							int Cost1 = Integer.parseInt(Cost);
							// Wallet.removeFromWallet(Cost1, player);
							event.entityPlayer.dropPlayerItem(new ItemStack(ID1, Amount1, 0));

						} else {
							event.entityPlayer.dropPlayerItem(new ItemStack(ID1, Amount1, 0));
						}

					}
				}
			}

		}

	}
}
