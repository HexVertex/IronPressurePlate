package xelitez.ironpp.client;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;

import org.lwjgl.opengl.GL11;

import xelitez.ironpp.BlockAPressurePlate;
import xelitez.ironpp.BlockPressurePlate;
import xelitez.ironpp.PPRegistry;
import xelitez.ironpp.TileEntityPressurePlate;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class PPRenderer implements ISimpleBlockRenderingHandler
{
	int id;
	
	public PPRenderer(int id)
	{
		this.id = id;
	}
	
	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID,
			RenderBlocks renderer) 
	{
        Tessellator var4 = Tessellator.instance;
        block.setBlockBoundsForItemRender();
        renderer.setRenderBoundsFromBlock(block);
        GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
		double height = 0.1875;
		double edge = 0.0D;
        GL11.glTranslatef(0.0F, 0.4F, 0.0F);
		renderer.setOverrideBlockTexture(Blocks.gold_block.getBlockTextureFromSide(0));
		renderer.setRenderBounds(edge, 0.0D, edge, 1.0D - edge, height, 0.1875D);
		renderAllFaces(var4, renderer, block, metadata);
		renderer.setRenderBounds(edge, 0.0D, 1.0D - 0.1875D, 1.0D - edge, height, 1.0D - edge);
		renderAllFaces(var4, renderer, block, metadata);
		renderer.setRenderBounds(edge, 0.0D, 0.1875D, 0.1875D, height, 1.0D - 0.1875D);
		renderAllFaces(var4, renderer, block, metadata);
		renderer.setRenderBounds(1.0D - 0.1875D, 0.0D, 0.1875D, 1.0D - edge, height, 1.0D - 0.1875D);
		renderAllFaces(var4, renderer, block, metadata);
		renderer.setOverrideBlockTexture(Blocks.iron_block.getBlockTextureFromSide(0));
		renderer.setRenderBounds(0.1875D, 0.0D, 0.1875D, 1.0D - 0.1875D, height, 1.0D - 0.1875D);
		renderAllFaces(var4, renderer, block, metadata);
		if(block instanceof BlockAPressurePlate)
		{
			renderer.setOverrideBlockTexture(Blocks.diamond_block.getBlockTextureFromSide(0));
	        GL11.glPushMatrix();
	        GL11.glDisable(GL11.GL_LIGHTING);
	        GL11.glTranslatef(0.5f, MathHelper.sin((float)((Math.PI / 180) * ((System.currentTimeMillis() / 40) % 180))) * 0.15f, 0.5F);
	        GL11.glRotatef(((System.currentTimeMillis() / 5) % 360) * 1f, 0.0F, 1.0F, 0.0F);
			var4.startDrawingQuads();
			var4.addVertexWithUV(-0.2D, 0.24D, -0.2D, renderer.overrideBlockTexture.getMinU(), renderer.overrideBlockTexture.getMinV());
			var4.addVertexWithUV(0.2D, 0.24D, -0.2D, renderer.overrideBlockTexture.getMaxU(), renderer.overrideBlockTexture.getMinV());
			var4.addVertexWithUV(0.2D, 0.24D, 0.2D, renderer.overrideBlockTexture.getMaxU(), renderer.overrideBlockTexture.getMaxV());
			var4.addVertexWithUV(-0.2D, 0.24D, 0.2D, renderer.overrideBlockTexture.getMinU(), renderer.overrideBlockTexture.getMaxV());
			var4.draw();
			var4.startDrawingQuads();
			var4.addVertexWithUV(0.2D, 0.24D, 0.2D, renderer.overrideBlockTexture.getMinU(), renderer.overrideBlockTexture.getMaxV());
			var4.addVertexWithUV(0.2D, 0.24D, -0.2D, renderer.overrideBlockTexture.getMinU(), renderer.overrideBlockTexture.getMinV());
			var4.addVertexWithUV(-0.2D, 0.24D, -0.2D, renderer.overrideBlockTexture.getMaxU(), renderer.overrideBlockTexture.getMinV());
			var4.addVertexWithUV(-0.2D, 0.24D, 0.2D, renderer.overrideBlockTexture.getMaxU(), renderer.overrideBlockTexture.getMaxV());
			var4.draw();
	        GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glPopMatrix();
		}
        renderer.clearOverrideBlockTexture();
	}
	
	private void renderAllFaces(Tessellator var4, RenderBlocks renderer, Block block, int metadata)
	{
        boolean var5 = block == Blocks.grass;
        int var14;
        float var8;
        float var9;
        float par3 = 1.0F;
        var4.startDrawingQuads();
        var4.setNormal(0.0F, -1.0F, 0.0F);
        renderer.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(0, metadata));
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
        renderer.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(1, metadata));
        var4.draw();

        if (var5 && renderer.useInventoryTint)
        {
            GL11.glColor4f(par3, par3, par3, 1.0F);
        }

        var4.startDrawingQuads();
        var4.setNormal(0.0F, 0.0F, -1.0F);
        renderer.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(2, metadata));
        var4.draw();
        var4.startDrawingQuads();
        var4.setNormal(0.0F, 0.0F, 1.0F);
        renderer.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(3, metadata));
        var4.draw();
        var4.startDrawingQuads();
        var4.setNormal(-1.0F, 0.0F, 0.0F);
        renderer.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(4, metadata));
        var4.draw();
        var4.startDrawingQuads();
        var4.setNormal(1.0F, 0.0F, 0.0F);
        renderer.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(5, metadata));
        var4.draw();
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z,
			Block block, int modelId, RenderBlocks renderer) 
	{
		Tessellator tes = Tessellator.instance;
		double cx = x + 0.5D;
		double cy = y + 0.12D;
		double cz = z + 0.5D;

		renderer.uvRotateEast = 3;
		renderer.uvRotateWest = 3;
		renderer.uvRotateNorth = 3;
		renderer.uvRotateSouth = 3;
		boolean activated;
		if(block instanceof BlockAPressurePlate)
		{
			activated = ((TileEntityPressurePlate)world.getTileEntity(x, y, z)).currentOutput > 0;
		}
		else
		{
			activated = world.getBlockMetadata(x, y, z) == 0 ? false : true;
		}
		double height = !activated ? 0.0625D : 0.03125D;
		double edge = 0.0625D;
		if((block instanceof BlockAPressurePlate && (PPRegistry.getItem((TileEntityPressurePlate)world.getTileEntity(x, y, z), FMLClientHandler.instance().getClient().thePlayer.worldObj.provider.dimensionId) == null) || block instanceof BlockPressurePlate))
		{
			renderer.setOverrideBlockTexture(Blocks.gold_block.getBlockTextureFromSide(0));
			renderer.setRenderBounds(edge, 0.0D, edge, 1.0D - edge, height, 0.1875D);
			renderer.renderStandardBlock(block, x, y, z);
			renderer.setRenderBounds(edge, 0.0D, 1.0D - 0.1875D, 1.0D - edge, height, 1.0D - edge);
			renderer.renderStandardBlock(block, x, y, z);
			renderer.setRenderBounds(edge, 0.0D, 0.1875D, 0.1875D, height, 1.0D - 0.1875D);
			renderer.renderStandardBlock(block, x, y, z);
			renderer.setRenderBounds(1.0D - 0.1875D, 0.0D, 0.1875D, 1.0D - edge, height, 1.0D - 0.1875D);
			renderer.renderStandardBlock(block, x, y, z);
			renderer.setOverrideBlockTexture(Blocks.iron_block.getBlockTextureFromSide(0));
			renderer.setRenderBounds(0.1875D, 0.0D, 0.1875D, 1.0D - 0.1875D, height, 1.0D - 0.1875D);
			renderer.renderStandardBlock(block, x, y, z);
			if(block instanceof BlockAPressurePlate)
			{
				tes.draw();
				GL11.glPushMatrix();
				tes.startDrawingQuads();
				GL11.glTranslatef((float)cx, (float)cy, (float)cz);
				renderer.setOverrideBlockTexture(Blocks.diamond_block.getBlockTextureFromSide(0));
				tes.addVertexWithUV(-0.1D, 0.0D, 0.1D, renderer.overrideBlockTexture.getMinU(), renderer.overrideBlockTexture.getMinV());
				tes.addVertexWithUV(0.1D, 0.0D, 0.1D, renderer.overrideBlockTexture.getMaxU(), renderer.overrideBlockTexture.getMinV());
				tes.addVertexWithUV(0.1D, 0.0D, -0.1D, renderer.overrideBlockTexture.getMaxU(), renderer.overrideBlockTexture.getMaxV());
				tes.addVertexWithUV(-0.1D, 0.0D, -0.1D, renderer.overrideBlockTexture.getMinU(), renderer.overrideBlockTexture.getMaxV());
				tes.draw();
				tes.startDrawingQuads();
				GL11.glTranslatef(0.0F, 0.001F, 0.0F);
				tes.addVertexWithUV(0.1D, 0.0D, 0.1D, renderer.overrideBlockTexture.getMinU(), renderer.overrideBlockTexture.getMaxV());
				tes.addVertexWithUV(0.1D, 0.0D, -0.1D, renderer.overrideBlockTexture.getMinU(), renderer.overrideBlockTexture.getMinV());
				tes.addVertexWithUV(-0.1D, 0.0D, -0.1D, renderer.overrideBlockTexture.getMaxU(), renderer.overrideBlockTexture.getMinV());
				tes.addVertexWithUV(-0.1D, 0.0D, 0.1D, renderer.overrideBlockTexture.getMaxU(), renderer.overrideBlockTexture.getMaxV());
				tes.draw();
				GL11.glPopMatrix();
				tes.startDrawingQuads();
			}
		}
		else
		{
			renderer.renderStandardBlock(block, x, y, z);
		}
		renderer.uvRotateEast = 0;
		renderer.uvRotateWest = 0;
		renderer.uvRotateNorth = 0;
		renderer.uvRotateSouth = 0;
		renderer.clearOverrideBlockTexture();
		renderer.setRenderBounds(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
		return true;
	}

	@Override
	public int getRenderId() {
		// TODO Auto-generated method stub
		return id;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		// TODO Auto-generated method stub
		return true;
	}

}
