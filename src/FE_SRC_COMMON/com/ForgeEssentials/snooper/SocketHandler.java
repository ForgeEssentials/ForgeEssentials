package com.ForgeEssentials.snooper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import com.ForgeEssentials.api.json.JSONException;
import com.ForgeEssentials.api.json.JSONObject;
import com.ForgeEssentials.api.snooper.Response;
import com.ForgeEssentials.util.OutputHandler;

public class SocketHandler extends Thread
{
	private SocketListner	listner;
	public Socket			socket;
	private OutputStream	os;
	private InputStream		is;
	
	public SocketHandler(Socket socket, SocketListner socketListner)
	{
		this.listner = socketListner;
		this.socket = socket;
		this.setName("ForgeEssentials - Snooper - SocketHandler #" + ModuleSnooper.id());
		this.start();
	}

	public void run()
	{
		OutputHandler.debug("Snooper connection: " + socket.getInetAddress().getHostAddress() + ":" + socket.getLocalPort());

		try
		{
			is = socket.getInputStream();
			os = socket.getOutputStream();
			
			int i = is.read();
			byte[] inBuffer = new byte[is.available()];
			is.read(inBuffer);
			String inString = new String(inBuffer);
			String inDecr = Security.decrypt(inString, ModuleSnooper.key);
			
			String out;
			try
			{
				out = Security.encrypt(getResponce((byte) i, new JSONObject(inDecr)), ModuleSnooper.key);
			}
			catch (Exception e)
			{
				out = Security.encrypt(getResponce((byte) i, new JSONObject()), ModuleSnooper.key);
			}
			
			os.write(out.getBytes());
			os.flush();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		close();
	}

	private String getResponce(byte i, JSONObject input)
	{
		try
		{
			Response responce = ResponseRegistry.getResponse(i);
			if(responce.allowed)
				return responce.getResponce(input).toString();
		}
		catch (JSONException e)
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
