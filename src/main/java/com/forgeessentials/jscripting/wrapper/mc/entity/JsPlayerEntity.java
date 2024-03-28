package com.forgeessentials.jscripting.wrapper.mc.entity;

import java.util.Objects;

import com.forgeessentials.commands.item.CommandVirtualchest;
import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.jscripting.fewrapper.fe.JsPoint;
import com.forgeessentials.jscripting.fewrapper.fe.JsWorldPoint;
import com.forgeessentials.jscripting.wrapper.mc.JsCommandSource;
import com.forgeessentials.jscripting.wrapper.mc.item.JsInventory;
import com.forgeessentials.jscripting.wrapper.mc.item.JsPlayerInventory;
import com.forgeessentials.jscripting.wrapper.mc.item.JsItemStack;
import com.forgeessentials.jscripting.wrapper.mc.world.JsBlock;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.GameType;

public class JsPlayerEntity extends JsLivingEntityBase<Player>
{
    protected JsPlayerInventory<?> inventory;

    private JsCommandSource commandSender;

    /**
     * @tsd.ignore
     */
    public static JsPlayerEntity get(Player player)
    {
        return player == null ? null : new JsPlayerEntity(player);
    }

    /**
     * @tsd.ignore
     */
    private JsPlayerEntity(Player that)
    {
        super(that);
    }

    /**
     * @tsd.ignore
     */
    public JsPlayerEntity(Player that, JsCommandSource commandSender)
    {
        super(that);
        this.commandSender = commandSender;
    }

    public void setPosition(double x, double y, double z)
    {
        that.setPos(x, x, z);
        ((ServerPlayer) that).connection.teleport(x, y, z, that.getYRot(), that.getXRot());
    }

    public void setPosition(double x, double y, double z, float yaw, float pitch)
    {
        that.setPos(x, x, z);
        ((ServerPlayer) that).connection.teleport(x, y, z, yaw, pitch);
    }

    public JsCommandSource asCommandSender()
    {
        if (commandSender != null || !(that instanceof Player))
            return commandSender;
        return commandSender = new JsCommandSource(that.createCommandSourceStack(), this);
    }

    public JsPlayerInventory<?> getInventory()
    {
        if (inventory == null)
            inventory = JsPlayerInventory.get(that.getInventory());
        return inventory;
    }

    public JsPoint<?> getBedLocation(String dimension)
    {
        BlockPos coord = ((ServerPlayer) that).getRespawnPosition();
        if (((ServerPlayer) that).getRespawnDimension().location().toString().equals(dimension))
        {
            return coord != null ? new JsWorldPoint<>(new WorldPoint(dimension, coord)) : null;
        }
        return null;
    }

    public GameType getGameType()
    {
        if (that instanceof ServerPlayer)
        {
            return ((ServerPlayer) that).gameMode.getGameModeForPlayer();
        }
        return GameType.DEFAULT_MODE;
    }

    /**
     * Sets the player's game mode and sends it to them.
     */
    public void setGameType(GameType gameType)
    {
        //FIXME
        //that.setGameMode(gameType);
    }

    // ----- CHECKED UNTIL HERE -----

    /**
     * Whether the player is currently using an item (by holding down use button)
     */
    public boolean isUsingItem()
    {
        return that.isUsingItem();
    }

    /**
     * Whether the player is currently using an item to block attacks
     */
    public boolean isBlocking()
    {
        return that.isBlocking() && !that.getMainHandItem().isEmpty();
    }

    public int getScore()
    {
        return that.getScore();
    }

    /**
     * Set player's score
     */
    public void setScore(int score)
    {
        that.setScore(score);
    }

    /**
     * Add to player's score
     */
    public void addScore(int score)
    {
        that.increaseScore(score);
    }

    /**
     * Returns how strong the player is against the specified block at this moment
     */
    public float getBreakSpeed(JsBlock block, boolean cannotHarvestBlock, int meta, int x, int y, int z)
    {
        block.getThat();
        return that.getDigSpeed(Block.stateById(meta), new BlockPos(x, y, z));
    }

    /**
     * Checks if the player has the ability to harvest a block (checks current inventory item for a tool if necessary)
     */
    public boolean canHarvestBlock(JsBlock block)
    {
        return that.hasCorrectToolForDrops(block.getThat().defaultBlockState());
    }

    public float getEyeHeight()
    {
        return that.getEyeHeight();
    }

    public boolean canAttackPlayer(JsPlayerEntity player)
    {
        return that.canAttack(player.getThat());
    }

    /**
     * Returns the current armor value as determined by a call to InventoryPlayer.getTotalArmorValue
     */
    public int getTotalArmorValue()
    {
        return that.getArmorValue();
    }

    /**
     * When searching for vulnerable players, if a player is invisible, the return value of this is the chance of seeing them anyway.
     */
    // public float getArmorVisibility()
    // {
    // return that.getArmorVisibility();
    // } //NOT A THING ANYMORE

    public boolean interactWith(JsEntity<?> entity)
    {
        switch (that.interactOn(entity.getThat(), InteractionHand.MAIN_HAND))
        {
        case SUCCESS:
        case PASS:
            return true;
        case FAIL:
            return false;
        case CONSUME:
        default:
            break;
        }

        return false;
    }

    /**
     * Returns the currently being used item by the player.
     */
    public JsItemStack getCurrentEquippedItem()
    {
        return JsItemStack.get(that.getUseItem());
    }

    /**
     * Destroys the currently equipped item from the player's inventory.
     */
    public void destroyCurrentEquippedItem()
    {
        that.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
    }

    /**
     * Attacks for the player the targeted entity with the currently equipped item.<br>
     * The equipped item has hitEntity called on it.
     */
    public void attackTargetEntityWithCurrentItem(JsEntity<?> targetEntity)
    {
        that.attack(targetEntity.getThat());
    }

    /**
     * Returns whether player is sleeping or not
     */
    public boolean isPlayerSleeping()
    {
        return that.isSleeping();
    }

    /**
     * Returns whether or not the player is asleep and the screen has fully faded.
     */
    public boolean isPlayerFullyAsleep()
    {
        return that.isSleepingLongEnough();
    }

    public JsItemStack getCurrentArmor(int slot)
    {
        EquipmentSlot eeslot = EquipmentSlot.MAINHAND;
        switch (slot)
        {
        case 0:
            eeslot = EquipmentSlot.FEET;
            break;
        case 1:
            eeslot = EquipmentSlot.LEGS;
            break;
        case 2:
            eeslot = EquipmentSlot.CHEST;
            break;
        case 3:
            eeslot = EquipmentSlot.HEAD;
            break;

        }
        return JsItemStack.get(that.getItemBySlot(eeslot));
    }

    /**
     * Add experience points to player.
     */
    public void addExperience(int exp)
    {
        that.giveExperiencePoints(exp);
    }

    /**
     * Add experience levels to this player.
     */
    public void addExperienceLevel(int levels)
    {
        that.giveExperienceLevels(levels);
    }

    /**
     * increases exhaustion level by supplied amount
     */
    public void addExhaustion(float exhaustion)
    {
        that.causeFoodExhaustion(exhaustion);
    }

    /**
     * Get the player's food level.
     */
    public int getFoodLevel()
    {
        return that.getFoodData().getFoodLevel();
    }

    /**
     * Get the player's food saturation level.
     */
    public float getSaturationLevel()
    {
        return that.getFoodData().getSaturationLevel();
    }

    public void addFoodStats(int foodLevel, float foodSaturationModifier)
    {
        that.getFoodData().eat(foodLevel, foodSaturationModifier);
    }

    /**
     * If foodLevel is not max.
     */
    public boolean needFood()
    {
        return that.getFoodData().needsFood();
    }

    public boolean canEat(boolean canEatWithoutHunger)
    {
        return that.canEat(canEatWithoutHunger);
    }

    /**
     * Returns the InventoryEnderChest of this player.
     */
    public JsInventory<?> getInventoryEnderChest()
    {
        return JsInventory.get(that.getEnderChestInventory());
    }

    public void displayGUIChest(JsInventory<Container> inventory)
    {
        that.openMenu(new SimpleMenuProvider(
                (syncId, inv, player) -> new ChestMenu(
                        CommandVirtualchest.chestTypes.get(CommandVirtualchest.rowCount - 1), syncId, inv,
                        Objects.requireNonNull(inventory.getThat()), CommandVirtualchest.rowCount),
                new TextComponent(CommandVirtualchest.name)));
    }

    public void closeScreen()
    {
        that.closeContainer();
    }
}
