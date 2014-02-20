package com.forgeessentials.commands;

import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

import com.forgeessentials.api.permissions.IPermRegisterEvent;
import com.forgeessentials.api.permissions.RegGroup;
import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.commands.util.InventoryPlayerExtend;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.Localization;
import com.forgeessentials.util.OutputHandler;

import cpw.mods.fml.common.FMLCommonHandler;

public class CommandClearInventory extends FEcmdModuleCommands
{
	@Override
	public String getCommandName()
	{
		return "clear";
	}
	
	@Override
    public String[] getDefaultAliases()
    {
        return new String[] {"ci"};
    }
	
	@Override
	public void processCommand(ICommandSender par1ICommandSender, String[] par2ArrayOfStr)
    {
        EntityPlayerMP var3 = par2ArrayOfStr.length == 0 ? getCommandSenderAsPlayer(par1ICommandSender) : getPlayer(par1ICommandSender, par2ArrayOfStr[0]);
        int var4 = par2ArrayOfStr.length >= 2 ? parseIntWithMin(par1ICommandSender, par2ArrayOfStr[1], 1) : -1;
        int var5 = par2ArrayOfStr.length >= 3 ? parseIntWithMin(par1ICommandSender, par2ArrayOfStr[2], 0) : -1;
        int var7;
        ItemStack[] MainHold = null;
        ItemStack[] ArmorHold = null;
        if (par2ArrayOfStr.length == 4)
        {
        	 MainHold = (ItemStack[])var3.inventory.mainInventory.clone();
            ArmorHold = (ItemStack[])var3.inventory.armorInventory.clone();
        	InventoryPlayerExtend test = new InventoryPlayerExtend(var3);
        	test.armorInventory = var3.inventory.armorInventory;
        	test.mainInventory = var3.inventory.mainInventory;
            var7 = test.clearInventory(var4, var5, parseIntWithMin(par1ICommandSender, par2ArrayOfStr[3], 0));
        }
        else
        {
            var7 = var3.inventory.clearInventory(var4, var5);
        }

        var3.inventoryContainer.detectAndSendChanges();

        if (var7 == -1)
        {
        	var3.inventory.mainInventory = MainHold;
        	var3.inventory.armorInventory = ArmorHold;
            throw new CommandException("Not Enough of the given Item + metadata in" + var3.getEntityName() + "\'s inventory", new Object[] {var3.getEntityName()});
        }
        else if (var7 == 0)
        {
            throw new CommandException("commands.clear.failure", new Object[] {var3.getEntityName()});
        }
        else
        {
            notifyAdmins(par1ICommandSender, "commands.clear.success", new Object[] {var3.getEntityName(), Integer.valueOf(var7)});
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
				int clearPar1 = -1, clearPar2 = -1;
				if (args.length >= 2)
				{
					clearPar1 = parseInt(sender, args[1]);
					clearPar2 = parseInt(sender, args[2]);
				}
				player.inventory.clearInventory(clearPar1, clearPar2);

				player.inventoryContainer.detectAndSendChanges();
				OutputHandler.chatWarning(sender, Localization.format("command.clear.doneBy", sender.getCommandSenderName()));
				OutputHandler.chatConfirmation(sender, Localization.format("command.clear.doneOf", args[0]));
			}
			else
			{
				OutputHandler.chatError(sender, Localization.format(Localization.ERROR_NOPLAYER, args[0]));
			}
		}
		else
		{
			OutputHandler.chatError(sender, Localization.get(Localization.ERROR_BADSYNTAX) + getSyntaxConsole());
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
		return "ForgeEssentials.BasicCommands.clear.self";
	}

	@Override
	public List<?> addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		if (args.length == 0)
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
	public void registerExtraPermissions(IPermRegisterEvent event)
	{
		event.registerPermissionLevel(getCommandPerm() + ".others", RegGroup.OWNERS);
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}
}
