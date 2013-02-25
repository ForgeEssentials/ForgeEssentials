package com.ForgeEssentials.WorldControl.commands;

//Depreciated
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;

import com.ForgeEssentials.api.permissions.PermissionsAPI;
import com.ForgeEssentials.api.permissions.query.PermQueryPlayerArea;
import com.ForgeEssentials.api.permissions.query.PermQuery.PermResult;
import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.util.BlockInfo;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.AreaSelector.AreaBase;
import com.ForgeEssentials.util.AreaSelector.Point;

public class CommandShift extends WorldControlCommandBase
{

	public CommandShift()
	{
		super(true);
	}

	@Override
	public String getName()
	{
		return "shift";
	}

	@Override
	public void processCommandPlayer(EntityPlayer player, String[] args)
	{
		PlayerInfo info = PlayerInfo.getPlayerInfo(player);
		AreaBase sel = info.getSelection();
		if(sel == null) {
			OutputHandler.chatError(player, "You must already have a selection!");
			return;
		}
		PermQueryPlayerArea query = new PermQueryPlayerArea(player, getCommandPerm(), sel, false);
		PermResult result = PermissionsAPI.checkPermResult(query);

		if(result==PermResult.ALLOW||result==PermResult.PARTIAL) {
			if(args.length>0 && args.length<3) {
				int amount = 0;
				int reverse = 0;
				FunctionHelper.Direction dir = FunctionHelper.getFacingDirection(player);
				if(args.length==1) {
					if(FunctionHelper.isInt(args[0])) {
						amount = Integer.parseInt(args[0]);
					}else{
						OutputHandler.chatError(player, "Invalid Argument: "+args[0]);
						return;
					}
				}
				if(args.length==2) {
					if(FunctionHelper.isInt(args[0])) {
						amount = Integer.parseInt(args[0]);
					}else{
						OutputHandler.chatError(player, "Amount must be a number!");
						return;
					}
					FunctionHelper.Direction dirt = FunctionHelper.getDirectionFromString(args[1]);
					if(dirt==null) {
						OutputHandler.chatError(player, "Invalid Direction! (NSWEUD)");
						return;
					}
					dir = dirt;
				}
				//North=-Z South=+Z East=+X West=-X Up=+Y Down=-Y
				/*int lowX1 = (sel.getLowPoint().x+(dir==FunctionHelper.Direction.EAST?amount:dir==FunctionHelper.Direction.WEST?-amount:0));
			int lowY1 = (sel.getLowPoint().y+(dir==FunctionHelper.Direction.UP?amount:dir==FunctionHelper.Direction.DOWN?-amount:0));
			int lowZ1 = (sel.getLowPoint().z+(dir==FunctionHelper.Direction.SOUTH?amount:dir==FunctionHelper.Direction.NORTH?-amount:0));
			int highX1 = (sel.getHighPoint().x+(dir==FunctionHelper.Direction.EAST?amount:dir==FunctionHelper.Direction.WEST?-amount:0));
			int highY1 = (sel.getHighPoint().y+(dir==FunctionHelper.Direction.UP?amount:dir==FunctionHelper.Direction.DOWN?-amount:0));
			int highZ1 = (sel.getHighPoint().z+(dir==FunctionHelper.Direction.SOUTH?amount:dir==FunctionHelper.Direction.NORTH?-amount:0));
			int lowX2 = (sel.getLowPoint().x+(dir==FunctionHelper.Direction.EAST?reverse:dir==FunctionHelper.Direction.WEST?-reverse:0));
			int lowY2 = (sel.getLowPoint().y+(dir==FunctionHelper.Direction.UP?reverse:dir==FunctionHelper.Direction.DOWN?-reverse:0));
			int lowZ2 = (sel.getLowPoint().z+(dir==FunctionHelper.Direction.SOUTH?reverse:dir==FunctionHelper.Direction.NORTH?-reverse:0));
			int highX2 = (sel.getHighPoint().x+(dir==FunctionHelper.Direction.EAST?reverse:dir==FunctionHelper.Direction.WEST?-reverse:0));
			int highY2 = (sel.getHighPoint().y+(dir==FunctionHelper.Direction.UP?reverse:dir==FunctionHelper.Direction.DOWN?-reverse:0));
			int highZ2 = (sel.getHighPoint().z+(dir==FunctionHelper.Direction.SOUTH?reverse:dir==FunctionHelper.Direction.NORTH?-reverse:0));
				 */
				if(dir==FunctionHelper.Direction.SOUTH||dir==FunctionHelper.Direction.NORTH) {
					if(dir==FunctionHelper.Direction.SOUTH) {
						Point pt1 = info.getPoint1();
						Point pt2 = info.getPoint2();
						pt1.z+=amount;
						pt2.z+=amount;
						info.setPoint1(pt1);
						info.setPoint2(pt2);
					}else{
						Point pt1 = info.getPoint1();
						Point pt2 = info.getPoint2();
						pt1.z-=amount;
						pt2.z-=amount;
						info.setPoint1(pt1);
						info.setPoint2(pt2);
					}
				}else if(dir==FunctionHelper.Direction.EAST||dir==FunctionHelper.Direction.WEST) {
					if(dir==FunctionHelper.Direction.EAST) {
						Point pt1 = info.getPoint1();
						Point pt2 = info.getPoint2();
						pt1.x+=amount;
						pt2.x+=amount;
						info.setPoint1(pt1);
						info.setPoint2(pt2);
					}else{
						Point pt1 = info.getPoint1();
						Point pt2 = info.getPoint2();
						pt1.x-=amount;
						pt2.x-=amount;
						info.setPoint1(pt1);
						info.setPoint2(pt2);
					}
				}else if(dir==FunctionHelper.Direction.UP||dir==FunctionHelper.Direction.DOWN) {
					if(dir==FunctionHelper.Direction.UP) {
						Point pt1 = info.getPoint1();
						Point pt2 = info.getPoint2();
						pt1.y+=amount;
						pt2.y+=amount;
						info.setPoint1(pt1);
						info.setPoint2(pt2);
					}else{
						Point pt1 = info.getPoint1();
						Point pt2 = info.getPoint2();
						pt1.y-=amount;
						pt2.y-=amount;
						info.setPoint1(pt1);
						info.setPoint2(pt2);
					}
				}
				OutputHandler.chatConfirmation(player, "Shifted by "+amount+"!");
			}
		}else{
			OutputHandler.chatError(player, "You do not have permission!");
		}
	}

	@Override
	public String getSyntaxPlayer(EntityPlayer player)
	{
		return "/" + getCommandName() + " vert/<amount> [direction(NSEWUD)]";
	}

	@Override
	public String getInfoPlayer(EntityPlayer player)
	{
		return "Shift Selection Positions";
	}

	@Override
	public String getCommandPerm()
	{
		return "ForgeEssentials.WorldControl.selection";
	}

}
