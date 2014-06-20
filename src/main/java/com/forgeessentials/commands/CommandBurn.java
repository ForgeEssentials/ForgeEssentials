package com.forgeessentials.commands;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.IPermRegisterEvent;
import com.forgeessentials.api.permissions.RegGroup;
import com.forgeessentials.api.permissions.query.PermQueryPlayer;
import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.util.ChatUtils;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.OutputHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.List;

public class CommandBurn extends FEcmdModuleCommands {
    @Override
    public String getCommandName()
    {
        return "burn";
    }

    @Override
    public void processCommandPlayer(EntityPlayer sender, String[] args)
    {
        if (args.length == 1)
        {
            if (args[0].toLowerCase().equals("me"))
            {
                sender.setFire(15);
                OutputHandler.chatError(sender, "Ouch! Hot!");
            }
            else if (APIRegistry.perms.checkPermAllowed(new PermQueryPlayer(sender, getCommandPerm() + ".others")))
            {
                EntityPlayerMP player = FunctionHelper.getPlayerForName(sender, args[0]);
                if (player != null)
                {
                    OutputHandler.chatConfirmation(sender, "You should feel bad about doing that.");
                    player.setFire(15);
                }
                else
                {
                    OutputHandler.chatError(sender, String.format("Player %s does not exist, or is not online.", args[0]));
                }
            }
        }
        else if (args.length == 2)
        {
            if (args[0].toLowerCase().equals("me"))
            {
                try
                {
                    sender.setFire(Integer.parseInt(args[1]));
                    OutputHandler.chatError(sender, "Ouch! Hot!");
                }
                catch (NumberFormatException e)
                {
                    OutputHandler.chatError(sender, String.format("%s param was not recognized as number. Please try again.", args[1]));
                }
            }
            else if (APIRegistry.perms.checkPermAllowed(new PermQueryPlayer(sender, getCommandPerm() + ".others")))
            {
                EntityPlayerMP player = FunctionHelper.getPlayerForName(sender, args[0]);
                if (player != null)
                {
                    player.setFire(parseIntWithMin(sender, args[1], 0));
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
            OutputHandler.chatError(sender, "Improper syntax. Please try this instead: [me|<player>]");
        }
    }

    @Override
    public void processCommandConsole(ICommandSender sender, String[] args)
    {
        int time = 15;
        if (args.length == 2)
        {
            time = parseIntWithMin(sender, args[1], 0);
        }
        EntityPlayerMP player = FunctionHelper.getPlayerForName(sender, args[0]);
        if (player != null)
        {
            player.setFire(time);
            ChatUtils.sendMessage(sender, "You should feel bad about doing that.");
        }
        else
        {
            ChatUtils.sendMessage(sender, String.format("Player %s does not exist, or is not online.", args[0]));
        }
    }

    @Override
    public void registerExtraPermissions(IPermRegisterEvent event)
    {
        event.registerPermissionLevel(getCommandPerm() + ".others", RegGroup.OWNERS);
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
    public RegGroup getReggroup()
    {
        return RegGroup.OWNERS;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {

        return "/burn <player> Set a player on fire.";
    }
}
