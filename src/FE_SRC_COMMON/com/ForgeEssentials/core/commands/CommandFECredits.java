package com.ForgeEssentials.core.commands;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;

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
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		sender.sendChatToPlayer("AbrarSyed: " + Localization.get(Localization.CREDITS_ABRARSYED));
		sender.sendChatToPlayer("Bob A Red Dino: " + Localization.get(Localization.CREDITS_BOBAREDDINO));
		sender.sendChatToPlayer("bspkrs: " + Localization.get(Localization.CREDITS_BSPKRS));
		sender.sendChatToPlayer("MysteriousAges: " + Localization.get(Localization.CREDITS_MYSTERIOUSAGES));
		sender.sendChatToPlayer("luacs1998: " + Localization.get(Localization.CREDITS_LUACS1998));
		sender.sendChatToPlayer("Dries007: " + Localization.get(Localization.CREDITS_DRIES007));
		sender.sendChatToPlayer("Malkierian: " + Localization.get(Localization.CREDITS_MALKIERIAN));
	}

	@Override
	public boolean canConsoleUseCommand()
	{
		return true;
	}

	@Override
	public boolean canPlayerUseCommand(EntityPlayer player)
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
		// TODO Auto-generated method stub
		return null;
	}

}
 
