package com.forgeessentials.jscripting.wrapper.mc.entity;

import java.util.Objects;

import com.forgeessentials.commands.item.CommandVirtualchest;
import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.jscripting.fewrapper.fe.JsPoint;
import com.forgeessentials.jscripting.fewrapper.fe.JsWorldPoint;
import com.forgeessentials.jscripting.wrapper.mc.JsICommandSender;
import com.forgeessentials.jscripting.wrapper.mc.item.JsInventory;
import com.forgeessentials.jscripting.wrapper.mc.item.JsInventoryPlayer;
import com.forgeessentials.jscripting.wrapper.mc.item.JsItemStack;
import com.forgeessentials.jscripting.wrapper.mc.world.JsBlock;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.GameType;

public class JsEntityPlayer extends JsEntityLivingBase<PlayerEntity> {
	protected JsInventoryPlayer<?> inventory;

	private JsICommandSender commandSender;

	/**
	 * @tsd.ignore
	 */
	public static JsEntityPlayer get(PlayerEntity player) {
		return player == null ? null : new JsEntityPlayer(player);
	}

	/**
	 * @tsd.ignore
	 */
	private JsEntityPlayer(PlayerEntity that) {
		super(that);
	}

	/**
	 * @tsd.ignore
	 */
	public JsEntityPlayer(PlayerEntity that, JsICommandSender commandSender) {
		super(that);
		this.commandSender = commandSender;
	}

	public void setPosition(double x, double y, double z) {
		that.setPos(x, x, z);
		((ServerPlayerEntity) that).connection.teleport(x, y, z, that.yRot, that.xRot);
	}

	public void setPosition(double x, double y, double z, float yaw, float pitch) {
		that.setPos(x, x, z);
		((ServerPlayerEntity) that).connection.teleport(x, y, z, yaw, pitch);
	}

	public JsICommandSender asCommandSender() {
		if (commandSender != null || !(that instanceof PlayerEntity))
			return commandSender;
		return commandSender = new JsICommandSender(that.createCommandSourceStack(), this);
	}

	public JsInventoryPlayer<?> getInventory() {
		if (inventory == null)
			inventory = JsInventoryPlayer.get(that.inventory);
		return inventory;
	}

	public JsPoint<?> getBedLocation(String dimension) {
		BlockPos coord = ((ServerPlayerEntity) that).getRespawnPosition();
		if (((ServerPlayerEntity) that).getRespawnDimension().location().toString().equals(dimension)) {
			return coord != null ? new JsWorldPoint<>(new WorldPoint(dimension, coord)) : null;
		}
		return null;
	}

	public GameType getGameType() {
		if (that instanceof ServerPlayerEntity) {
			return ((ServerPlayerEntity) that).gameMode.getGameModeForPlayer();
		}
		return GameType.NOT_SET;
	}

	/**
	 * Sets the player's game mode and sends it to them.
	 */
	public void setGameType(GameType gameType) {
		that.setGameMode(gameType);
	}

	// ----- CHECKED UNTIL HERE -----

	/**
	 * Whether the player is currently using an item (by holding down use button)
	 */
	public boolean isUsingItem() {
		return that.isUsingItem();
	}

	/**
	 * Whether the player is currently using an item to block attacks
	 */
	public boolean isBlocking() {
		return that.isBlocking() && !that.getMainHandItem().isEmpty();
	}

	public int getScore() {
		return that.getScore();
	}

	/**
	 * Set player's score
	 */
	public void setScore(int score) {
		that.setScore(score);
	}

	/**
	 * Add to player's score
	 */
	public void addScore(int score) {
		that.increaseScore(score);
	}

	/**
	 * Returns how strong the player is against the specified block at this moment
	 */
	public float getBreakSpeed(JsBlock block, boolean cannotHarvestBlock, int meta, int x, int y, int z) {
		block.getThat();
		return that.getDigSpeed(Block.stateById(meta), new BlockPos(x, y, z));
	}

	/**
	 * Checks if the player has the ability to harvest a block (checks current
	 * inventory item for a tool if necessary)
	 */
	public boolean canHarvestBlock(JsBlock block) {
		return that.hasCorrectToolForDrops(block.getThat().defaultBlockState());
	}

	public float getEyeHeight() {
		return that.getEyeHeight();
	}

	public boolean canAttackPlayer(JsEntityPlayer player) {
		return that.canAttack(player.getThat());
	}

	/**
	 * Returns the current armor value as determined by a call to
	 * InventoryPlayer.getTotalArmorValue
	 */
	public int getTotalArmorValue() {
		return that.getArmorValue();
	}

	/**
	 * When searching for vulnerable players, if a player is invisible, the return
	 * value of this is the chance of seeing them anyway.
	 */
	// public float getArmorVisibility()
	// {
	// return that.getArmorVisibility();
	// } //NOT A THING ANYMORE

	public boolean interactWith(JsEntity<?> entity) {
		switch (that.interactOn(entity.getThat(), Hand.MAIN_HAND)) {
		case SUCCESS:
		case PASS:
			return true;
		case FAIL:
			return false;
		case CONSUME:
			break;
		default:
			break;
		}

		return false;
	}

	/**
	 * Returns the currently being used item by the player.
	 */
	public JsItemStack getCurrentEquippedItem() {
		return JsItemStack.get(that.getUseItem());
	}

	/**
	 * Destroys the currently equipped item from the player's inventory.
	 */
	public void destroyCurrentEquippedItem() {
		that.setItemInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
		;
	}

	/**
	 * Attacks for the player the targeted entity with the currently equipped
	 * item.<br>
	 * The equipped item has hitEntity called on it.
	 */
	public void attackTargetEntityWithCurrentItem(JsEntity<?> targetEntity) {
		that.attack(targetEntity.getThat());
	}

	/**
	 * Returns whether player is sleeping or not
	 */
	public boolean isPlayerSleeping() {
		return that.isSleeping();
	}

	/**
	 * Returns whether or not the player is asleep and the screen has fully faded.
	 */
	public boolean isPlayerFullyAsleep() {
		return that.isSleepingLongEnough();
	}

	public JsItemStack getCurrentArmor(int slot) {
		EquipmentSlotType eeslot = EquipmentSlotType.MAINHAND;
		switch (slot) {
		case 0:
			eeslot = EquipmentSlotType.FEET;
			break;
		case 1:
			eeslot = EquipmentSlotType.LEGS;
			break;
		case 2:
			eeslot = EquipmentSlotType.CHEST;
			break;
		case 3:
			eeslot = EquipmentSlotType.HEAD;
			break;

		}
		return JsItemStack.get(that.getItemBySlot(eeslot));
	}

	/**
	 * Add experience points to player.
	 */
	public void addExperience(int exp) {
		that.giveExperiencePoints(exp);
	}

	/**
	 * Add experience levels to this player.
	 */
	public void addExperienceLevel(int levels) {
		that.giveExperienceLevels(levels);
	}

	/**
	 * increases exhaustion level by supplied amount
	 */
	public void addExhaustion(float exhaustion) {
		that.causeFoodExhaustion(exhaustion);
	}

	/**
	 * Get the player's food level.
	 */
	public int getFoodLevel() {
		return that.getFoodData().getFoodLevel();
	}

	/**
	 * Get the player's food saturation level.
	 */
	public float getSaturationLevel() {
		return that.getFoodData().getSaturationLevel();
	}

	public void addFoodStats(int foodLevel, float foodSaturationModifier) {
		that.getFoodData().eat(foodLevel, foodSaturationModifier);
	}

	/**
	 * If foodLevel is not max.
	 */
	public boolean needFood() {
		return that.getFoodData().needsFood();
	}

	public boolean canEat(boolean canEatWithoutHunger) {
		return that.canEat(canEatWithoutHunger);
	}

	/**
	 * Returns the InventoryEnderChest of this player.
	 */
	public JsInventory<?> getInventoryEnderChest() {
		return JsInventory.get(that.getEnderChestInventory());
	}

	public void displayGUIChest(JsInventory<IInventory> inventory) {
		that.openMenu(new SimpleNamedContainerProvider(
				(syncId, inv, player) -> new ChestContainer(
						CommandVirtualchest.chestTypes.get(CommandVirtualchest.rowCount - 1), syncId, inv,
						Objects.requireNonNull(inventory.getThat()), CommandVirtualchest.rowCount),
				new StringTextComponent(CommandVirtualchest.name)));
	}

	public void closeScreen() {
		that.closeContainer();
	}
}
