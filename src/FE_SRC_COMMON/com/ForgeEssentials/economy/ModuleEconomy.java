package com.ForgeEssentials.economy;

import java.io.File;
import java.util.HashMap;

import com.ForgeEssentials.api.modules.FEModule;
import com.ForgeEssentials.api.modules.event.FEModuleInitEvent;
import com.ForgeEssentials.api.modules.event.FEModuleServerInitEvent;
import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.economy.commands.CommandAddToWallet;
import com.ForgeEssentials.economy.commands.CommandGetWallet;
import com.ForgeEssentials.economy.commands.CommandMoney;
import com.ForgeEssentials.economy.commands.CommandPaidCommand;
import com.ForgeEssentials.economy.commands.CommandPay;
import com.ForgeEssentials.economy.commands.CommandRemoveWallet;
import com.ForgeEssentials.economy.commands.CommandSellCommand;
import com.ForgeEssentials.economy.commands.CommandSetWallet;

import cpw.mods.fml.common.registry.GameRegistry;

/**
 * Call the WalletHandler class when working with Economy
 */
@FEModule(name = "Economy", parentMod = ForgeEssentials.class, configClass = ConfigEconomy.class)
public class ModuleEconomy
{
	@FEModule.Config
	public static ConfigEconomy	config;

	@FEModule.ModuleDir
	public static File			moduleDir;
	
	private static HashMap<String, ModuleEconomy>	playerEconomyMap	= new HashMap<String, ModuleEconomy>();

	public static int			startbuget;
	
	@FEModule.Init
	public void load(FEModuleInitEvent e)
	{
		GameRegistry.registerPlayerTracker(new WalletHandler());
	}

	@FEModule.ServerInit
	public void serverStarting(FEModuleServerInitEvent e)
	{
		e.registerServerCommand(new CommandAddToWallet());
		e.registerServerCommand(new CommandRemoveWallet());
		e.registerServerCommand(new CommandGetWallet());
		e.registerServerCommand(new CommandSetWallet());
		e.registerServerCommand(new CommandPay());
		e.registerServerCommand(new CommandPaidCommand());
		e.registerServerCommand(new CommandSellCommand());
		e.registerServerCommand(new CommandMoney());
	}
}
