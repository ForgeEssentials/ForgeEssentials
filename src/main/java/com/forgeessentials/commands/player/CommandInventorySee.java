package com.forgeessentials.commands.player;

import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.api.UserIdent;
import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.commands.util.PlayerInvChest;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;

/**
 * Opens other player inventory.
 */
public class CommandInventorySee extends ForgeEssentialsCommandBase
{

    public CommandInventorySee()
    {
    }

    @Override
    public String getPrimaryAlias()
    {
        return "invsee";
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
        return ModuleCommands.PERM + ".invsee";
    }

    @Override
    public void processCommandPlayer(MinecraftServer server, ServerPlayerEntity sender, String[] args) throws CommandException
    {
        if (args[0] == null)
            throw new TranslatedCommandException("You need to specify a player!");

        if (!FMLEnvironment.dist.isDedicatedServer())
        {
            return;
        }
        ServerPlayerEntity player = sender;
        ServerPlayerEntity victim = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);
        if (victim == null)
            throw new TranslatedCommandException("Player %s not found.", args[0]);

        if (player.containerMenu != player.inventoryMenu)
        {
            player.closeContainer();;
        }
        player.nextContainerCounter();;

        PlayerInvChest chest = new PlayerInvChest(victim, sender);
        player.displayGUIChest(chest);
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
