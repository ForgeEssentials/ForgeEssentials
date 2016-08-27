
declare namespace MC {

    type int = number;
    type long = number;
    type float = number;
    type double = number;

    interface UUID {
    }

    interface JavaList<T> {
        size(): int;
        get(index: int): T;
    }

    interface IChatComponent { }
    interface UserIdent { }
    interface Item { }

    interface Block {
        getName(): string;
    }

    interface World {
        getDimension(): int;
        getDifficulty(): int;
        getPlayerEntities(): JavaList<EntityPlayer>;
        blockExists(x: int, y: int, z: int): boolean;
        getBlock(x: int, y: int, z: int): Block;
    }

    interface WorldServer extends World { }

    interface WorldPoint { }
    interface WorldZone { }

    interface ICommandSender {
        getName(): string;
    }

    interface Entity {
        getId(): string;
        getUuid(): UUID;
        getName(): string;
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
        setHealth(value: float);
        getMaxHealth(): float;
        getTotalArmorValue(): int;

        canEntityBeSeen(other: Entity): boolean;
    }

    interface EntityPlayer extends EntityLivingBase, ICommandSender {
        setPosition(x: double, y: double, z: double): void;
        setPosition(x: double, y: double, z: double, yaw: float, pitch: float): void;
    }

    interface CommandParserArgs {
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
        toArray(): string[];
        tostring(): string;
        getSenderPoint(): WorldPoint;
        getWorldZone(): WorldZone;
        needsPlayer(): void;
    }

    interface McStatic {
        getBlockFromName(name: string): Block;

        confirm(player: ICommandSender, message: string): void;

        doAs(sender: ICommandSender, doAsPlayer: UUID, hideChatOutput: boolean): ICommandSender;
        doAs(sender: ICommandSender, doAsPlayer: EntityPlayer, hideChatOutput: boolean): ICommandSender;

        cmd(sender: ICommandSender, cmd: string, ...args: string[]): ICommandSender;
    }
}

declare var mc: MC.McStatic;

declare var player: MC.EntityPlayer;

declare var sender: MC.ICommandSender;

declare var args: MC.CommandParserArgs;
