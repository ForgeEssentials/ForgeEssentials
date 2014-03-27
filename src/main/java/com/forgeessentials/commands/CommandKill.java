
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.DamageSource;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.IPermRegisterEvent;
import com.forgeessentials.api.permissions.RegGroup;
import com.forgeessentials.api.permissions.query.PermQueryPlayer;
import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.util.ChatUtils;
import com.forgeessentials.util.FunctionHelper;

import cpw.mods.fml.common.FMLCommonHandler;

public class CommandKill extends FEcmdModuleCommands
{

	@Override
	public String getCommandName()
	{
		return "kill";
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		if (args.length >= 1 && APIRegistry.perms.checkPermAllowed(new PermQueryPlayer(sender, getCommandPerm() + ".others")))
		{
			EntityPlayerMP player = FunctionHelper.getPlayerForName(sender, args[0]);
			if (player != null)
			{
				player.attackEntityFrom(DamageSource.outOfWorld, 1000);
				ChatUtils.sendMessage(player, Localization.get("command.kill.msg"));
			}
			else
			{
				ChatUtils.sendMessage(sender, String.format("Player %s does not exist, or is not online.", args[0]));
			}
		}
		else
		{
			sender.attackEntityFrom(DamageSource.outOfWorld, 1000);
			ChatUtils.sendMessage(sender, Localization.get("command.kill.msg"));
		}
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		if (args.length >= 1)
		{
			EntityPlayerMP player = FunctionHelper.getPlayerForName(sender, args[0]);
			if (player != null)
			{
				player.attackEntityFrom(DamageSource.outOfWorld, 1000);
				ChatUtils.sendMessage(player, Localization.get("command.kill.msg"));
			}
			else
			{
				ChatUtils.sendMessage(sender, String.format("Player %s does not exist, or is not online.", args[0]));
			}
		}
		else
		{
			ChatUtils.sendMessage(sender, "Improper syntax. Please try this instead: " + getSyntaxConsole());
		}
	}

	@Override
	public boolean canConsoleUseCommand()
	{
		return true;
	}

	@Override
	public String getCommandPerm()
	{
		return "ForgeEssentials.BasicCommands." + getCommandName();
	}

	@Override
	public void registerExtraPermissions(IPermRegisterEvent event)
	{
		event.registerPermissionLevel(getCommandPerm() + ".others", RegGroup.OWNERS);
	}

	@Override
	public List<?> addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		if (args.length == 1)
			return getListOfStringsMatchingLastWord(args, FMLCommonHandler.instance().getMinecraftServerInstance().getAllUsernames());
		else
			return null;
	}

	@Override
	public RegGroup getReggroup()
	{
		return RegGroup.OWNERS;
	}

	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

}
