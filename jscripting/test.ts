
export function processCommand(args: mc.CommandArgs) {
    args.confirm('Hello JavaScript! Hello ' + args.sender.getName() + '!');

    args.confirm('sin(3) = ' + Math.sin(3));

    if (args.player) {
        args.confirm('Running script as player');
        args.confirm('Your health is ' + args.player.getHealth());
    } else {
        args.confirm('Running script as server');
    }

    Permissions.registerPermission('script.test', PermissionLevel.TRUE, 'script test!');

    args.confirm(``);
    var point: mc.world.Point = new mc.world.Point(1, 2, 3);
    args.confirm(`new mc.world.Point(1, 2, 3) = ${point.getX()}, ${point.getY()}, ${point.getZ()}`);
    args.confirm(`instanceof mc.world.Point = ${point instanceof mc.world.Point ? 'true' : 'false'}`);
    args.confirm(`instanceof mc.world.WorldPoint = ${point instanceof mc.world.WorldPoint ? 'true' : 'false'}`);

    args.confirm(``);
    var point2: mc.world.WorldPoint = new mc.world.WorldPoint(0, 4, 5, 6);
    args.confirm(`new mc.world.WorldPoint(0, 4, 5, 6) = ${point2.getX()}, ${point2.getY()}, ${point2.getZ()}, dim = ${point2.getDimension()}`);
    args.confirm(`instanceof mc.world.Point = ${point2 instanceof mc.world.Point ? 'true' : 'false'}`);
    args.confirm(`instanceof mc.world.WorldPoint = ${point2 instanceof mc.world.WorldPoint ? 'true' : 'false'}`);
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
        args.confirm('NBT data of player:');
        args.confirm(JSON.stringify(nbt));
        setNbt(args.player, nbt);
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

Server.registerCommand({
    name: 'scripttest',
    usage: '/scripttest',
    opOnly: false,
    processCommand: processCommand,
    tabComplete: processCommand,
});

Server.registerEvent('PlayerInteractEvent', (event: mc.event.PlayerInteractEvent) => {
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
