package net.minecraftforge.permission;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Vec3;

/**
 * Class to hold all information regarding a permission check
 */
public class PermissionContext
{

    private EntityPlayer player;

    private ICommandSender sender;

    private ICommand command;

    private int dimension;

    private Vec3 sourceLocationStart;

    private Vec3 sourceLocationEnd;

    private Vec3 targetLocationStart;

    private Vec3 targetLocationEnd;

    private Entity sourceEntity;

    private Entity targetEntity;

    public PermissionContext()
    {
    }

    public PermissionContext(ICommandSender sender)
    {
        this.sender = sender;
        if (sender instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) sender;
            this.player = player;
            this.dimension = player.dimension;
            this.sourceLocationStart = new Vec3(player.posX, player.posY, player.posZ);
        }
    }

    public PermissionContext(ICommandSender sender, ICommand command)
    {
        this(sender);
        this.command = command;
    }

    public ICommandSender getSender()
    {
        return sender;
    }

    public EntityPlayer getPlayer()
    {
        return player;
    }

    public ICommand getCommand()
    {
        return command;
    }

    public int getDimension()
    {
        return dimension;
    }

    public Vec3 getSourceLocationStart()
    {
        return sourceLocationStart;
    }

    public Vec3 getSourceLocationEnd()
    {
        return sourceLocationEnd;
    }

    public Vec3 getTargetLocationStart()
    {
        return targetLocationStart;
    }

    public Vec3 getTargetLocationEnd()
    {
        return targetLocationEnd;
    }

    public Entity getSourceEntity()
    {
        return sourceEntity;
    }

    public Entity getTargetEntity()
    {
        return targetEntity;
    }

    public PermissionContext setSender(ICommandSender sender)
    {
        if (sender instanceof EntityPlayer)
            return setPlayer((EntityPlayer) sender);
        this.sender = sender;
        return this;
    }

    public PermissionContext setPlayer(EntityPlayer player)
    {
        this.sender = this.player = player;
        return this;
    }

    public PermissionContext setCommand(ICommand command)
    {
        this.command = command;
        return this;
    }

    public PermissionContext setDimension(int dimension)
    {
        this.dimension = dimension;
        return this;
    }

    public PermissionContext setSourceStart(Vec3 location)
    {
        this.sourceLocationStart = location;
        return this;
    }

    public PermissionContext setSourceEnd(Vec3 location)
    {
        this.sourceLocationEnd = location;
        return this;
    }

    public PermissionContext setTargetStart(Vec3 location)
    {
        this.targetLocationStart = location;
        return this;
    }

    public PermissionContext setTargetEnd(Vec3 location)
    {
        this.targetLocationEnd = location;
        return this;
    }

    public PermissionContext setSource(Entity entity)
    {
        this.sourceEntity = entity;
        return this;
    }

    public PermissionContext setTarget(Entity entity)
    {
        this.targetEntity = entity;
        return this;
    }

    public boolean isConsole()
    {
        return player == null && (sender == null || sender instanceof MinecraftServer);
    }

    public boolean isPlayer()
    {
        return player instanceof EntityPlayer;
    }

}