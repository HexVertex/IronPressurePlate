package xelitez.ironpp;

import net.minecraft.server.v1_4_6.IInventory;
import net.minecraft.server.v1_4_6.ItemBlock;
import net.minecraft.server.v1_4_6.ItemStack;
import net.minecraft.server.v1_4_6.Slot;

public class SlotPP extends Slot
{
    public SlotPP(IInventory var1, int var2, int var3, int var4)
    {
        super(var1, var2, var3, var4);
    }

    public boolean isAllowed(ItemStack var1)
    {
        return var1.getItem() instanceof ItemBlock;
    }
}
