/**
 * the proxy used by this mod
 * 
 * @author Kalvin
 */
package xelitez.ironpp;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import cpw.mods.fml.common.network.IGuiHandler;

public class CommonProxy implements IGuiHandler
{

	public Object getServerGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if(te != null && te instanceof TileEntityPressurePlate)
		{
			return new ContainerPressurePlate((TileEntityPressurePlate)te);
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

}