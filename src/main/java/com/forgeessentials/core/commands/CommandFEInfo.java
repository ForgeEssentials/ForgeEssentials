package com.forgeessentials.core.commands;

import net.minecraft.command.ICommandSender;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.RegGroup;
import com.forgeessentials.core.moduleLauncher.ModuleLauncher;
import com.forgeessentials.core.preloader.Data;
import com.forgeessentials.core.preloader.FEModContainer;
import com.forgeessentials.core.preloader.asm.FEeventAdder;
import com.forgeessentials.util.ChatUtils;

public class CommandFEInfo extends ForgeEssentialsCommandBase {

	@Override
	public String getCommandName() {
		return "feinfo";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/feinfo";
	}

	@Override
	public boolean canConsoleUseCommand() {
		return true;
	}

	@Override
	public String getCommandPerm() {
		return "fe.core.info";
	}
	
	@Override
	public void processCommand(ICommandSender sender, String[] args){
		if (args.length == 0){
			ChatUtils.sendMessage(sender, "/feinfo debug Produces ASM transformer debug output.");
			ChatUtils.sendMessage(sender, "/feinfo reload Reloads all configs.");
			ChatUtils.sendMessage(sender, "/feinfo about About ForgeEssentials");
		}
		else if (args[0].equalsIgnoreCase("debug")){
			try
			{
				if (FEeventAdder.addedPlace)
				{
					ChatUtils.sendMessage(sender, "The custom event 'PlayerBlockPlace' was added.");
				}
				else
				{
					ChatUtils.sendMessage(sender, "The custom event 'PlayerBlockPlace' was NOT added. Some functions might not work!");
					ChatUtils.sendMessage(sender, "The classname should be '" + Data.ISob.get("className") + "' but is '" + ItemStack.class.getName() + "'.");
				}
			}
			catch (Exception ex)
			{
				ChatUtils.sendMessage(sender, "Error finding custom event 'PlayerBlockPlace'");
			}
			ChatUtils.sendMessage(sender, "This output is also in your FML logs.");
		}
		else if (args[0].equalsIgnoreCase("reload")){
			ChatUtils.sendMessage(sender, "Reloading ForgeEssentials configs. May not work for all settings!");
			ChatUtils.sendMessage(sender, EnumChatFormatting.RED + "This is experimental!");
			ModuleLauncher.instance.reloadConfigs(sender);
			ChatUtils.sendMessage(sender, "Done!");
		}
		else if (args[0].equalsIgnoreCase("about")){
			ChatUtils.sendMessage(sender, "You are currently running ForgeEssentials version " + FEModContainer.version);
			ChatUtils.sendMessage(sender, "Please refer to https://github.com/ForgeEssentials/ForgeEssentialsMain/wiki/Team-Information if you would like more information about the FE developers.");
		}
	}

	@Override
	public RegGroup getReggroup() {
		// TODO Auto-generated method stub
		return RegGroup.MEMBERS;
	}

}
