package xelitez.ironpp;

import org.bukkit.craftbukkit.v1_4_6.inventory.CraftInventory;
import org.bukkit.craftbukkit.v1_4_6.inventory.CraftInventoryView;
import org.bukkit.inventory.InventoryView;

import net.minecraft.server.v1_4_6.Container;
import net.minecraft.server.v1_4_6.EntityHuman;
import net.minecraft.server.v1_4_6.IInventory;
import net.minecraft.server.v1_4_6.ItemStack;
import net.minecraft.server.v1_4_6.PlayerInventory;
import net.minecraft.server.v1_4_6.Slot;

public class ContainerPressurePlate extends Container
{
    public TileEntityPressurePlate tpp;
    public EntityHuman player;
    public InventoryView bukkitEntity;
    private IInventory inventory;

    public ContainerPressurePlate(TileEntityPressurePlate var1, PlayerInventory var2)
    {
        this.tpp = var1;
        this.player = var2.player;
        this.drawSlots(var2, var1);
    }

    public void drawSlots(IInventory var1, IInventory var2)
    {
        this.inventory = var2;
        var2.startOpen();

        for (int var4 = 0; var4 < var2.getSize(); ++var4)
        {
            this.a((Slot)(new SlotPP(var2, 0, 65, 173)));
        }

        int var5;

        for (var5 = 0; var5 < 3; ++var5)
        {
            for (int var6 = 0; var6 < 9; ++var6)
            {
                this.a(new Slot(var1, var6 + var5 * 9 + 9, 38 + var5 * 18, 151 - var6 * 18));
            }
        }

        for (var5 = 0; var5 < 9; ++var5)
        {
            this.a(new Slot(var1, var5, 97, 151 - var5 * 18));
        }
    }

    public void removeAllSlots()
    {
        this.c.clear();
    }

    public Slot getSlot(int var1)
    {
        return this.c.size() > var1 ? (Slot)this.c.get(var1) : null;
    }

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

    public boolean a(EntityHuman var1)
    {
        return this.tpp.a_(var1);
    }

    public ItemStack b(EntityHuman var1, int var2)
    {
        ItemStack var3 = null;
        Slot var4 = (Slot)this.c.get(var2);
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
                ((Slot)this.c.get(0)).set((ItemStack)null);
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
                if (!((Slot)this.c.get(0)).isAllowed(var5))
                {
                    return null;
                }

                if (((Slot)this.c.get(0)).d() && !this.a(this.tpp.getItem(0), 1, 37, true))
                {
                    return null;
                }

                if (var5.hasTag() && var5.count == 1)
                {
                    ((Slot)this.c.get(0)).set(var5.cloneItemStack());
                    var5.count = 0;
                }
                else if (var5.count >= 1)
                {
                    ((Slot)this.c.get(0)).set(new ItemStack(var5.id, 1, var5.getData()));
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

    public void b(EntityHuman var1)
    {
        super.b(var1);
        this.inventory.f();
    }

	@Override
	public InventoryView getBukkitView() 
	{
		if(this.bukkitEntity != null)
		{
			return this.bukkitEntity;
		}
		if(tpp != null && player != null)
		{
			CraftInventory inv = new CraftInventory(this.tpp);
			this.bukkitEntity = new CraftInventoryView(player.getBukkitEntity(), inv, this);
			return this.bukkitEntity;
		}
		return null;
	}
}
