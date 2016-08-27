mc.confirm(sender, 'Hello JavaScript! Hello ' + sender.getName() + '!');

mc.confirm(sender, 'sin(3) = ' + Math.sin(3));

if (player) {
    mc.confirm(sender, 'Running script as player');
    mc.confirm(sender, 'Your health is ' + player.getHealth());
} else {
    mc.confirm(sender, 'Running script as server');
}

mc.confirm(sender, 'Arguments: ' + args.toString());
var i = 0;
while (!args.isEmpty()) {
    i++;
    mc.confirm(sender, '  ' + i + ': ' + args.remove());
}

// var doAsHideChat = mc.doAs(sender, sender, true);
// mc.cmd(doAsHideChat, 'give', sender.getCommandSenderName(), 'minecraft:dirt', '1');
