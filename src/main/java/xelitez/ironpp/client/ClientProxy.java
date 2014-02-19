package xelitez.ironpp.client;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import xelitez.ironpp.CommonProxy;
import xelitez.ironpp.TileEntityPressurePlate;
import cpw.mods.fml.client.FMLClientHandler;

public class ClientProxy extends CommonProxy
{
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world,
            int x, int y, int z)
    {
        TileEntity te = world.getTileEntity(x, y, z);

        switch (ID)
        {
            case 0:
                if (te != null && te instanceof TileEntityPressurePlate)
                {
                    return new GuiAPressurePlate((TileEntityPressurePlate)te);
                }
                else
                {
                    return null;
                }

            case 1:
                if (te != null && te instanceof TileEntityPressurePlate)
                {
                    return new GuiPassword((TileEntityPressurePlate)te, false, x, y, z);
                }
                else
                {
                    return null;
                }

            default:
                return null;
        }
    }

    @Override
    public World getClientWorld()
    {
        return FMLClientHandler.instance().getClient().theWorld;
    }
}
