package com.forgeessentials.core.commands.selections;

//Depreciated

import com.forgeessentials.api.permissions.RegGroup;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.util.ChatUtils;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.PlayerInfo;
import cpw.mods.fml.common.registry.GameData;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.EnumChatFormatting;

import java.util.List;

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
		String wandId = null, wandName;
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
		}

        PlayerInfo info = PlayerInfo.getPlayerInfo(sender.getPersistentID());

        // Check for rebind
        boolean rebind = args.length > 0 && args[0].equalsIgnoreCase("rebind");
        
		// Check for unbind
		if (!rebind && ((info.wandEnabled && info.wandID.equals(wandId)) | (args.length > 0 && args[0].equalsIgnoreCase("unbind")))) {
			ChatUtils.sendMessage(sender, EnumChatFormatting.LIGHT_PURPLE + "Wand unbound from " + wandName);
			info.wandEnabled = false;
			return;
		}

		// Check for permissions
		if (!checkCommandPerm(sender)) {
			OutputHandler.chatError(sender, "You have no permission to use fewand!");
			return;
		}

		if (args.length > 0 && !rebind) {
			List<Object> data = FunctionHelper.parseIdAndMetaFromString(args[0], false);
			wandItem = ((Item) GameData.getItemRegistry().getObject(data.get(0)));
			wandId = wandItem.getUnlocalizedName();
			wandDmg = (int) data.get(1);
			if (wandDmg == -1) {
				wandDmg = 0;
			}
		}
		
		// Bind wand
		info.wandEnabled = true;
		info.wandID = wandId;
		info.wandDmg = wandDmg;
		OutputHandler.chatConfirmation(sender, "Wand bound to " + wandName);
    }

    @Override
    public boolean canPlayerUseCommand(EntityPlayer player)
    {
        PlayerInfo info = PlayerInfo.getPlayerInfo(player.getPersistentID());
        if (info.wandEnabled)
        {
            return true;
        }
        else
        {
            return checkCommandPerm(player);
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
    public RegGroup getReggroup()
    {

        return RegGroup.MEMBERS;
    }
}
