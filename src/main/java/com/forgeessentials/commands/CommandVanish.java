package com.forgeessentials.commands;

import com.forgeessentials.api.permissions.RegGroup;
import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.util.OutputHandler;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;

import java.util.HashSet;
import java.util.UUID;

public class CommandVanish extends FEcmdModuleCommands {
    public static final String TAGNAME = "vanish";

    public static HashSet<UUID> vanishedPlayers = new HashSet<UUID>();

    @Override
    public String getCommandName()
    {
        return "vanish";
    }

    @Override
    public RegGroup getReggroup()
    {
        return RegGroup.OWNERS;
    }

    @Override
    public void processCommandPlayer(EntityPlayer sender, String[] args)
    {
        NBTTagCompound tag = sender.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
        tag.setBoolean(TAGNAME, !tag.getBoolean(TAGNAME));
        sender.getEntityData().setTag(EntityPlayer.PERSISTED_NBT_TAG, tag);

        if (tag.getBoolean(TAGNAME))
        {
            OutputHandler.chatConfirmation(sender, "You are vanished now.");
            vanishedPlayers.add(sender.getPersistentID());
        }
        else
        {
            OutputHandler.chatConfirmation(sender, "You are un vanished now.");
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

        return "/vanish Makes yourself invisible";
    }
}
