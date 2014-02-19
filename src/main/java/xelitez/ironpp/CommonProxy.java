/**
 * the proxy used by this mod
 *
 * @author Kalvin
 */
package xelitez.ironpp;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;

public class CommonProxy implements IGuiHandler
{
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world,
            int x, int y, int z)
    {
        TileEntity te = world.getTileEntity(x, y, z);

        switch (ID)
        {
            case 0:
                if (te != null && te instanceof TileEntityPressurePlate)
                {
                    TileEntityPressurePlate tpp = (TileEntityPressurePlate) te;
                    return new ContainerPressurePlate(tpp, player.inventory);
                }
                else
                {
                    return null;
                }

            default:
                if (te != null && te instanceof TileEntityPressurePlate)
                {
                    TileEntityPressurePlate tpp = (TileEntityPressurePlate) te;
                    return new ContainerPressurePlate(tpp, player.inventory);
                }
                else
                {
                    return null;
                }
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
