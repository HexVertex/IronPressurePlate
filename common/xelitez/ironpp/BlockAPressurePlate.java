/**
 * This is the class of the Advanced Iron Pressure Plate.
 * Most of this is copied from the BlockPressurePlate.java so I will only explain the changes.
 * 
 * @author Kalvin
 */

package xelitez.ironpp;

import ibxm.Player;

import java.util.List;
import java.util.Random;

import xelitez.ironpp.client.GuiAPressurePlate;
import xelitez.ironpp.client.GuiModifyPressurePlate;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import cpw.mods.fml.server.FMLServerHandler;

import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.Block;
import net.minecraft.src.BlockContainer;
import net.minecraft.src.BlockFence;
import net.minecraft.src.ChunkCache;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityItem;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EnumMobType;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.ItemBlock;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Material;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.PPManager;
import net.minecraft.src.TileEntity;
import net.minecraft.src.TileEntityChest;
import net.minecraft.src.World;
import net.minecraft.src.Entity;

public class BlockAPressurePlate extends BlockContainer
{
    private EnumMobType triggerMobType;
    
    private Random random = new Random();

	protected BlockAPressurePlate(int par1, int par2, Material par3Material) {
		super(par1, par2, par3Material);
		this.setRequiresSelfNotify();
        this.triggerMobType = EnumMobType.everything;
        this.setCreativeTab(CreativeTabs.tabRedstone);
        this.setTickRandomly(true);
        float var5 = 0.0625F;
        this.setBlockBounds(var5, 0.0F, var5, 1.0F - var5, 0.03125F, 1.0F - var5);
        this.blockIndexInTexture = par2;
	}
	
	public int tickRate()
    {
        return 20;
    }

    public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4)
    {
        return null;
    }

    public boolean isOpaqueCube()
    {
        return false;
    }

    public boolean renderAsNormalBlock()
    {
        return false;
    }

    public boolean getBlocksMovement(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
    {
        return true;
    }

	@Override
	public TileEntity createNewTileEntity(World var1) 
	{
		return new TileEntityPressurePlate();
	}
	
    public boolean canPlaceBlockAt(World par1World, int par2, int par3, int par4)
    {
        return par1World.doesBlockHaveSolidTopSurface(par2, par3 - 1, par4) || BlockFence.isIdAFence(par1World.getBlockId(par2, par3 - 1, par4));
    }

    public void onBlockPlacedBy(World par1World, int par2, int par3, int par4, EntityLiving par5EntityLiving) 
    {
    	if(par5EntityLiving instanceof EntityPlayer && par1World.getBlockTileEntity(par2, par3, par4) != null && par1World.getBlockTileEntity(par2, par3, par4) instanceof TileEntityPressurePlate)
    	{
    		TileEntityPressurePlate tpp = (TileEntityPressurePlate)par1World.getBlockTileEntity(par2, par3, par4);
    		tpp.registerPlayer(((EntityPlayer)par5EntityLiving).username);
            if(!par1World.isRemote || FMLCommonHandler.instance().getSide().isServer())
            {
            	PacketSendManager.sendAddPressurePlateToClient(tpp, tpp.worldObj.provider.worldType);
            	PacketSendManager.sendUsesPasswordToClient(par2, par3, par4, par1World.provider.worldType, tpp.getIsEnabled(2));
            	if(tpp.item[0] != null)
            	{
            		PacketSendManager.sendItemStackToClients(par2, par3, par4, tpp.item[0].itemID, tpp.item[0].getItemDamage(), tpp.item[0].stackSize, par1World.provider.worldType);
            	}
            	else
            	{
            		PacketSendManager.sendItemStackToClients(par2, par3, par4, 0, 0, 0, par1World.provider.worldType);
            	}
            }
    	}
    }
    
    public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, int par5)
    {
        boolean var6 = false;

        if (!par1World.doesBlockHaveSolidTopSurface(par2, par3 - 1, par4) && !BlockFence.isIdAFence(par1World.getBlockId(par2, par3 - 1, par4)))
        {
            var6 = true;
        }

        if (var6)
        {
            this.dropBlockAsItem(par1World, par2, par3, par4, par1World.getBlockMetadata(par2, par3, par4), 0);
            par1World.setBlockWithNotify(par2, par3, par4, 0);
        }
    }
    
    public void updateTick(World par1World, int par2, int par3, int par4, Random par5Random)
    {
        if (!par1World.isRemote)
        {
            if (((TileEntityPressurePlate)par1World.getBlockTileEntity(par2, par3, par4)).activated)
            {
                this.setStateIfMobInteractsWithPlate(par1World, par2, par3, par4);
            }
        }
    }
    
    public void onEntityCollidedWithBlock(World par1World, int par2, int par3, int par4, Entity par5Entity)
    {
        if (!par1World.isRemote)
        {
            if (!((TileEntityPressurePlate)par1World.getBlockTileEntity(par2, par3, par4)).activated)
            {
                this.setStateIfMobInteractsWithPlate(par1World, par2, par3, par4);
            }
        }
    }
    
    /**
     * this method checks if something is on the pressure plate and then checks
     * with the TileEntityPressurePate class if it should be turned on.
     * @param par1World World
     * @param par2  	x coordinate
     * @param par3		y coordinate
     * @param par4		z coordinate
     */
    private void setStateIfMobInteractsWithPlate(World par1World, int par2, int par3, int par4)
    {
        boolean var5 = ((TileEntityPressurePlate)par1World.getBlockTileEntity(par2, par3, par4)).activated;
        boolean var6 = false;
        float var7 = 0.125F;
        List var8 = null;

        if (this.triggerMobType == EnumMobType.everything)
        {
            var8 = par1World.getEntitiesWithinAABBExcludingEntity((Entity)null, AxisAlignedBB.getAABBPool().addOrModifyAABBInPool((double)((float)par2 + var7), (double)par3, (double)((float)par4 + var7), (double)((float)(par2 + 1) - var7), (double)par3 + 0.25D, (double)((float)(par4 + 1) - var7)));
        }

        if (this.triggerMobType == EnumMobType.mobs)
        {
            var8 = par1World.getEntitiesWithinAABB(EntityLiving.class, AxisAlignedBB.getAABBPool().addOrModifyAABBInPool((double)((float)par2 + var7), (double)par3, (double)((float)par4 + var7), (double)((float)(par2 + 1) - var7), (double)par3 + 0.25D, (double)((float)(par4 + 1) - var7)));
        }

        if (this.triggerMobType == EnumMobType.players)
        {
            var8 = par1World.getEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.getAABBPool().addOrModifyAABBInPool((double)((float)par2 + var7), (double)par3, (double)((float)par4 + var7), (double)((float)(par2 + 1) - var7), (double)par3 + 0.25D, (double)((float)(par4 + 1) - var7)));
        }
        if(par1World.getBlockTileEntity(par2, par3, par4) instanceof TileEntityPressurePlate)
        {
	        for(int var10 = 0;var10 < var8.size();var10++)
	        {
	        	if (var8.size() > 0 && (((TileEntityPressurePlate)par1World.getBlockTileEntity(par2, par3, par4)).findMobName(PPManager.getEntityString((Entity)var8.get(var10)))))
	        	{
	        		var6 = true;
	        	}
	        
	        	if (var8.size() > 0);
	        	{
	        		if((var8.get(var10) instanceof EntityPlayer))
	        		{
	        			if(((TileEntityPressurePlate)par1World.getBlockTileEntity(par2, par3, par4)).findMobName(PPManager.getEntityType((EntityLiving)var8.get(var10))))
	        			{
	        				if(((TileEntityPressurePlate)par1World.getBlockTileEntity(par2, par3, par4)).isPlayerInList(((EntityPlayer)var8.get(var10)).username))
	        				{
	        					if(((TileEntityPressurePlate)par1World.getBlockTileEntity(par2, par3, par4)).isInPlayerList(((EntityPlayer)var8.get(var10)).username))
	        					{
	        						var6 = true;
	        					}
	        				}
        					else
        					{
        						if( ((TileEntityPressurePlate)par1World.getBlockTileEntity(par2, par3, par4)).getIsEnabled(0))
        						{
        							var6 = true;
        						}
        					}
	        			}
	        		}
	        	}
	        }
        }
        if(par1World.getBlockTileEntity(par2, par3, par4) instanceof TileEntityPressurePlate)
        {
        	TileEntityPressurePlate tpp = (TileEntityPressurePlate)par1World.getBlockTileEntity(par2, par3, par4);
	        if (var6 && !var5)
	        {
	            tpp.setActivated(true, par1World, par2, par3, par4);
	            par1World.notifyBlocksOfNeighborChange(par2, par3, par4, this.blockID);
	            par1World.notifyBlocksOfNeighborChange(par2, par3 - 1, par4, this.blockID);
	            par1World.markBlocksDirty(par2, par3, par4, par2, par3, par4);
	            if(((TileEntityPressurePlate)par1World.getBlockTileEntity(par2, par3, par4)).getIsEnabled(1))
	            {
	            	par1World.playSoundEffect((double)par2 + 0.5D, (double)par3 + 0.1D, (double)par4 + 0.5D, "random.click", 0.3F, 0.6F);
	            }
	        }
	
	        if (!var6 && var5)
	        {
	            tpp.setActivated(false, par1World, par2, par3, par4);
	            par1World.notifyBlocksOfNeighborChange(par2, par3, par4, this.blockID);
	            par1World.notifyBlocksOfNeighborChange(par2, par3 - 1, par4, this.blockID);
	            par1World.markBlocksDirty(par2, par3, par4, par2, par3, par4);
	            if(((TileEntityPressurePlate)par1World.getBlockTileEntity(par2, par3, par4)).getIsEnabled(1))
	            {
	            	par1World.playSoundEffect((double)par2 + 0.5D, (double)par3 + 0.1D, (double)par4 + 0.5D, "random.click", 0.3F, 0.5F);
	            }
	        }
        }

        if (var6)
        {
            par1World.scheduleBlockUpdate(par2, par3, par4, this.blockID, this.tickRate());
        }
    }
    
    /**
     * sets what happens when the block gets destroyed.
     * in this case closes the Gui and removes the block's TileEntity.
     * @param par1World	the world
     * @param par2		x coordinate
     * @param par3		y coordinate
     * @param par4		z coordinate
     * @param par5		I think this is the block ID
     * @param par6		I think this is the Block Metadata
     */
    public void breakBlock(World par1World, int par2, int par3, int par4, int par5, int par6)
    {
        if (par6 > 0)
        {
            par1World.notifyBlocksOfNeighborChange(par2, par3, par4, this.blockID);
            par1World.notifyBlocksOfNeighborChange(par2, par3 - 1, par4, this.blockID);
        }
        PacketSendManager.sendCloseGuiPacketToAllPlayers(((TileEntityPressurePlate)par1World.getBlockTileEntity(par2, par3, par4)));
        TileEntityPressurePlate var7 = (TileEntityPressurePlate)par1World.getBlockTileEntity(par2, par3, par4);

        if (var7 != null)
        {
            for (int var8 = 0; var8 < var7.getSizeInventory(); ++var8)
            {
                ItemStack var9 = var7.getStackInSlot(var8);

                if (var9 != null)
                {
                    float var10 = this.random.nextFloat() * 0.8F + 0.1F;
                    float var11 = this.random.nextFloat() * 0.8F;
                    EntityItem var14;

                    for (float var12 = this.random.nextFloat() * 0.8F + 0.1F; var9.stackSize > 0; par1World.spawnEntityInWorld(var14))
                    {
                        int var13 = this.random.nextInt(21) + 10;

                        if (var13 > var9.stackSize)
                        {
                            var13 = var9.stackSize;
                        }

                        var9.stackSize -= var13;
                        var14 = new EntityItem(par1World, (double)((float)par2 + var10), (double)((float)par3 + var11), (double)((float)par4 + var12), new ItemStack(var9.itemID, var13, var9.getItemDamage()));
                        float var15 = 0.05F;
                        var14.motionX = (double)((float)this.random.nextGaussian() * var15);
                        var14.motionY = (double)((float)this.random.nextGaussian() * var15 + 0.1F);
                        var14.motionZ = (double)((float)this.random.nextGaussian() * var15);

                        if (var9.hasTagCompound())
                        {
                            var14.item.setTagCompound((NBTTagCompound)var9.getTagCompound().copy());
                        }
                    }
                }
            }
            PPRegistry.removePressurePlate(var7, var7.worldObj.provider.worldType);
            if(!par1World.isRemote || FMLCommonHandler.instance().getSide().isServer())
            {
            	PacketSendManager.sendRemovePressurePlateToClient(var7, var7.worldObj.provider.worldType);
            }
        }

        super.breakBlock(par1World, par2, par3, par4, par5, par6);
        par1World.removeBlockTileEntity(par2, par3, par4);
    }
    
    public void setBlockBoundsBasedOnState(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
    {
        boolean var5 = ((TileEntityPressurePlate)par1IBlockAccess.getBlockTileEntity(par2, par3, par4)).activated;
        float var6 = 0.0625F;

        if (var5)
        {
            this.setBlockBounds(var6, 0.0F, var6, 1.0F - var6, 0.03125F, 1.0F - var6);
        }
        else
        {
            this.setBlockBounds(var6, 0.0F, var6, 1.0F - var6, 0.0625F, 1.0F - var6);
        }
    }
    
    public boolean isPoweringTo(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
    {
        return ((TileEntityPressurePlate)par1IBlockAccess.getBlockTileEntity(par2, par3, par4)).activated;
    }
    
    public boolean isIndirectlyPoweringTo(World par1World, int par2, int par3, int par4, int par5)
    {
        return !(((TileEntityPressurePlate)par1World.getBlockTileEntity(par2, par3, par4)).activated) ? false : par5 == 1;
    }
    
    public boolean canProvidePower()
    {
        return true;
    }
    
    public void setBlockBoundsForItemRender()
    {
        float var1 = 0.5F;
        float var2 = 0.125F;
        float var3 = 0.5F;
        this.setBlockBounds(0.5F - var1, 0.5F - var2, 0.5F - var3, 0.5F + var1, 0.5F + var2, 0.5F + var3);
    }
    
    public int getMobilityFlag()
    {
        return 1;
    }
   
    /**
     * sets what happens when you right click on the block.
     * it opens my Gui in this case.
     */
    public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9)
    {  	
    	TileEntity te = par1World.getBlockTileEntity(par2, par3, par4);
    	if (te == null || !(te instanceof TileEntityPressurePlate) || par5EntityPlayer.isSneaking())
    	{
    		return false;
    	}
    	if(FMLCommonHandler.instance().getSide().isServer())
    	{
    		if(PPRegistry.getUsesPassword((TileEntityPressurePlate)te, par1World.provider.worldType))
    		{
            	par5EntityPlayer.openGui(IronPP.instance, 1, par1World, par2, par3, par4);
            	return true;
    		}
    		else
    		{
            	par5EntityPlayer.openGui(IronPP.instance, 0, par1World, par2, par3, par4);
            	return true;
    		}
    	}
    	if(par1World.isRemote)
    	{
        	return true;
    	}
		if(PPRegistry.getUsesPassword((TileEntityPressurePlate)te, par1World.provider.worldType))
		{
        	par5EntityPlayer.openGui(IronPP.instance, 1, par1World, par2, par3, par4);
        	return true;
		}
		else
		{
        	par5EntityPlayer.openGui(IronPP.instance, 0, par1World, par2, par3, par4);
        	return true;
		}
    }
    
    @SideOnly(Side.CLIENT)
    public int getBlockTexture(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
    {
		World world = FMLClientHandler.instance().getClient().theWorld;
		ItemStack item = PPRegistry.getItem(par2, par3, par4, world.provider.worldType);
		if(item != null && item.itemID != IronPP.APressurePlateIron.blockID)
		{
			return this.blocksList[item.itemID].getBlockTextureFromSideAndMetadata(par5, item.getItemDamage());
		}
    	return this.getBlockTextureFromSideAndMetadata(par5, par1IBlockAccess.getBlockMetadata(par2, par3, par4));
    }
    
    @SideOnly(Side.CLIENT)
    public int colorMultiplier(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
    {
		World world = FMLClientHandler.instance().getClient().theWorld;
		ItemStack item = PPRegistry.getItem(par2, par3, par4, world.provider.worldType);
		if(item != null && item.itemID != IronPP.APressurePlateIron.blockID)
		{
			return this.blocksList[item.itemID].colorMultiplier(par1IBlockAccess, par2, par3, par4);
		}
        return super.colorMultiplier(par1IBlockAccess, par2, par3, par4);
    }
    
    

}
