package com.ForgeEssentials.chat.irc;

import java.io.IOException;
import java.util.ArrayList;

import org.schwering.irc.lib.IRCConnection;

import com.ForgeEssentials.util.OutputHandler;

public class IRCThread extends Thread {
	public IRCConnection connect;
	private String host = "irc.esper.net";
	private String password = null;

	  private String nickname = "feirctest";

	  private String quitMsg;
	  private String channel = "#forgeessentials";
	  public static ArrayList<String> postqueue = new ArrayList<String>();
	 
	  public IRCThread(){
		  OutputHandler.felog.info("Attempting to establish connection to IRC server "+ host);
		  connect = new IRCConnection(host, new int[]{ 5555, 6667} , password, nickname, nickname, nickname);
		  connect.addIRCEventListener(new IRCEventHandler());
			connect.setEncoding("UTF-8");
			connect.setPong(true);
			connect.setDaemon(false);
			connect.setColors(false);
			try {
				connect.connect();
				connect.doJoin(channel);
			} catch (IOException e) {
				OutputHandler.felog.warning("Could not connect to IRC server "+ host + ", aborting! Run /irc reconnect to reattempt.");
			}
			start();
	  }
	  public void run(){
		  while (true){
			  for (Object s : postqueue.toArray()){
				  connect.send(s.toString());
				  postqueue.clear();
			  }
		  }
	  }
	  public void joinChannels(){
		  connect.doJoin(channel);
		  System.out.println("Joined channel:" + channel);
	  }
	  public void post(String line){
		  
	  }
	  public void endConnection(){
		  connect.doQuit();
	  }

}
