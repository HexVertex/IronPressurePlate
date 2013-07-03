/**
 * This is the class of the Advanced Iron Pressure Plate.
 * Most of this is copied from the BlockPressurePlate.java so I will only explain the changes.
 *
 * @author Kalvin
 */

package xelitez.ironpp;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockFence;
import net.minecraft.block.EnumMobType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockAPressurePlate extends BlockContainer
{
    private EnumMobType triggerMobType;

    private Random random = new Random();

    protected BlockAPressurePlate(int par1, String par2, Material par3Material)
    {
        super(par1, par3Material);
        this.triggerMobType = EnumMobType.everything;
        this.setCreativeTab(CreativeTabs.tabRedstone);
        this.setTickRandomly(true);
        float var5 = 0.0625F;
        this.setBlockBounds(var5, 0.0F, var5, 1.0F - var5, 0.03125F, 1.0F - var5);
        this.func_94353_c_(1);
    }
    
    protected void func_94353_c_(int par1)
    {
        boolean flag = 15 > 0;
        float f = 0.0625F;

        if (flag)
        {
            this.setBlockBounds(f, 0.0F, f, 1.0F - f, 0.03125F, 1.0F - f);
        }
        else
        {
            this.setBlockBounds(f, 0.0F, f, 1.0F - f, 0.0625F, 1.0F - f);
        }
    }

    @Override
    public int tickRate(World par1)
    {
        return 20;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4)
    {
        return null;
    }

    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    @Override
    public boolean getBlocksMovement(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
    {
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World var1)
    {
        return new TileEntityPressurePlate();
    }

    @Override
    public boolean canPlaceBlockAt(World par1World, int par2, int par3, int par4)
    {
        return par1World.doesBlockHaveSolidTopSurface(par2, par3 - 1, par4) || BlockFence.isIdAFence(par1World.getBlockId(par2, par3 - 1, par4)) || IronPP.getNonSolidBlockEnabled(par1World.getBlockId(par2, par3 - 1, par4), par1World.getBlockMetadata(par2, par3 - 1, par4));
    }

    @Override
    public void onBlockPlacedBy(World par1World, int par2, int par3, int par4, EntityLiving par5EntityLiving, ItemStack par6ItemStack)
    {
        if (par5EntityLiving instanceof EntityPlayer && par1World.getBlockTileEntity(par2, par3, par4) != null && par1World.getBlockTileEntity(par2, par3, par4) instanceof TileEntityPressurePlate)
        {
            TileEntityPressurePlate tpp = (TileEntityPressurePlate)par1World.getBlockTileEntity(par2, par3, par4);
            tpp.registerPlayer(((EntityPlayer)par5EntityLiving).username);

            if (!par1World.isRemote || FMLCommonHandler.instance().getSide().isServer())
            {
                PacketSendManager.sendAddPressurePlateToClient(tpp, tpp.worldObj.provider.dimensionId);
                PacketSendManager.sendUsesPasswordToClient(par2, par3, par4, par1World.provider.dimensionId, tpp.getIsEnabled(2));

                if (tpp.item[0] != null)
                {
                    PacketSendManager.sendItemStackToClients(par2, par3, par4, tpp.item[0].itemID, tpp.item[0].getItemDamage(), tpp.item[0].stackSize, par1World.provider.dimensionId);
                }
                else
                {
                    PacketSendManager.sendItemStackToClients(par2, par3, par4, 0, 0, 0, par1World.provider.dimensionId);
                }
            }
        }
    }

    @Override
    public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, int par5)
    {
        boolean var6 = false;

        if (!par1World.doesBlockHaveSolidTopSurface(par2, par3 - 1, par4) && !BlockFence.isIdAFence(par1World.getBlockId(par2, par3 - 1, par4)) && !IronPP.getNonSolidBlockEnabled(par1World.getBlockId(par2, par3 - 1, par4), par1World.getBlockMetadata(par2, par3 - 1, par4)))
        {
            var6 = true;
        }

        if (var6)
        {
            this.dropBlockAsItem(par1World, par2, par3, par4, par1World.getBlockMetadata(par2, par3, par4), 0);
            par1World.setBlockToAir(par2, par3, par4);
        }
    }

    @Override
    public void updateTick(World par1World, int par2, int par3, int par4, Random par5Random)
    {
        if (!par1World.isRemote)
        {
        	if(((TileEntityPressurePlate)par1World.getBlockTileEntity(par2, par3, par4)).currentOutput != 0)
        	{
        		this.setStateIfMobInteractsWithPlate(par1World, par2, par3, par4);
        	}
        }
    }

    @Override
    public void onEntityCollidedWithBlock(World par1World, int par2, int par3, int par4, Entity par5Entity)
    {
        if (!par1World.isRemote)
        {
            float var7 = 0.125F;
        	if(((TileEntityPressurePlate)par1World.getBlockTileEntity(par2, par3, par4)).currentOutput != ((TileEntityPressurePlate)par1World.getBlockTileEntity(par2, par3, par4)).calculateOut(par1World.getEntitiesWithinAABBExcludingEntity((Entity)null, AxisAlignedBB.getAABBPool().getAABB((double)((float)par2 + var7), (double)par3, (double)((float)par4 + var7), (double)((float)(par2 + 1) - var7), (double)par3 + 0.25D, (double)((float)(par4 + 1) - var7)))) && ((TileEntityPressurePlate)par1World.getBlockTileEntity(par2, par3, par4)).calculateOut(par1World.getEntitiesWithinAABBExcludingEntity((Entity)null, AxisAlignedBB.getAABBPool().getAABB((double)((float)par2 + var7), (double)par3, (double)((float)par4 + var7), (double)((float)(par2 + 1) - var7), (double)par3 + 0.25D, (double)((float)(par4 + 1) - var7)))) != 0)
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
        boolean var6 = false;
        float var7 = 0.125F;
        List<?> var8 = null;

        if (this.triggerMobType == EnumMobType.everything)
        {
            var8 = par1World.getEntitiesWithinAABBExcludingEntity((Entity)null, AxisAlignedBB.getAABBPool().getAABB((double)((float)par2 + var7), (double)par3, (double)((float)par4 + var7), (double)((float)(par2 + 1) - var7), (double)par3 + 0.25D, (double)((float)(par4 + 1) - var7)));
        }

        if (this.triggerMobType == EnumMobType.mobs)
        {
            var8 = par1World.getEntitiesWithinAABB(EntityLiving.class, AxisAlignedBB.getAABBPool().getAABB((double)((float)par2 + var7), (double)par3, (double)((float)par4 + var7), (double)((float)(par2 + 1) - var7), (double)par3 + 0.25D, (double)((float)(par4 + 1) - var7)));
        }

        if (this.triggerMobType == EnumMobType.players)
        {
            var8 = par1World.getEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.getAABBPool().getAABB((double)((float)par2 + var7), (double)par3, (double)((float)par4 + var7), (double)((float)(par2 + 1) - var7), (double)par3 + 0.25D, (double)((float)(par4 + 1) - var7)));
        }

        if (par1World.getBlockTileEntity(par2, par3, par4) instanceof TileEntityPressurePlate)
        {
            for (int var10 = 0; var10 < var8.size(); var10++)
            {
                if (var8.size() > 0 && (((TileEntityPressurePlate)par1World.getBlockTileEntity(par2, par3, par4)).findMobName(EntityList.getEntityString((Entity)var8.get(var10)))))
                {
                    var6 = true;
                }

                if (var8.size() > 0);

                {
                    if ((var8.get(var10) instanceof EntityPlayer))
                    {
                        if (((TileEntityPressurePlate)par1World.getBlockTileEntity(par2, par3, par4)).findMobName("humanoid"))
                        {
                            if (((TileEntityPressurePlate)par1World.getBlockTileEntity(par2, par3, par4)).isPlayerInList(((EntityPlayer)var8.get(var10)).username))
                            {
                                if (((TileEntityPressurePlate)par1World.getBlockTileEntity(par2, par3, par4)).isInPlayerList(((EntityPlayer)var8.get(var10)).username))
                                {
                                    var6 = true;
                                }
                            }
                            else
                            {
                                if (((TileEntityPressurePlate)par1World.getBlockTileEntity(par2, par3, par4)).getIsEnabled(0))
                                {
                                    var6 = true;
                                }
                            }
                        }
                    }
                }
            }
        }

        if (par1World.getBlockTileEntity(par2, par3, par4) instanceof TileEntityPressurePlate)
        {
            TileEntityPressurePlate tpp = (TileEntityPressurePlate)par1World.getBlockTileEntity(par2, par3, par4);
            
            if (var6 || tpp.currentOutput > 0)
            {
                tpp.setActivated(true, par1World, par2, par3, par4, var8);
                par1World.notifyBlocksOfNeighborChange(par2, par3, par4, this.blockID);
                par1World.notifyBlocksOfNeighborChange(par2, par3 - 1, par4, this.blockID);
                par1World.markBlockRangeForRenderUpdate(par2, par3, par4, par2, par3, par4);

            }
            if(var6)
            {
            	par1World.scheduleBlockUpdate(par2, par3, par4, this.blockID, this.tickRate(par1World));
            }
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
    @Override
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
                            var14.getEntityItem().setTagCompound((NBTTagCompound)var9.getTagCompound().copy());
                        }
                    }
                }
            }

            PPRegistry.removePressurePlate(var7, var7.worldObj.provider.dimensionId);

            if (!par1World.isRemote || FMLCommonHandler.instance().getSide().isServer())
            {
                PacketSendManager.sendRemovePressurePlateToClient(var7, var7.worldObj.provider.dimensionId);
            }
        }

        super.breakBlock(par1World, par2, par3, par4, par5, par6);
        par1World.removeBlockTileEntity(par2, par3, par4);
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
    {
        boolean var5 = ((TileEntityPressurePlate)par1IBlockAccess.getBlockTileEntity(par2, par3, par4)).currentOutput > 0;
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

    @Override
    public int isProvidingWeakPower(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
    {
    	return ((TileEntityPressurePlate)par1IBlockAccess.getBlockTileEntity(par2, par3, par4)).currentOutput;
    }

    @Override
    public int isProvidingStrongPower(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
    {
        return par5 == 1 ? ((TileEntityPressurePlate)par1IBlockAccess.getBlockTileEntity(par2, par3, par4)).currentOutput : 0;
    }
    
    @Override
    public boolean canProvidePower()
    {
        return true;
    }

    @Override
    public void setBlockBoundsForItemRender()
    {
        float var1 = 0.5F;
        float var2 = 0.125F;
        float var3 = 0.5F;
        this.setBlockBounds(0.5F - var1, 0.5F - var2, 0.5F - var3, 0.5F + var1, 0.5F + var2, 0.5F + var3);
    }

    @Override
    public int getMobilityFlag()
    {
        return 1;
    }

    /**
     * sets what happens when you right click on the block.
     * it opens my Gui in this case.
     */
    @Override
    public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9)
    {
        TileEntity te = par1World.getBlockTileEntity(par2, par3, par4);

        if (te == null || !(te instanceof TileEntityPressurePlate) || par5EntityPlayer.isSneaking())
        {
            return false;
        }

        if (FMLCommonHandler.instance().getSide().isServer())
        {
            if (PPRegistry.getUsesPassword((TileEntityPressurePlate)te, par1World.provider.dimensionId))
            {
                par5EntityPlayer.openGui(IronPP.instance, 1, par1World, par2, par3, par4);
                PacketSendManager.sendPPIntToClient(1, par5EntityPlayer);
                return true;
            }
            else
            {
                par5EntityPlayer.openGui(IronPP.instance, 0, par1World, par2, par3, par4);
                return true;
            }
        }

        if (par1World.isRemote)
        {
            return true;
        }

        if (PPRegistry.getUsesPassword((TileEntityPressurePlate)te, par1World.provider.dimensionId))
        {
            par5EntityPlayer.openGui(IronPP.instance, 1, par1World, par2, par3, par4);
            PacketSendManager.sendPPIntToClient(1, par5EntityPlayer);
            return true;
        }
        else
        {
            par5EntityPlayer.openGui(IronPP.instance, 0, par1World, par2, par3, par4);
            return true;
        }
    }
    
    @SideOnly(Side.CLIENT)
    public Icon getBlockTexture(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
    {
    	World world = FMLClientHandler.instance().getClient().theWorld;
    	ItemStack item = PPRegistry.getItem(par2, par3, par4, world.provider.dimensionId);
    	
    	if (item != null && item.itemID != IronPP.APressurePlateIron.blockID)
    	{
    		return Block.blocksList[item.itemID].getIcon(par5, item.getItemDamage());
    	}
    	
    	return this.getIcon(par5, par1IBlockAccess.getBlockMetadata(par2, par3, par4));
    }
    
    @SideOnly(Side.CLIENT)
    public int colorMultiplier(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
    {
    	World world = FMLClientHandler.instance().getClient().theWorld;
    	ItemStack item = PPRegistry.getItem(par2, par3, par4, world.provider.dimensionId);
    
    	if (item != null && item.itemID != IronPP.APressurePlateIron.blockID)
    	{
    		return Block.blocksList[item.itemID].colorMultiplier(par1IBlockAccess, par2, par3, par4);
    	}
    
    	return super.colorMultiplier(par1IBlockAccess, par2, par3, par4);
    }


    @Override
    public boolean removeBlockByPlayer(World world, EntityPlayer player, int x, int y, int z)
    {
        TileEntity te = world.getBlockTileEntity(x, y, z);

        if (te != null && te instanceof TileEntityPressurePlate)
        {
            TileEntityPressurePlate tpp = (TileEntityPressurePlate)te;

            if (FMLCommonHandler.instance().getEffectiveSide().isServer())
            {
                if (tpp.getIsEnabled(3) && !player.capabilities.isCreativeMode)
                {
                    player.openGui(IronPP.instance, 1, world, x, y, z);
                    PacketSendManager.sendPPIntToClient(2, player);
                }
                else
                {
                    return world.setBlockToAir(x, y, z);
                }
            }
            else
            {
                return false;
            }
        }

        return true;
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
