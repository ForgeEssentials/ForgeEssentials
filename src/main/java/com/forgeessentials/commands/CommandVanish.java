package com.forgeessentials.commands;

import java.util.HashSet;
import java.util.UUID;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.util.OutputHandler;

public class CommandVanish extends FEcmdModuleCommands {
    public static final String TAGNAME = "vanish";

    public static HashSet<UUID> vanishedPlayers = new HashSet<UUID>();

    @Override
    public String getCommandName()
    {
        return "vanish";
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {
        return RegisteredPermValue.OP;
    }

    @Override
    public void processCommandPlayer(EntityPlayerMP sender, String[] args)
    {
        NBTTagCompound tag = sender.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
        tag.setBoolean(TAGNAME, !tag.getBoolean(TAGNAME));
        sender.getEntityData().setTag(EntityPlayer.PERSISTED_NBT_TAG, tag);

        if (tag.getBoolean(TAGNAME))
        {
            OutputHandler.chatConfirmation(sender, "You have vanished.");
            vanishedPlayers.add(sender.getPersistentID());
        }
        else
        {
            OutputHandler.chatConfirmation(sender, "You have reappeared.");
            vanishedPlayers.remove(sender.getPersistentID());

            for (Object fakePlayer : MinecraftServer.getServer().worldServers[sender.dimension].playerEntities)
            {
                if (fakePlayer != sender)
                {
                    EntityPlayer player = (EntityPlayer) fakePlayer;
                    player.setInvisible(false);
                }
            }
        }
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/vanish Toggles invisibility.";
    }
}
