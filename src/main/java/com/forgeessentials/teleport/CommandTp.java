package com.forgeessentials.teleport;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.command.server.CommandTeleport;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.MathHelper;
import net.minecraftforge.permission.PermissionLevel;
import net.minecraftforge.permission.PermissionObject;

import com.forgeessentials.commons.selections.WarpPoint;
import com.forgeessentials.core.misc.TeleportHelper;

public class CommandTp extends CommandTeleport implements PermissionObject
{

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void processCommand(ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 1)
        {
            throw new WrongUsageException("commands.tp.usage", new Object[0]);
        }
        else
        {
            byte b0 = 0;
            Entity entity;

            if (args.length != 2 && args.length != 4 && args.length != 6)
            {
                entity = getCommandSenderAsPlayer(sender);
            }
            else
            {
                entity = func_175768_b(sender, args[0]);
                b0 = 1;
            }

            if (args.length != 1 && args.length != 2)
            {
                if (args.length < b0 + 3)
                {
                    throw new WrongUsageException("commands.tp.usage", new Object[0]);
                }
                else if (entity.worldObj != null)
                {
                    int i = b0 + 1;
                    CommandBase.CoordinateArg argX = func_175770_a(entity.posX, args[b0], true);
                    CommandBase.CoordinateArg argY = func_175767_a(entity.posY, args[i++], 0, 0, false);
                    CommandBase.CoordinateArg argZ = func_175770_a(entity.posZ, args[i++], true);
                    CommandBase.CoordinateArg argPitch = func_175770_a(entity.rotationYaw, args.length > i ? args[i++] : "~", false);
                    CommandBase.CoordinateArg argYaw = func_175770_a(entity.rotationPitch, args.length > i ? args[i] : "~", false);
                    float pitch;

                    if (entity instanceof EntityPlayerMP)
                    {
                        pitch = (float) argPitch.func_179629_b();
                        if (!argPitch.func_179630_c())
                            pitch = MathHelper.wrapAngleTo180_float(pitch);
                        float yaw = (float) argYaw.func_179629_b();
                        if (!argYaw.func_179630_c())
                            yaw = MathHelper.wrapAngleTo180_float(yaw);
                        if (yaw > 90.0F || yaw < -90.0F)
                        {
                            yaw = MathHelper.wrapAngleTo180_float(180.0F - yaw);
                            pitch = MathHelper.wrapAngleTo180_float(pitch + 180.0F);
                        }

                        WarpPoint pos = new WarpPoint(entity.worldObj.provider.getDimensionId(), argX.func_179629_b(), argY.func_179629_b(),
                                argZ.func_179629_b(), pitch, yaw);
                        if (argX.func_179630_c())
                            pos.setX(pos.getX() + entity.posX);
                        if (argY.func_179630_c())
                            pos.setX(pos.getY() + entity.posY);
                        if (argZ.func_179630_c())
                            pos.setX(pos.getZ() + entity.posZ);
                        if (argPitch.func_179630_c())
                            pos.setPitch(pos.getPitch() + entity.rotationPitch);
                        if (argYaw.func_179630_c())
                            pos.setYaw(pos.getYaw() + entity.rotationYaw);
                        TeleportHelper.teleport((EntityPlayerMP) entity, pos);
                    }
                    else
                    {
                        float f2 = (float) MathHelper.wrapAngleTo180_double(argPitch.func_179628_a());
                        pitch = (float) MathHelper.wrapAngleTo180_double(argYaw.func_179628_a());

                        if (pitch > 90.0F || pitch < -90.0F)
                        {
                            pitch = MathHelper.wrapAngleTo180_float(180.0F - pitch);
                            f2 = MathHelper.wrapAngleTo180_float(f2 + 180.0F);
                        }

                        entity.setLocationAndAngles(argX.func_179628_a(), argY.func_179628_a(), argZ.func_179628_a(), f2, pitch);
                        entity.setRotationYawHead(f2);
                    }

                    notifyOperators(sender, this, "commands.tp.success.coordinates", new Object[] { entity.getName(), Double.valueOf(argX.func_179628_a()),
                            Double.valueOf(argY.func_179628_a()), Double.valueOf(argZ.func_179628_a()) });
                }
            }
            else
            {
                Entity targetEntity = func_175768_b(sender, args[args.length - 1]);
                if (targetEntity instanceof EntityPlayerMP)
                {
                    WarpPoint pos = new WarpPoint(targetEntity.worldObj.provider.getDimensionId(), targetEntity.posX, targetEntity.posY, targetEntity.posZ,
                            targetEntity.rotationPitch, targetEntity.rotationYaw);
                    TeleportHelper.teleport((EntityPlayerMP) entity, pos);
                }
                else if (targetEntity.worldObj != entity.worldObj)
                {
                    throw new CommandException("commands.tp.notSameDimension", new Object[0]);
                }
                else
                {
                    entity.mountEntity((Entity) null);

                    if (entity instanceof EntityPlayerMP)
                    {
                        ((EntityPlayerMP) entity).playerNetServerHandler.setPlayerLocation(targetEntity.posX, targetEntity.posY, targetEntity.posZ,
                                targetEntity.rotationPitch, targetEntity.rotationYaw);
                    }
                    else
                    {
                        entity.setLocationAndAngles(targetEntity.posX, targetEntity.posY, targetEntity.posZ, targetEntity.rotationPitch,
                                targetEntity.rotationYaw);
                    }

                    notifyOperators(sender, this, "commands.tp.success", new Object[] { targetEntity.getName(), targetEntity.getName() });
                }
            }
        }
    }

    @Override
    public String getPermissionNode()
    {
        return TeleportModule.PERM_TP;
    }

    @Override
    public PermissionLevel getPermissionLevel()
    {
        return PermissionLevel.OP;
    }

}
