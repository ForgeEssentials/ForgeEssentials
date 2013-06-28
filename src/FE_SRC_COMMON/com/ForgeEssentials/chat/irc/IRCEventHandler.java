package com.ForgeEssentials.chat.irc;

import org.schwering.irc.lib.IRCEventListener;
import org.schwering.irc.lib.IRCModeParser;
import org.schwering.irc.lib.IRCUser;

public class IRCEventHandler implements IRCEventListener{

	@Override
	public void onRegistered() {
		System.out.println("Registered");
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onError(String msg) {
		System.out.println("Errored: "+ msg);
		
	}

	@Override
	public void onError(int num, String msg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onInvite(String chan, IRCUser user, String passiveNick) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onJoin(String chan, IRCUser user) {
		System.out.println(user.getUsername() + " joined channel");
		
	}

	@Override
	public void onKick(String chan, IRCUser user, String passiveNick, String msg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMode(String chan, IRCUser user, IRCModeParser modeParser) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMode(IRCUser user, String passiveNick, String mode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNick(IRCUser user, String newNick) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNotice(String target, IRCUser user, String msg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPart(String chan, IRCUser user, String msg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPing(String ping) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPrivmsg(String target, IRCUser user, String msg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onQuit(IRCUser user, String msg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onReply(int num, String value, String msg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTopic(String chan, IRCUser user, String topic) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unknown(String prefix, String command, String middle,
			String trailing) {
		// TODO Auto-generated method stub
		
	}

}
