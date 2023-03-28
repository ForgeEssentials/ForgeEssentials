package com.forgeessentials.core.preloader.mixin.entity;
//
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//
//import net.minecraft.entity.item.ItemFrameEntity;
//import net.minecraft.util.DamageSource;
//import net.minecraftforge.common.MinecraftForge;
//import net.minecraftforge.fe.event.entity.EntityAttackedEvent;
//
//@Mixin(ItemFrameEntity.class)
//public class MixinItemFrameEntity
//{
//    /**
//     * Solve for item frame bow killing
//     * @author Maximuslotro
//     * @reason stuff
//     */
//    @Inject(at = @At("HEAD"), 
//            method = "Lnet/minecraft/entity/item/ItemFrameEntity;hurt(Lnet/minecraft/util/DamageSource;F)Z", cancellable = true)
//    public void hurt(DamageSource source, float amount, CallbackInfoReturnable<Boolean> callback) {
//        EntityAttackedEvent event = new EntityAttackedEvent((ItemFrameEntity) (Object) this, source, amount);
//        if (MinecraftForge.EVENT_BUS.post(event))
//        {
//            callback.setReturnValue(event.result);
//            //callback.cancel();
//        }
//        amount = event.getDamage();
//    }
//}
