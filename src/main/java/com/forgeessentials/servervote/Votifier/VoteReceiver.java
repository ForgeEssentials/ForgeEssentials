/*
 * Copyright (C) 2012 Vex Software LLC
 * This file -was- part of Votifier.
 * 
 * Votifier is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Votifier is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Votifier.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.forgeessentials.servervote.Votifier;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.FMLLog;

import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.servervote.ConfigServerVote;
import com.forgeessentials.servervote.VoteEvent;
import com.forgeessentials.util.output.LoggingHandler;

/**
 * Like 90% copied from Votifier github: https://github.com/vexsoftware/votifier I only changed the init code and the
 * event stuff.
 *
 * @author Dries007
 */
public class VoteReceiver extends Thread
{
    /**
     * The host to listen on.
     */
    private final String host;

    /**
     * The port to listen on.
     */
    private final int port;

    /**
     * The server socket.
     */
    private ServerSocket server;

    /**
     * The running flag.
     */
    private boolean running = true;

    public VoteReceiver(String host, int port) throws Exception
    {
        if (host.isEmpty())
        {
            host = "0.0.0.0";

            try
            {
                InetAddress var2 = InetAddress.getLocalHost();
                host = var2.getHostAddress();
            }
            catch (UnknownHostException var3)
            {
                FMLLog.severe("Unable to determine local host IP, please set server-ip/hostname in the servervote config : " + var3.getMessage());
            }
        }

        this.host = host;
        this.port = port;

        initialize();
    }

    private void initialize() throws Exception
    {
        try
        {
            server = new ServerSocket();
            server.bind(new InetSocketAddress(host, port));
            LoggingHandler.felog.info("Votifier connection handler initialized!");
        }
        catch (Exception ex)
        {
            FMLLog.severe("Error initializing vote receiver. Please verify that the configured");
            FMLLog.severe("IP address and port are not already in use. This is a common problem");
            FMLLog.severe("with hosting services and, if so, you should check with your hosting provider." + ex);
            throw new Exception(ex);
        }
    }

    /**
     * Shuts the vote receiver down cleanly.
     */
    public void shutdown()
    {
        running = false;
        if (server == null)
        {
            return;
        }
        try
        {
            server.close();
        }
        catch (Exception ex)
        {
            FMLLog.severe("Unable to shut down vote receiver cleanly.");
        }
        System.gc();
    }

    @Override
    public void run()
    {
        while (running)
        {
            try
            {
                Socket socket = server.accept();
                socket.setSoTimeout(5000); // Don't hang on slow connections.
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                InputStream in = socket.getInputStream();

                // Send them our version.
                writer.write("VOTIFIER FECOMPAT");
                writer.newLine();
                writer.flush();

                // Read the 256 byte block.
                byte[] block = new byte[256];
                in.read(block, 0, block.length);

                // Decrypt the block.
                Cipher cipher = Cipher.getInstance("RSA");
                cipher.init(Cipher.DECRYPT_MODE, ConfigServerVote.privateKey);
                block = cipher.doFinal(block);
                int position = 0;

                // Perform the opcode check.
                String opcode = readString(block, position);
                position += opcode.length() + 1;
                // Something went wrong in RSA.
                if (!opcode.equals("VOTE"))
                {
                    LoggingHandler.felog.error("Could not decrypt vote payload!");
                    if (ForgeEssentials.isDebug())
                    {
                        LoggingHandler.felog.error("Vote payload (for debugging):");
                        LoggingHandler.felog.error(new String(block));
                    }
                    throw new GeneralSecurityException();
                }

                // Parse the block.
                String serviceName = readString(block, position);
                position += serviceName.length() + 1;
                String username = readString(block, position);
                position += username.length() + 1;
                String address = readString(block, position);
                position += address.length() + 1;
                String timeStamp = readString(block, position);
                position += timeStamp.length() + 1;

                // Create the vote.
                VoteEvent vote = new VoteEvent(username, serviceName, address, timeStamp);

                EntityPlayerMP player = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUsername(vote.player);
                if (player == null)
                {
                    if (!ConfigServerVote.allowOfflineVotes)
                    {
                        LoggingHandler.felog.debug("Player for vote not online, vote canceled.");
                        vote.setFeedback("notOnline");
                        vote.setCanceled(true);
                    }
                }

                if (!vote.isCanceled())
                {
                    MinecraftForge.EVENT_BUS.post(vote);
                }

                // Clean up.
                writer.close();
                in.close();
                socket.close();
            }
            catch (SocketException ex)
            {
                FMLLog.severe("Protocol error. Ignoring packet");
            }
            catch (BadPaddingException ex)
            {
                FMLLog.severe("Unable to decrypt vote record. Make sure that that your public key matches the one you gave the server list.");
            }
            catch (IOException ex)
            {
                FMLLog.severe("Exception caught while receiving a vote notification");
            }
            catch (GeneralSecurityException e)
            {
                FMLLog.severe("Unable to decode vote");
            }
        }

        System.gc();
    }

    /**
     * Reads a string from a block of data.
     *
     * @param data
     *            The data to read from
     * @return The string
     */
    private static String readString(byte[] data, int offset)
    {
        StringBuilder builder = new StringBuilder();
        for (int i = offset; i < data.length; i++)
        {
            if (data[i] == '\n')
            {
                break; // Delimiter reached.
            }
            builder.append((char) data[i]);
        }
        return builder.toString();
    }
}
