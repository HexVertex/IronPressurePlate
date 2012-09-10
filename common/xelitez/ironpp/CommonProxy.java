/**
 * the proxy used by this mod
 * 
 * @author Kalvin
 */
package xelitez.ironpp;

import xelitez.ironpp.client.GuiAPressurePlate;
import xelitez.ironpp.ContainerPressurePlate;
import net.minecraft.src.Container;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;
import cpw.mods.fml.common.network.IGuiHandler;

public class CommonProxy implements IGuiHandler
{
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if(te != null && te instanceof TileEntityPressurePlate)
		{
			TileEntityPressurePlate tpp = (TileEntityPressurePlate) te;
			return new ContainerPressurePlate(tpp, player.inventory);
		}
		else
		{
			return null;
		}
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) 
	{
		return null;
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
