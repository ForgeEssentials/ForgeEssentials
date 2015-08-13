package com.forgeessentials.core.preloader.mixin.server.management;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.server.management.ItemInWorldManager;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.fe.event.player.PlayerPostInteractEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import cpw.mods.fml.common.eventhandler.Event;

@Mixin(ItemInWorldManager.class)
public abstract class MixinItemInWorldManager_01
{

    @Shadow
    private EntityPlayerMP thisPlayerMP;

    @Shadow
    private World theWorld;

    @Shadow
    abstract boolean isCreative();

    @Overwrite
    public boolean activateBlockOrUseItem(EntityPlayer player, World world, ItemStack item, int x, int y, int z, int side, float dx, float dy, float dz)
    {
        PlayerInteractEvent event = ForgeEventFactory.onPlayerInteract(player, Action.RIGHT_CLICK_BLOCK, x, y, z, side, world);
        if (event.isCanceled())
        {
            // PATCH: Fix a Forge bug related to fake players
            if (thisPlayerMP.playerNetServerHandler != null)
                thisPlayerMP.playerNetServerHandler.sendPacket(new S23PacketBlockChange(x, y, z, theWorld));
            return false;
        }

        // PATCH: Fix a Forge bug allowing onItemUseFirst to trigger even if event.useItem is set to DENY
        if (event.useItem != Event.Result.DENY && item != null && item.getItem().onItemUseFirst(item, player, world, x, y, z, side, dx, dy, dz))
        {
            // PATCH: Add event to get actual result of interaction
            MinecraftForge.EVENT_BUS.post(new PlayerPostInteractEvent(player, world, item, x, y, z, side, dx, dy, dz));
            if (item.stackSize <= 0)
                ForgeEventFactory.onPlayerDestroyItem(thisPlayerMP, item);
            return true;
        }

        Block block = world.getBlock(x, y, z);
        boolean useBlock = !player.isSneaking() || player.getHeldItem() == null;
        if (!useBlock)
            useBlock = player.getHeldItem().getItem().doesSneakBypassUse(world, x, y, z, player);
        boolean result = false;

        if (useBlock)
        {
            if (event.useBlock != Event.Result.DENY)
            {
                result = block.onBlockActivated(world, x, y, z, player, side, dx, dy, dz);
                // PATCH: Add event to get actual result of interaction
                if (result)
                    MinecraftForge.EVENT_BUS.post(new PlayerPostInteractEvent(player, world, block, x, y, z, side, dx, dy, dz));
            }
            else
            {
                // PATCH: Fix a Forge bug related to fake players
                if (thisPlayerMP.playerNetServerHandler != null)
                    thisPlayerMP.playerNetServerHandler.sendPacket(new S23PacketBlockChange(x, y, z, theWorld));
                result = event.useItem != Event.Result.ALLOW;
            }
        }

        if (item != null && !result && event.useItem != Event.Result.DENY)
        {
            int meta = item.getItemDamage();
            int size = item.stackSize;
            result = item.tryPlaceItemIntoWorld(player, world, x, y, z, side, dx, dy, dz);
            if (isCreative())
            {
                item.setItemDamage(meta);
                item.stackSize = size;
            }
            if (item.stackSize <= 0)
                ForgeEventFactory.onPlayerDestroyItem(thisPlayerMP, item);
            if (result)
                MinecraftForge.EVENT_BUS.post(new PlayerPostInteractEvent(player, world, item, x, y, z, side, dx, dy, dz));
        }

        return result;
    }
}
