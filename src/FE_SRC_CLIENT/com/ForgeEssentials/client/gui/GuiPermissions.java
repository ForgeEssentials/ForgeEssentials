package com.ForgeEssentials.client.gui;

import com.ForgeEssentials.client.network.PacketPermNodeList;

import cpw.mods.fml.common.network.PacketDispatcher;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class GuiPermissions extends GuiScreen {
	 /** Also counts the number of updates, not certain as to why yet. */
    private int updateCounter2 = 0;

    /** Counts the number of screen updates. */
    private int updateCounter = 0;
    
	public void initGui(){
		byte b0 = -16;
		GuiButton nodeList;
		this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 24 + b0, "Return to main menu"));
		this.buttonList.add(nodeList = new GuiButton(1, this.width / 2 - 100, this.height / 4 + 48 + b0, "Permission node list - NYI"));
		nodeList.enabled = false;
	}
	protected void actionPerformed(GuiButton par1GuiButton)
    {
        switch (par1GuiButton.id)
        {
            case 0:
                this.mc.displayGuiScreen(new GuiFEMain());
                break;
            case 1:
            	this.mc.displayGuiScreen(new GuiPermNodeList());
            	PacketDispatcher.sendPacketToServer(new PacketPermNodeList().getPayload());
            	break;
            
        }
    }
	public void updateScreen()
    {
        super.updateScreen();
        ++this.updateCounter;
    }
	public void drawScreen(int par1, int par2, float par3)
    {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRenderer, "ForgeEssentials Permissions Menu", this.width / 2, 40, 16777215);
        super.drawScreen(par1, par2, par3);
    }

}
