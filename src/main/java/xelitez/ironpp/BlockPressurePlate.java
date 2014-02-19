/**
 * some sort of a dummy class to extend the BlockPressurePlate class
 *
 * @author Kalvin
 */
package xelitez.ironpp;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;

public class BlockPressurePlate extends net.minecraft.block.BlockPressurePlate
{
    protected BlockPressurePlate(String par1,
            Sensitivity par2EnumSensitivity, Material par3Material)
    {
    	super(par1, par3Material, par2EnumSensitivity);
    }
    
    @Override
    public int getRenderType()
    {
        return FMLCommonHandler.instance().getSide().isClient() ? IronPP.ppRenderer.getRenderId() : 0;
    }
    
    @Override
    public void registerBlockIcons(IIconRegister par1IconRegister)
    {
        this.blockIcon = Block.getBlockFromName("iron_block").getBlockTextureFromSide(0);
    }
    
}
