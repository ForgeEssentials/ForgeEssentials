
export function processCommand(event: MC.CommandEvent) {
    if (event.isEmpty()) {
        event.sender.chatConfirm('/jscript randomtp range [x z]: Teleport to some random location');
        return;
    }

    var r: int = event.parseInt();
    var x: int;
    var z: int;
    if (!event.isEmpty()) {
        x = event.parseInt();
        z = event.parseInt();
    } else if (event.player) {
        x = event.player.getX();
        z = event.player.getZ();
    } else {
        event.sender.chatConfirm('Error: no player!');
        return;
    }

    if (event.isTabCompletion)
        return;

    var hiddenChatSender = event.sender.doAs(null, true);
    Server.runCommand(hiddenChatSender, 'spreadplayers', x, z, 0, r, false, event.sender.getName());
}
