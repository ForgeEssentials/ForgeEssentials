package com.forgeessentials.serverNetwork.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

import com.forgeessentials.serverNetwork.ModuleNetworking;
import com.forgeessentials.serverNetwork.server.packets.CustomPacket;
import com.forgeessentials.serverNetwork.server.packets.DataPacket;
import com.forgeessentials.serverNetwork.server.packets.Packet;
import com.forgeessentials.serverNetwork.server.packets.PacketDirection;
import com.forgeessentials.serverNetwork.server.packets.PacketType;
import com.forgeessentials.serverNetwork.server.packets.bidirectional.CloseSessionPacket;
import com.forgeessentials.serverNetwork.server.packets.clientTypes.ClientPasswordPacket;
import com.forgeessentials.serverNetwork.server.packets.clientTypes.ClientValidationPacket;
import com.forgeessentials.serverNetwork.server.packets.serverTypes.ServerPasswordResponcePacket;
import com.forgeessentials.util.output.logger.LoggingHandler;

public class FeNetworkClient{
    private final String serverHost;
    private final int serverPort;
    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private Thread clientThread;
    private boolean authenticated;
    private boolean clientRunning = false;

    public FeNetworkClient(String serverHost, int serverPort) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
    }


    public int connect() {
        if(clientRunning) {
            return 1;
        }
        clientThread = new Thread(() -> {
            clientRunning=true;
            try {
                socket = new Socket(serverHost, serverPort);
                // Disable automatic closing of the streams on socket close
                socket.setSoLinger(false, 0);

                // Wrap the streams with FilterInputStream and FilterOutputStream
                FilterInputStream filterInputStream = new FilterInputStream(socket.getInputStream()) {
                    @Override
                    public void close() throws IOException {
                        // Do not close the underlying input stream
                    }
                };

                // Use the wrapped streams for reading/writing operations
                inputStream = new DataInputStream(filterInputStream);
                outputStream = new DataOutputStream(socket.getOutputStream());
                System.out.println("Connected to server: " + serverHost + ":" + serverPort);

                // Perform validation by sending the validation packet to the server
                sendValidationPacket();
                sendPacket0(new ClientPasswordPacket("password123"));
                // Start listening for server responses
                listenForResponses();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, "FEClientNetworkThread");
        clientThread.start();
        return 0;
    }

    private void sendValidationPacket() {
        // Create a custom packet with the validation string and int values
        ClientValidationPacket validationPacket = new ClientValidationPacket("FENetwork", ModuleNetworking.channelVersion);

        // Send the validation packet to the server
        try {
        // Encode the packet and write the packet data
        byte[] packetData = validationPacket.encode();
        outputStream.write(packetData);

        // Flush the output stream to ensure the data is sent immediately
        outputStream.flush();
    } catch (IOException e) {
        e.printStackTrace();
    }
    }

    public int disconnect(boolean sendClosePacket) {
        try {
            if (socket != null) {
                clientRunning=false;
                if(sendClosePacket) {sendPacket(new CloseSessionPacket());}
                socket.close();
                System.out.println("Disconnected from server");
                return 0;
            }
            return 1;
        } catch (IOException e) {
            e.printStackTrace();
            return 1;
        }
    }

    public void sendPacket(Packet packet) {
        if(!authenticated) {
            System.out.println("Can't Send Packet since client is not authenticated with server!");
            return;
        }
        sendPacket0(packet);
    }
    private void sendPacket0(Packet packet) {
        try {
            // Write the packet type identifier
            int packetTypeOrdinal = packet.getType().ordinal();
            outputStream.writeInt(packetTypeOrdinal);

            // Encode the packet and write the packet data
            byte[] packetData = packet.encode();
            outputStream.writeInt(packetData.length);
            outputStream.write(packetData);

            // Flush the output stream to ensure the data is sent immediately
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
            disconnect(false);
        }
    }

    private void listenForResponses() {
        try {
            while (clientRunning) {
             // Check if there is data available to be read
                while (inputStream.available() <= 0) {
                    // Wait for a short duration before checking again
                    Thread.sleep(100);
                }

                // Read the packet type identifier
                int packetTypeOrdinal = inputStream.readInt();
                PacketType packetType = PacketType.values()[packetTypeOrdinal];

                // Read the packet data length
                int packetDataLength = inputStream.readInt();

                // Read the packet data
                byte[] packetData = new byte[packetDataLength];
                inputStream.readFully(packetData);

                // Decode the packet based on its type
                Packet packet = PacketType.decodePacket(packetType, packetData);

                // Process the received packet
                handlePacket(packet);
            }
        } catch (SocketException ex) {
            if (clientRunning) {
                ex.printStackTrace();
                LoggingHandler.felog.fatal("Protocol error. Ignoring packet");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        } finally {
            disconnect(false);
        }
    }

    private void handlePacket(Packet packet) {
        if(packet.getDirection().equals(PacketDirection.Client_To_Server)) {
            System.out.println("Invalid packet received from server. Closing session.");
            sendPacket(new CloseSessionPacket());
            disconnect(true);
            return;
        }
        if (packet instanceof DataPacket) {
            DataPacket dataPacket = (DataPacket) packet;
            String data = dataPacket.getData();
            System.out.println("Received data: " + data);
            // Process the received data packet as needed
            // ...
        } else if (packet instanceof CustomPacket) {
            CustomPacket customPacket = (CustomPacket) packet;
            String stringValue = customPacket.getStringValue();
            int intValue = customPacket.getIntValue();
            boolean booleanValue = customPacket.getBooleanValue();
            System.out.println("Received custom packet: stringValue=" + stringValue +
                    ", intValue=" + intValue +
                    ", booleanValue=" + booleanValue);
            // Process the received custom packet as needed
            // ...
        } else if (packet instanceof CloseSessionPacket) {
            System.out.println("Received close session packet");
            disconnect(false);
        } else if (packet instanceof ServerPasswordResponcePacket) {
            System.out.println("Received Server Password Responce Packet");
            ServerPasswordResponcePacket passpacket = (ServerPasswordResponcePacket) packet;
            if(!passpacket.isAuthenticated()) {
                System.out.println("Authentication failed. Closing connection.");
                disconnect(false);
            }
            authenticated=true;
            System.out.println("Authentication succeeded!");
        } else {
            System.out.println("Received unknown packet type. Closing connection.");
            disconnect(true); // Close the connection when an unknown packet is received
        }
    }

    public boolean isAuthenticated()
    {
        return authenticated;
    }
}
