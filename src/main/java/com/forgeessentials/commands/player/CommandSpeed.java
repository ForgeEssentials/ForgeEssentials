package com.forgeessentials.commands.player;

import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.FloatNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.commons.network.NetworkUtils;
import com.forgeessentials.core.commands.BaseCommand;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class CommandSpeed extends BaseCommand
{

    public CommandSpeed(String name, int permissionLevel, boolean enabled)
    {
        super(name, permissionLevel, enabled);
    }

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
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int processCommandPlayer(CommandContext<CommandSource> ctx, Object... params) throws CommandSyntaxException
    {
        ChatOutputHandler.chatWarning(ctx.getSource(), "Here be dragons. Proceed at own risk. Use /speed reset to reset your speed..");
     // float speed = Float.parseFloat(args[0]);

        if (params.toString() == "reset")
        {
            ChatOutputHandler.chatNotification(ctx.getSource(), "Resetting speed to regular walking speed.");
            NetworkUtils.sendTo(new Packet6Speed(0.0F), player);
            CompoundNBT tagCompound = new CompoundNBT();
            player.capabilities.writeCapabilitiesToNBT(tagCompound);
            tagCompound.getCompound("abilities").put("flySpeed", new FloatNBT(0.05F));
            tagCompound.getCompound("abilities").put("walkSpeed", new FloatNBT(0.1F));
            player.abilities.readCapabilitiesFromNBT(tagCompound);
            player.onUpdateAbilities();
            return Command.SINGLE_SUCCESS;
        }

        float speed = 0.05F;

        int multiplier = parseInt(args[0]);

        if (multiplier >= 10)
        {
            ChatOutputHandler.chatWarning(ctx.getSource(), "Multiplier set too high. Bad things may happen, so we're throttling your speed to 10x walking speed.");
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
        NetworkUtils.sendTo(new Packet6Speed(speed), player);
        return Command.SINGLE_SUCCESS;
    }
}
