
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
		 * Registers a new event handler.
		 */
		registerEvent(event: string, handler: (event: mc.event.Event) => void): void;
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
		 * Returns the amount of time this player was active on the server in seconds
		 */
		getTps(): double;
		/**
		 * Time since server start in ms
		 */
		getUptime(): long;
		/**
		 * Returns the number of players currently online
		 */
		getCurrentPlayerCount(): int;
		/**
		 * Returns the total number of unique players that have connected to this server
		 */
		getUniquePlayerCount(): int;
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
		getGameType(): net.minecraft.world.WorldSettings.GameType;
		/**
		 * Sets the player's game mode and sends it to them.
		 */
		setGameType(gameType: net.minecraft.world.WorldSettings.GameType): void;
		/**
		 * Whether the player is currently using an item (by holding down use button)
		 */
		isUsingItem(): boolean;
		/**
		 * Whether the player is currently using an item to block attacks
		 */
		isBlocking(): boolean;
		getScore(): int;
		/**
		 * Set player's score
		 */
		setScore(score: int): void;
		/**
		 * Add to player's score
		 */
		addScore(score: int): void;
		/**
		 * Returns how strong the player is against the specified block at this moment
		 */
		getBreakSpeed(block: mc.world.Block, cannotHarvestBlock: boolean, meta: int, x: int, y: int, z: int): float;
		/**
		 * Checks if the player has the ability to harvest a block (checks current inventory item for a tool if necessary)
		 */
		canHarvestBlock(block: mc.world.Block): boolean;
		getEyeHeight(): float;
		canAttackPlayer(player: EntityPlayer): boolean;
		/**
		 * Returns the current armor value as determined by a call to InventoryPlayer.getTotalArmorValue
		 */
		getTotalArmorValue(): int;
		/**
		 * When searching for vulnerable players, if a player is invisible, the return value of this is the chance of seeing
		 * them anyway.
		 */
		getArmorVisibility(): float;
		interactWith(entity: Entity): boolean;
		/**
		 * Returns the currently being used item by the player.
		 */
		getCurrentEquippedItem(): mc.item.ItemStack;
		/**
		 * Destroys the currently equipped item from the player's inventory.
		 */
		destroyCurrentEquippedItem(): void;
		/**
		 * Attacks for the player the targeted entity with the currently equipped item.
		 * The equipped item has hitEntity called on it.
		 */
		attackTargetEntityWithCurrentItem(targetEntity: Entity): void;
		/**
		 * Returns whether player is sleeping or not
		 */
		isPlayerSleeping(): boolean;
		/**
		 * Returns whether or not the player is asleep and the screen has fully faded.
		 */
		isPlayerFullyAsleep(): boolean;
		getCurrentArmor(slot: int): mc.item.ItemStack;
		/**
		 * Add experience points to player.
		 */
		addExperience(exp: int): void;
		/**
		 * Add experience levels to this player.
		 */
		addExperienceLevel(levels: int): void;
		/**
		 * increases exhaustion level by supplied amount
		 */
		addExhaustion(exhaustion: float): void;
		/**
		 * Get the player's food level.
		 */
		getFoodLevel(): int;
		/**
		 * Get the player's food saturation level.
		 */
		getSaturationLevel(): float;
		addFoodStats(foodLevel: int, foodSaturationModifier: float): void;
		/**
		 * If foodLevel is not max.
		 */
		needFood(): boolean;
		canEat(canEatWithoutHunger: boolean): boolean;
		/**
		 * Returns the InventoryEnderChest of this player.
		 */
		getInventoryEnderChest(): mc.item.Inventory;
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
		getWorldTime(): long;
		getTotalWorldTime(): long;
		/**
		 * Sets the world time.
		 */
		setWorldTime(time: long): void;
		setSpawnLocation(x: int, y: int, z: int): void;
		/**
		 * Called when checking if a certain block can be mined or not. The 'spawn safe zone' check is located here.
		 */
		canMineBlock(player: mc.entity.EntityPlayer, x: int, y: int, z: int): boolean;
		getWeightedThunderStrength(weight: float): float;
		/**
		 * Not sure about this actually. Reverting this one myself.
		 */
		getRainStrength(strength: float): float;
		/**
		 * Returns true if the current thunder strength (weighted with the rain strength) is greater than 0.9
		 */
		isThundering(): boolean;
		/**
		 * Returns true if the current rain strength is greater than 0.2
		 */
		isRaining(): boolean;
		canLightningStrikeAt(x: int, y: int, z: int): boolean;
		/**
		 * Checks to see if the biome rainfall values for a given x,y,z coordinate set are extremely high
		 */
		isBlockHighHumidity(x: int, y: int, z: int): boolean;
		/**
		 * Returns current world height.
		 */
		getHeight(): int;
		/**
		 * Returns current world height.
		 */
		getActualHeight(): int;
		getTopBlock(x: int, z: int): Block;
		/**
		 * Returns true if the block at the specified coordinates is empty
		 */
		isAirBlock(x: int, y: int, z: int): boolean;
		/**
		 * Checks if the specified block is able to see the sky
		 */
		canBlockSeeTheSky(x: int, y: int, z: int): boolean;
		/**
		 * Does the same as getBlockLightValue_do but without checking if its not a normal block
		 */
		getFullBlockLightValue(x: int, y: int, z: int): int;
		/**
		 * Gets the light value of a block location
		 */
		getBlockLightValue(x: int, y: int, z: int): int;
		/**
		 * Gets the light value of a block location. This is the actual function that gets the value and has a bool flag
		 * that indicates if its a half step block to get the maximum light value of a direct neighboring block (left,
		 * right, forward, back, and up)
		 */
		getBlockLightValue_do(x: int, y: int, z: int, isHalfBlock: boolean): int;
		/**
		 * Returns the y coordinate with a block in it at this x, z coordinate
		 */
		getHeightValue(x: int, z: int): int;
		/**
		 * Returns how bright the block is shown as which is the block's light value looked up in a lookup table (light
		 * values aren't linear for brightness). Args: x, y, z
		 */
		getLightBrightness(x: int, y: int, z: int): float;
		/**
		 * Checks whether its daytime by seeing if the light subtracted from the skylight is less than 4
		 */
		isDaytime(): boolean;
		/**
		 * calls calculateCelestialAngle
		 */
		getCelestialAngle(arg1: float): float;
		/**
		 * gets the current fullness of the moon expressed as a float between 1.0 and 0.0, in steps of .25
		 */
		getCurrentMoonPhaseFactor(): float;
		getCurrentMoonPhaseFactorBody(): float;
		/**
		 * Return getCelestialAngle() * 2 * PI
		 */
		getCelestialAngleRadians(arg1: float): float;
		/**
		 * Gets the closest player to the entity within the specified distance (if distance is less than 0 then ignored).
		 */
		getClosestPlayerToEntity(entity: mc.entity.Entity, dist: double): mc.entity.EntityPlayer;
		/**
		 * Gets the closest player to the point within the specified distance (distance can be set to less than 0 to not limit the distance).
		 */
		getClosestPlayer(x: double, y: double, z: double, dist: double): mc.entity.EntityPlayer;
		/**
		 * Retrieve the world seed from level.dat
		 */
		getSeed(): long;
	}
	
	class WorldServer extends World {
		static get(dim: int): WorldServer;
	}
	
}

declare class LocalStorage {
	/**
	 * Returns an integer representing the number of data items stored in the Storage object.
	 */
	static length(): int;
	/**
	 * When passed a number n, this method will return the name of the nth key in the storage.
	 */
	static key(n: int): string;
	/**
	 * When passed a key name, will return that key's value.
	 */
	static getItem(key: string): string;
	/**
	 * When passed a key name and value, will add that key to the storage, or update that key's value if it already exists.
	 * Returns the previous value for the passed key.
	 */
	static setItem(key: string, value: any): string;
	/**
	 * When passed a key name, will remove that key from the storage.
	 * Returns the previous value for the passed key.
	 */
	static removeItem(key: string): any;
	/**
	 * When invoked, will empty all keys out of the storage.
	 */
	static clear(): void;
}

declare interface Window {
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

declare var Server: mc.Server;
declare var window: Window;

declare var World: typeof mc.world.World;
declare var Item: typeof mc.item.Item;
declare var Block: typeof mc.world.Block;
declare var localStorage: typeof LocalStorage;
