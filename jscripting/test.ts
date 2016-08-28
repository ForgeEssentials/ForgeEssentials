
export function processCommand(args: MC.CommandArgs) {
    args.confirm('Hello JavaScript! Hello ' + args.sender.getName() + '!');

    args.confirm('sin(3) = ' + Math.sin(3));

    if (args.player) {
        args.confirm('Running script as player');
        args.confirm('Your health is ' + args.player.getHealth());
    } else {
        args.confirm('Running script as server');
    }

    args.confirm('Arguments: ' + args.toString());
    var i = 0;
    while (!args.isEmpty()) {
        i++;
        args.confirm('  ' + i + ': ' + args.remove());
    }

    // var doAsHideChat = args.doAs(sender, sender, true);
    // args.cmd(doAsHideChat, 'give', args.sender.getCommandSenderName(), 'minecraft:dirt', '1');

    setTimeout(() => {
        args.notify('Timeout 3 (6s)');
    }, 6000);

    var t1 = setTimeout(() => {
        args.notify('Timeout 2 (4s)');
    }, 4000);

    setTimeout(() => {
        args.notify('Timeout 1 (2s) -> clear timeout 2');
        clearTimeout(t1);
    }, 2000);
}

export function onInteract_left(sender: MC.ICommandSender) {
    sender.chatConfirm('interact_left');
}

export function onInteract_right(sender: MC.ICommandSender) {
    sender.chatConfirm('interact_right');
}
