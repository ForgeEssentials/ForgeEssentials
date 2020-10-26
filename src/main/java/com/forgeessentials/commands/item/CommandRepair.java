package com.forgeessentials.commands.item;

import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;

public class CommandRepair extends ForgeEssentialsCommandBase
{

    @Override
    public String getPrimaryAlias()
    {
        return "repair";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        if (sender instanceof EntityPlayer)
        {
            return "/repair [player] Repairs the item you or another player is holding.";
        }
        else
        {
            return "/repair <player> Repairs the item the player is holding.";
        }

    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.OP;
    }

    @Override
    public String getPermissionNode()
    {
        return ModuleCommands.PERM + ".repair";
    }

    @Override
    public void registerExtraPermissions()
    {
        APIRegistry.perms.registerPermission(getPermissionNode() + ".others", DefaultPermissionLevel.OP, "Allows repairing items held by another player");
    }

    @Override
    public void processCommandPlayer(MinecraftServer server, EntityPlayerMP sender, String[] args) throws CommandException
    {
        if (args.length == 0)
        {
            ItemStack item = sender.getHeldItemMainhand();
            if (item == null)
                throw new TranslatedCommandException("You are not holding a reparable item.");
            item.setItemDamage(0);
        }
        else if (args.length == 1 && PermissionAPI.hasPermission(sender, getPermissionNode() + ".others"))
        {
            EntityPlayerMP player = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);
            if (player == null)
                throw new TranslatedCommandException("Player %s does not exist, or is not online.", args[0]);

            ItemStack item = player.getHeldItemMainhand();
            if (item != null)
                item.setItemDamage(0);
        }
        else
        {
            throw new TranslatedCommandException(getUsage(sender));
        }
    }

    @Override
    public void processCommandConsole(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length == 1)
        {
            // PlayerSelector.matchPlayers(sender, args[0])
            EntityPlayerMP player = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);
            if (player != null)
            {

                ItemStack item = player.getHeldItemMainhand();

                if (item != null)
                {
                    item.setItemDamage(0);
                }

            }
            else
                throw new TranslatedCommandException("Player %s does not exist, or is not online.", args[0]);
        }
        else
            throw new TranslatedCommandException(getUsage(sender));
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos)
    {
        if (args.length == 1)
        {
            return matchToPlayers(args);
        }
        else
        {
            return null;
        }
    }

}
