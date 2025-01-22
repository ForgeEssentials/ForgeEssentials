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

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLLog;

import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.servervote.ConfigServerVote;
import com.forgeessentials.servervote.VoteEvent;
import com.forgeessentials.util.output.LoggingHandler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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

    private static final SecureRandom RANDOM = new SecureRandom();

    private final String challenge;

    private final Gson gson = new GsonBuilder().create();

    private Key key;

    public static String newToken()
    {
        return new BigInteger(130, RANDOM).toString(32);
    }

    public void setKey(String token)
    {
        if (token != null && !token.isEmpty())
        {
            key = new SecretKeySpec(token.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        }
    }

    public VoteReceiver(String host, int port, String token) throws Exception
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
        this.challenge = newToken();
        setKey(token);
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

    private boolean hmacEqual(byte[] sig, byte[] message, Key key) throws NoSuchAlgorithmException, InvalidKeyException
    {
        // See https://www.nccgroup.trust/us/about-us/newsroom-and-events/blog/2011/february/double-hmac-verification/
        // This randomizes the byte order to make timing attacks more difficult.
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(key);
        byte[] calculatedSig = mac.doFinal(message);

        // Generate a random key for use in comparison
        byte[] randomKey = new byte[32];
        RANDOM.nextBytes(randomKey);

        // Then generate two HMACs for the different signatures found
        Mac mac2 = Mac.getInstance("HmacSHA256");
        mac2.init(new SecretKeySpec(randomKey, "HmacSHA256"));
        byte[] clientSig = mac2.doFinal(sig);
        mac2.reset();
        byte[] realSig = mac2.doFinal(calculatedSig);

        return MessageDigest.isEqual(clientSig, realSig);
    }

    @Override
    public void run()
    {
        while (running)
        {
            try (
                    Socket socket = server.accept();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                    BufferedInputStream in = new BufferedInputStream(socket.getInputStream());
            )
            {
                socket.setSoTimeout(5000); // Don't hang on slow connections.

                // Send them our version.
                writer.write("VOTIFIER FECOMPAT " + challenge + "\n");
                writer.flush();

                VoteEvent vote = null;

                in.mark(256);
                int header = (in.read() << 8) + in.read();
                if (header == 0x733a)
                {
                    int length = (in.read() << 8) + in.read();
                    byte[] block = new byte[length];
                    int off = 0;
                    while (off < length)
                    {
                        int n = in.read(block, off, block.length - off);
                        if (n <= 0)
                        {
                            break;
                        }
                        off += n;
                    }
                    String message = new String(block);
                    HashMap<?,?> data = gson.fromJson(message, HashMap.class);
                    String signature = data.get("signature").toString();
                    String payload = data.get("payload").toString();

                    byte[] sig = Base64.getDecoder().decode(signature);
                    if (key == null || !hmacEqual(sig, payload.getBytes(StandardCharsets.UTF_8), key))
                    {
                        writer.write("{\"status\":\"error\",\"errorMessage\":\"Invalid Signature!\"}");
                        writer.flush();
                    }
                    else
                    {
                        HashMap<?,?> _payload = gson.fromJson(payload, HashMap.class);
                        if (!challenge.equals(_payload.get("challenge")))
                        {
                            writer.write("{\"status\":\"error\",\"errorMessage\":\"Invalid Challenge!\"}");
                            writer.flush();
                        }
                        else
                        {
                            try
                            {
                                vote = new VoteEvent(_payload.get("username").toString(), _payload.get("serviceName").toString(),
                                        _payload.get("address").toString(), _payload.get("timestamp").toString());
                                writer.write("{\"status\":\"ok\"}");
                                writer.flush();
                            }
                            catch (NullPointerException e)
                            {
                                writer.write("{\"status\":\"error\",\"errorMessage\":\"Malformed Vote Payload\"}");
                                writer.flush();
                            }
                        }
                    }
                }
                else if (ConfigServerVote.allowClassic)
                {
                    in.reset();
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
                    vote = new VoteEvent(username, serviceName, address, timeStamp);
                }
                else
                {
                    LoggingHandler.felog.error("Classic Votifier Disabled, packet ignored!");
                }

                if (vote != null)
                {
                    EntityPlayerMP player = MinecraftServer.getServer().getConfigurationManager().getPlayerByUsername(vote.player);
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
                }
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
     * @param data The data to read from
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
