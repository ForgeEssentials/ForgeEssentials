package com.forgeessentials.teleport;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.command.server.CommandTeleport;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.permission.PermissionLevel;
import net.minecraftforge.permission.PermissionObject;

import com.forgeessentials.commons.selections.WarpPoint;
import com.forgeessentials.core.misc.TeleportHelper;

public class CommandTp extends CommandTeleport implements PermissionObject
{

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
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
                entity = getEntity(server, sender, args[0]);
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
                    CommandBase.CoordinateArg argX = parseCoordinate(entity.posX, args[b0], true);
                    CommandBase.CoordinateArg argY = parseCoordinate(entity.posY, args[i++], 0, 0, false);
                    CommandBase.CoordinateArg argZ = parseCoordinate(entity.posZ, args[i++], true);
                    CommandBase.CoordinateArg argPitch = parseCoordinate(entity.rotationYaw, args.length > i ? args[i++] : "~", false);
                    CommandBase.CoordinateArg argYaw = parseCoordinate(entity.rotationPitch, args.length > i ? args[i] : "~", false);
                    float pitch;

                    if (entity instanceof EntityPlayerMP)
                    {
                        pitch = (float) argPitch.getAmount();
                        if (!argPitch.isRelative())
                            pitch = MathHelper.wrapDegrees(pitch);
                        float yaw = (float) argYaw.getAmount();
                        if (!argYaw.isRelative())
                            yaw = MathHelper.wrapDegrees(yaw);
                        if (yaw > 90.0F || yaw < -90.0F)
                        {
                            yaw = MathHelper.wrapDegrees(180.0F - yaw);
                            pitch = MathHelper.wrapDegrees(pitch + 180.0F);
                        }

                        WarpPoint pos = new WarpPoint(entity.worldObj.provider.getDimension(), argX.getAmount(), argY.getAmount(),
                                argZ.getAmount(), pitch, yaw);
                        if (argX.isRelative())
                            pos.setX(pos.getX() + entity.posX);
                        if (argY.isRelative())
                            pos.setX(pos.getY() + entity.posY);
                        if (argZ.isRelative())
                            pos.setX(pos.getZ() + entity.posZ);
                        if (argPitch.isRelative())
                            pos.setPitch(pos.getPitch() + entity.rotationPitch);
                        if (argYaw.isRelative())
                            pos.setYaw(pos.getYaw() + entity.rotationYaw);
                        TeleportHelper.teleport((EntityPlayerMP) entity, pos);
                    }
                    else
                    {
                        float f2 = (float) MathHelper.wrapDegrees(argPitch.getResult());
                        pitch = (float) MathHelper.wrapDegrees(argYaw.getResult());

                        if (pitch > 90.0F || pitch < -90.0F)
                        {
                            pitch = MathHelper.wrapDegrees(180.0F - pitch);
                            f2 = MathHelper.wrapDegrees(f2 + 180.0F);
                        }

                        entity.setLocationAndAngles(argX.getResult(), argY.getResult(), argZ.getResult(), f2, pitch);
                        entity.setRotationYawHead(f2);
                    }

                    notifyCommandListener(sender, this, "commands.tp.success.coordinates", new Object[] { entity.getName(), Double.valueOf(argX.getResult()),
                            Double.valueOf(argY.getResult()), Double.valueOf(argZ.getResult()) });
                }
            }
            else
            {
                Entity targetEntity = getEntity(FMLCommonHandler.instance().getMinecraftServerInstance(), sender, args[args.length - 1]);
                if (targetEntity instanceof EntityPlayerMP)
                {
                    WarpPoint pos = new WarpPoint(targetEntity.worldObj.provider.getDimension(), targetEntity.posX, targetEntity.posY, targetEntity.posZ,
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
                        ((EntityPlayerMP) entity).connection.setPlayerLocation(targetEntity.posX, targetEntity.posY, targetEntity.posZ,
                                targetEntity.rotationPitch, targetEntity.rotationYaw);
                    }
                    else
                    {
                        entity.setLocationAndAngles(targetEntity.posX, targetEntity.posY, targetEntity.posZ, targetEntity.rotationPitch,
                                targetEntity.rotationYaw);
                    }

                    notifyCommandListener(sender, this, "commands.tp.success", new Object[] { targetEntity.getName(), targetEntity.getName() });
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
