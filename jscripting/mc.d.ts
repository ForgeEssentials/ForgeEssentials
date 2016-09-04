
declare type byte = number;
declare type int = number;
declare type long = number;
declare type float = number;
declare type double = number;

declare function getNbt(entity: mc.entity.Entity | mc.item.ItemStack): any;
declare function setNbt(entity: mc.entity.Entity | mc.item.ItemStack, data: any);

declare function setTimeout(handler: (...args: any[]) => void, timeout?: any, ...args: any[]): number;
declare function setInterval(handler: (...args: any[]) => void, interval?: any, ...args: any[]): number;
declare function clearTimeout(handle: int): void;
declare function clearInterval(handle: int): void;

declare function createAxisAlignedBB(minX: double, minY: double, minZ: double, maxX: double, maxY: double, maxZ: double);

/**
 * Constants that tell getNbt and setNbt the types of entries. Use nbt[NBT_INT + 'myVar'] for access
 */
declare const NBT_BYTE: string;
declare const NBT_SHORT: string;
declare const NBT_INT: string;
declare const NBT_LONG: string;
declare const NBT_FLOAT: string;
declare const NBT_DOUBLE: string;
declare const NBT_BYTE_ARRAY: string;
declare const NBT_STRING: string;
declare const NBT_COMPOUND: string;
declare const NBT_INT_ARRAY: string;

declare abstract class JavaList<T> extends Array<T> {
    size(): int;
    isEmpty(): boolean;
    toArray(): any[];
    get(index: int): T;
    add(element: T): T;
    set(index: int, element: T): T;
    clear(): void;
    remove(index: int): T;
    remove(element: T): boolean;
}

declare namespace cpw.mods.fml.common.eventhandler.Event {
    enum Result {
        DENY,
        DEFAULT,
        ALLOW
    }
}

declare namespace cpw.mods.fml.common.eventhandler {
    enum EventPriority {
        HIGHEST, // First to execute
        HIGH,
        NORMAL,
        LOW,
        LOWEST // Last to execute
    }
}

declare namespace mc {

	class AreaBase extends Wrapper {
	}

	class ICommandSender extends Wrapper {
		getName(): string;
		getPlayer(): entity.EntityPlayer;
		doAs(userIdOrPlayer: any, hideChatOutput: boolean): ICommandSender;
		chat(message: string): void;
		chatConfirm(message: string): void;
		chatNotification(message: string): void;
		chatError(message: string): void;
		chatWarning(message: string): void;
	}

	interface Server {
		getServer(): ICommandSender;
		/**
		 * Runs a Minecraft command.
		 * Be sure to separate each argument of the command as a single argument to this function.
		 *
		 * Right: runCommand(sender, 'give', player.getName(), 'minecraft:dirt', 1);
		 * Wrong: runCommand(sender, 'give ' + player.getName() + ' minecraft:dirt 1');
		 */
		runCommand(sender: ICommandSender, cmd: string, ...args: any[]): void;
		/**
		 * Runs a Minecraft command and ignores any errors it might throw
		 */
		tryRunCommand(sender: ICommandSender, cmd: string, ...args: any[]): void;
		/**
		 * Broadcast an uncolored message to all players
		 */
		chat(message: string): void;
		/**
		 * Broadcast a confirmation message to all players
		 */
		chatConfirm(message: string): void;
		/**
		 * Broadcast a notification message to all players
		 */
		chatNotification(message: string): void;
		/**
		 * Broadcast an error message to all players
		 */
		chatError(message: string): void;
		/**
		 * Broadcast a warning message to all players
		 */
		chatWarning(message: string): void;
		/**
		 * Registers a new event handler.
		 */
		registerEvent(event: string, handler: (event: mc.event.Event) => void): void;
	}

	interface Window {
		/**
		 * Set a timeout to call 'handler' after 'timeout' milliseconds.
		 */
		setTimeout(handler: (...args: any[]) => void, timeout?: any, ...args: any[]): number;
		/**
		 * Set a interval to call 'handler' fn repeatedly each 'interval' milliseconds.
		 */
		setInterval(handler: (...args: any[]) => void, interval?: any, ...args: any[]): number;
		/**
		 * Clear a timeout.
		 */
		clearTimeout(handle: int): void;
		/**
		 * Clear an interval.
		 */
		clearInterval(handle: int): void;
	}

	class Zone extends Wrapper {
	}

}

declare namespace mc.entity {

	class Entity extends Wrapper {
		getName(): string;
		getId(): string;
		getUuid(): java.util.UUID;
		getEntityId(): int;
		getDimension(): int;
		getX(): double;
		getY(): double;
		getZ(): double;
		getMotionX(): double;
		getMotionY(): double;
		getMotionZ(): double;
		getChunkCoordX(): int;
		getChunkCoordY(): int;
		getChunkCoordZ(): int;
		getWidth(): float;
		getHeight(): float;
		getStepHeight(): float;
		isOnGround(): boolean;
		getRidingEntity(): Entity;
		getRiddenByEntity(): Entity;
		getWorld(): mc.world.World;
		getEntityType(): string;
	}

	class EntityList extends JavaList<Entity> {
	}

	class EntityLivingBase extends Entity {
		getHealth(): float;
		setHealth(value: float): void;
		getMaxHealth(): float;
		getTotalArmorValue(): int;
		canEntityBeSeen(other: Entity): boolean;
	}

	class EntityPlayer extends EntityLivingBase {
		setPosition(x: double, y: double, z: double): void;
		setPosition(x: double, y: double, z: double, yaw: float, pitch: float): void;
		asCommandSender(): mc.ICommandSender;
		getInventory(): mc.item.InventoryPlayer;
		getBedLocation(dimension: int): fe.Point;
	}

	class EntityPlayerList extends JavaList<EntityPlayer> {
	}

	class EntitySheep extends Entity {
		getFleeceColor(): int;
		setFleeceColor(color: int): void;
		isSheared(): boolean;
		setSheared(sheared: boolean): void;
	}

}

declare namespace mc.event {

	class Event {
		getEventType(): string;
		isCancelable(): boolean;
		isCanceled(): boolean;
		setCanceled(cancel: boolean): void;
		hasResult(): boolean;
		getResult(): cpw.mods.fml.common.eventhandler.Event.Result;
		setResult(value: cpw.mods.fml.common.eventhandler.Event.Result): void;
		getPhase(): cpw.mods.fml.common.eventhandler.EventPriority;
		setPhase(value: cpw.mods.fml.common.eventhandler.EventPriority): void;
		toString(): string;
	}

}

declare namespace mc.event.entity {

	class EntityEvent extends mc.event.Event {
		constructor();
		getEntity(): mc.entity.Entity;
	}

	class LivingEvent extends EntityEvent {
		constructor();
		getPlayer(): mc.entity.EntityLivingBase;
	}

}

declare namespace mc.event.entity.player {

	class AchievementEvent extends PlayerEvent {
		constructor();
	}

	class AnvilRepairEvent extends PlayerEvent {
		constructor();
	}

	class ArrowLooseEvent extends PlayerEvent {
		constructor();
	}

	class ArrowNockEvent extends PlayerEvent {
		constructor();
	}

	class AttackEntityEvent extends PlayerEvent {
		constructor();
	}

	class BonemealEvent extends PlayerEvent {
		constructor();
	}

	class EntityInteractEvent extends PlayerEvent {
		constructor();
	}

	class EntityItemPickupEvent extends PlayerEvent {
		constructor();
	}

	class PlayerBreakSpeedEvent extends PlayerEvent {
		constructor();
	}

	class PlayerEvent extends mc.event.entity.LivingEvent {
		constructor();
		getPlayer(): mc.entity.EntityPlayer;
	}

	class PlayerInteractEvent extends PlayerEvent {
		constructor();
	}

	class PlayerWakeUpEvent extends PlayerEvent {
		constructor();
	}

	class UseHoeEvent extends PlayerEvent {
		constructor();
	}

}

declare namespace mc.item {

	class Enchantment extends Wrapper {
	}

	class Inventory extends Wrapper {
		getStackInSlot(slot: int): ItemStack;
		setStackInSlot(slot: int, stack: ItemStack): void;
		isStackValidForSlot(slot: int, stack: ItemStack): boolean;
		getSize(): int;
		getStackLimit(): int;
		getName(): string;
		hasCustomName(): boolean;
	}

	class InventoryPlayer extends Inventory {
		getCurrentItem(): ItemStack;
		getCurrentItemIndex(): int;
		setCurrentItemIndex(index: int): void;
	}

	class Item extends Wrapper {
		static get(name: string): Item;
		getName(): string;
	}

	class ItemStack extends Wrapper {
		constructor(block: mc.world.Block, stackSize: int);
		constructor(block: mc.world.Block, stackSize: int, damage: int);
		constructor(item: Item, stackSize: int);
		constructor(item: Item, stackSize: int, damage: int);
		getItem(): Item;
		getStackSize(): int;
		setStackSize(size: int): void;
		getMaxStackSize(): int;
		isStackable(): boolean;
		isDamageable(): boolean;
		isDamaged(): boolean;
		getDamage(): int;
		setDamage(damage: int): void;
		getMaxDamage(): int;
		getDisplayName(): string;
		setDisplayName(name: string): void;
		hasDisplayName(): boolean;
		isItemEnchanted(): boolean;
		getRepairCost(): int;
		setRepairCost(cost: int): void;
	}

}

declare namespace mc.util {

	class AxisAlignedBB extends Wrapper {
		constructor(minX: double, minY: double, minZ: double, maxX: double, maxY: double, maxZ: double);
		setBounds(minX: double, minY: double, minZ: double, maxX: double, maxY: double, maxZ: double): AxisAlignedBB;
	}

}

declare namespace mc.world {

	class Block extends Wrapper {
		static get(name: string): Block;
		getName(): string;
	}

	class TileEntity extends Wrapper {
		getInventory(): mc.item.Inventory;
	}

	class World extends Wrapper {
		static get(dim: int): WorldServer;
		getDimension(): int;
		getDifficulty(): int;
		getPlayerEntities(): mc.entity.EntityPlayerList;
		getEntitiesWithinAABB(axisAlignedBB: mc.util.AxisAlignedBB): mc.entity.EntityList;
		blockExists(x: int, y: int, z: int): boolean;
		getBlock(x: int, y: int, z: int): Block;
		setBlock(x: int, y: int, z: int, block: Block): void;
		setBlock(x: int, y: int, z: int, block: Block, meta: int): void;
		getTileEntity(x: int, y: int, z: int): TileEntity;
		asWorldServer(): WorldServer;
	}

	class WorldServer extends World {
		static get(dim: int): WorldServer;
	}

}

/**
 * Basic wrapped java object
 */
declare class Wrapper {
	equals(obj: Wrapper): boolean;
	toString(): string;
	hashCode(): int;
	isInstanceOf(type: string): boolean;
}

declare namespace java.util {
	class UUID {
		static randomUUID(): UUID;
		static nameUUIDFromBytes(arg0: byte[]): UUID;
		static fromString(arg0: string): UUID;
		constructor(arg0: long, arg1: long);
		getLeastSignificantBits(): long;
		getMostSignificantBits(): long;
		version(): int;
		variant(): int;
		timestamp(): long;
		clockSequence(): int;
		node(): long;
		toString(): string;
		hashCode(): int;
		equals(arg0: any): boolean;
		compareTo(arg0: UUID): int;
	}

}

declare var window: mc.Window;
declare var Server: mc.Server;

declare var World: typeof mc.world.World;
declare var Block: typeof mc.world.Block;
declare var Item: typeof mc.item.Item;
