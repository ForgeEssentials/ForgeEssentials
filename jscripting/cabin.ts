/**
 * Builds a small cute cabin around the player
 */

// Using these blocks to build the cabin
const blockTypes = {
    s: Block.get('minecraft:stone'),
    g: Block.get('minecraft:grass'),
    f: Block.get('minecraft:fence'),
    w: Block.get('minecraft:planks'),
    d: Block.get('minecraft:wooden_door'),
    D: Block.get('minecraft:wooden_door'),
    b: Block.get('minecraft:bed'),
    B: Block.get('minecraft:bed'),
    l: Block.get('minecraft:glass'),
    p: Block.get('minecraft:wooden_slab'),
    c: Block.get('minecraft:chest'),
    o: Block.get('minecraft:obsidian')
};

const facing_east = 0;
const facing_south = 1;
const facing_west = 2;
const facing_north = 3;
const bed_empty = 0;
const bed_occupied = 4;
const bed_head = 8;
const bed_foot = 0;
const door_top = 8;
const door_bottom = 0;

// Using these layers to build the cabin
const layer = [
    [
        'ssssssssss',
        'ssssssssss',
        'ssssssssss',
        'ssssssssss',
        'ssssssssss',
        'ssssssssss',
        'ssssssssss',
        'ssssssssss',
        'ssssssssss'
    ],
    [
        'gggggggggg',
        'gggggggggg',
        'gggggggggg',
        'gssssssssg',
        'gswwwwwwsg',
        'gswwwwwwsg',
        'gswwwwwwsg',
        'gssssssssg',
        'gggggggggg'
    ],
    [
        //   W
        //  S  N
        //   E
        '          ',
        ' fff ffff ',
        ' f      f ',
        ' wwwdwwww ',
        ' w     cw ',
        ' w     cw ',
        ' w Bb bBw ',
        ' wwwwwwww ',
        '          '
    ],
    [
        '          ',
        '          ',
        '          ',
        ' wlwDwllw ',
        ' w      w ',
        ' l      l ',
        ' w      w ',
        ' wwwwwwww '


    ],
    [
        '          ',
        '          ',
        '          ',
        ' wwwwwwww ',
        ' w      w ',
        ' w      w ',
        ' w      w ',
        ' wwwwwwww '
    ],
    [
        '          ',
        '          ',
        '          ',
        '  p    p  ',
        ' pwwwwwwp ',
        '  wwwwww  ',
        ' pwwwwwwp ',
        '  p    p  '
    ]];



function buildcabin(args: fe.CommandArgs) {
    if (args.isTabCompletion) // This is important so TAB completion does not actually change stuff
        return;

    let arga = args.toArray();
    if (arga.length > 1 && arga[0] === 'here')
        Server.chat('!');

    //    if (!args.player.isOnGround())
    //       return;

    // Get the location where to build
    let placex = args.player.getX() - 6;
    let placey = args.player.getY() - 2;
    let placez = args.player.getZ() + 4;
    let world = args.player.getWorld();
    //    var chest:mc.World.Block = null;
    let chest = new mc.world.Block();

    // place basic blocks first
    for (let iy = 0; iy < layer.length; iy++) {
        for (let ix = 0; ix < layer[iy].length; ix++) {
            for (let iz = 0; iz < layer[iy][ix].length; iz++) {
                let blockType = blockTypes[layer[iy][ix][iz]];
                if (!blockType)
                    continue;
                switch (layer[iy][ix][iz]) {
                    case 'b':
                    case 'B':
                    case 'd':
                    case 'D':
                        break;
                    case 'c':
                        world.setBlock(placex + ix, placey + iy, placez - iz, blockType);
                        chest = world.getBlock(placex + ix, placey + iy, placez - iz);
                        break;
                    default:
                        world.setBlock(placex + ix, placey + iy, placez - iz, blockType);
                        break;
                }
            } // iz
        } // ix
    } // iy
    // place dynamic blocks second
    for (let iy = 0; iy < layer.length; iy++) {
        for (let ix = 0; ix < layer[iy].length; ix++) {
            for (let iz = 0; iz < layer[iy][ix].length; iz++) {
                let blockType = blockTypes[layer[iy][ix][iz]];
                if (!blockType)
                    continue;
                switch (layer[iy][ix][iz]) {
                    case 'b':
                    case 'B':
                        // world.setBlock(placex + ix, placey + iy, placez - iz, blockType, facing_north|((layer[iy][ix][iz]=='B')?bed_head:0));
                        break;
                    case 'd':
                    case 'D':
                        world.setBlock(placex + ix, placey + iy, placez - iz, blockType, facing_east | ((layer[iy][ix][iz] === 'D') ? door_top : 0));
                        break;
                    default:
                        break;
                }
            } // iz
        } // ix
    } // iy

    world.setBlock(placex + 6, placey + 2, placez - 6, Block.get('minecraft:bed'), facing_north);
    world.setBlock(placex + 6, placey + 2, placez - 7, Block.get('minecraft:bed'), facing_north | bed_head);

    // Server.chat(chest);
    // var mychest: MC.Entity. = null;
    // let x = chest.isInstanceOf('Chest');
    Server.chat(chest.getName());

    // var chestinv = chest.getInventory();
    // chestinv.setStackInSlot(39, Item.createItemStack(Block.getBlock('minecraft:pumpkin'), 1));
    //    var gold = item.getItem('minecraft:gold_ore');

}

FEServer.registerCommand({
    name: 'buildcabin',
    usage: '/buildcabin here',
    permission: 'fe.commands.buildcabin',
    opOnly: false,
    processCommand: buildcabin,
    tabComplete: buildcabin,
});
