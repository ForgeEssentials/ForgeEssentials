package com.forgeessentials.economy;

import java.io.File;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.APIRegistry.ForgeEssentialsRegistrar.PermRegister;
import com.forgeessentials.api.permissions.IPermRegisterEvent;
import com.forgeessentials.api.permissions.RegGroup;
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

	@PermRegister
	public static void registerPerms(IPermRegisterEvent event)
	{
		event.registerPermissionLevel("ForgeEssentials.Economy.pay", RegGroup.MEMBERS);
		event.registerPermissionLevel("ForgeEssentials.Economy.requestpayment", RegGroup.MEMBERS);
		event.registerPermissionLevel("ForgeEssentials.Economy.money", RegGroup.MEMBERS);

		event.registerPermissionLevel("ForgeEssentials.Economy.getwallet", RegGroup.OWNERS);
		event.registerPermissionLevel("ForgeEssentials.Economy.paidcommand", RegGroup.OWNERS);
		event.registerPermissionLevel("ForgeEssentials.Economy.addtowallet", RegGroup.OWNERS);
		event.registerPermissionLevel("ForgeEssentials.Economy.sellcommand", RegGroup.OWNERS);
		event.registerPermissionLevel("ForgeEssentials.Economy.removewallet", RegGroup.OWNERS);
		event.registerPermissionLevel("ForgeEssentials.Economy.setwallet", RegGroup.OWNERS);
	}
}
