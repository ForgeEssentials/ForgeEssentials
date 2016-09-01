
declare type int = number;
declare type long = number;
declare type float = number;
declare type double = number;

declare function getNbt(entity: mc.entity.Entity | mc.item.ItemStack): any;
declare function setNbt(entity: mc.entity.Entity | mc.item.ItemStack, data: any);

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

/**
 * Constants for permission level used when registering permissions
 */ 
declare enum PermissionLevel {
    TRUE, OP, FALSE
}

declare abstract class JavaList<T> {
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

declare namespace mc {
    
    type CommandCallback = (args: CommandArgs) => void;
    
	namespace world {
		
		class Block extends mc.JavaObject {
			getName(): string;
		}
		
		interface BlockStatic {
			getBlock(name: string): Block;
		}
		
		class Point extends mc.JavaObject {
			constructor(x: int, y: int, z: int);
			getX(): int;
			getY(): int;
			getZ(): int;
			setX(x: int): Point;
			setY(y: int): Point;
			setZ(z: int): Point;
			length(): double;
			distance(other: Point): double;
			add(other: Point): void;
			subtract(other: Point): void;
			distance(x: int, y: int, z: int): double;
			add(x: int, y: int, z: int): void;
			subtract(x: int, y: int, z: int): void;
		}
		
		class World extends mc.JavaObject {
			getDimension(): int;
			getDifficulty(): int;
			getPlayerEntities(): mc.entity.EntityPlayerList;
			getEntitiesWithinAABB(axisAlignedBB: mc.AxisAlignedBB): mc.entity.EntityList;
			blockExists(x: int, y: int, z: int): boolean;
			getBlock(x: int, y: int, z: int): Block;
			setBlock(x: int, y: int, z: int, block: Block): void;
			setBlock(x: int, y: int, z: int, block: Block, meta: int): void;
			asWorldServer(): WorldServer;
		}
		
		class WorldArea extends mc.AreaBase {
		}
		
		class WorldPoint extends Point {
			constructor(dim: int, x: int, y: int, z: int);
			getDimension(): int;
			setDimension(dim: int): void;
			setX(x: int): WorldPoint;
			setY(y: int): WorldPoint;
			setZ(z: int): WorldPoint;
		}
		
		class WorldServer extends World {
		}
		
		interface WorldStatic {
			getWorld(dim: int): WorldServer;
		}
		
	}
	
	namespace entity {
		
		class Entity extends mc.JavaObject {
			getName(): string;
			getId(): string;
			getUuid(): mc.UUID;
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
			constructor(list: List);
		}
		
		class EntityLivingBase extends Entity {
			getHealth(): float;
			setHealth(value: float): void;
			getMaxHealth(): float;
			getTotalArmorValue(): int;
			canEntityBeSeen(other: Entity): boolean;
		}
		
		class EntityPlayer extends EntityLivingBase {
			constructor(player: EntityPlayer);
			constructor(player: EntityPlayer, commandSender: mc.ICommandSender);
			setPosition(x: double, y: double, z: double): void;
			setPosition(x: double, y: double, z: double, yaw: float, pitch: float): void;
			asCommandSender(): mc.ICommandSender;
			getInventory(): mc.item.InventoryPlayer;
			getBedLocation(dimension: int): mc.world.Point;
		}
		
		class EntityPlayerList extends JavaList<EntityPlayer> {
			constructor(list: List);
		}
		
		class EntitySheep extends Entity {
			getFleeceColor(): int;
			setFleeceColor(color: int): void;
			isSheared(): boolean;
			setSheared(sheared: boolean): void;
		}
		
	}
	
	namespace event {
		
		class EntityEvent extends Event {
			constructor();
			getEntity(): mc.entity.Entity;
		}
		
		class Event {
			constructor();
			getEventType(): string;
			isCancelable(): boolean;
			isCanceled(): boolean;
			setCanceled(cancel: boolean): void;
			hasResult(): boolean;
			getResult(): Result;
			setResult(value: Result): void;
			getPhase(): EventPriority;
			setPhase(value: EventPriority): void;
			toString(): string;
		}
		
		class LivingEvent extends EntityEvent {
			constructor();
			getPlayer(): mc.entity.EntityLivingBase;
		}
		
		class PlayerEvent extends LivingEvent {
			constructor();
			getPlayer(): mc.entity.EntityPlayer;
		}
		
		class PlayerInteractEvent extends PlayerEvent {
			constructor();
		}
		
	}
	
	namespace item {
		
		class Enchantment extends mc.JavaObject {
		}
		
		class Inventory extends mc.JavaObject {
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
		
		class Item extends mc.JavaObject {
			getName(): string;
		}
		
		class ItemStack extends mc.JavaObject {
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
		
		interface ItemStatic {
			getItem(name: string): Item;
			createItemStack(block: mc.world.Block, stackSize: int): ItemStack;
			createItemStack(block: mc.world.Block, stackSize: int, damage: int): ItemStack;
			createItemStack(item: Item, stackSize: int): ItemStack;
			createItemStack(item: Item, stackSize: int, damage: int): ItemStack;
		}
		
	}
	
	namespace server {
		
		interface PermissionsStatic {
			checkBooleanPermission(permissionValue: string): boolean;
			getPermission(ident: UserIdent, point: mc.world.WorldPoint, area: mc.world.WorldArea, groups: string[], permissionNode: string, isProperty: boolean): string;
			checkPermission(player: mc.entity.EntityPlayer, permissionNode: string): boolean;
			getPermissionProperty(player: mc.entity.EntityPlayer, permissionNode: string): string;
			registerPermissionDescription(permissionNode: string, description: string): void;
			getPermissionDescription(permissionNode: string): string;
			registerPermission(permission: string, level: PermissionLevel): void;
			registerPermission(permissionNode: string, level: PermissionLevel, description: string): void;
			registerPermissionProperty(permissionNode: string, defaultValue: string): void;
			registerPermissionProperty(permissionNode: string, defaultValue: string, description: string): void;
			registerPermissionPropertyOp(permissionNode: string, defaultValue: string): void;
			registerPermissionPropertyOp(permissionNode: string, defaultValue: string, description: string): void;
			checkUserPermission(ident: UserIdent, permissionNode: string): boolean;
			getUserPermissionProperty(ident: UserIdent, permissionNode: string): string;
			getUserPermissionPropertyInt(ident: UserIdent, permissionNode: string): int;
			checkUserPermission(ident: UserIdent, targetPoint: mc.world.WorldPoint, permissionNode: string): boolean;
			getUserPermissionProperty(ident: UserIdent, targetPoint: mc.world.WorldPoint, permissionNode: string): string;
			checkUserPermission(ident: UserIdent, targetArea: mc.world.WorldArea, permissionNode: string): boolean;
			getUserPermissionProperty(ident: UserIdent, targetArea: mc.world.WorldArea, permissionNode: string): string;
			checkUserPermission(ident: UserIdent, zone: mc.Zone, permissionNode: string): boolean;
			getUserPermissionProperty(ident: UserIdent, zone: mc.Zone, permissionNode: string): string;
			getGroupPermissionProperty(group: string, permissionNode: string): string;
			getGroupPermissionProperty(group: string, zone: mc.Zone, permissionNode: string): string;
			checkGroupPermission(group: string, permissionNode: string): boolean;
			checkGroupPermission(group: string, zone: mc.Zone, permissionNode: string): boolean;
			getGroupPermissionProperty(group: string, point: mc.world.WorldPoint, permissionNode: string): string;
			checkGroupPermission(group: string, point: mc.world.WorldPoint, permissionNode: string): boolean;
			getGlobalPermissionProperty(permissionNode: string): string;
			getGlobalPermissionProperty(zone: mc.Zone, permissionNode: string): string;
			checkGlobalPermission(permissionNode: string): boolean;
			checkGlobalPermission(zone: mc.Zone, permissionNode: string): boolean;
			setPlayerPermission(ident: UserIdent, permissionNode: string, value: boolean): void;
			setPlayerPermissionProperty(ident: UserIdent, permissionNode: string, value: string): void;
			setGroupPermission(group: string, permissionNode: string, value: boolean): void;
			setGroupPermissionProperty(group: string, permissionNode: string, value: string): void;
			getZones(): mc.Zone[];
			getZoneById(id: int): mc.Zone;
			getZoneById(id: string): mc.Zone;
			getServerZone(): ServerZone;
			isSystemGroup(group: string): boolean;
			groupExists(groupName: string): boolean;
			createGroup(groupName: string): boolean;
			addPlayerToGroup(ident: UserIdent, group: string): void;
			removePlayerFromGroup(ident: UserIdent, group: string): void;
			getPrimaryGroup(ident: UserIdent): string;
		}
		
		interface ServerStatic {
			getServer(): mc.ICommandSender;
			/**
			 * Runs a Minecraft command.
			 * Be sure to separate each argument of the command as a single argument to this function.
			 * 
			 * Right: runCommand(sender, 'give', player.getName(), 'minecraft:dirt', 1);
			 * Wrong: runCommand(sender, 'give ' + player.getName() + ' minecraft:dirt 1');
			 */
			runCommand(sender: mc.ICommandSender, cmd: string, ...args: any[]): void;
			/**
			 * Runs a Minecraft command and ignores any errors it might throw
			 */
			tryRunCommand(sender: mc.ICommandSender, cmd: string, ...args: any[]): void;
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
			 * Registers a new command in the game.
			 * The processCommand and tabComplete handler can be the same, if the processCommand handler properly checks for args.isTabCompletion.
			 */
			registerCommand(options: CommandOptions): void;
			/**
			 * Registers a new event handler.
			 */
			registerEvent(event: string, handler: (event: mc.event.Event) => void): void;
			createPoint(x: int, y: int, z: int): mc.world.Point;
			createWorldPoint(dimension: int, x: int, y: int, z: int): mc.world.WorldPoint;
			createAxisAlignedBB(minX: double, minY: double, minZ: double, maxX: double, maxY: double, maxZ: double): mc.AxisAlignedBB;
		}
		
		class ServerZone extends mc.JavaObject {
		}
		
		class UserIdent extends mc.JavaObject {
			hasUsername(): boolean;
			hasUuid(): boolean;
			hasPlayer(): boolean;
			isFakePlayer(): boolean;
			isPlayer(): boolean;
			isNpc(): boolean;
			getUuid(): mc.UUID;
			getUsername(): string;
			getUsernameOrUuid(): string;
			getPlayer(): mc.entity.EntityPlayer;
			getFakePlayer(): mc.entity.EntityPlayer;
			getFakePlayer(world: mc.world.WorldServer): mc.entity.EntityPlayer;
			toSerializeString(): string;
			toString(): string;
			hashCode(): int;
			checkPermission(permissionNode: string): boolean;
			getPermissionProperty(permissionNode: string): string;
		}
		
	}
	
	class AreaBase extends JavaObject {
	}
	
	class AxisAlignedBB extends JavaObject {
		setBounds(minX: double, minY: double, minZ: double, maxX: double, maxY: double, maxZ: double): AxisAlignedBB;
	}
	
	class CommandArgs {
		sender: ICommandSender;
		player: entity.EntityPlayer;
		ident: server.UserIdent;
		isTabCompletion: boolean;
		constructor(args: CommandParserArgs);
		toArray(): string[];
		toString(): string;
		sendMessage(message: IChatComponent): void;
		confirm(message: string, ...args: any[]): void;
		notify(message: string, ...args: any[]): void;
		warn(message: string, ...args: any[]): void;
		error(message: string, ...args: any[]): void;
		size(): int;
		remove(): string;
		peek(): string;
		get(index: int): string;
		isEmpty(): boolean;
		hasPlayer(): boolean;
		parsePlayer(): server.UserIdent;
		parsePlayer(mustExist: boolean): server.UserIdent;
		parsePlayer(mustExist: boolean, mustBeOnline: boolean): server.UserIdent;
		parseItem(): item.Item;
		parseBlock(): world.Block;
		parsePermission(): string;
		checkPermission(perm: string): void;
		hasPermission(perm: string): boolean;
		tabComplete(...completionList: string[]): void;
		tabCompleteWord(completion: string): void;
		parseWorld(): world.WorldServer;
		parseInt(): int;
		parseInt(min: int, max: int): int;
		parseLong(): long;
		parseDouble(): double;
		parseBoolean(): boolean;
		parseTimeReadable(): long;
		checkTabCompletion(): void;
		requirePlayer(): void;
		getSenderPoint(): world.WorldPoint;
		needsPlayer(): void;
	}
	
	class CommandOptions {
		name: string;
		usage?: string;
		permission?: string;
		opOnly?: boolean;
		processCommand: CommandCallback;
		tabComplete?: CommandCallback;
		constructor();
	}
	
	class ICommandSender extends JavaObject {
		constructor(sender: ICommandSender);
		constructor(player: entity.EntityPlayer, jsPlayer: entity.EntityPlayer);
		getName(): string;
		getPlayer(): entity.EntityPlayer;
		doAs(userIdOrPlayer: any, hideChatOutput: boolean): ICommandSender;
		chat(message: string): void;
		chatConfirm(message: string): void;
		chatNotification(message: string): void;
		chatError(message: string): void;
		chatWarning(message: string): void;
	}
	
	interface WindowStatic {
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
		createPoint(x: int, y: int, z: int): world.Point;
		createWorldPoint(dimension: int, x: int, y: int, z: int): world.WorldPoint;
		createAxisAlignedBB(minX: double, minY: double, minZ: double, maxX: double, maxY: double, maxZ: double): AxisAlignedBB;
	}
	
	/**
	 * Basic wrapped java object
	 */
	class JavaObject {
		equals(obj: JavaObject): boolean;
		toString(): string;
		hashCode(): int;
		isInstanceOf(type: string): boolean;
	}
	
	class Zone extends JavaObject {
	}
	
	class UUID {
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
	
	interface CommandParserArgs { }
	
	interface IChatComponent { }
	
	namespace entity { interface List { } }
	
	namespace event { interface EventPriority { } }
	
	namespace event { interface Result { } }
	
	namespace server { interface PermissionLevel { } }
	
}

declare var window: mc.WindowStatic;
declare var Server: mc.server.ServerStatic;
declare var World: mc.world.WorldStatic;
declare var Block: mc.world.BlockStatic;
declare var Item: mc.item.ItemStatic;
declare var Permissions: mc.server.PermissionsStatic;

/**
 * Set a timeout to call 'handler' after 'timeout' milliseconds.
 */
declare function setTimeout(handler: (...args: any[]) => void, timeout?: any, ...args: any[]): number;
/**
 * Set a interval to call 'handler' fn repeatedly each 'interval' milliseconds.
 */
declare function setInterval(handler: (...args: any[]) => void, interval?: any, ...args: any[]): number;
/**
 * Clear a timeout.
 */
declare function clearTimeout(handle: int): void;
/**
 * Clear an interval.
 */
declare function clearInterval(handle: int): void;
declare function createPoint(x: int, y: int, z: int): mc.world.Point;
declare function createWorldPoint(dimension: int, x: int, y: int, z: int): mc.world.WorldPoint;
declare function createAxisAlignedBB(minX: double, minY: double, minZ: double, maxX: double, maxY: double, maxZ: double): mc.AxisAlignedBB;
