
declare namespace fe {

    type CommandCallback = (args: fe.CommandArgs) => void;

}

declare namespace net.minecraftforge.server.permission {
    enum DefaultPermissionLevel {
        TRUE,
        OP,
        FALSE,
    }
}

declare namespace com.forgeessentials.commons.selections {
    enum AreaShape {
        BOX,
        ELLIPSOID,
        CYLINDER,
    }
}

declare function createPoint(x: int, y: int, z: int);
declare function createWorldPoint(dim: int, x: int, y: int, z: int);

declare namespace fe {
	
	class AreaShape {
		static BOX: com.forgeessentials.commons.selections.AreaShape;
		static ELLIPSOID: com.forgeessentials.commons.selections.AreaShape;
		static CYLINDER: com.forgeessentials.commons.selections.AreaShape;
	}
	
	class CommandArgs extends Wrapper {
		sender: mc.CommandSource;
		player: mc.entity.PlayerEntity;
		ident: UserIdent;
		context: com.mojang.brigadier.context.CommandContext;
		params: string;
		hasPlayer(): boolean;
		confirm(message: string, args: any[]): void;
		notify(message: string, args: any[]): void;
		warn(message: string, args: any[]): void;
		error(message: string, args: any[]): void;
		parsePlayer(name: string): UserIdent;
		parsePlayer(name: string, mustExist: boolean): UserIdent;
		parsePlayer(name: string, mustExist: boolean, mustBeOnline: boolean): UserIdent;
		parseItem(argumentName: string): mc.item.Item;
		parseBlock(argumentName: string): mc.world.Block;
		parseWorld(argumentName: string): mc.world.ServerWorld;
		hasPermission(perm: string): boolean;
		parseTimeReadable(time: string): long;
		getSenderPoint(): WorldPoint;
		needsPlayer(): void;
		getArgumentBoolean(argumentName: string): boolean;
		getArgumentDouble(argumentName: string): double;
		getArgumentFloat(argumentName: string): float;
		getArgumentInteger(argumentName: string): int;
		getArgumentLong(argumentName: string): long;
		getArgumentString(argumentName: string): string;
	}
	
	class CommandOptions {
		name: string;
		usage?: string;
		opOnly?: boolean;
		executesMethod: boolean;
		executionParams?: string;
		/**
		 * Don't EVER USE THIS! INTERNAL USE ONLY!
		 */
		listsSubNodes?: java.util.List;
		/**
		 * Don't implement this directly, instead use subNode[anyLetterNumber] (ex. subNode4, subNodeXYZ)
		 */
		subNode?: any;
		processCommand: CommandCallback;
		constructor();
	}
	
	interface FEServer {
		/**
		 * Registers a new command in the game.
		 * The processCommand and tabComplete handler can be the same, if the processCommand handler properly checks for args.isTabCompletion.
		 */
		registerCommand(options: CommandOptions): void;
		/**
		 * Returns the total number of unique players that have connected to this server
		 */
		getUniquePlayerCount(): int;
		/**
		 * Returns the list of players who have ever connected.
		 */
		getAllPlayers(): java.util.Set;
		/**
		 * Returns the amount of time this player was active on the server in seconds
		 */
		getTimePlayed(playerId: java.util.UUID): long;
		getLastLogout(playerId: java.util.UUID): java.util.Date;
		getLastLogin(playerId: java.util.UUID): java.util.Date;
		/**
		 * Adds a CoRoutine callback
		 */
		AddCoRoutine(count: int, tickStep: int, method: string, sender: mc.CommandSource): void;
		AddCoRoutine(count: int, tickStep: int, method: string, sender: mc.CommandSource, extraData: any): void;
	}
	
	class Permissions {
		static checkBooleanPermission(permissionValue: string): boolean;
		static getPermission(ident: UserIdent, point: WorldPoint, area: WorldArea, groups: string[], permissionNode: string, isProperty: boolean): string;
		static checkPermission(player: mc.entity.PlayerEntity, permissionNode: string): boolean;
		static getPermissionProperty(player: mc.entity.PlayerEntity, permissionNode: string): string;
		static registerPermissionDescription(permissionNode: string, description: string): void;
		static getPermissionDescription(permissionNode: string): string;
		static registerPermission(permissionNode: string, level: net.minecraftforge.server.permission.DefaultPermissionLevel, description: string): void;
		static registerPermissionProperty(permissionNode: string, defaultValue: string): void;
		static registerPermissionProperty(permissionNode: string, defaultValue: string, description: string): void;
		static registerPermissionPropertyOp(permissionNode: string, defaultValue: string): void;
		static registerPermissionPropertyOp(permissionNode: string, defaultValue: string, description: string): void;
		static checkUserPermission(ident: UserIdent, permissionNode: string): boolean;
		static getUserPermissionProperty(ident: UserIdent, permissionNode: string): string;
		static getUserPermissionPropertyInt(ident: UserIdent, permissionNode: string): int;
		static checkUserPermission(ident: UserIdent, targetPoint: WorldPoint, permissionNode: string): boolean;
		static getUserPermissionProperty(ident: UserIdent, targetPoint: WorldPoint, permissionNode: string): string;
		static checkUserPermission(ident: UserIdent, targetArea: WorldArea, permissionNode: string): boolean;
		static getUserPermissionProperty(ident: UserIdent, targetArea: WorldArea, permissionNode: string): string;
		static checkUserPermission(ident: UserIdent, zone: Zone, permissionNode: string): boolean;
		static getUserPermissionProperty(ident: UserIdent, zone: Zone, permissionNode: string): string;
		static getGroupPermissionProperty(group: string, permissionNode: string): string;
		static getGroupPermissionProperty(group: string, zone: Zone, permissionNode: string): string;
		static checkGroupPermission(group: string, permissionNode: string): boolean;
		static checkGroupPermission(group: string, zone: Zone, permissionNode: string): boolean;
		static getGroupPermissionProperty(group: string, point: WorldPoint, permissionNode: string): string;
		static checkGroupPermission(group: string, point: WorldPoint, permissionNode: string): boolean;
		static getGlobalPermissionProperty(permissionNode: string): string;
		static getGlobalPermissionProperty(zone: Zone, permissionNode: string): string;
		static checkGlobalPermission(permissionNode: string): boolean;
		static checkGlobalPermission(zone: Zone, permissionNode: string): boolean;
		static setPlayerPermission(ident: UserIdent, permissionNode: string, value: boolean): void;
		static setPlayerPermissionProperty(ident: UserIdent, permissionNode: string, value: string): void;
		static setGroupPermission(group: string, permissionNode: string, value: boolean): void;
		static setGroupPermissionProperty(group: string, permissionNode: string, value: string): void;
		static getZones(): Zone[];
		static getZoneById(id: int): Zone;
		static getZoneById(id: string): Zone;
		static getServerZone(): ServerZone;
		static isSystemGroup(group: string): boolean;
		static groupExists(groupName: string): boolean;
		static createGroup(groupName: string): boolean;
		static addPlayerToGroup(ident: UserIdent, group: string): void;
		static removePlayerFromGroup(ident: UserIdent, group: string): void;
		static getPrimaryGroup(ident: UserIdent): string;
		constructor();
		getZoneAt(worldPoint: WorldPoint): Zone;
		getZonesAt(worldPoint: WorldPoint): java.util.List;
	}
	
	class PlayerInfo extends Wrapper {
		getUserIdent(): UserIdent;
		getFirstLogin(): java.util.Date;
		getLastLogin(): java.util.Date;
		getLastLogout(): java.util.Date;
		getTimePlayed(): long;
		setActive(): void;
		setActive(delta: long): void;
		getInactiveTime(): long;
		removeTimeout(name: string): void;
		checkTimeout(name: string): boolean;
		getRemainingTimeout(name: string): long;
		startTimeout(name: string, milliseconds: long): void;
		isWandEnabled(): boolean;
		setWandEnabled(wandEnabled: boolean): void;
		getWandID(): string;
		setWandID(wandID: string): void;
		getSel1(): Point;
		getSel2(): Point;
		getSelDim(): string;
		setSel1(point: Point): void;
		setSel2(point: Point): void;
		setSelDim(dimension: string): void;
		getLastTeleportOrigin(): WarpPoint;
		setLastTeleportOrigin(lastTeleportStart: WarpPoint): void;
		getLastDeathLocation(): WarpPoint;
		setLastDeathLocation(lastDeathLocation: WarpPoint): void;
		getLastTeleportTime(): long;
		setLastTeleportTime(currentTimeMillis: long): void;
		getHome(): WarpPoint;
		setHome(home: WarpPoint): void;
	}
	
	class Point extends Wrapper {
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
	
	class ServerZone extends Zone {
		getRootZone(): Zone;
		groupExists(name: string): boolean;
		createGroup(name: string): boolean;
		getZonesAt(worldPoint: WorldPoint): java.util.List;
		getZoneAt(worldPoint: WorldPoint): Zone;
		getPlayerGroups(player: mc.entity.PlayerEntity): java.util.List;
		addZone(zoneName: string, area: WorldArea): Zone;
		removeZone(zone: Zone): void;
	}
	
	class UserIdent extends Wrapper {
		hasUsername(): boolean;
		hasUuid(): boolean;
		hasPlayer(): boolean;
		isFakePlayer(): boolean;
		isPlayer(): boolean;
		isNpc(): boolean;
		getUuid(): java.util.UUID;
		getUsername(): string;
		getUsernameOrUuid(): string;
		getPlayer(): mc.entity.PlayerEntity;
		getFakePlayer(): mc.entity.PlayerEntity;
		getFakePlayer(world: mc.world.ServerWorld): mc.entity.PlayerEntity;
		toSerializeString(): string;
		toString(): string;
		hashCode(): int;
		checkPermission(permissionNode: string): boolean;
		getPermissionProperty(permissionNode: string): string;
		getPlayerInfo(): PlayerInfo;
		getWallet(): Wallet;
	}
	
	class Wallet extends Wrapper {
		get(): long;
		set(value: long): void;
		add(amount: long): void;
		add(amount: double): void;
		covers(value: long): boolean;
		withdraw(value: long): boolean;
		toString(): string;
	}
	
	class WarpPoint extends Wrapper {
		static fromString(value: string): WarpPoint;
		toWorldPoint(): WorldPoint;
		getBlockX(): int;
		getX(): double;
		getY(): double;
		getZ(): double;
		getDimension(): string;
		getPitch(): float;
		getYaw(): float;
		set(dim: string, xd: double, yd: double, zd: double, pitch: float, yaw: float): void;
		setDimension(dim: string): void;
		setX(value: double): void;
		setY(value: double): void;
		setZ(value: double): void;
		setPitch(value: float): void;
		setYaw(value: float): void;
		length(): double;
		distance(v: WarpPoint): double;
		distance(e: mc.entity.Entity): double;
		toString(): string;
		toReadableString(): string;
	}
	
	class WorldArea extends mc.AreaBase {
		constructor(dim: string, p1: Point, p2: Point);
		getDimension(): string;
	}
	
	class WorldPoint extends Point {
		constructor(dim: string, x: int, y: int, z: int);
		getDimension(): string;
		setDimension(dim: string): void;
		setX(x: int): WorldPoint;
		setY(y: int): WorldPoint;
		setZ(z: int): WorldPoint;
	}
	
	class Zone extends Wrapper {
		getId(): int;
		getName(): string;
		isPlayerInZone(player: mc.entity.PlayerEntity): boolean;
		isInZone(point: WorldPoint): boolean;
		isInZone(point: WorldArea): boolean;
		isPartOfZone(point: WorldArea): boolean;
		getParent(): Zone;
		getServerZone(): ServerZone;
	}
	
}

declare namespace fe.command {
	
	class ArgumentType extends java.lang.Enum {
		static BOOLEAN: ArgumentType;
		static DOUBLE: ArgumentType;
		static FLOAT: ArgumentType;
		static INTEGER: ArgumentType;
		static LONG: ArgumentType;
		static STRINGWORD: ArgumentType;
		static STRINGQUOTE: ArgumentType;
		static STRINGGREEDY: ArgumentType;
		static values(): ArgumentType[];
		static valueOf(name: string): ArgumentType;
		static getType(type: ArgumentType): com.mojang.brigadier.arguments.ArgumentType;
	}
	
	class CommandNodeArgument {
		executesMethod: boolean;
		executionParams?: string;
		argumentName: string;
		argumentType: JsArgumentType;
		constructor();
	}
	
	class CommandNodeLiteral {
		executesMethod: boolean;
		executionParams?: string;
		literal: string;
		constructor();
	}
	
	class CommandNodeWrapper {
		/**
		 * Don't EVER USE THIS! INTERNAL USE ONLY!
		 */
		listsChildNodes?: java.util.List;
		/**
		 * Don't implement this directly, instead use childNode[anyLetterNumber] (ex. childNode4, childNodeXYZ)
		 */
		childNode?: any;
		type: JsNodeType;
		containedNode: JsCommandNodeLiteral/JsCommandNodeArgument;
		constructor();
	}
	
	class NodeType extends java.lang.Enum {
		static LITERAL: NodeType;
		static ARGUMENT: NodeType;
		static values(): NodeType[];
		static valueOf(name: string): NodeType;
	}
	
}

declare namespace fe.event.entity.player {
	
	class FEPlayerEvent extends mc.event.entity.player.PlayerEvent {
		constructor();
	}
	
}

declare namespace fe.world {
	
	class WorldBorder extends Wrapper {
		static get(world: mc.world.World): WorldBorder;
		isEnabled(): boolean;
		setEnabled(enabled: boolean): void;
		getCenter(): fe.Point;
		setCenter(center: fe.Point): void;
		getSize(): fe.Point;
		setSize(size: fe.Point): void;
		getShape(): com.forgeessentials.commons.selections.AreaShape;
		setShape(shape: com.forgeessentials.commons.selections.AreaShape): void;
		getArea(): mc.AreaBase;
	}
	
}

declare class PermissionLevel {
	static TRUE: net.minecraftforge.server.permission.DefaultPermissionLevel;
	static OP: net.minecraftforge.server.permission.DefaultPermissionLevel;
	static FALSE: net.minecraftforge.server.permission.DefaultPermissionLevel;
}


declare var FEServer: fe.FEServer;

declare var Permissions: typeof fe.Permissions;
declare var AreaShape: typeof fe.AreaShape;
