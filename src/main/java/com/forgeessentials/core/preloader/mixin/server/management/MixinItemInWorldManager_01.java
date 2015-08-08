package com.forgeessentials.core.preloader.mixin.server.management;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.server.management.ItemInWorldManager;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;

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
    public boolean activateBlockOrUseItem(EntityPlayer p_73078_1_, World p_73078_2_, ItemStack p_73078_3_, int p_73078_4_, int p_73078_5_, int p_73078_6_, int p_73078_7_, float p_73078_8_, float p_73078_9_, float p_73078_10_)
    {
        PlayerInteractEvent event = ForgeEventFactory
                .onPlayerInteract(p_73078_1_, Action.RIGHT_CLICK_BLOCK, p_73078_4_, p_73078_5_, p_73078_6_, p_73078_7_, p_73078_2_);
        if (event.isCanceled())
        {
            thisPlayerMP.playerNetServerHandler.sendPacket(new S23PacketBlockChange(p_73078_4_, p_73078_5_, p_73078_6_, theWorld));
            return false;
        }

        if (event.useItem != Event.Result.DENY && p_73078_3_ != null && p_73078_3_.getItem().onItemUseFirst(p_73078_3_, p_73078_1_, p_73078_2_, p_73078_4_, p_73078_5_, p_73078_6_, p_73078_7_, p_73078_8_, p_73078_9_, p_73078_10_))
        {
            if (p_73078_3_.stackSize <= 0) ForgeEventFactory.onPlayerDestroyItem(thisPlayerMP, p_73078_3_);
            return true;
        }

        Block block = p_73078_2_.getBlock(p_73078_4_, p_73078_5_, p_73078_6_);
        boolean isAir = block.isAir(p_73078_2_, p_73078_4_, p_73078_5_, p_73078_6_);
        boolean useBlock = !p_73078_1_.isSneaking() || p_73078_1_.getHeldItem() == null;
        if (!useBlock) useBlock = p_73078_1_.getHeldItem().getItem().doesSneakBypassUse(p_73078_2_, p_73078_4_, p_73078_5_, p_73078_6_, p_73078_1_);
        boolean result = false;

        if (useBlock)
        {
            if (event.useBlock != Event.Result.DENY)
            {
                result = block.onBlockActivated(p_73078_2_, p_73078_4_, p_73078_5_, p_73078_6_, p_73078_1_, p_73078_7_, p_73078_8_, p_73078_9_, p_73078_10_);
            }
            else
            {
                thisPlayerMP.playerNetServerHandler.sendPacket(new S23PacketBlockChange(p_73078_4_, p_73078_5_, p_73078_6_, theWorld));
                result = event.useItem != Event.Result.ALLOW;
            }
        }

        if (p_73078_3_ != null && !result && event.useItem != Event.Result.DENY)
        {
            int meta = p_73078_3_.getItemDamage();
            int size = p_73078_3_.stackSize;
            result = p_73078_3_.tryPlaceItemIntoWorld(p_73078_1_, p_73078_2_, p_73078_4_, p_73078_5_, p_73078_6_, p_73078_7_, p_73078_8_, p_73078_9_, p_73078_10_);
            if (isCreative())
            {
                p_73078_3_.setItemDamage(meta);
                p_73078_3_.stackSize = size;
            }
            if (p_73078_3_.stackSize <= 0) ForgeEventFactory.onPlayerDestroyItem(thisPlayerMP, p_73078_3_);
        }

        /* Re-enable if this causes bukkit incompatibility, or re-write client side to only send a single packet per right click.
        if (par3ItemStack != null && ((!result && event.useItem != Event.Result.DENY) || event.useItem == Event.Result.ALLOW))
        {
            this.tryUseItem(thisPlayerMP, par2World, par3ItemStack);
        }*/
        return result;
    }
}
