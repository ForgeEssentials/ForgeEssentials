package com.forgeessentials.core.commands;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import com.forgeessentials.util.ChatUtils;
import com.forgeessentials.util.Localization;
import com.forgeessentials.util.OutputHandler;

public class CommandFECredits extends ForgeEssentialsCommandBase
{

	@Override
	public String getCommandName()
	{
		return "fecredits";
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		OutputHandler.chatConfirmation(sender, "AbrarSyed: " + Localization.get(Localization.CREDITS_ABRARSYED));
		OutputHandler.chatConfirmation(sender, "Bob A Red Dino: " + Localization.get(Localization.CREDITS_BOBAREDDINO));
		OutputHandler.chatConfirmation(sender, "bspkrs: " + Localization.get(Localization.CREDITS_BSPKRS));
		OutputHandler.chatConfirmation(sender, "MysteriousAges: " + Localization.get(Localization.CREDITS_MYSTERIOUSAGES));
		OutputHandler.chatConfirmation(sender, "luacs1998: " + Localization.get(Localization.CREDITS_LUACS1998));
		OutputHandler.chatConfirmation(sender, "Dries007: " + Localization.get(Localization.CREDITS_DRIES007));
		OutputHandler.chatConfirmation(sender, "Malkierian: " + Localization.get(Localization.CREDITS_MALKIERIAN));

		ChatUtils.sendMessage(sender, "More info:");
		ChatUtils.sendMessage(sender, "https://github.com/ForgeEssentials/ForgeEssentialsMain/wiki/Team-Information");
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		ChatUtils.sendMessage(sender, "AbrarSyed: " + Localization.get(Localization.CREDITS_ABRARSYED));
		ChatUtils.sendMessage(sender, "Bob A Red Dino: " + Localization.get(Localization.CREDITS_BOBAREDDINO));
		ChatUtils.sendMessage(sender, "bspkrs: " + Localization.get(Localization.CREDITS_BSPKRS));
		ChatUtils.sendMessage(sender, "MysteriousAges: " + Localization.get(Localization.CREDITS_MYSTERIOUSAGES));
		ChatUtils.sendMessage(sender, "luacs1998: " + Localization.get(Localization.CREDITS_LUACS1998));
		ChatUtils.sendMessage(sender, "Dries007: " + Localization.get(Localization.CREDITS_DRIES007));
		ChatUtils.sendMessage(sender, "Malkierian: " + Localization.get(Localization.CREDITS_MALKIERIAN));

		ChatUtils.sendMessage(sender, "More info:");
		ChatUtils.sendMessage(sender, "https://github.com/ForgeEssentials/ForgeEssentialsMain/wiki/Team-Information");
	}

	@Override
	public boolean canConsoleUseCommand()
	{
		return true;
	}

	@Override
	public String getCommandPerm()
	{
		return null;
	}

	@Override
	public List<?> addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		return null;
	}


	@Override
	public String getSyntaxConsole()
	{
		return "";
	}

	@Override
	public String getSyntaxPlayer(EntityPlayer player)
	{
		return "";
	}

	@Override
	public String getInfoConsole()
	{
		return "Get all of the FE credits.";
	}

	@Override
	public String getInfoPlayer(EntityPlayer player)
	{
		return "Get all of the FE credits.";
	}

	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}
}
