package com.forgeessentials.teleport;

import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.permission.PermissionLevel;
import net.minecraftforge.permission.PermissionManager;

import com.forgeessentials.api.UserIdent;
import com.forgeessentials.commons.selections.WarpPoint;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.TeleportHelper;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.util.PlayerInfo;



public class CommandBed extends ForgeEssentialsCommandBase
{

    public CommandBed()
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public String getCommandName()
    {
        return "febed";
    }

    @Override
    public String[] getDefaultAliases()
    {
        return new String[] { "bed" };
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {

        return "/bed [player]: Teleport you or another player to the bed last used.";
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public PermissionLevel getPermissionLevel()
    {
        return PermissionLevel.TRUE;
    }

    @Override
    public String getPermissionNode()
    {
        return TeleportModule.PERM_BED;
    }

    @Override
    public void processCommandPlayer(EntityPlayerMP sender, String[] args) throws CommandException
    {
        if (args.length >= 1 && PermissionManager.checkPermission(sender, TeleportModule.PERM_BED_OTHERS))
        {
            EntityPlayerMP player = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);
            if (player != null)
            {
                tp(player);
            }
            else
                throw new TranslatedCommandException("Player %s does not exist, or is not online.", args[0]);
        }
        else
        {
            tp(sender);
        }
    }

    private void tp(EntityPlayerMP player) throws CommandException
    {
        World world = player.worldObj;
        if (!world.provider.canRespawnHere())
            world = DimensionManager.getWorld(0);

        BlockPos spawn = player.getBedLocation(world.provider.getDimensionId());
        if (spawn == null && world.provider.getDimensionId() != 0)
        {
            world = DimensionManager.getWorld(0);
            spawn = player.getBedLocation(world.provider.getDimensionId());
        }
        if (spawn == null)
            throw new TranslatedCommandException("No bed found.");

        spawn = EntityPlayer.getBedSpawnLocation(player.worldObj, spawn, true);
        if (spawn == null)
            throw new TranslatedCommandException("Your bed has been obstructed.");

        PlayerInfo.get(player.getPersistentID()).setLastTeleportOrigin(new WarpPoint(player));
        WarpPoint spawnPoint = new WarpPoint(world.provider.getDimensionId(), spawn, player.rotationPitch, player.rotationYaw);
        TeleportHelper.teleport(player, spawnPoint);
    }

    @Override
    public void processCommandConsole(ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length >= 1)
        {
            EntityPlayerMP player = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);
            if (player != null)
            {
                tp(player);
            }
            else
                throw new TranslatedCommandException("Player %s does not exist, or is not online.", args[0]);
        }
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos)
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

}
