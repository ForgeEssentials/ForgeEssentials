package com.forgeessentials.core.commands.selections;

//Depreciated

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import org.apache.commons.lang3.tuple.Pair;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.PlayerInfo;

import cpw.mods.fml.common.registry.GameData;

public class CommandWand extends ForgeEssentialsCommandBase {

    @Override
    public String getCommandName()
    {
        return "/fewand";
    }

    @Override
    public void processCommandPlayer(EntityPlayer sender, String[] args)
    {
		// Get the wand item (or hands)
		Item wandItem;
		String wandId, wandName;
		int wandDmg = 0;
		if (sender.getCurrentEquippedItem() != null) {
			wandName = sender.getCurrentEquippedItem().getDisplayName();
			wandItem = sender.getCurrentEquippedItem().getItem();
			wandDmg = sender.getCurrentEquippedItem().getItemDamage();
			wandId = wandItem.getUnlocalizedName();
			if (wandDmg == -1) {
				wandDmg = 0;
			}
		} else {
			wandName = "your hands";
			wandId = "hands";
		}

        PlayerInfo info = PlayerInfo.getPlayerInfo(sender.getPersistentID());

        // Check for rebind
        boolean rebind = args.length > 0 && args[0].equalsIgnoreCase("rebind");
        
		// Check for unbind
		if (!rebind && ((info.isWandEnabled() && info.getWandID().equals(wandId)) | (args.length > 0 && args[0].equalsIgnoreCase("unbind")))) {
			OutputHandler.sendMessage(sender, EnumChatFormatting.LIGHT_PURPLE + "Wand unbound from " + wandName);
			info.setWandEnabled(false);
			return;
		}

		// Check for permissions
		if (!checkCommandPermission(sender)) {
			OutputHandler.chatError(sender, "You have no permission to use fewand!");
			return;
		}

		if (args.length > 0 && !rebind) {
			Pair<String, Integer> data = FunctionHelper.parseIdAndMetaFromString(args[0], false);
			wandItem = ((Item) GameData.getItemRegistry().getObject(data.getLeft()));
			wandId = wandItem.getUnlocalizedName();
			wandDmg = data.getRight();
			if (wandDmg == -1) {
				wandDmg = 0;
			}
		}
		
		// Bind wand
		info.setWandEnabled(true);
		info.setWandID(wandId);
		info.setWandDmg(wandDmg);
		OutputHandler.chatConfirmation(sender, "Wand bound to " + wandName);
    }

    @Override
    public boolean canPlayerUseCommand(EntityPlayer player)
    {
        PlayerInfo info = PlayerInfo.getPlayerInfo(player.getPersistentID());
        if (info.isWandEnabled())
        {
            return true;
        }
        else
        {
            return checkCommandPermission(player);
        }
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public String getPermissionNode()
    {
        return "fe.core.pos.wand";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/" + getCommandName() + " [rebind|unbind|ITEM] Toggles the wand";
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {

        return RegisteredPermValue.TRUE;
    }
}
