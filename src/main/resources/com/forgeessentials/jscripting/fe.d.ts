
declare namespace fe {

    type CommandCallback = (args: fe.CommandArgs) => void;

}

declare namespace net.minecraftforge.permission {
    enum PermissionLevel {
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
		sender: mc.ICommandSender;
		player: mc.entity.EntityPlayer;
		ident: UserIdent;
		isTabCompletion: boolean;
		toArray(): string[];
		toString(): string;
		confirm(message: string, ...args: any[]): void;
		notify(message: string, ...args: any[]): void;
		warn(message: string, ...args: any[]): void;
		error(message: string, ...args: any[]): void;
		size(): int;
		remove(): string;
		peek(): string;
		get(index: int): string;
		getAllArgs(): string;
		isEmpty(): boolean;
		hasPlayer(): boolean;
		parsePlayer(): UserIdent;
		parsePlayer(mustExist: boolean): UserIdent;
		parsePlayer(mustExist: boolean, mustBeOnline: boolean): UserIdent;
		parseItem(): mc.item.Item;
		parseBlock(): mc.world.Block;
		parsePermission(): string;
		checkPermission(perm: string): void;
		hasPermission(perm: string): boolean;
		tabComplete(...completionList: string[]): void;
		tabCompleteWord(completion: string): void;
		parseWorld(): mc.world.WorldServer;
		parseInt(): int;
		parseInt(min: int, max: int): int;
		parseLong(): long;
		parseDouble(): double;
		parseBoolean(): boolean;
		parseTimeReadable(): long;
		checkTabCompletion(): void;
		requirePlayer(): void;
		getSenderPoint(): WorldPoint;
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
	}
	
	class Permissions {
		static checkBooleanPermission(permissionValue: string): boolean;
		static getPermission(ident: UserIdent, point: WorldPoint, area: WorldArea, groups: string[], permissionNode: string, isProperty: boolean): string;
		static checkPermission(player: mc.entity.EntityPlayer, permissionNode: string): boolean;
		static getPermissionProperty(player: mc.entity.EntityPlayer, permissionNode: string): string;
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
		getWandDmg(): int;
		setWandDmg(wandDmg: int): void;
		getSel1(): Point;
		getSel2(): Point;
		getSelDim(): int;
		setSel1(point: Point): void;
		setSel2(point: Point): void;
		setSelDim(dimension: int): void;
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
		getPlayerGroups(player: mc.entity.EntityPlayer): java.util.List;
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
		getPlayer(): mc.entity.EntityPlayer;
		getFakePlayer(): mc.entity.EntityPlayer;
		getFakePlayer(world: mc.world.WorldServer): mc.entity.EntityPlayer;
		toSerializeString(): string;
		toString(): string;
		hashCode(): int;
		checkPermission(permissionNode: string): boolean;
		getPermissionProperty(permissionNode: string): string;
		getPlayerInfo(): PlayerInfo;
	}
	
	class WarpPoint extends Wrapper {
		static fromString(value: string): WarpPoint;
		toWorldPoint(): WorldPoint;
		getBlockX(): int;
		getX(): double;
		getY(): double;
		getZ(): double;
		getDimension(): int;
		getPitch(): float;
		getYaw(): float;
		set(dim: int, xd: double, yd: double, zd: double, pitch: float, yaw: float): void;
		setDimension(dim: int): void;
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
	}
	
	class WorldPoint extends Point {
		constructor(dim: int, x: int, y: int, z: int);
		getDimension(): int;
		setDimension(dim: int): void;
		setX(x: int): WorldPoint;
		setY(y: int): WorldPoint;
		setZ(z: int): WorldPoint;
	}
	
	class Zone extends Wrapper {
		getId(): int;
		getName(): string;
		isPlayerInZone(player: mc.entity.EntityPlayer): boolean;
		isInZone(point: WorldPoint): boolean;
		isInZone(point: WorldArea): boolean;
		isPartOfZone(point: WorldArea): boolean;
		getParent(): Zone;
		getServerZone(): ServerZone;
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
