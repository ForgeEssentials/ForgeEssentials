package com.forgeessentials.chat.irc;

import net.minecraft.entity.Entity;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ICommandSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.UUID;

import org.pircbotx.User;

import com.forgeessentials.util.output.ChatOutputHandler;

public class IrcCommandSender extends CommandSource
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
    public String getTextName()
    {
        return "IRC:" + user.getNick();
    }

    @Override
    public ITextComponent getDisplayName()
    {
        return new StringTextComponent(this.getTextName());
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
    public Vector3d getPosition()
    {
        return getServer().getPositionVector();
    }

    @Override
    public World getEntityWorld()
    {
        return getServer().getEntityWorld();
    }

    @Override
    public Entity getEntity()
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

    @Override
    public void sendMessage(ITextComponent p_145747_1_, UUID p_145747_2_)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean acceptsSuccess()
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean acceptsFailure()
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean shouldInformAdmins()
    {
        // TODO Auto-generated method stub
        return false;
    }

}
