package net.minecraftforge.permissions;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;

/**
 * This is the base class for a context. A permission framework <i><b>might</b></i> subclass this context, to allow additional properties for the context.
 */
public class PermissionContext implements IContext {

    private EntityPlayer player;

    private EntityPlayer targetPlayer;

    private ICommand command;

    private ICommandSender commandSender;

    private Vec3 sourceLocationStart;

    private Vec3 sourceLocationEnd;

    private Vec3 targetLocationStart;

    private Vec3 targetLocationEnd;

    private Entity sourceEntity;

    private Entity targetEntity;

    @Override
    public EntityPlayer getPlayer()
    {
        return player;
    }

    @Override
    public EntityPlayer getTargetPlayer()
    {
        return targetPlayer;
    }

    @Override
    public ICommand getCommand()
    {
        return command;
    }

    @Override
    public ICommandSender getCommandSender()
    {
        return commandSender;
    }

    @Override
    public Vec3 getSourceLocationStart()
    {
        return sourceLocationStart;
    }

    @Override
    public Vec3 getSourceLocationEnd()
    {
        return sourceLocationEnd;
    }

    @Override
    public Vec3 getTargetLocationStart()
    {
        return targetLocationStart;
    }

    @Override
    public Vec3 getTargetLocationEnd()
    {
        return targetLocationEnd;
    }

    @Override
    public Entity getSourceEntity()
    {
        return sourceEntity;
    }

    @Override
    public Entity getTargetEntity()
    {
        return targetEntity;
    }

    public PermissionContext setPlayer(EntityPlayer player)
    {
        this.player = player;
        return this;
    }

    public PermissionContext setTargetPlayer(EntityPlayer player)
    {
        this.targetPlayer = player;
        return this;
    }

    public PermissionContext setCommand(ICommand command)
    {
        this.command = command;
        return this;
    }

    public PermissionContext setCommandSender(ICommandSender sender)
    {
        this.commandSender = sender;
        return this;
    }

    public PermissionContext setSourceLocationStart(Vec3 location)
    {
        this.sourceLocationStart = location;
        return this;
    }

    public PermissionContext setSourceLocationEnd(Vec3 location)
    {
        this.sourceLocationEnd = location;
        return this;
    }

    public PermissionContext setTargetLocationStart(Vec3 location)
    {
        this.targetLocationStart = location;
        return this;
    }

    public PermissionContext setTargetLocationEnd(Vec3 location)
    {
        this.targetLocationEnd = location;
        return this;
    }

    public PermissionContext setSourceEntity(Entity entity)
    {
        this.sourceEntity = entity;
        return this;
    }

    public PermissionContext setTargetEntity(Entity entity)
    {
        this.targetEntity = entity;
        return this;
    }

    public PermissionContext()
    {
    }

    public boolean isConsole()
    {
        return player == null && commandSender != null && !(commandSender instanceof EntityPlayer);
    }

    public boolean isPlayer()
    {
        return (player instanceof EntityPlayer) || (commandSender instanceof EntityPlayer);
    }

}