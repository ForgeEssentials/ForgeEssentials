package com.ForgeEssentials.WorldControl.commands;

//Depreciated
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;

import com.ForgeEssentials.WorldControl.BlockInfo;
import com.ForgeEssentials.api.permissions.PermissionsAPI;
import com.ForgeEssentials.api.permissions.query.PermQueryPlayerArea;
import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.AreaSelector.AreaBase;
import com.ForgeEssentials.util.AreaSelector.Point;

public class CommandContract extends WorldControlCommandBase
{

	public CommandContract()
	{
		super(true);
	}

	@Override
	public String getName()
	{
		return "contract";
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
		if(args.length>0 && args.length<4) {
			int amount = 0;
			int reverse = 0;
			FunctionHelper.Direction dir = FunctionHelper.getFacingDirection(player);
			if(args.length==1) {
				if(BlockInfo.isInt(args[0])) {
					amount = Integer.parseInt(args[0]);
				}else{
					OutputHandler.chatError(player, "Amount must be a number!");
					return;
				}
			}
			if(args.length>=2) {
				if(BlockInfo.isInt(args[0])) {
					amount = Integer.parseInt(args[0]);
				}else{
					OutputHandler.chatError(player, "Amount must be a number!");
					return;
				}
				if(BlockInfo.isInt(args[1])) {
					reverse = Integer.parseInt(args[1]);
				}else{
					FunctionHelper.Direction dirt = FunctionHelper.getDirectionFromString(args[1]);
					if(dirt==null) {
						OutputHandler.chatError(player, "Invalid Direction! (NSWEUD)");
						return;
					}
					dir = dirt;
				}
			}
			if(args.length==3) {
				FunctionHelper.Direction dirt = FunctionHelper.getDirectionFromString(args[2]);
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
				boolean isGood = info.getPoint1().z>=info.getPoint2().z;
				if(isGood) {
					if(dir==FunctionHelper.Direction.SOUTH) {
						Point pt = info.getPoint1();
						pt.z-=amount;
						info.setPoint1(pt);
					}else{
						Point pt = info.getPoint2();
						pt.z+=amount;
						info.setPoint2(pt);
					}
				}else{
					if(dir==FunctionHelper.Direction.SOUTH) {
						Point pt = info.getPoint2();
						pt.z-=amount;
						info.setPoint2(pt);
					}else{
						Point pt = info.getPoint1();
						pt.z+=amount;
						info.setPoint1(pt);
					}
				}
			}else if(dir==FunctionHelper.Direction.EAST||dir==FunctionHelper.Direction.WEST) {
				boolean isGood = info.getPoint1().x>=info.getPoint2().x;
				if(isGood) {
					if(dir==FunctionHelper.Direction.EAST) {
						Point pt = info.getPoint1();
						pt.x-=amount;
						info.setPoint1(pt);
					}else{
						Point pt = info.getPoint2();
						pt.x+=amount;
						info.setPoint2(pt);
					}
				}else{
					if(dir==FunctionHelper.Direction.WEST) {
						Point pt = info.getPoint2();
						pt.x-=amount;
						info.setPoint2(pt);
					}else{
						Point pt = info.getPoint1();
						pt.x+=amount;
						info.setPoint1(pt);
					}
				}
			}else if(dir==FunctionHelper.Direction.UP||dir==FunctionHelper.Direction.DOWN) {
				boolean isGood = info.getPoint1().y>=info.getPoint2().y;
				if(isGood) {
					if(dir==FunctionHelper.Direction.UP) {
						Point pt = info.getPoint1();
						pt.y-=amount;
						info.setPoint1(pt);
					}else{
						Point pt = info.getPoint2();
						pt.y+=amount;
						info.setPoint2(pt);
					}
				}else{
					if(dir==FunctionHelper.Direction.DOWN) {
						Point pt = info.getPoint2();
						pt.y-=amount;
						info.setPoint2(pt);
					}else{
						Point pt = info.getPoint1();
						pt.y+=amount;
						info.setPoint1(pt);
					}
				}
			}
			if(reverse>0) {
				if(dir==FunctionHelper.Direction.SOUTH||dir==FunctionHelper.Direction.NORTH) {
					boolean isGood = info.getPoint1().z>=info.getPoint2().z;
					if(isGood) {
						if(dir==FunctionHelper.Direction.SOUTH) {
							Point pt = info.getPoint2();
							pt.z-=reverse;
							info.setPoint2(pt);
						}else{
							Point pt = info.getPoint1();
							pt.z+=reverse;
							info.setPoint2(pt);
						}
					}else{
						if(dir==FunctionHelper.Direction.SOUTH) {
							Point pt = info.getPoint1();
							pt.z-=reverse;
							info.setPoint1(pt);
						}else{
							Point pt = info.getPoint2();
							pt.z+=reverse;
							info.setPoint2(pt);
						}
					}
				}else if(dir==FunctionHelper.Direction.EAST||dir==FunctionHelper.Direction.WEST) {
					boolean isGood = info.getPoint1().x>=info.getPoint2().x;
					if(isGood) {
						if(dir==FunctionHelper.Direction.EAST) {
							Point pt = info.getPoint2();
							pt.x-=reverse;
							info.setPoint2(pt);
						}else{
							Point pt = info.getPoint1();
							pt.x+=reverse;
							info.setPoint1(pt);
						}
					}else{
						if(dir==FunctionHelper.Direction.WEST) {
							Point pt = info.getPoint1();
							pt.x-=reverse;
							info.setPoint1(pt);
						}else{
							Point pt = info.getPoint2();
							pt.x+=reverse;
							info.setPoint2(pt);
						}
					}
				}else if(dir==FunctionHelper.Direction.UP||dir==FunctionHelper.Direction.DOWN) {
					boolean isGood = info.getPoint1().y>=info.getPoint2().y;
					if(isGood) {
						if(dir==FunctionHelper.Direction.UP) {
							Point pt = info.getPoint2();
							pt.y-=reverse;
							info.setPoint2(pt);
						}else{
							Point pt = info.getPoint1();
							pt.y+=reverse;
							info.setPoint1(pt);
						}
					}else{
						if(dir==FunctionHelper.Direction.DOWN) {
							Point pt = info.getPoint1();
							pt.y-=reverse;
							info.setPoint1(pt);
						}else{
							Point pt = info.getPoint2();
							pt.y+=reverse;
							info.setPoint2(pt);
						}
					}
				}
			}
			OutputHandler.chatConfirmation(player, "Contracted by "+amount+"!");
		}
	}

	@Override
	public String getSyntaxPlayer(EntityPlayer player)
	{
		return "/" + getCommandName() + " <amount> [reverse] [direction(NSEWUD)]";
	}

	@Override
	public String getInfoPlayer(EntityPlayer player)
	{
		return "Contract Selection Positions";
	}

	@Override
	public String getCommandPerm()
	{
		return "ForgeEssentials.WorldControl.commands.selectionmanipulate";
	}

}
