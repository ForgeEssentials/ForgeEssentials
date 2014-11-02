package com.forgeessentials.snooper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import com.forgeessentials.api.snooper.Response;
import com.forgeessentials.util.OutputHandler;
import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

public class SocketHandler extends Thread
{
    private SocketListner listner;
    public Socket socket;
    private OutputStream os;
    private InputStream is;

    public SocketHandler(Socket socket, SocketListner socketListner)
    {
        listner = socketListner;
        this.socket = socket;
        setName("ForgeEssentials - Snooper - SocketHandler #" + ModuleSnooper.id());
        start();
    }

    @Override
    public void run()
    {
        OutputHandler.debug("Snooper connection: " + socket.getInetAddress().getHostAddress() + ":" + socket.getLocalPort());
        JsonParser parser = new JsonParser();

        try
        {
            is = socket.getInputStream();
            os = socket.getOutputStream();

            int i = is.read();
            String inString = new String(ByteStreams.toByteArray(is), Charsets.UTF_8);
            String inDecr = Security.decrypt(inString, ModuleSnooper.key);

            String out;
            try
            {
                out = Security.encrypt(getResponse(i, parser.parse(inDecr)), ModuleSnooper.key);
            }
            catch (Exception e)
            {
                out = Security.encrypt(getResponse(i, new JsonObject()), ModuleSnooper.key);
            }

            os.write(out.getBytes(Charsets.UTF_8));
            os.flush();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        close();
    }

    private static String getResponse(int i, JsonElement jsonElement)
    {
        try
        {
            Response response = ResponseRegistry.getResponse(i);
            if (response.allowed)
            {
                return response.getResponse(jsonElement.getAsJsonObject()).toString();
            }
        }
        catch (JsonParseException e)
        {
            e.printStackTrace();
        }
        return "";
    }

    public void close()
    {
        try
        {
            socket.close();
            listner.socketList.remove(this);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        System.gc();
    }
}
