package com.forgeessentials.commands.player;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraftforge.permission.PermissionLevel;

import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.util.output.ChatOutputHandler;

public class CommandSpeed extends ForgeEssentialsCommandBase
{
    @Override
    public String getCommandName()
    {
        return "fespeed";
    }

    @Override
    public String[] getDefaultAliases()
    {
        return new String[] { "speed" };
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_)
    {
        return "/speed <speed> Set or change the player's speed.";
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public PermissionLevel getPermissionLevel()
    {
        return PermissionLevel.OP;
    }

    @Override
    public String getPermissionNode()
    {
        return ModuleCommands.PERM + ".speed";
    }

    @Override
    public void processCommandPlayer(EntityPlayerMP player, String[] args) throws CommandException
    {
        /*
         * if (!PlayerInfo.get(player).getHasFEClient()) { ChatOutputHandler.chatError(player, "You need the FE client addon to use this command.");
         * ChatOutputHandler.chatError(player, "Please visit https://github.com/ForgeEssentials/ForgeEssentialsMain/wiki/FE-Client-mod for more information."); return; }
         */

        ChatOutputHandler.chatWarning(player, "Here be dragons. Proceed at own risk. Use /speed reset to reset your speed..");
        if (args.length >= 1)
        {
            // float speed = Float.parseFloat(args[0]);

            if (args[0].equals("reset"))
            {
                ChatOutputHandler.chatNotification(player, "Resetting speed to regular walking speed.");
                // NetworkUtils.netHandler.sendTo(new Packet6Speed(0.0F), player);
                NBTTagCompound tagCompound = new NBTTagCompound();
                player.capabilities.writeCapabilitiesToNBT(tagCompound);
                tagCompound.getCompoundTag("abilities").setTag("flySpeed", new NBTTagFloat(0.05F));
                tagCompound.getCompoundTag("abilities").setTag("walkSpeed", new NBTTagFloat(0.1F));
                player.capabilities.readCapabilitiesFromNBT(tagCompound);
                player.sendPlayerAbilities();
                return;
            }

            float speed = 0.05F;

            int multiplier = parseInt(args[0]);

            if (multiplier >= 10)
            {
                ChatOutputHandler.chatWarning(player, "Multiplier set too high. Bad things may happen, so we're throttling your speed to 10x walking speed.");
                multiplier = 10;
            }
            speed = speed * multiplier;
            NBTTagCompound tagCompound = new NBTTagCompound();
            player.capabilities.writeCapabilitiesToNBT(tagCompound);
            tagCompound.getCompoundTag("abilities").setTag("flySpeed", new NBTTagFloat(speed));
            tagCompound.getCompoundTag("abilities").setTag("walkSpeed", new NBTTagFloat(speed));
            player.capabilities.readCapabilitiesFromNBT(tagCompound);
            player.sendPlayerAbilities();

            ChatOutputHandler.chatNotification(player, "Walk/fly speed set to " + speed);
            // NetworkUtils.netHandler.sendTo(new Packet6Speed(speed), player);
        }
    }
}
