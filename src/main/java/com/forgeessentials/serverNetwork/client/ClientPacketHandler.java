package com.forgeessentials.serverNetwork.client;

import com.forgeessentials.serverNetwork.ModuleNetworking;
import com.forgeessentials.serverNetwork.NetworkParentSendingOnClientCommandSender;
import com.forgeessentials.serverNetwork.packetbase.PacketHandler;
import com.forgeessentials.serverNetwork.packetbase.packets.Packet10SharedCommandSending;
import com.forgeessentials.serverNetwork.packetbase.packets.Packet11SharedCommandResponse;
import com.forgeessentials.serverNetwork.packetbase.packets.Packet1ServerValidationResponse;
import com.forgeessentials.serverNetwork.packetbase.packets.Packet2ClientNewConnectionData;
import com.forgeessentials.serverNetwork.packetbase.packets.Packet3ClientConnectionData;
import com.forgeessentials.serverNetwork.packetbase.packets.Packet4ServerPasswordResponce;
import com.forgeessentials.serverNetwork.packetbase.packets.Packet5SharedCloseSession;
import com.forgeessentials.serverNetwork.utils.ConnectionData.LocalClientData;
import com.forgeessentials.serverNetwork.utils.EncryptionUtils;
import com.forgeessentials.util.output.logger.LoggingHandler;

import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class ClientPacketHandler implements PacketHandler
{

    @Override
    public void handle(Packet1ServerValidationResponse validationResponce)
    {
        LocalClientData data = ModuleNetworking.getLocalClient();
        if(data.getRemoteServerId().equals("notSet")) {
            data.setRemoteServerId(validationResponce.getServerId());
            LoggingHandler.felog.info("FENetworkClient Connected to new FENetworkServer: " + validationResponce.getServerId());
            String clientPassword = EncryptionUtils.generatePasskey(10);
            data.setPassword(clientPassword);
            FENetworkClient.getInstance().sendPacket(new Packet2ClientNewConnectionData(data.getLocalClientId()));
            return;
        }
        else if (data.getRemoteServerId().equals(validationResponce.getServerId())){
            if(data.getPrivatekey().equals("notSet")) {
                LoggingHandler.felog.error("FENetworkClient You need to manualy enter the privateKey of FENetworkServer: " + validationResponce.getServerId()
                +" into the LocalFENetworkClientData file before you can connect!");
                LoggingHandler.felog.debug("FENetworkClient Needs to add privateKey");
                if(!FENetworkClient.getInstance().shutdown) {
                    FENetworkClient.getInstance().disconnect();
                }
                return;
            }
            LoggingHandler.felog.debug("FENetworkClient Connected to known FENetworkServer: " + validationResponce.getServerId());
            try
            {
                FENetworkClient.getInstance().sendPacket(new Packet3ClientConnectionData(data.getLocalClientId(), EncryptionUtils.encryptString(data.getPassword(), data.getPrivatekey())));
            }
            catch (Exception e)
            {
                e.printStackTrace();
                LoggingHandler.felog.error("FENetworkClient Failed to send password to FENetworkServer!");
                FENetworkClient.getInstance().disconnect();
            }
        }
        else {
            LoggingHandler.felog.error("FENetworkClient Server ID does not match previously connected server! if you are trying to connect "
                    + "to a new server, delete the LocalFENetworkClientData.json file and start the server to connect again!");
            FENetworkClient.getInstance().disconnect();
        }
    }

    @Override
    public void handle(Packet4ServerPasswordResponce passwordPacket)
    {
        ModuleNetworking.getLocalClient().setAuthenticated(passwordPacket.isAuthenticated());
        LoggingHandler.felog.debug("FENetworkClient authentication "+(passwordPacket.isAuthenticated() ?"Successful":"Failed"));
    }

    @Override
    public void handle(Packet5SharedCloseSession closeSession)
    {
        System.out.println("FENetworkClient Received close orders");
    }

    @Override
    public void handle(Packet10SharedCommandSending commandPacket)
    {
        ServerLifecycleHooks.getCurrentServer().getCommands().performCommand(
                new NetworkParentSendingOnClientCommandSender(ModuleNetworking.getLocalClient().getRemoteServerId()).createCommandSourceStack(), 
                commandPacket.getCommandToSend());
    }

    @Override
    public void handle(Packet11SharedCommandResponse commandResponce)
    {
        LoggingHandler.felog.info("CommandResponse from server: "+commandResponce.getCommandResponse());
    }
}
