package com.forgeessentials.chat.irc;

import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import org.pircbotx.User;

import com.forgeessentials.util.output.ChatOutputHandler;

public class IrcCommandSender implements ICommandSender
{

    private User user;

    public IrcCommandSender(User user)
    {
        this.user = user;
    }

    public User getUser()
    {
        return user;
    }

    @Override
    public String getName()
    {
        return "IRC:" + user.getNick();
    }

    @Override
    public ITextComponent getDisplayName()
    {
        return new StringTextComponent(this.getName());
    }

    @Override
    public void sendMessage(ITextComponent chatComponent)
    {
        if (user.getBot().isConnected())
            user.send().message(ChatOutputHandler.stripFormatting(chatComponent.plainCopy().toString()));
    }

    @Override
    public boolean canUseCommand(int p_70003_1_, String p_70003_2_)
    {
        return true;
    }

    @Override
    public BlockPos getPosition()
    {
        return getServer().getPosition();
    }

    @Override
    public Vector3d getPositionVector()
    {
        return getServer().getPositionVector();
    }

    @Override
    public World getEntityWorld()
    {
        return getServer().getEntityWorld();
    }

    @Override
    public Entity getCommandSenderEntity()
    {
        return getServer().getCommandSenderEntity();
    }

    @Override
    public boolean sendCommandFeedback()
    {
        return getServer().sendCommandFeedback();
    }

    @Override
    public void setCommandStat(Type p_174794_1_, int p_174794_2_)
    {
        getServer().setCommandStat(p_174794_1_, p_174794_2_);
    }

    @Override
    public MinecraftServer getServer()
    {
        return ServerLifecycleHooks.getCurrentServer();
    }

}
