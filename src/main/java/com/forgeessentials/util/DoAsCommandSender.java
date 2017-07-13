package com.forgeessentials.util;

import net.minecraft.command.CommandResultStats.Type;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;

public class DoAsCommandSender implements ICommandSender
{

    protected ICommandSender sender;

    protected UserIdent ident;

    protected boolean hideChatMessages;

    public DoAsCommandSender()
    {
        this.ident = APIRegistry.IDENT_SERVER;
        this.sender = getServer();
    }

    public DoAsCommandSender(UserIdent ident)
    {
        this.ident = ident;
        this.sender = getServer();
    }

    public DoAsCommandSender(UserIdent ident, ICommandSender sender)
    {
        this.ident = ident;
        this.sender = sender;
    }

    public ICommandSender getOriginalSender()
    {
        return sender;
    }

    public UserIdent getUserIdent()
    {
        return ident;
    }

    @Override
    public String getName()
    {
        return sender.getName();
    }

    @Override
    public ITextComponent getDisplayName()
    {
        return sender.getDisplayName();
    }

    @Override
    public void sendMessage(ITextComponent message)
    {
        if (!hideChatMessages)
            sender.sendMessage(message);
    }

    @Override
    public boolean canUseCommand(int level, String command)
    {
        return true;
    }

    @Override
    public World getEntityWorld()
    {
        return sender.getEntityWorld();
    }

    @Override
    public BlockPos getPosition()
    {
        return sender.getPosition();
    }

    @Override
    public Vec3d getPositionVector()
    {
        return sender.getPositionVector();
    }

    @Override
    public Entity getCommandSenderEntity()
    {
        return sender.getCommandSenderEntity();
    }

    @Override
    public boolean sendCommandFeedback()
    {
        return sender.sendCommandFeedback();
    }

    @Override
    public void setCommandStat(Type p_174794_1_, int p_174794_2_)
    {
        sender.setCommandStat(p_174794_1_, p_174794_2_);
    }

    @Override
    public MinecraftServer getServer()
    {
        return FMLCommonHandler.instance().getMinecraftServerInstance();
    }

    public UserIdent getIdent()
    {
        return ident;
    }

    public void setIdent(UserIdent ident)
    {
        this.ident = ident;
    }

    public void setHideChatMessages(boolean hideChatMessages)
    {
        this.hideChatMessages = hideChatMessages;
    }

    public boolean isHideChatMessages()
    {
        return hideChatMessages;
    }

}