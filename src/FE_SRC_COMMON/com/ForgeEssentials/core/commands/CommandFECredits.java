package com.ForgeEssentials.core.commands;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.util.Localization;

public class CommandFECredits extends ForgeEssentialsCommandBase
{

	@Override
	public String getCommandName()
	{
		return "credits";
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		sender.sendChatToPlayer("AbrarSyed: " + Localization.get(Localization.CREDITS_ABRARSYED));
		sender.sendChatToPlayer("Bob A Red Dino: " + Localization.get(Localization.CREDITS_BOBAREDDINO));
		sender.sendChatToPlayer("bspkrs: " + Localization.get(Localization.CREDITS_BSPKRS));
		sender.sendChatToPlayer("MysteriousAges: " + Localization.get(Localization.CREDITS_MYSTERIOUSAGES));
		sender.sendChatToPlayer("luacs1998: " + Localization.get(Localization.CREDITS_LUACS1998));
		sender.sendChatToPlayer("Dries007: " + Localization.get(Localization.CREDITS_DRIES007));
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

}
