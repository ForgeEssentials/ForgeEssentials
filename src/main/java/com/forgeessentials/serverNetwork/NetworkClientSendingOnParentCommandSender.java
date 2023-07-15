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

public class NetworkClientSendingOnParentCommandSender implements ICommandSource
{
    String connectedId;

    public NetworkClientSendingOnParentCommandSender(String connectedId)
    {
        this.connectedId = connectedId;
    }

    @Override
    public void sendMessage(ITextComponent chatComponent, UUID senderUUID)
    {
        if (ModuleNetworking.getClients().containsKey(connectedId))
            if(ModuleNetworking.getClients().get(connectedId).getCurrentChannel().isOpen()) {
                ModuleNetworking.getInstance().getServer().sendPacketFor(
                        ModuleNetworking.getClients().get(connectedId).getCurrentChannel(), 
                        new Packet11SharedCommandResponse(chatComponent.getString()));
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
                serverworld, 4, "Client@"+connectedId,
                new StringTextComponent("Client@"+connectedId), server, (Entity) null);
    }

}
