package com.forgeessentials.commands.player;

import net.minecraft.command.CommandException;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.FloatNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.util.output.ChatOutputHandler;

public class CommandSpeed extends ForgeEssentialsCommandBase
{

    @Override
    public String getPrimaryAlias()
    {
        return "speed";
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.OP;
    }

    @Override
    public String getPermissionNode()
    {
        return ModuleCommands.PERM + ".speed";
    }

    @Override
    public void processCommandPlayer(MinecraftServer server, ServerPlayerEntity player, String[] args) throws CommandException
    {
        ChatOutputHandler.chatWarning(player, "Here be dragons. Proceed at own risk. Use /speed reset to reset your speed..");
        if (args.length >= 1)
        {
            // float speed = Float.parseFloat(args[0]);

            if (args[0].equals("reset"))
            {
                ChatOutputHandler.chatNotification(player, "Resetting speed to regular walking speed.");
                // NetworkUtils.netHandler.sendTo(new Packet6Speed(0.0F), player);
                CompoundNBT tagCompound = new CompoundNBT();
                player.capabilities.writeCapabilitiesToNBT(tagCompound);
                tagCompound.getCompound("abilities").put("flySpeed", new FloatNBT(0.05F));
                tagCompound.getCompound("abilities").put("walkSpeed", new FloatNBT(0.1F));
                player.abilities.readCapabilitiesFromNBT(tagCompound);
                player.onUpdateAbilities();
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
            CompoundNBT tagCompound = new CompoundNBT();
            player.capabilities.writeCapabilitiesToNBT(tagCompound);
            tagCompound.getCompound("abilities").put("flySpeed", new FloatNBT(speed));
            tagCompound.getCompound("abilities").put("walkSpeed", new FloatNBT(speed));
            player.capabilities.readCapabilitiesFromNBT(tagCompound);
            player.onUpdateAbilities();

            ChatOutputHandler.chatNotification(player, "Walk/fly speed set to " + speed);
            // NetworkUtils.netHandler.sendTo(new Packet6Speed(speed), player);
        }
    }

}
