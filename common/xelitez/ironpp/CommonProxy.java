/**
 * the proxy used by this mod
 * 
 * @author Kalvin
 */
package xelitez.ironpp;

import xelitez.ironpp.client.GuiAPressurePlate;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;
import cpw.mods.fml.common.network.IGuiHandler;

public class CommonProxy implements IGuiHandler
{

	public Object getServerGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if(te != null && te instanceof TileEntityPressurePlate)
		{
			return new ContainerPressurePlate((TileEntityPressurePlate)te, player.inventory);
		}
		else
		{
			return null;
		}
	}

	public Object getClientGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) 
	{
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if(te != null && te instanceof TileEntityPressurePlate)
		{
			return new GuiAPressurePlate((TileEntityPressurePlate)te);
		}
		else
		{
			return null;
		}
	}
	
	public World getClientWorld()
	{
		return null;
	}
	
	public void RegisterKeyHandler()
	{
		
	}
	
	public String getKey(int i)
	{
		return "";
	}

}
