/**
 * some sort of a dummy class to extend the BlockPressurePlate class
 *
 * @author Kalvin
 */
package xelitez.ironpp;

import net.minecraft.block.EnumMobType;
import net.minecraft.block.material.Material;

public class BlockPressurePlate extends net.minecraft.block.BlockPressurePlate
{
    protected BlockPressurePlate(int par1, int par2,
            EnumMobType par3EnumMobType, Material par4Material)
    {
        super(par1, par2, par3EnumMobType, par4Material);
        this.setRequiresSelfNotify();
    }
}
