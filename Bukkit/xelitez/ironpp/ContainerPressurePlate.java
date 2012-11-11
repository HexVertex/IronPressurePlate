package xelitez.ironpp;

import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.bukkit.craftbukkit.inventory.CraftInventoryPlayer;
import org.bukkit.craftbukkit.inventory.CraftInventoryView;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

import net.minecraft.server.Container;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.IInventory;
import net.minecraft.server.ItemStack;
import net.minecraft.server.PlayerInventory;
import net.minecraft.server.Slot;

public class ContainerPressurePlate extends Container
{
    public TileEntityPressurePlate tpp;
    public HumanEntity player;
    public IInventory inventory;

    public ContainerPressurePlate(TileEntityPressurePlate var1, PlayerInventory var2)
    {
        this.tpp = var1;
        player = var2.player.getBukkitEntity();
        this.drawSlots(var2, var1);
    }

    public void drawSlots(IInventory var1, IInventory var2)
    {
        this.inventory = var2;
        var2.startOpen();

        for (int var4 = 0; var4 < var2.getSize(); ++var4)
        {
            this.a(new SlotPP(var2, 0, -54, 154));
        }

        int var5;

        for (var5 = 0; var5 < 3; ++var5)
        {
            for (int var6 = 0; var6 < 9; ++var6)
            {
                this.a(new Slot(var1, var6 + var5 * 9 + 9, -81 + var5 * 18, 132 - var6 * 18));
            }
        }

        for (var5 = 0; var5 < 9; ++var5)
        {
            this.a(new Slot(var1, var5, -22, 132 - var5 * 18));
        }
    }

    public void removeAllSlots()
    {
        this.b.clear();
    }

    public Slot getSlot(int var1)
    {
        return this.b.size() > var1 ? (Slot)this.b.get(var1) : null;
    }

    /**
     * args: slotID, itemStack to put in slot
     */
    public void setItem(int var1, ItemStack var2)
    {
        if (this.getSlot(var1) != null)
        {
            this.getSlot(var1).set(var2);
        }
    }

    public void a(ItemStack[] var1)
    {
        for (int var2 = 0; var2 < var1.length; ++var2)
        {
            if (this.getSlot(var2) != null)
            {
                this.getSlot(var2).set(var1[var2]);
            }
        }
    }

    public boolean c(EntityHuman var1)
    {
        return this.tpp.a(var1);
    }

    /**
     * Take a stack from the specified inventory slot.
     */
    public ItemStack b(EntityHuman var1, int var2)
    {
        ItemStack var3 = null;
        Slot var4 = (Slot)this.b.get(var2);
        ItemStack var5;

        if (var4 != null && !var4.d() && var2 != 0 && this.tpp.getItem(0) != null)
        {
            var5 = var4.getItem();

            if (!this.a(this.tpp.getItem(0), 1, 37, true))
            {
                return null;
            }

            if (var5 != null && var5.count != 0)
            {
                var4.e();
            }
            else
            {
                ((Slot)this.b.get(0)).set((ItemStack)null);
            }
        }

        if (var4 != null && var4.d())
        {
            var5 = var4.getItem();
            var3 = var5.cloneItemStack();

            if (var2 == 0)
            {
                if (!this.a(var5, 1, 37, true))
                {
                    return null;
                }
            }
            else
            {
                if (!((Slot)this.b.get(0)).isAllowed(var5))
                {
                    return null;
                }

                if (((Slot)this.b.get(0)).d() && !this.a(this.tpp.getItem(0), 1, 37, true))
                {
                    return null;
                }

                if (var5.hasTag() && var5.count == 1)
                {
                    ((Slot)this.b.get(0)).set(var5.cloneItemStack());
                    var5.count = 0;
                }
                else if (var5.count >= 1)
                {
                    ((Slot)this.b.get(0)).set(new ItemStack(var5.id, 1, var5.getData()));
                    --var5.count;
                }
            }

            if (var5.count == 0)
            {
                var4.set((ItemStack)null);
            }
            else
            {
                var4.e();
            }

            if (var5.count == var3.count)
            {
                return null;
            }
        }

        return var3;
    }

    public void switchMob(String var1)
    {
        if (var1 == "Players")
        {
            var1 = "humanoid";
        }

        if (var1 == "Items")
        {
            var1 = "Item";
        }

        for (int var2 = 0; var2 < this.tpp.allowedMobs.length; ++var2)
        {
            PPList var3 = this.tpp.allowedMobs[var2];

            if (var3.getMobname().matches(var1) && !var3.getEnabled())
            {
                this.tpp.allowedMobs[var2].enable();
                return;
            }

            if (var3.getMobname().matches(var1) && var3.getEnabled())
            {
                this.tpp.allowedMobs[var2].disable();
                return;
            }
        }
    }

    public void switchPlayer(String var1)
    {
        for (int var2 = 0; var2 < this.tpp.allowedPlayers.size(); ++var2)
        {
            PPPlayerList var3 = (PPPlayerList)this.tpp.allowedPlayers.get(var2);

            if (var3.getUsername().matches(var1) && !var3.getEnabled())
            {
                var3.enable();
                return;
            }

            if (var3.getUsername().matches(var1) && var3.getEnabled())
            {
                var3.disable();
                return;
            }
        }
    }

    /**
     * Callback for when the crafting gui is closed.
     */
    public void a(EntityHuman var1)
    {
        super.a(var1);
        this.inventory.f();
    }
    
    private CraftInventoryView bukkitEntity = null;

	@Override
	public CraftInventoryView getBukkitView() 
	{
		if (this.bukkitEntity != null)
		{
			return this.bukkitEntity;
		}
		CraftInventory inventory;
		if (this.inventory instanceof PlayerInventory) 
		{
		inventory = new CraftInventoryPlayer((PlayerInventory)this.inventory);
		}
		else
		{
			inventory = new CraftInventory(this.inventory);
		}
		this.bukkitEntity = new CraftInventoryView(this.player, inventory, this);
		return this.bukkitEntity;
	}
}
