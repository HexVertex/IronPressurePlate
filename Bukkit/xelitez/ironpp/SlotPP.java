package xelitez.ironpp;

import net.minecraft.server.IInventory;
import net.minecraft.server.ItemBlock;
import net.minecraft.server.ItemStack;
import net.minecraft.server.Slot;

public class SlotPP extends Slot
{
    public SlotPP(IInventory var1, int var2, int var3, int var4)
    {
        super(var1, var2, var3, var4);
    }

    /**
     * Check if the stack is a valid item for this slot. Always true beside for the armor slots.
     */
    public boolean isAllowed(ItemStack var1)
    {
        return var1.getItem() instanceof ItemBlock;
    }
}
