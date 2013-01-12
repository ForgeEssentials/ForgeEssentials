package com.ForgeEssentials.economy;

import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;

import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.core.moduleLauncher.FEModule;
import com.ForgeEssentials.core.moduleLauncher.FEModule.Init;
import com.ForgeEssentials.core.moduleLauncher.FEModule.PreInit;
import com.ForgeEssentials.core.moduleLauncher.FEModule.ServerInit;
import com.ForgeEssentials.core.moduleLauncher.event.FEModuleInitEvent;
import com.ForgeEssentials.core.moduleLauncher.event.FEModulePreInitEvent;
import com.ForgeEssentials.core.moduleLauncher.event.FEModuleServerInitEvent;
import com.ForgeEssentials.economy.commands.CommandAddToWallet;
import com.ForgeEssentials.economy.commands.CommandGetWallet;
import com.ForgeEssentials.economy.commands.CommandRemoveWallet;
import com.ForgeEssentials.economy.commands.CommandSetWallet;
import com.ForgeEssentials.permission.PermissionRegistrationEvent;

import cpw.mods.fml.common.IPlayerTracker;
import cpw.mods.fml.common.registry.GameRegistry;

/**
 * Call the Wallet class when working with Economy
 */
@FEModule(name = "Economy", parentMod = ForgeEssentials.class)
public class ModuleEconomy implements IPlayerTracker
{
	private static HashMap<String, ModuleEconomy> playerEconomyMap = new HashMap<String, ModuleEconomy>();

	/**
	 * Returns the player's economy instance
	 * 
	 * @param player
	 *            target player
	 * @return the player's economy instance
	 */
	public static ModuleEconomy getPlayerInfo(EntityPlayer player)
	{
		ModuleEconomy info = playerEconomyMap.get(player.username);

		if (info == null)
		{
			info = new ModuleEconomy(player);

			playerEconomyMap.put(player.username, info);
		}

		return info;
	}

	/**
	 * Returns the player's economy instance
	 * 
	 * @param username
	 *            target's username
	 * @return the player's economy instance
	 */
	public static ModuleEconomy getPlayerInfo(String username)
	{
		ModuleEconomy info = playerEconomyMap.get(username);

		return info;
	}

	public int wallet;

	private ModuleEconomy(EntityPlayer player)
	{
	}

	public ModuleEconomy()
	{
	}

	public static void saveData(EntityPlayer player)
	{
		NBTTagCompound economyNBT = player.getEntityData();
		economyNBT.setInteger("Economy-" + player.username, ModuleEconomy.getPlayerInfo(player).wallet);

	}

	public static void loadData(EntityPlayer player)
	{
		NBTTagCompound economyNBT = player.getEntityData();
		ModuleEconomy.getPlayerInfo(player).wallet = economyNBT.getInteger("Economy-" + player.username);
		Wallet.doesWalletExist(player);
	}

	@PreInit
	public void preLoad(FEModulePreInitEvent e)
	{
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Init
	public void load(FEModuleInitEvent e)
	{
		GameRegistry.registerPlayerTracker(this);
	}

	@ServerInit
	public void serverStarting(FEModuleServerInitEvent e)
	{
		e.registerServerCommand(new CommandAddToWallet());
		e.registerServerCommand(new CommandRemoveWallet());
		e.registerServerCommand(new CommandGetWallet());
		e.registerServerCommand(new CommandSetWallet());
	}

	@ForgeSubscribe
	public void registerPermissions(PermissionRegistrationEvent event)
	{

	}

	@Override
	public void onPlayerLogin(EntityPlayer player)
	{
		loadData(player);
	}

	@Override
	public void onPlayerLogout(EntityPlayer player)
	{
		saveData(player);
	}

	@Override
	public void onPlayerChangedDimension(EntityPlayer player)
	{
		saveData(player);
	}

	@Override
	public void onPlayerRespawn(EntityPlayer player)
	{
		loadData(player);
	}
}
