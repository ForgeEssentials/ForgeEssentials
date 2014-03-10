package com.forgeessentials.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class GuiPermNodeList extends GuiScreen {
	 /** Also counts the number of updates, not certain as to why yet. */
    private int updateCounter2 = 0;

    /** Counts the number of screen updates. */
    private int updateCounter = 0;
    
    public static String[] nodes;
    
	public void initGui(){
		// draw the gui
		byte b0 = -16;
		this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 32 + b0, "Return to permissions menu"));
		// request for the list of nodes
		
	}
	protected void actionPerformed(GuiButton par1GuiButton)
    {
        switch (par1GuiButton.id)
        {
            case 0:
                this.mc.displayGuiScreen(new GuiPermissions());
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
        this.drawCenteredString(this.fontRenderer, "List of permission nodes - NYI", this.width / 2, this.height / 2, 16777215);
        if ((nodes == null)){
        	this.drawCenteredString(this.fontRenderer, "There was supposed to be a list of perm nodes here, /nbut then RlonRyan had it for dinner.", this.width / 2, 60, 16777215);
        }
        super.drawScreen(par1, par2, par3);
    }


}
