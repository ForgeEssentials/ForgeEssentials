
mc.confirm(player, 'sin(3) = ' + Math.sin(3));

for (var i = 1; i < 3; i++) {
    mc.confirm(player, 'Hello ' + player.getCommandSenderName() + '! #' + i);
}

var doAsHideChat = mc.doAs(player, player, true);
mc.cmd(doAsHideChat, 'give', player.getCommandSenderName(), 'minecraft:dirt', '1');

// mc.cmd(player, 'help');
