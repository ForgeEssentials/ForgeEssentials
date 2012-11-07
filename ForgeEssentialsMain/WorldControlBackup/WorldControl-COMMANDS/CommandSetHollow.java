package com.ForgeEssentials.commands;

import com.ForgeEssentials.WorldControl.FunctionHandler;

import net.minecraft.src.CommandBase;
import net.minecraft.src.ICommandSender;

public class CommandSetHollow extends CommandBase {

	@Override
	public String getCommandName() {
		return "walls";
	}

	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		try{
			CommandInfo inf = null;
			boolean clear=false;
			boolean floor = false;
			boolean roof = false;
			if(var2.length==1) {
				inf = CommandProcesser.processIDMetaCombo(var2[0]);
			}else if(var2.length==2){
				inf = CommandProcesser.processIDMetaCombo(var2[0]);
				if(var2[1].equals("floor")) {
					floor=true;
				}else if(var2[1].equals("roof")) {
					roof=true;
				}else if(var2[1].equals("rooffloor")) {
					roof=true;
					floor=true;
				}else if(var2[1].equals("floorroof")) {
					roof=true;
					floor=true;
				}else if(var2[1].equals("clear")) {
					clear=true;
				}
			}else if(var2.length==3){
				inf = CommandProcesser.processIDMetaCombo(var2[0]);
				if(var2[1].equals("floor")) {
					floor=true;
				}else if(var2[1].equals("roof")) {
					roof=true;
				}else if(var2[1].equals("rooffloor")) {
					roof=true;
					floor=true;
				}else if(var2[1].equals("floorroof")) {
					roof=true;
					floor=true;
				}else if(var2[1].equals("clear")) {
					clear=true;
					if(var2[2].equals("clear")) {
						clear=true;
					}
				}else{
					this.getCommandSenderAsPlayer(var1).addChatMessage("Walls Command Failed(Try /walls <id(:meta)> (<roof/floor/rooffloor/floorroof/none/clear>) (<clear>))");
					return;
				}
			}

			FunctionHandler.instance.setHollowCommand(inf, this.getCommandSenderAsPlayer(var1), clear);
		}catch(Exception e) {
			this.getCommandSenderAsPlayer(var1).addChatMessage("Walls Command Failed!(Unknown Reason)");
		}
	}

}
