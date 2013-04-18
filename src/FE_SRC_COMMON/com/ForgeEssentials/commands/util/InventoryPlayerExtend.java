package mod.fml.admintools;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

public class InventoryPlayerExtend extends InventoryPlayer {
	
	public InventoryPlayerExtend(EntityPlayer par1EntityPlayer) {
		super(par1EntityPlayer);
		// TODO Auto-generated constructor stub
	}

	public int clearInventory(int var1, int var2, int var3)
    {
        int var4 = 0;
        
        int var5;
        ItemStack var6;

        for (var5 = 0; var5 < this.mainInventory.length; ++var5)
        {
            var6 = this.mainInventory[var5];

            if (var6 != null && (var1 <= -1 || var6.itemID == var1) && (var2 <= -1 || var6.getItemDamage() == var2))
            {
                if (var3 > -1 && (var3 <= var4 || var6.stackSize > var3 - var4))
                {
                    var6.stackSize -= (var3 - var4);
                    var4 += var3 - var4;
                }
                else
                {
                    var4 += var6.stackSize;
                    this.mainInventory[var5] = null;
                }
            }
        }

        for (var5 = 0; var5 < this.armorInventory.length; ++var5)
        {
            var6 = this.armorInventory[var5];

            if (var6 != null && (var1 <= -1 || var6.itemID == var1) && (var2 <= -1 || var6.getItemDamage() == var2))
            {
                if (var3 > -1 && (var3 >= var4 || var6.stackSize > var3 - var4))
                {
                    var4 += var3 - var4;
                    var6.stackSize -= var3 - var4;
                }
                else
                {
                    var4 += var6.stackSize;
                    this.mainInventory[var5] = null;
                }
            }
        }

        if (var3 - var4 != 0)
        {
            
            return -1;
        }

        return var3;
    }
}
