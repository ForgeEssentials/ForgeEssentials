package com.ForgeEssentials.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class GuiEconomy extends GuiScreen{
	 /** Also counts the number of updates, not certain as to why yet. */
    private int updateCounter2 = 0;

    /** Counts the number of screen updates. */
    private int updateCounter = 0;
    
    // Get the amount of money from the player tracker/whatever - temp hardcoded until network functionality is in.
    public int amount = 0;
    
	public void initGui(){
		byte b0 = -16;
		this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 24 + b0, "Return to main FE menu"));
	}
	protected void actionPerformed(GuiButton par1GuiButton)
    {
        switch (par1GuiButton.id)
        {
            case 0:
                this.mc.displayGuiScreen(new GuiFEMain());
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
        this.drawCenteredString(this.fontRenderer, "ForgeEssentials Economy Menu", this.width / 2, 40, 16777215);
        this.drawCenteredString(this.fontRenderer, "You currently have " + amount + " money in your wallet.", this.width / 2, this.height / 4 + 30, 16777215);
        super.drawScreen(par1, par2, par3);
    }

}
