package com.ForgeEssentials.auth;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import com.ForgeEssentials.api.permissions.PermissionsAPI;
import com.ForgeEssentials.api.permissions.query.PermQueryPlayer;
import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.core.commands.PermissionDeniedException;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;

import cpw.mods.fml.common.FMLCommonHandler;

public class CommandAuth extends ForgeEssentialsCommandBase
{
	private static String[]	playerCommands	= new String[] { "help", "login", "register", "changepass", "kick", "setpass", "unregister" };
	private static String[]	serverCommands	= new String[] { "help", "kick", "setpass", "unregister" };

	@Override
	public String getCommandName()
	{
		return "auth";
	}

	@Override
	public List<?> getCommandAliases()
	{
		ArrayList<String> list = new ArrayList<String>();
		list.add("AUTH");
		return list;
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		if (args.length == 0)
			throw new WrongUsageException("command.auth.usage");

		boolean hasAdmin = PermissionsAPI.checkPermAllowed(new PermQueryPlayer(sender, getCommandPerm() + ".admin"));

		// one arg? must be help.
		if (args.length == 1)
		{
			if (args[0].equalsIgnoreCase("help"))
			{
				sender.sendChatToPlayer(" - /auth register <password>  - forces the player to login again");
				sender.sendChatToPlayer(" - /auth login <player>  - forces the player to login again");
				sender.sendChatToPlayer(" - /auth changepass <oldpass> <newpass>  - sets the players password to the specified");

				if (!hasAdmin)
					return;

				sender.sendChatToPlayer(" - /auth kick <player>  - forces the player to login again");
				sender.sendChatToPlayer(" - /auth setpass <player> <password>  - sets the players password to the specified");
				sender.sendChatToPlayer(" - /auth unregister <player>  - forces the player to register again");
				return;
			}
			else
				throw new WrongUsageException("command.auth.usage");
		}

		// 2 args? seconds needs to be the player.
		if (args.length == 2)
		{
			// parse login
			if (args[0].equalsIgnoreCase("login"))
			{
				PlayerPassData data = PlayerPassData.getData(sender.username);
				if (data == null)
				{
					OutputHandler.chatError(sender, Localization.format("message.auth.error.notregisterred", sender.username));
					return;
				}

				String pass = ModuleAuth.encrypt(args[1]);

				// login worked
				if (data.password.equals(pass))
				{
					ModuleAuth.unLogged.remove(sender.username);
					OutputHandler.chatConfirmation(sender, Localization.get("command.auth.login.success"));
				}
				else
				{
					OutputHandler.chatError(sender, Localization.get("command.auth.login.fail"));
				}

				return;

			}
			// parse register
			else if (args[0].equalsIgnoreCase("register"))
			{
				if (PlayerPassData.getData(sender.username) != null)
				{
					OutputHandler.chatError(sender, Localization.format("command.auth.error.yesregisterred", sender.username));
					return;
				}				
				
				if (ModuleAuth.isEnabled() && !ModuleAuth.allowOfflineReg)
				{
					OutputHandler.chatError(sender, Localization.format("command.auth.error.disabledreg"));
					return;
				}

				String pass = ModuleAuth.encrypt(args[1]);
				PlayerPassData.registerData(new PlayerPassData(sender.username, pass));
				ModuleAuth.unRegistered.remove(sender.username);
				OutputHandler.chatConfirmation(sender, Localization.get("command.auth.register.success"));
				return;
			}
			
			// stop if unlogged.
			if (ModuleAuth.unLogged.contains(sender.username))
			{
				OutputHandler.chatError(sender, Localization.get("message.auth.needlogin"));
				return;
			}
			else if (ModuleAuth.unRegistered.contains(sender.username))
			{
				OutputHandler.chatError(sender, Localization.get("message.auth.needregister"));
				return;
			}

			// check for players.. all the rest of these should be greated than 1.
			String name = args[1];
			boolean isLogged = true;

			// check if the player is logged.
			EntityPlayerMP player = FunctionHelper.getPlayerForName(name);
			if (player == null)
			{
				OutputHandler.chatWarning(sender, "A player of that name is not on the server. Doing the action anyways.");
				isLogged = false;
			}

			// parse ./auth kick
			if (args[0].equalsIgnoreCase("kick"))
			{
				if (!hasAdmin)
					throw new PermissionDeniedException();
				else if (!isLogged)
					throw new PlayerNotFoundException();
				else
				{
					ModuleAuth.unLogged.add(name);
					OutputHandler.chatConfirmation(sender, Localization.format("command.auth.kick.user", name));
					OutputHandler.chatWarning(player, Localization.get("command.auth.kick.target"));
					return;
				}
			}
			// parse ./auth setpass
			else if (args[0].equalsIgnoreCase("setPass"))
			{
				if (!hasAdmin)
					throw new PermissionDeniedException();

				throw new WrongUsageException("command.auth.usage.setpass");
			}

			// parse ./auth unregister
			else if (args[0].equalsIgnoreCase("unregister"))
			{
				if (!hasAdmin)
					throw new PermissionDeniedException();

				if (PlayerPassData.getData(name) == null)
					throw new WrongUsageException("message.auth.error.notregisterred", name);

				PlayerPassData.deleteData(name);
				return;
			}

			// ERROR! :D
			else
				throw new WrongUsageException("command.auth.usage");
		}
		// 3 args? must be a command - player - pass
		else if (args.length == 3)
		{
			if (ModuleAuth.unLogged.contains(sender.username))
			{
				OutputHandler.chatError(sender, Localization.get("message.auth.needlogin"));
				return;
			}
			else if (ModuleAuth.unRegistered.contains(sender.username))
			{
				OutputHandler.chatError(sender, Localization.get("message.auth.needregister"));
				return;
			}

			// parse changePass
			if (args[0].equalsIgnoreCase("changepass"))
			{
				PlayerPassData data = PlayerPassData.getData(sender.username);
				String oldpass = ModuleAuth.encrypt(args[1]);
				String newPass = ModuleAuth.encrypt(args[2]);

				if (args[1].equals(args[2]))
				{
					OutputHandler.chatConfirmation(sender, Localization.get("command.auth.change.same"));
					return;
				}

				if (!data.password.equals(oldpass))
				{
					OutputHandler.chatConfirmation(sender, Localization.get("command.auth.change.wrongpass"));
					return;
				}

				data.password = newPass;
				OutputHandler.chatConfirmation(sender, Localization.get("command.auth.change.success"));
				return;

			}

			// check for players.. all the rest of these should be greated than 1.
			String name = args[1];
			// check if the player is logged.
			EntityPlayerMP player = FunctionHelper.getPlayerForName(name);
			if (player == null)
			{
				OutputHandler.chatWarning(sender, "A player of that name is not on the server. Doing the action anyways.");
			}

			// pasre setPass
			if (args[0].equalsIgnoreCase("setPass"))
			{
				if (!hasAdmin)
					throw new PermissionDeniedException();

				PlayerPassData data = PlayerPassData.getData(name);
				if (data == null)
				{
					data = new PlayerPassData(name, args[2]);
					PlayerPassData.registerData(data);
				}
				else
				{
					data.password = ModuleAuth.encrypt(args[2]);
				}
			}
		}
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		if (args.length == 0)
			throw new WrongUsageException("command.auth.usage");

		// one arg? must be help.
		if (args.length == 1)
		{
			if (args[0].equalsIgnoreCase("help"))
			{
				sender.sendChatToPlayer(" - /auth kick <player>  - forces the player to login again");
				sender.sendChatToPlayer(" - /auth setpass <player> <password>  - sets the players password to the specified");
				sender.sendChatToPlayer(" - /auth unregister <player>  - forces the player to register again");
				return;
			}
			else
				throw new WrongUsageException("command.auth.usage");
		}

		// check for players.. all the rest of these should be greated than 1.
		String name = args[1];
		boolean isLogged = true;

		// check if the player is logged.
		EntityPlayerMP player = FunctionHelper.getPlayerForName(name);
		if (player == null)
		{
			sender.sendChatToPlayer("A player of that name is not on the server. Doing the action anyways.");
			isLogged = false;
		}

		// 2 args? seconds needs to be the player.
		if (args.length == 2)
		{
			// parse ./auth kick
			if (args[0].equalsIgnoreCase("kick"))
			{
				if (!isLogged)
					throw new WrongUsageException("command.auth.usage.kick");
				else
				{
					ModuleAuth.unLogged.add(name);
					sender.sendChatToPlayer(Localization.format("command.auth.kick.user", name));
					OutputHandler.chatWarning(player, Localization.get("command.auth.kick.target"));
					return;
				}
			}
			// parse ./auth setpass
			else if (args[0].equalsIgnoreCase("setPass"))
				throw new WrongUsageException("command.auth.usage.setpass");
			else if (args[0].equalsIgnoreCase("unregister"))
			{
				if (PlayerPassData.getData(name) == null)
					throw new WrongUsageException("message.auth.error.notregisterred", "name");

				PlayerPassData.deleteData(name);
				return;
			}

			// ERROR! :D
			else
				throw new WrongUsageException("command.auth.usage");
		}
		// 3 args? must be a command - player - pass
		else if (args.length == 3)
		{
			// pasre setPass
			if (args[0].equalsIgnoreCase("setPass"))
			{
				PlayerPassData data = PlayerPassData.getData(name);
				if (data == null)
				{
					data = new PlayerPassData(name, args[2]);
					PlayerPassData.registerData(data);
				}
				else
				{
					data.password = ModuleAuth.encrypt(args[2]);
				}
			}
		}
	}

	@Override
	public boolean canConsoleUseCommand()
	{
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<?> addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		ArrayList<String> list = new ArrayList<String>();
		switch (args.length)
			{
				case 1:
					if (sender instanceof EntityPlayer)
					{
						list.addAll(CommandBase.getListOfStringsMatchingLastWord(args, playerCommands));
					}
					else
					{
						list.addAll(CommandBase.getListOfStringsMatchingLastWord(args, serverCommands));
					}
					break;
				case 2:
					if (args[0].equalsIgnoreCase("kick") || args[0].equalsIgnoreCase("setpass") ||
							args[0].equalsIgnoreCase("unregister"))
					{
						list.addAll(CommandBase.getListOfStringsMatchingLastWord(args, FMLCommonHandler.instance().getMinecraftServerInstance().getAllUsernames()));
					}
			}
		return list;
	}

	@Override
	public String getCommandPerm()
	{
		return "ForgeEssentials.ModuleAuth";
	}
}
