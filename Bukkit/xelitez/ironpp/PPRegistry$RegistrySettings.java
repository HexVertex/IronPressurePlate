package xelitez.ironpp;

import net.minecraft.server.ItemStack;

class PPRegistry$RegistrySettings
{
    public int xCoord;
    public int yCoord;
    public int zCoord;
    public ItemStack item;
    public int dimension;
    public boolean usesPassword;

    public PPRegistry$RegistrySettings(int var1, int var2, int var3, ItemStack var4, int var5, boolean var6)
    {
        this.xCoord = var1;
        this.yCoord = var2;
        this.zCoord = var3;
        this.item = var4;
        this.dimension = var5;
        this.usesPassword = var6;
    }
}
