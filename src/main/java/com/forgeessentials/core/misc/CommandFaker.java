package com.forgeessentials.core.misc;

import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.server.ServerLifecycleHooks;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.util.output.logger.LoggingHandler;
import com.mojang.authlib.GameProfile;

public class CommandFaker implements CommandSource
{

    @Override
    public void sendSystemMessage(Component p_145747_1_)
    {
        LoggingHandler.felog.info("CommandFaker: " + p_145747_1_.getString());
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

    public CommandSourceStack createCommandSourceStack(int level)
    {
        if (level < 0)
        {
            level = 0;
        }
        else if (level > 4)
        {
            level = 4;
        }
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        ServerLevel serverworld = server.overworld();
        return new CommandSourceStack(this, Vec3.ZERO, Vec2.ZERO,
                serverworld, level, APIRegistry.IDENT_COMMANDFAKER.getUsername(),
                new TextComponent(APIRegistry.IDENT_COMMANDFAKER.getUsername()), server, new FakePlayer(serverworld, new GameProfile(APIRegistry.IDENT_COMMANDFAKER.getUuid(), APIRegistry.IDENT_COMMANDFAKER.getUsername())));
    }
}
