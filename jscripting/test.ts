
const colors = [
    Format.BLACK,
    Format.DARK_BLUE,
    Format.DARK_GREEN,
    Format.DARK_AQUA,
    Format.DARK_RED,
    Format.DARK_PURPLE,
    Format.GOLD,
    Format.GRAY,
    Format.DARK_GRAY,
    Format.BLUE,
    Format.GREEN,
    Format.AQUA,
    Format.RED,
    Format.LIGHT_PURPLE,
    Format.YELLOW,
    Format.WHITE,
    // Format.OBFUSCATED,
    // Format.BOLD,
    // Format.STRIKETHROUGH,
    // Format.UNDERLINE,
    // Format.ITALIC,
    // Format.RESET,
];

export function processCommand(args: fe.CommandArgs) {
    args.confirm('Hello JavaScript! Hello ' + args.sender.getName() + '!');

    function print(message: string) {
        var sOut = '';
        for (let i = 0; i < message.length; i++)
            sOut += colors[Math.floor(Math.random() * colors.length)] + message[i];
        args.confirm(sOut);
    }

    print('Hello colors!');

    // for (let i = 0; i < colors.length; i++)
    //     args.confirm(colors[i] + 'TEST');

    if (args.player) {
        args.confirm('Running script as player');
        args.confirm('Your health is ' + args.player.getHealth());
    } else {
        args.confirm('Running script as server');
    }

    args.confirm('uptime = ' + Server.getUptime());
    args.confirm('timePlayed = ' + FEServer.getTimePlayed(args.player.getUuid()));
    args.confirm('lastLogin = ' + FEServer.getLastLogin(args.player.getUuid()));
    args.confirm('lastLogout = ' + FEServer.getLastLogout(args.player.getUuid()));

    fe.Permissions.registerPermission('script.test', PermissionLevel.TRUE, 'script test!');

    var point: fe.Point = new fe.Point(1, 2, 3);
    args.confirm(`new fe.Point(1, 2, 3) = ${point.getX()}, ${point.getY()}, ${point.getZ()}`);
    args.confirm(`instanceof fe.WorldPoint = ${point instanceof fe.WorldPoint ? 'true' : 'false'}`);

    args.confirm(``);
    var point2: fe.WorldPoint = new fe.WorldPoint(0, 4, 5, 6);
    args.confirm(`new fe.WorldPoint(0, 4, 5, 6) = ${point2.getX()}, ${point2.getY()}, ${point2.getZ()}, dim = ${point2.getDimension()}`);
    args.confirm(`instanceof fe.WorldPoint = ${point2 instanceof fe.WorldPoint ? 'true' : 'false'}`);
    args.confirm(``);

    args.confirm('Arguments: ' + args.toString());
    var i = 0;
    while (!args.isEmpty()) {
        i++;
        args.confirm('  ' + i + ': ' + args.remove());
    }

    if (args.player) {
        var nbt = getNbt(args.player);
        nbt[NBT_INT + 'jscript_counter'] = (nbt[NBT_INT + 'jscript_counter'] || 0) + 1;
        args.confirm('NBT data of player: ' + JSON.stringify(nbt));
        setNbt(args.player, nbt);
    }

    var counter = +LocalStorage.getItem('test_counter') || 0;
    counter++;
    args.confirm('Counter at ' + counter);
    LocalStorage.setItem('test_counter', counter);

    // var doAsHideChat = args.doAs(sender, sender, true);
    // args.cmd(doAsHideChat, 'give', args.sender.getCommandSenderName(), 'minecraft:dirt', '1');

    // setTimeout(() => {
    //     args.notify('Timeout 3 (6s)');
    // }, 6000);
    //
    // var t1 = setTimeout(() => {
    //     args.notify('Timeout 2 (4s)');
    // }, 4000);
    //
    // setTimeout(() => {
    //     args.notify('Timeout 1 (2s) -> clear timeout 2');
    //     clearTimeout(t1);
    // }, 2000);
}

FEServer.registerCommand({
    name: 'scripttest',
    usage: '/scripttest',
    opOnly: false,
    processCommand: processCommand,
    tabComplete: processCommand,
});

Server.registerEvent('PlayerInteractEvent', (event: mc.event.entity.player.PlayerInteractEvent) => {
    // Server.chatConfirm('player interact event from ' + event.getPlayer().getName());
});

// function isInt(value: any): boolean {
//     return !isNaN(value) && parseInt(value) === value && !isNaN(parseInt(value, 10));
// }

// function nbt2js(nbt: any): any {
//     for (let field in Object.keys(nbt)) {
//         let value: any = nbt[field];
//         let fieldParts = field.split(':');
//         let type = fieldParts[0];
//         let key = fieldParts[1];
//         switch (type) {
//             case NBT_STRING:
//                 delete nbt[field];
//                 nbt[key] = value;
//                 break;
//             case NBT_COMPOUND:
//                 delete nbt[field];
//                 nbt[key] = nbt2js(value);
//                 break;
//             default:
//                 break;
//         }
//     }
// }
//
// function js2nbt(nbt: any): any {
//     for (let field in Object.keys(nbt)) {
//         let value: any = nbt[field];
//         let fieldParts = field.split(':');
//         if (fieldParts.length !== 2) {
//             delete nbt[field];
//             if (typeof value === 'object') {
//                 nbt[NBT_COMPOUND + ':' + field] = js2nbt(value);
//             } else if (typeof value === 'string') {
//                 nbt[NBT_STRING + ':' + field] = js2nbt(value);
//             } else {
//                 // TODO: Error
//             }
//         }
//     }
// }
