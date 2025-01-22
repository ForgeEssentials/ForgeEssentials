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
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fe.event.player.PlayerPostInteractEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;



@Mixin(ItemInWorldManager.class)
public abstract class MixinItemInWorldManager
{

    @Shadow
    private EntityPlayerMP thisPlayerMP;

    @Shadow
    private World theWorld;

    @Shadow
    private WorldSettings.GameType gameType;

    @Shadow
    abstract boolean isCreative();

    /**
     * Fixes a few Forge bugs, and adds PlayerPostInteractEvent.
     */
	@Inject(method = "activateBlockOrUseItem(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;Lnet/minecraft/util/BlockPos;Lnet/minecraft/util/EnumFacing;FFF)Z", at = @At("HEAD"), cancellable = true)
    public void activateBlockOrUseItem(EntityPlayer player, World worldIn, ItemStack stack, BlockPos pos, EnumFacing side, float offsetX, float offsetY, float offsetZ, CallbackInfoReturnable<Boolean> callback)
    {
		if (this.gameType == WorldSettings.GameType.SPECTATOR)
        {
            TileEntity tileentity = worldIn.getTileEntity(pos);

            if (tileentity instanceof ILockableContainer)
            {
                Block block = worldIn.getBlockState(pos).getBlock();
                ILockableContainer ilockablecontainer = (ILockableContainer)tileentity;

                if (ilockablecontainer instanceof TileEntityChest && block instanceof BlockChest)
                {
                    ilockablecontainer = ((BlockChest)block).getLockableContainer(worldIn, pos);
                }

                if (ilockablecontainer != null)
                {
                    player.displayGUIChest(ilockablecontainer);
                    callback.setReturnValue(true);
                    return;
                }
            }
            else if (tileentity instanceof IInventory)
            {
                player.displayGUIChest((IInventory)tileentity);
                callback.setReturnValue(true);
                return;
            }

            callback.setReturnValue(false);
            return;
        }
        else
        {
            net.minecraftforge.event.entity.player.PlayerInteractEvent event = net.minecraftforge.event.ForgeEventFactory.onPlayerInteract(player,
                    net.minecraftforge.event.entity.player.PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK, worldIn, pos, side, new net.minecraft.util.Vec3(offsetX, offsetY, offsetZ));
            if (event.isCanceled())
            {
            	// PATCH: Fix a Forge bug related to fake players
                if (thisPlayerMP.playerNetServerHandler != null)
                    thisPlayerMP.playerNetServerHandler.sendPacket(new S23PacketBlockChange(theWorld, pos));
                callback.setReturnValue(false);
                return;
            }
         // PATCH: Fix a Forge bug allowing onItemUseFirst to trigger even if event.useItem is set to DENY
            if (event.useItem != Result.DENY && stack != null && stack.getItem().onItemUseFirst(stack, player, worldIn, pos, side, offsetX, offsetY, offsetZ))
            {
             // PATCH: Add event to get actual result of interaction
                MinecraftForge.EVENT_BUS.post(new PlayerPostInteractEvent(player, worldIn, stack, pos, side, offsetX, offsetY, offsetZ));
                if (stack.stackSize <= 0)
                    ForgeEventFactory.onPlayerDestroyItem(thisPlayerMP, stack);
                callback.setReturnValue(true);
                return;
            }

            IBlockState iblockstate = worldIn.getBlockState(pos);
            boolean isAir = worldIn.isAirBlock(pos);
            boolean useBlock = !player.isSneaking() || player.getHeldItem() == null;
            if (!useBlock) useBlock = player.getHeldItem().getItem().doesSneakBypassUse(worldIn, pos, player);
            boolean result = false;

            if (useBlock)
            {
                if (event.useBlock != net.minecraftforge.fml.common.eventhandler.Event.Result.DENY)
                {
                    result = iblockstate.getBlock().onBlockActivated(worldIn, pos, iblockstate, player, side, offsetX, offsetY, offsetZ);
                    if (result)
                        MinecraftForge.EVENT_BUS.post(new PlayerPostInteractEvent(player, worldIn, iblockstate, pos, side, offsetX, offsetY, offsetZ));
                }
                else
                {
                	// PATCH: Fix a Forge bug related to fake players
                    if (thisPlayerMP.playerNetServerHandler != null)
                        thisPlayerMP.playerNetServerHandler.sendPacket(new S23PacketBlockChange(theWorld, pos));
                    result = event.useItem != net.minecraftforge.fml.common.eventhandler.Event.Result.ALLOW;
                }
            }
            if (stack != null && !result && event.useItem != net.minecraftforge.fml.common.eventhandler.Event.Result.DENY)
            {
                int meta = stack.getMetadata();
                int size = stack.stackSize;
                result = stack.onItemUse(player, worldIn, pos, side, offsetX, offsetY, offsetZ);
                if (isCreative())
                {
                    stack.setItemDamage(meta);
                    stack.stackSize = size;
                }
                if (stack.stackSize <= 0)
                    ForgeEventFactory.onPlayerDestroyItem(thisPlayerMP, stack);
                if (result)
                    MinecraftForge.EVENT_BUS.post(new PlayerPostInteractEvent(player, worldIn, stack, pos, side, offsetX, offsetY, offsetZ));
            }
            callback.setReturnValue(result);
            return;
        }
    }
}
