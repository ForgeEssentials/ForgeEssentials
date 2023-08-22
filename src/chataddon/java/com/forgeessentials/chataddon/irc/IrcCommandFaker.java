package com.forgeessentials.chataddon.irc;

import java.util.UUID;

import org.jetbrains.annotations.NotNull;
import org.pircbotx.hooks.events.MessageEvent;


import net.minecraft.command.CommandSource;
import net.minecraft.command.ICommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class IrcCommandFaker implements ICommandSource
{
	private MessageEvent event;
    @Override
    public void sendMessage(ITextComponent p_145747_1_, @NotNull UUID p_145747_2_)
    {
    	if(p_145747_1_.getString().startsWith("/")) {
    		event.respond("!"+p_145747_1_.getString().substring(1));
    		return;
    	}
    	event.respond(p_145747_1_.getString());
    }

    @Override
    public boolean acceptsSuccess()
    {
        return true;
    }

    @Override
    public boolean acceptsFailure()
    {
        return true;
    }

    @Override
    public boolean shouldInformAdmins()
    {
        return false;
    }

    public CommandSource createCommandSourceStack(int level, MessageEvent event)
    {
    	this.event = event;
    	String name = event.getUser().getNick();
        if (level < 0)
        {
            level = 0;
        }
        else if (level > 4)
        {
            level = 4;
        }
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        ServerWorld serverworld = server.overworld();
        return new CommandSource(this, Vector3d.ZERO, Vector2f.ZERO,
                serverworld, level, name,
                new StringTextComponent(name), server, (Entity)null);
    }
}

