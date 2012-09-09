/**
 * this is some sort of dummy class to be able to
 * save data and work with Guis
 * 
 * @author Kalvin
 */
package xelitez.ironpp;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import net.minecraft.src.Container;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IInventory;
import net.minecraft.src.InventoryPlayer;
import net.minecraft.src.ItemBlock;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Slot;

@SideOnly(Side.CLIENT)
public class ContainerPressurePlate extends Container
{
	public TileEntityPressurePlate tpp;
	
	public ContainerPressurePlate(TileEntityPressurePlate tpp, InventoryPlayer par1InventoryPlayer)
	{
		this.tpp = tpp;
		this.drawSlots(par1InventoryPlayer, tpp);
	}
	
	public void drawSlots(InventoryPlayer par1InventoryPlayer, IInventory var1)
	{
        int var4;
        int var5;
        for (var5 = 0; var5 < var1.getSizeInventory(); ++var5)
        {
        	this.addSlotToContainer(new SlotPP(var1, 0, -54, 154));
        }

        int var6;
        for (var6 = 0; var6 < 3; ++var6)
        {
            for (int var7 = 0; var7 < 9; ++var7)
            {
                this.addSlotToContainer(new Slot(par1InventoryPlayer, var7 + var6 * 9 + 9, -81 + var6 * 18, 132 - var7 * 18));
            }
        }

        for (var6 = 0; var6 < 9; ++var6)
        {
            this.addSlotToContainer(new Slot(par1InventoryPlayer, var6, -22, 132 - var6 * 18));
        }
	}
	
	public void removeAllSlots()
	{
		this.inventorySlots.clear();
	}
	
    public Slot getSlot(int par1)
    {
    	if(this.inventorySlots.size() >= par1)
    	{
    		return (Slot)this.inventorySlots.get(par1);
    	}
    	return null;
    }
    
    public void putStackInSlot(int par1, ItemStack par2ItemStack)
    {
    	if(this.getSlot(par1) != null)
    	{
    		this.getSlot(par1).putStack(par2ItemStack);
    	}
    }
    
    public void putStacksInSlots(ItemStack[] par1ArrayOfItemStack)
    {
        for (int var2 = 0; var2 < par1ArrayOfItemStack.length; ++var2)
        {
        	if(this.getSlot(var2) != null)
        	{
        		this.getSlot(var2).putStack(par1ArrayOfItemStack[var2]);
        	}
        }
    }

	public boolean canInteractWith(EntityPlayer var1) 
	{
		return this.tpp.isUseableByPlayer(var1);
	}
	
    public ItemStack transferStackInSlot(int par1)
    {
        ItemStack var2 = null;
        Slot var3 = (Slot)this.inventorySlots.get(par1);
        if (var3 != null && !var3.getHasStack())
        {
            if (par1 != 0)
            {
            	if(tpp.getStackInSlot(0) != null)
            	{
                    ItemStack var4 = var3.getStack();
	                if (!this.mergeItemStack(tpp.getStackInSlot(0), 1, 37, true))
	                {
	                    return null;
	                }
	                
	                if (var4 == null || var4.stackSize == 0)
	                {
	                    ((Slot) this.inventorySlots.get(0)).putStack((ItemStack)null);
	                }
	                else
	                {
	                    var3.onSlotChanged();
	                }

	                var3.onPickupFromSlot(var4);
            	}
            }
        }

        if (var3 != null && var3.getHasStack())
        {
            ItemStack var4 = var3.getStack();
            var2 = var4.copy();

            if (par1 == 0)
            {
                if (!this.mergeItemStack(var4, 1, 37, true))
                {
                    return null;
                }
            }
            else
            {
                if (!((Slot)this.inventorySlots.get(0)).isItemValid(var4))
                {
                    return null;
                }
                
                if(((Slot)this.inventorySlots.get(0)).getHasStack() && !this.mergeItemStack(tpp.getStackInSlot(0), 1, 37, true))
                {
                	return null;
                }

                if (var4.hasTagCompound() && var4.stackSize == 1)
                {
                    ((Slot)this.inventorySlots.get(0)).putStack(var4.copy());
                    var4.stackSize = 0;
                }
                else if (var4.stackSize >= 1)
                {
                    ((Slot)this.inventorySlots.get(0)).putStack(new ItemStack(var4.itemID, 1, var4.getItemDamage()));
                    --var4.stackSize;
                }
            }

            if (var4.stackSize == 0)
            {
                var3.putStack((ItemStack)null);
            }
            else
            {
                var3.onSlotChanged();
            }

            if (var4.stackSize == var2.stackSize)
            {
                return null;
            }

            var3.onPickupFromSlot(var4);
        }

        return var2;
    }
	
    public void switchMob(String var1)
    {	
    	if(var1 == "Players")
    	{
    		var1 = "humanoid";
    	}
    	if(var1 == "Items")
    	{
    		var1 = "Item";
    	}
    	for(int var4 = 0;var4 < tpp.allowedMobs.length;var4++)
    	{
    		PPList pp = tpp.allowedMobs[var4];
    		if(pp.getMobname().matches(var1) && !pp.getEnabled())
    		{
    			tpp.allowedMobs[var4].enable();
    			return;
    		}
    		if(pp.getMobname().matches(var1) && pp.getEnabled())
    		{
    			tpp.allowedMobs[var4].disable();
    			return;
    		}
    	}
    	return;
    }
    
    public void switchPlayer(String var1)
    {
    	for(int var2 = 0;var2 < tpp.allowedPlayers.size();var2++)
    	{
    		PPPlayerList pp = (PPPlayerList)tpp.allowedPlayers.get(var2);
    		if(pp.getUsername().matches(var1) && !pp.getEnabled())
    		{
    			pp.enable();
    			return;
    		}
    		if(pp.getUsername().matches(var1) && pp.getEnabled())
    		{
    			pp.disable();
    			return;
    		}
    	}
    	return;
    }

}