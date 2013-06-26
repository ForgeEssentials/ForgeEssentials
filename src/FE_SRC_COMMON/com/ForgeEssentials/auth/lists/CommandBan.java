package com.ForgeEssentials.auth.lists;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.BanEntry;

import com.ForgeEssentials.api.APIRegistry;
import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;

public class CommandBan extends ForgeEssentialsCommandBase{

	public static boolean legacyban;
	
	@Override
	public String getCommandName() {
		return "ban";
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args) {
		if (APIRegistry.perms.checkPermAllowed(sender, getCommandPerm())){
		
			if (args.length >= 2  && args[0].equalsIgnoreCase("show") && args[1].equalsIgnoreCase("ips"))
	        {
				sender.sendChatToPlayer(sender.translateString("commands.banlist.ips", new Object[] {Integer.valueOf(MinecraftServer.getServer().getConfigurationManager().getBannedIPs().getBannedList().size())}));
				sender.sendChatToPlayer(joinNiceString(MinecraftServer.getServer().getConfigurationManager().getBannedIPs().getBannedList().keySet().toArray()));
	        }
	        else if (args.length >= 1 && args[0].equalsIgnoreCase("show")){
	        	sender.sendChatToPlayer(sender.translateString("commands.banlist.players", new Object[] {Integer.valueOf(MinecraftServer.getServer().getConfigurationManager().getBannedPlayers().getBannedList().size())}));
	        	sender.sendChatToPlayer(joinNiceString(MinecraftServer.getServer().getConfigurationManager().getBannedPlayers().getBannedList().keySet().toArray()));
	        }
	        else if (args.length >= 2 && args[0].equalsIgnoreCase("player")){
				APIRegistry.perms.setPlayerPermission(args[1], "ForgeEssentials.Auth.isBanned", true, "_GLOBAL_");
				EntityPlayerMP entityplayermp = MinecraftServer.getServer().getConfigurationManager().getPlayerForUsername(args[1]);
		           entityplayermp.playerNetServerHandler.kickPlayerFromServer(PlayerTracker.banned);
		           if (legacyban){
		           BanEntry be = new BanEntry(args[1]);
		           be.setBannedBy(sender.getCommandSenderName());
		           }
		           sender.sendChatToPlayer("Successfully banned " + args[1]); 
			}
			else if (args.length >= 1 && args[0].equalsIgnoreCase("help")){
				sender.sendChatToPlayer("Allows you to ban a player from the server. Note that banlist.txt may not have any effect. Use the old way if you need to ban IP addresses.");
			}
			else if (args.length >= 2 && args[0].equalsIgnoreCase("pardon")){
				APIRegistry.perms.setPlayerPermission(args[1], "ForgeEssentials.Auth.isBanned", false, "_GLOBAL_");
				if (legacyban){
				MinecraftServer.getServer().getConfigurationManager().getBannedPlayers().remove(args[1]);
				}
			}
		}
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args) {
			if (args.length >= 2  && args[0].equalsIgnoreCase("show") && args[1].equalsIgnoreCase("ips"))
	        {
				sender.sendChatToPlayer(sender.translateString("commands.banlist.ips", new Object[] {Integer.valueOf(MinecraftServer.getServer().getConfigurationManager().getBannedIPs().getBannedList().size())}));
				sender.sendChatToPlayer(joinNiceString(MinecraftServer.getServer().getConfigurationManager().getBannedIPs().getBannedList().keySet().toArray()));
	        }
	        else if (args.length >= 1 && args[0].equalsIgnoreCase("show")){
	        	sender.sendChatToPlayer(sender.translateString("commands.banlist.players", new Object[] {Integer.valueOf(MinecraftServer.getServer().getConfigurationManager().getBannedPlayers().getBannedList().size())}));
	        	sender.sendChatToPlayer(joinNiceString(MinecraftServer.getServer().getConfigurationManager().getBannedPlayers().getBannedList().keySet().toArray()));
	        }
			else if (args.length >= 2 && args[0].equalsIgnoreCase("player")){
				APIRegistry.perms.setPlayerPermission(args[1], "ForgeEssentials.Auth.isBanned", true, "_GLOBAL_");
				EntityPlayerMP entityplayermp = MinecraftServer.getServer().getConfigurationManager().getPlayerForUsername(args[1]);
		           entityplayermp.playerNetServerHandler.kickPlayerFromServer(PlayerTracker.banned);
		           BanEntry be = new BanEntry(args[1]);
		           be.setBannedBy(sender.getCommandSenderName());
		           sender.sendChatToPlayer("Successfully banned " + args[1]); 
			}
			else if (args.length >= 1 && args[0].equalsIgnoreCase("help")){
				sender.sendChatToPlayer("Allows you to ban a player from the server. Note that banlist.txt will not have any effect. Use the old way if you need to ban IP addresses.");
			}
			else if (args.length >= 2 && args[0].equalsIgnoreCase("pardon")){
				APIRegistry.perms.setPlayerPermission(args[1], "ForgeEssentials.Auth.isBanned", false, "_GLOBAL_");
			}
		}
	

	@Override
	public boolean canConsoleUseCommand() {
		return true;
	}

	@Override
	public List<?> addTabCompletionOptions(ICommandSender sender, String[] args) {
		return null;
	}

	@Override
	public String getCommandPerm() {
		return "ForgeEssentials.Auth.ban";
	}

}
