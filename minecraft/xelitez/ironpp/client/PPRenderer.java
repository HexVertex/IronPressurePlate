package xelitez.ironpp.client;

import org.lwjgl.opengl.GL11;

import xelitez.ironpp.IronPP;
import xelitez.ironpp.PPRegistry;
import xelitez.ironpp.TileEntityPressurePlate;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class PPRenderer implements ISimpleBlockRenderingHandler{

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID,
			RenderBlocks renderer) 
	{
        Tessellator var4 = Tessellator.instance;
        boolean var5 = block.blockID == Block.grass.blockID;
        int var14;
        float var8;
        float var9;
        float par3 = 1.0F;
        block.setBlockBoundsForItemRender();
        renderer.updateCustomBlockBounds(block);
        GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        var4.startDrawingQuads();
        var4.setNormal(0.0F, -1.0F, 0.0F);
        renderer.renderBottomFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(0, metadata));
        var4.draw();

        if (var5 && renderer.useInventoryTint)
        {
            var14 = block.getRenderColor(metadata);
            var8 = (float)(var14 >> 16 & 255) / 255.0F;
            var9 = (float)(var14 >> 8 & 255) / 255.0F;
            float var10 = (float)(var14 & 255) / 255.0F;
            GL11.glColor4f(var8 * par3, var9 * par3, var10 * par3, 1.0F);
        }

        var4.startDrawingQuads();
        var4.setNormal(0.0F, 1.0F, 0.0F);
        renderer.renderTopFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(1, metadata));
        var4.draw();

        if (var5 && renderer.useInventoryTint)
        {
            GL11.glColor4f(par3, par3, par3, 1.0F);
        }

        var4.startDrawingQuads();
        var4.setNormal(0.0F, 0.0F, -1.0F);
        renderer.renderEastFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(2, metadata));
        var4.draw();
        var4.startDrawingQuads();
        var4.setNormal(0.0F, 0.0F, 1.0F);
        renderer.renderWestFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(3, metadata));
        var4.draw();
        var4.startDrawingQuads();
        var4.setNormal(-1.0F, 0.0F, 0.0F);
        renderer.renderNorthFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(4, metadata));
        var4.draw();
        var4.startDrawingQuads();
        var4.setNormal(1.0F, 0.0F, 0.0F);
        renderer.renderSouthFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(5, metadata));
        var4.draw();
        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z,
			Block block, int modelId, RenderBlocks renderer) 
	{
		TileEntityPressurePlate tpp = (TileEntityPressurePlate)world.getBlockTileEntity(x, y, z);
		ItemStack item;
		int var5 = 16777215;
		if(PPRegistry.getItem(x, y, z, tpp.worldObj.provider.dimensionId) != null)
		{
			item = PPRegistry.getItem(x, y, z, tpp.worldObj.provider.dimensionId);
			ForgeHooksClient.bindTexture(Block.blocksList[PPRegistry.getItem(x, y, z, tpp.worldObj.provider.dimensionId).itemID].getTextureFile(), 0);
			var5 = Block.blocksList[PPRegistry.getItem(x, y, z, tpp.worldObj.provider.dimensionId).itemID].colorMultiplier(world, x, y, z);
			block = Block.blocksList[PPRegistry.getItem(x, y, z, tpp.worldObj.provider.dimensionId).itemID];
		}
		renderer.uvRotateEast = 3;
		renderer.uvRotateWest = 3;
		renderer.uvRotateNorth = 3;
		renderer.uvRotateSouth = 3;
		renderer.renderStandardBlock(block, x, y, z);
		renderer.uvRotateEast = 0;
		renderer.uvRotateWest = 0;
		renderer.uvRotateNorth = 0;
		renderer.uvRotateSouth = 0;
		return true;
	}

	@Override
	public boolean shouldRender3DInInventory() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public int getRenderId() {
		// TODO Auto-generated method stub
		return 2151;
	}

}
