
export function randomTpCommand(args: fe.CommandArgs) {
    if (args.isEmpty()) {
        args.confirm('/jscript randomtp range [x z]: Teleport to some random location');
        return;
    }

    var player = args.player;
    var r: int = args.parseInt();
    var x: int;
    var z: int;
    if (!args.isEmpty()) {
        x = args.parseInt();
        z = args.parseInt();
    } else if (args.player) {
        x = args.player.getX();
        z = args.player.getZ();
    } else {
        args.confirm('Error: no player!');
        return;
    }

    if (!args.isEmpty()) {
        player = args.parsePlayer(true, true).getPlayer();
    }

    if (args.isTabCompletion) // This is important so TAB completion does not actually change stuff
        return;

    var hiddenChatSender = args.sender.doAs(null, true);
    Server.runCommand(hiddenChatSender, 'spreadplayers', x, z, 0, r, false, args.sender.getName());
}

FEServer.registerCommand({
    name: 'randomtp',
    usage: '/randomtp range [x z] [player]',
    permission: 'fe.teleport.randomtp',
    opOnly: false,
    processCommand: randomTpCommand,
    tabComplete: randomTpCommand,
});
