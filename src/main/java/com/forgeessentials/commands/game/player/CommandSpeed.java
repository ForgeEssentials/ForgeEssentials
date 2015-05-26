package com.forgeessentials.commands.game.player;

import com.forgeessentials.commons.network.Packet6Speed;
import com.forgeessentials.util.PlayerInfo;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.commons.network.NetworkUtils;
import com.forgeessentials.util.OutputHandler;

public class CommandSpeed extends FEcmdModuleCommands
{
    @Override
    public void processCommandPlayer(EntityPlayerMP player, String[] args)
    {
        if (!PlayerInfo.get(player).getHasFEClient())
        {
            OutputHandler.chatError(player, "You need the FE client addon to use this command.");
            OutputHandler.chatError(player, "Please visit https://github.com/ForgeEssentials/ForgeEssentialsMain/wiki/FE-Client-mod for more information.");
            return;
        }

        OutputHandler.chatWarning(player, "Here be dragons. Proceed at own risk. Use /speed reset to reset your speed..");
        if (args.length >= 1)
        {
            //float speed = Float.parseFloat(args[0]);

            if (args[0].equals("reset"))
            {
                OutputHandler.chatNotification(player, "Resetting speed to regular walking speed.");
                NetworkUtils.netHandler.sendTo(new Packet6Speed(0.0F), player);
                return;
            }

            float speed = 0.05F;

            int multiplier = parseInt(player, args[0]);

            if (multiplier >= 10)
            {
                OutputHandler.chatWarning(player, "Multiplier set too high. Bad things may happen, so we're throttling your speed to 10x walking speed.");
                multiplier = 10;
            }
            speed = speed * multiplier;
            NetworkUtils.netHandler.sendTo(new Packet6Speed(speed), player);
        }
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {
        return RegisteredPermValue.OP;
    }

    @Override
    public String getCommandName()
    {
        return "speed";
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_)
    {
        return "/speed <speed> Set or change the player's speed.";
    }
}
