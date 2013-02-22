package com.ForgeEssentials.WorldControl.TickTasks;


import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import com.ForgeEssentials.WorldControl.BlockArray;
import com.ForgeEssentials.WorldControl.BlockArrayBackup;
import com.ForgeEssentials.WorldControl.BlockInfo;
import com.ForgeEssentials.WorldControl.ConfigWorldControl;
import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.util.ITickTask;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.AreaSelector.AreaBase;
import com.ForgeEssentials.util.AreaSelector.Point;
import com.ForgeEssentials.util.AreaSelector.Selection;

public class TickTaskLoadBlocks implements ITickTask {
	
	EntityPlayer player;
	World world;
	AreaBase sel;
	int last=0;
	int	current=0;
	Point currentBlock = null;
	int	changed=0;
	BlockArrayBackup backup;
	int ticks = 0;
	
	protected boolean place(int x, int y, int z, BlockInfo info) {
		BlockInfo.SingularBlockInfo inf = info.randomBlock();
		if(inf==null)return false;
		if(inf.meta<0)return false;
		if(requiresBackup())backup.before.addBlock(world, x, y, z, true);
		world.setBlock(x, y, z, 0);
		boolean canPlace = (inf==null||inf.block==null);
		if(!canPlace)canPlace = inf==null||inf.block.canPlaceBlockAt(world, x, y, z);
		if(!canPlace)return false;
		world.setBlockAndMetadataWithNotify(x, y, z, inf.block==null?0:inf.block.blockID, inf.meta);
		if(inf.nbt!=null && world.getBlockTileEntity(x, y, z)!=null) {
			inf.nbt.setInteger("x", x);
			inf.nbt.setInteger("y", y);
			inf.nbt.setInteger("z", z);
			world.getBlockTileEntity(x, y, z).readFromNBT(inf.nbt);
		}
		if(requiresBackup())backup.after.addBlock(world, x, y, z, true);
		return true;
	}
	
	protected boolean place(int x, int y, int z, BlockInfo.SingularBlockInfo inf) {
		if(inf==null)return false;
		if(inf.meta<0)return false;
		if(requiresBackup())backup.before.addBlock(world, x, y, z, true);
		world.setBlock(x, y, z, 0);
		boolean canPlace = (inf==null||inf.block==null);
		if(!canPlace)canPlace = inf==null||inf.block.canPlaceBlockAt(world, x, y, z);
		if(!canPlace)return false;
		world.setBlockAndMetadataWithNotify(x, y, z, inf.block==null?0:inf.block.blockID, inf.meta);
		if(inf.nbt!=null && world.getBlockTileEntity(x, y, z)!=null) {
			inf.nbt.setInteger("x", x);
			inf.nbt.setInteger("y", y);
			inf.nbt.setInteger("z", z);
			world.getBlockTileEntity(x, y, z).readFromNBT(inf.nbt);
		}
		if(requiresBackup())backup.after.addBlock(world, x, y, z, true);
		return true;
	}
	
	protected boolean place(int x, int y, int z, BlockArray.LoadingBlock inf) {
		if(inf==null)return false;
		if(inf.meta<0)return false;
		if(requiresBackup())backup.before.addBlock(world, x, y, z, true);
		world.setBlock(x, y, z, 0);
		boolean canPlace = (inf==null||Block.blocksList[inf.id]==null);
		if(!canPlace)canPlace = inf==null||Block.blocksList[inf.id].canPlaceBlockAt(world, x, y, z);
		if(!canPlace)return false;
		world.setBlockAndMetadataWithNotify(x, y, z, Block.blocksList[inf.id]==null?0:inf.id, inf.meta);
		if(inf.TEData!=null && world.getBlockTileEntity(x, y, z)!=null) {
			inf.TEData.setInteger("x", x);
			inf.TEData.setInteger("y", y);
			inf.TEData.setInteger("z", z);
			world.getBlockTileEntity(x, y, z).readFromNBT(inf.TEData);
		}
		if(requiresBackup())backup.after.addBlock(world, x, y, z, true);
		return true;
	}
	
	public TickTaskLoadBlocks(EntityPlayer player, AreaBase sel) {
		this.player = player;
		this.world = player.worldObj;
		this.sel = sel;
		currentBlock = sel.getLowPoint();
		this.backup = new BlockArrayBackup(new BlockArray(sel.getLowPoint().x, sel.getLowPoint().y, sel.getLowPoint().z, false, sel.getXLength()-1, sel.getYLength()-1, sel.getZLength()-1), new BlockArray(sel.getLowPoint().x, sel.getLowPoint().y, sel.getLowPoint().z, false, sel.getXLength()-1, sel.getYLength()-1, sel.getZLength()-1));
		last = sel.getXLength() * sel.getYLength() * sel.getZLength();
	}
	
	protected boolean placeBlock() {
		return false;
	}
	
	protected void runTick() {
		
	}

	protected void endTick() {
		
	}

	protected void onCompleted() {
		
	}
	
	protected int x;
	protected int y;
	protected int z;
	
	protected boolean usesCoordsSystem() {
		return true;
	}

	@Override
	public void tick()
	{
		ticks++;
		int changed = 0;
		runTick();
		if(editsBlocks()&&currentBlock!=null&&usesCoordsSystem()) {
			x = currentBlock.x;
			y = currentBlock.y;
			z = currentBlock.z;
		}
		for(int c = current;c<last;c++) {
			if(changed>=ConfigWorldControl.blocksPerTick) {
				changed = 0;
				endTick();
				if(current==last)onCompleted();
				return;
			}else{
				if(placeBlock()) {
					this.changed++;
					changed++;
				}
				updatePosition();
				current++;
			}
		}
		endTick();
		onCompleted();
	}
	
	private void updatePosition() {
		if(editsBlocks()&&usesCoordsSystem()) {
			x++;
			if (x > sel.getHighPoint().x)
			{
				// Reset y, increment z.
				x = sel.getLowPoint().x;
				z++;
				if (z > sel.getHighPoint().z)
				{
					// Reset z, increment x.
					z = sel.getLowPoint().z;
					y++;
					// Check stop condition
					if (y > sel.getHighPoint().y)
					{
						setComplete();
					}
				}
			}
			currentBlock = new Point(x, y, z);
		}
	}
	
	protected boolean requiresBackup() {
		return true;
	}

	@Override
	public void onComplete()
	{
		if(requiresBackup())PlayerInfo.getPlayerInfo(player.username).addUndoAction(backup);
		OutputHandler.chatConfirmation(player, "" + changed + " blocks changed in "+(double)ticks/20D+" seconds");
	}
	
	private boolean forceComplete = false;
	
	protected void setComplete() {
		forceComplete = true;
	}

	@Override
	public boolean isComplete()
	{
		return current>=last||forceComplete;
	}

	@Override
	public boolean editsBlocks()
	{
		return true;
	}
	
}
