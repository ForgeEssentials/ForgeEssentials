package com.ForgeEssentials.WorldControl.weintegration;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import com.ForgeEssentials.core.ForgeEssentials;
import com.sk89q.worldedit.LocalConfiguration;
import com.sk89q.worldedit.WorldEdit;

public class WEIntegration {

	public WorldEdit controller;
	public Constructor<?> worldvector;
	public Method handleCommand;
	public Method handleBlockLeftClick;
	public Method handleBlockRightClick;
	public Method handleArmSwing;
	public Method handleRightClick;
	public ConsoleLocalPlayer localplayer;
	private ConsoleServerInterface serverinterface;
	private LocalConfiguration localconfiguration = new LocalConfig(ForgeEssentials.FEDIR);

	public WEIntegration() throws Throwable {
		this.localconfiguration.load();
		this.serverinterface = new ConsoleServerInterface(Helper.getOwner().getWorld());

		Class<?> e;
		try {
			e = Class.forName("com.sk89q.worldedit.WorldEdit");
			this.controller = (WorldEdit)e.getConstructors()[0].newInstance(new Object[]{this.getServerinterface(), this.getLocalconfiguration()});
			Method[] m = e.getMethods();

			for(int i = 0; i < m.length; ++i) {
				if(m[i].getName().compareTo("handleCommand") == 0) {
					this.handleCommand = m[i];
				} else if(m[i].getName().compareTo("handleBlockLeftClick") == 0) {
					this.handleBlockLeftClick = m[i];
				} else if(m[i].getName().compareTo("handleBlockRightClick") == 0) {
					this.handleBlockRightClick = m[i];
				} else if(m[i].getName().compareTo("handleRightClick") == 0) {
					this.handleRightClick = m[i];
				} else if(m[i].getName().compareTo("handleArmSwing") == 0) {
					this.handleArmSwing = m[i];
				}
			}
		} catch (Exception var7) {
			var7.printStackTrace();
			throw new Exception("Couldn't find the WorldEdit method.");
		}

		try {
			e = Class.forName("com.sk89q.worldedit.WorldVector");
			Class<?> localworld = Class.forName("com.sk89q.worldedit.LocalWorld");
			this.worldvector = e.getConstructor(new Class<?>[]{localworld, Integer.TYPE, Integer.TYPE, Integer.TYPE});
		} catch (Exception var6) {
			var6.printStackTrace();
			throw new Exception("Couldn't find the WorldVector method.");
		}
	}

	// This goes here for the moment..
	public static void handleMouseButtonDown(Player spcplayer, int blockx, int blocky, int blockz, int side, boolean isLeft) {

		if(Console.PLUGIN_MANAGER == null || !Helper.WORLDEDITLOADED || spc_WorldEdit.WEP == null) {
			System.out.println("Can't handle click.");
			return;
		}

		if(blocky >= 0) {
			try {
				ConsoleLocalPlayer player = new ConsoleLocalPlayer(spc_WorldEdit.WEP.getServerinterface(), spcplayer);
				player.hide = true;
				if(!isLeft) {
					spc_WorldEdit.WEP.getHandleRightClick().invoke(spc_WorldEdit.WEP.getController(), new Object[]{player});
					if((blockx != player.blockrightx || blocky != player.blockrighty || blockz != player.blockrightz) && blocky > -1) {
						Object e = spc_WorldEdit.WEP.getWorldvector().newInstance(new Object[]{player.getWorld(), Integer.valueOf(blockx), Integer.valueOf(blocky), Integer.valueOf(blockz)});
						spc_WorldEdit.WEP.getHandleBlockRightClick().invoke(spc_WorldEdit.WEP.getController(), new Object[]{player, e});
						player.blockrightx = blockx;
						player.blockrighty = blocky;
						player.blockrightz = blockz;
					}
				} else {
					spc_WorldEdit.WEP.getHandleArmSwing().invoke(spc_WorldEdit.WEP.getController(), new Object[]{player});
					if((blockx != player.blockleftx || blocky != player.blocklefty || blockz != player.blockleftz) && blocky > -1) {
						Object e = spc_WorldEdit.WEP.getWorldvector().newInstance(new Object[]{player.getWorld(), Integer.valueOf(blockx), Integer.valueOf(blocky), Integer.valueOf(blockz)});
						spc_WorldEdit.WEP.getHandleBlockLeftClick().invoke(spc_WorldEdit.WEP.getController(), new Object[]{player, e});
						player.blockleftx = blockx;
						player.blocklefty = blocky;
						player.blockleftz = blockz;
					}
				}
			} catch (Throwable var3) {
				var3.printStackTrace();
			}

		}
	}

	public Object getController() {
		return this.controller;
	}

	public Constructor<?> getWorldvector() {
		return this.worldvector;
	}

	public Method getHandleCommand() {
		return this.handleCommand;
	}

	public Method getHandleBlockLeftClick() {
		return this.handleBlockLeftClick;
	}

	public Method getHandleBlockRightClick() {
		return this.handleBlockRightClick;
	}

	public Method getHandleArmSwing() {
		return this.handleArmSwing;
	}

	public Method getHandleRightClick() {
		return this.handleRightClick;
	}

	public void setPlayer(Player player) {
		this.localplayer = new ConsoleLocalPlayer(this.getServerinterface(), player);
		this.controller.getSession(this.localplayer).setCUISupport(true);
		this.controller.getSession(this.localplayer).dispatchCUISetup(this.localplayer);
	}

	public ConsoleLocalPlayer getPlayer() {
		return this.localplayer;
	}

	public ConsoleServerInterface getServerinterface() {
		return this.serverinterface;
	}

	public LocalConfiguration getLocalconfiguration() {
		return this.localconfiguration;
	}

}
