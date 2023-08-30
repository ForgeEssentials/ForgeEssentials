package com.forgeessentials.multiworld.command;



	@Override
	public void parse(CommandParserArgs arguments) throws CommandException {
		switch (subCmd) {
			case "gamerule" :
				parseGamerule(arguments);
				break;
		}
	}
	// Print lists of multiworlds, available providers and available world-types

	public static void parseList(CommandParserArgs arguments) throws CommandException {
			case "providers" :
				arguments.confirm("Available world providers:");
				for (String provider : ModuleMultiworld.getMultiworldManager()
						.getWorldProviders().keySet()) {
					arguments.confirm("  " + provider);
				}
				break;
		}
	}

	public static void parseGamerule(CommandParserArgs arguments) throws CommandException { arguments.checkPermission(ModuleMultiworld.PERM_MANAGE); Multiworld world =
      parseWorld(arguments);
      
      GameRules rules = world.getWorldServer().getGameRules(); arguments.tabComplete(rules.getRules());
      
      if (arguments.isEmpty()) { // Check all gamerules if (!arguments.isTabCompletion) { arguments.confirm("Game rules for %s:", world.getName()); for (String rule :
      rules.getRules()) arguments.confirm(rule + " = " + rules.getString(rule)); } return; }

	String rule = arguments.remove();if(!rules.hasRule(rule))throw new CommandException("commands.gamerule.norule",rule);

	if(arguments.isEmpty())
	{ // Check gamerule arguments.confirm(rule + " = " + rules.getString(rule));
		// return; }

		// Set gamerule if (arguments.isTabCompletion) return; String value =
		// arguments.remove(); rules.setOrCreateGameRule(rule, value);
		arguments.confirm("Set gamerule %s = %s for world %s", rule, value,
				world.getName());
	}
}
