package net.minecraftforge.permission;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.rcon.RConConsoleSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.FMLCommonHandler;

/**
 * Class to hold all information regarding a permission check
 */
public class PermissionContext
{

    private EntityPlayer player;

    private ICommandSender sender;

    private ICommand command;

    private int dimension;

    private Vec3d sourceLocationStart;

    private Vec3d sourceLocationEnd;

    private Vec3d targetLocationStart;

    private Vec3d targetLocationEnd;

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
            this.sourceLocationStart = new Vec3d(player.posX, player.posY, player.posZ);
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

    public Vec3d getSourceLocationStart()
    {
        return sourceLocationStart;
    }

    public Vec3d getSourceLocationEnd()
    {
        return sourceLocationEnd;
    }

    public Vec3d getTargetLocationStart()
    {
        return targetLocationStart;
    }

    public Vec3d getTargetLocationEnd()
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

    public PermissionContext setSourceStart(Vec3d location)
    {
        this.sourceLocationStart = location;
        return this;
    }

    public PermissionContext setSourceEnd(Vec3d location)
    {
        this.sourceLocationEnd = location;
        return this;
    }

    public PermissionContext setTargetStart(Vec3d location)
    {
        this.targetLocationStart = location;
        return this;
    }

    public PermissionContext setTargetEnd(Vec3d location)
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

    public boolean isRCon()
    {
        if (FMLCommonHandler.instance().getSide().isServer())
            return sender instanceof RConConsoleSource;
        else return false; // rcon doesn't exist on clients
    }

}