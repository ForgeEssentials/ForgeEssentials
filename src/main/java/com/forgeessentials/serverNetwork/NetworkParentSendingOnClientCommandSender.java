package com.forgeessentials.serverNetwork;

import java.util.UUID;

import com.forgeessentials.serverNetwork.packetbase.packets.Packet11SharedCommandResponse;

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
public class NetworkParentSendingOnClientCommandSender implements ICommandSource
{
    String connectedId;

    public NetworkParentSendingOnClientCommandSender(String connectedId)
    {
        this.connectedId = connectedId;
    }
    @Override
    public void sendMessage(ITextComponent chatComponent, UUID senderUUID)
    {
        if(ModuleNetworking.getInstance().getClient().isChannelOpen()) {
            ModuleNetworking.getInstance().getClient().sendPacket(new Packet11SharedCommandResponse(chatComponent.getString()));
        }
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

    public CommandSource createCommandSourceStack()
    {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        ServerWorld serverworld = server.overworld();
        return new CommandSource(this, Vector3d.ZERO, Vector2f.ZERO,
                serverworld, 4, "Parent@"+connectedId,
                new StringTextComponent("Parent@"+connectedId), server, (Entity) null);
    }
}
