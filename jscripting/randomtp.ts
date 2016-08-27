
function main() {
    if (args.isEmpty()) {
        sender.chatConfirm('/jscript randomtp range [x z]: Teleport to some random location');
        return;
    }

    var r: int = args.parseInt();
    var x: int;
    var z: int;
    if (!args.isEmpty()) {
        x = args.parseInt();
        z = args.parseInt();
    } else if (player) {
        x = player.getX();
        z = player.getZ();
    } else {
        sender.chatConfirm('Error: no player!');
        return;
    }

    if (args.isTabCompletion)
        return;

    var hiddenChatSender = sender.doAs(sender.getName(), false);
    Server.runCommand(hiddenChatSender, 'spreadplayers', x, z, 0, r, false, sender.getName());
}

main();
