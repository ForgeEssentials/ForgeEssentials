
var timer: number = 0;

export function sheepRandomCommand(args: fe.CommandArgs) {
    if (args.isEmpty()) {
        if (!args.isTabCompletion) // This is important so TAB completion does not actually change stuff
            sheepRandom(args.player);
        return;
    }

    args.tabComplete('on', 'off', 'summon');
    var arg = args.remove().toLowerCase();

    var interval: number = 200;
    if (!args.isEmpty()) {
        interval = args.parseInt();
        if (interval < 1)
            interval = 1;
    }

    if (args.isTabCompletion) // This is important so TAB completion does not actually change stuff
        return;

    var newState: boolean;
    switch (arg) {
        case 'on':
            newState = true;
            break;
        case 'off':
            newState = false;
            break;
        case 'summon':
            var sender = args.sender.doAs(args.player, true);
            for (let i = 0; i < interval; i++) {
                Server.runCommand(sender, 'summon', 'Sheep');
            }
            return;
    }

    if (timer && newState) {
        return args.warn('sheeprandom already turned on');
    } else if (!timer && !newState) {
        return args.warn('sheeprandom already turned off');
    }

    if (newState) {
        timer = setInterval(() => sheepRandom(args.player), interval);
        return args.confirm('sheeprandom turned on');
    } else {
        clearInterval(timer);
        timer = 0;
        return args.confirm('sheeprandom turned off');
    }
}

export function sheepRandom(player: mc.entity.EntityPlayer) {
    var r = 40;
    var aabb = new mc.util.AxisAlignedBB(
        player.getX() - r, player.getY() - r, player.getZ() - r,
        player.getX() + r, player.getY() + r, player.getZ() + r
    );
    var entities = player.getWorld().getEntitiesWithinAABB(aabb);
    entities.forEach((entity: mc.entity.EntitySheep) => {
        if (entity instanceof mc.entity.EntitySheep) {
            entity.setFleeceColor(Math.round(Math.random() * 15));
        }
    });
}

// Server.registerEvent('PlayerInteractEvent', (event: mc.event.entity.player.PlayerInteractEvent) => {
//     sheepRandom(event.getPlayer());
// });

Server.registerEvent('AttackEntityEvent', (event: mc.event.entity.player.PlayerInteractEvent) => {
    sheepRandom(event.getPlayer());
});

FEServer.registerCommand({
    name: 'sheeprandom',
    usage: '/sheeprandom [on|off|summon]',
    permission: 'fe.commands.sheeprandom',
    opOnly: false,
    processCommand: sheepRandomCommand,
    tabComplete: sheepRandomCommand,
});
