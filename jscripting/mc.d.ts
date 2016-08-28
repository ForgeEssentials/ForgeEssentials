
declare type int = number;
declare type long = number;
declare type float = number;
declare type double = number;

declare namespace MC {
	
	interface JavaObject {
		equals(obj: JavaObject): boolean;
		toString(): string;
		hashCode(): int;
	}
	
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
	
	interface Block extends JavaObject {
		getName(): string;
	}
	
	interface BlockStatic {
		getBlock(name: string): Block;
	}
	
	interface CommandArgs {
		sender: ICommandSender;
		player: EntityPlayer;
		ident: UserIdent;
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
		isEmpty(): boolean;
		hasPlayer(): boolean;
		parsePlayer(): UserIdent;
		parsePlayer(mustExist: boolean): UserIdent;
		parsePlayer(mustExist: boolean, mustBeOnline: boolean): UserIdent;
		parseItem(): Item;
		parseBlock(): Block;
		parsePermission(): string;
		checkPermission(perm: string): void;
		hasPermission(perm: string): boolean;
		tabComplete(...completionList: string[]): void;
		tabCompleteWord(completion: string): void;
		parseWorld(): WorldServer;
		parseInt(): int;
		parseInt(min: int, max: int): int;
		parseLong(): long;
		parseDouble(): double;
		parseBoolean(): boolean;
		parseTimeReadable(): long;
		checkTabCompletion(): void;
		requirePlayer(): void;
		getSenderPoint(): WorldPoint;
		getWorldZone(): WorldZone;
		needsPlayer(): void;
	}
	
	interface CommandOptions {
		name: string;
		usage?: string;
		permission?: string;
		opOnly?: boolean;
	}
	
	interface ICommandSender extends JavaObject {
		getName(): string;
		getPlayer(): EntityPlayer;
		doAs(userIdOrPlayer: any, hideChatOutput: boolean): ICommandSender;
		chatConfirm(message: string): void;
		chatNotification(message: string): void;
		chatError(message: string): void;
		chatWarning(message: string): void;
	}
	
	interface Enchantment extends JavaObject {
	}
	
	interface Entity extends JavaObject {
		getName(): string;
		getId(): string;
		getUuid(): UUID;
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
		getWorld(): World;
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
		getCommandSender(): ICommandSender;
		getInventory(): InventoryPlayer;
		getBedLocation(dimension: int): Point;
	}
	
	interface EntityPlayerList extends JavaList<EntityPlayer> {
	}
	
	interface Inventory extends JavaObject {
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
	
	interface Item extends JavaObject {
		getName(): string;
	}
	
	interface ItemStack extends JavaObject {
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
		createItemStack(block: Block, stackSize: int): ItemStack;
		createItemStack(block: Block, stackSize: int, damage: int): ItemStack;
		createItemStack(item: Item, stackSize: int): ItemStack;
		createItemStack(item: Item, stackSize: int, damage: int): ItemStack;
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
	
	interface ServerStatic {
		getServer(): ICommandSender;
		runCommand(sender: ICommandSender, cmd: string, ...args: any[]): void;
		chatConfirm(message: string): void;
		chatNotification(message: string): void;
		chatError(message: string): void;
		chatWarning(message: string): void;
		registerCommand(options: CommandOptions, processCommand: CommandCallback, tabComplete?: CommandCallback): void;
		setTimeout(handler: (...args: any[]) => void, timeout?: any, ...args: any[]): number;
		setInterval(handler: (...args: any[]) => void, timeout?: any, ...args: any[]): number;
		clearTimeout(handle: number): void;
		clearInterval(handle: number): void;
	}
	
	interface UserIdent extends JavaObject {
		hasUsername(): boolean;
		hasUuid(): boolean;
		hasPlayer(): boolean;
		isFakePlayer(): boolean;
		isPlayer(): boolean;
		isNpc(): boolean;
		getUuid(): UUID;
		getUsername(): string;
		getUsernameOrUuid(): string;
		getPlayer(): EntityPlayer;
		getFakePlayer(): EntityPlayer;
		getFakePlayer(world: WorldServer): EntityPlayer;
		toSerializeString(): string;
		toString(): string;
		hashCode(): int;
		checkPermission(permissionNode: string): boolean;
		getPermissionProperty(permissionNode: string): string;
	}
	
	interface World extends JavaObject {
		getDimension(): int;
		getDifficulty(): int;
		getPlayerEntities(): JavaList<EntityPlayer>;
		blockExists(x: int, y: int, z: int): boolean;
		getBlock(x: int, y: int, z: int): Block;
		setBlock(x: int, y: int, z: int, block: Block): void;
		setBlock(x: int, y: int, z: int, block: Block, meta: int): void;
	}
	
	interface WorldPoint extends Point {
		getDimension(): int;
		setDimension(dim: int): void;
		setX(x: int): WorldPoint;
		setY(y: int): WorldPoint;
		setZ(z: int): WorldPoint;
	}
	
	interface WorldStatic {
		getWorld(dim: int): World;
	}
	
	interface WorldServer { }
	
	interface IChatComponent { }
	
	interface UUID { }
	
	interface WorldZone { }
	
}

declare var Server: MC.ServerStatic;
declare var World: MC.WorldStatic;
declare var Block: MC.BlockStatic;
declare var Item: MC.ItemStatic;

declare function getNbt(entity: MC.Entity | MC.ItemStack): any;
declare function setNbt(entity: MC.Entity | MC.ItemStack, data: any);

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
