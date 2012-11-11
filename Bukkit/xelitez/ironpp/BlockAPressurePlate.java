package xelitez.ironpp;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import java.util.List;
import java.util.Random;
import net.minecraft.server.AxisAlignedBB;
import net.minecraft.server.BlockContainer;
import net.minecraft.server.BlockFence;
import net.minecraft.server.CreativeModeTab;
import net.minecraft.server.Entity;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityItem;
import net.minecraft.server.EntityLiving;
import net.minecraft.server.EnumMobType;
import net.minecraft.server.IBlockAccess;
import net.minecraft.server.ItemStack;
import net.minecraft.server.Material;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.PPManager;
import net.minecraft.server.TileEntity;
import net.minecraft.server.World;

public class BlockAPressurePlate extends BlockContainer
{
    private EnumMobType triggerMobType;
    private Random random = new Random();

    protected BlockAPressurePlate(int var1, int var2, Material var3)
    {
        super(var1, var2, var3);
        this.r();
        this.triggerMobType = EnumMobType.EVERYTHING;
        this.a(CreativeModeTab.d);
        this.b(true);
        float var4 = 0.0625F;
        this.a(var4, 0.0F, var4, 1.0F - var4, 0.03125F, 1.0F - var4);
        this.textureId = var2;
    }

    /**
     * How many world ticks before ticking
     */
    public int r_()
    {
        return 20;
    }

    /**
     * Returns a bounding box from the pool of bounding boxes (this means this box can change after the pool has been
     * cleared to be reused)
     */
    public AxisAlignedBB e(World var1, int var2, int var3, int var4)
    {
        return null;
    }

    /**
     * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
     * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
     */
    public boolean c()
    {
        return false;
    }

    /**
     * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
     */
    public boolean b()
    {
        return false;
    }

    public boolean c(IBlockAccess var1, int var2, int var3, int var4)
    {
        return true;
    }

    /**
     * Returns a new instance of a block's tile entity class. Called on placing the block.
     */
    public TileEntity a(World var1)
    {
        return new TileEntityPressurePlate();
    }

    /**
     * Checks to see if its valid to put this block at the specified coordinates. Args: world, x, y, z
     */
    public boolean canPlace(World var1, int var2, int var3, int var4)
    {
        return var1.t(var2, var3 - 1, var4) || BlockFence.c(var1.getTypeId(var2, var3 - 1, var4));
    }

    /**
     * Called when the block is placed in the world.
     */
    public void postPlace(World var1, int var2, int var3, int var4, EntityLiving var5)
    {
        if (var5 instanceof EntityHuman && var1.getTileEntity(var2, var3, var4) != null && var1.getTileEntity(var2, var3, var4) instanceof TileEntityPressurePlate)
        {
            TileEntityPressurePlate var6 = (TileEntityPressurePlate)var1.getTileEntity(var2, var3, var4);
            var6.registerPlayer(((EntityHuman)var5).name);

            if (!var1.isStatic || FMLCommonHandler.instance().getSide().isServer())
            {
                PacketSendManager.sendAddPressurePlateToClient(var6, var6.world.worldProvider.dimension);
                PacketSendManager.sendUsesPasswordToClient(var2, var3, var4, var1.worldProvider.dimension, Boolean.valueOf(var6.getIsEnabled(2)));

                if (var6.item[0] != null)
                {
                    PacketSendManager.sendItemStackToClients(var2, var3, var4, var6.item[0].id, var6.item[0].getData(), var6.item[0].count, var1.worldProvider.dimension);
                }
                else
                {
                    PacketSendManager.sendItemStackToClients(var2, var3, var4, 0, 0, 0, var1.worldProvider.dimension);
                }
            }
        }
    }

    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor blockID
     */
    public void doPhysics(World var1, int var2, int var3, int var4, int var5)
    {
        boolean var6 = false;

        if (!var1.t(var2, var3 - 1, var4) && !BlockFence.c(var1.getTypeId(var2, var3 - 1, var4)))
        {
            var6 = true;
        }

        if (var6)
        {
            this.c(var1, var2, var3, var4, var1.getData(var2, var3, var4), 0);
            var1.setTypeId(var2, var3, var4, 0);
        }
    }

    /**
     * Ticks the block if it's been scheduled
     */
    public void b(World var1, int var2, int var3, int var4, Random var5)
    {
        if (!var1.isStatic && ((TileEntityPressurePlate)var1.getTileEntity(var2, var3, var4)).activated)
        {
            this.setStateIfMobInteractsWithPlate(var1, var2, var3, var4);
        }
    }

    /**
     * Triggered whenever an entity collides with this block (enters into the block). Args: world, x, y, z, entity
     */
    public void a(World var1, int var2, int var3, int var4, Entity var5)
    {
        if (!var1.isStatic && !((TileEntityPressurePlate)var1.getTileEntity(var2, var3, var4)).activated)
        {
            this.setStateIfMobInteractsWithPlate(var1, var2, var3, var4);
        }
    }

    private void setStateIfMobInteractsWithPlate(World var1, int var2, int var3, int var4)
    {
        boolean var5 = ((TileEntityPressurePlate)var1.getTileEntity(var2, var3, var4)).activated;
        boolean var6 = false;
        float var7 = 0.125F;
        List var8 = null;

        if (this.triggerMobType == EnumMobType.EVERYTHING)
        {
            var8 = var1.getEntities((Entity)null, AxisAlignedBB.a().a((double)((float)var2 + var7), (double)var3, (double)((float)var4 + var7), (double)((float)(var2 + 1) - var7), (double)var3 + 0.25D, (double)((float)(var4 + 1) - var7)));
        }

        if (this.triggerMobType == EnumMobType.MOBS)
        {
            var8 = var1.a(EntityLiving.class, AxisAlignedBB.a().a((double)((float)var2 + var7), (double)var3, (double)((float)var4 + var7), (double)((float)(var2 + 1) - var7), (double)var3 + 0.25D, (double)((float)(var4 + 1) - var7)));
        }

        if (this.triggerMobType == EnumMobType.PLAYERS)
        {
            var8 = var1.a(EntityHuman.class, AxisAlignedBB.a().a((double)((float)var2 + var7), (double)var3, (double)((float)var4 + var7), (double)((float)(var2 + 1) - var7), (double)var3 + 0.25D, (double)((float)(var4 + 1) - var7)));
        }

        if (var1.getTileEntity(var2, var3, var4) instanceof TileEntityPressurePlate)
        {
            for (int var9 = 0; var9 < var8.size(); ++var9)
            {
                if (var8.size() > 0 && ((TileEntityPressurePlate)var1.getTileEntity(var2, var3, var4)).findMobName(PPManager.getEntityString((Entity)var8.get(var9))))
                {
                    var6 = true;
                }

                if (var8.size() > 0)
                {
                    ;
                }

                if (var8.get(var9) instanceof EntityHuman && ((TileEntityPressurePlate)var1.getTileEntity(var2, var3, var4)).findMobName(PPManager.getEntityType((EntityLiving)var8.get(var9))))
                {
                    if (((TileEntityPressurePlate)var1.getTileEntity(var2, var3, var4)).isPlayerInList(((EntityHuman)var8.get(var9)).name))
                    {
                        if (((TileEntityPressurePlate)var1.getTileEntity(var2, var3, var4)).isInPlayerList(((EntityHuman)var8.get(var9)).name))
                        {
                            var6 = true;
                        }
                    }
                    else if (((TileEntityPressurePlate)var1.getTileEntity(var2, var3, var4)).getIsEnabled(0))
                    {
                        var6 = true;
                    }
                }
            }
        }

        if (var1.getTileEntity(var2, var3, var4) instanceof TileEntityPressurePlate)
        {
            TileEntityPressurePlate var10 = (TileEntityPressurePlate)var1.getTileEntity(var2, var3, var4);

            if (var6 && !var5)
            {
                var10.setActivated(true, var1, var2, var3, var4);
                var1.applyPhysics(var2, var3, var4, this.id);
                var1.applyPhysics(var2, var3 - 1, var4, this.id);
                var1.e(var2, var3, var4, var2, var3, var4);

                if (((TileEntityPressurePlate)var1.getTileEntity(var2, var3, var4)).getIsEnabled(1))
                {
                    var1.makeSound((double)var2 + 0.5D, (double)var3 + 0.1D, (double)var4 + 0.5D, "random.click", 0.3F, 0.6F);
                }
            }

            if (!var6 && var5)
            {
                var10.setActivated(false, var1, var2, var3, var4);
                var1.applyPhysics(var2, var3, var4, this.id);
                var1.applyPhysics(var2, var3 - 1, var4, this.id);
                var1.e(var2, var3, var4, var2, var3, var4);

                if (((TileEntityPressurePlate)var1.getTileEntity(var2, var3, var4)).getIsEnabled(1))
                {
                    var1.makeSound((double)var2 + 0.5D, (double)var3 + 0.1D, (double)var4 + 0.5D, "random.click", 0.3F, 0.5F);
                }
            }
        }

        if (var6)
        {
            var1.a(var2, var3, var4, this.id, this.r_());
        }
    }

    /**
     * ejects contained items into the world, and notifies neighbours of an update, as appropriate
     */
    public void remove(World var1, int var2, int var3, int var4, int var5, int var6)
    {
        if (var6 > 0)
        {
            var1.applyPhysics(var2, var3, var4, this.id);
            var1.applyPhysics(var2, var3 - 1, var4, this.id);
        }

        PacketSendManager.sendCloseGuiPacketToAllPlayers((TileEntityPressurePlate)var1.getTileEntity(var2, var3, var4));
        TileEntityPressurePlate var7 = (TileEntityPressurePlate)var1.getTileEntity(var2, var3, var4);

        if (var7 != null)
        {
            for (int var8 = 0; var8 < var7.getSize(); ++var8)
            {
                ItemStack var9 = var7.getItem(var8);

                if (var9 != null)
                {
                    float var10 = this.random.nextFloat() * 0.8F + 0.1F;
                    float var11 = this.random.nextFloat() * 0.8F;
                    EntityItem var12;

                    for (float var13 = this.random.nextFloat() * 0.8F + 0.1F; var9.count > 0; var1.addEntity(var12))
                    {
                        int var14 = this.random.nextInt(21) + 10;

                        if (var14 > var9.count)
                        {
                            var14 = var9.count;
                        }

                        var9.count -= var14;
                        var12 = new EntityItem(var1, (double)((float)var2 + var10), (double)((float)var3 + var11), (double)((float)var4 + var13), new ItemStack(var9.id, var14, var9.getData()));
                        float var15 = 0.05F;
                        var12.motX = (double)((float)this.random.nextGaussian() * var15);
                        var12.motY = (double)((float)this.random.nextGaussian() * var15 + 0.1F);
                        var12.motZ = (double)((float)this.random.nextGaussian() * var15);

                        if (var9.hasTag())
                        {
                            var12.itemStack.setTag((NBTTagCompound)var9.getTag().clone());
                        }
                    }
                }
            }

            PPRegistry.removePressurePlate(var7, var7.world.worldProvider.dimension);

            if (!var1.isStatic || FMLCommonHandler.instance().getSide().isServer())
            {
                PacketSendManager.sendRemovePressurePlateToClient(var7, var7.world.worldProvider.dimension);
            }
        }

        super.remove(var1, var2, var3, var4, var5, var6);
        var1.q(var2, var3, var4);
    }

    /**
     * Updates the blocks bounds based on its current state. Args: world, x, y, z
     */
    public void updateShape(IBlockAccess var1, int var2, int var3, int var4)
    {
        boolean var5 = ((TileEntityPressurePlate)var1.getTileEntity(var2, var3, var4)).activated;
        float var6 = 0.0625F;

        if (var5)
        {
            this.a(var6, 0.0F, var6, 1.0F - var6, 0.03125F, 1.0F - var6);
        }
        else
        {
            this.a(var6, 0.0F, var6, 1.0F - var6, 0.0625F, 1.0F - var6);
        }
    }

    /**
     * Is this block powering the block on the specified side
     */
    public boolean b(IBlockAccess var1, int var2, int var3, int var4, int var5)
    {
        return ((TileEntityPressurePlate)var1.getTileEntity(var2, var3, var4)).activated;
    }

    /**
     * Is this block indirectly powering the block on the specified side
     */
    public boolean c(IBlockAccess var1, int var2, int var3, int var4, int var5)
    {
        return !((TileEntityPressurePlate)var1.getTileEntity(var2, var3, var4)).activated ? false : var5 == 1;
    }

    /**
     * Can this block provide power. Only wire currently seems to have this change based on its state.
     */
    public boolean isPowerSource()
    {
        return true;
    }

    /**
     * Sets the block's bounds for rendering it as an item
     */
    public void f()
    {
        float var1 = 0.5F;
        float var2 = 0.125F;
        float var3 = 0.5F;
        this.a(0.5F - var1, 0.5F - var2, 0.5F - var3, 0.5F + var1, 0.5F + var2, 0.5F + var3);
    }

    /**
     * Returns the mobility information of the block, 0 = free, 1 = can't push but can move over, 2 = total immobility
     * and stop pistons
     */
    public int q_()
    {
        return 1;
    }

    /**
     * Called upon block activation (right click on the block.)
     */
    public boolean interact(World var1, int var2, int var3, int var4, EntityHuman var5, int var6, float var7, float var8, float var9)
    {
        TileEntity var10 = var1.getTileEntity(var2, var3, var4);

        if (var10 != null && var10 instanceof TileEntityPressurePlate && !var5.isSneaking())
        {
            if (FMLCommonHandler.instance().getSide().isServer())
            {
                if (PPRegistry.getUsesPassword((TileEntityPressurePlate)var10, var1.worldProvider.dimension))
                {
                    var5.openGui(IronPP.instance, 1, var1, var2, var3, var4);
                    PacketSendManager.sendPPIntToClient(1, var5);
                    return true;
                }
                else
                {
                    var5.openGui(IronPP.instance, 0, var1, var2, var3, var4);
                    return true;
                }
            }
            else if (var1.isStatic)
            {
                return true;
            }
            else if (PPRegistry.getUsesPassword((TileEntityPressurePlate)var10, var1.worldProvider.dimension))
            {
                var5.openGui(IronPP.instance, 1, var1, var2, var3, var4);
                PacketSendManager.sendPPIntToClient(1, var5);
                return true;
            }
            else
            {
                var5.openGui(IronPP.instance, 0, var1, var2, var3, var4);
                return true;
            }
        }
        else
        {
            return false;
        }
    }

    public boolean removeBlockByPlayer(World var1, EntityHuman var2, int var3, int var4, int var5)
    {
        TileEntity var6 = var1.getTileEntity(var3, var4, var5);

        if (var6 != null && var6 instanceof TileEntityPressurePlate)
        {
            TileEntityPressurePlate var7 = (TileEntityPressurePlate)var6;

            if (!FMLCommonHandler.instance().getEffectiveSide().isServer())
            {
                return false;
            }

            if (!var7.getIsEnabled(3) || var2.abilities.canInstantlyBuild)
            {
                return var1.setTypeId(var3, var4, var5, 0);
            }

            var2.openGui(IronPP.instance, 1, var1, var3, var4, var5);
            PacketSendManager.sendPPIntToClient(2, var2);
        }

        return true;
    }
}
