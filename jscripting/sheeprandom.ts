
export function sheepRandomCommand(args: MC.CommandArgs) {
    var player = args.player;

    if (args.isTabCompletion) // This is important so TAB completion does not actually change stuff
        return;

    var list = player.getWorld().getEntitiesWithinAABB(Factory.createAxisAlignedBB(player.getX() - 10, player.getY() - 10, player.getZ() - 10, player.getX() + 10, player.getY() + 10, player.getZ() + 10));
    var listArray = list.toArray();
    for (var i = 0; i < listArray.length; i++) {
        if (listArray[i].getEntityType() == "EntitySheep") {
            listArray[i].setFleeceColor(Math.round(Math.random() * 15));
        }
    }
}

Server.registerCommand({
    name: 'sheeprandom',
    usage: '/sheeprandom',
    permission: 'fe.teleport.sheeprandom',
    opOnly: false,
    processCommand: sheepRandomCommand,
    tabComplete: sheepRandomCommand,
});
