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

public class CommandWand extends ForgeEssentialsCommandBase {

    @Override
    public String getCommandName()
    {
        return "/fewand";
    }

    @Override
    public void processCommandPlayer(EntityPlayer sender, String[] args)
    {
        boolean allowed = checkCommandPerm(sender);

        PlayerInfo info = PlayerInfo.getPlayerInfo(sender.getPersistentID());
        Item currentID = sender.getCurrentEquippedItem() == null ? Item.getItemFromBlock(Blocks.air) : sender.getCurrentEquippedItem().getItem();
        int currentDmg = 0;

        if (currentID != FunctionHelper.AIR && sender.getCurrentEquippedItem().getHasSubtypes())
        {
            currentDmg = sender.getCurrentEquippedItem().getItemDamage();
        }

        String currentName = currentID == FunctionHelper.AIR ? "your fists" : sender.getCurrentEquippedItem().getDisplayName();
        String wandName = "";
        if (info.wandEnabled)
        {
            if (sender.getCurrentEquippedItem() == null || info.wandID == FunctionHelper.AIR.getUnlocalizedName())
            {
                wandName = "your fists";
            }
            else
            {
                wandName = sender.getCurrentEquippedItem().getDisplayName();
            }
        }

        if (args.length > 0)
        {
            if (args[0].equalsIgnoreCase("rebind"))
            {
                if (allowed)
                {
                    info.wandEnabled = true;
                    info.wandID = currentID.getUnlocalizedName();
                    info.wandDmg = currentDmg == -1 ? 0 : currentDmg;
                    OutputHandler.chatConfirmation(sender, "Wand bound to " + currentName);
                    return;
                }
                else
                {
                    OutputHandler.chatError(sender, "Could not bind wand to " + currentName);
                    return;
                }
            }
            else if (args[0].equalsIgnoreCase("unbind"))
            {
                info.wandEnabled = false;
                ChatUtils.sendMessage(sender, EnumChatFormatting.LIGHT_PURPLE + "Wand unbound from " + wandName);
                return;
            }
            else
            {
                if (allowed)
                {
                    int[] parsed = FunctionHelper.parseIdAndMetaFromString(args[0], false);
                    currentID = ((Item)GameData.getItemRegistry().getObject(parsed[0]));
                    currentDmg = parsed[1];
                    info.wandEnabled = true;
                    info.wandID = currentID.getUnlocalizedName();
                    info.wandDmg = currentDmg == -1 ? 0 : currentDmg;
                    OutputHandler.chatConfirmation(sender, "Wand bound to " + currentName);
                }
                else
                {
                    OutputHandler.chatError(sender, "Could not bind wand to " + currentName);
                    return;
                }
            }
        }
        else
        {
            if (info.wandEnabled)
            {
                info.wandEnabled = false;
                ChatUtils.sendMessage(sender, EnumChatFormatting.LIGHT_PURPLE + "Wand unbound from " + wandName);
                return;
            }
            else
            {
                if (allowed)
                {
                    info.wandEnabled = true;
                    info.wandID = currentID.getUnlocalizedName();
                    info.wandDmg = currentDmg == -1 ? 0 : currentDmg;
                    OutputHandler.chatConfirmation(sender, "Wand bound to " + currentName);
                    return;
                }
                else
                {
                    OutputHandler.chatError(sender, "Could not bind wand to " + currentName);
                    return;
                }
            }
        }
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
    public String getCommandPerm()
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
