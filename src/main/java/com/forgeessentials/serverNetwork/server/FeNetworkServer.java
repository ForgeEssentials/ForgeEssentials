package com.forgeessentials.serverNetwork.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.forgeessentials.serverNetwork.ModuleNetworking;
import com.forgeessentials.serverNetwork.server.packets.DataPacket;
import com.forgeessentials.serverNetwork.server.packets.Packet;
import com.forgeessentials.serverNetwork.server.packets.PacketDirection;
import com.forgeessentials.serverNetwork.server.packets.PacketType;
import com.forgeessentials.serverNetwork.server.packets.bidirectional.CloseSessionPacket;
import com.forgeessentials.serverNetwork.server.packets.clientTypes.ClientPasswordPacket;
import com.forgeessentials.serverNetwork.server.packets.serverTypes.ServerPasswordResponcePacket;
import com.forgeessentials.util.output.logger.LoggingHandler;

public class FeNetworkServer
{
    private final String remoteServerHost;
    private final int remoteServerPort;
    private ServerSocket serverSocket;
    private List<Session> sessions;
    private Thread serverThread;
    private boolean serverRunning = false;
    private int numConenctionsTotal =0;

    public FeNetworkServer(String remoteServerHost, int remoteServerPort) {
        this.remoteServerHost = remoteServerHost;
        this.remoteServerPort = remoteServerPort;
        sessions = new ArrayList<>();
    }

    public int startServer() {
        if(serverRunning) {
            return 1;
        }
        try {
            serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress(remoteServerHost, remoteServerPort));
            System.out.println("Server started on " + serverSocket.getInetAddress().getHostAddress() + ":" + serverSocket.getLocalPort());

            serverThread = new Thread(new Runnable() {
                @Override
                public void run()
                {
                    serverRunning=true;
                    while (serverRunning) {
                        try
                        {
                            Socket socket = serverSocket.accept();
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
                            DataInputStream dataInputStream = new DataInputStream(filterInputStream);
                            // Validate the connection before further processing
                            if (!validateConnection(socket, dataInputStream)) {
                                socket.close();
                                continue;
                            }

                            numConenctionsTotal+=1;
                            Thread connectionThread = new Thread(() -> handleConnection(socket, dataInputStream), "FeServerListening"+numConenctionsTotal);
                            connectionThread.start();
                        } catch (SocketException ex) {
                            if (serverRunning) {
                                LoggingHandler.felog.fatal("Protocol error. Ignoring packet");
                            }
                        }
                        catch (IOException ex)
                        {
                            ex.printStackTrace();
                            LoggingHandler.felog.fatal("Exception caught while receiving a FEnetwork packet");
                        }
                    }

                }
            }, "FEServerNetworkThread");
            serverThread.start();
        } catch (IOException e) {
            e.printStackTrace();
            return 1;
        }
        return 0;
    }

    private boolean validateConnection(Socket socket, DataInputStream dataInputStream) {
        try {
            // Read the initial bytes from the socket and perform protocol verification
            byte[] buffer = new byte[13];
            dataInputStream.read(buffer);

            // Perform protocol verification based on the read bytes
            // Check if the first 9 bytes represent the channel name and the next 4 bytes represent the channel version
            String channelName = new String(buffer, 0, 9);
            int channelVersion = ByteBuffer.wrap(buffer, 9, 4).getInt();
            //System.out.println("Recieved client with channel: "+channelName+", version: "+channelVersion);
            // Ensure that the random integer is within the 4-byte range
            //randomInt = randomInt & 0xFFFFFFFF;
            
            // Perform checks on the channel name and version
            if(channelName.equals("FENetwork")) {
                if(channelVersion==ModuleNetworking.channelVersion) {
                    System.out.println("Valid connection detected, Continuing.");
                    return true;
                }
                System.out.println("Client tried joining with mismatched channel version! Closing connection.");
                return false;
            }
            handleInvalidConnection(socket);
            System.out.println("Invalid connection detected! Closing connection.");
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void handleInvalidConnection(Socket socket) {
        try {
            String errorMessage = "Invalid protocol detected trying to access this ForgeEssentials Server Network!\n"
                    + "This protocol can only be used for connecting between two servers running ForgeEssentials 16.0.0+";
            byte[] errorBytes = errorMessage.getBytes(StandardCharsets.UTF_8);

            // Create an OutputStream from the socket
            OutputStream outputStream = socket.getOutputStream();

            // Send the error message as a response
            outputStream.write(errorBytes);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int stopServer(boolean sendClosePacket) {
        try {
            closeAllSessions(sendClosePacket);
            if (serverSocket != null) {
                serverRunning=false;
                serverSocket.close();
                System.out.println("Server stopped");
                return 0;
            }
            return 1;
        } catch (IOException e) {
            e.printStackTrace();
            return 1;
        }
    }

    private void handleConnection(Socket socket, DataInputStream dataInputStream) {
        try {

            // Check if there is data available to be read
            while (dataInputStream.available() <= 0) {
                // Wait for a short duration before checking again
                Thread.sleep(100);
            }
            
            // Read the packet type identifier
            int packetTypeOrdinal = dataInputStream.readInt();
            PacketType packetType = null;
            if (packetTypeOrdinal >= 0 && packetTypeOrdinal < PacketType.values().length) {
                packetType = PacketType.values()[packetTypeOrdinal];
            } else {
                // Handle invalid packet type here
                // For example, you can close the connection or send an error response
                System.out.println("Invalid packet type received. Closing connection.");
                socket.close();
                return;
            }

            Session session = null;

            if (packetType != PacketType.PASSWORD) {
                //if(packetType.equals(PacketType.CLOSE_SESSION)) {
                //    socket.close();
                //    return;
                //}
                // Check if the session is authenticated
                session = findSessionBySocket(socket);
                if (session == null || !isSessionAuthenticated(session)) {
                    // Client not authenticated or session not found, reject the connection
                    System.out.println("Client not authenticated or session not found. Closing connection.");
                    socket.close();
                    return;
                }
            } else {
                // Read the packet data length
                int packetDataLength = dataInputStream.readInt();

                // Read the packet data
                byte[] packetData = new byte[packetDataLength];
                dataInputStream.read(packetData);

                // Read the password packet
                ClientPasswordPacket passwordPacket = new ClientPasswordPacket(null).decode(packetData);
                String password = passwordPacket.getPassword();

                // Authenticate the client
                if (!authenticate(password)) {
                    // Authentication failed, reject the connection
                    System.out.println("Authentication failed for client. Closing connection.");
                    sendPacketToSocket(socket, new ServerPasswordResponcePacket(false));
                    socket.close();
                    return;
                }

                // Create a new session and add it to the list of authenticated sessions
                session = new Session(socket, password);
                sessions.add(session);
                System.out.println("New session created: " + session);
                sendPacketToSession(session, new ServerPasswordResponcePacket(true));
            }

            while (sessions.contains(session)) {
                // Check if there is data available to be read
                if(socket.isClosed()) {
                    System.out.println("Socket was closed1.");
                    return;
                }
                while (!socket.isClosed()&&dataInputStream.available() <= 0) {
                    // Wait for a short duration before checking again
                    Thread.sleep(100);
                }
                if(socket.isClosed()) {
                    System.out.println("Socket was closed2.");
                    return;
                }
                // Read the packet type identifier
                packetTypeOrdinal = dataInputStream.readInt();
                packetType = PacketType.values()[packetTypeOrdinal];

                if (!isSessionAuthenticated(session)) {
                    // Client session not authenticated, reject the packet and close the connection
                    System.out.println("Packet received from unauthenticated session. Closing connection.");
                    socket.close();
                    return;
                }

                // Read the packet data length
                int packetDataLength = dataInputStream.readInt();

                // Read the packet data
                byte[] packetData = new byte[packetDataLength];
                dataInputStream.readFully(packetData);

                // Construct the packet object
                Packet packet = PacketType.decodePacket(packetType, packetData);

                // Handle the received packet
                handlePacket(packet, socket);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        } finally {
            closeSession(findSessionBySocket(socket), socket, false);
        }
    }

    private boolean isSessionAuthenticated(Session session) {
        // Check if the session is authenticated based on your criteria
        // For example, you can check if the session is present in the list of authenticated sessions
        return sessions.contains(session);
    }

    private void handlePacket(Packet packet, Socket socket) {
        if(packet.getDirection().equals(PacketDirection.Server_To_Client)) {
            System.out.println("Invalid packet received from client. Closing session.");
            closeSession(findSessionBySocket(socket), socket, true);
            return;
        }
        if (packet instanceof DataPacket) {
            DataPacket dataPacket = (DataPacket) packet;
            String data = dataPacket.getData();
            System.out.println("Received data: " + data);
            // Process the data packet as needed
            // ...
        } else if (packet instanceof ClientPasswordPacket) {
            // Ignore password packet, as it should only be received initially for authentication
        } else if (packet instanceof CloseSessionPacket) {
            System.out.println("Received CloseSessionPacket");
            closeSession(findSessionBySocket(socket), socket, false);
        } else {
            System.out.println("Invalid packet received. Closing session.");
            closeSession(findSessionBySocket(socket), socket, true);
        }
    }

    private boolean authenticate(String password) {
        // Your authentication logic here
        // Return true if the password is valid, false otherwise
        // For demonstration purposes, let's assume the password is "password123"
        return password.equals("password123");
    }

    private Session findSessionBySocket(Socket socket) {
        for (Session session : sessions) {
            if (session.getSocket() == socket) {
                return session;
            }
        }
        return null;
    }

    private void closeAllSessions(boolean sendClosePacket) {
        for(Session s : sessions) {
            try{
                System.out.println("Socket Close1.");
                if(sendClosePacket) {sendPacketToSocket(s.getSocket(), new CloseSessionPacket());}
                s.getSocket().close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        sessions.clear();
    }
    private void closeSession(Session session, Socket fallBackSocket, boolean sendClosePacket) {
        if (session != null) {
            try {
                try {
                    sessions.remove(session);
                }catch(Exception e) {}
                if(sendClosePacket) {sendPacketToSocket(session.getSocket(), new CloseSessionPacket());}
                session.getSocket().close();
                System.out.println("Closed session: " + session.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                if(sendClosePacket) {sendPacketToSocket(fallBackSocket, new CloseSessionPacket());}
                fallBackSocket.close();
                System.out.println("Closed socket: " + fallBackSocket.getInetAddress().getHostAddress() + ":" + fallBackSocket.getPort());
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("FeNetwork Session and socket not found. Cannot close socket.");
            }
        }
    }
    private void sendPacketToSocket(Socket socket, Packet packet) {
        try (DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream())) {
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
        }
    }
    public void sendPacketToSession(Session session, Packet packet) {
        sendPacketToSocket(session.getSocket(), packet);
    }
    public void sendPacketToAllSessions(Packet packet) {
        for(Session s : sessions) {
            sendPacketToSession(s, packet);

        }
    }
}