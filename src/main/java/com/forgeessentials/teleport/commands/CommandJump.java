package com.forgeessentials.teleport.commands;

import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.commons.selections.WarpPoint;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.TeleportHelper;
import com.forgeessentials.teleport.TeleportModule;
import com.forgeessentials.util.PlayerUtil;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class CommandJump extends ForgeEssentialsCommandBuilder
{

    public CommandJump(boolean enabled)
    {
        super(enabled);
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
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        return baseBuilder
                .executes(CommandContext -> execute(CommandContext, "blank")
                        );
    }

    @Override
    public int processCommandPlayer(CommandContext<CommandSource> ctx, String params) throws CommandSyntaxException
    {
        jump(getServerPlayer(ctx.getSource()));
        return Command.SINGLE_SUCCESS;
    }

    public void jump(ServerPlayerEntity player) throws CommandException
    {
        RayTraceResult mo = PlayerUtil.getPlayerLookingSpot(player, 500);
        if (mo == null) {
        	ChatOutputHandler.chatError(player, "The spot you are looking at is too far away to teleport.");
        	return;
		}

        BlockPos pos = new BlockPos(mo.getLocation().x, mo.getLocation().y, mo.getLocation().z);
        pos.offset(0, 1, 0);
        TeleportHelper.teleport(player, new WarpPoint(player.level.dimension(), pos, player.xRot, player.yRot));
    }

    @SubscribeEvent
    public void playerInteractEvent(PlayerInteractEvent event)
    {
        if (!(event.getPlayer() instanceof ServerPlayerEntity))
            return;
        if (!(event instanceof PlayerInteractEvent.RightClickEmpty) && !(event instanceof PlayerInteractEvent.RightClickBlock))
            return;
        ItemStack stack = event.getPlayer().getMainHandItem();
        if (stack == ItemStack.EMPTY || stack.getItem() != Items.COMPASS)
            return;
        if (!hasPermission(event.getPlayer(), TeleportModule.PERM_JUMP_TOOL))
            return;

        try
        {
            jump((ServerPlayerEntity) event.getPlayer());
        }
        catch (Exception e)
        {
        	e.printStackTrace();
            StringTextComponent msg = new StringTextComponent(e.getCause() + e.getMessage());
            msg.getStyle().withColor(TextFormatting.RED);
            event.getPlayer().sendMessage(msg,event.getPlayer().getUUID());
        }
    }

}
