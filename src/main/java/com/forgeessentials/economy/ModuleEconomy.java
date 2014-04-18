package com.forgeessentials.economy;

import java.io.File;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.economy.commands.CommandAddToWallet;
import com.forgeessentials.economy.commands.CommandGetWallet;
import com.forgeessentials.economy.commands.CommandMoney;
import com.forgeessentials.economy.commands.CommandPaidCommand;
import com.forgeessentials.economy.commands.CommandPay;
import com.forgeessentials.economy.commands.CommandRemoveWallet;
import com.forgeessentials.economy.commands.CommandSellCommand;
import com.forgeessentials.economy.commands.CommandSetWallet;
import com.forgeessentials.util.events.modules.FEModuleInitEvent;
import com.forgeessentials.util.events.modules.FEModuleServerInitEvent;

import cpw.mods.fml.common.IPlayerTracker;
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

	public static int			startbuget;

	@FEModule.Init
	public void load(FEModuleInitEvent e)
	{
		APIRegistry.wallet = new WalletHandler();
		GameRegistry.registerPlayerTracker((IPlayerTracker) APIRegistry.wallet);
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
