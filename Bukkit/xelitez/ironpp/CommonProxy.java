package xelitez.ironpp;

import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.server.v1_4_6.EntityHuman;
import net.minecraft.server.v1_4_6.TileEntity;
import net.minecraft.server.v1_4_6.World;

public class CommonProxy implements IGuiHandler
{
    public Object getServerGuiElement(int var1, EntityHuman var2, World var3, int var4, int var5, int var6)
    {
        TileEntity var7 = var3.getTileEntity(var4, var5, var6);
        TileEntityPressurePlate var8;

        switch (var1)
        {
            case 0:
                if (var7 != null && var7 instanceof TileEntityPressurePlate)
                {
                    var8 = (TileEntityPressurePlate)var7;
                    return new ContainerPressurePlate(var8, var2.inventory);
                }

                return null;

            default:
                if (var7 != null && var7 instanceof TileEntityPressurePlate)
                {
                    var8 = (TileEntityPressurePlate)var7;
                    return new ContainerPressurePlate(var8, var2.inventory);
                }
                else
                {
                    return null;
                }
        }
    }

    public Object getClientGuiElement(int var1, EntityHuman var2, World var3, int var4, int var5, int var6)
    {
        return null;
    }

    public World getClientWorld()
    {
        return null;
    }

    public void RegisterKeyHandler() {}

    public String getKey(int var1)
    {
        return "";
    }
}
