/**
 * this is some sort of dummy class to be able to
 * save data and work with Guis
 * 
 * @author Kalvin
 */
package xelitez.ironpp;

import net.minecraft.src.Container;
import net.minecraft.src.EntityPlayer;

public class ContainerPressurePlate extends Container
{
	public TileEntityPressurePlate tpp;
	
	public ContainerPressurePlate(TileEntityPressurePlate tpp)
	{
		this.tpp = tpp;
	}

	public boolean canInteractWith(EntityPlayer var1) 
	{
		return true;
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