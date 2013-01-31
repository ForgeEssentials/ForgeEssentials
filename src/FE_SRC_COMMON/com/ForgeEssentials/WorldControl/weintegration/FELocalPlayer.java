package com.ForgeEssentials.WorldControl.weintegration;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;

import com.sk89q.worldedit.LocalPlayer;
import com.sk89q.worldedit.LocalWorld;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldVector;
import com.sk89q.worldedit.bags.BlockBag;

public class FELocalPlayer extends LocalPlayer {
	private final EntityPlayer player;
	private final LocalWorld world;
	private final com.ForgeEssentials.WorldControl.weintegration.FEBlockBag bag;

	public FELocalPlayer(EntityPlayer player) {
		super(new com.ForgeEssentials.WorldControl.weintegration.FEServerInterface());

		this.player = player;
		this.world = WEIntegration.getWorld(player.worldObj);
		this.bag = new com.ForgeEssentials.WorldControl.weintegration.FEBlockBag(player);
	}

	@Override
	public String[] getGroups() {
		return new String[0];
	}

	@Override
	public BlockBag getInventoryBlockBag() {
		return bag;
	}

	@Override
	public int getItemInHand() {
		return player.inventory.mainInventory[player.inventory.currentItem] == null ? 0 : player.inventory.mainInventory[player.inventory.currentItem].itemID;
	}

	@Override
	public String getName() {
		return player.username;
	}

	@Override
	public double getPitch() {
		return player.rotationPitch;
	}

	@Override
	public WorldVector getPosition() {
		return new WorldVector(world, player.posX, player.posY, player.posZ);
	}

	@Override
	public LocalWorld getWorld() {
		return world;
	}

	@Override
	public double getYaw() {
		return player.rotationYaw;
	}

	@Override
	public void giveItem(int arg0, int arg1) {
		player.inventory.addItemStackToInventory(new ItemStack(arg0, arg1, 0));
	}

	// Inject perms system here?
	@Override
	public boolean hasPermission(String arg0) {
		return true; // this is temporary
	}

	@Override
	public void print(String arg0) {
		for (String part : arg0.split("\n")) {
            player.sendChatToPlayer("\u00a7d"+part);
        }
	}

	@Override
	public void printDebug(String arg0) {
		for (String part : arg0.split("\n")) {
            player.sendChatToPlayer("\u00a77"+part);
        }
	}

	@Override
	public void printError(String arg0) {
		for (String part : arg0.split("\n")) {
            player.sendChatToPlayer("\u00a7c"+part);
        }
	}

	@Override
	public void printRaw(String arg0) {
		for (String part : arg0.split("\n")) {
            player.sendChatToPlayer(part);
        }
	}

	@Override
	public void setPosition(Vector arg0, float arg1, float arg2) {
		player.setPositionAndRotation(arg0.getX(), arg0.getY(), arg0.getZ(), arg1, arg2);
	}

	@Override
	public boolean isHoldingPickAxe() {
		return player.inventory.mainInventory[player.inventory.currentItem] != null && player.inventory.mainInventory[player.inventory.currentItem].getItem() instanceof ItemPickaxe;
	}
}