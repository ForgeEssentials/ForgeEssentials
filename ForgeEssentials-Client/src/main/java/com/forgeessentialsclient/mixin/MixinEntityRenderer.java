package com.forgeessentialsclient.mixin;


import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.resource.ISelectiveResourceReloadListener;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@OnlyIn(Dist.CLIENT)
@Mixin(GameRenderer.class)
public abstract class MixinEntityRenderer implements ISelectiveResourceReloadListener
{

    @Final
    private Minecraft minecraft;

    @Shadow private Entity crosshairPickEntity;

    @Overwrite
    public void pick(float idkWhatThisIs) {
        Entity entity = this.minecraft.getCameraEntity();
        if (entity != null) {
           if (this.minecraft.level != null) {
              this.minecraft.getProfiler().push("pick");
              this.minecraft.crosshairPickEntity = null;
              double maxReach = (double)this.minecraft.gameMode.getPickRange();
              this.minecraft.hitResult = entity.pick(maxReach, idkWhatThisIs, false);
              Vector3d vector3d = entity.getEyePosition(idkWhatThisIs);
              boolean flag = false;
              double blockDistance = maxReach;
              if (this.minecraft.gameMode.hasFarPickRange()) {
            	  maxReach = 6.0D;
                  blockDistance = 6.0D;
              } else {
                 if (maxReach > 3.0D) {
                    flag = true;
                 }
                 maxReach = blockDistance;
              }

              blockDistance = blockDistance * blockDistance;
              if (this.minecraft.hitResult != null) {
                 blockDistance = this.minecraft.hitResult.getLocation().distanceToSqr(vector3d);
              }

              Vector3d vector3d1 = entity.getViewVector(1.0F);
              Vector3d vector3d2 = vector3d.add(vector3d1.x * maxReach, vector3d1.y * maxReach, vector3d1.z * maxReach);
              AxisAlignedBB axisalignedbb = entity.getBoundingBox().expandTowards(vector3d1.scale(maxReach)).inflate(1.0D, 1.0D, 1.0D);
              EntityRayTraceResult entityraytraceresult = ProjectileHelper.getEntityHitResult(entity, vector3d, vector3d2, axisalignedbb, (p_215312_0_) -> {
                 return !p_215312_0_.isSpectator() && p_215312_0_.isPickable();
              }, blockDistance);
              if (entityraytraceresult != null) {
                 Entity entity1 = entityraytraceresult.getEntity();
                 Vector3d vector3d3 = entityraytraceresult.getLocation();
                 double d2 = vector3d.distanceToSqr(vector3d3);
                 if (flag && d2 > 9.0D) {
                    this.minecraft.hitResult = BlockRayTraceResult.miss(vector3d3, Direction.getNearest(vector3d1.x, vector3d1.y, vector3d1.z), new BlockPos(vector3d3));
                 } else if (d2 < blockDistance || this.minecraft.hitResult == null) {
                    this.minecraft.hitResult = entityraytraceresult;
                    if (entity1 instanceof LivingEntity || entity1 instanceof ItemFrameEntity) {
                       this.minecraft.crosshairPickEntity = entity1;
                    }
                 }
              }
              this.minecraft.getProfiler().pop();
           }
        }
     }

}
