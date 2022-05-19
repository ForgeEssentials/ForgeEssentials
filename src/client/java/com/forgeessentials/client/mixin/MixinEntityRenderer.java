package com.forgeessentials.client.mixin;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.resource.ISelectiveResourceReloadListener;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@OnlyIn(Dist.CLIENT)
@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer implements ISelectiveResourceReloadListener
{

    @Final
    private Minecraft mc;

    @Shadow
    private Entity pointedEntity;

    @Overwrite
    public void getMouseOver(float partialTime)
    {
        Entity entity = this.mc.getRenderViewEntity();

        if (entity != null)
        {
            if (this.mc.level != null)
            {
                this.mc.mcProfiler.startSection("pick");
                this.mc.pointedEntity = null;
                double maxReach = this.mc.playerController.getBlockReachDistance();
                this.mc.objectMouseOver = entity.rayTrace(maxReach, partialTime);
                double blockDistance = maxReach;
                Vector3d vec3 = entity.getPositionEyes(partialTime);

                if (this.mc.playerController.extendedReach())
                {
                    maxReach = 6.0D;
                    blockDistance = 6.0D;
                }
                else
                {
                    if (maxReach > 3.0D)
                    {
                        blockDistance = 3.0D;
                    }

                    maxReach = blockDistance;
                }

                if (this.mc.objectMouseOver != null)
                {
                    blockDistance = this.mc.objectMouseOver.hitVec.distanceTo(vec3);
                }

                Vector3d vec31 = entity.getLook(partialTime);
                Vector3d vec32 = vec3.addVector(vec31.x * maxReach, vec31.y * maxReach, vec31.z * maxReach);
                this.pointedEntity = null;
                Vector3d vec33 = null;
                float f1 = 1.0F;
                List<?> list = this.mc.world.getEntitiesWithinAABBExcludingEntity(entity,
                        entity.getEntityBoundingBox().expand(vec31.x * maxReach, vec31.y * maxReach, vec31.z * maxReach).grow(f1, f1, f1));
                double d2 = blockDistance;

                for (int i = 0; i < list.size(); ++i)
                {
                    Entity entity1 = (Entity) list.get(i);

                    if (entity1.canBeCollidedWith())
                    {
                        float f2 = entity1.getCollisionBorderSize();
                        AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().grow(f2, f2, f2);
                        RayTraceResult movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec32);

                        if (axisalignedbb.contains(vec3))
                        {
                            if (0.0D < d2 || d2 == 0.0D)
                            {
                                this.pointedEntity = entity1;
                                vec33 = movingobjectposition == null ? vec3 : movingobjectposition.hitVec;
                                d2 = 0.0D;
                            }
                        }
                        else if (movingobjectposition != null)
                        {
                            double d3 = vec3.distanceTo(movingobjectposition.hitVec);

                            if (d3 < d2 || d2 == 0.0D)
                            {
                                if (entity1 == entity.getRidingEntity() && !entity.canRiderInteract())
                                {
                                    if (d2 == 0.0D)
                                    {
                                        this.pointedEntity = entity1;
                                        vec33 = movingobjectposition.hitVec;
                                    }
                                }
                                else
                                {
                                    this.pointedEntity = entity1;
                                    vec33 = movingobjectposition.hitVec;
                                    d2 = d3;
                                }
                            }
                        }
                    }
                }

                if (this.pointedEntity != null && (d2 < blockDistance || this.mc.objectMouseOver == null))
                {
                    this.mc.objectMouseOver = new RayTraceResult(this.pointedEntity, vec33);

                    if (this.pointedEntity instanceof LivingEntity || this.pointedEntity instanceof ItemFrameEntity)
                    {
                        this.mc.pointedEntity = this.pointedEntity;
                    }
                }

                this.mc.mcProfiler.endSection();
            }
        }
    }

}
