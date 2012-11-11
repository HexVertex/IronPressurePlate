package xelitez.ironpp;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;

import net.minecraft.server.Block;
import net.minecraft.server.Chunk;
import net.minecraft.server.Entity;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityLiving;
import net.minecraft.server.EntityTypes;
import net.minecraft.server.IInventory;
import net.minecraft.server.ItemStack;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.NBTTagList;
import net.minecraft.server.TileEntity;
import net.minecraft.server.World;
import xelitez.ironpp.PPSettings$SettingsButton;

public class TileEntityPressurePlate extends TileEntity implements IInventory
{
    public ItemStack[] item = new ItemStack[1];
    public PPList[] allowedMobs;
    public List mobs = new ArrayList();
    public List allowedPlayers = new ArrayList();
    List living = new ArrayList();
    List other = new ArrayList();
    public boolean update = false;
    private int countdown = 0;
    public boolean activated = false;
    private boolean check = false;
    private boolean register = false;
    public PPSettings pps;
    public List settings;
    public String password = "";

    public TileEntityPressurePlate()
    {
        this.registerMobs();
        this.registerSettings();
        this.register = true;
    }

    public void setActivated(boolean var1, World var2, int var3, int var4, int var5)
    {
        this.activated = var1;
        var2.update(var3, var4, var5, var2.getTypeId(var3, var4, var5));
        Chunk var6 = var2.getChunkAt(var3 >> 4, var5 >> 4);
        int var7 = var3 & 15;
        int var8 = var5 & 15;

        if (var2.isStatic || var6.seenByPlayer && Block.u[var6.getTypeId(var7, var4, var8) & 4095])
        {
            var2.notify(var3, var4, var5);
        }

        if (!var2.isStatic || FMLCommonHandler.instance().getSide().isServer())
        {
            PacketSendManager.sendBlockBooleanToClient(this, var1);
        }
    }

    /**
     * Allows the entity to update its state. Overridden in most subclasses, e.g. the mob spawner uses this to count
     * ticks and creates a new spawn inside its implementation.
     */
    public void g()
    {
        if (this.x != 0 && this.y != 0 && this.z != 0 && this.register && this.world != null)
        {
            if (!PPRegistry.getContainsPressurePlate(this, this.world.worldProvider.dimension))
            {
                PPRegistry.addPressurePlate(this, this.world.worldProvider.dimension);
            }

            this.register = false;
        }

        if (this.countdown > 0)
        {
            --this.countdown;
        }

        if (this.world != null && this.update)
        {
            if (PPRegistry.world == null)
            {
                PPRegistry.world = this.world;
            }

            if (this.check)
            {
                this.world.applyPhysics(this.x, this.y, this.z, IronPP.APressurePlateIron.id);
                this.world.applyPhysics(this.x, this.y - 1, this.z, IronPP.APressurePlateIron.id);
            }
        }
    }

    public void scheduleUpdate(int var1)
    {
        this.update = true;
        this.countdown = var1;
    }

    public void registerMobs()
    {
        if (this.mobs.size() == 0)
        {
            Field var1 = EntityTypes.class.getDeclaredFields()[1];
            var1.setAccessible(true);
            Map var2 = null;

            try
            {
                var2 = (Map)var1.get((Object)null);
            }
            catch (Exception var8)
            {
                var8.printStackTrace();
            }

            if (var2 != null)
            {
                Iterator var3 = var2.keySet().iterator();

                while (var3.hasNext())
                {
                    Class var4 = (Class)var3.next();

                    try
                    {
                        String var5;

                        if (EntityLiving.class.isAssignableFrom(var4) && var4.getConstructor(new Class[] {World.class}) != null && !Modifier.isAbstract(var4.getModifiers()))
                        {
                            var5 = (String)var2.get(var4);
                            this.living.add(var5);
                        }

                        if (Entity.class.isAssignableFrom(var4) && !EntityLiving.class.isAssignableFrom(var4) && var4.getConstructor(new Class[] {World.class}) != null && !Modifier.isAbstract(var4.getModifiers()))
                        {
                            var5 = (String)var2.get(var4);
                            this.other.add(var5);
                        }
                    }
                    catch (SecurityException var6)
                    {
                        var6.printStackTrace();
                    }
                    catch (NoSuchMethodException var7)
                    {
                        ;
                    }
                }
            }

            Collections.sort(this.living);
            Collections.sort(this.other);
            int var11;

            for (var11 = 0; var11 < this.living.size(); ++var11)
            {
                this.mobs.add(this.living.get(var11));
            }

            for (var11 = 0; var11 < this.other.size(); ++var11)
            {
                this.mobs.add(this.other.get(var11));
            }

            this.mobs.add(0, "humanoid");
            this.mobs.remove("item");
        }

        if (this.allowedMobs == null)
        {
            this.allowedMobs = new PPList[this.mobs.size()];
            boolean[] var9 = new boolean[this.mobs.size()];

            for (int var10 = 0; var10 < this.mobs.size(); ++var10)
            {
                if (this.allowedMobs[var10] == null)
                {
                    this.allowedMobs[var10] = new PPList((String)this.mobs.get(var10));
                }
                else
                {
                    var9[var10] = this.allowedMobs[var10].getEnabled();
                    this.allowedMobs[var10] = new PPList((String)this.mobs.get(var10), var9[var10]);
                }
            }
        }
    }

    public void registerPlayer(String var1)
    {
        if (!this.isInPlayerList(var1))
        {
            this.addPlayer(var1);
        }
    }

    public void registerSettings()
    {
        if (this.settings == null || this.settings.size() == 0)
        {
            this.settings = new ArrayList();
            this.pps = new PPSettings(this);
            PPSettings var10000 = this.pps;
            PPSettings.addLineWithButton("Unlisted players are by default:", "Enabled", "Disabled", false, 0);
            var10000 = this.pps;
            PPSettings.addLineWithButton("Sound is:", "On", "Off", true, 1);
            var10000 = this.pps;
            PPSettings.addLineWithButton("Password", "Enabled", "Disabled", false, 2);
            var10000 = this.pps;
            PPSettings.addClickableLine("Set Password", 0);
            var10000 = this.pps;
            PPSettings.addLineWithButton("Ask password on break", "Yes", "No", false, 3);
            var10000 = this.pps;
            PPSettings.addLine("Note: if you have set no password but enabled password then just press enter if you get stuck on the gui screen");
            int var1 = 0;

            while (true)
            {
                PPSettings var10001 = this.pps;

                if (var1 >= PPSettings.buttons.size())
                {
                    break;
                }

                var10001 = this.pps;
                this.settings.add(PPSettings.buttons.get(var1));
                ++var1;
            }
        }
    }

    public boolean addPlayer(String var1)
    {
        for (int var2 = 0; var2 < this.allowedPlayers.size(); ++var2)
        {
            if (((PPPlayerList)this.allowedPlayers.get(var2)).getUsername().matches(var1))
            {
                return false;
            }
        }

        this.allowedPlayers.add(new PPPlayerList(var1));
        return true;
    }

    public boolean removePlayer(String var1)
    {
        for (int var2 = 0; var2 < this.allowedPlayers.size(); ++var2)
        {
            if (((PPPlayerList)this.allowedPlayers.get(var2)).getUsername().matches(var1))
            {
                this.allowedPlayers.remove(var2);
                return true;
            }
        }

        return false;
    }

    public void switchMob(String var1, World var2)
    {
        for (int var3 = 0; var3 < this.allowedMobs.length; ++var3)
        {
            PPList var4 = this.allowedMobs[var3];

            if (var2.isStatic)
            {
                if (var4.getMobname().matches(var1) && !var4.getEnabled())
                {
                    this.allowedMobs[var3].enable();
                    return;
                }

                if (var4.getMobname().matches(var1) && var4.getEnabled())
                {
                    this.allowedMobs[var3].disable();
                    return;
                }
            }
            else if (var4.getMobname().matches(var1))
            {
                if (!var4.getEnabled())
                {
                    this.allowedMobs[var3].enable();
                }
                else
                {
                    this.allowedMobs[var3].disable();
                }

                PacketSendManager.sendSwitchMobButtonPacketToClient(this, var3);
                return;
            }
        }
    }

    public void switchPlayer(String var1, World var2)
    {
        for (int var3 = 0; var3 < this.allowedPlayers.size(); ++var3)
        {
            PPPlayerList var4 = (PPPlayerList)this.allowedPlayers.get(var3);

            if (var4.getUsername().matches(var1))
            {
                if (!var4.getEnabled())
                {
                    var4.enable();
                }
                else
                {
                    var4.disable();
                }

                PacketSendManager.sendPressurePlatePlayerDataToClient(this);
                return;
            }
        }
    }

    public boolean findMobName(String var1)
    {
        for (int var2 = 0; var2 < this.allowedMobs.length; ++var2)
        {
            PPList var3 = this.allowedMobs[var2];

            if (var1 != null && var3.getMobname().matches(var1) && var3.getEnabled())
            {
                return true;
            }
        }

        return false;
    }

    public boolean isInPlayerList(String var1)
    {
        for (int var2 = 0; var2 < this.allowedPlayers.size(); ++var2)
        {
            PPPlayerList var3 = (PPPlayerList)this.allowedPlayers.get(var2);

            if (var1 != null && var3 != null && var3.getUsername().matches(var1) && var3.getEnabled())
            {
                return true;
            }
        }

        return false;
    }

    public boolean isPlayerInList(String var1)
    {
        for (int var2 = 0; var2 < this.allowedPlayers.size(); ++var2)
        {
            PPPlayerList var3 = (PPPlayerList)this.allowedPlayers.get(var2);

            if (var1 != null && var3 != null && var3.getUsername().matches(var1))
            {
                return true;
            }
        }

        return false;
    }

    public void setEnabledForPlayer(String var1, boolean var2)
    {
        for (int var3 = 0; var3 < this.allowedPlayers.size(); ++var3)
        {
            PPPlayerList var4 = (PPPlayerList)this.allowedPlayers.get(var3);

            if (var4.getUsername().matches(var1))
            {
                var4.isEnabled = var2;
            }
        }
    }

    public void switchSetting(int var1)
    {
        if (((PPSettings$SettingsButton)this.settings.get(var1)).enabled)
        {
            ((PPSettings$SettingsButton)this.settings.get(var1)).enabled = false;
        }
        else
        {
            ((PPSettings$SettingsButton)this.settings.get(var1)).enabled = true;
        }

        if (var1 == 2)
        {
            PPRegistry.setUsesPassword(this, this.world.worldProvider.dimension, this.getIsEnabled(2));
            PacketSendManager.sendUsesPasswordToClient(this.x, this.y, this.z, this.world.worldProvider.dimension, Boolean.valueOf(this.getIsEnabled(2)));
        }

        PacketSendManager.sendSettingsDataToClient(this);
    }

    public void setSetting(int var1, boolean var2)
    {
        ((PPSettings$SettingsButton)this.settings.get(var1)).enabled = var2;
    }

    public boolean getIsEnabled(int var1)
    {
        return ((PPSettings$SettingsButton)this.settings.get(var1)).enabled;
    }

    /**
     * Reads a tile entity from NBT.
     */
    public void a(NBTTagCompound var1)
    {
        super.a(var1);
        this.registerMobs();
        this.registerSettings();
        NBTTagList var2;
        int var3;
        NBTTagCompound var4;
        byte var5;

        try
        {
            var2 = var1.getList("Mobs");

            for (var3 = 0; var3 < this.mobs.size(); ++var3)
            {
                var4 = (NBTTagCompound)var2.get(var3);
                var5 = var4.getByte("mob");

                if (var5 >= 0 && var5 < this.allowedMobs.length)
                {
                    this.allowedMobs[var5] = PPList.loadSettingsFromNBT(var4, (String)this.mobs.get(var3));
                }
            }
        }
        catch (Exception var12)
        {
            ;
        }

        try
        {
            var2 = var1.getList("Players");

            for (var3 = 0; var3 < var2.size(); ++var3)
            {
                var4 = (NBTTagCompound)var2.get(var3);
                var5 = var4.getByte("player");
                String var6 = var4.getString("username");
                boolean var7 = var4.getBoolean("isEnabled");

                if (var5 >= 0 && var5 < this.allowedMobs.length)
                {
                    this.allowedPlayers.add(new PPPlayerList(var6, var7));
                }
            }
        }
        catch (Exception var11)
        {
            ;
        }

        try
        {
            var2 = var1.getList("Items");

            for (var3 = 0; var3 < var2.size(); ++var3)
            {
                var4 = (NBTTagCompound)var2.get(var3);
                int var13 = var4.getByte("Slot") & 255;

                if (var13 >= 0 && var13 < this.item.length)
                {
                    this.item[var13] = ItemStack.a(var4);
                }
            }
        }
        catch (Exception var10)
        {
            FMLLog.log(Level.FINE, var10, "no items found, adding...", new Object[0]);
            this.item = new ItemStack[1];
        }

        try
        {
            var2 = var1.getList("Settings");

            for (var3 = 0; var3 < var2.size(); ++var3)
            {
                var4 = (NBTTagCompound)var2.get(var3);
                var5 = var4.getByte("setting");
                boolean var14 = var4.getBoolean("enabled");

                if (var5 >= 0)
                {
                    PPSettings var10001 = this.pps;

                    if (var5 < PPSettings.buttons.size())
                    {
                        this.setSetting(var5, var14);
                    }
                }
            }
        }
        catch (Exception var9)
        {
            ;
        }

        try
        {
            this.password = var1.getString("password");
        }
        catch (Exception var8)
        {
            FMLLog.log(Level.FINE, var8, "no password setting found, adding...", new Object[0]);
            this.password = "";
        }

        this.register = true;
        this.check = true;
        this.scheduleUpdate(0);
    }

    /**
     * Writes a tile entity to NBT.
     */
    public void b(NBTTagCompound var1)
    {
        super.b(var1);
        NBTTagList var2 = new NBTTagList();

        for (int var3 = 0; var3 < this.allowedMobs.length; ++var3)
        {
            NBTTagCompound var4 = new NBTTagCompound();
            var4.setByte("mob", (byte)var3);
            this.allowedMobs[var3].writeToNBT(var4);
            var2.add(var4);
        }

        var1.set("Mobs", var2);
        NBTTagList var8 = new NBTTagList();

        if (this.allowedPlayers.size() > 0 && this.allowedPlayers != null)
        {
            for (int var9 = 0; var9 < this.allowedPlayers.size(); ++var9)
            {
                NBTTagCompound var5 = new NBTTagCompound();
                var5.setByte("player", (byte)var9);
                var5.setString("username", ((PPPlayerList)this.allowedPlayers.get(var9)).getUsername());
                var5.setBoolean("isEnabled", ((PPPlayerList)this.allowedPlayers.get(var9)).getEnabled());
                var8.add(var5);
            }

            var1.set("Players", var8);
        }

        NBTTagList var11 = new NBTTagList();

        for (int var10 = 0; var10 < this.item.length; ++var10)
        {
            if (this.item[var10] != null)
            {
                NBTTagCompound var6 = new NBTTagCompound();
                var6.setByte("Slot", (byte)var10);
                this.item[var10].save(var6);
                var11.add(var6);
            }
        }

        var1.set("Items", var11);
        NBTTagList var12 = new NBTTagList();
        int var13 = 0;

        while (true)
        {
            PPSettings var10001 = this.pps;

            if (var13 >= PPSettings.buttons.size())
            {
                var1.set("Settings", var12);
                var1.setString("password", this.password);
                return;
            }

            NBTTagCompound var7 = new NBTTagCompound();
            var7.setByte("setting", (byte)var13);
            var7.setBoolean("enabled", this.getIsEnabled(var13));
            var12.add(var7);
            ++var13;
        }
    }

    /**
     * Returns the number of slots in the inventory.
     */
    public int getSize()
    {
        return this.item.length;
    }

    /**
     * Returns the stack in slot i
     */
    public ItemStack getItem(int var1)
    {
        return this.item[var1];
    }

    /**
     * Removes from an inventory slot (first arg) up to a specified number (second arg) of items and returns them in a
     * new stack.
     */
    public ItemStack splitStack(int var1, int var2)
    {
        ItemStack var3 = this.getItem(var1);

        if (var3 != null)
        {
            if (var3.count <= var2)
            {
                this.setItem(var1, (ItemStack)null);
            }
            else
            {
                var3 = var3.a(var2);

                if (var3.count == 0)
                {
                    this.setItem(var1, (ItemStack)null);
                }
            }
        }

        return var3;
    }

    /**
     * When some containers are closed they call this on each slot, then drop whatever it returns as an EntityItem -
     * like when you close a workbench GUI.
     */
    public ItemStack splitWithoutUpdate(int var1)
    {
        ItemStack var2 = this.getItem(var1);

        if (var2 != null)
        {
            this.setItem(var1, (ItemStack)null);
        }

        return var2;
    }

    /**
     * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
     */
    public void setItem(int var1, ItemStack var2)
    {
        this.item[var1] = var2;

        if (var2 != null && var2.count > this.getMaxStackSize())
        {
            var2.count = this.getMaxStackSize();
        }

        PPRegistry.setItem(this, this.world.worldProvider.dimension, this.item[0]);
        this.world.i(this.x, this.y, this.z);
    }

    /**
     * Returns the name of the inventory.
     */
    public String getName()
    {
        return "Advanced Pressure Plate";
    }

    /**
     * Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be extended. *Isn't
     * this more of a set than a get?*
     */
    public int getMaxStackSize()
    {
        return 1;
    }

    /**
     * Do not make give this method the name canInteractWith because it clashes with Container
     */
    public boolean a(EntityHuman var1)
    {
        return this.world.getTileEntity(this.x, this.y, this.z) != this ? false : var1.e((double)this.x + 0.5D, (double)this.y + 0.5D, (double)this.z + 0.5D) <= 64.0D;
    }

    public void startOpen() {}

    public void f() {}

	@Override
	public ItemStack[] getContents() 
	{
		return this.item;
	}

	@Override
	public void onOpen(CraftHumanEntity paramCraftHumanEntity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClose(CraftHumanEntity paramCraftHumanEntity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<HumanEntity> getViewers() 
	{
		return null;
	}

	@Override
	public void setMaxStackSize(int paramInt) {
		// TODO Auto-generated method stub
		
	}
}
