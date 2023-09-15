package com.forgeessentials.jscripting.fewrapper.fe;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.script.ScriptException;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.jscripting.ScriptInstance;
import com.forgeessentials.jscripting.command.CommandJScriptCommand;
import com.forgeessentials.jscripting.wrapper.mc.JsICommandSender;
import com.forgeessentials.util.PlayerInfo;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * @tsd.interface FEServer
 */
public class JsFEServer
{

    private class CORoutine
    {
        int tickCount, tickStep, lastCount;
        String method;
        JsICommandSender sender;
        Object extraData;

        CORoutine(int tickCount, int tickStep, String method, JsICommandSender sender, Object extraData)
        {
            this.tickCount = tickCount;
            this.lastCount = tickCount;
            this.tickStep = tickStep;
            this.method = method;
            this.sender = sender;
            this.extraData = extraData;
        }
    }

    HashSet<CORoutine> coRoutineHashSet = new HashSet<>();
    private ScriptInstance script;

    private JsICommandSender server;

    public JsFEServer(ScriptInstance script)
    {
        this.script = script;
    }

    /**
     * Registers a new command in the game. <br>
     * The processCommand and tabComplete handler can be the same, if the processCommand handler properly checks for args.isTabCompletion.
     *
     * @tsd.def registerCommand(options: CommandOptions): void;
     */
    public void registerCommand(Object options) throws ScriptException
    {
        JsCommandOptions opt = script.getProperties(new JsCommandOptions(), options, JsCommandOptions.class);
        script.registerScriptCommand(new CommandJScriptCommand(script, opt));
    }

    /**
     * Returns the total number of unique players that have connected to this server
     */
    public int getUniquePlayerCount()
    {
        return APIRegistry.perms.getServerZone().getKnownPlayers().size();
    }

    /**
     * Returns the list of players who have ever connected.
     */
    public Set<JsUserIdent> getAllPlayers()
    {
        return APIRegistry.perms.getServerZone().getKnownPlayers().stream().map(JsUserIdent::new).collect(Collectors.toSet());
    }

    /**
     * Returns the amount of time this player was active on the server in seconds
     */
    public long getTimePlayed(UUID playerId)
    {
        PlayerInfo pi = PlayerInfo.get(playerId);
        return pi == null ? 0 : pi.getTimePlayed() / 1000;
    }

    public Date getLastLogout(UUID playerId)
    {
        PlayerInfo pi = PlayerInfo.get(playerId);
        return pi == null ? null : pi.getLastLogout();
    }

    public Date getLastLogin(UUID playerId)
    {
        PlayerInfo pi = PlayerInfo.get(playerId);
        return pi == null ? null : pi.getLastLogin();
    }

    /**
     * Adds a CoRoutine callback
     *
     * @param count
     * @param tickStep
     * @param method
     * @param sender
     */
    public void AddCoRoutine(int count, int tickStep, String method, JsICommandSender sender)
    {
        AddCoRoutine(count, tickStep, method, sender, null);
    }

    public void AddCoRoutine(int count, int tickStep, String method, JsICommandSender sender, Object extraData)
    {
        coRoutineHashSet.add(new CORoutine(count * tickStep, tickStep, method, sender, extraData));
    }

    @SubscribeEvent
    public void _onTick(TickEvent.ServerTickEvent e)
    {
        if (e.phase == Phase.START)
        {
            Iterator<CORoutine> iterator = coRoutineHashSet.iterator();
            while (iterator.hasNext()) {
                CORoutine c = iterator.next();
                try
                {
                    if ((c.lastCount - c.tickCount) == c.tickStep)
                    {
                        c.lastCount = c.tickCount;
                        script.tryCallGlobal(c.method, c.sender);
                    }
                    c.tickCount--;
                    if (c.tickCount < 0)
                    {
                        iterator.remove();
                    }
                }
                catch (ScriptException scriptException)
                {
                    scriptException.printStackTrace();
                    iterator.remove();
                }
            }
        }
    }

    /**
     * Creates a custom inventory from an Itemstack list
     *
     * @param name
     * @param hasCustom
     * @param stacks
     * @return
     */
    /*
     * public JsInventory<InventoryBasic> createCustomInventory(final String name, boolean hasCustom, JsItemStack[] stacks) { InventoryBasic inventoryBasic = new
     * InventoryBasic(name, hasCustom, stacks.length); for (int i = 0; i < stacks.length; i++) { inventoryBasic.setInventorySlotContents(i, stacks[i].getThat()); } return
     * JsInventory.get(inventoryBasic); }
     */
    /**
     * Clones an existing inventory
     *
     * @param name
     * @param hasCustom
     * @param inventory
     * @param size
     * @return
     */
    /*
     * public JsInventory<Inventory> cloneInventory(final String name, boolean hasCustom, JsInventory<IInventory> inventory, int size) { if (size > inventory.getSize()) { size =
     * inventory.getSize(); }
     * 
     * InventoryBasic inventoryBasic = new InventoryBasic(name, hasCustom, size); for (int i = 0; i < size; i++) { inventoryBasic.setInventorySlotContents(i,
     * inventory.getThat().getStackInSlot(i));
     * 
     * } return JsInventory.get(inventoryBasic); }
     * 
     * private abstract class BasicInteraction extends Inventory implements IInteractionObject {
     * 
     * public BasicInteraction(String p_i1561_1_, boolean p_i1561_2_, IInventory source) { super(p_i1561_1_, p_i1561_2_, ((source.getContainerSize() - 1) / 9 + 1) * 9); for (int i
     * = 0; i < source.getSizeInventory(); i++) { this.setInventorySlotContents(i, source.getStackInSlot(i)); } } }
     */
    /**
     * Gets a Special Interaction Object that is designed to be used as a menu WARNING: Do not close the screen during the callback. This causes a desync!
     *
     * @param name
     * @param displayName
     * @param inventory
     * @param callbackMethod
     * @return
     */
    /*
     * public JsInteractionObject<InteractionObject> getMenuChest(final String name, final String displayName, final JsInventory<IInventory> inventory, final String callbackMethod)
     * { final boolean hasCustomName = displayName != null;
     * 
     * final InteractionObject menuChest = new BasicInteraction(name, hasCustomName, inventory.getThat()) {
     * 
     * @Override public Container createContainer(PlayerInventory inventoryPlayer, PlayerEntity entityPlayer) { return new ChestContainer(inventoryPlayer, this, entityPlayer) {
     * 
     * @Override public ItemStack slotClick(int p_slotClick_1_, int p_slotClick_2_, ClickType p_slotClick_3_, EntityPlayer p_slotClick_4_) { JsItemStack stack = JsItemStack.EMPTY;
     * try { JsInventory<IInventory> inv = null; JsItemStack itemStack = null; if (p_slotClick_1_ >= 0 && p_slotClick_1_ < inventorySlots.size()) { Slot slot =
     * inventorySlots.get(p_slotClick_1_); itemStack = JsItemStack.get(slot.getItem()); inv = JsInventory.get(slot.container); } Object _stack =
     * script.tryCallGlobal(callbackMethod, JsEntityPlayer.get(p_slotClick_4_), p_slotClick_1_, p_slotClick_2_, p_slotClick_3_, inv, itemStack); if (_stack instanceof JsItemStack)
     * { stack = (JsItemStack) _stack; } } catch (ScriptException e) { e.printStackTrace(); }
     * 
     * return stack.getThat(); }
     * 
     * @Override public ItemStack transferStackInSlot(PlayerEntity p_transferStackInSlot_1_, int p_transferStackInSlot_2_) { return ItemStack.EMPTY; }
     * 
     * @Override public boolean canMergeSlot(ItemStack p_canMergeSlot_1_, Slot p_canMergeSlot_2_) { return false; }
     * 
     * @Override public boolean canDragIntoSlot(Slot p_canDragIntoSlot_1_) { return false; } }; }
     * 
     * @Override public String getGuiID() { return "minecraft:chest"; }
     * 
     * @Override public String getName() { return name; }
     * 
     * @Override public boolean hasCustomName() { return hasCustomName; }
     * 
     * @Override public ITextComponent getDisplayName() { return displayName != null ? new StringTextComponent(displayName) : null; } };
     * 
     * return new JsInteractionObject<>(menuChest); }
     */
}
