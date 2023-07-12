package com.forgeessentials.serverNetwork.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.forgeessentials.serverNetwork.server.packets.DataPacket;
import com.forgeessentials.serverNetwork.server.packets.Packet;
import com.forgeessentials.serverNetwork.server.packets.PacketDirection;
import com.forgeessentials.serverNetwork.server.packets.PacketType;
import com.forgeessentials.serverNetwork.server.packets.bidirectional.CloseSessionPacket;
import com.forgeessentials.serverNetwork.server.packets.clientTypes.ClientPasswordPacket;
import com.forgeessentials.serverNetwork.server.packets.serverTypes.ServerPasswordResponcePacket;

public class FeNetworkServer
{
    private final String remoteServerHost;
    private final int remoteServerPort;
    private ServerSocket serverSocket;
    private List<Session> sessions;
    private int numConenctionsTotal =0;

    public FeNetworkServer(String remoteServerHost, int remoteServerPort) {
        this.remoteServerHost = remoteServerHost;
        this.remoteServerPort = remoteServerPort;
        sessions = new ArrayList<>();
    }

    public void startServer() {
        try {
            serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress(remoteServerHost, remoteServerPort));
            System.out.println("Server started on " + serverSocket.getInetAddress().getHostAddress() + ":" + serverSocket.getLocalPort());

            while (true) {
                Socket socket = serverSocket.accept();
                numConenctionsTotal+=1;
                Thread connectionThread = new Thread(() -> handleConnection(socket), "FeServerListening"+numConenctionsTotal);
                connectionThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopServer() {
        try {
            closeAllSessions();
            if (serverSocket != null) {
                serverSocket.close();
                System.out.println("Server stopped");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleConnection(Socket socket) {
        try (DataInputStream inputStream = new DataInputStream(socket.getInputStream());
             DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream())) {

            // Read the packet type identifier
            int packetTypeOrdinal = inputStream.readInt();
            PacketType packetType = PacketType.values()[packetTypeOrdinal];

            Session session = null;

            if (packetType != PacketType.PASSWORD) {
                if(packetType.equals(PacketType.CLOSE_SESSION)) {
                    socket.close();
                    return;
                }
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
                int packetDataLength = inputStream.readInt();

                // Read the packet data
                byte[] packetData = new byte[packetDataLength];
                inputStream.readFully(packetData);

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
                // Read the packet type identifier
                packetTypeOrdinal = inputStream.readInt();
                packetType = PacketType.values()[packetTypeOrdinal];

                if (!isSessionAuthenticated(session)) {
                    // Client session not authenticated, reject the packet and close the connection
                    System.out.println("Packet received from unauthenticated session. Closing connection.");
                    socket.close();
                    return;
                }

                // Read the packet data length
                int packetDataLength = inputStream.readInt();

                // Read the packet data
                byte[] packetData = new byte[packetDataLength];
                inputStream.readFully(packetData);

                // Construct the packet object
                Packet packet = PacketType.decodePacket(packetType, packetData);

                // Handle the received packet
                handlePacket(packet, socket);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                closeSession(findSessionBySocket(socket));
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
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
            sendPacketToSocket(socket, new CloseSessionPacket());
            closeSession(findSessionBySocket(socket));
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
            closeSession(findSessionBySocket(socket));
        } else {
            System.out.println("Invalid packet received. Closing session.");
            closeSession(findSessionBySocket(socket));
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

    private void closeAllSessions() {
        sessions.clear();
    }
    private void closeSession(Session session) {
        if (session != null) {
            try {
                try {
                    sessions.remove(session);
                }catch(Exception e) {}
                session.getSocket().close();
                System.out.println("Closed session: " + session);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("FeNetwork Session not found. Cannot close session.");
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
}