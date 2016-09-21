
export function pumpkinHeadCommand(args: fe.CommandArgs) {
    var player = args.player;
    if (!args.player) {
        args.confirm('Error: no player!');
        return;
    }

    if (args.isTabCompletion) // This is important so TAB completion does not actually change stuff
        return;

    player.getInventory().setStackInSlot(39, new mc.item.ItemStack(mc.world.Block.get('minecraft:pumpkin'), 1));
}

FEServer.registerCommand({
    name: 'pumpkinhead',
    usage: '/pumpkinhead',
    permission: 'fe.commands.pumpkinhead',
    opOnly: false,
    processCommand: pumpkinHeadCommand,
    tabComplete: pumpkinHeadCommand,
});
