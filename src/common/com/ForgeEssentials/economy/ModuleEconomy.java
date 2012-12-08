package com.ForgeEssentials.economy;

import java.util.HashMap;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;

import com.ForgeEssentials.core.IFEModule;
import com.ForgeEssentials.economy.commands.CommandAddToWallet;
import com.ForgeEssentials.economy.commands.CommandGetWallet;
import com.ForgeEssentials.economy.commands.CommandRemoveWallet;
import com.ForgeEssentials.economy.commands.CommandSetWallet;
import com.ForgeEssentials.permission.ForgeEssentialsPermissionRegistrationEvent;

import cpw.mods.fml.common.IPlayerTracker;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.registry.GameRegistry;

/**
 * Call the Wallet class when working with Economy
 */
public class ModuleEconomy implements IFEModule, IPlayerTracker
{
	private static HashMap<String, ModuleEconomy> playerEconomyMap = new HashMap<String, ModuleEconomy>();

	/**
	 * Returns the player's economy instance
	 * @param player target player
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
	 * @param username target's username
	 * @return the player's economy instance
	 */
	public static ModuleEconomy getPlayerInfo(String username)
	{
		ModuleEconomy info = playerEconomyMap.get(username);
		
		return info;
	}

	public int wallet;

	private ModuleEconomy(EntityPlayer player) {}
	
	public ModuleEconomy() {}
	
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

	@Override
	public void preLoad(FMLPreInitializationEvent e) 
	{
		MinecraftForge.EVENT_BUS.register(this);		
	}

	@Override
	public void load(FMLInitializationEvent e) 
	{
		GameRegistry.registerPlayerTracker(this);
	}

	@Override
	public void postLoad(FMLPostInitializationEvent e) {}

	@Override
	public void serverStarting(FMLServerStartingEvent e) 
	{
		e.registerServerCommand(new CommandAddToWallet());
		e.registerServerCommand(new CommandRemoveWallet());
		e.registerServerCommand(new CommandGetWallet());
		e.registerServerCommand(new CommandSetWallet());		
	}

	@Override
	public void serverStarted(FMLServerStartedEvent e) {}

	@Override
	public void serverStopping(FMLServerStoppingEvent e) {}
	
	@ForgeSubscribe
	public void registerPermissions(ForgeEssentialsPermissionRegistrationEvent event)
	{
		event.registerPermissionDefault("ForgeEssentials.Economy.walletremove", true);
		event.registerPermissionDefault("ForgeEssentials.Economy.walletget", true);
		event.registerPermissionDefault("ForgeEssentials.Economy.walletadd", true);
		event.registerPermissionDefault("ForgeEssentials.Economy.walletset", true);
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
