package com.forgeessentials.teleport;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.permissions.PermissionsManager;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.UserIdent;
import com.forgeessentials.commons.selections.WarpPoint;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.TeleportHelper;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.util.PlayerInfo;

import cpw.mods.fml.common.FMLCommonHandler;

public class CommandBed extends ForgeEssentialsCommandBase {

    public CommandBed()
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public String getCommandName()
    {
        return "bed";
    }

    @Override
    public void processCommandPlayer(EntityPlayerMP sender, String[] args)
    {
        if (args.length >= 1 && PermissionsManager.checkPermission(sender, TeleportModule.PERM_BED_OTHERS))
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

    private void tp(EntityPlayerMP player)
    {
        World world = player.worldObj;
        if (!world.provider.canRespawnHere())
            world = DimensionManager.getWorld(0);

        ChunkCoordinates spawn = player.getBedLocation(world.provider.dimensionId);
        if (spawn == null && world.provider.dimensionId != 0)
        {
            world = DimensionManager.getWorld(0);
            spawn = player.getBedLocation(world.provider.dimensionId);
        }
        if (spawn == null)
            throw new TranslatedCommandException("No bed found.");

        spawn = EntityPlayer.verifyRespawnCoordinates(player.worldObj, spawn, true);
        if (spawn == null)
            throw new TranslatedCommandException("Your bed has been obstructed.");

        PlayerInfo.getPlayerInfo(player.getPersistentID()).setLastTeleportOrigin(new WarpPoint(player));
        WarpPoint spawnPoint = new WarpPoint(world.provider.dimensionId, spawn, player.rotationPitch, player.rotationYaw);
        TeleportHelper.teleport(player, spawnPoint);
    }

    @Override
    public void processCommandConsole(ICommandSender sender, String[] args)
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
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public String getPermissionNode()
    {
        return TeleportModule.PERM_BED;
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
        return RegisteredPermValue.TRUE;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {

        return "/bed [player] Teleport you or another player to the bed last used.";
    }
}
