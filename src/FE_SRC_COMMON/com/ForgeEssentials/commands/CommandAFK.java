package com.ForgeEssentials.commands;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.Configuration;

import com.ForgeEssentials.api.APIRegistry;
import com.ForgeEssentials.api.permissions.IPermRegisterEvent;
import com.ForgeEssentials.api.permissions.RegGroup;
import com.ForgeEssentials.api.permissions.query.PermQueryPlayer;
import com.ForgeEssentials.commands.util.AFKdata;
import com.ForgeEssentials.commands.util.FEcmdModuleCommands;
import com.ForgeEssentials.commands.util.TickHandlerCommands;
import com.ForgeEssentials.util.ChatUtils;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;

public class CommandAFK extends FEcmdModuleCommands
{
    public static CommandAFK instance;
    public CommandAFK()
    {
        instance = this;
    }
    public final String         NOTICEPERM = getCommandPerm() + ".notice";
	public static List<String>	afkList	= new ArrayList<String>();

	// Config
	public static int			warmup	= 5;

	@Override
	public void doConfig(Configuration config, String category)
	{
		warmup = config.get(category, "warmup", 5, "Time in sec. you have to stand still to activate AFK.").getInt();
	}

	@Override
	public String getCommandName()
	{
		return "afk";
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		TickHandlerCommands.afkListToAdd.add(new AFKdata((EntityPlayerMP) sender));
		OutputHandler.chatConfirmation(sender, Localization.format("command.afk.warmup", warmup));
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
	}

	@Override
	public boolean canConsoleUseCommand()
	{
		return false;
	}

	@Override
	public String getCommandPerm()
	{
		return "ForgeEssentials.BasicCommands." + getCommandName();
	}

	public void abort(AFKdata afkData)
	{
		if (!afkData.player.capabilities.isCreativeMode)
			afkData.player.capabilities.disableDamage = false;
		afkData.player.sendPlayerAbilities();
		afkList.remove(afkData.player.username);
		TickHandlerCommands.afkListToRemove.add(afkData);

		if (APIRegistry.perms.checkPermAllowed(new PermQueryPlayer(afkData.player, NOTICEPERM)))
			ChatUtils.sendMessage(MinecraftServer.getServer().getConfigurationManager(),
					Localization.format("command.afk.notice.out", afkData.player.username));
		else
		    OutputHandler.chatConfirmation(afkData.player, Localization.get("command.afk.out"));
	}

	public void makeAFK(AFKdata afkData)
	{
		afkData.player.capabilities.disableDamage = true;
		afkData.player.sendPlayerAbilities();
		afkList.add(afkData.player.username);

		if (APIRegistry.perms.checkPermAllowed(new PermQueryPlayer(afkData.player, NOTICEPERM)))
			ChatUtils.sendMessage(MinecraftServer.getServer().getConfigurationManager(),
					Localization.format("command.afk.notice.in", afkData.player.username));
		else
		    OutputHandler.chatConfirmation(afkData.player, Localization.get("command.afk.in"));
	}

	@Override
	public void registerExtraPermissions(IPermRegisterEvent event)
	{
	    event.registerPermissionLevel(NOTICEPERM, RegGroup.MEMBERS);
	}

	@Override
	public List<?> addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		return null;
	}

	@Override
	public RegGroup getReggroup()
	{
		return RegGroup.MEMBERS;
	}
}
