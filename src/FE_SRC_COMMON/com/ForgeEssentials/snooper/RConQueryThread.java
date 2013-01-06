package com.ForgeEssentials.snooper;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.PortUnreachableException;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.network.rcon.IServer;
import net.minecraft.network.rcon.RConUtils;

import com.ForgeEssentials.api.snooper.Response;

public class RConQueryThread implements Runnable
{
    /** The time of the last client auth check */
    private long lastAuthCheckTime;

    /** The RCon query port */
    private int queryPort;

    /** Port the server is running on */
    private int serverPort;

    /** The maximum number of players allowed on the server */
    private int maxPlayers;

    /** The current server message of the day */
    private String serverMotd;

    /** The name of the currently loaded world */
    private String worldName;

    /** The remote socket querying the server */
    private DatagramSocket querySocket = null;

    /** A buffer for incoming DatagramPackets */
    private byte[] buffer = new byte[1460];

    /** Storage for incoming DatagramPackets */
    private DatagramPacket incomingPacket = null;
    private Map field_72644_p;

    /** The hostname of this query server */
    private String queryHostname;

    /** The hostname of the running server */
    private String serverHostname;

    /** A map of SocketAddress objects to RConThreadQueryAuth objects */
    private Map queryClients;

    /**
     * The time that this RConThreadQuery was constructed, from (new Date()).getTime()
     */
    private long time;
    
    /** True if the Thread is running, false otherwise */
    protected boolean running = false;

    /** Thread for this runnable class */
    protected Thread rconThread;
    protected int field_72615_d = 5;

    /** A list of registered DatagramSockets */
    protected List socketList = new ArrayList();

    /** A list of registered ServerSockets */
    protected List serverSocketList = new ArrayList();

	private IServer server;

    public RConQueryThread(IServer par1IServer)
    {	
        this.queryPort = ModuleSnooper.port;
        this.serverHostname = ModuleSnooper.hostname;
        this.serverPort = par1IServer.getPort();
        this.queryHostname = "0.0.0.0";
        this.server = par1IServer;
        
        if (0 != this.serverHostname.length() && !this.queryHostname.equals(this.serverHostname))
        {
            this.queryHostname = this.serverHostname;
        }
        else
        {
            this.serverHostname = "0.0.0.0";

            try
            {
                InetAddress var2 = InetAddress.getLocalHost();
                this.queryHostname = var2.getHostAddress();
            }
            catch (UnknownHostException var3)
            {
                this.logWarning("Unable to determine local host IP, please set server-ip/hostname in the snooper config : " + var3.getMessage());
            }
        }

        if (0 == this.queryPort)
        {
            this.queryPort = this.serverPort;
            this.logInfo("Setting default query port to " + this.queryPort);
        }

        this.field_72644_p = new HashMap();
        
        this.queryClients = new HashMap();
        this.time = (new Date()).getTime();
    }

    /**
     * Sends a byte array as a DatagramPacket response to the client who sent the given DatagramPacket
     */
    private void sendResponsePacket(byte[] par1ArrayOfByte, DatagramPacket par2DatagramPacket) throws IOException
    {
        this.querySocket.send(new DatagramPacket(par1ArrayOfByte, par1ArrayOfByte.length, par2DatagramPacket.getSocketAddress()));
    }

    /**
     * Parses an incoming DatagramPacket, returning true if the packet was valid
     */
    private boolean parseIncomingPacket(DatagramPacket par1DatagramPacket) throws IOException
    {
        byte[] var2 = par1DatagramPacket.getData();
        int var3 = par1DatagramPacket.getLength();
        SocketAddress var4 = par1DatagramPacket.getSocketAddress();
        this.logDebug("Packet len " + var3 + " [" + var4 + "]");

        if (3 <= var3 && -2 == var2[0] && -3 == var2[1])
        {
            this.logDebug("Packet \'" + RConUtils.getByteAsHexString(var2[2]) + "\' [" + var4 + "]");
            
            if(var2[2] == 9)
            {
            	this.sendAuthChallenge(par1DatagramPacket);
                this.logDebug("Challenge [" + var4 + "]");
                return true;
            }
            else
            {
            	if (!this.verifyClientAuth(par1DatagramPacket).booleanValue())
                {
                    this.logDebug("Invalid challenge [" + var4 + "]");
                    return false;
                }
            	else
            	{
            		Response response = ResponseRegistry.getResponse(var2[2]);
            		if(response == null) return false;
            		byte[] bt = response.getResponceByte(this.getRequestId(par1DatagramPacket.getSocketAddress()), par1DatagramPacket);
            		this.logDebug(new String(bt));
            		this.sendResponsePacket(bt, par1DatagramPacket);
            		this.logDebug("Case " + var2[2] + " [" + var4 + "] ");
            		return true;
            	}
            }
        }
        else
        {
            this.logDebug("Invalid packet [" + var4 + "]");
            return false;
        }
    }
    
    /**
     * Returns the request ID provided by the authorized client
     */
    private byte[] getRequestId(SocketAddress par1SocketAddress)
    {
        return ((RConThreadQueryAuth)this.queryClients.get(par1SocketAddress)).getRequestId();
    }

    /**
     * Returns true if the client has a valid auth, otherwise false
     */
    private Boolean verifyClientAuth(DatagramPacket par1DatagramPacket)
    {
        SocketAddress var2 = par1DatagramPacket.getSocketAddress();

        if (!this.queryClients.containsKey(var2))
        {
            return Boolean.valueOf(false);
        }
        else
        {
            byte[] var3 = par1DatagramPacket.getData();
            return ((RConThreadQueryAuth)this.queryClients.get(var2)).getRandomChallenge() != RConUtils.getBytesAsBEint(var3, 7, par1DatagramPacket.getLength()) ? Boolean.valueOf(false) : Boolean.valueOf(true);
        }
    }

    /**
     * Sends an auth challenge DatagramPacket to the client and adds the client to the queryClients map
     */
    private void sendAuthChallenge(DatagramPacket par1DatagramPacket) throws IOException
    {
        RConThreadQueryAuth var2 = new RConThreadQueryAuth(this, par1DatagramPacket);
        this.queryClients.put(par1DatagramPacket.getSocketAddress(), var2);
        this.sendResponsePacket(var2.getChallengeValue(), par1DatagramPacket);
    }

    /**
     * Removes all clients whose auth is no longer valid
     */
    private void cleanQueryClientsMap()
    {
        if (this.running)
        {
            long var1 = System.currentTimeMillis();

            if (var1 >= this.lastAuthCheckTime + 30000L)
            {
                this.lastAuthCheckTime = var1;
                Iterator var3 = this.queryClients.entrySet().iterator();

                while (var3.hasNext())
                {
                    Entry var4 = (Entry)var3.next();

                    if (((RConThreadQueryAuth)var4.getValue()).hasExpired(var1).booleanValue())
                    {
                        var3.remove();
                    }
                }
            }
        }
    }

    public void run()
    {
        this.logInfo("Query running on " + this.serverHostname + ":" + this.queryPort);
        this.lastAuthCheckTime = System.currentTimeMillis();
        this.incomingPacket = new DatagramPacket(this.buffer, this.buffer.length);

        try
        {
            while (this.running)
            {
                try
                {
                    this.querySocket.receive(this.incomingPacket);
                    this.cleanQueryClientsMap();
                    this.parseIncomingPacket(this.incomingPacket);
                }
                catch (SocketTimeoutException var7)
                {
                    this.cleanQueryClientsMap();
                }
                catch (PortUnreachableException var8)
                {
                    ;
                }
                catch (IOException var9)
                {
                    this.stopWithException(var9);
                }
            }
        }
        finally
        {
            this.closeAllSockets();
        }
    }

    /**
     * Stops the query server and reports the given Exception
     */
    private void stopWithException(Exception par1Exception)
    {
        if (this.running)
        {
            this.logWarning("Unexpected exception, buggy JRE? (" + par1Exception.toString() + ")");

            if (!this.initQuerySystem())
            {
                this.logSevere("Failed to recover from buggy JRE, shutting down!");
                this.running = false;
            }
        }
    }

    /**
     * Initializes the query system by binding it to a port
     */
    private boolean initQuerySystem()
    {
        try
        {
            this.querySocket = new DatagramSocket(this.queryPort, InetAddress.getByName(this.serverHostname));
            this.registerSocket(this.querySocket);
            this.querySocket.setSoTimeout(500);
            return true;
        }
        catch (SocketException var2)
        {
            this.logWarning("Unable to initialise query system on " + this.serverHostname + ":" + this.queryPort + " (Socket): " + var2.getMessage());
        }
        catch (UnknownHostException var3)
        {
            this.logWarning("Unable to initialise query system on " + this.serverHostname + ":" + this.queryPort + " (Unknown Host): " + var3.getMessage());
        }
        catch (Exception var4)
        {
            this.logWarning("Unable to initialise query system on " + this.serverHostname + ":" + this.queryPort + " (E): " + var4.getMessage());
        }

        return false;
    }
    
    /**
     * Creates a new Thread object from this class and starts running
     */
    public synchronized void startThread()
    {
    	if (!this.running)
        {
            if (0 < this.queryPort && 65535 >= this.queryPort)
            {
                if (this.initQuerySystem())
                {
                	this.rconThread = new Thread(this);
                    this.rconThread.start();
                    this.running = true;
                }
            }
            else
            {
                this.logWarning("Invalid query port " + this.queryPort + " found in the Snooper configs! (queries disabled)");
            }
        }
    }

    /**
     * Returns true if the Thread is running, false otherwise
     */
    public boolean isRunning()
    {
        return this.running;
    }

    /**
     * Log debug message
     */
    protected void logDebug(String par1Str)
    {
        this.server.logDebug(par1Str);
    }

    /**
     * Log information message
     */
    protected void logInfo(String par1Str)
    {
        this.server.logInfo(par1Str);
    }

    /**
     * Log warning message
     */
    protected void logWarning(String par1Str)
    {
        this.server.logWarning(par1Str);
    }

    /**
     * Log severe error message
     */
    protected void logSevere(String par1Str)
    {
        this.server.logSevere(par1Str);
    }

    /**
     * Registers a DatagramSocket with this thread
     */
    protected void registerSocket(DatagramSocket par1DatagramSocket)
    {
        this.logDebug("registerSocket: " + par1DatagramSocket);
        this.socketList.add(par1DatagramSocket);
    }

    /**
     * Closes the specified DatagramSocket
     */
    protected boolean closeSocket(DatagramSocket par1DatagramSocket, boolean par2)
    {
        this.logDebug("closeSocket: " + par1DatagramSocket);

        if (null == par1DatagramSocket)
        {
            return false;
        }
        else
        {
            boolean var3 = false;

            if (!par1DatagramSocket.isClosed())
            {
                par1DatagramSocket.close();
                var3 = true;
            }

            if (par2)
            {
                this.socketList.remove(par1DatagramSocket);
            }

            return var3;
        }
    }

    /**
     * Closes the specified ServerSocket
     */
    protected boolean closeServerSocket(ServerSocket par1ServerSocket)
    {
        return this.closeServerSocket_do(par1ServerSocket, true);
    }

    /**
     * Closes the specified ServerSocket
     */
    protected boolean closeServerSocket_do(ServerSocket par1ServerSocket, boolean par2)
    {
        this.logDebug("closeSocket: " + par1ServerSocket);

        if (null == par1ServerSocket)
        {
            return false;
        }
        else
        {
            boolean var3 = false;

            try
            {
                if (!par1ServerSocket.isClosed())
                {
                    par1ServerSocket.close();
                    var3 = true;
                }
            }
            catch (IOException var5)
            {
                this.logWarning("IO: " + var5.getMessage());
            }

            if (par2)
            {
                this.serverSocketList.remove(par1ServerSocket);
            }

            return var3;
        }
    }

    /**
     * Closes all of the opened sockets
     */
    protected void closeAllSockets()
    {
        this.closeAllSockets_do(false);
    }

    /**
     * Closes all of the opened sockets
     */
    protected void closeAllSockets_do(boolean par1)
    {
        int var2 = 0;
        Iterator var3 = this.socketList.iterator();

        while (var3.hasNext())
        {
            DatagramSocket var4 = (DatagramSocket)var3.next();

            if (this.closeSocket(var4, false))
            {
                ++var2;
            }
        }

        this.socketList.clear();
        var3 = this.serverSocketList.iterator();

        while (var3.hasNext())
        {
            ServerSocket var5 = (ServerSocket)var3.next();

            if (this.closeServerSocket_do(var5, false))
            {
                ++var2;
            }
        }

        this.serverSocketList.clear();

        if (par1 && 0 < var2)
        {
            this.logWarning("Force closed " + var2 + " sockets");
        }
        
        if(ModuleSnooper.autoReboot)
        {
        	ModuleSnooper.startQuery();
        }
    }
}
