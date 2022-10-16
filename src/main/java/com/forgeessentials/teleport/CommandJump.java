package com.forgeessentials.teleport;

import net.minecraft.command.CommandException;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;

import com.forgeessentials.commons.selections.WarpPoint;
import com.forgeessentials.core.misc.TeleportHelper;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.util.PlayerUtil;

public class CommandJump extends BaseCommand
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
    public void processCommandPlayer(MinecraftServer server, ServerPlayerEntity player, String[] args) throws CommandException
    {
        jump(player);
    }

    public void jump(ServerPlayerEntity player) throws CommandException
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
        if (!(event.getEntityPlayer() instanceof ServerPlayerEntity))
            return;
        if (!(event instanceof PlayerInteractEvent.RightClickEmpty) && !(event instanceof PlayerInteractEvent.RightClickBlock))
            return;
        ItemStack stack = event.getEntityPlayer().getHeldItemMainhand();
        if (stack == ItemStack.EMPTY || stack.getItem() != Items.COMPASS)
            return;
        if (!PermissionAPI.hasPermission(event.getEntityPlayer(), TeleportModule.PERM_JUMP_TOOL))
            return;

        try
        {
            jump((ServerPlayerEntity) event.getEntityPlayer());
        }
        catch (CommandException ce)
        {
            TranslationTextComponent msg = new TranslationTextComponent(ce.getMessage(), ce.getErrorObjects());
            msg.getStyle().setColor(TextFormatting.RED);
            event.getPlayer().sendMessage(msg);
        }
    }

}
