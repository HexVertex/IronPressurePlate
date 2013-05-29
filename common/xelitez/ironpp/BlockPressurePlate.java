/**
 * some sort of a dummy class to extend the BlockPressurePlate class
 *
 * @author Kalvin
 */
package xelitez.ironpp;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.block.Block;
import net.minecraft.block.EnumMobType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;

public class BlockPressurePlate extends net.minecraft.block.BlockPressurePlate
{
    protected BlockPressurePlate(int par1, String par2,
            EnumMobType par3EnumMobType, Material par4Material)
    {
    	super(par1, par2, par4Material, par3EnumMobType);
    }
    
    @Override
    public int getRenderType()
    {
        return FMLCommonHandler.instance().getSide().isClient() ? IronPP.ppRenderer.getRenderId() : 0;
    }
    
    public void registerIcons(IconRegister par1IconRegister)
    {
        this.blockIcon = Block.blockIron.getBlockTextureFromSide(0);
    }
    
}
