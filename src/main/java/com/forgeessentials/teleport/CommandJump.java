package com.forgeessentials.teleport;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;

import com.forgeessentials.commons.selections.WarpPoint;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.TeleportHelper;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.util.PlayerUtil;

public class CommandJump extends ForgeEssentialsCommandBase
{

    public CommandJump()
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public String getPrimaryAlias()
    {
        return "jump";
    }
    
    @Override
    public String getUsage(ICommandSender sender)
    {
        return "/jump Teleport to the location you are looking at";
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
        return TeleportModule.PERM_JUMP;
    }

    @Override
    public void processCommandPlayer(MinecraftServer server, EntityPlayerMP player, String[] args) throws CommandException
    {
        jump(player);
    }

    public void jump(EntityPlayerMP player) throws CommandException
    {
        RayTraceResult mo = PlayerUtil.getPlayerLookingSpot(player, 500);
        if (mo == null)
            throw new TranslatedCommandException("The spot you are looking at is too far away to teleport.");
        BlockPos pos = mo.getBlockPos();
        TeleportHelper.teleport(player, new WarpPoint(player.getEntityWorld().provider.getDimension(), pos.getX(), pos.getY() + 1, pos.getZ(),
                player.rotationPitch, player.rotationYaw));
    }

    @SubscribeEvent
    public void playerInteractEvent(PlayerInteractEvent event)
    {
        if (!(event.getEntityPlayer() instanceof EntityPlayerMP))
            return;
        if (!(event instanceof PlayerInteractEvent.RightClickEmpty) && !(event instanceof PlayerInteractEvent.RightClickBlock))
            return;
        ItemStack stack = event.getEntityPlayer().getHeldItemMainhand();
        if (stack == null || stack.getItem() != Items.COMPASS)
            return;
        if (!PermissionAPI.hasPermission(event.getEntityPlayer(), TeleportModule.PERM_JUMP_TOOL))
            return;

        try
        {
            jump((EntityPlayerMP) event.getEntityPlayer());
        }
        catch (CommandException ce)
        {
            TextComponentTranslation msg = new TextComponentTranslation(ce.getMessage(), ce.getErrorObjects());
            msg.getStyle().setColor(TextFormatting.RED);
            event.getEntityPlayer().sendMessage(msg);
        }
    }

}
