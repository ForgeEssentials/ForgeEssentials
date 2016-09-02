
export function tabComplete(args: mc.CommandArgs) {
    processCommand(args);
}

export function processCommand(args: mc.CommandArgs) {
    var player = args.player;
    if (!args.player) {
        args.confirm('Error: no player!');
        return;
    }

    if (args.isTabCompletion) // This is important so TAB completion does not actually change stuff
        return;

    player.getInventory().setStackInSlot(39, Item.createItemStack(Block.getBlock('minecraft:pumpkin'), 1));
}
