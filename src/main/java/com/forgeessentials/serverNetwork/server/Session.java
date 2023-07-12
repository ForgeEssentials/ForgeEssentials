package com.forgeessentials.serverNetwork.server;

import java.net.Socket;
import java.util.UUID;

public class Session {
    private final String sessionId;
    private final Socket socket;
    private final String password;

    public Session(Socket socket, String password) {
        this.sessionId = UUID.randomUUID().toString();
        this.socket = socket;
        this.password = password;
    }

    public String getSessionId() {
        return sessionId;
    }

    public Socket getSocket() {
        return socket;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "Session{" +
                "sessionId='" + sessionId + '\'' +
                ", socket=" + socket.getInetAddress().getHostAddress() + ":" + socket.getPort() +
                ", password='" + password + '\'' +
                '}';
    }
}
