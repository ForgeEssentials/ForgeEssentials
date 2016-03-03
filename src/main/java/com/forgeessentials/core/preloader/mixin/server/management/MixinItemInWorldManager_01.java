package com.forgeessentials.core.preloader.mixin.server.management;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.server.management.ItemInWorldManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fe.event.player.PlayerPostInteractEvent;
import net.minecraftforge.fml.common.eventhandler.Event;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ItemInWorldManager.class)
public abstract class MixinItemInWorldManager_01
{

    @Shadow
    private EntityPlayerMP thisPlayerMP;

    @Shadow
    private World theWorld;

    @Shadow
    private WorldSettings.GameType gameType;

    @Shadow
    abstract boolean isCreative();

    @Overwrite
    public boolean activateBlockOrUseItem(EntityPlayer player, World world, ItemStack item, BlockPos pos, EnumFacing side, float dx, float dy, float dz)
    {
        if (this.gameType == WorldSettings.GameType.SPECTATOR)
        {
            TileEntity tileentity = world.getTileEntity(pos);

            if (tileentity instanceof ILockableContainer)
            {
                Block block = world.getBlockState(pos).getBlock();
                ILockableContainer ilockablecontainer = (ILockableContainer) tileentity;

                if (ilockablecontainer instanceof TileEntityChest && block instanceof BlockChest)
                {
                    ilockablecontainer = ((BlockChest) block).getLockableContainer(world, pos);
                }

                if (ilockablecontainer != null)
                {
                    player.displayGUIChest(ilockablecontainer);
                    return true;
                }
            }
            else if (tileentity instanceof IInventory)
            {
                player.displayGUIChest((IInventory) tileentity);
                return true;
            }

            return false;
        }
        else
        {
            net.minecraftforge.event.entity.player.PlayerInteractEvent event = net.minecraftforge.event.ForgeEventFactory.onPlayerInteract(player,
                    net.minecraftforge.event.entity.player.PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK, world, pos, side);
            if (event.isCanceled())
            {
                // PATCH: Fix a Forge bug related to fake players
                if (thisPlayerMP.playerNetServerHandler != null)
                    thisPlayerMP.playerNetServerHandler.sendPacket(new S23PacketBlockChange(theWorld, pos));
                return false;
            }

            // PATCH: Fix a Forge bug allowing onItemUseFirst to trigger even if event.useItem is set to DENY
            if (item != null && event.useItem != Event.Result.DENY && item.getItem().onItemUseFirst(item, player, world, pos, side, dx, dy, dz))
            {
                // PATCH: Add event to get actual result of interaction
                MinecraftForge.EVENT_BUS.post(new PlayerPostInteractEvent(player, world, item, pos, side, dx, dy, dz));
                if (item.stackSize <= 0)
                    net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(thisPlayerMP, item);
                return true;
            }

            IBlockState blackState = world.getBlockState(pos);
            // boolean isAir = world.isAirBlock(pos);
            boolean useBlock = !player.isSneaking() || player.getHeldItem() == null;
            if (!useBlock)
                useBlock = player.getHeldItem().getItem().doesSneakBypassUse(world, pos, player);
            boolean result = false;

            if (useBlock)
            {
                if (event.useBlock != net.minecraftforge.fml.common.eventhandler.Event.Result.DENY)
                {
                    result = blackState.getBlock().onBlockActivated(world, pos, blackState, player, side, dx, dy, dz);
                    // PATCH: Add event to get actual result of interaction
                    if (result)
                        MinecraftForge.EVENT_BUS.post(new PlayerPostInteractEvent(player, world, blackState, pos, side, dx, dy, dz));
                }
                else
                {
                    thisPlayerMP.playerNetServerHandler.sendPacket(new S23PacketBlockChange(theWorld, pos));
                    result = event.useItem != net.minecraftforge.fml.common.eventhandler.Event.Result.ALLOW;
                }
            }
            if (item != null && !result && event.useItem != net.minecraftforge.fml.common.eventhandler.Event.Result.DENY)
            {
                int meta = item.getMetadata();
                int size = item.stackSize;
                result = item.onItemUse(player, world, pos, side, dx, dy, dz);
                if (isCreative())
                {
                    item.setItemDamage(meta);
                    item.stackSize = size;
                }
                if (item.stackSize <= 0)
                    net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(thisPlayerMP, item);
                // PATCH: Add event to get actual result of interaction
                if (result)
                    MinecraftForge.EVENT_BUS.post(new PlayerPostInteractEvent(player, world, item, pos, side, dx, dy, dz));
            }
            return result;
        }
    }

}
