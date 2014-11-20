package com.forgeessentials.commands;

import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.UserIdent;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import java.util.List;

public class CommandSmite extends FEcmdModuleCommands {

    @Override
    public String getCommandName()
    {
        return "smite";
    }

    @Override
    public void processCommandPlayer(EntityPlayerMP sender, String[] args)
    {
        if (args.length >= 1)
        {
            if (args[0].toLowerCase().equals("me"))
            {
                sender.worldObj.addWeatherEffect(new EntityLightningBolt(sender.worldObj, sender.posX, sender.posY, sender.posZ));
                OutputHandler.chatConfirmation(sender, "Was that really a good idea?");
            }
            else
            {
                EntityPlayerMP player = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);
                if (player != null)
                {
                    player.worldObj.addWeatherEffect(new EntityLightningBolt(player.worldObj, player.posX, player.posY, player.posZ));
                    OutputHandler.chatConfirmation(sender, "You should feel bad about doing that.");
                }
                else
                {
                    OutputHandler.chatError(sender, String.format("Player %s does not exist, or is not online.", args[0]));
                }
            }
        }
        else
        {
            MovingObjectPosition mop = FunctionHelper.getPlayerLookingSpot(sender, 500);
            if (mop == null)
            {
                OutputHandler.chatError(sender, "You must first look at the ground!");
            }
            else
            {
                sender.worldObj.addWeatherEffect(new EntityLightningBolt(sender.worldObj, mop.blockX, mop.blockY, mop.blockZ));
                OutputHandler.chatConfirmation(sender, "I hope that didn't start a fire.");
            }
        }
    }

    @Override
    public void processCommandConsole(ICommandSender sender, String[] args)
    {
        if (args.length >= 1)
        {
            EntityPlayerMP player = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);
            if (player != null)
            {
                player.worldObj.addWeatherEffect(new EntityLightningBolt(player.worldObj, player.posX, player.posY, player.posZ));
                OutputHandler.chatConfirmation(sender, "You should feel bad about doing that.");
            }
            else
            {
                OutputHandler.chatError(sender, String.format("Player %s does not exist, or is not online.", args[0]));
            }
        }
        else
        {
            OutputHandler.chatError(sender, "Improper syntax. Please try this instead: <player>");
        }
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
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
    public RegisteredPermValue getDefaultPermission()
    {
        return RegisteredPermValue.OP;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        if (sender instanceof EntityPlayer)
        {
            return "/smite [me] <player> Smite yourself or another player.";
        }
        else
        {
            return "/smite <player> Smite someone.";
        }
    }

}
