
declare type int = number;
declare type long = number;
declare type float = number;
declare type double = number;

declare function getNbt(entity: MC.Entity.Entity | MC.Item.ItemStack): any;
declare function setNbt(entity: MC.Entity.Entity | MC.Item.ItemStack, data: any);

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
declare const PERMLEVEL_TRUE: int;
declare const PERMLEVEL_OP: int;
declare const PERMLEVEL_FALSE: int;

declare namespace MC {
    
    interface JavaList<T> {
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
    
    type CommandCallback = (args: CommandArgs) => void;
    
	namespace World {
		
		interface Block extends MC.JavaObject {
			getName(): string;
		}
		
		interface BlockStatic {
			getBlock(name: string): Block;
		}
		
		interface World extends MC.JavaObject {
			getDimension(): int;
			getDifficulty(): int;
			getPlayerEntities(): MC.Entity.EntityPlayerList;
			getEntitiesWithinAABB(axisAlignedBB: MC.AxisAlignedBB): MC.Entity.EntityList;
			blockExists(x: int, y: int, z: int): boolean;
			getBlock(x: int, y: int, z: int): Block;
			setBlock(x: int, y: int, z: int, block: Block): void;
			setBlock(x: int, y: int, z: int, block: Block, meta: int): void;
			asWorldServer(): WorldServer;
		}
		
		interface WorldArea extends MC.AreaBase {
		}
		
		interface WorldPoint extends MC.Point {
			getDimension(): int;
			setDimension(dim: int): void;
			setX(x: int): WorldPoint;
			setY(y: int): WorldPoint;
			setZ(z: int): WorldPoint;
		}
		
		interface WorldServer extends World {
		}
		
		interface WorldStatic {
			getWorld(dim: int): WorldServer;
		}
		
	}
	
	namespace Entity {
		
		interface Entity extends MC.JavaObject {
			getName(): string;
			getId(): string;
			getUuid(): MC.UUID;
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
			getWorld(): MC.World.World;
			getEntityType(): string;
		}
		
		interface EntityList extends JavaList<Entity> {
		}
		
		interface EntityLivingBase extends Entity {
			getHealth(): float;
			setHealth(value: float): void;
			getMaxHealth(): float;
			getTotalArmorValue(): int;
			canEntityBeSeen(other: Entity): boolean;
		}
		
		interface EntityPlayer extends EntityLivingBase {
			setPosition(x: double, y: double, z: double): void;
			setPosition(x: double, y: double, z: double, yaw: float, pitch: float): void;
			asCommandSender(): MC.ICommandSender;
			getInventory(): MC.Item.InventoryPlayer;
			getBedLocation(dimension: int): MC.Point;
		}
		
		interface EntityPlayerList extends JavaList<EntityPlayer> {
		}
		
		interface EntitySheep extends Entity {
			getFleeceColor(): int;
			setFleeceColor(color: int): void;
			isSheared(): boolean;
			setSheared(sheared: boolean): void;
		}
		
	}
	
	namespace Event {
		
		interface EntityEvent extends Event {
			getEntity(): MC.Entity.Entity;
		}
		
		interface Event {
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
		
		interface LivingEvent extends EntityEvent {
			getPlayer(): MC.Entity.EntityLivingBase;
		}
		
		interface PlayerEvent extends LivingEvent {
			getPlayer(): MC.Entity.EntityPlayer;
		}
		
		interface PlayerInteractEvent extends PlayerEvent {
		}
		
	}
	
	namespace Item {
		
		interface Enchantment extends MC.JavaObject {
		}
		
		interface Inventory extends MC.JavaObject {
			getStackInSlot(slot: int): ItemStack;
			setStackInSlot(slot: int, stack: ItemStack): void;
			isStackValidForSlot(slot: int, stack: ItemStack): boolean;
			getSize(): int;
			getStackLimit(): int;
			getName(): string;
			hasCustomName(): boolean;
		}
		
		interface InventoryPlayer extends Inventory {
			getCurrentItem(): ItemStack;
			getCurrentItemIndex(): int;
			setCurrentItemIndex(index: int): void;
		}
		
		interface Item extends MC.JavaObject {
			getName(): string;
		}
		
		interface ItemStack extends MC.JavaObject {
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
			createItemStack(block: MC.World.Block, stackSize: int): ItemStack;
			createItemStack(block: MC.World.Block, stackSize: int, damage: int): ItemStack;
			createItemStack(item: Item, stackSize: int): ItemStack;
			createItemStack(item: Item, stackSize: int, damage: int): ItemStack;
		}
		
	}
	
	namespace Server {
		
		interface PermissionsStatic {
			checkBooleanPermission(permissionValue: string): boolean;
			getPermission(ident: UserIdent, point: MC.World.WorldPoint, area: MC.World.WorldArea, groups: string[], permissionNode: string, isProperty: boolean): string;
			checkPermission(player: MC.Entity.EntityPlayer, permissionNode: string): boolean;
			getPermissionProperty(player: MC.Entity.EntityPlayer, permissionNode: string): string;
			registerPermissionDescription(permissionNode: string, description: string): void;
			getPermissionDescription(permissionNode: string): string;
			registerPermission(permission: string, level: int): void;
			registerPermission(permissionNode: string, level: int, description: string): void;
			registerPermissionProperty(permissionNode: string, defaultValue: string): void;
			registerPermissionProperty(permissionNode: string, defaultValue: string, description: string): void;
			registerPermissionPropertyOp(permissionNode: string, defaultValue: string): void;
			registerPermissionPropertyOp(permissionNode: string, defaultValue: string, description: string): void;
			checkUserPermission(ident: UserIdent, permissionNode: string): boolean;
			getUserPermissionProperty(ident: UserIdent, permissionNode: string): string;
			getUserPermissionPropertyInt(ident: UserIdent, permissionNode: string): int;
			checkUserPermission(ident: UserIdent, targetPoint: MC.World.WorldPoint, permissionNode: string): boolean;
			getUserPermissionProperty(ident: UserIdent, targetPoint: MC.World.WorldPoint, permissionNode: string): string;
			checkUserPermission(ident: UserIdent, targetArea: MC.World.WorldArea, permissionNode: string): boolean;
			getUserPermissionProperty(ident: UserIdent, targetArea: MC.World.WorldArea, permissionNode: string): string;
			checkUserPermission(ident: UserIdent, zone: MC.Zone, permissionNode: string): boolean;
			getUserPermissionProperty(ident: UserIdent, zone: MC.Zone, permissionNode: string): string;
			getGroupPermissionProperty(group: string, permissionNode: string): string;
			getGroupPermissionProperty(group: string, zone: MC.Zone, permissionNode: string): string;
			checkGroupPermission(group: string, permissionNode: string): boolean;
			checkGroupPermission(group: string, zone: MC.Zone, permissionNode: string): boolean;
			getGroupPermissionProperty(group: string, point: MC.World.WorldPoint, permissionNode: string): string;
			checkGroupPermission(group: string, point: MC.World.WorldPoint, permissionNode: string): boolean;
			getGlobalPermissionProperty(permissionNode: string): string;
			getGlobalPermissionProperty(zone: MC.Zone, permissionNode: string): string;
			checkGlobalPermission(permissionNode: string): boolean;
			checkGlobalPermission(zone: MC.Zone, permissionNode: string): boolean;
			setPlayerPermission(ident: UserIdent, permissionNode: string, value: boolean): void;
			setPlayerPermissionProperty(ident: UserIdent, permissionNode: string, value: string): void;
			setGroupPermission(group: string, permissionNode: string, value: boolean): void;
			setGroupPermissionProperty(group: string, permissionNode: string, value: string): void;
			getZones(): MC.Zone[];
			getZoneById(id: int): MC.Zone;
			getZoneById(id: string): MC.Zone;
			getServerZone(): ServerZone;
			isSystemGroup(group: string): boolean;
			groupExists(groupName: string): boolean;
			createGroup(groupName: string): boolean;
			addPlayerToGroup(ident: UserIdent, group: string): void;
			removePlayerFromGroup(ident: UserIdent, group: string): void;
			getPrimaryGroup(ident: UserIdent): string;
		}
		
		interface ServerStatic {
			getServer(): MC.ICommandSender;
			/**
			 * Runs a Minecraft command.
			 * Be sure to separate each argument of the command as a single argument to this function.
			 * 
			 * Right: runCommand(sender, 'give', player.getName(), 'minecraft:dirt', 1);
			 * Wrong: runCommand(sender, 'give ' + player.getName() + ' minecraft:dirt 1');
			 */
			runCommand(sender: MC.ICommandSender, cmd: string, ...args: any[]): void;
			/**
			 * Runs a Minecraft command and ignores any errors it might throw
			 */
			tryRunCommand(sender: MC.ICommandSender, cmd: string, ...args: any[]): void;
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
			registerEvent(event: string, handler: (event: MC.Event.Event) => void): void;
			createPoint(x: int, y: int, z: int): MC.Point;
			createWorldPoint(dimension: int, x: int, y: int, z: int): MC.World.WorldPoint;
			createAxisAlignedBB(minX: double, minY: double, minZ: double, maxX: double, maxY: double, maxZ: double): MC.AxisAlignedBB;
		}
		
		interface ServerZone extends MC.JavaObject {
		}
		
		interface UserIdent extends MC.JavaObject {
			hasUsername(): boolean;
			hasUuid(): boolean;
			hasPlayer(): boolean;
			isFakePlayer(): boolean;
			isPlayer(): boolean;
			isNpc(): boolean;
			getUuid(): MC.UUID;
			getUsername(): string;
			getUsernameOrUuid(): string;
			getPlayer(): MC.Entity.EntityPlayer;
			getFakePlayer(): MC.Entity.EntityPlayer;
			getFakePlayer(world: MC.World.WorldServer): MC.Entity.EntityPlayer;
			toSerializeString(): string;
			toString(): string;
			hashCode(): int;
			checkPermission(permissionNode: string): boolean;
			getPermissionProperty(permissionNode: string): string;
		}
		
	}
	
	interface AreaBase extends JavaObject {
	}
	
	interface AxisAlignedBB extends JavaObject {
		setBounds(minX: double, minY: double, minZ: double, maxX: double, maxY: double, maxZ: double): AxisAlignedBB;
	}
	
	interface CommandArgs {
		sender: ICommandSender;
		player: Entity.EntityPlayer;
		ident: Server.UserIdent;
		isTabCompletion: boolean;
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
		parsePlayer(): Server.UserIdent;
		parsePlayer(mustExist: boolean): Server.UserIdent;
		parsePlayer(mustExist: boolean, mustBeOnline: boolean): Server.UserIdent;
		parseItem(): Item.Item;
		parseBlock(): World.Block;
		parsePermission(): string;
		checkPermission(perm: string): void;
		hasPermission(perm: string): boolean;
		tabComplete(...completionList: string[]): void;
		tabCompleteWord(completion: string): void;
		parseWorld(): World.WorldServer;
		parseInt(): int;
		parseInt(min: int, max: int): int;
		parseLong(): long;
		parseDouble(): double;
		parseBoolean(): boolean;
		parseTimeReadable(): long;
		checkTabCompletion(): void;
		requirePlayer(): void;
		getSenderPoint(): World.WorldPoint;
		needsPlayer(): void;
	}
	
	interface CommandOptions {
		name: string;
		usage?: string;
		permission?: string;
		opOnly?: boolean;
		processCommand: CommandCallback;
		tabComplete?: CommandCallback;
	}
	
	interface ICommandSender extends JavaObject {
		getName(): string;
		getPlayer(): Entity.EntityPlayer;
		doAs(userIdOrPlayer: any, hideChatOutput: boolean): ICommandSender;
		chat(message: string): void;
		chatConfirm(message: string): void;
		chatNotification(message: string): void;
		chatError(message: string): void;
		chatWarning(message: string): void;
	}
	
	interface Point extends JavaObject {
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
		createPoint(x: int, y: int, z: int): Point;
		createWorldPoint(dimension: int, x: int, y: int, z: int): World.WorldPoint;
		createAxisAlignedBB(minX: double, minY: double, minZ: double, maxX: double, maxY: double, maxZ: double): AxisAlignedBB;
	}
	
	/**
	 * Basic wrapped java object
	 */
	interface JavaObject {
		equals(obj: JavaObject): boolean;
		toString(): string;
		hashCode(): int;
		isInstanceOf(type: string): boolean;
	}
	
	interface Zone extends JavaObject {
	}
	
	interface UUID {
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
	
	namespace Event { interface EventPriority { } }
	
	namespace Event { interface Result { } }
	
	interface IChatComponent { }
	
}

declare var window: MC.WindowStatic;
declare var Server: MC.Server.ServerStatic;
declare var World: MC.World.WorldStatic;
declare var Block: MC.World.BlockStatic;
declare var Item: MC.Item.ItemStatic;
declare var Permissions: MC.Server.PermissionsStatic;

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
declare function createPoint(x: int, y: int, z: int): MC.Point;
declare function createWorldPoint(dimension: int, x: int, y: int, z: int): MC.World.WorldPoint;
declare function createAxisAlignedBB(minX: double, minY: double, minZ: double, maxX: double, maxY: double, maxZ: double): MC.AxisAlignedBB;
