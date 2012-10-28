package com.ForgeEssentials.WorldControl;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.ForgeEssentials.commands.CommandButcher;
import com.ForgeEssentials.commands.CommandChunk;
import com.ForgeEssentials.commands.CommandCopy;
import com.ForgeEssentials.commands.CommandCount;
import com.ForgeEssentials.commands.CommandCut;
import com.ForgeEssentials.commands.CommandDimension;
import com.ForgeEssentials.commands.CommandDistr;
import com.ForgeEssentials.commands.CommandDrain;
import com.ForgeEssentials.commands.CommandExtend1;
import com.ForgeEssentials.commands.CommandExtend2;
import com.ForgeEssentials.commands.CommandExtinguish;
import com.ForgeEssentials.commands.CommandFlip;
import com.ForgeEssentials.commands.CommandGreen;
import com.ForgeEssentials.commands.CommandHPos1;
import com.ForgeEssentials.commands.CommandHPos2;
import com.ForgeEssentials.commands.CommandIce;
import com.ForgeEssentials.commands.CommandLoad;
import com.ForgeEssentials.commands.CommandLoadRelative;
import com.ForgeEssentials.commands.CommandMiniChunk;
import com.ForgeEssentials.commands.CommandMove;
import com.ForgeEssentials.commands.CommandOverlay;
import com.ForgeEssentials.commands.CommandPaste;
import com.ForgeEssentials.commands.CommandPos1;
import com.ForgeEssentials.commands.CommandPos2;
import com.ForgeEssentials.commands.CommandRedo;
import com.ForgeEssentials.commands.CommandReplace;
import com.ForgeEssentials.commands.CommandReplaceAbove;
import com.ForgeEssentials.commands.CommandReplaceBelow;
import com.ForgeEssentials.commands.CommandReplaceNear;
import com.ForgeEssentials.commands.CommandRotate;
import com.ForgeEssentials.commands.CommandSave;
import com.ForgeEssentials.commands.CommandSet;
import com.ForgeEssentials.commands.CommandSetAbove;
import com.ForgeEssentials.commands.CommandSetBelow;
import com.ForgeEssentials.commands.CommandSetHollow;
import com.ForgeEssentials.commands.CommandSetNear;
import com.ForgeEssentials.commands.CommandSetReach;
import com.ForgeEssentials.commands.CommandSetTop;
import com.ForgeEssentials.commands.CommandShift;
import com.ForgeEssentials.commands.CommandSnow;
import com.ForgeEssentials.commands.CommandStack;
import com.ForgeEssentials.commands.CommandTpSel1;
import com.ForgeEssentials.commands.CommandTpSel2;
import com.ForgeEssentials.commands.CommandTree;
import com.ForgeEssentials.commands.CommandUndo;
import com.ForgeEssentials.commands.CommandUngreen;
import com.ForgeEssentials.commands.CommandUnice;
import com.ForgeEssentials.commands.CommandUnsnow;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.MathHelper;
import net.minecraft.src.MovingObjectPosition;
import net.minecraft.src.Vec3;
import net.minecraft.src.World;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.registry.TickRegistry;

/**
 * 
 * @author UnknownCoder : Max Bruce
 * @author AbrarSyed  adapted
 * the main WorldControl class 
 */
public class WorldControlMain
{
	public static int wandID;
	public static final String CHANNEL = "WorldControl";
	
	public void load(cpw.mods.fml.common.event.FMLInitializationEvent nothingHere)
	{
		new FunctionHandler();
		
		MinecraftForge.EVENT_BUS.register(new WandController());
		
		TickRegistry.registerTickHandler(new TickHandler(), Side.CLIENT);
		
		loadPlugins();
	}
	
	public void serverLoad(FMLServerStartingEvent event)
	{
		System.out.println("Commands loading");
		event.registerServerCommand(new CommandSet());
        event.registerServerCommand(new CommandReplace());
        event.registerServerCommand(new CommandCount());
        event.registerServerCommand(new CommandUndo());
        event.registerServerCommand(new CommandRedo());
        event.registerServerCommand(new CommandPos1());
        event.registerServerCommand(new CommandPos2());
        event.registerServerCommand(new CommandHPos1());
        event.registerServerCommand(new CommandHPos2());
        event.registerServerCommand(new CommandDimension());
        event.registerServerCommand(new CommandReplaceNear());
        event.registerServerCommand(new CommandSetNear());
        event.registerServerCommand(new CommandGreen());
        event.registerServerCommand(new CommandUngreen());
        event.registerServerCommand(new CommandDrain());
        event.registerServerCommand(new CommandSetHollow());
        event.registerServerCommand(new CommandExtend1());
        event.registerServerCommand(new CommandExtend2());
        event.registerServerCommand(new CommandSave());
        event.registerServerCommand(new CommandLoad());
        event.registerServerCommand(new CommandLoadRelative());
        event.registerServerCommand(new CommandCopy());
        event.registerServerCommand(new CommandPaste());
        event.registerServerCommand(new CommandTree());
        event.registerServerCommand(new CommandStack());
        event.registerServerCommand(new CommandCut());
        event.registerServerCommand(new CommandMove());
        event.registerServerCommand(new CommandOverlay());
        event.registerServerCommand(new CommandSetTop());
        event.registerServerCommand(new CommandTpSel1());
        event.registerServerCommand(new CommandTpSel2());
        event.registerServerCommand(new CommandUnsnow());
        event.registerServerCommand(new CommandSnow());
        event.registerServerCommand(new CommandUnice());
        event.registerServerCommand(new CommandIce());
        event.registerServerCommand(new CommandDistr());
        event.registerServerCommand(new CommandSetBelow());
        event.registerServerCommand(new CommandSetAbove());
        event.registerServerCommand(new CommandReplaceBelow());
        event.registerServerCommand(new CommandReplaceAbove());
        event.registerServerCommand(new CommandButcher());
        event.registerServerCommand(new CommandExtinguish());
        event.registerServerCommand(new CommandChunk());
        event.registerServerCommand(new CommandMiniChunk());
        event.registerServerCommand(new CommandSetReach());
        event.registerServerCommand(new CommandShift());
        event.registerServerCommand(new CommandRotate());
        event.registerServerCommand(new CommandFlip());
        for(int i = 0;i<plugins.size();i++) {
        	plugins.get(i).loadCommands(event);
        }
	}
	
	// have to figure out what this does.
	
	public static WCClassLoader CL = new WCClassLoader();
	
	public List<WCPlugin> plugins = new ArrayList<WCPlugin>();
	public void loadPlugins()
	{
		try{
			File dir = new File(System.getProperty("user.dir")+"\\WCplugins\\");
			if(!dir.exists())
				dir.mkdir();
			
			if(dir.isDirectory()) {
				for (File child : dir.listFiles()) {
					if (".".equals(child.getName()) || "..".equals(child.getName())) {
						continue;
					}
					if(child.toString().endsWith(".jar")) {
						JarFile jar = new JarFile(child);
						for(Enumeration entry=jar.entries();entry.hasMoreElements();)
						{
							WCPlugin plugin = (WCPlugin)CL.loadClass("com.ForgeEssentials.WorldControl.plugin_"+child.getName().substring(0, child.getName().length()-4), jar.getInputStream((JarEntry)entry.nextElement())).newInstance();
							plugins.add(plugin);
							plugin.helper = APIHelper.instance;
							plugin.load();
						}
						jar.close();
					}
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
	// used for tree spawning and any other commands that target what the player is looking at
	// copied from buckets
	public static MovingObjectPosition getMovingObjectPositionFromPlayer(World par1World, EntityPlayer par2EntityPlayer)
    {
        float var4 = 1.0F;
        float var5 = par2EntityPlayer.prevRotationPitch + (par2EntityPlayer.rotationPitch - par2EntityPlayer.prevRotationPitch) * var4;
        float var6 = par2EntityPlayer.prevRotationYaw + (par2EntityPlayer.rotationYaw - par2EntityPlayer.prevRotationYaw) * var4;
        double var7 = par2EntityPlayer.prevPosX + (par2EntityPlayer.posX - par2EntityPlayer.prevPosX) * (double)var4;
        double var9 = par2EntityPlayer.prevPosY + (par2EntityPlayer.posY - par2EntityPlayer.prevPosY) * (double)var4 + 1.62D - (double)par2EntityPlayer.yOffset;
        double var11 = par2EntityPlayer.prevPosZ + (par2EntityPlayer.posZ - par2EntityPlayer.prevPosZ) * (double)var4;
        Vec3 var13 = par1World.func_82732_R().getVecFromPool(var7, var9, var11);
        float var14 = MathHelper.cos(-var6 * 0.017453292F - (float)Math.PI);
        float var15 = MathHelper.sin(-var6 * 0.017453292F - (float)Math.PI);
        float var16 = -MathHelper.cos(-var5 * 0.017453292F);
        float var17 = MathHelper.sin(-var5 * 0.017453292F);
        float var18 = var15 * var16;
        float var20 = var14 * var16;
        double var21 = 5.0D;
        if (par2EntityPlayer instanceof EntityPlayerMP)
        {
            var21 = ((EntityPlayerMP)par2EntityPlayer).theItemInWorldManager.getBlockReachDistance();
        }
        Vec3 var23 = var13.addVector((double)var18 * var21, (double)var17 * var21, (double)var20 * var21);
        return par1World.rayTraceBlocks_do_do(var13, var23, true, false);
    }
}
