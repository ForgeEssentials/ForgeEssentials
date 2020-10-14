package com.forgeessentials.util.selections;

//Depreciated

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.moduleLauncher.ModuleLauncher;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.output.ChatOutputHandler;

public class CommandWand extends ForgeEssentialsCommandBase
{

    @Override
    public String getPrimaryAlias()
    {
        return "/fewand";
    }

    @Override
    public void processCommandPlayer(MinecraftServer server, EntityPlayerMP sender, String[] args) throws CommandException
    {
        if (ModuleLauncher.getModuleList().contains("WEIntegrationTools"))
        {
            ChatOutputHandler.chatNotification(sender, "WorldEdit is installed. Please use WorldEdit selections (//wand, //set, etc)");
            ChatOutputHandler.chatNotification(sender, "Please refer to http://wiki.sk89q.com/wiki/WorldEdit/Selection for more info.");
            return;
        }

        // Get the wand item (or hands)
        Item wandItem;
        String wandId, wandName;
        int wandDmg = 0;
        if (sender.getHeldItemMainhand() != null)
        {
            wandName = sender.getHeldItemMainhand().getDisplayName();
            wandItem = sender.getHeldItemMainhand().getItem();
            wandDmg = sender.getHeldItemMainhand().getItemDamage();
            wandId = wandItem.getUnlocalizedName();
            if (wandDmg == -1)
            {
                wandDmg = 0;
            }
        }
        else
        {
            wandName = "your hands";
            wandId = "hands";
        }

        PlayerInfo info = PlayerInfo.get(sender.getPersistentID());

        // Check for rebind
        boolean rebind = args.length > 0 && args[0].equalsIgnoreCase("rebind");

        // Check for unbind
        if (!rebind && ((info.isWandEnabled() && info.getWandID().equals(wandId)) | (args.length > 0 && args[0].equalsIgnoreCase("unbind"))))
        {
            ChatOutputHandler.sendMessage(sender, TextFormatting.LIGHT_PURPLE + "Wand unbound from " + wandName);
            info.setWandEnabled(false);
            return;
        }

        // Check for permissions
        if (!checkCommandPermission(sender))
            throw new TranslatedCommandException(FEPermissions.MSG_NO_COMMAND_PERM);

        // Bind wand
        info.setWandEnabled(true);
        info.setWandID(wandId);
        info.setWandDmg(wandDmg);
        ChatOutputHandler.chatConfirmation(sender, "Wand bound to " + wandName);
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
    public String getUsage(ICommandSender sender)
    {
        return "/" + getPrimaryAlias() + " [rebind|unbind|ITEM] Toggles the wand";
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.ALL;
    }

}
