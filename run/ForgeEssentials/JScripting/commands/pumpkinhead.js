"use strict";
function tabComplete(args) {
    processCommand(args);
}
exports.tabComplete = tabComplete;
function processCommand(args) {
    var player = args.player;
    if (!args.player) {
        args.confirm('Error: no player!');
        return;
    }
    if (args.isTabCompletion)
        return;
    player.getInventory().setStackInSlot(39, Factory.createItemStack(Block.getBlock("minecraft:pumpkin"), 1));
}
exports.processCommand = processCommand;
