
export function sheepRandomCommand(args: MC.CommandArgs) {
    if (args.isTabCompletion) // This is important so TAB completion does not actually change stuff
        return;
    sheepRandom(args.player);
}

export function sheepRandom(player: MC.Entity.EntityPlayer) {
    var aabb = createAxisAlignedBB(player.getX() - 10, player.getY() - 10, player.getZ() - 10, player.getX() + 10, player.getY() + 10, player.getZ() + 10);
    var list = player.getWorld().getEntitiesWithinAABB(aabb);
    var listArray = list.toArray();
    for (var i = 0; i < listArray.length; i++) {
        if (listArray[i].getEntityType() === 'EntitySheep') {
            listArray[i].setFleeceColor(Math.round(Math.random() * 15));
        }
    }
}

Server.registerEvent('playerInteractEvent', (event: MC.Event.PlayerInteractEvent) => {
    sheepRandom(event.getPlayer());
});

Server.registerCommand({
    name: 'sheeprandom',
    usage: '/sheeprandom',
    permission: 'fe.teleport.sheeprandom',
    opOnly: false,
    processCommand: sheepRandomCommand,
    tabComplete: sheepRandomCommand,
});
