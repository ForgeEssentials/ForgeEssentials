
export function sheepRandomCommand(args: mc.CommandArgs) {
    if (args.isTabCompletion) // This is important so TAB completion does not actually change stuff
        return;
    sheepRandom(args.player);
}

export function sheepRandom(player: mc.entity.EntityPlayer) {
    var r = 60;
    var aabb = createAxisAlignedBB(
        player.getX() - r, player.getY() - r, player.getZ() - r,
        player.getX() + r, player.getY() + r, player.getZ() + r
    );
    var list = player.getWorld().getEntitiesWithinAABB(aabb);
    var listArray = list.toArray();
    for (var i = 0; i < listArray.length; i++) {
        if (listArray[i].getEntityType() === 'EntitySheep') {
            listArray[i].setFleeceColor(Math.round(Math.random() * 15));
        }
    }
}

Server.registerEvent('PlayerInteractEvent', (event: mc.event.PlayerInteractEvent) => {
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
