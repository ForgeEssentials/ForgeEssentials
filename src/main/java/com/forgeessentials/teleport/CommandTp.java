package com.forgeessentials.teleport;

import java.util.EnumSet;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.command.server.CommandTeleport;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
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
                    CommandBase.CoordinateArg argYaw = func_175770_a(entity.rotationYaw, args.length > i ? args[i++] : "~", false);
                    CommandBase.CoordinateArg argPitch = func_175770_a(entity.rotationPitch, args.length > i ? args[i] : "~", false);
                    float f;

                    if (entity instanceof EntityPlayerMP)
                    {
                        EnumSet enumset = EnumSet.noneOf(S08PacketPlayerPosLook.EnumFlags.class);
                        if (argX.func_179630_c())
                            enumset.add(S08PacketPlayerPosLook.EnumFlags.X);
                        if (argY.func_179630_c())
                            enumset.add(S08PacketPlayerPosLook.EnumFlags.Y);
                        if (argZ.func_179630_c())
                            enumset.add(S08PacketPlayerPosLook.EnumFlags.Z);
                        if (argPitch.func_179630_c())
                            enumset.add(S08PacketPlayerPosLook.EnumFlags.X_ROT);
                        if (argYaw.func_179630_c())
                            enumset.add(S08PacketPlayerPosLook.EnumFlags.Y_ROT);

                        f = (float) argYaw.func_179629_b();
                        if (!argYaw.func_179630_c())
                            f = MathHelper.wrapAngleTo180_float(f);

                        float f1 = (float) argPitch.func_179629_b();
                        if (!argPitch.func_179630_c())
                            f1 = MathHelper.wrapAngleTo180_float(f1);

                        if (f1 > 90.0F || f1 < -90.0F)
                        {
                            f1 = MathHelper.wrapAngleTo180_float(180.0F - f1);
                            f = MathHelper.wrapAngleTo180_float(f + 180.0F);
                        }

                        WarpPoint pos = new WarpPoint(entity.worldObj.provider.getDimensionId(), argX.func_179629_b(), argY.func_179629_b(),
                                argZ.func_179629_b(), f, f1);
                        TeleportHelper.teleport((EntityPlayerMP) entity, pos);
                    }
                    else
                    {
                        float f2 = (float) MathHelper.wrapAngleTo180_double(argYaw.func_179628_a());
                        f = (float) MathHelper.wrapAngleTo180_double(argPitch.func_179628_a());

                        if (f > 90.0F || f < -90.0F)
                        {
                            f = MathHelper.wrapAngleTo180_float(180.0F - f);
                            f2 = MathHelper.wrapAngleTo180_float(f2 + 180.0F);
                        }

                        entity.setLocationAndAngles(argX.func_179628_a(), argY.func_179628_a(), argZ.func_179628_a(), f2, f);
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
                            targetEntity.rotationYaw, targetEntity.rotationPitch);
                    TeleportHelper.teleport((EntityPlayerMP) targetEntity, pos);
                    return;
                }
                if (targetEntity.worldObj != entity.worldObj)
                {
                    throw new CommandException("commands.tp.notSameDimension", new Object[0]);
                }
                else
                {
                    entity.mountEntity((Entity) null);

                    if (entity instanceof EntityPlayerMP)
                    {
                        ((EntityPlayerMP) entity).playerNetServerHandler.setPlayerLocation(targetEntity.posX, targetEntity.posY, targetEntity.posZ,
                                targetEntity.rotationYaw, targetEntity.rotationPitch);
                    }
                    else
                    {
                        entity.setLocationAndAngles(targetEntity.posX, targetEntity.posY, targetEntity.posZ, targetEntity.rotationYaw,
                                targetEntity.rotationPitch);
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
