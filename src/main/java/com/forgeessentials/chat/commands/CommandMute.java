package com.forgeessentials.chat.commands;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.UserIdent;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import java.util.List;

public class CommandMute extends ForgeEssentialsCommandBase {
    @Override
    public String getCommandName()
    {
        return "mute";
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        if (args.length == 1)
        {
            return getListOfStringsMatchingLastWord(args, FMLCommonHandler.instance().getMinecraftServerInstance().getAllUsernames());
        }
        else
        {
            return null;
        }
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args)
    {
        if (args.length == 1)
        {
            EntityPlayerMP receiver = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);
            if (receiver == null)
            {
                OutputHandler.chatError(receiver, String.format("Player %s does not exist, or is not online.", args[0]));
                return;
            }
            NBTTagCompound tag = receiver.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
            tag.setBoolean("mute", true);
            receiver.getEntityData().setTag(EntityPlayer.PERSISTED_NBT_TAG, tag);
            OutputHandler.chatError(sender, String.format("You muted %s.", args[0]));
            OutputHandler.chatError(receiver, String.format("You were muted by %s.", sender.getCommandSenderName()));
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
        return "fe.chat." + getCommandName();
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {

        return "/mute <player> Mutes the specified player.";
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {

        return RegisteredPermValue.OP;
    }

}
