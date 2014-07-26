package com.forgeessentials.commands;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.RegGroup;
import com.forgeessentials.api.permissions.query.PermQueryPlayer;
import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.util.ChatUtils;
import com.forgeessentials.util.FunctionHelper;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.DamageSource;

import java.util.List;

public class CommandKill extends FEcmdModuleCommands {

    @Override
    public String getCommandName()
    {
        return "kill";
    }

    @Override
    public void processCommandPlayer(EntityPlayer sender, String[] args)
    {
        if (args.length >= 1 && APIRegistry.perms.checkPermAllowed(new PermQueryPlayer(sender, getCommandPerm() + ".others")))
        {
            EntityPlayerMP player = FunctionHelper.getPlayerForName(sender, args[0]);
            if (player != null)
            {
                player.attackEntityFrom(DamageSource.outOfWorld, 1000);
                ChatUtils.sendMessage(player, "You were killed. You probably deserved it.");
            }
            else
            {
                ChatUtils.sendMessage(sender, String.format("Player %s does not exist, or is not online.", args[0]));
            }
        }
        else
        {
            sender.attackEntityFrom(DamageSource.outOfWorld, 1000);
            ChatUtils.sendMessage(sender, "You were killed. You probably deserved it.");
        }
    }

    @Override
    public void processCommandConsole(ICommandSender sender, String[] args)
    {
        if (args.length >= 1)
        {
            EntityPlayerMP player = FunctionHelper.getPlayerForName(sender, args[0]);
            if (player != null)
            {
                player.attackEntityFrom(DamageSource.outOfWorld, 1000);
                ChatUtils.sendMessage(player, "You were killed. You probably deserved it.");
            }
            else
            {
                ChatUtils.sendMessage(sender, String.format("Player %s does not exist, or is not online.", args[0]));
            }
        }
        else
        {
            ChatUtils.sendMessage(sender, "Improper syntax. Please try this instead: <player>");
        }
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public void registerExtraPermissions()
    {
        APIRegistry.permReg.registerPermissionLevel(getCommandPerm() + ".others", RegGroup.OWNERS);
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

        return "/kill <player> Commit suicide, or kill other players (with permission)";
    }

}
