export function pardonTempBanCommand(args: fe.CommandArgs) {
    if (args.isEmpty()) {
        args.confirm('/pardontempban [player]');
        return;
    }

    var player: UserIdent = args.parsePlayer(true,false);

    if (args.isTabCompletion) // This is important so TAB completion does not actually change stuff
        return;
	var info: PlayerInfo = player.getPlayerInfo();
	info.removeTimeout('tempban');
}

FEServer.registerCommand({
    name: 'pardontempban',
    usage: '/pardontempban [player]',
    permission: 'fe.commands.pardontempban',
    opOnly: true,
    processCommand: pardonTempBanCommand,
    tabComplete: pardonTempBanCommand,
});