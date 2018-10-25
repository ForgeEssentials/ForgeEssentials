package com.forgeessentials.client.mixin;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@SideOnly(Side.CLIENT)
@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer implements IResourceManagerReloadListener
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
            if (this.mc.world != null)
            {
                this.mc.mcProfiler.startSection("pick");
                this.mc.pointedEntity = null;
                double maxReach = this.mc.playerController.getBlockReachDistance();
                this.mc.objectMouseOver = entity.rayTrace(maxReach, partialTime);
                double blockDistance = maxReach;
                Vec3d vec3 = entity.getPositionEyes(partialTime);

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

                Vec3d vec31 = entity.getLook(partialTime);
                Vec3d vec32 = vec3.addVector(vec31.x * maxReach, vec31.y * maxReach, vec31.z * maxReach);
                this.pointedEntity = null;
                Vec3d vec33 = null;
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

                    if (this.pointedEntity instanceof EntityLivingBase || this.pointedEntity instanceof EntityItemFrame)
                    {
                        this.mc.pointedEntity = this.pointedEntity;
                    }
                }

                this.mc.mcProfiler.endSection();
            }
        }
    }

}
