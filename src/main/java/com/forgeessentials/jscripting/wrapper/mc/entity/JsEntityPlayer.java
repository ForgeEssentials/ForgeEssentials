package com.forgeessentials.jscripting.wrapper.mc.entity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameType;

import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.jscripting.fewrapper.fe.JsPoint;
import com.forgeessentials.jscripting.fewrapper.fe.JsWorldPoint;
import com.forgeessentials.jscripting.wrapper.mc.JsICommandSender;
import com.forgeessentials.jscripting.wrapper.mc.item.JsInventory;
import com.forgeessentials.jscripting.wrapper.mc.item.JsInventoryPlayer;
import com.forgeessentials.jscripting.wrapper.mc.item.JsItemStack;
import com.forgeessentials.jscripting.wrapper.mc.world.JsBlock;

public class JsEntityPlayer extends JsEntityLivingBase<EntityPlayer>
{
    protected JsInventoryPlayer<?> inventory;

    private JsICommandSender commandSender;

    /**
     * @tsd.ignore
     */
    public static JsEntityPlayer get(EntityPlayer player)
    {
        return player == null ? null : new JsEntityPlayer(player);
    }

    /**
     * @tsd.ignore
     */
    private JsEntityPlayer(EntityPlayer that)
    {
        super(that);
    }

    /**
     * @tsd.ignore
     */
    public JsEntityPlayer(EntityPlayer that, JsICommandSender commandSender)
    {
        super(that);
        this.commandSender = commandSender;
    }

    public void setPosition(double x, double y, double z)
    {
        that.posX = x;
        that.posY = y;
        that.posZ = z;
        ((EntityPlayerMP) that).connection.setPlayerLocation(x, y, z, that.cameraYaw, that.cameraPitch);
    }

    public void setPosition(double x, double y, double z, float yaw, float pitch)
    {
        that.posX = x;
        that.posY = y;
        that.posZ = z;
        ((EntityPlayerMP) that).connection.setPlayerLocation(x, y, z, yaw, pitch);
    }

    public JsICommandSender asCommandSender()
    {
        if (commandSender != null || !(that instanceof EntityPlayer))
            return commandSender;
        return commandSender = new JsICommandSender(that, this);
    }

    public JsInventoryPlayer<?> getInventory()
    {
        if (inventory == null)
            inventory = JsInventoryPlayer.get(that.inventory);
        return inventory;
    }

    public JsPoint<?> getBedLocation(int dimension)
    {
        BlockPos coord = EntityPlayer.getBedSpawnLocation(that.world, that.getBedLocation(dimension), false);
        return coord != null ? new JsWorldPoint<>(new WorldPoint(dimension, coord)) : null;
    }

    public GameType getGameType()
    {
        if (that instanceof EntityPlayerMP)
        {
            return ((EntityPlayerMP) that).interactionManager.getGameType();
        }
        return GameType.NOT_SET;
    }

    /**
     * Sets the player's game mode and sends it to them.
     */
    public void setGameType(GameType gameType)
    {
        that.setGameType(gameType);
    }

    // ----- CHECKED UNTIL HERE -----

    /**
     * Whether the player is currently using an item (by holding down use button)
     */
    public boolean isUsingItem()
    {
        return that.isHandActive();
    }

    /**
     * Whether the player is currently using an item to block attacks
     */
    public boolean isBlocking()
    {
        return that.isActiveItemStackBlocking();
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
        that.addScore(score);
    }

    /**
     * Returns how strong the player is against the specified block at this moment
     */
    public float getBreakSpeed(JsBlock block, boolean cannotHarvestBlock, int meta, int x, int y, int z)
    {
        return that.getDigSpeed(block.getThat().getStateFromMeta(meta), new BlockPos(x, y, z));
    }

    /**
     * Checks if the player has the ability to harvest a block (checks current inventory item for a tool if necessary)
     */
    public boolean canHarvestBlock(JsBlock block)
    {
        return that.canHarvestBlock(block.getThat().getDefaultState());
    }

    public float getEyeHeight()
    {
        return that.getEyeHeight();
    }

    public boolean canAttackPlayer(JsEntityPlayer player)
    {
        return that.canAttackPlayer(player.getThat());
    }

    /**
     * Returns the current armor value as determined by a call to InventoryPlayer.getTotalArmorValue
     */
    public int getTotalArmorValue()
    {
        return that.getTotalArmorValue();
    }

    /**
     * When searching for vulnerable players, if a player is invisible, the return value of this is the chance of seeing
     * them anyway.
     */
    public float getArmorVisibility()
    {
        return that.getArmorVisibility();
    }

    public boolean interactWith(JsEntity<?> entity)
    {
        switch(that.interactOn(entity.getThat(), EnumHand.MAIN_HAND)){
        case SUCCESS:
        case PASS:
            return true;
        case FAIL:
            return false;
        }

        return false;
    }

    /**
     * Returns the currently being used item by the player.
     */
    public JsItemStack getCurrentEquippedItem()
    {
        return JsItemStack.get(that.getActiveItemStack());
    }

    /**
     * Destroys the currently equipped item from the player's inventory.
     */
    public void destroyCurrentEquippedItem()
    {
        that.resetActiveHand();
    }

    /**
     * Attacks for the player the targeted entity with the currently equipped item.<br>
     * The equipped item has hitEntity called on it.
     */
    public void attackTargetEntityWithCurrentItem(JsEntity<?> targetEntity)
    {
        that.attackTargetEntityWithCurrentItem(targetEntity.getThat());
    }

    /**
     * Returns whether player is sleeping or not
     */
    public boolean isPlayerSleeping()
    {
        return that.isPlayerSleeping();
    }

    /**
     * Returns whether or not the player is asleep and the screen has fully faded.
     */
    public boolean isPlayerFullyAsleep()
    {
        return that.isPlayerFullyAsleep();
    }

    public JsItemStack getCurrentArmor(int slot)
    {
        EntityEquipmentSlot eeslot = EntityEquipmentSlot.MAINHAND;
        switch (slot){
        case 0:
            eeslot = EntityEquipmentSlot.FEET;
            break;
        case 1:
            eeslot = EntityEquipmentSlot.LEGS;
            break;
        case 2:
            eeslot = EntityEquipmentSlot.CHEST;
            break;
        case 3:
            eeslot = EntityEquipmentSlot.HEAD;
            break;

        }
        return JsItemStack.get(that.getItemStackFromSlot(eeslot));
    }

    /**
     * Add experience points to player.
     */
    public void addExperience(int exp)
    {
        that.addExperience(exp);
    }

    /**
     * Add experience levels to this player.
     */
    public void addExperienceLevel(int levels)
    {
        that.addExperienceLevel(levels);
    }

    /**
     * increases exhaustion level by supplied amount
     */
    public void addExhaustion(float exhaustion)
    {
        that.addExhaustion(exhaustion);
    }

    /**
     * Get the player's food level.
     */
    public int getFoodLevel()
    {
        return that.getFoodStats().getFoodLevel();
    }

    /**
     * Get the player's food saturation level.
     */
    public float getSaturationLevel()
    {
        return that.getFoodStats().getSaturationLevel();
    }

    public void addFoodStats(int foodLevel, float foodSaturationModifier)
    {
        that.getFoodStats().addStats(foodLevel, foodSaturationModifier);
    }

    /**
     * If foodLevel is not max.
     */
    public boolean needFood()
    {
        return that.getFoodStats().needFood();
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
        return JsInventory.get(that.getInventoryEnderChest());
    }
}
