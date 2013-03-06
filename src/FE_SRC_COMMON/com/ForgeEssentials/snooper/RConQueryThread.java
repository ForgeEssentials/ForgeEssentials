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
import com.ForgeEssentials.util.OutputHandler;

public class RConQueryThread implements Runnable
{
	/** The time of the last client auth check */
	private long									lastAuthCheckTime;

	/** The RCon query port */
	private int										queryPort;

	/** Port the server is running on */
	private int										serverPort;

	/** The remote socket querying the server */
	private DatagramSocket							querySocket			= null;

	/** A buffer for incoming DatagramPackets */
	private byte[]									buffer				= new byte[1460];

	/** Storage for incoming DatagramPackets */
	private DatagramPacket							incomingPacket		= null;
	/** The hostname of this query server */
	private String									queryHostname;

	/** The hostname of the running server */
	private String									serverHostname;

	/** A map of SocketAddress objects to RConThreadQueryAuth objects */
	private Map<SocketAddress, RConThreadQueryAuth>	queryClients;

	/** True if the Thread is running, false otherwise */
	protected boolean								running				= false;

	/** Thread for this runnable class */
	protected Thread								rconThread;
	protected int									field_72615_d		= 5;

	/** A list of registered DatagramSockets */
	protected List<DatagramSocket>					socketList			= new ArrayList<DatagramSocket>();

	/** A list of registered ServerSockets */
	protected List<ServerSocket>					serverSocketList	= new ArrayList<ServerSocket>();

	private IServer									server;

	public RConQueryThread(IServer par1IServer)
	{
		queryPort = ModuleSnooper.port;
		serverHostname = ModuleSnooper.hostname;
		serverPort = par1IServer.getPort();
		queryHostname = "0.0.0.0";
		server = par1IServer;

		if (0 != serverHostname.length() && !queryHostname.equals(serverHostname))
		{
			queryHostname = serverHostname;
		}
		else
		{
			serverHostname = "0.0.0.0";

			try
			{
				InetAddress var2 = InetAddress.getLocalHost();
				queryHostname = var2.getHostAddress();
			}
			catch (UnknownHostException var3)
			{
				logWarning("Unable to determine local host IP, please set server-ip/hostname in the snooper config : " + var3.getMessage());
			}
		}

		if (0 == queryPort)
		{
			queryPort = serverPort;
			logInfo("Setting default query port to " + queryPort);
		}

		new HashMap<Object, Object>();

		queryClients = new HashMap<SocketAddress, RConThreadQueryAuth>();
		new Date().getTime();
	}

	/**
	 * Sends a byte array as a DatagramPacket response to the client who sent
	 * the given DatagramPacket
	 */
	private void sendResponsePacket(byte[] par1ArrayOfByte, DatagramPacket par2DatagramPacket) throws IOException
	{
		querySocket.send(new DatagramPacket(par1ArrayOfByte, par1ArrayOfByte.length, par2DatagramPacket.getSocketAddress()));
	}

	/**
	 * Parses an incoming DatagramPacket, returning true if the packet was valid
	 */
	private boolean parseIncomingPacket(DatagramPacket par1DatagramPacket) throws IOException
	{
		byte[] var2 = par1DatagramPacket.getData();
		int var3 = par1DatagramPacket.getLength();
		SocketAddress var4 = par1DatagramPacket.getSocketAddress();
		logDebug("Packet len " + var3 + " [" + var4 + "]");

		if (3 <= var3 && -2 == var2[0] && -3 == var2[1])
		{
			logDebug("Packet \'" + RConUtils.getByteAsHexString(var2[2]) + "\' [" + var4 + "]");

			if (var2[2] == 9)
			{
				sendAuthChallenge(par1DatagramPacket);
				logDebug("Challenge [" + var4 + "]");
				return true;
			}
			else
			{
				if (!verifyClientAuth(par1DatagramPacket).booleanValue())
				{
					logDebug("Invalid challenge [" + var4 + "]");
					return false;
				}
				else
				{
					Response response = ResponseRegistry.getResponse(var2[2]);
					if (response == null)
						return false;
					byte[] bt = response.getResponceByte(getRequestId(par1DatagramPacket.getSocketAddress()), par1DatagramPacket);
					sendResponsePacket(bt, par1DatagramPacket);
					logDebug("Case " + var2[2] + " [" + var4 + "] ");
					return true;
				}
			}
		}
		else
		{
			logDebug("Invalid packet [" + var4 + "]");
			return false;
		}
	}

	/**
	 * Returns the request ID provided by the authorized client
	 */
	private byte[] getRequestId(SocketAddress par1SocketAddress)
	{
		return ((RConThreadQueryAuth) queryClients.get(par1SocketAddress)).getRequestId();
	}

	/**
	 * Returns true if the client has a valid auth, otherwise false
	 */
	private Boolean verifyClientAuth(DatagramPacket par1DatagramPacket)
	{
		SocketAddress var2 = par1DatagramPacket.getSocketAddress();

		if (!queryClients.containsKey(var2))
			return Boolean.valueOf(false);
		else
		{
			byte[] var3 = par1DatagramPacket.getData();
			return ((RConThreadQueryAuth) queryClients.get(var2)).getRandomChallenge() != RConUtils.getBytesAsBEint(var3, 7, par1DatagramPacket.getLength()) ? Boolean.valueOf(false) : Boolean.valueOf(true);
		}
	}

	/**
	 * Sends an auth challenge DatagramPacket to the client and adds the client
	 * to the queryClients map
	 */
	private void sendAuthChallenge(DatagramPacket par1DatagramPacket) throws IOException
	{
		RConThreadQueryAuth var2 = new RConThreadQueryAuth(this, par1DatagramPacket);
		queryClients.put(par1DatagramPacket.getSocketAddress(), var2);
		sendResponsePacket(var2.getChallengeValue(), par1DatagramPacket);
	}

	/**
	 * Removes all clients whose auth is no longer valid
	 */
	private void cleanQueryClientsMap()
	{
		if (running)
		{
			long var1 = System.currentTimeMillis();

			if (var1 >= lastAuthCheckTime + 30000L)
			{
				lastAuthCheckTime = var1;
				Iterator<?> var3 = queryClients.entrySet().iterator();

				while (var3.hasNext())
				{
					Entry<?, ?> var4 = (Entry<?, ?>) var3.next();

					if (((RConThreadQueryAuth) var4.getValue()).hasExpired(var1).booleanValue())
					{
						var3.remove();
					}
				}
			}
		}
	}

	@Override
	public void run()
	{
		logInfo("Query running on " + serverHostname + ":" + queryPort);
		lastAuthCheckTime = System.currentTimeMillis();
		incomingPacket = new DatagramPacket(buffer, buffer.length);

		try
		{
			while (running)
			{
				try
				{
					querySocket.receive(incomingPacket);
					cleanQueryClientsMap();
					parseIncomingPacket(incomingPacket);
				}
				catch (SocketTimeoutException var7)
				{
					cleanQueryClientsMap();
				}
				catch (PortUnreachableException var8)
				{
					;
				}
				catch (IOException var9)
				{
					stopWithException(var9);
				}
			}
		}
		finally
		{
			closeAllSockets();
		}
	}

	/**
	 * Stops the query server and reports the given Exception
	 */
	private void stopWithException(Exception par1Exception)
	{
		if (running)
		{
			logWarning("Unexpected exception, buggy JRE? (" + par1Exception.toString() + ")");

			if (!initQuerySystem())
			{
				logSevere("Failed to recover from buggy JRE, shutting down!");
				running = false;
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
			querySocket = new DatagramSocket(queryPort, InetAddress.getByName(serverHostname));
			registerSocket(querySocket);
			querySocket.setSoTimeout(500);
			return true;
		}
		catch (SocketException var2)
		{
			logWarning("Unable to initialize query system on " + serverHostname + ":" + queryPort + " (Socket): " + var2.getMessage());
		}
		catch (UnknownHostException var3)
		{
			logWarning("Unable to initialize query system on " + serverHostname + ":" + queryPort + " (Unknown Host): " + var3.getMessage());
		}
		catch (Exception var4)
		{
			logWarning("Unable to initialize query system on " + serverHostname + ":" + queryPort + " (E): " + var4.getMessage());
		}

		return false;
	}

	/**
	 * Creates a new Thread object from this class and starts running
	 */
	public synchronized void startThread()
	{
		if (!running)
		{
			if (0 < queryPort && 65535 >= queryPort)
			{
				if (initQuerySystem())
				{
					rconThread = new Thread(this);
					rconThread.start();
					running = true;
				}
			}
			else
			{
				logWarning("Invalid query port " + queryPort + " found in the Snooper configs! (queries disabled)");
			}
		}
	}

	/**
	 * Returns true if the Thread is running, false otherwise
	 */
	public boolean isRunning()
	{
		return running;
	}

	/**
	 * Log debug message
	 */
	protected void logDebug(String par1Str)
	{
		OutputHandler.finer(par1Str);
	}

	/**
	 * Log information message
	 */
	protected void logInfo(String par1Str)
	{
		server.logInfo(par1Str);
	}

	/**
	 * Log warning message
	 */
	protected void logWarning(String par1Str)
	{
		server.logWarning(par1Str);
	}

	/**
	 * Log severe error message
	 */
	protected void logSevere(String par1Str)
	{
		server.logSevere(par1Str);
	}

	/**
	 * Registers a DatagramSocket with this thread
	 */
	protected void registerSocket(DatagramSocket par1DatagramSocket)
	{
		logDebug("registerSocket: " + par1DatagramSocket);
		socketList.add(par1DatagramSocket);
	}

	/**
	 * Closes the specified DatagramSocket
	 */
	protected boolean closeSocket(DatagramSocket par1DatagramSocket, boolean par2)
	{
		logDebug("closeSocket: " + par1DatagramSocket);

		if (null == par1DatagramSocket)
			return false;
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
				socketList.remove(par1DatagramSocket);
			}

			return var3;
		}
	}

	/**
	 * Closes the specified ServerSocket
	 */
	protected boolean closeServerSocket(ServerSocket par1ServerSocket)
	{
		return closeServerSocket_do(par1ServerSocket, true);
	}

	/**
	 * Closes the specified ServerSocket
	 */
	protected boolean closeServerSocket_do(ServerSocket par1ServerSocket, boolean par2)
	{
		logDebug("closeSocket: " + par1ServerSocket);

		if (null == par1ServerSocket)
			return false;
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
				logWarning("IO: " + var5.getMessage());
			}

			if (par2)
			{
				serverSocketList.remove(par1ServerSocket);
			}

			return var3;
		}
	}

	/**
	 * Closes all of the opened sockets
	 */
	protected void closeAllSockets()
	{
		closeAllSockets_do(false);
	}

	/**
	 * Closes all of the opened sockets
	 */
	protected void closeAllSockets_do(boolean par1)
	{
		int var2 = 0;
		Iterator<DatagramSocket> var3 = socketList.iterator();

		while (var3.hasNext())
		{
			DatagramSocket var4 = (DatagramSocket) var3.next();

			if (closeSocket(var4, false))
			{
				++var2;
			}
		}

		socketList.clear();
		Iterator<ServerSocket> var4 = serverSocketList.iterator();

		while (var4.hasNext())
		{
			ServerSocket var5 = (ServerSocket) var4.next();

			if (closeServerSocket_do(var5, false))
			{
				++var2;
			}
		}

		serverSocketList.clear();

		if (par1 && 0 < var2)
		{
			logWarning("Forcibly closed " + var2 + " sockets");
		}

		if (ModuleSnooper.autoReboot)
		{
			ModuleSnooper.startQuery();
		}
	}

	public void interrupt()
	{
		rconThread.interrupt();
		System.gc();
	}
}
