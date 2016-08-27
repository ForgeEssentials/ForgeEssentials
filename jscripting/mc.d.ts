
declare namespace MC {

    type int = number;
    type long = number;
    type float = number;
    type double = number;

    interface UUID {
    }

    interface ICommandSender {
        getName(): string;
    }

    interface EntityPlayer extends ICommandSender {
        getId(): string;
        getUuid(): UUID;

        getHealth(): float;
        setHealth(value: float);

        getMaxHealth(): float;
    }

    interface IChatComponent { }
    interface UserIdent { }
    interface Item { }
    interface Block { }
    interface WorldServer { }
    interface WorldPoint { }
    interface WorldZone { }

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

    export function confirm(player: ICommandSender, message: string): void;

    export function doAs(sender: ICommandSender, doAsPlayer: UUID, hideChatOutput: boolean): ICommandSender;
    export function doAs(sender: ICommandSender, doAsPlayer: EntityPlayer, hideChatOutput: boolean): ICommandSender;

    export function cmd(sender: ICommandSender, cmd: string, ...args: string[]): ICommandSender;

}

declare var mc: typeof MC;

declare var player: MC.EntityPlayer;

declare var sender: MC.ICommandSender;

declare var args: MC.CommandParserArgs;
