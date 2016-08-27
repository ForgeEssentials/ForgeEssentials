
declare namespace MC {

    interface UUID {
    }

    interface ICommandSender {
        getCommandSenderName(): string;
    }

    interface EntityPlayer extends ICommandSender {
        getPersistentID(): UUID;
    }

    export function confirm(player: ICommandSender, message: string): void;

    export function doAs(sender: ICommandSender, doAsPlayer: UUID, hideChatOutput: boolean): ICommandSender;
    export function doAs(sender: ICommandSender, doAsPlayer: EntityPlayer, hideChatOutput: boolean): ICommandSender;

    export function cmd(sender: ICommandSender, cmd: string, ...args: string[]): ICommandSender;

}

declare var mc: typeof MC;

declare var player: MC.EntityPlayer;
