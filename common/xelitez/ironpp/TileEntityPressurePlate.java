/**
 * this is the class for the TileEntity of the advanced
 * iron Pressure Plate block.
 * 
 * @author Kalvin
 */
package xelitez.ironpp;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import xelitez.ironpp.PPSettings.SettingsButton;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;

import net.minecraft.src.Block;
import net.minecraft.src.Chunk;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NBTTagList;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;

public class TileEntityPressurePlate extends TileEntity implements IInventory
{
	public ItemStack[] item = new ItemStack[1];
	/**
	 * registeres the lists of mobs and players.
	 */
	public PPList[] allowedMobs;
	public List mobs  = new ArrayList();
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
    	registerMobs();
    	this.registerSettings();
    	this.register = true;
    }
	
	public void setActivated(boolean b, World world, int par1, int par2, int par3)
	{
		this.activated = b;
		world.notifyBlockChange(par1, par2, par3, world.getBlockId(par1, par2, par3));
        Chunk var5 = world.getChunkFromChunkCoords(par1 >> 4, par3 >> 4);
        int var6 = par1 & 15;
        int var7 = par3 & 15;
        if ((world.isRemote || var5.deferRender && Block.requiresSelfNotify[var5.getBlockID(var6, par2, var7) & 4095]))
        {
            world.markBlockNeedsUpdate(par1, par2, par3);
        }
        if(!world.isRemote || FMLCommonHandler.instance().getSide().isServer())
        {	
        	PacketSendManager.sendBlockBooleanToClient(this, b);
        }
	}
	
    public void updateEntity() 
    {
    	if(this.xCoord != 0 && this.yCoord != 0 && this.zCoord != 0 && register && this.worldObj != null)
    	{
    		if(!PPRegistry.getContainsPressurePlate(this, this.worldObj.provider.dimensionId))
    		{
    			PPRegistry.addPressurePlate(this, this.worldObj.provider.dimensionId);
    		}
    		register = false;
    	}
    	if(countdown > 0)
    	{
    		countdown--;
    	}
    	if(worldObj != null && update == true)
    	{
    		if(PPRegistry.world == null)
    		{
    			PPRegistry.world = this.worldObj;
    		}
            if(check)
            {
            	worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, IronPP.APressurePlateIron.blockID);
            	worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord - 1, zCoord, IronPP.APressurePlateIron.blockID);            	
            }
    	}
    }
    
    public void scheduleUpdate(int i)
    {
    	update = true;
    	countdown = i;
    }
    
	/**
	 * a method to register all mobs that are registered in the entity list.
	 * most of the credit for this method goes to Risugami.
	 */
    public void registerMobs()
    {
        if (mobs.size() == 0)
        {
            Field field = (net.minecraft.src.EntityList.class).getDeclaredFields()[1];
            field.setAccessible(true);
            Map map = null;

            try
            {
                map = (Map)field.get(null);
            }
            catch (Exception exception)
            {
                exception.printStackTrace();
            }

            if (map != null)
            {
                for (Iterator iterator = map.keySet().iterator(); iterator.hasNext();)
                {
                    Class class1 = (Class)iterator.next();

                    try
                    {
                        if ((net.minecraft.src.EntityLiving.class).isAssignableFrom(class1) && class1.getConstructor(new Class[]
                                {
                                    net.minecraft.src.World.class
                                }) != null && !Modifier.isAbstract(class1.getModifiers()))
                        {
                            String s1 = (String)map.get(class1);
                            living.add(s1);
                        }
                        if ((net.minecraft.src.Entity.class).isAssignableFrom(class1) && !(net.minecraft.src.EntityLiving.class).isAssignableFrom(class1) && class1.getConstructor(new Class[]
                                {
                                    net.minecraft.src.World.class
                                }) != null && !Modifier.isAbstract(class1.getModifiers()))
                        {
                            String s1 = (String)map.get(class1);
                            other.add(s1);
                        }
                    }
                    catch (SecurityException securityexception)
                    {
                        securityexception.printStackTrace();
                    }
                    catch (NoSuchMethodException nosuchmethodexception) { }
                }
            }
            Collections.sort(living);
            Collections.sort(other);
            int var1;
            for(var1 = 0;var1 < living.size();var1++)
            {
            	mobs.add(living.get(var1));
            }
            for(var1 = 0;var1 < other.size();var1++)
            {
            	mobs.add(other.get(var1));
            }
            mobs.add(0, "humanoid");
            mobs.remove("item");
        }
        if(allowedMobs == null)
        {
        	allowedMobs = new PPList[mobs.size()];
	        boolean[] bl;
	        bl = new boolean[mobs.size()];
	        for(int var1 = 0;var1 < mobs.size();var1++)
	        {
	        	if(allowedMobs[var1] == null)
	        	{
	        		allowedMobs[var1] = new PPList((String)mobs.get(var1));
	        	}
	        	else
    			{
    				bl[var1] = allowedMobs[var1].getEnabled();
        			allowedMobs[var1] = new PPList((String)mobs.get(var1), bl[var1]);
    			}
    		}
        }
    }
    
    /**
     * method used to register the player that has
     * placed the pressure plate.
     * @param username
     */
    public void registerPlayer(String username)
    {
        if(!isInPlayerList(username))
        {
        	addPlayer(username);
        }
    }
    
    public void registerSettings()
    {
    	if(settings == null || settings.size() == 0)
    	{
	    	settings = new ArrayList();
	    	pps = new PPSettings(this);
	    	pps.addLineWithButton("Unlisted players are by default:", "Enabled", "Disabled", false, 0);
	    	pps.addLineWithButton("Sound is:", "On", "Off", true, 1);
	    	pps.addLineWithButton("Password", "Enabled", "Disabled", false, 2);
	    	pps.addClickableLine("Set Password", 0);
	    	pps.addLineWithButton("Ask password on break", "Yes", "No", false, 3);
	    	pps.addLine("Note: if you have set no password but enabled password then just press enter if you get stuck on the gui screen");
	    	for(int var1 = 0;var1 < pps.buttons.size();var1++)
	    	{
	    		settings.add(pps.buttons.get(var1));
	    	}
    	}
    }
    
    /**
     * method to add a playername to the pressure plate
     * @param player	name of the player
     * @return	true or false 	(telling if it succeeded to add or failed)
     */
    public boolean addPlayer(String player)
    {
    	for(int i = 0;i < allowedPlayers.size();i++)
    	{
    		if(((PPPlayerList)allowedPlayers.get(i)).getUsername().matches(player))
    		{
    			return false;
    		}
    	}
    	allowedPlayers.add(new PPPlayerList(player));
    	return true;
    }
    
    /**
     * method to remove the playername in the same way as described above.
     * @param player
     * @return
     */
    public boolean removePlayer(String player)
    {
    	for(int i = 0;i < allowedPlayers.size();i++)
    	{
    		if(((PPPlayerList)allowedPlayers.get(i)).getUsername().matches(player))
    		{
    			allowedPlayers.remove(i);
    			return true;
    		}
    	}
    	return false;
    }
    
    /**
     * method to switch a mob to set if the mob can trigger
     * the pressure plate. (Note that this does not directly
     * changes the gui if on a remote world)
     * @param var1		the registered name of the mob
     * @param world		the game world(used to check if it's a server world or not)
     */
    public void switchMob(String var1, World world)
    {	
    	for(int var4 = 0;var4 < allowedMobs.length;var4++)
    	{
    		PPList pp = allowedMobs[var4];
    		if(world.isRemote)
    		{
	    		if(pp.getMobname().matches(var1) && !pp.getEnabled())
	    		{
	    			allowedMobs[var4].enable();
	    			return;
	    		}
	    		if(pp.getMobname().matches(var1) && pp.getEnabled())
	    		{
	    			allowedMobs[var4].disable();
	    			return;
	    		}
    		}
    		else
    		{
    			if(pp.getMobname().matches(var1))
    			{
    				if(!pp.getEnabled())
    				{
    					allowedMobs[var4].enable();
    				}
    				else
    				{
    					allowedMobs[var4].disable();
    				}
    				PacketSendManager.sendSwitchMobButtonPacketToClient(this, var4);	
    				return;
    			}
    		}
    	}
    	return;
    }
    
    /**
     * almost the same method as the one above this one
     * but to switch players.
     * @param var1
     * @param world
     */
    public void switchPlayer(String var1, World world)
    {	
    	for(int var4 = 0;var4 < allowedPlayers.size();var4++)
    	{
    		PPPlayerList pp = (PPPlayerList)allowedPlayers.get(var4);
    		if(pp.getUsername().matches(var1))
    		{
	    		if(!pp.getEnabled())
	    		{
	    			pp.enable();
	    		}
	    		else
	    		{
	    			pp.disable();
	    		}
    			PacketSendManager.sendPressurePlatePlayerDataToClient(this);
    			return;
    		}
    	}
    	return;
    }
    
    /**
     * method to check if a mob is in the list and then
     * if it should be activating the pressure plate or not.
     * @param var1	mob name
     * @return true if it activates
     */
    public boolean findMobName(String var1)
    {
    	for(int var2 = 0;var2 < allowedMobs.length;var2++)
    	{
    		PPList pp = allowedMobs[var2];
    		if(var1 != null)
    		{
    			if(pp.getMobname().matches(var1) && pp.getEnabled())
    			{
    				return true;
    			}	
    		}
    	}
        return false;
    }
    
    /**
     * same as the method above but for players
     * @param username
     * @return
     */
    public boolean isInPlayerList(String username)
    {
    	for(int var1 = 0; var1 < allowedPlayers.size(); var1++)
    	{
    		PPPlayerList pp = (PPPlayerList)allowedPlayers.get(var1);
    		if(username != null && pp != null)
    		{
    			if(pp.getUsername().matches(username) && pp.getEnabled())
    			{
    				return true;
    			}
    		}
    	}
    	return false;
    }
    
    public boolean isPlayerInList(String username)
    {
    	for(int var1 = 0; var1 < allowedPlayers.size(); var1++)
    	{
    		PPPlayerList pp = (PPPlayerList)allowedPlayers.get(var1);
    		if(username != null && pp != null)
    		{
    			if(pp.getUsername().matches(username))
    			{
    				return true;
    			}
    		}
    	}
    	return false;
    }
    
    /**
     * a method to directly enable or disable a player.
     * @param username
     * @param enabled
     */
    public void setEnabledForPlayer(String username, boolean enabled)
    {
    	for(int var1 = 0; var1 < allowedPlayers.size(); var1++)
    	{
    		PPPlayerList pp = (PPPlayerList)allowedPlayers.get(var1);
			if(pp.getUsername().matches(username))
			{
				pp.isEnabled = enabled;
			}
    	}
    }
    
    public void switchSetting(int index)
    {
		if(((SettingsButton)settings.get(index)).enabled)
		{
			((SettingsButton)settings.get(index)).enabled = false;
		}
		else
		{
			((SettingsButton)settings.get(index)).enabled = true;
		}
		if(index == 2)
		{
			PPRegistry.setUsesPassword(this, worldObj.provider.dimensionId, this.getIsEnabled(2));
			PacketSendManager.sendUsesPasswordToClient(this.xCoord, this.yCoord, this.zCoord, this.worldObj.provider.dimensionId, this.getIsEnabled(2));
		}
		PacketSendManager.sendSettingsDataToClient(this);
    }
    
    public void setSetting(int index, boolean b)
    {
		((SettingsButton)settings.get(index)).enabled = b;
    }
    
	public boolean getIsEnabled(int index)
	{	
		return ((SettingsButton)settings.get(index)).enabled;
	}
    
    /**
     * loads the pressure plate data.
     */
    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readFromNBT(par1NBTTagCompound);
        this.registerMobs();
		this.registerSettings();
        try
        {
	        NBTTagList var2 = par1NBTTagCompound.getTagList("Mobs");
	    	for(int var3 = 0; var3 < mobs.size();var3++)
	    	{
	            NBTTagCompound var4 = (NBTTagCompound)var2.tagAt(var3);
	            int var5 = var4.getByte("mob");
	            if (var5 >= 0 && var5 < this.allowedMobs.length)
	            {
	                this.allowedMobs[var5] = PPList.loadSettingsFromNBT(var4, (String)mobs.get(var3));
	            }
	    	}
        }
        catch(Exception e)
        {
        }
        try
        {
	        NBTTagList var6 = par1NBTTagCompound.getTagList("Players");
	    	for(int var7 = 0; var7 < var6.tagCount();var7++)
	    	{
	            NBTTagCompound var8 = (NBTTagCompound)var6.tagAt(var7);
	            int var9 = var8.getByte("player");
	            String var10 = var8.getString("username");
	            boolean var11 = var8.getBoolean("isEnabled");
	            if (var9 >= 0 && var9 < this.allowedMobs.length)
	            {
	                this.allowedPlayers.add(new PPPlayerList(var10, var11));
	            }
	    	}
        } catch(Exception e)
        {
        }
    	try
    	{
	        NBTTagList var11 = par1NBTTagCompound.getTagList("Items");
	
	        for (int var12 = 0; var12 < var11.tagCount(); ++var12)
	        {
	            NBTTagCompound var14 = (NBTTagCompound)var11.tagAt(var12);
	            int var13 = var14.getByte("Slot") & 255;
	
	            if (var13 >= 0 && var13 < this.item.length)
	            {
	                this.item[var13] = ItemStack.loadItemStackFromNBT(var14);
	            }
	        }
    	} catch(Exception e)
    	{
    		FMLLog.log(Level.FINE, e, "no items found, adding...");
    		item = new ItemStack[1];
    	}
    	try
    	{
	        NBTTagList var11 = par1NBTTagCompound.getTagList("Settings");
	        for(int var12 = 0;var12 < var11.tagCount();var12++)
	        {
	        	NBTTagCompound var14 = (NBTTagCompound)var11.tagAt(var12);
	        	int var13 = var14.getByte("setting");
	        	boolean var15 = var14.getBoolean("enabled");
	        	if(var13 >= 0 && var13 < this.pps.buttons.size())
	        	{
	        		this.setSetting(var13, var15);
	        	}
	        }
    	}
    	catch(Exception e)
    	{
    		
    	}
    	try
    	{
    		password = par1NBTTagCompound.getString("password"); 
    	}
    	catch(Exception e)
    	{
    		FMLLog.log(Level.FINE, e, "no password setting found, adding...");
    		password = "";
    	}
    	register = true;
        check = true;
        this.scheduleUpdate(0);
    }

    /**
     * saves the pressure plate data.
     */
    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeToNBT(par1NBTTagCompound);
        NBTTagList var2 = new NBTTagList();
        for (int var3 = 0; var3 < allowedMobs.length; ++var3)
        {
            NBTTagCompound var4 = new NBTTagCompound();
            var4.setByte("mob", (byte)var3);
            this.allowedMobs[var3].writeToNBT(var4);
            var2.appendTag(var4);  
        }
        par1NBTTagCompound.setTag("Mobs", var2);
        NBTTagList var6 = new NBTTagList();
        if(allowedPlayers.size() > 0 && allowedPlayers != null)
        {
	        for (int var5 = 0; var5 < allowedPlayers.size(); var5++)
	        {
	            NBTTagCompound var7 = new NBTTagCompound();
	            var7.setByte("player", (byte)var5);
	            var7.setString("username", ((PPPlayerList)allowedPlayers.get(var5)).getUsername());
	            var7.setBoolean("isEnabled", ((PPPlayerList)allowedPlayers.get(var5)).getEnabled());
	            var6.appendTag(var7); 
	        }
	        par1NBTTagCompound.setTag("Players", var6);
        }
        NBTTagList var8 = new NBTTagList();

        for (int var9 = 0; var9 < this.item.length; ++var9)
        {
            if (this.item[var9] != null)
            {
                NBTTagCompound var10 = new NBTTagCompound();
                var10.setByte("Slot", (byte)var9);
                this.item[var9].writeToNBT(var10);
                var8.appendTag(var10);
            }
        }
        par1NBTTagCompound.setTag("Items", var8);
        NBTTagList var9 = new NBTTagList();
        for (int var3 = 0;var3 < this.pps.buttons.size();var3++)
        {
            NBTTagCompound var4 = new NBTTagCompound();
            var4.setByte("setting", (byte)var3);
            var4.setBoolean("enabled", this.getIsEnabled(var3));
            var9.appendTag(var4);
        }
        par1NBTTagCompound.setTag("Settings", var9);
        par1NBTTagCompound.setString("password", password);
    }

	@Override
	public int getSizeInventory() 
	{
		return item.length;
	}

	@Override
	public ItemStack getStackInSlot(int par1) 
	{
		return item[par1];
	}

	@Override
	public ItemStack decrStackSize(int par1, int par2) 
	{
		ItemStack stack = this.getStackInSlot(par1);
		if(stack != null)
		{
			if(stack.stackSize <= par2)
			{
				setInventorySlotContents(par1, null);
			}
			else
			{
				stack = stack.splitStack(par2);
				if(stack.stackSize == 0)
				{
					setInventorySlotContents(par1, null);
				}
			}
		}
		return stack;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int par1) 
	{
		ItemStack stack = getStackInSlot(par1);
		if(stack != null)
		{
			setInventorySlotContents(par1, null);
		}
		return stack;
	}

	@Override
	public void setInventorySlotContents(int par1, ItemStack par2ItemStack) 
	{
        this.item[par1] = par2ItemStack;

        if (par2ItemStack != null && par2ItemStack.stackSize > this.getInventoryStackLimit())
        {
            par2ItemStack.stackSize = this.getInventoryStackLimit();
        }
        PPRegistry.setItem(this, this.worldObj.provider.dimensionId, this.item[0]);
        worldObj.markBlockAsNeedsUpdate(xCoord, yCoord, zCoord);
	}

	@Override
	public String getInvName() 
	{
		return "Advanced Pressure Plate";
	}

	@Override
	public int getInventoryStackLimit() 
	{
		return 1;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer par1EntityPlayer) 
	{
		return this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord, this.zCoord) != this ? false : par1EntityPlayer.getDistanceSq((double)this.xCoord + 0.5D, (double)this.yCoord + 0.5D, (double)this.zCoord + 0.5D) <= 64.0D;
	}

	@Override
	public void openChest() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void closeChest() {
		// TODO Auto-generated method stub
		
	}
    
    

}
