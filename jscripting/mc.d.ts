
declare namespace MC {

    interface EntityPlayer {
        getCommandSenderName(): string;
    }

    export function confirm(player, message: string): void;

}

declare var mc: typeof MC;

declare var player: MC.EntityPlayer;
