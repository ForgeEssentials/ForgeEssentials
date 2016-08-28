
export function processCommand(event: MC.CommandEvent) {
    Server.chatConfirm('Hello JavaScript! Hello ' + sender.getName() + '!');

    Server.chatConfirm('sin(3) = ' + Math.sin(3));

    if (player) {
        Server.chatConfirm('Running script as player');
        Server.chatConfirm('Your health is ' + player.getHealth());
    } else {
        Server.chatConfirm('Running script as server');
    }

    Server.chatConfirm('Arguments: ' + args.toString());
    var i = 0;
    while (!args.isEmpty()) {
        i++;
        Server.chatConfirm('  ' + i + ': ' + args.remove());
    }

    // var doAsHideChat = Server.doAs(sender, sender, true);
    // Server.cmd(doAsHideChat, 'give', sender.getCommandSenderName(), 'minecraft:dirt', '1');
}
