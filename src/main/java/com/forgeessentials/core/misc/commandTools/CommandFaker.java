package com.forgeessentials.core.misc.commandTools;

import java.util.UUID;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.util.output.logger.LoggingHandler;
import com.mojang.authlib.GameProfile;

import net.minecraft.command.CommandSource;
import net.minecraft.command.ICommandSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;

public class CommandFaker implements ICommandSource
{

    @Override
    public void sendMessage(ITextComponent p_145747_1_, @NotNull UUID p_145747_2_)
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

    public CommandSource createCommandSourceStack(int level)
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
        ServerWorld serverworld = server.overworld();
        return new CommandSource(this, Vector3d.ZERO, Vector2f.ZERO,
                serverworld, level, APIRegistry.IDENT_COMMANDFAKER.getUsername(),
                new StringTextComponent(APIRegistry.IDENT_COMMANDFAKER.getUsername()), server, new FakePlayer(serverworld, new GameProfile(APIRegistry.IDENT_COMMANDFAKER.getUuid(), APIRegistry.IDENT_COMMANDFAKER.getUsername())));
    }
}
