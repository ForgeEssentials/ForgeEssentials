
export function pumpkinHeadCommand(args: fe.CommandArgs) {
    if (!args.hasPlayer()) {
        args.error("Error: no player!");
        return;
    }

    var player = args.player;
    player.getInventory().setStackInSlot(39, new mc.item.ItemStack(mc.world.Block.get('minecraft:pumpkin'), 1));
}

FEServer.registerCommand({
    name: 'pumpkinhead',
    usage: '/pumpkinhead',
    opOnly: false,
    processCommand: pumpkinHeadCommand,
});
