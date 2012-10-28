package com.ForgeEssentials.commands;

import com.ForgeEssentials.WorldControl.FunctionHandler;

import net.minecraft.src.CommandBase;
import net.minecraft.src.ICommandSender;

public class CommandFlip extends CommandBase {

	@Override
	public String getCommandName() {
		return "flip";
	}

	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		try{
			int id = 0;
			boolean flipX = false;
			boolean flipY = false;
			boolean flipZ = false;
			if(var2.length==4) {
				id = Integer.parseInt(var2[0]);
				flipX = FunctionHandler.instance.cpy.get(id).flipX;
				flipY = FunctionHandler.instance.cpy.get(id).flipY;
				flipZ = FunctionHandler.instance.cpy.get(id).flipZ;
				if(Integer.parseInt(var2[1])==1) {
					flipX = !flipX;
				}
				if(Integer.parseInt(var2[2])==1) {
					flipY = !flipY;
				}
				if(Integer.parseInt(var2[3])==1) {
					flipZ = !flipZ;
				}
				FunctionHandler.instance.cpy.get(id).flipX=flipX;
				FunctionHandler.instance.cpy.get(id).flipY=flipY;
				FunctionHandler.instance.cpy.get(id).flipZ=flipZ;
			}else if(var2.length==3) {
				flipX = FunctionHandler.instance.cpy.get(id).flipX;
				flipY = FunctionHandler.instance.cpy.get(id).flipY;
				flipZ = FunctionHandler.instance.cpy.get(id).flipZ;
				if(Integer.parseInt(var2[0])==1) {
					flipX = !flipX;
				}
				if(Integer.parseInt(var2[1])==1) {
					flipY = !flipY;
				}
				if(Integer.parseInt(var2[2])==1) {
					flipZ = !flipZ;
				}
				FunctionHandler.instance.cpy.get(id).flipX=flipX;
				FunctionHandler.instance.cpy.get(id).flipY=flipY;
				FunctionHandler.instance.cpy.get(id).flipZ=flipZ;
				this.getCommandSenderAsPlayer(var1).addChatMessage("Fliped to: "+(flipX?1:0)+", "+(flipY?1:0)+", "+(flipZ?1:0));
			}else{
				this.getCommandSenderAsPlayer(var1).addChatMessage("Flip Command Failed(Try /flip (<id>) <isX> <isY> <isZ>)");
				return;
			}
		}catch(Exception e) {
			this.getCommandSenderAsPlayer(var1).addChatMessage("Flip Command Failed!(Unknown Reason)");
		}
	}

}
